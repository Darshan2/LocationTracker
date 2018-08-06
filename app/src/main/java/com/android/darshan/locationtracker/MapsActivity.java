package com.android.darshan.locationtracker;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.darshan.locationtracker.Utils.Consts;
import com.android.darshan.locationtracker.Utils.DbHelper;
import com.android.darshan.locationtracker.models.User;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.prefs.PreferenceChangeListener;

/*
    If user exist from this activity by pressing home button. App will start the foreground
    service and keep track of the periodic location updates. Till user intentionally
    closes the app.

    App is made to update location in background, only if MapActivity is not destroyed.

    User can stop the periodic Location updates. Either by pressing back button when MapActivity is
    in foreground or by closing the app in Overview list
 */
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "MapsActivity";

    private static final String PERMISSION_ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final int REQUEST_CHECK_SETTINGS = 100;
    private static final float DEFAULT_ZOOM = 18f;

    private FusedLocationProviderClient mFusedLocationClient;

    private GoogleMap mMap;
    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;
    private LatLng mCurrentLatLng;
    private boolean isTrackingOn = true;
    private boolean isForeground = true;
    private String mUserName = "";

    private GeofencingClient mGeofencingClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Log.d(TAG, "onCreate: ");

        getPermissions();

        getLogInInfo();

        Intent intent = getIntent();
        if(intent != null && intent.hasExtra(getString(R.string.intent_tracking))) {
            isTrackingOn = intent.getBooleanExtra(getString(R.string.intent_tracking), true);
        }

        if(isTrackingOn) {
            createLocationRequest();
            //load real time location every 10secs max, 5sec min
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

            mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult == null) {
                        return;
                    }
                    for (Location location : locationResult.getLocations()) {
                        // Update UI with location data
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        LatLng latLng = new LatLng(latitude, longitude);
                        Log.d(TAG, "onLocationResult: " + location);
                        moveCamera(latLng);
                    }
                }
            };
        }

    }



    private void getLogInInfo() {
        SharedPreferences loginSharedPreferences =
                getSharedPreferences(getString(R.string.shared_pref_name), MODE_PRIVATE);

        if(loginSharedPreferences.contains(getString(R.string.shared_pref_key_user_name))) {
            String userName = loginSharedPreferences.getString(getString(R.string.shared_pref_key_user_name), "");
            if (!userName.equals("")) {
                mUserName = userName;
            }
        }
    }

    private void loadLocationFromDB() {
        Toast errorToast = Toast.makeText(this, "Unknown last location", Toast.LENGTH_SHORT);

        if (!mUserName.equals("")) {
            DbHelper dbHelper = new DbHelper(this);
            ArrayList<User> userArrayList = dbHelper.getAllUsersWithUserName(mUserName);
            User user = userArrayList.get(0);
//            Log.d(TAG, "loadLocationFromDB: " + user);

            com.android.darshan.locationtracker.models.Location lastKnownLocation = user.getLastLocation();

            try {
                double latitude = Double.valueOf(lastKnownLocation.getLatitude());
                double longitude = Double.valueOf(lastKnownLocation.getLongitude());
                LatLng latLng = new LatLng(latitude, longitude);
                moveCamera(latLng);

            } catch (NumberFormatException e) {
                e.printStackTrace();
                errorToast.show();
            }
        }
    }


    private void moveCamera(LatLng latLng) {
        if(isForeground) {
            Log.d(TAG, "moveCamera: ");
            mCurrentLatLng = latLng;
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        isForeground = true;
        if(isTrackingOn) {
            startLocationUpdates();
        }

    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest,
                mLocationCallback,
                null);
    }


    private void initMap() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        Log.d(TAG, "initMap: ");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Toast.makeText(this, "Map is ready", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: ");
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        if(!isTrackingOn) {
            //to get rid of default gps button
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            loadLocationFromDB();
        }

        mMap.setMyLocationEnabled(true);

    }


    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                Log.d(TAG, "onSuccess: createLocationRequest");
                // All location settings are satisfied. The client can initialize
                // location requests here.
                // ...
            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(MapsActivity.this,
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });


    }


    private void getPermissions() {
        int grantPermission = ActivityCompat.checkSelfPermission(this, PERMISSION_ACCESS_FINE_LOCATION);

        if(grantPermission == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "getPermissions: Permission granted");
            initMap();

        } else {
            Log.d(TAG, "getPermissions: Ask user for permission/s");
            String[] permissionArr = { PERMISSION_ACCESS_FINE_LOCATION } ;
            ActivityCompat.requestPermissions(this, permissionArr, 123);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //permission granted
            Log.d(TAG, "onRequestPermissionsResult: Permission granted");
            initMap();
        } else {
            Log.d(TAG, "onRequestPermissionsResult: Permission not granted");
        }
    }


    @Override
    protected void onStop() {
        Log.d(TAG, "onStop: ");
        super.onStop();
        
        if(isTrackingOn) {
            //start fore ground service
            Log.d(TAG, "onStop: Start service");
            isForeground = false;
            Intent intent = new Intent(this, LocationMonitorService.class);
            intent.setAction(Consts.STARTFOREGROUND_ACTION);
            intent.putExtra(getString(R.string.intent_start_foreground), true);
            startService(intent);
        }
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        super.onDestroy();

        if(isTrackingOn) {
            if(mCurrentLatLng != null) {
                DbHelper dbHelper = new DbHelper(this);
                double latitude = mCurrentLatLng.latitude;
                double longitude = mCurrentLatLng.longitude;

                String latitudeStr = String.valueOf(latitude);
                String longitudeStr = String.valueOf(longitude);

                com.android.darshan.locationtracker.models.Location newLocation =
                        new com.android.darshan.locationtracker.models.Location(latitudeStr, longitudeStr);

                int count = dbHelper.updateLastLocation(newLocation, mUserName);
                if (count == 0) {
                    Toast.makeText(this, "New location update failed", Toast.LENGTH_SHORT).show();
                }
            }

            stopLocationUpdates();

        }
    }


    private void stopLocationUpdates() {
        Log.d(TAG, "stopLocationUpdates: ");
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);

        Intent intent = new Intent(this, LocationMonitorService.class);
        intent.setAction(Consts.STOPFOREGROUND_ACTION);
        intent.putExtra(getString(R.string.intent_start_foreground), false);
        startService(intent);
    }
}
