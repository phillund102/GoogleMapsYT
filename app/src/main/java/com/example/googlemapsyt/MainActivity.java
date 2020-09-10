package com.example.googlemapsyt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class MainActivity
        extends FragmentActivity
            implements OnMapReadyCallback
{
    //creating variables
    private GoogleMap mapAPI;
    private static final int PERMISSION_REQUEST_CODE = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //mapFragment places the map in the application
        //mapFragment creates a wrapper around a view of a map
        //with the help of fragment in activity_main.xml
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapAPI);
        //If mapFragment is null it will throw the assert
        assert mapFragment != null;
        //getMapAsync class automatically initializes the maps system and view
        //Must be used for mapFragment to work
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mapAPI = googleMap; //Giving mapAPI value of googleMap
        mapAPI.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        //Before asking, checks if permission for location is already granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            //Location permission granted, no need to ask
            //setMyLocationEnabled is the button to get back to user location
            mapAPI.setMyLocationEnabled(true);

            LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            //Get longitude and latitude to update the camera with the right position
            assert location != null;
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();
            LatLng latLng = new LatLng(latitude, longitude);
            //LatLng is Latitude/Longitude and 15 is zooming factor
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng,15);
            mapAPI.moveCamera(cameraUpdate);
        }
        else
        {
            //Permission denied for location
            getLocationPermission();
        }
    }

    private void getLocationPermission()
    {
        //Gets whether it should show UI with rationale before requesting a permission
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION))
        {
            new AlertDialog.Builder(this)
                    //Message should be id to be able to change from english to swedish
                    .setTitle("Location Permission Needed")
                    .setMessage("This map need your location")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener()
                {
                    //Request permission
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                PERMISSION_REQUEST_CODE);
                    }
                }).create().show();
        }
        else
        {
            //Request permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    //Callback on permission request
    //requestCode is int that is specific to match the result
    //in this case PERMISSION_REQUEST_CODE
    //Permission: the request permission (i.e Location)
    //grantResults: PackageManager.PERMISSION_GRANTED or
    //              PackageManager.PERMISSION_DENIED
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        //Could be if-statement but to add more permissions you can add more cases.
        switch (requestCode)
        {
            //First case is Location Request
            case PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        //Location granted
                        //Moving camera to location
                        mapAPI.setMyLocationEnabled(true);
                        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        assert location != null;
                        double longitude = location.getLongitude();
                        double latitude = location.getLatitude();
                        LatLng latLng = new LatLng(latitude, longitude);
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
                        mapAPI.moveCamera(cameraUpdate);
                    }
                }
            }
        }
    }
}