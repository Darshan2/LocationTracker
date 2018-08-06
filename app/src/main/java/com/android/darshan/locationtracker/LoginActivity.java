package com.android.darshan.locationtracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.darshan.locationtracker.Utils.DbHelper;
import com.android.darshan.locationtracker.models.User;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    private EditText etUserName, etPassword;
    private Button btnLogin;
    private TextView tvSignUpLink;

    private DbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mDbHelper = new DbHelper(this);

        etUserName = findViewById(R.id.et_userName);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        tvSignUpLink = findViewById(R.id.tv_signUpLink);

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

                User user = mDbHelper.getUserWith(userName, passWord);
                if(user != null) {
                    Log.d(TAG, "onClick: login success");
                    SharedPreferences sharedPreferences =
                            getSharedPreferences(getString(R.string.shared_pref_name), MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(getString(R.string.shared_pref_key_user_name), user.getUserName());
                    editor.apply();
                    finish();

                } else {
                    Toast.makeText(LoginActivity.this, "User name Or Password is incorrect", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



}
