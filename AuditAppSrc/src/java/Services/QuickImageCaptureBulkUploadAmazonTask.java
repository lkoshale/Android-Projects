package com.machadalo.audit.Services;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.ContentValues;
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
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
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
import com.google.android.gms.playlog.internal.LogEvent;
import com.machadalo.audit.Constants;
import com.machadalo.audit.extras.QueueData;
import com.machadalo.audit.sqllite.DataBaseHandler;
import com.machadalo.audit.sqllite.InventoryImagePathTable;
import com.machadalo.audit.sqllite.QuickImageCapturePathTable;
import com.machadalo.audit.sqllite.QuickImageCaptureTable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.machadalo.audit.DemoCapture.getCredProvider;

/**
 * Created by Lokesh on 03-07-2017.
 */

public class QuickImageCaptureBulkUploadAmazonTask extends IntentService {

    public String supplierName;
    public String inventoryName;
    Double lat,lon;
    Uri imageUri;
    public String path;
    public String QuickCaptureTable_ID;

    public QuickImageCaptureBulkUploadAmazonTask() {
        super("QuickImageCaptureBulkImageUpload");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        supplierName = intent.getStringExtra("supplierName");
        QuickCaptureTable_ID = intent.getStringExtra("ID");

        final DataBaseHandler db_handle = DataBaseHandler.getInstance(getApplicationContext());
        db_handle.printTotalRows(QuickImageCaptureTable.TABLE_NAME);
        final List<QuickImageCaptureTable> data = getData();


        if (data == null || data.size() == 0) {
            Log.d("amazon upload", "No data to upload");
            stopSelf();
            return;
        }

        try {
            Log.e("QuickService","inside Try");
            AWSCredentialsProvider credProvider;
            credProvider = getCredProvider(null, getApplicationContext());

            // Create an S3 client
            AmazonS3 s3 = new AmazonS3Client(credProvider);
            s3.setRegion(Region.getRegion(Regions.AP_NORTHEAST_1));

            for (int i = 0; i < data.size(); i++) {

                Log.e("QuickService","inside FOR");
                //adde to get lat lon of the image when taken from the table
                // CoordinateAdressMapTable

                if (data.get(i).getLat() == null || data.get(i).getLon() == null){
                    continue;
                }

                lat = Double.parseDouble(data.get(i).getLat());
                lon = Double.parseDouble(data.get(i).getLon());

                //getAdredd from the geolocation
                //getAdd();
                // address = getAddressLine(getApplicationContext()) + ", " + getLocality(getApplicationContext());


                // process only for files which  are not uploaded yet to amazon
                if (data.get(i).getIsAmazonUploaded().compareTo("true")==0)
                    continue;

                final String fileName = data.get(i).getImageName();
               // final String inventoryImageId = data.get(i).getInventoryImagePathTableId();
                Log.e("QuickService",fileName);

                File file = new File(Environment.getExternalStorageDirectory() + File.separator + "machadalo_temp", fileName);

                //GETTING URI IMAGE FILE
                imageUri = Uri.fromFile(file);
                //painting lat lon and date time
              //  addLocationDateInImage(fileName);

                path = imageUri.getPath();
                Log.e("InQuickServivce",path+" "+data.get(i).getLocalPath());

                TransferUtility utility = new TransferUtility(s3, getApplicationContext());

                TransferObserver transferObserver = utility.upload(
                        Constants.bucket,          /* The bucket to upload to */
                        fileName,/* The key for the uploaded object */
                        file               /* The file where the data to upload exists */
                );

                Log.e("QuickamazonUpload", "uploading.. " + fileName);

                transferObserver.setTransferListener(new TransferListener() {
                    @Override
                    public void onStateChanged(int id, TransferState state) {
                        if (state == TransferState.COMPLETED) {
                            // mark this entry as amazon uploaded
                           // InventoryImagePathTable.updateIsAmazonUploaded(db_handle, inventoryImageId, Constants.True);

                            SQLiteDatabase db = db_handle.getWritableDatabase();

                            ContentValues values = new ContentValues();
                            values.put(QuickImageCapturePathTable.KEY_IS_AMAZON_UPLOADED,"true");

                            db.update(QuickImageCaptureTable.TABLE_NAME,values,QuickImageCapturePathTable.KEY_LOCAL_PATH+ " = \'" + path+ "\'",null);


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


    public List<QuickImageCaptureTable> getData(){

        List<QuickImageCaptureTable> list = new ArrayList<>();

        String Query = "SELECT * FROM "+ QuickImageCapturePathTable.TABLE_NAME+" WHERE "+QuickImageCapturePathTable.KEY_QuickImageCaptureID
                + " = \'"+QuickCaptureTable_ID+"\' ;";

        DataBaseHandler db_handle = DataBaseHandler.getInstance(getApplicationContext());
        SQLiteDatabase db = db_handle.getReadableDatabase();

        Cursor cursor = db.rawQuery(Query,null);

        while (cursor.moveToNext()){

            String lat = cursor.getString(cursor.getColumnIndex(QuickImageCapturePathTable.KEY_LAT));
            String lon = cursor.getString(cursor.getColumnIndex(QuickImageCapturePathTable.KEY_LON));
            String localPath = cursor.getString(cursor.getColumnIndex(QuickImageCapturePathTable.KEY_LOCAL_PATH));
            String imageName = cursor.getString(cursor.getColumnIndex(QuickImageCapturePathTable.KEY_IMAGE_NAME));
            String isAmazonUploaded = cursor.getString(cursor.getColumnIndex(QuickImageCapturePathTable.KEY_IS_AMAZON_UPLOADED));

            QuickImageCaptureTable object = new QuickImageCaptureTable();
            object.setImageName(imageName);
            object.setLat(lat);
            object.setLon(lon);
            object.setLocalPath(localPath);
            object.setSupplierName(supplierName);
            object.setIsAmazonUploaded(isAmazonUploaded);

            list.add(object);

        }

        return list;
    }




}
