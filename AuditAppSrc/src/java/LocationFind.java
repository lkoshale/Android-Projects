package com.machadalo.audit;

import android.content.Context;
        import android.content.Intent;
        import android.content.pm.PackageManager;
        import android.location.Address;
        import android.location.Geocoder;
        import android.location.Location;
        import android.support.v4.app.ActivityCompat;
        import android.support.v4.content.ContextCompat;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.util.Log;
        import android.widget.Toast;

        import com.google.android.gms.common.ConnectionResult;
        import com.google.android.gms.common.api.GoogleApiClient;
        import com.google.android.gms.location.LocationListener;
        import com.google.android.gms.location.LocationRequest;
        import com.google.android.gms.location.LocationServices;

        import java.io.IOException;
        import java.util.List;
        import java.util.Locale;

public class LocationFind extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    String address;
    Location location;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    double lat, lon;
    int geocoderMaxResults = 1;
    public static int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION =1;


    String activityType;
    String activityDate;
    String comment;
    String shortlistedInventoryDetailsId;
    String supplierName;
    String inventoryType;

    Context context = this;
//    Bundle name = getIntent().getExtras();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supplierName = getIntent().getStringExtra("supplierName");
        Bundle bundle = getIntent().getExtras();

        activityType = bundle.getString("activityType");
        activityDate = bundle.getString("activityDate");
        //comment is not present i bunlde :checked by lokesh 16-6-17
      //  Log.e("In LocationFind ","values is  "+bundle.containsKey("comment"));
        comment = bundle.getString("comment");
        shortlistedInventoryDetailsId = bundle.getString("shortlistedInventoryDetailsId");
        inventoryType = bundle.getString("inventoryType");

        buildGoogleApiClient();
    }


    @Override
    public void onConnected(Bundle bundle) {

        mLocationRequest = LocationRequest.create();

        int permissionCheck = ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionCheck == PackageManager.PERMISSION_GRANTED){
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        }

        mLocationRequest.setInterval(1000); // Update location every second
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
        catch (Exception e){
            updateUI();
        }


        location = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (location != null) {
            lat = (location.getLatitude());
            lon = (location.getLongitude());

        }
        updateUI();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        lat = (location.getLatitude());
        lon = (location.getLongitude());
        updateUI();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        buildGoogleApiClient();
    }

    synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();


    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

            // MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        }
        mGoogleApiClient.connect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.disconnect();
    }

    void updateUI() {
        address = getAddressLine(this) + ", " + getLocality(this);
        lat = (getLatitude());
        lon = (getLongitude());
        if(address != null)
            launchActivity();
        else {
            Toast.makeText(getApplicationContext(), "Address Resolution Failed, Please Try Again", Toast.LENGTH_LONG).show();
            finish();
        }

    }


    public double getLatitude() {
        if (location != null) {
            lat = location.getLatitude();
        }

        return lat;
    }

    /**
     * GPSTracker longitude getter and setter
     * @return
     */
    public double getLongitude() {
        if (location != null) {
            lon = location.getLongitude();
        }

        return lon;
    }


    public List<Address> getGeocoderAddress(Context context) {
        if (location != null) {

            Geocoder geocoder = new Geocoder(context, Locale.ENGLISH);

            try {
                /**
                 * Geocoder.getFromLocation - Returns an array of Addresses
                 * that are known to describe the area immediately surrounding the given latitude and longitude.
                 */
                List<Address> addresses = geocoder.getFromLocation(lat, lon, this.geocoderMaxResults);
                return addresses;
            } catch (IOException e) {
                //e.printStackTrace();
//                Log.e(TAG, "Impossible to connect to Geocoder", e);
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
            String addressLine = address.getAddressLine(0);

            return addressLine;
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
            String locality = address.getLocality();

            return locality;
        }
        else {
            return null;
        }
    }
    private void launchActivity() {
        try {
            String latitude = String.valueOf(lat);
            String longitude = String.valueOf(lon);
            Intent demoCaptureIntent = new Intent(this, DemoCapture.class);
            demoCaptureIntent.putExtra("id", "");
            demoCaptureIntent.putExtra("address", address);
            demoCaptureIntent.putExtra("location", "");
            demoCaptureIntent.putExtra("submitStatus", "");
            demoCaptureIntent.putExtra("inventDate", "");
            demoCaptureIntent.putExtra("adcounter", "");
            demoCaptureIntent.putExtra("actualactivity", "AssignedAcitivty");
            demoCaptureIntent.putExtra("imagepath", "NULL");
            demoCaptureIntent.putExtra("latitude", latitude);
            demoCaptureIntent.putExtra("longitude", longitude);
            demoCaptureIntent.putExtra("supplierName", supplierName);
            demoCaptureIntent.putExtra("activityType", activityType);
            demoCaptureIntent.putExtra("activityDate", activityDate);
            demoCaptureIntent.putExtra("shortlistedInventoryDetailsId", shortlistedInventoryDetailsId);
            demoCaptureIntent.putExtra("comment", comment);
            demoCaptureIntent.putExtra("inventoryType", inventoryType);
            startActivity(demoCaptureIntent);
            finish();
        }catch (Exception e){
            Log.e("Error",e.getMessage());
        }
    }
}
