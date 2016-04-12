package me.appdory;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import me.appdory.Accounts.VkAccount;
import me.appdory.sync.SyncAdapter;

public class LoginActivity extends Activity {

    ImageButton mLogInWithVkButton;
    Button mVkLogInDoneButton;

    ProgressBar mProgress;

    RelativeLayout mLayout;

    Button mToRegion;

    boolean mWasLoggedInOnCreate;

    int loggedInServices = 0;

    static final int VK_LOGIN_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mLogInWithVkButton = (ImageButton) findViewById(R.id.log_in_with_vk_button);
        mVkLogInDoneButton = (Button) findViewById(R.id.vk_log_in_done_button);
        mLayout = (RelativeLayout) findViewById(R.id.login_layout);
        mProgress = (ProgressBar) findViewById(R.id.login_progress);

        mToRegion = (Button) findViewById(R.id.login_to_region);

        mToRegion.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, RegionSelectorActivity.class);
                startActivity(i);

            }
        });

        initializeVk();

        mLogInWithVkButton.setImageResource(R.drawable.ic_vk_icon_disabled);
        mVkLogInDoneButton.setEnabled(false);

        if (Accounts.VkAccount.gotInstance()) {
            onVkLoggedIn();
        }

        mWasLoggedInOnCreate = Accounts.VkAccount.gotInstance();

        mVkLogInDoneButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (loggedInServices > 0) {
                    loginDone();
                } else {
                    Toast.makeText(LoginActivity.this, R.string.not_logged_in,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        mLogInWithVkButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, VkLoginActivity.class);
                startActivityForResult(intent, VK_LOGIN_REQUEST_CODE);

            }
        });
    }

    void loginDone() {
        Toast.makeText(LoginActivity.this, R.string.syncing, Toast.LENGTH_SHORT).show();
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                hideLayout();
            }

            @Override
            protected Void doInBackground(Void... params) {
                Accounts.VkAccount.getInstance().saveToSharedPreferences(LoginActivity.this);
                SyncAdapter syncAdapter = new SyncAdapter(LoginActivity.this, false);
                syncAdapter.resetViewedNotifications();
                syncAdapter.onPerformSync(null, null, null, null, null);
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                launchMainActivity();
            }
        }.execute();


    }

    void initializeVk() {
        VkAccount.readFromSharedPreferences(LoginActivity.this);
    }

    void launchMainActivity() {
        Intent mainActivityIntent = new Intent(LoginActivity.this,
                MainActivity.class);
        mainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainActivityIntent);
    }

    protected void onResume() {
        super.onResume();

        if (mWasLoggedInOnCreate) {
            launchMainActivity();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VK_LOGIN_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // авторизовались успешно
                String accessToken = data.getStringExtra("token");
                long userId = data.getLongExtra("user_id", 0);
                Accounts.VkAccount.setNewInstance(accessToken, userId);
                onVkLoggedIn();
            }
        }
    }

    void hideLayout() {
        for (int i = 0; i < mLayout.getChildCount(); i++) {
            mLayout.getChildAt(i).setVisibility(View.GONE);
        }
        mProgress.setVisibility(View.VISIBLE);
    }

    void showLayout() {
        for (int i = 0; i < mLayout.getChildCount(); i++) {
            mLayout.getChildAt(i).setVisibility(View.VISIBLE);
        }
        mProgress.setVisibility(View.GONE);
    }

    void onVkLoggedIn() {
        mVkLogInDoneButton.setEnabled(true);
        mLogInWithVkButton.setImageResource(R.drawable.ic_vk_icon);
        loggedInServices++;
    }

}
