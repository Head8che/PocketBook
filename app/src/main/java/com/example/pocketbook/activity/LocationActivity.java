package com.example.pocketbook.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.pocketbook.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
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
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// https://developers.google.com/maps/documentation/android-sdk/start
// https://developer.android.com/reference/android/widget/AutoCompleteTextView
public class LocationActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mGoogleMap;
    FusedLocationProviderClient mFusedLocationProviderClient;
    private static final String TAG = "LocationActivity";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int MY_PERMISSIONS_ACCESS_COARSE_LOCATION = 1;

    // Permission status is user allows location services
    private Boolean mPermissionStatus = false;
    private AutoCompleteTextView mSearchField; // Widget
    private Button setLocationButton;
    private double Lat;
    private double Lng;
    private String locationAddress;

    Marker marker;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        mSearchField = findViewById(R.id.locationSearchField);
        setLocationButton = findViewById(R.id.locationConfirmButton);

        setLocationButton.setOnClickListener(v -> {
            setLocationButton.setClickable(false);
            if (marker != null) {
                Intent locationData = new Intent();
//                    locationData.putExtra("MeetingDetails", mPinnedMap);
                locationData.putExtra("Lat", Lat);
                locationData.putExtra("Lng",Lng);
                locationData.putExtra("Address", locationAddress);
                setResult(RESULT_OK, locationData);
                finish();

            } else {
                Toast.makeText(LocationActivity.this,
                        "No location specified", Toast.LENGTH_SHORT).show();
            }
            setLocationButton.setClickable(true);
        });

        mSearchField.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH
                    || actionId == EditorInfo.IME_ACTION_DONE
                    || event.getAction() == KeyEvent.ACTION_DOWN
                    || event.getAction() == KeyEvent.KEYCODE_ENTER) {

                geoLocate();
            }
            return false;
        });
        //
        if (checkServicesAvailability()) {
            getLocationPermission();
        }
        else{
            Log.d("Service Status", "Access Denied to Google Services");
        }
    }

    /**
     * Connects to the Google Api Server
     * @return true if the connection to the server was successful, false otherwise
     */
    public boolean checkServicesAvailability() {
        GoogleApiAvailability mapSdkConnection = GoogleApiAvailability.getInstance();
        int isAvailable = mapSdkConnection.isGooglePlayServicesAvailable(this);
        if (isAvailable == ConnectionResult.SUCCESS) {
            return true;
        } else if (mapSdkConnection.isUserResolvableError(isAvailable)) {
            Dialog dialog = mapSdkConnection.getErrorDialog(this, isAvailable, 0);
            dialog.show();
        } else {
            Toast.makeText(this, "Error Connecting to Maps SDK",
                    Toast.LENGTH_LONG).show();
        }
        return false;
    }

    /**
     * When the map is ready the user will have a functionality
     * such as long click to drop the pin or drag the current pin to a new spot
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        mGoogleMap = googleMap;

        // Gets the device location if permission was granted
        if (mPermissionStatus) {
            getDeviceLocation();

            // Needed to see the current device's location
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(false);



        }

    }

    /**
     * Gets the location permission
     *
     */
    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {FINE_LOCATION, COARSE_LOCATION};
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                mPermissionStatus = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(this, permissions,
                        MY_PERMISSIONS_ACCESS_COARSE_LOCATION);
            }
        } else {
            ActivityCompat.requestPermissions(this, permissions,
                    MY_PERMISSIONS_ACCESS_COARSE_LOCATION);
        }
    }

    /**
     * Request permission to access the location
     * @param requestCode code that the location activity was launched with
     * @param permissions code that the location activity returns
     * @param grantResults grant access
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        mPermissionStatus = false;

        if (requestCode == MY_PERMISSIONS_ACCESS_COARSE_LOCATION) {
            if (grantResults.length > 0) {
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        mPermissionStatus = false;
                        return;
                    }
                }
                mPermissionStatus = true;
                initMap();
            }
        }
    }

    /**
     * Locates the inputted MeetingDetails from the user
     */
    private void geoLocate() {
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
            goToLocationZoom(addressLocation.getLatitude(), addressLocation.getLongitude(),
                    15f);
            setPin( addressLocation.getAddressLine(0), addressLocation.getLatitude(),
                    addressLocation.getLongitude());
            Lat = addressLocation.getLatitude();
            Lng = addressLocation.getLongitude();
            locationAddress = addressLocation.getAddressLine(0);
            Log.d("GEoLocate", addressLocation.toString());
        }
    }

    /**
     * Initializing the map
     */
    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.LocationActivity);
        if (mapFragment != null) {
            mapFragment.getMapAsync(LocationActivity.this);
        }
    }

    /**
     * Gets the current location from the device
     */
    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting the devices current location");
        mFusedLocationProviderClient = LocationServices
                .getFusedLocationProviderClient(this);
        try {
            if (mPermissionStatus) {
                final Task<Location> location = mFusedLocationProviderClient.getLastLocation();

                location.addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult() != null) {
                            Location currentLocation = task.getResult();
                            goToLocationZoom(currentLocation.getLatitude(),
                                    currentLocation.getLongitude(), 15f);
                            Geocoder geocoder = new Geocoder(LocationActivity.this);
                            Address addressLocation = null;
                            List<Address> list2;
                            try {
                                list2 = geocoder.getFromLocation(currentLocation
                                        .getLatitude(),
                                        currentLocation.getLongitude(), 1);
                                addressLocation = list2.get(0);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (addressLocation != null) {
                                setPin(addressLocation.getAddressLine(0),
                                        currentLocation.getLatitude(),
                                        currentLocation.getLongitude());
                            }
                        }
                    } else {
                        Toast.makeText(LocationActivity.this,
                                "Unable to get current location",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e(TAG,"Error: " + e.getMessage());
        }
    }


    /**
     * Allow the user to zoom in into a specific location
     * @param lat Gets the latitude of the selected location
     * @param lng Gets the longitude of the selected location
     * @param zoom Zooms in into the selected location
     */
    public void goToLocationZoom(double lat, double lng, float zoom) {
        LatLng latilong = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latilong, zoom);
        if (mGoogleMap != null) {
            mGoogleMap.moveCamera(update);
        }
        hideSoftKeyboard();
    }


    /**
     * Allow the user to use the dropped pin option to select a location
     * @param title Gets the street name/number of the location
     * @param lat Gets the latitude of the selected location
     * @param lng Gets the longitude of the selected location
     */
    private void setPin(String title, double lat, double lng) {
        if (marker != null) {
            marker.remove();
        }
        LatLng mPinnedMap = new LatLng(lat, lng);
        MarkerOptions options = new MarkerOptions()
                .draggable(false)
                .title(title)
                .position(mPinnedMap);
        if (mGoogleMap != null) {
            marker = mGoogleMap.addMarker(options);
        }
    }

    /**
     * Hides the Keyboard
     */
    private void hideSoftKeyboard() {
        this.getWindow().setSoftInputMode(WindowManager
                .LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }


}