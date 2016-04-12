package me.appdory;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.perm.kate.api.KException;

import org.json.JSONException;

import java.io.IOException;

import me.appdory.Accounts.VkAccount;

public class MainActivity extends Activity {

    DrawerLayout mDrawerLayout;
    ListView mDrawerList;
    ActionBarDrawerToggle mDrawerToggle;
    TextView mDrawerUsernameTextView;

    String mNavigationDrawerConcerts;
    String mNavigationDrawerMyMusic;
    String mNavigationDrawerLogout;
    String mNavigationDrawerAccount;
    String mNavigationDrawerSaveState;

    String mAppName;

    String mTitle;

    // The authority for the sync adapter's content provider
    public static final String AUTHORITY = "me.appdory.datasync.provider";
    // An account type, in the form of a domain name
    public static final String ACCOUNT_TYPE = "me.appdory";
    // The account name
    public static final String ACCOUNT = "AppDory Dummy Account";
    // Instance fields
    public static Account account;

    // Sync interval constants
    public static final long SECONDS_PER_MINUTE = 60L;
    public static final long MINUTES_PER_HOUR = 60L;
    public static final long SYNC_INTERVAL_IN_HOURS = 1L;
    public static final long SYNC_INTERVAL = SYNC_INTERVAL_IN_HOURS
            * MINUTES_PER_HOUR * SECONDS_PER_MINUTE;
    // A content resolver for accessing the provider
    ContentResolver mResolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeAccount();
        initializeDrawerList();
        openFragment(new ConcertListFragment(), mNavigationDrawerConcerts);
    }

    void initializeDrawerList() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.drawer_list);

        Resources resources = getResources();

        mNavigationDrawerAccount = resources
                .getString(R.string.navigation_drawer_account);
        mNavigationDrawerConcerts = resources
                .getString(R.string.navigation_drawer_concerts);
        mNavigationDrawerLogout = resources
                .getString(R.string.navigation_drawer_logout);
        mNavigationDrawerMyMusic = resources
                .getString(R.string.navigation_drawer_my_music);
        mNavigationDrawerSaveState = resources
                .getString(R.string.navigation_drawer_save_state);

        mAppName = resources.getString(R.string.app_name);

        mDrawerList.addHeaderView(getHeaderView());
        mDrawerUsernameTextView = (TextView) findViewById(R.id.drawer_username_textview);
        loadUserInfoFromVk();

        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, resources
                .getStringArray(R.array.navigation_drawer_strings)));

        mDrawerList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                selectItem(position);
            }
        });

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActionBar().setTitle(mAppName);
            }

        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
    }

    View getHeaderView() {
        View view;
        LayoutInflater inflater = LayoutInflater.from(this);
        view = inflater.inflate(R.layout.drawer_header, mDrawerList, false);
        return view;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    void selectItem(int position) {
        String item = (String) mDrawerList.getItemAtPosition(position);
        if (item == null) {
            mDrawerLayout.closeDrawer(mDrawerList);
            return;
        }
        if (item.equals(mNavigationDrawerAccount)) {
            openFragment(new AccountFragment(), mNavigationDrawerAccount);
        }
        if (item.equals(mNavigationDrawerConcerts)) {
            openFragment(new ConcertListFragment(), mNavigationDrawerConcerts);
        }
        if (item.equals(mNavigationDrawerLogout)) {
            logout();
        }
        if (item.equals(mNavigationDrawerMyMusic)) {
            openFragment(new ArtistsFragment(), mNavigationDrawerMyMusic);
        }
        if (item.equals(mNavigationDrawerSaveState)) {
            openFragment(new SaveStateFragment(), mNavigationDrawerSaveState);
        }
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    void openFragment(Fragment fragment, String caption) {

        mTitle = caption;
        getActionBar().setTitle(caption);

        FragmentManager fragmentManager = getFragmentManager();

        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment).commit();
    }

    void logout() {
        System.err.println("logout");
        Accounts.VkAccount.logout();
        Accounts.VkAccount.removeFromSharedPreferences(MainActivity.this);
        Intent loginActivityIntent = new Intent(MainActivity.this,
                LoginActivity.class);
        loginActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginActivityIntent);
        System.err.println("Should be started");
    }

    @SuppressWarnings("deprecation")
    public static Account createSyncAccount(Context context) {
        // Create the account type and default account
        Account newAccount = new Account(ACCOUNT, ACCOUNT_TYPE);
        // Get an instance of the Android account manager
        AccountManager accountManager = (AccountManager) context
                .getSystemService(ACCOUNT_SERVICE);

        if (Build.VERSION.SDK_INT >= 22) {
            accountManager.removeAccountExplicitly(newAccount);
        } else {
            accountManager.removeAccount(newAccount, null, null);
        }

		/*
         * Add the account and account type, no password or user data If
		 * successful, return the Account object, otherwise report an error.
		 */
        if (accountManager.addAccountExplicitly(newAccount, null, null)) {
        } else {
            throw new RuntimeException("Error making account");
        }
        return newAccount;
    }

    void initializeAccount() {
        account = createSyncAccount(this);
        mResolver = getContentResolver();

        // Inform the system that this account is eligible for auto sync
        // when the network is up
        ContentResolver.setSyncAutomatically(account, AUTHORITY, true);

        // Inform the system that this account supports sync
        ContentResolver.setIsSyncable(account, AUTHORITY, 1);

        // Recommend a schedule for automatic synchronization.
        // The system may modify this based
        // on other scheduled syncs and network utilization.
        ContentResolver.addPeriodicSync(account, AUTHORITY, Bundle.EMPTY,
                SYNC_INTERVAL);
    }

    void loadUserInfoFromVk() {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected void onPreExecute() {
                mDrawerUsernameTextView.setText("Loading...");
            }

            @Override
            protected String doInBackground(Void... params) {
                VkAccount vk = Accounts.VkAccount.getInstance();
                try {
                    vk.loadNameSurnamePhoto();
                } catch (IOException | JSONException | KException e) {
                    return e.toString();
                }
                return vk.name + " " + vk.surname;
            }

            @Override
            protected void onPostExecute(String result) {
                mDrawerUsernameTextView.setText(result);
                loadUserpic();
            }
        }.execute();
    }

    void loadUserpic() {
        new AsyncTask<Void, Void, Bitmap>() {

            ProgressBar progress;
            ImageView image;

            @Override
            protected void onPreExecute() {
                progress = (ProgressBar) findViewById(R.id.drawer_userpic_progress);
                image = (ImageView) findViewById(R.id.drawer_userpic);
                progress.setVisibility(View.VISIBLE);
                image.setVisibility(View.GONE);
            }

            @Override
            protected Bitmap doInBackground(Void... params) {
                try {
                    Bitmap bmp = Accounts.VkAccount.getInstance()
                            .loadPhotoBitmap();
                    return Utils.getCircular(bmp);
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                image.setImageBitmap(result);
                image.setVisibility(View.VISIBLE);
                progress.setVisibility(View.GONE);
            }
        }.execute();
    }

}
