package com.android.darshan.locationtracker;

import android.Manifest;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
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
import android.util.Log;
import android.widget.Toast;

import com.android.darshan.locationtracker.Utils.Consts;
import com.android.darshan.locationtracker.database.UserEntry;
import com.android.darshan.locationtracker.database.UserRepository;
import com.android.darshan.locationtracker.di.MyApp;
import com.android.darshan.locationtracker.view_models.MapsViewModel;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import javax.inject.Inject;


/*
    If user exist from this activity by pressing home button. App will start the foreground
    service and keep track of the periodic location updates. Till user intentionally
    closes the app.

    App is made to update location in background, only if MapActivity is not destroyed.

    UserEntry can stop the periodic Location updates. Either by pressing back button when MapActivity is
    in foreground or by closing the app in Overview list
 */
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "MapsActivity";

    private static final String PERMISSION_ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final int REQUEST_CHECK_SETTINGS = 100;
    private static final float DEFAULT_ZOOM = 18f;

    @Inject UserRepository mUserRepository;

    private FusedLocationProviderClient mFusedLocationClient;

    private GoogleMap mMap;
    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;
    private LatLng mCurrentLatLng;
    private boolean isTrackingOn = true;
    private boolean isForeground = true;
    private boolean isServiceRunning;
    private String mUserName = "";

    private  UserEntry mUser;
//    private UserRepository mUserRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Log.d(TAG, "onCreate: ");

        //Dependency injection
        ((MyApp)getApplication()).getAppComponent().inject(this);

        getPermissions();

        getLogInInfo();

        initViewModel();

//        mUserRepository = new UserRepository(getApplication());

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
                Log.d(TAG, "getLogInInfo: " + userName);
                mUserName = userName;
            }
        }
    }


    private void initViewModel() {
        if(!mUserName.equals("")) {
            MapsViewModel.Factory factory = new MapsViewModel.Factory(getApplication(), mUserName);
            MapsViewModel mapsViewModel = ViewModelProviders.of(this, factory).get(MapsViewModel.class);

            LiveData<UserEntry> userEntryLiveData = mapsViewModel.getUserWithName();
            userEntryLiveData.observe(this, new Observer<UserEntry>() {
                @Override
                public void onChanged(@Nullable UserEntry userEntry) {
                    mUser = userEntry;
                    if(!isTrackingOn) {
                        loadLocationFromDB(userEntry);
                    }
                }
            });
        }
    }


    private void moveCamera(LatLng latLng) {
        if(isForeground) {
            mCurrentLatLng = latLng;
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
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
        }

        mMap.setMyLocationEnabled(true);

    }


    private void loadLocationFromDB(UserEntry user) {
        Log.d(TAG, "loadLocationFromDB: " );

        try {
            double latitude = Double.valueOf(user.getLatitude());
            double longitude = Double.valueOf(user.getLongitude());
            LatLng latLng = new LatLng(latitude, longitude);
            moveCamera(latLng);

        } catch (NumberFormatException e) {
            e.printStackTrace();
            Toast.makeText(this, "Unknown last location", Toast.LENGTH_SHORT).show();
        }
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
    protected void onResume() {
        super.onResume();

        isForeground = true;
        if(isTrackingOn) {
            //Stop the LocationMonitor service if it is running
            if(isServiceRunning) {
                stopForegroundService();
            }
            startLocationUpdates();
        }

    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop: ");
        super.onStop();

        if(isTrackingOn) {
            //start fore ground service
            Log.d(TAG, "onStop: Start service");
            isServiceRunning = true;
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
                double latitude = mCurrentLatLng.latitude;
                double longitude = mCurrentLatLng.longitude;

                String latitudeStr = String.valueOf(latitude);
                String longitudeStr = String.valueOf(longitude);

                mUser.setLatitude(latitudeStr);
                mUser.setLongitude(longitudeStr);

                mUserRepository.updateUser(mUser);

            }

            stopLocationUpdates();

        }
    }


    private void stopLocationUpdates() {
        Log.d(TAG, "stopLocationUpdates: ");
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);

        stopForegroundService();
    }

    private void stopForegroundService() {
        Log.d(TAG, "stopForegroundService: ");
        isServiceRunning = false;
        Intent intent = new Intent(this, LocationMonitorService.class);
        intent.setAction(Consts.STOPFOREGROUND_ACTION);
        intent.putExtra(getString(R.string.intent_start_foreground), false);
        startService(intent);
    }
}
