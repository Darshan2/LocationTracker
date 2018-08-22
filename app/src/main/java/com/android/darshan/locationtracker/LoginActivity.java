package com.android.darshan.locationtracker;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.darshan.locationtracker.database.UserEntry;
import com.android.darshan.locationtracker.view_models.LogInViewModel;

import java.util.List;

/*
    When checking for authenticated user(based on their user name and pass word),
    we assume that querying the Db will require more time than just checking
    the user in already loaded AllUsersList(From ViewModel).

    The above mentioned condition may not be true when AllUsersList contain say(10,000 entries).
    In that case directly query Db for the user(using their user name and pass word)
 */

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    private EditText etUserName, etPassword;

    private List<UserEntry> mAllUsers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.d(TAG, "onCreate: ");

        etUserName = findViewById(R.id.et_userName);
        etPassword = findViewById(R.id.et_password);
        Button btnLogin = findViewById(R.id.btn_login);
        TextView tvSignUpLink = findViewById(R.id.tv_signUpLink);

        initViewModel();


        tvSignUpLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = etUserName.getText().toString();
                String passWord = etPassword.getText().toString();

                if(mAllUsers != null && mAllUsers.size() > 0) {
                    UserEntry user = getUserWithName(userName);
                    if (user != null) {
                        if (user.getPassWord().equals(passWord)) {
                            Log.d(TAG, "onClick: login success");
                            saveLoginInfo(user);
                            finish();
                        } else {
                            showToast("Incorrect password!");
                        }
                    } else {
                        showToast("User name does not exists!");
                    }
                } else {
                    showToast("Sign up first");
                }
            }
        });
    }

    private void initViewModel() {
        LogInViewModel logInViewModel = ViewModelProviders.of(this).get(LogInViewModel.class);
        LiveData<List<UserEntry>> allUsersLiveData = logInViewModel.loadAllUsers();
        allUsersLiveData.observe(this, new Observer<List<UserEntry>>() {
            @Override
            public void onChanged(@Nullable List<UserEntry> userEntries) {
                mAllUsers = userEntries;
            }
        });
    }


    private UserEntry getUserWithName(String userName) {
        UserEntry user = null;
        for (UserEntry userEntry : mAllUsers) {
//            Log.d(TAG, "getUserWithName: " + userEntry);
            if (userEntry.getUserName().equals(userName)) {
                user = userEntry;
                break;
            }
        }
        return user;
    }


    private void saveLoginInfo(UserEntry user) {
        SharedPreferences sharedPreferences =
                getSharedPreferences(getString(R.string.shared_pref_name), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(getString(R.string.shared_pref_key_user_name), user.getUserName());
        editor.apply();
    }

    private void showToast(String message) {
        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
    }



}
