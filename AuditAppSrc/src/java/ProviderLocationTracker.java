package com.machadalo.audit;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by root on 6/1/17.
 */

public class ProviderLocationTracker implements LocationListener, LocationTracker {

    // Get Class Name
    private String TAG = ProviderLocationTracker.class.getName();

    // The minimum distance to change Updates in meters
    private static final long MIN_UPDATE_DISTANCE = 10;

    // The minimum time between updates in milliseconds
    private static final long MIN_UPDATE_TIME = 1000 * 60;

    private LocationManager lm;

    public enum ProviderType{
        NETWORK,
        GPS
    };
    private String provider;

    private Location lastLocation;
    private long lastTime;

    private boolean isRunning;

    private LocationUpdateListener listener;

    public ProviderLocationTracker(Context context, ProviderType type) {
        Log.d(TAG, "ProvideLocationTracker() constructor");
        lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        if(type == ProviderType.NETWORK){
            Log.d(TAG, "ProvideLocationTracker() constructor --> Type provided is network");
            provider = LocationManager.NETWORK_PROVIDER;
        }
        else{
            Log.d(TAG, "ProvideLocationTracker() constructor --> Type provided is gps");
            provider = LocationManager.GPS_PROVIDER;
        }
    }

    public void start(){
        Log.d(TAG, "Start() called");
        if(isRunning){
            //Already running, do nothing
            Log.d(TAG, "start() --> Already running..returning from start()");
            return;
        }
        Log.d(TAG, "start() --> The provider is on. getting updates");

        //The provider is on, so start getting updates.  Update current location
        isRunning = true;
        // start is called, update the location
        lm.requestLocationUpdates(provider, MIN_UPDATE_TIME, MIN_UPDATE_DISTANCE, this);
        lastLocation = null;
        lastTime = 0;
    }

    public void start(LocationUpdateListener update) {

        Log.d(TAG, "start() with UpdateListener called()");
        start();
        Log.d(TAG, "start() with UpdateListener()  --> variable listener is assigned callback update");
        listener = update;

    }

    public void stop(){
        Log.d(TAG, "stop() called");
        if(isRunning){
            lm.removeUpdates(this);
            isRunning = false;
            listener = null;
        }
    }

    public boolean hasLocation(){
        Log.d(TAG, "hasLocation() called");
        if(lastLocation == null){
            Log.d(TAG, "haslocation()--> last location is null returning false");
            return false;
        }
        if(System.currentTimeMillis() - lastTime > 5 * MIN_UPDATE_TIME){
            Log.d(TAG, "haslocation() --> stale, returning false");
            return false; //stale
        }
        Log.d(TAG, "lastlocation() --> returning true");
        return true;
    }

    public boolean hasPossiblyStaleLocation(){
        Log.d(TAG, "hasPossiblyStaleLocation() is called");
        if(lastLocation != null){
            Log.d(TAG, "hasPossiblyStaleLocation() --> lastlocation is not null, hence a stale location can be found");
            return true;
        }
        Log.d(TAG, "hasPossiblyStaleLocation() --> lastlocation is null, return from getLastKnowLocation()");
        return lm.getLastKnownLocation(provider)!= null;
    }

    public Location getLocation(){
        Log.d(TAG, "getLocation() is called.");
        if(lastLocation == null){
            Log.d(TAG, "getLocation()--> lastlocation is null. Returning null");
            return null;
        }
        Log.d(TAG, "getLocation()--> lastlocation is stale. Returning null");
        if(System.currentTimeMillis() - lastTime > 5 * MIN_UPDATE_TIME){
            return null; //stale
        }
        Log.d(TAG, "getLocation()-->Returning lastlocation");
        return lastLocation;
    }
    public Location getPossiblyStaleLocation(){
        Log.d(TAG, "getPossiblyStaleLocation() is called");

        if(lastLocation != null){
            Log.d(TAG, "getPossiblyStaleLocation() --> lastlocation is not null.");
            return lastLocation;
        }
        Log.d(TAG, "getPossiblyStaleLocation() --> lastlocation is null. returning from getLastKnowLocation()");
        return lm.getLastKnownLocation(provider);
    }

    public void onLocationChanged(Location newLoc) {
        Log.d(TAG, "onLocationChanged() is called");
        long now = System.currentTimeMillis();
        if(listener != null){
            Log.d(TAG, "onLocationChanged() --> listner is not null");
            listener.onUpdate(lastLocation, lastTime, newLoc, now);
        }
        lastLocation = newLoc;
        lastTime = now;
    }

    public void onProviderDisabled(String arg0) {

    }

    public void onProviderEnabled(String arg0) {

    }

    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
    }

}
