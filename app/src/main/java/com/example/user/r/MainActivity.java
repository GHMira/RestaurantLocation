package com.example.user.r;

import android.Manifest;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.r.database.DbHelper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.example.user.r.R.layout.activity_main;

public class MainActivity extends AppCompatActivity {

    private int LOCATION_PERMISSION = 1;
    private static Context mContext;


    private DbHelper dbHelper;
    private SQLiteDatabase db;
    private String[] columnNames = {DbHelper.ID, DbHelper.NAME, DbHelper.LOKACIJA};
    private String sortOrder = DbHelper.ID + " DESC";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        mContext = this;
        super.onCreate(savedInstanceState);
        setContentView(activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final TextView firstTextView=(TextView)findViewById(R.id.textView);

        dbHelper = new DbHelper(this);
        db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(DbHelper.TABLE_NAME, columnNames, null, null, null, null, sortOrder);

        Set<String> rows = new HashSet<>();
        while(cursor.moveToNext()) {
            String restoran = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.NAME));
            rows.add(restoran);
        }
        cursor.close();

        if(rows.size() > 0) {
            ListView listView = (ListView) findViewById(R.id.idBaza);
            List list = new ArrayList<>();
            list.addAll(rows);
            ArrayAdapter<String> adapter= new ArrayAdapter<String>(this,  android.R.layout.simple_list_item_1, list);
            listView.setAdapter(adapter);
        }

        Button firstButton=(Button)findViewById(R.id.firstButton);
        firstButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                firstTextView.setText("Lociranje");
                boolean hasLocationPermission = checkPermission();
                if(hasLocationPermission){
                    Toast.makeText(MainActivity.this, "You have already granted location permission!", Toast.LENGTH_SHORT).show();
                } else {
                    requestLocationPermission();
                }
                checkInternetPermission();
                addMap();
            }
        });
    }

    private void checkInternetPermission(){
        if (ContextCompat.checkSelfPermission(MainActivity.getContext(), Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED ) {
            new AlertDialog.Builder(MainActivity.getContext())
                    .setTitle("Permission needed")
                    .setMessage("Allow app to access internet!")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.INTERNET}, 1);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
        } else {
            Toast.makeText(MainActivity.this, "You have already granted internet permission!", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkPermission() {
        int permissionState = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("Allow app to access your location!")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();

        } else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == LOCATION_PERMISSION) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void addMap() {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static Context getContext() {
        return mContext;
    }
}
