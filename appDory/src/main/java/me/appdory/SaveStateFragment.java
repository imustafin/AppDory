package me.appdory;

import android.app.Fragment;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import me.appdory.model.DbContract.ConcertTable;
import me.appdory.model.DbHelper;

public class SaveStateFragment extends Fragment {

    Button mLoadButton;
    Button mSaveButton;
    TextView mTokenText;

    Button mSyncButton;

    Button mNotificationButton;

    Button mResetViewedNotificationsButton;

    public SaveStateFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_save_state, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mLoadButton = (Button) view.findViewById(R.id.load_button);
        mSaveButton = (Button) view.findViewById(R.id.save_button);
        mTokenText = (TextView) view.findViewById(R.id.token_text);
        mSyncButton = (Button) view.findViewById(R.id.sync_button);
        mNotificationButton = (Button) view
                .findViewById(R.id.notification_button);
        mResetViewedNotificationsButton = (Button) view
                .findViewById(R.id.reset_notifications_viewed);

        mLoadButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "no action", Toast.LENGTH_SHORT)
                        .show();
            }
        });

        mSaveButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "no action", Toast.LENGTH_SHORT)
                        .show();
            }
        });

        mSyncButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Requesting sync now", Toast.LENGTH_SHORT).show();
                ContentResolver.requestSync(MainActivity.account,
                        MainActivity.AUTHORITY, Bundle.EMPTY);
            }
        });

        mNotificationButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Notifications.showDoryNotificationWithText(getActivity(),
                        "Keks", Notifications.ID_DEBUG_MESSAGE);
            }
        });

        mResetViewedNotificationsButton
                .setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        new AsyncTask<Void, Void, String>() {

                            @Override
                            protected String doInBackground(Void... params) {
                                DbHelper helper = new DbHelper(getActivity());
                                SQLiteDatabase db = helper
                                        .getWritableDatabase();
                                ContentValues values = new ContentValues();
                                values.put(ConcertTable.COLUMN_NAME_VIEWED,
                                        false);
                                String where = ConcertTable.COLUMN_NAME_VIEWED
                                        + "=1";
                                int affected = db.update(
                                        ConcertTable.TABLE_NAME, values, where,
                                        null);
                                return String.valueOf(affected);
                            }

                            @Override
                            protected void onPostExecute(String result) {
                                Toast.makeText(
                                        getActivity(),
                                        "Reset notifications on " + result
                                                + " rows", Toast.LENGTH_SHORT)
                                        .show();
                            }

                        }.execute();
                    }
                });

    }
}
