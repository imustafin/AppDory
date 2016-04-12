package me.appdory;

import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import me.appdory.listadapters.ConcertListAdapter;
import me.appdory.model.Concert;
import me.appdory.model.DbContract.ConcertTable;
import me.appdory.model.DbHelper;
import me.appdory.sync.SyncAdapter;

public class ConcertListFragment extends Fragment {

    List<Concert> mConcerts;
    ConcertListAdapter mAdapter;
    ListView mListView;
    ProgressBar mLoadingSpinner;
    LinearLayout mConcertListLayout;

    Menu mActionBarButtons;

    Context mContext;

    public ConcertListFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        setHasOptionsMenu(true);

        mContext = getActivity().getApplicationContext();

        View view = inflater.inflate(R.layout.fragment_concert_list, container,
                false);

        mLoadingSpinner = (ProgressBar) view.findViewById(R.id.loading_spinner);
        mListView = (ListView) view.findViewById(R.id.concert_list_view);
        mConcertListLayout = (LinearLayout) view
                .findViewById(R.id.concert_list_layout);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                updateConcertList();
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                postExecute();
            }

        }.execute();
    }

    void updateConcertList() {
        SyncAdapter.lock.lock();
        DbHelper dbHelper = new DbHelper(mContext);

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String sortOrder = ConcertTable.COLUMN_NAME_DATE + " DESC";

        Cursor cursor = db.query(ConcertTable.TABLE_NAME, null, null, null,
                null, null, sortOrder);

        cursor.moveToFirst();

        ArrayList<Concert> concerts = new ArrayList<Concert>();

        while (!cursor.isAfterLast()) {
            concerts.add(new Concert(cursor));
            cursor.moveToNext();
        }

        db.close();
        if (mConcerts != null) {
            mConcerts.clear();
            mConcerts.addAll(concerts);
        } else {
            mConcerts = concerts;
        }
        SyncAdapter.lock.unlock();
    }

    void postExecute() {
        if (getView() == null) {
            return;
        }

        mAdapter = new ConcertListAdapter(getActivity(),
                R.id.concert_list_view, mConcerts);

        mListView.setAdapter(mAdapter);

        showLayout();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        mActionBarButtons = menu;
        inflater.inflate(R.menu.concert_list_actions, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                doSync();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void hideLayout() {
        mConcertListLayout.setVisibility(View.GONE);
        mLoadingSpinner.setVisibility(View.VISIBLE);
    }

    void showLayout() {
        mConcertListLayout.setVisibility(View.VISIBLE);
        mLoadingSpinner.setVisibility(View.GONE);
    }

    void doSync() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                mActionBarButtons.findItem(R.id.action_refresh).setEnabled(
                        false);
                hideLayout();
            }

            @Override
            protected Void doInBackground(Void... params) {
                new SyncAdapter(getActivity(), false).onPerformSync(null, null,
                        null, null, null);
                updateConcertList();
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                ((BaseAdapter) mListView.getAdapter()).notifyDataSetChanged();
                showLayout();
                MenuItem menuItem = mActionBarButtons
                        .findItem(R.id.action_refresh);
                if (menuItem != null) {
                    menuItem.setEnabled(true);
                }
            }

        }.execute();
    }

}
