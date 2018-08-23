package com.android.darshan.locationtracker;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.darshan.locationtracker.database.UserEntry;
import com.android.darshan.locationtracker.database.UserRepository;
import com.android.darshan.locationtracker.di.MyApp;
import com.android.darshan.locationtracker.view_models.SignupViewModel;

import java.util.List;

import javax.inject.Inject;

/*
    When checking for existing user(based on their user name), we assume that querying the Db will require more time than
    just checking the user in already loaded AllUsersList(From ViewModel).

    The above mentioned condition may not be true when AllUsersList contain say 10,000 entries.
    In that case directly query Db for the user(using their user name)
 */

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";

    private EditText etUserName, etPhoneNumber, etPassword;
    private Button btnSignup;

    @Inject UserRepository mUserRepository;
    private List<UserEntry> mAllUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //Dependency injection
        ((MyApp)getApplication()).getAppComponent().inject(this);

//        mUserRepository = new UserRepository(getApplication());

        etUserName = findViewById(R.id.et_userName);
        etPhoneNumber = findViewById(R.id.et_phoneNumber);
        etPassword = findViewById(R.id.et_password);
        btnSignup = findViewById(R.id.btn_signup);

        SignupViewModel signupViewModel = ViewModelProviders.of(this).get(SignupViewModel.class);
        LiveData<List<UserEntry>> allUsersLiveData = signupViewModel.loadAllUsers();
        allUsersLiveData.observe(this, new Observer<List<UserEntry>>() {
            @Override
            public void onChanged(@Nullable List<UserEntry> userEntries) {
                mAllUsers = userEntries;
            }
        });

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = etUserName.getText().toString();
                String phoneNum = etPhoneNumber.getText().toString();
                String password = etPassword.getText().toString();

                if(!userName.equals("") && !phoneNum.equals("") && !password.equals("")) {
                    UserEntry userEntry = new UserEntry(userName, phoneNum, password, "", "");
                    if(!isUserAlreadyExist(userName)) {
                      mUserRepository.insertUser(userEntry);
                      finish();
                    } else {
                        Toast.makeText(SignupActivity.this, "User name already exist!", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(SignupActivity.this, "All fields must be filled.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private boolean isUserAlreadyExist(final String userName) {
        boolean exists = false;

        if(mAllUsers != null && mAllUsers.size() > 0) {
            for(UserEntry userEntry : mAllUsers) {
                if(userEntry.getUserName().equals(userName)) {
                    exists = true;
                    break;
                }
            }
        }

        return exists;

    }


}
