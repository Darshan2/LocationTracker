package com.android.darshan.locationtracker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.darshan.locationtracker.Utils.DbHelper;
import com.android.darshan.locationtracker.models.User;

import java.util.ArrayList;

public class SignupActivity extends AppCompatActivity {

    private EditText etUserName, etPhoneNumber, etPassword;
    private Button btnSignup;

    private DbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mDbHelper = new DbHelper(this);

        etUserName = findViewById(R.id.et_userName);
        etPhoneNumber = findViewById(R.id.et_phoneNumber);
        etPassword = findViewById(R.id.et_password);
        btnSignup = findViewById(R.id.btn_signup);

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = etUserName.getText().toString();
                String phoneNum = etPhoneNumber.getText().toString();
                String password = etPassword.getText().toString();

                if(!userName.equals("") && !phoneNum.equals("") && !password.equals("")) {
                    User user = new User(userName, phoneNum, password);
                    if(!checkIfUserAlreadyExist(userName)) {
                      mDbHelper.addNewUserToDB(user);
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


    private boolean checkIfUserAlreadyExist(String userName) {
        ArrayList<User> existingUsersList = mDbHelper.getAllUsersWithUserName(userName);
        if(existingUsersList.size() > 0) {
            return true;
        } else {
            return false;
        }
    }


}
