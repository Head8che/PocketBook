package com.example.pocketbook.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.pocketbook.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// https://developers.google.com/maps/documentation/android-sdk/start
// https://developer.android.com/reference/android/widget/AutoCompleteTextView
public class LocationActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener {
    private GoogleMap mGoogleMap;
    private Geocoder geocoder; // Gets the converts address to coordinates
    FusedLocationProviderClient mFusedLocationProviderClient;
    private static final String TAG = "LocationActivity";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int MY_PERMISSIONS_ACCESS_COARSE_LOCATION = 1;

    // Permission status is user allows location services
    private Boolean mPermissionStatus = false;
    private AutoCompleteTextView mSearchField; // Widget
    private Button setLocationButton;
    private LatLng mPinnedMap;
    private double Lat;
    private double Lng;
    private String locationAddress;
    Marker marker;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        mSearchField = (AutoCompleteTextView) findViewById(R.id.locationSearchField);
        setLocationButton = (Button) findViewById(R.id.locationConfirmButton);

        setLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (marker != null) {
                    Intent locationData = new Intent();
                    locationData.putExtra("Location", mPinnedMap);
                    locationData.putExtra("Lat", Lat);
                    locationData.putExtra("Lng",Lng);
                    locationData.putExtra("Address", locationAddress);
                    setResult(RESULT_OK, locationData);
                    finish();

                } else {
                    Toast.makeText(LocationActivity.this, "No location specified", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mSearchField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == KeyEvent.ACTION_DOWN
                        || event.getAction() == KeyEvent.KEYCODE_ENTER) {

                    String inputtedLocation = mSearchField.getText().toString();
                    geoLocate(inputtedLocation);
                }
                return false;
            }
        });
        //
        if (checkServicesAvailability()) {
            getLocationPermission();
        }
        else{
            Log.d("Service Status", "Access Denied to Google Services");
        }
    }

    public boolean checkServicesAvailability() {
        GoogleApiAvailability mapSdkConnection = GoogleApiAvailability.getInstance();
        int isAvailable = mapSdkConnection.isGooglePlayServicesAvailable(this);
        if (isAvailable == ConnectionResult.SUCCESS) {
            return true;
        } else if (mapSdkConnection.isUserResolvableError(isAvailable)) {
            Dialog dialog = mapSdkConnection.getErrorDialog(this, isAvailable, 0);
            dialog.show();
        } else {
            Toast.makeText(this, "Error Connecting to Maps SDK", Toast.LENGTH_LONG).show();
        }
        return false;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("Status", "Failed Connection");
    }

    // When the map is ready the user will have a functionality such as long click to drop the pin or drag the current pin to a new spot
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        mGoogleMap = googleMap;
        if(mGoogleMap != null) {
            mGoogleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                @Override
                public void onMapLongClick(LatLng latLng) {
                    Geocoder geocoder = new Geocoder(LocationActivity.this);
                    List<Address> list = null;
                    try {
                        list = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Address addressLocation = list.get(0);
                    marker.setTitle(addressLocation.getAddressLine(0));
                    setPin(addressLocation.getAddressLine(0), addressLocation.getLatitude(), addressLocation.getLongitude());
                    Log.d("Long Click:", addressLocation.getAddressLine(0));
                }
            });

            mGoogleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                @Override
                public void onMarkerDragStart(Marker marker) {
                }

                @Override
                public void onMarkerDrag(Marker marker) {
                }

                @Override
                public void onMarkerDragEnd(Marker marker) {
                    Geocoder geoLocation = new Geocoder(LocationActivity.this);
                    LatLng ll = marker.getPosition();
                    List<Address> list = null;
                    try {
                        list = geoLocation.getFromLocation(ll.latitude, ll.longitude, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Address addressLocation = list.get(0);
                    marker.setTitle(addressLocation.getAddressLine(0));
                    setPin(addressLocation.getAddressLine(0),addressLocation.getLatitude(), addressLocation.getLongitude());
                    Log.d("Marker Drag:", addressLocation.getAddressLine(0));


                }
            });
        }

        // Gets the device location if permission was granted
        if (mPermissionStatus) {
            getDeviceLocation();

            // Needed to see the current device's location
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(false);



        }

    }

    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {FINE_LOCATION, COARSE_LOCATION};
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mPermissionStatus = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(this, permissions, MY_PERMISSIONS_ACCESS_COARSE_LOCATION);
            }
        } else {
            ActivityCompat.requestPermissions(this, permissions, MY_PERMISSIONS_ACCESS_COARSE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mPermissionStatus = false;
        switch (requestCode) {
            case MY_PERMISSIONS_ACCESS_COARSE_LOCATION: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; ++i) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mPermissionStatus = false;
                            return;
                        }
                    }
                    mPermissionStatus = true;
                    initMap();
                }
            }
        }
    }

    // Locates the inputted Location from the user
    private void geoLocate(String inputtedLocation) {
        hideSoftKeyboard();
        Geocoder geocoder = new Geocoder(LocationActivity.this);
        String searchString = mSearchField.getText().toString();
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(searchString, 1);
        } catch (IOException e) {
            Log.e(TAG, "geoLocate: IOException: " + e.getMessage());
        }

        if (list.size() > 0) {
            Address addressLocation = list.get(0);
            goToLocationZoom(addressLocation.getLatitude(), addressLocation.getLongitude(), 15f, addressLocation.getAddressLine(0));
            setPin( addressLocation.getAddressLine(0), addressLocation.getLatitude(), addressLocation.getLongitude());
            Lat = addressLocation.getLatitude();
            Lng = addressLocation.getLongitude();
            locationAddress = addressLocation.getAddressLine(0);
            Log.d("GEoLocate", addressLocation.toString());
//            Log.d("GEoLocate", addressLocation.getLocality());
//            Log.d("GEoLocate", addressLocation.getAdminArea());
//            Log.d("GEoLocate", addressLocation.getFeatureName());
        }
    }

    // initializing the map
    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.LocationActivity);
        if (mapFragment != null) {
            mapFragment.getMapAsync(LocationActivity.this);
        }
    }

    //Current Location of the device [Bug] // Will resolve
    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting the devices current location");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (mPermissionStatus) {
                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            if (task.getResult() != null) {
                                Location currentLocation = (Location) task.getResult();
                                goToLocationZoom(currentLocation.getLatitude(), currentLocation.getLongitude(), 15f,"LOCATION");
                                Geocoder geocoder = new Geocoder(LocationActivity.this);
                                Address addressLocation = null;
                                List<Address> list2 = new ArrayList<>();
                                try {
                                    list2 = geocoder.getFromLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), 1);
                                    addressLocation = list2.get(0);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                setPin( addressLocation.getAddressLine(0), currentLocation.getLatitude(), currentLocation.getLongitude());
                            }
                        } else {
                            Toast.makeText(LocationActivity.this, "Unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e(TAG,"Error: " + e.getMessage());
        }
    }


    public void goToLocationZoom(double lat, double lng, float zoom, String title) {
        LatLng latilong = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latilong, zoom);
        if (mGoogleMap != null) {
            mGoogleMap.moveCamera(update);
        }
        hideSoftKeyboard();
    }



    // Dropped Pin
    private void setPin(String title, double lat, double lng) {
        if (marker != null) {
            marker.remove();
        }
        mPinnedMap = new LatLng(lat, lng);
        MarkerOptions options = new MarkerOptions()
                .draggable(true)
                .title(title)
                .position(mPinnedMap);
        if (mGoogleMap != null) {
            marker = mGoogleMap.addMarker(options);
        }
    }

    private void hideSoftKeyboard() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }


}