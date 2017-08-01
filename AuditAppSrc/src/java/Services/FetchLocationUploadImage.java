package com.machadalo.audit.Services;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.format.Time;
import android.util.Log;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.machadalo.audit.AsyncTasks.WriteToInventoryActivityImage;
import com.machadalo.audit.Constants;
import com.machadalo.audit.extras.ConnectionDetector;
import com.machadalo.audit.sqllite.CoordinateAddressMapTable;
import com.machadalo.audit.sqllite.DataBaseHandler;
import com.machadalo.audit.sqllite.InventoryImagePathTable;
import com.machadalo.audit.sqllite.QuickImageCapturePathTable;
import com.machadalo.audit.sqllite.QuickImageCaptureTable;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by Lokesh on 22-06-2017.
 */

// this service is called when SUBMIT button is clicked

    /*
        It fetches the location adds it in Location Table and imageInventoryPathTable then if internet present
        it fetches  address and paint the images with lat lon
        address and cureent date and time then it uploads the image in Django and amazon .

     */

    //intent sevice and extends google call backs and location listener
public class FetchLocationUploadImage extends IntentService implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener
        ,LocationListener {


    String  mode ;  //passed from the intent
    public int MODE = 0 ;
    // Default Mode 0 -----> all data fetched from backened weather called from
    // assigned fragment or pending fragment or completed fargment
    // Mode 1  ------> called from the Capture Faragment with just society name


    Bundle bundle;
    String address;
    Location location;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    double lat, lon;
    Context context ;


    //required details that are passed by through the intenet from the
    // previous intent
    String comment;
    String pic_name;
    String bucket;
    String inventoryImageId;
    String imagePath;
    String myImagePath;
    String imageUriString;
    String cords;
    Uri imageUri;
    Bitmap alteredBitmap;
    String mydate;
    String Address;
    String ID;

    String picturename;
    String uploadFileName;
    String abc;

    boolean insertedLatLon = false;
    boolean Imagepainted = false;
    boolean imageUploaded = false;


    private Uri compressimageUri;

    //for checking internet
    ConnectionDetector cd;
    boolean isInternetPresent = false;

    //cordinate table id field
    long ID_FROM_CORDSTABLE;

    String activityType;
    String activityDate;
    String supplierName;
    String shortlistedInventoryDetailsId;

    //constructor
    public FetchLocationUploadImage(){
        super("test");
    }


    // this method is called first
    // For more in intentService read the Documentation
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        context = getApplicationContext();

        //getting data from DemoCapture class
        bundle = intent.getExtras();

        comment = intent.getStringExtra("comment");

        pic_name = intent.getStringExtra("pic_name");
        bucket = intent.getStringExtra("bucket");
        inventoryImageId = intent.getStringExtra("inventoryImageId");
        imagePath = intent.getStringExtra("imagePath");
        imageUriString = intent.getStringExtra("imageUri");

        if (imageUriString !=null)
            imageUri = Uri.parse(imageUriString);


        ID = intent.getStringExtra("id");
        uploadFileName = intent.getStringExtra("uploadFileName");
        abc = intent.getStringExtra("abc");
        activityType = intent.getStringExtra("activityType");

        activityDate = intent.getStringExtra("activityDate");
        supplierName = intent.getStringExtra("supplierName");
        shortlistedInventoryDetailsId = intent.getStringExtra("shortlistedInventoryDetailsId");

        mode = intent.getStringExtra("mode");

        if (mode!=null && mode.compareTo("CaptureMode")==0){
           MODE = 1;
        }
        else if (mode!=null && mode.compareTo("onlyLocation")==0){
            MODE = 2;
        }


        //for internet connection
        cd = new ConnectionDetector(getApplicationContext());


        Log.e("LoactionService","onHandleIntent");
        //connect to the google client
        buildGoogleApiClient();
        mGoogleApiClient.connect();

    }


    // cllabck after google client connects
    @Override
    public void onConnected(@Nullable Bundle bundle) {

        Log.e("LoactionService","onConnected");

        //create loction request
        mLocationRequest = LocationRequest.create();

        //permission check auto genrated
        int permissionCheck = ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionCheck == PackageManager.PERMISSION_GRANTED){
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        }

        mLocationRequest.setInterval(1000); // Update location every second
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
        catch (Exception e){
            Log.e("in Exception","calling update before");
            e.printStackTrace();
            insertAndUploadImage();
        }


        location = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        if (location != null) {
            Log.e("location not null","second update Ui called");
            lat = (location.getLatitude());
            lon = (location.getLongitude());

        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        Log.e("location is null","second update Ui called");
        insertAndUploadImage();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e("LoactionService","onConnSuspend");
    }


    //if coonection failed try to connect again
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("LoactionService","onConnFailed");
        buildGoogleApiClient();
    }


    // called when the location changes the location request is being made every second
    @Override
    public void onLocationChanged(Location location) {

        Log.e("LoactionService", "onLocationChanged");

            lat = (location.getLatitude());
            lon = (location.getLongitude());
            insertAndUploadImage();

    }

    //builds the google api client
    synchronized void buildGoogleApiClient() {
        Log.e("LocationService","buildApi");
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();


    }


    // this method is called as we get the location information
    //to insert the lat lon in table and upload the image
    public void insertAndUploadImage() {


        Log.e("LoactionService","UpdateUi");

        // get the lat and lon
        lat = (getLatitude());
        lon = (getLongitude());

        if (MODE == 1) {

            isInternetPresent = cd.isConnectingToInternet();

            if (isInternetPresent && insertedLatLon == false && Double.toString(lat).compareTo("0.0") != 0 && Double.toString(lon).compareTo("0.0") != 0) {

                // uploadImage();
                String path = imageUri.getPath();
                Log.e("path", path + "  " + lat + " " + lon);

                DataBaseHandler db_handle = DataBaseHandler.getInstance(getBaseContext());
                SQLiteDatabase db = db_handle.getWritableDatabase();

                ContentValues values = new ContentValues();
                values.put(QuickImageCapturePathTable.KEY_LAT, Double.toString(lat));
                values.put(QuickImageCapturePathTable.KEY_LON, Double.toString(lon));

                // before thi lat lon column are null si update them
                db.update(QuickImageCapturePathTable.TABLE_NAME, values, QuickImageCapturePathTable.KEY_LOCAL_PATH + " = \'" + path + "\'", null);

                // Asyntask to get Adress from lat lon and then paint and upload the image
                FetchLocationUploadImage.AWSUpload uploadObject = new FetchLocationUploadImage.AWSUpload();
                uploadObject.execute(new String[]{});

                insertedLatLon = true;
                mGoogleApiClient.disconnect();
            }

        }else if (MODE == 2){

            if (  insertedLatLon == false && Double.toString(lat).compareTo("0.0") != 0 && Double.toString(lon).compareTo("0.0") != 0){

                if (bundle != null) {
                    Messenger messenger = (Messenger) bundle.get("msg");
                    Message msg = Message.obtain();
                    Bundle bundle1 = new Bundle();
                    bundle1.putString("msg", "lat lon found");
                    bundle1.putString("lat",Double.toString(lat));
                    bundle1.putString("lon",Double.toString(lon));
                    msg.setData(bundle1);

                    try {
                        messenger.send(msg);
                    } catch (RemoteException e) {
                        Log.i("error", "error");
                    }

                }
                insertedLatLon = true;
                mGoogleApiClient.disconnect();
                return;
            }

        }else {
            // to avoid multiple update and insert calls insertedlatlon bool is used
            // if the data is not inserted once and the lat lon is not 0.0 then enter th if clause
            if (insertedLatLon == false && Double.toString(lat).compareTo("0.0") != 0 && Double.toString(lon).compareTo("0.0") != 0) {

                //update lat lon in the Inventory image path table
                InsertLatLonInTable();

                //insert lat lon in table
                DataBaseHandler db_handle = DataBaseHandler.getInstance(getBaseContext());
                SQLiteDatabase db = db_handle.getWritableDatabase();

                ContentValues values = new ContentValues();
                values.put(CoordinateAddressMapTable.KEY_LAT, Double.toString(lat));
                values.put(CoordinateAddressMapTable.KEY_LON, Double.toString(lon));

                //as lat lon are primary key mutliple insertion not allowed but
                // this insert call wil be made many times so it will
                // through an sqlite exception
                try {

                    Log.e("to insert lat and lon", Double.toString(lat) + " " + Double.toString(lon));

                    ID_FROM_CORDSTABLE = db.insert(CoordinateAddressMapTable.TABLE_NAME, null, values);

                } catch (SQLiteConstraintException e) {
                    Log.e("In LocationService", "Duplicate insertion");
                }

                isInternetPresent = cd.isConnectingToInternet();

                //if internte present paint and upload the image
                if (isInternetPresent) {

                    // uploadImage();
                    // Asyntask to get Adress from lat lon and then paint and upload the image
                    FetchLocationUploadImage.AWSUpload uploadObject = new FetchLocationUploadImage.AWSUpload();
                    uploadObject.execute(new String[]{});
                    //upload in Django
                    Log.e("DjangoUpload", activityDate + " " + activityType + " " + shortlistedInventoryDetailsId + " " + inventoryImageId);
                    WriteToInventoryActivityImage instance = new WriteToInventoryActivityImage(getApplicationContext());
                    instance.execute(Constants.INVENTORY_ACTIVITY_IMAGE_URL, activityDate, activityType, shortlistedInventoryDetailsId, comment, pic_name, inventoryImageId,String.valueOf(lat),String.valueOf(lon));

                }

                //data inseerted
                insertedLatLon = true;
                //disconnect the google client
                mGoogleApiClient.disconnect();

            }
        }


        return;

    }




    /**
     * Try to get Locality
     * @return null or locality
     */

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


    // this method is called for adding lat lon and adress in image
    public void addLocationDateInImage(){

    //since there may be multiple callbackes but we need to paint image once
        // this boolesn determines this method is invoked only once after the seevice created
        if (Imagepainted == true){
            return;
        }

        Log.e("Location Service","in AddLOcation And Date in Iamge .....Image Painting Started");

        // getting image bitmap
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
            Log.d("AddressINonActFrres", Address);

            // canvas.drawText(cords,190,250,paint);
            Address =  cords + "   "+Address;
            canvas.drawText(Address, 190, 250, paint);

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
            // TODO gives some leaking error now

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

        Imagepainted = true;

    }


    //used for uploading in Amazon
    public static AWSCredentialsProvider getCredProvider(AWSCredentialsProvider sCredProvider,
                                                         Context appContext) {
        if(sCredProvider == null) {
            sCredProvider = new CognitoCachingCredentialsProvider(
                    appContext,
                    "578460219112", "ap-northeast-1:900e0d1f-df62-4fa8-a948-4eb018a361b5",  "arn:aws:iam::578460219112:role/Cognito_auditappUnauth_Role",
                    null, Regions.AP_NORTHEAST_1);
            sCredProvider.refresh();
        }
        return sCredProvider;
    }

    //used in painting image
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }


    //compress the image
    public String compressImage(String imageUri) {

        Log.d("DemCaptImageUri", imageUri);
        String filePath = getRealPathFromURI(imageUri);
        Log.d("DemCaptFilePath", filePath);

        Bitmap scaledBitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612

        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {               imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);               actualHeight = (int) maxHeight;             } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
            // outOfMemoryException();
        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight,Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
            // outOfMemoryException();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        String filename = getFilename();
        abc= filename;
        try {
            out = new FileOutputStream(filename);

//          write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return filename;

    }

    //used in getting image
    // get path from the uri
    private String getRealPathFromURI(String contentURI) {

        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }
    }


    // used in editing bitmap to add lat lon in image
    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height/ (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;      }       final float totalPixels = width * height;       final float totalReqPixelsCap = reqWidth * reqHeight * 2;       while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }


    // get the file name of image
    public String getFilename() {


        File file = new File(Environment.getExternalStorageDirectory().getPath(), "machadalo_temp");

        if (!file.exists()) {
            file.mkdirs();
        }
        Calendar c = Calendar.getInstance();
        Log.e("Date", String.valueOf(c.getTime()));
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        Time now = new Time();
        String time = String.valueOf(now);
        String formattedDate = df.format(c.getTime());
        String mainPic = formattedDate+"-"+ID+"(Photo).jpg";
        picturename = mainPic;
        String uriSting = (file.getAbsolutePath() + "/" + uploadFileName);

        return uriSting;

    }



    // called if the internet is present
    // this AsynTask gets the address from geolocation
    // Paint the image and upload in the Amazon
    private class AWSUpload extends AsyncTask<String, Void, String> { // asynctask which runs on dif thread to parse json object from server

        public int geocoderMaxResults = 1;
        public int getAddcalls = 0;

        //TODO a bug here when location fetched first time
        public void getAdd(){

            getAddcalls++;

            //if trying counts exceed 13 set address as not available
            // ( lat lon is still available )
            if (getAddcalls > 13){

                address = "Not available";

                //this passes msg to th Handler in the DemoCapture class
                // that Address is not available now
                if (bundle != null) {
                    Messenger messenger = (Messenger) bundle.get("msg");
                    Message msg = Message.obtain();
                    Bundle bundle1 = new Bundle();
                    bundle1.putString("msg", "Unable to get Address NOW!!!");
                    msg.setData(bundle1);

                    try {
                        messenger.send(msg);
                    } catch (RemoteException e) {
                        Log.i("error", "error");
                    }

                }

                return;
            }

            // get Adress from geocoder api
            if (getAddressLine(context) != null && getLocality(context)!=null ){
                address = getAddressLine(context) + ", " + getLocality(context);
            }
            else{

                // if unable to get address thread sleep for 1 sec
                // and try gain 12 times
                try {
                    Log.e("InAsyntask","Sleep for 1 sec");
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //try again
                getAdd();
            }

        }

        // try to get Address for the lat lon and then paints the image and Uploads in  Amazon
        @Override
        protected String doInBackground(String... params) {
            try {

                //get Address
                getAdd();

                Log.e("In Service",lat+" "+lon+" "+address);

                Address = address;

                //paint the image
                addLocationDateInImage();

                if (MODE == 0) {
                    //Update or insert the Adress in the Database in CordinateAdressMapTable
                    DataBaseHandler db_handle = DataBaseHandler.getInstance(getBaseContext());
                    SQLiteDatabase db = db_handle.getWritableDatabase();

                    ContentValues contentValues = new ContentValues();
                    contentValues.put(CoordinateAddressMapTable.KEY_ADD, address);

                    db.update(CoordinateAddressMapTable.TABLE_NAME, contentValues, CoordinateAddressMapTable.KEY_LAT + " = " + "\'" + Double.toString(lat) + "\'", null);
                }


                //printing rows in the LOGCAT
               // db_handle.printTotalRows(CoordinateAddressMapTable.TABLE_NAME);


                /********* Image is painted now upload in amazon ***********/
                Log.e("In ClickedImageService","Asyntask image uploading Starts");

                compressimageUri= getImageUri(getApplicationContext(),alteredBitmap);

                Log.d("DemCaptAWSUpload", compressimageUri.toString());
                compressImage(compressimageUri.toString());

                Log.d("ImageUi", compressimageUri.toString());

                AWSCredentialsProvider credProvider = null;
                credProvider =  getCredProvider(credProvider, getApplicationContext());

                // Create an S3 client
                AmazonS3 s3 = new AmazonS3Client(credProvider);
                s3.setRegion(Region.getRegion(Regions.AP_NORTHEAST_1));

                TransferUtility transferUtility = new TransferUtility(s3, getApplicationContext());

                File file = new File(Environment.getExternalStorageDirectory() + File.separator  +  "machadalo_temp", pic_name);

                TransferObserver transferObserver = transferUtility.upload(
                        bucket,          /* The bucket to upload to */
                        pic_name,/* The key for the uploaded object */
                        file               /* The file where the data to upload exists */
                );

                myImagePath = transferObserver.getAbsoluteFilePath();
//                Log.d("PathinAwsUpload", imagePath);

                java.util.logging.Logger.getLogger("com.amazonaws.request").setLevel(java.util.logging.Level.FINEST);
                transferObserver.setTransferListener(new TransferListener() {
                    @Override
                    public void onStateChanged(int id, TransferState state) {
                        if(state == TransferState.COMPLETED){

                            if (MODE == 1){

                                String path = imageUri.getPath();
                                DataBaseHandler db_handle = DataBaseHandler.getInstance(getApplicationContext());
                                SQLiteDatabase db = db_handle.getWritableDatabase();

                                ContentValues values = new ContentValues();
                                values.put(QuickImageCapturePathTable.KEY_IS_AMAZON_UPLOADED,"true");

                                db.update(QuickImageCapturePathTable.TABLE_NAME,values,QuickImageCapturePathTable.KEY_LOCAL_PATH+ " = \'" + path + "\'",null);


                            }
                            else{
                                // mark this entry as amazon uploaded
                                DataBaseHandler db_handle = DataBaseHandler.getInstance(getApplicationContext());
                                InventoryImagePathTable.updateIsAmazonUploaded(db_handle, inventoryImageId, Constants.True);

                            }
                            Log.e("IN the UploadSrvice", "uploaded sucessfully");

                            //aDDED BY  lokesh in the original code of uploading data

                            // this send message to the Hanlder present in The Democapture class that
                            // the image is uploaded successfully and make a toast for it
                            if (bundle != null) {
                                Messenger messenger = (Messenger) bundle.get("msg");
                                Message msg = Message.obtain();
                                Bundle bundle1 = new Bundle();
                                bundle1.putString("msg", "Image Uploaded Successfully");
                                msg.setData(bundle1);

                                try {
                                    messenger.send(msg);
                                } catch (RemoteException e) {
                                    Log.i("error", "error");
                                }

                            }

                            //ends the handler


                        }
                    }

                    @Override
                    public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

                    }

                    @Override
                    public void onError(int id, Exception ex) {
                        //dialog.dismiss();
                        if (bundle != null) {
                            Messenger messenger = (Messenger) bundle.get("msg");
                            Message msg = Message.obtain();
                            Bundle bundle1 = new Bundle();
                            bundle1.putString("msg", "Amazon Server Error!! Image not Uploaded");
                            msg.setData(bundle1);

                            try {
                                messenger.send(msg);
                            } catch (RemoteException e) {
                                Log.i("error", "error");
                            }

                        }
                        Log.e("error","error" + ex.getMessage());
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        // methods to get daress from the geocoder
        // all three methods return null if they cant fine th address
        public String getAddressLine(Context context) {
            Log.e("LoactionService","getAddrsLine");
            List<android.location.Address> addresses = getGeocoderAddress(context);

            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                String addressLine = address.getAddressLine(0);

                return addressLine;
            } else {
                return null;
            }
        }

        //get locality from geocoder
        public String getLocality(Context context) {
            Log.e("LoactionService","getLocality");
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

        //get Address from geocoder
        public List<Address> getGeocoderAddress(Context context) {
            Log.e("LoactionService","getGuiCode");
            if (location != null) {

                Geocoder geocoder = new Geocoder(context, Locale.ENGLISH);

                try {
                    /**
                     * Geocoder.getFromLocation - Returns an array of Addresses
                     * that are known to describe the area immediately surrounding the given latitude and longitude.
                     */
                    Log.e("in geoCoder",String.valueOf(lat)+" "+String.valueOf(lon));
                    List<Address> addresses = geocoder.getFromLocation(lat, lon, this.geocoderMaxResults);
                    return addresses;
                } catch (IOException e) {
                    e.printStackTrace();
//                Log.e(TAG, "Impossible to connect to Geocoder", e);
                }
            }

            return null;
        }


    }//AsyncTask ENDS


    // Method to update or insert the Lat lon in invetory image Path Table
    public void InsertLatLonInTable(){

        Log.e("IN LCCSERVICE","InsertlatLOnTable");

        // get the path as for image there is a unique path
        String path = imageUri.getPath();
        Log.e("path",path+"  "+lat+" "+lon);

        DataBaseHandler db_handle  = DataBaseHandler.getInstance(getBaseContext());
        SQLiteDatabase db = db_handle.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(InventoryImagePathTable.KEY_LATITUDE,Double.toString(lat));
        contentValues.put(InventoryImagePathTable.KEY_LONGITUDE,Double.toString(lon));

        // before thi lat lon column are null si update them
        db.update(InventoryImagePathTable.TABLE_NAME,contentValues,InventoryImagePathTable.KEY_LOCAL_IMAGE_PATH+ " =\'" + path + "\'",null);

        /*
        // this just for check that the data is inserted is suceessfully
        String GetQuery = "SELECT * FROM "+InventoryImagePathTable.TABLE_NAME+" WHERE "+InventoryImagePathTable.KEY_LOCAL_IMAGE_PATH
                + " = "+"\""+path+"\" ;";

        // db.rawQuery(Query,null);
        Cursor cursor = db.rawQuery(GetQuery,null);
        while (cursor.moveToNext()){
            String patthT = cursor.getString(cursor.getColumnIndexOrThrow(InventoryImagePathTable.KEY_LOCAL_IMAGE_PATH));
            String LAT = cursor.getString(cursor.getColumnIndexOrThrow(InventoryImagePathTable.KEY_LATITUDE));
            String LON = cursor.getString(cursor.getColumnIndexOrThrow(InventoryImagePathTable.KEY_LONGITUDE));

            Log.e("In HAndleClickSer",patthT+" "+LAT+ " "+LON+" ");
        }
        //check the table
        //  db_handle.printTotalRows(InventoryImagePathTable.TABLE_NAME);

            */

    }


}
