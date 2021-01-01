package com.example.miniproj;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private Button mLiveButton;
    private MarkerOptions mMarkerOptions;
    private GoogleMap mMap;
    private boolean mFlag;
    private Marker mMarker;
    private BroadcastReceiver mReciever;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.location_map);
        mLiveButton = findViewById(R.id.live_traking);
        mapFragment.getMapAsync(this);
        mReciever = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                double latitude = intent.getDoubleExtra("latitude", 0.0);
                double longitude = intent.getDoubleExtra("longitude", 0.0);
                Log.d("ther", "adsfasdf");
                mMarker.setPosition(new LatLng(latitude, longitude));
            }
        };
        mFlag = false;
        mLiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mFlag) {

                    registerReceiver(mReciever, new IntentFilter("com.lax.tracker"));
                    mFlag = true;
                }
                else {
                    unregisterReceiver(mReciever);
                    mFlag = false;
                }
            }
        });
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

    private boolean checkPermissions() {
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 100);

            return true;
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPermissions();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Toast.makeText(getApplicationContext(), (grantResults[0] == PackageManager.PERMISSION_GRANTED) + "", Toast.LENGTH_LONG).show();

        Toast.makeText(getApplicationContext(), (grantResults[1] == PackageManager.PERMISSION_GRANTED) + "", Toast.LENGTH_LONG).show();

        if (requestCode == 100) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                startService(new Intent(getApplicationContext(), RouteService.class));
            } else {
                checkPermissions();
            }
        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMarkerOptions = new MarkerOptions().position(sydney).title("Marker in Sydney");
        mMarker = mMap.addMarker(mMarkerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(final LatLng latLng) {
                mMarker.setPosition(latLng);
                new AsyncTask<Void, Void, List<Address>>() {

                    @Override
                    protected List<Address> doInBackground(Void... voids) {
                        List<Address> listAddresses = null;
                        Geocoder geo = new Geocoder(getApplicationContext());
                        try {
                            listAddresses = geo.getFromLocation(latLng.latitude,
                                        latLng.longitude,
                                        1);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        return listAddresses;
                    }

                    public void onPostExecute(List<Address> listAddresses) {
                        Address _address = null;
                        if ((listAddresses != null) && (listAddresses.size() > 0)) {
                            _address = listAddresses.get(0);
                            mMarker.setTitle(_address.getAddressLine(0));
                            Toast.makeText(getApplicationContext(), _address.getAddressLine(0), Toast.LENGTH_LONG).show();
                        }

                    }

                }.execute();
            }
        });
    }
}
