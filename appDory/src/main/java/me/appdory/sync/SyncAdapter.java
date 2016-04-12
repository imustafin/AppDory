package me.appdory.sync;

import android.accounts.Account;
import android.app.NotificationManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.perm.kate.api.Audio;
import com.perm.kate.api.KException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import me.appdory.Accounts;
import me.appdory.Accounts.VkAccount;
import me.appdory.DoryAPI;
import me.appdory.R;
import me.appdory.model.ArtistCountPair;
import me.appdory.model.Concert;
import me.appdory.model.DbContract.ArtistTable;
import me.appdory.model.DbContract.ConcertTable;
import me.appdory.model.DbContract.MyRegionTable;
import me.appdory.model.DbHelper;
import me.appdory.Notifications;

public class SyncAdapter extends AbstractThreadedSyncAdapter {

    public static final String KEY_LOGGED_IN = "logged_in";

    public static Lock lock = new ReentrantLock();

    Context mContext;

    int mCount = 0;

    static final String PICS_DIR = "concert_pics";

    static boolean syncing = false;

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContext = context;
    }

    public SyncAdapter(Context context, boolean autoInitialize,
                       boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {

        lock.lock();

        if (syncing) {
            return;
        }
        syncing = true;
        try {
            syncVk();
        } catch (IOException | JSONException | KException e) {
            e.printStackTrace();
            syncResultBad(e.toString());
        }
    }

    synchronized void syncVk() throws IOException, JSONException, KException {
        System.err.println("Make sync!");

        notificationProgress(0, 0, true,
                mContext.getString(R.string.started_syncing));

        Accounts.VkAccount.readFromSharedPreferences(mContext);

        if (!Accounts.VkAccount.gotInstance()) {
            syncResultBad(mContext.getString(R.string.no_instance_vkaccount));
            return;
        }

        VkAccount vkAccount = Accounts.VkAccount.getInstance();

        vkAccount.loadNameSurnamePhoto();
        notificationProgress(0, 0, true,
                mContext.getString(R.string.vk_done_get_dory_id));

        String doryId = DoryAPI.getUid(String.valueOf(vkAccount.userId),
                DoryAPI.SOCIAL_VK, vkAccount.name, vkAccount.surname);

        notificationProgress(0, 0, true,
                mContext.getString(R.string.doryid_done_vk_audio_get));

        ArrayList<Audio> songs = vkAccount.api.getAudio(null, null, null, null,
                null, null);

        ArrayList<ArtistCountPair> musicInfo = fetchMusic(songs);
        notificationProgress(musicInfo.size(), 0, false,
                mContext.getString(R.string.fetched_music_saving));

        saveMusicInfoToDb(musicInfo);
        sendMusicToServer(musicInfo, doryId);

        getConcertsAndSaveToDb(doryId);

        syncResultOk();

        showNotificationsForEachConcert();
    }

    ArrayList<ArtistCountPair> fetchMusic(ArrayList<Audio> songs) {
        Map<String, Integer> map = new HashMap<String, Integer>();
        for (Audio song : songs) {
            String artist = song.artist;
            Integer count = map.get(artist);
            if (count == null) {
                count = 0;
            }
            count++;
            map.put(artist, count);
        }
        ArrayList<ArtistCountPair> list = new ArrayList<ArtistCountPair>();
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            list.add(new ArtistCountPair(entry.getKey(), entry.getValue()
                    .toString()));
        }
        return list;
    }

    void saveMusicInfoToDb(ArrayList<ArtistCountPair> list) {
        DbHelper dbHelper = new DbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(ArtistTable.TABLE_NAME, null, null);
        int progress = 0;
        for (ArtistCountPair pair : list) {
            ContentValues values = new ContentValues();
            values.put(ArtistTable.COLUMN_NAME_ARTIST_NAME, pair.artist);
            values.put(ArtistTable.COLUMN_NAME_ARTIST_COUNT, pair.count);
            db.insert(ArtistTable.TABLE_NAME, null, values);
            progress++;
            notificationProgress(list.size(), progress, false,
                    mContext.getString(R.string.saved_to_local_db));
        }
        db.close();
    }

    void getConcertsAndSaveToDb(String doryId) throws IOException,
            JSONException {

        notificationProgress(0, 0, true, "Reading my regions from db");
        DbHelper dbHelper = new DbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Set<String> regions = new HashSet<String>();

        Cursor regionCursor = db.query(MyRegionTable.TABLE_NAME,
                new String[]{MyRegionTable.COLUMN_NAME_REGION}, null, null,
                null, null, null);
        while (regionCursor.moveToNext()) {
            regions.add(regionCursor.getString(regionCursor
                    .getColumnIndex(MyRegionTable.COLUMN_NAME_REGION)));
        }
        regionCursor.close();

        notificationProgress(0, 0, true,
                mContext.getString(R.string.cleaning_concert_db));
        String[] projection = {ConcertTable.COLUMN_NAME_ID};
        Cursor cursor = db.query(ConcertTable.TABLE_NAME, projection,
                ConcertTable.COLUMN_NAME_VIEWED + "=1", null, null, null, null);
        cursor.moveToFirst();
        ArrayList<String> viewedIds = new ArrayList<String>();
        for (int i = 0; i < cursor.getCount(); i++, cursor.moveToNext()) {
            viewedIds.add(cursor.getString(0));
        }
        cursor.close();
        db.delete(ConcertTable.TABLE_NAME, null, null);

        notificationProgress(0, 0, true,
                mContext.getString(R.string.cleaning_pics_dir));
        cleanPicsDir();

        List<String> ids = null;
        notificationProgress(0, 0, true,
                mContext.getString(R.string.getting_my_concert_ids));
        ids = DoryAPI.getConcertIDsOf(doryId);
        notificationProgress(0, 0, true,
                mContext.getString(R.string.got_concert_ids));
        for (int i = 0; i < ids.size(); i++) {
            String id = ids.get(i);
            notificationProgress(ids.size(), i, false,
                    mContext.getString(R.string.getting_concert_info));
            JSONObject json = DoryAPI.getConcertJson(id);
            ContentValues values = new ContentValues();
            Concert concert = new Concert(json);
            if (!regions.contains(concert.region)) {
                notificationProgress(ids.size(), i, false, "Skipping concert not from our region");
                continue;
            }
            notificationProgress(0, 0, true,
                    mContext.getString(R.string.loading_pic_for) + i + "/"
                            + ids.size());
            String filename = downloadPictureToFile(i, concert.image);
            notificationProgress(ids.size(), i, false,
                    mContext.getString(R.string.saving_concert_to_local_db));
            values.put(ConcertTable.COLUMN_NAME_BITMAP_PATH, filename);
            concert.putColumnsToContentValues(values);
            if (viewedIds.contains(String.valueOf(concert.id))) {
                values.put(ConcertTable.COLUMN_NAME_VIEWED, true);
            } else {
                values.put(ConcertTable.COLUMN_NAME_VIEWED, false);
            }
            db.insert(ConcertTable.TABLE_NAME, null, values);
        }
        db.close();
    }

    private void sendMusicToServer(ArrayList<ArtistCountPair> musicInfo,
                                   String doryId) throws IOException, JSONException {

        int progress = 0;

        for (ArtistCountPair pair : musicInfo) {
            String artist = pair.artist;
            String count = pair.count;
            String from = DoryAPI.FROM_VK;
            DoryAPI.postArtistInfo(doryId, artist, count, from);
            progress++;
            notificationProgress(musicInfo.size(), progress, false,
                    mContext.getString(R.string.sending_to_server_));
        }
    }

    void notificationProgress(int max, int cur, boolean indeterminate,
                              String text) {
        NotificationCompat.Builder builder = Notifications
                .getDoryBuilder(mContext);

        builder.setProgress(max, cur, indeterminate);
        builder.setContentText(text);

        NotificationManager notificationManager = (NotificationManager) mContext
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(Notifications.ID_SYNC_MESSAGE,
                builder.build());

    }

    void syncResultBad(String message) {
        Notifications.showDoryNotificationWithLongText(mContext,
                mContext.getString(R.string.error_syncing_) + message,
                Notifications.ID_SYNC_MESSAGE);

        syncing = false;
        lock.unlock();
    }

    void syncResultOk() {
        Notifications.showDoryNotificationWithText(mContext,
                mContext.getString(R.string.syncing_ok),
                Notifications.ID_SYNC_MESSAGE);
        syncing = false;
        lock.unlock();
    }

    void showNotificationsForEachConcert() {

        DbHelper dbHelper = new DbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.query(ConcertTable.TABLE_NAME, null,
                ConcertTable.COLUMN_NAME_VIEWED + "=0", null, null, null, null);

        cursor.moveToFirst();

        ContentValues viewedContentValues = new ContentValues();
        viewedContentValues.put(ConcertTable.COLUMN_NAME_VIEWED, true);

        while (!cursor.isAfterLast()) {
            Concert concert = new Concert(cursor);

            Notifications.showConcertNotification(mContext, concert);

            db.update(ConcertTable.TABLE_NAME, viewedContentValues,
                    ConcertTable.COLUMN_NAME_ID + "=" + concert.id, null);

            cursor.moveToNext();
        }

        db.close();
    }

    void cleanPicsDir() {
        File picsDir = new File(mContext.getFilesDir(), PICS_DIR);
        File[] files = picsDir.listFiles();
        if (files == null) {
            return;
        }
        for (File f : files) {
            f.delete();
        }
    }

    String downloadPictureToFile(int number, String fileUrl) {
        try {
            URL url = new URL(fileUrl);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return saveBitmapToFile(number, BitmapFactory.decodeStream(input));
        } catch (IOException e) {
            System.err.println(fileUrl);
            e.printStackTrace();
        }
        return null;
    }

    String saveBitmapToFile(int number, Bitmap bitmap) {
        File picsDir = new File(mContext.getFilesDir(), PICS_DIR);
        picsDir.mkdir();
        File pic = new File(picsDir, number + ".png");
        FileOutputStream out = null;
        boolean result = false;
        Bitmap scaled;

        int desiredHeight = mContext.getResources().getDimensionPixelSize(
                R.dimen.concert_list_item_height);

        if (bitmap.getHeight() <= desiredHeight) {
            scaled = bitmap;
        } else {
            scaled = Bitmap.createScaledBitmap(bitmap,
                    (int) ((float) desiredHeight / bitmap.getHeight() * bitmap
                            .getWidth()), desiredHeight, false);
        }

        try {
            out = new FileOutputStream(pic);
            result = scaled.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String ret = result ? pic.getAbsolutePath() : null;
        return ret;
    }

    public void resetViewedNotifications() {
        DbHelper helper = new DbHelper(mContext);
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ConcertTable.COLUMN_NAME_VIEWED, false);
        String where = ConcertTable.COLUMN_NAME_VIEWED + "=1";
        db.update(ConcertTable.TABLE_NAME, values, where, null);
    }
}
