package com.example.miniproj;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class RouteService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    // Binder given to clients
    // Registered callbacks
    private static final String LOGSERVICE = "#######";
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    Context c = this;
    public static int PacketSize;
    public static int LocationInterval;
    public static int LocationFastestInterval;
    /// private static final int drawableIcon = R.drawable.cast_ic_notification_small_icon;
    public static PendingIntent contentIntent;
    public static String NotificationTxt;
    public static String NotificationTitle;
    public static int drawable_small;

    @Override
    public void onCreate() {
        super.onCreate();
        // building Google Api Client at the startup
        buildGoogleApiClient();
        Toast.makeText(RouteService.this, "Test 1", Toast.LENGTH_LONG).show();
        //calling and building notification for ANDROID O within 5 seconds of the Service Startup.
//        ForegroundServiceInitialize();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!mGoogleApiClient.isConnected())
            mGoogleApiClient.connect();

        return START_NOT_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        //Inntizlizing ArrayList
//        TrackLoc = new ArrayList < > ();
        // We already have taken the permission from the user so this is redundant here...
        @SuppressLint("MissingPermission") Location l = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        startLocationUpdate();

    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this, "Connection Has been Suspended..", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        Toast.makeText(this, connectionResult.getErrorMessage().toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        try {
            Log.d("Latitude", ""+location.getLatitude());
            Intent broadcastIntent = new Intent("com.lax.tracker");
            //  broadcastIntent.setAction(MainActivity.mBroadcastStringAction);
            broadcastIntent.putExtra("latitude", location.getLatitude());
            broadcastIntent.putExtra("longitude", location.getLongitude());
            broadcastIntent.putExtra("speed", String.valueOf(location.getSpeed()));
            broadcastIntent.putExtra("time", String.valueOf(location.getTime()));
            broadcastIntent.putExtra("altitude", String.valueOf(location.getAltitude()));

            sendBroadcast(broadcastIntent);
        } catch (NullPointerException e) {
            Toast.makeText(RouteService.this, "Location has been disabled...", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }


    private void initLocationRequest() {
        //these are the parameters defined.
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(LocationInterval);
        mLocationRequest.setFastestInterval(LocationFastestInterval);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdate() {
        //Getting all the paramters.
        initLocationRequest();
        //Requesting Location Updates..
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }


    private void stopLocationUpdate() {
        //TO Remove Location Udpates
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
    }


}