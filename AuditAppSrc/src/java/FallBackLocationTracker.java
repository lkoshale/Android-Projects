package com.machadalo.audit;

/**
 * Created by root on 6/1/17.
 */

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import android.app.Service;


public class FallBackLocationTracker extends Service implements LocationTracker, LocationTracker.LocationUpdateListener {

    // Get Class Name
    private String TAG = FallBackLocationTracker.class.getName();

    private boolean isRunning;

    private ProviderLocationTracker gps;
    private ProviderLocationTracker net;

    // How many Geocoder should return our GPSTracker
    int geocoderMaxResults = 1;

    private LocationUpdateListener listener;

    Location lastLoc;
    long lastTime;

    public FallBackLocationTracker(Context context) {
        Log.d(TAG, "FallBackLocationTracker() constructor");
        gps = new ProviderLocationTracker(context, ProviderLocationTracker.ProviderType.GPS);
        net = new ProviderLocationTracker(context, ProviderLocationTracker.ProviderType.NETWORK);
        //start();
    }

    public void start(){
        Log.d(TAG, "start() is called");
        if(isRunning){
            //Already running, do nothing
            Log.d(TAG, "start() --> already running. return ");
            return;
        }
        //Start both
        Log.d(TAG, "start() --> starting gps ");
        gps.start(this);
        Log.d(TAG, "start() --> starting network ");
        net.start(this);
        Log.d(TAG, "start() --> setting isRunning=True");
        isRunning = true;
    }

    public void start(LocationUpdateListener update) {
        Log.d(TAG, "start() with listener called in Fallback class");
        start();
        listener = update;
    }


    public void stop(){
        if(isRunning){
            gps.stop();
            net.stop();
            isRunning = false;
            listener = null;
        }
    }

    public boolean hasLocation(){
        //If either has a location, use it
        Log.d(TAG, "hasLocation() is called");
        return gps.hasLocation() || net.hasLocation();
    }

    public boolean hasPossiblyStaleLocation(){
        //If either has a location, use it
        Log.d(TAG, "hasPossiblyStaleLocation() is called");
        return gps.hasPossiblyStaleLocation() || net.hasPossiblyStaleLocation();
    }

    public Location getLocation(){
        Log.d(TAG, "getLocation() is called");
        Location ret = gps.getLocation();
        if(ret == null){
            Log.d(TAG, "getLocation() --> no gps location found. switching to network");
            ret = net.getLocation();
        }
        Log.d(TAG, "getLocation() --> returning location");
        return ret;
    }

    public Location getPossiblyStaleLocation(){
        Log.d(TAG, "getPossiblyStaleLocation() is called");
        Location ret = gps.getPossiblyStaleLocation();
        if(ret == null){
            Log.d(TAG, "getPossiblyStaleLocation() --> not gps found. network");
            ret = net.getPossiblyStaleLocation();
        }
        return ret;
    }

    public void onUpdate(Location oldLoc, long oldTime, Location newLoc, long newTime) {
        Log.d(TAG, "onUpdate() is called");
        boolean update = false;

        //We should update only if there is no last location, the provider is the same, or the provider is more accurate, or the old location is stale
        if(lastLoc == null){
            update = true;
        }
        else if(lastLoc != null && lastLoc.getProvider().equals(newLoc.getProvider())){
            update = true;
        }
        else if(newLoc.getProvider().equals(LocationManager.GPS_PROVIDER)){
            update = true;
        }
        else if (newTime - lastTime > 5 * 60 * 1000){
            update = true;
        }

        if(update){
            if(listener != null){
                listener.onUpdate(lastLoc, lastTime, newLoc, newTime);
            }
            lastLoc = newLoc;
            lastTime = newTime;
        }
    }


    /**
     * Get list of address by latitude and longitude
     * @return null or List<Address>
     */
    public List<Address> getGeocoderAddress(Context context) {
        if (lastLoc != null) {

            Geocoder geocoder = new Geocoder(context, Locale.ENGLISH);

            try {
                /**
                 * Geocoder.getFromLocation - Returns an array of Addresses
                 * that are known to describe the area immediately surrounding the given latitude and longitude.
                 */
                double latitude = lastLoc.getLatitude();
                double longitude = lastLoc.getLongitude();
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, this.geocoderMaxResults);
                return addresses;
            } catch (IOException e) {
                //e.printStackTrace();
                Log.e(TAG, "Impossible to connect to Geocoder", e);
            }
        }

        return null;
    }

    /**
     * Try to get AddressLine
     * @return null or addressLine
     */
    public String getAddressLine(Context context) {
        List<Address> addresses = getGeocoderAddress(context);

        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);
            return address.getAddressLine(0);
        } else {
            return null;
        }
    }

    /**
     * Try to get Locality
     * @return null or locality
     */
    public String getLocality(Context context) {
        List<Address> addresses = getGeocoderAddress(context);

        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);
            return address.getLocality();
        }
        else {
            return null;
        }
    }

    /**
     * Try to get Postal Code
     * @return null or postalCode
     */
    public String getPostalCode(Context context) {
        List<Address> addresses = getGeocoderAddress(context);

        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);
            return address.getPostalCode();
        } else {
            return null;
        }
    }

    /**
     * Try to get CountryName
     * @return null or postalCode
     */
    public String getCountryName(Context context) {
        List<Address> addresses = getGeocoderAddress(context);
        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);
            return address.getCountryName();

        } else {
            return null;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
