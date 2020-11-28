package com.example.user.r;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class UserLocationListener implements LocationListener {

    private MapsActivity mapsActivity;
    private GoogleMap mMap;
    private Marker user;

    public UserLocationListener(MapsActivity mapsActivity, GoogleMap googleMap) {
        this.mapsActivity = mapsActivity;
        this.mMap = googleMap;
    }

    @Override
    public void onLocationChanged(Location location) {

        double longitude = location.getLongitude();
        double latitude =  location.getLatitude();

        /*------- To get city name from coordinates -------- */
        String cityName = null;
        Geocoder gcd = new Geocoder( MainActivity.getContext(), Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = gcd.getFromLocation(latitude,longitude, 1);
            if (addresses.size() > 0) {
                System.out.println(addresses.get(0).getLocality());
                cityName = addresses.get(0).getLocality();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        String s = longitude + "\n" + latitude + "\n\nMy Current City is: "
                + cityName;
        // Add a marker on current location and move the camera
        LatLng currentPosition = new LatLng(-latitude, longitude);
        if(this.user == null) {
            this.user = mMap.addMarker(new MarkerOptions()
                    .position(currentPosition)
                    .title("Current location: " + s)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
        } else {
            this.user.setPosition(currentPosition);
            this.user.setTitle("Current location: " + s);
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 12));
        mapsActivity.onLocationChange(this.user);
    }

    public Marker getLocation() {
        return this.user;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
