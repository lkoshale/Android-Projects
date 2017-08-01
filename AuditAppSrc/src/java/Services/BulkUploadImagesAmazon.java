package com.machadalo.audit.Services;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.util.Log;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.machadalo.audit.Constants;
import com.machadalo.audit.extras.QueueData;
import com.machadalo.audit.sqllite.DataBaseHandler;
import com.machadalo.audit.sqllite.InventoryImagePathTable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static com.machadalo.audit.DemoCapture.getCredProvider;

/**
 * Created by root on 13/2/17.
 */

public class BulkUploadImagesAmazon extends IntentService {

    Double lat,lon;
    String address;
    Uri imageUri;
    public int getAddcalls = 0;


    public BulkUploadImagesAmazon(){
        super("BulkUploadImagesAmazon");
    }

    @Override
    protected void onHandleIntent(Intent intent) {


        final DataBaseHandler db_handle = DataBaseHandler.getInstance(getApplicationContext());
        List<QueueData> data = InventoryImagePathTable.getQueuedData(db_handle);


        if (data == null || data.size() == 0) {
            Log.d("amazon upload", "No data to upload");
            stopSelf();
            return;
        }

        try {

            AWSCredentialsProvider credProvider;
            credProvider = getCredProvider(null, getApplicationContext());

            // Create an S3 client
            AmazonS3 s3 = new AmazonS3Client(credProvider);
            s3.setRegion(Region.getRegion(Regions.AP_NORTHEAST_1));

            for (int i = 0; i < data.size(); i++) {


                //adde to get lat lon of the image when taken from the table
                // CoordinateAdressMapTable
                lat = Double.parseDouble(data.get(i).getLat());
                lon = Double.parseDouble(data.get(i).getLon());

                //getAdredd from the geolocation
                getAdd();
                // address = getAddressLine(getApplicationContext()) + ", " + getLocality(getApplicationContext());


                // process only for files which  are not uploaded yet to amazon
                if (data.get(i).getIsAmazonUploaded().equals(Constants.True))
                    continue;

                final String fileName = data.get(i).getImagePath();
                final String inventoryImageId = data.get(i).getInventoryImagePathTableId();

                File file = new File(Environment.getExternalStorageDirectory() + File.separator + "machadalo_temp", fileName);

                //GETTING URI IMAGE FILE
                imageUri = Uri.fromFile(file);
                //painting lat lon and date time
                addLocationDateInImage(fileName);


                TransferUtility utility = new TransferUtility(s3, getApplicationContext());

                TransferObserver transferObserver = utility.upload(
                        Constants.bucket,          /* The bucket to upload to */
                        fileName,/* The key for the uploaded object */
                        file               /* The file where the data to upload exists */
                );

                Log.e("amazonUpload", "uploading.. " + fileName);

                transferObserver.setTransferListener(new TransferListener() {
                    @Override
                    public void onStateChanged(int id, TransferState state) {
                        if (state == TransferState.COMPLETED) {
                            // mark this entry as amazon uploaded
                            InventoryImagePathTable.updateIsAmazonUploaded(db_handle, inventoryImageId, Constants.True);
                            Log.e("TransferComplete", "file " + fileName + " uploaded");
                        }
                    }

                    @Override
                    public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

                    }

                    @Override
                    public void onError(int id, Exception ex) {
                        Log.e("error", "error" + ex.getMessage());
                    }
                });
            } // for loop
        } // try
        catch (Exception e) {
            Log.d("AmazonUpload", e.getMessage());
            stopSelf();
        } finally {

        }
    }
    // onHandler event


    public void getAdd(){

        getAddcalls++;

        if (getAddcalls > 13){

            address = "Not available";
            return;
        }


        if (getAddressLine(getApplicationContext()) != null && getLocality(getApplicationContext())!=null ){

            address = getAddressLine(getApplicationContext()) + ", " + getLocality(getApplicationContext());

        }
        else{

            try {
                Log.e("InAsyntask","Sleep for 1 sec");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            getAdd();
        }

    }

    //for getting Address
    public String getAddressLine(Context context) {
        Log.e("bulkUploadAmazon","getAddrsLine");
        List<Address> addresses = getGeocoderAddress(context);

        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);
            String addressLine = address.getAddressLine(0);

            return addressLine;
        } else {
            return null;
        }
    }


    public List<Address> getGeocoderAddress(Context context) {
        Log.e("bulkUploadAmazon","getGuiCode");

        Geocoder geocoder = new Geocoder(context, Locale.ENGLISH);

        try {
            /**
             * Geocoder.getFromLocation - Returns an array of Addresses
             * that are known to describe the area immediately surrounding the given latitude and longitude.
             */
            List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);
            return addresses;
        } catch (IOException e) {
            //e.printStackTrace();
//                Log.e(TAG, "Impossible to connect to Geocoder", e);
        }

        return null;
    }

    public String getLocality(Context context) {
        Log.e("bulkUploadAmazon","getLocality");
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


    //Add location and date in image before uploading
    public void addLocationDateInImage(String fileName){

        String pic_name = fileName;
        Bitmap alteredBitmap;
        String cords;
        String mydate;

        Log.e("bulkUploadAmazon","in AddLOcation And Date in Iamge .....Image Painting Started");

        Uri selectedImage = imageUri;
        getContentResolver().notifyChange(selectedImage, null);
        ContentResolver cr = getContentResolver();
        Bitmap bitmap;

        try {
            // get the bitmap from the image uri using the content resolver api to get the image

            // bitmap= StringToBitMap(compressImage(selectedImage.toString())) ;
            bitmap = MediaStore.Images.Media.getBitmap(cr, selectedImage);
            alteredBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
            Canvas canvas = new Canvas(alteredBitmap);
            Paint paint = new Paint();
            canvas.drawBitmap(bitmap, 0, 0, paint);
            paint.setColor(Color.WHITE);
            paint.setTextSize(80);

            // String text = GetAddress(lat,lon);
            cords = " "+lat + "," + lon+ " ,";

            //TODO the image of map is downloaded here
            //   String url = "http://maps.google.com/maps/api/staticmap?markers=color:red%7Clabel:S%7C11211&center=" + lat + "," + lon + "&zoom=17&maptype=hybrid&roadmap=" + lat + "," + lon + "&size=360x190&sensor=false";
            //   new DemoCapture.DownloadImageTask((ImageView) findViewById(R.id.image_view_map)).execute(url);


            mydate = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
           // Log.d("AddressINonActFrres", Address);

           // canvas.drawText(cords,190,250,paint);
            address = cords +" "+address;

            canvas.drawText(address, 190, 250, paint);

            canvas.drawText(mydate, 190, 350, paint);
            //canvas.drawText("Oxford Chambers", 190, 370, paint);
            // set the bitmap to the image view
            // /*Maps recreation to combine two image

            // txtDateandTime.setText("Address:" + Address + "\n Date :" + mydate + "\n");

            int nh = (int) (alteredBitmap.getHeight() * (512.0 / alteredBitmap.getWidth()));
            Bitmap scaled = Bitmap.createScaledBitmap(alteredBitmap, 512, nh, true);

            // imageView.setImageBitmap(scaled);

            //  /*Out of memory Error

            //End
            // /* imageView.setImageBitmap(alteredBitmap);
            File path = new File(Environment.getExternalStorageDirectory()
                    + File.separator + "machadalo_temp");
            File photo = new File(Environment.getExternalStorageDirectory().toString()
                    + File.separator + "machadalo_temp", pic_name);
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(photo);
                alteredBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();
                // MediaStore.Images.Media.insertImage(context.getContentResolver(),
                //          alteredBitmap, photo.getPath(), supplierName);
                //
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
                // notify the user
                //TODO do it again whole
                //outOfMemoryException();
                // Toast.makeText(DemoCapture.this, "failed to load", Toast.LENGTH_LONG).show();
                Log.e("CameraApp3", e.toString());
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            //TODO gives some leaking error now
            /*
            MediaScannerConnection.scanFile(this, new String[] { photo.toString() }, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            Log.i("ExternalStorage", "Scanned " + path + ":");
                            Log.i("ExternalStorage", "-> uri=" + uri);
                        }
                    });
                */

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
