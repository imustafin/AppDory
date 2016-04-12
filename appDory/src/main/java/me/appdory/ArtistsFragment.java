package me.appdory;

import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import me.appdory.listadapters.ArtistCountPairAdapter;
import me.appdory.model.ArtistCountPair;
import me.appdory.model.DbContract.ArtistTable;
import me.appdory.model.DbHelper;
import me.appdory.sync.SyncAdapter;

public class ArtistsFragment extends Fragment {

    List<ArtistCountPair> mPairs;
    ArtistCountPairAdapter mAdapter;
    ListView mListView;
    ProgressBar mLoadingSpinner;

    public ArtistsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_artists, container,
                false);

        mListView = (ListView) view.findViewById(R.id.artists_list);
        mLoadingSpinner = (ProgressBar) view.findViewById(R.id.loading_spinner);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mListView.setVisibility(View.GONE);
        mLoadingSpinner.setVisibility(View.VISIBLE);

        new AsyncTask<Context, Void, List<ArtistCountPair>>() {

            @Override
            protected List<ArtistCountPair> doInBackground(Context... params) {
                SyncAdapter.lock.lock();
                DbHelper dbHelper = new DbHelper(params[0]);

                SQLiteDatabase db = dbHelper.getReadableDatabase();

                String[] projection = {ArtistTable.COLUMN_NAME_ARTIST_NAME,
                        ArtistTable.COLUMN_NAME_ARTIST_COUNT};

                String sortOrder = ArtistTable.COLUMN_NAME_ARTIST_COUNT + " DESC, "
                        + ArtistTable.COLUMN_NAME_ARTIST_NAME + " DESC";

                Cursor cursor = db.query(ArtistTable.TABLE_NAME, projection,
                        null, null, null, null, sortOrder);

                cursor.moveToFirst();

                ArrayList<ArtistCountPair> pairs = new ArrayList<ArtistCountPair>();

                while (!cursor.isAfterLast()) {
                    String artist = cursor
                            .getString(cursor
                                    .getColumnIndex(ArtistTable.COLUMN_NAME_ARTIST_NAME));
                    int count = cursor
                            .getInt(cursor
                                    .getColumnIndex(ArtistTable.COLUMN_NAME_ARTIST_COUNT));
                    pairs.add(new ArtistCountPair(artist, String.valueOf(count)));
                    cursor.moveToNext();
                }

                db.close();

                SyncAdapter.lock.unlock();

                return pairs;

            }

            protected void onPostExecute(List<ArtistCountPair> result) {
                postExecute(result);
            }

        }.execute(view.getContext());

    }

    void postExecute(List<ArtistCountPair> list) {
        if (getView() == null) {
            return;
        }
        mListView.setVisibility(View.VISIBLE);
        mLoadingSpinner.setVisibility(View.GONE);

        mPairs = list;

        mAdapter = new ArtistCountPairAdapter(getActivity(),
                R.id.concert_list_view, mPairs);

        mListView.setAdapter(mAdapter);
    }

}
