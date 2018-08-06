package com.android.darshan.locationtracker;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.darshan.locationtracker.Utils.Consts;
import com.android.darshan.locationtracker.Utils.DbHelper;
import com.android.darshan.locationtracker.models.Location;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int ERROR_DIALOG_REQUEST = 1001;

    private boolean isTracking = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        isTracking = sharedPreferences.getBoolean(getString(R.string.pref_tracking_key), true);

        Button btnStartTracking = findViewById(R.id.btn_startTracking);
        btnStartTracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isDeviceHaveServices()) {
                    if(isLoggedIn()) {
                        launchMap();
                    } else {
                        Toast.makeText(MainActivity.this, "Login First", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Error:Can not launch map", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    private boolean isLoggedIn() {
        SharedPreferences loginSharedPreferences =
                getSharedPreferences(getString(R.string.shared_pref_name), MODE_PRIVATE);

        if(loginSharedPreferences != null) {
            if(loginSharedPreferences.contains(getString(R.string.shared_pref_key_user_name))) {
                String userName = loginSharedPreferences.getString(getString(R.string.shared_pref_key_user_name), "");
                if (!userName.equals("")) {
                    Log.d(TAG, "checkLogInStatus: logged in");
                    return true;
                }
            }
        }

        return false;
    }


    private void launchMap() {
        if(isTracking) {
            //load real time location
            Log.d(TAG, "launchMap: online");
            Intent intent = new Intent(this, MapsActivity.class);
            intent.putExtra(getString(R.string.intent_tracking), true);
            startActivity(intent);
        } else {
            // load offline location from database
            Intent intent = new Intent(this, MapsActivity.class);
            intent.putExtra(getString(R.string.intent_tracking), false);
            startActivity(intent);
            Log.d(TAG, "launchMap: offline");
        }
    }


    /*
        Check if user device have necessary services to show GoogleMaps
     */
    public boolean isDeviceHaveServices() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();

        int available = googleApiAvailability.isGooglePlayServicesAvailable(this);

        if(available == ConnectionResult.SUCCESS) {
            //everything is ok, we can move to next steps
            Log.d(TAG, "isDeviceHaveServices: Google play services is working");
            return true;
        } else if(googleApiAvailability.isUserResolvableError(available)) {
            //some error occurs, but it can be resolved
            Log.d(TAG, "isDeviceHaveServices: Some error occur, but we can fix it");
            //custom predefined Maps API dialog
            Dialog dialog = googleApiAvailability.getErrorDialog(this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            //I can not do anything. User can not make Maps request.
            Log.d(TAG, "isDeviceHaveServices: Device does not have required services, and it can not be resolved");
            Toast.makeText(this, "You can't make maps request", Toast.LENGTH_SHORT).show();
        }
        return false;
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if(itemId == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        if(itemId == R.id.action_login) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            return true;
        }

        if(itemId == R.id.action_logout) {
            SharedPreferences sharedPreferences =
                    getSharedPreferences(getString(R.string.shared_pref_name), MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(getString(R.string.shared_pref_key_user_name), "");
            editor.apply();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
