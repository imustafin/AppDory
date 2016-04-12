package me.appdory;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class AccountFragment extends Fragment {

    TextView mName;
    Button mLoginButton;
    Button mLogoutButton;
    Button mRefreshButton;

    public AccountFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_account, container,
                false);

        mName = (TextView) view.findViewById(R.id.name);
        mLoginButton = (Button) view.findViewById(R.id.login);
        mLogoutButton = (Button) view.findViewById(R.id.logout);
        mRefreshButton = (Button) view.findViewById(R.id.refresh);

        mLogoutButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "No operation",
                        Toast.LENGTH_SHORT).show();
            }
        });

        mLoginButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "No operation",
                        Toast.LENGTH_SHORT).show();
            }
        });

        mRefreshButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "no action", Toast.LENGTH_SHORT)
                        .show();
            }
        });

        return view;
    }

    void launchLoginActivity() {
        Intent loginActivityIntent = new Intent(getActivity(),
                LoginActivity.class);
        loginActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginActivityIntent);
    }

}
