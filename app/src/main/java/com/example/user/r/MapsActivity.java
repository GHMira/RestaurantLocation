package com.example.user.r;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Toast;

import com.example.user.r.database.DbHelper;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, RestoranFragment.OnListFragmentInteractionListener {

    private GoogleMap mMap;
    private HashMap<Marker, Object> restorani;
    private Marker currentUserLocation;

    private DbHelper dbHelper;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        dbHelper = new DbHelper(getApplicationContext());
        db = dbHelper.getReadableDatabase();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().
                findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);

        LocationManager locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);

        LocationListener locationListener = new UserLocationListener(this, mMap);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 5000, 10, locationListener);

    }

    void onLocationChange(Marker currentUserLocation) {
        this.currentUserLocation = currentUserLocation;
        if(currentUserLocation != null) {

            LatLng currentPosition = currentUserLocation.getPosition();

            Object dataTransfer[] = new Object[3];
            dataTransfer[0] = mMap;
            dataTransfer[1] = currentPosition.longitude;
            dataTransfer[2] = currentPosition.latitude;

            GetNearbyRestaurants restaurants = new GetNearbyRestaurants(this);
            restaurants.execute(dataTransfer);
            Toast.makeText(MainActivity.getContext(), "Showing restaurants", Toast.LENGTH_LONG).show();
        }
    }

    void addToDb(Marker marker){
        ContentValues novRestoan = new ContentValues();
        novRestoan.put(DbHelper.NAME, marker.getTitle());
        novRestoan.put(DbHelper.LOKACIJA, marker.getPosition().toString());
        db.insert(DbHelper.TABLE_NAME, null, novRestoan);
    }

    void asyncResult(HashMap<Marker, Object> result) {
        restorani = result;

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                addToDb(marker);
                for (Map.Entry<Marker, Object> entry : restorani.entrySet()) {

                    Marker restoran = entry.getKey();
                    if( !restoran.equals(marker)) {
                        continue;
                    } else {

                        FragmentManager fragmentManager = getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                        Object[] podaci = (Object[]) entry.getValue();
                        ArrayList<String> list = new ArrayList<>();
                        for(int i = 0; i < podaci.length; i++) {
                            list.add(podaci[i].toString());
                        }

                        Bundle args = new Bundle();
                        args.putStringArrayList("restoran", list);

                        Fragment someFragment = new RestoranFragment();
                        someFragment.setArguments(args);
                        View prikaz = findViewById(R.id.prikaz);

                        Fragment map = fragmentManager.findFragmentById(R.id.map);
//                        fragmentTransaction.remove(map);
//                        fragmentTransaction.commit();
//                        getSupportFragmentManager().popBackStack();
//                        fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.add(R.id.prikaz, someFragment ); // give your fragment container id in first parameter
                        fragmentTransaction.addToBackStack(null);  // if written, this transaction will be added to backstack
                        fragmentTransaction.commit();
                    }

                }
                return false;
            }
        });
    }
}
