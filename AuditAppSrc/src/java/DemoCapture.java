package com.machadalo.audit;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.Time;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.ResponseHeaderOverrides;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.machadalo.audit.Services.FetchLocationUploadImage;
import com.machadalo.audit.extras.ConnectionDetector;
import com.machadalo.audit.sqllite.DataBaseHandler;
import com.machadalo.audit.sqllite.InventoryImagePathTable;
import com.machadalo.audit.sqllite.QuickImageCapturePathTable;
import com.machadalo.audit.sqllite.QuickImageCaptureTable;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DemoCapture extends AppCompatActivity {
    private Toolbar toolbar;

    Bundle locationData;
    //variable for drawer layout
    private DrawerLayout drawerLayout;
    TextView editResult,edit;

    //List of option the menu
    private ListView listView;
    private String[] menu;
    private TextView resultText;
    Bitmap alteredBitmap;
    ImageView imageView;
    //variables to get the extra intent data
    String Name;
    AlertDialog levelDialog;

    String email;
    String role;
    String ID;
    String SubmitStatus="";
    String strDate;
    String Location;
    String Address;
    String cords;
    TextView txtID;
    TextView txtLocation;
    TextView txtAddress, txtDateandTime;
    String mydate;
    Spinner spComment;

    String activityType;
    String activityDate;
    String comment;
    String shortlistedInventoryDetailsId;
    String myImagePath;
    String inventoryType;
    String inventoryImageId;
    String localImagePath;

    Button SubmitButton;

    DataBaseHandler db;
    //this a listener for the navigation drawer
    private ActionBarDrawerToggle drawerListener;

    //this variable indicates whether user is aware of drawer's existence
    private boolean userLearnedDrawer;

    //this variable indicates whether the drawer is started for the very first time or it is coming back from a rotation
    private boolean FromSavedInstanceState;

    //constant representing the shared preferences file
    public static final String PREF_FILE_NAME = "testpref";

    //contant representing the name of the preference
    public static final String KEY_USER_LEARNED_DRAWER = "user_learned_drawer";

    // this text view will hold the name of the user
    TextView textView;
    String result = null;

    int code;

    private static String logtag = "CameraApp3";
    // tells us which camera to take a picture from
    private static int TAKE_PICTURE = 1;
    // empty variable to hold our image Uri once we store it
    private Uri imageUri;

    private Uri compressimageUri;

    String  mode ;  //passed from the intent
    public int MODE = 0 ;
    public String QuickImageCaptureTable_ID;
    // Default Mode 0 -----> all data fetched from backened weather called from
    // assigned fragment or pending fragment or completed fargment
    // Mode 1  ------> called from the Capture Faragment with just society name


    String pic_name;
    String mapImage;

    Context context;
    InputStream is = null;

    String line = null;

    private static String lat;
    private static String lon;
    private static String address;

    int serverResponseCode = 0;
    ProgressDialog dialog = null;
    Button btnBarcode;
    String upLoadServerUri = null;
    TextView txtbarcode,txtadcounter;
    /**********
     * File Path
     *************/

    String imagePath="";
    String activityValue;
    String uploadFileName = "pic.jpg";
    String abc = "";
    String supplierName;

    String uploadFileNameMap = "picMap.png";
    String imageMapped;
    // flag for Internet connection status
    Boolean isInternetPresent = false;
    ConnectionDetector cd;

    TransferUtility transferUtility;
    AmazonS3Client s3Client;
    String bucket = "androidtokyo";
    Bundle bundle;


/*
    public HandleClickedImageService myService;
    public boolean isBound ;
    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.e("ServiceConnection","Connected");
            myLocalBinder binder = (myLocalBinder)iBinder;
            myService = binder.getService();
            isBound = true;
            myService.startS();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.e("ServiceConnection","Disconnected");
            isBound = false;
        }
    };

    */


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db =  DataBaseHandler.getInstance(this);
        setContentView(R.layout.activity_auditor_capture);
        imageView = (ImageView) findViewById(R.id.image_view_camera);

         bundle = getIntent().getExtras();

        activityType = bundle.getString("activityType");
        activityDate = bundle.getString("activityDate");
        shortlistedInventoryDetailsId = bundle.getString("shortlistedInventoryDetailsId");
        supplierName = bundle.getString("supplierName");
        inventoryType = bundle.getString("inventoryType");

        ID = bundle.getString("id");
        Address = bundle.getString("address");
        //Log.d("Address", Address);
        Location = bundle.getString("location");
       // Log.d("Location", Location);
        imagePath=bundle.getString("imagepath");
        activityValue=bundle.getString("actualactivity");
        // SubmitStatus=bundle.getString("submitStatus");
        strDate=bundle.getString("inventDate");

        address = bundle.getString("address");
        lat = bundle.getString("latitude");
        lon = bundle.getString("longitude");

        //Check the mode -----> here
        mode = bundle.getString("mode");
        QuickImageCaptureTable_ID = bundle.getString("ID");

        if(mode != null && mode.compareTo("CaptureMode")==0){
            MODE = 1;
        }

        // creating connection detector class instance
        cd = new ConnectionDetector(getApplicationContext());
        txtID = (TextView) findViewById(R.id.inventID);
        txtID.setText(ID);
        txtAddress = (TextView) findViewById(R.id.address);
        txtAddress.setText(Address);
        txtLocation = (TextView) findViewById(R.id.location);
        txtLocation.setText(Location);
        txtDateandTime = (TextView) findViewById(R.id.dateandtime);
        txtbarcode = (TextView) findViewById(R.id.brcodeResult);
        SubmitButton = (Button)findViewById(R.id.upload_button);

        String [] possibleComments =  new String[]{"Invetory OK", "Inventory has torn edges",  "Inventory has been stolen",  "Inventory is physically dismantled", "Inventory was fallen down"};

        Spinner spComment = (Spinner) findViewById(R.id.spComment);
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, possibleComments);
        spComment.setAdapter(adapter);

        if (activityValue.equals("AssignedAcitivty"))
        {
            View v = null;
            takePhoto(v);
        }
        else if (activityValue.equals("QueueFragment"))
        {

            Bitmap bm = BitmapFactory.decodeFile(imagePath);
            abc=imagePath;
            imageView.setImageBitmap(bm);

            SharedPreferences sharedPreferences=getSharedPreferences("Coordinates",Context.MODE_PRIVATE);
            lat= sharedPreferences.getString("Latitude","10");
            lon= sharedPreferences.getString("Longitude","10");
            String url = "http://maps.google.com/maps/api/staticmap?markers=color:red%7Clabel:S%7C11211&center=" + lat + "," + lon + "&zoom=17&maptype=hybrid&roadmap=" + lat + "," + lon + "&size=360x190&sensor=false";
            //new DownloadImageTask((ImageView) findViewById(R.id.image_view_map)).execute(url);

        }

//        GPSTracker gpsTracker = new GPSTracker(this);
//        if (gpsTracker.getIsGPSTrackingEnabled()) {
//            address = gpsTracker.getAddressLine(this) + ", " + gpsTracker.getLocality(this);
//            lat = String.valueOf(gpsTracker.getLatitude());
//            lon = String.valueOf(gpsTracker.getLongitude());
//

        //TODO commented out and added in the upload image after getting location

        /*
            SharedPreferences sharedPreferences = getSharedPreferences("Coordinates",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor=sharedPreferences.edit();
            String lat1 =  String.valueOf(lat);
            String lon1 = String.valueOf(lon);
            editor.putString("Latitude",lat1);
            editor.putString("Longitude",lon1);
            editor.commit();

            */


//
//        } else {
//            // can't get location
//            // GPS or Network is not enabled
//            // Ask user to enable GPS/network in settings
//            gpsTracker.showSettingsAlert();
//        }
        final IntentIntegrator integrator = new IntentIntegrator(this);
        btnBarcode = (Button) findViewById(R.id.buttonBarcode);
        btnBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //integrator.initiateScan();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("content://media/internal/images/media"));
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException eee){
                    try {
                        intent = new Intent(Intent.ACTION_VIEW);
                        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
                    } catch (Exception err){
                        Toast.makeText(getApplicationContext(), "This app not supported in your device", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        //Barcode Initialization


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        Button btnBarcodeOpen = (Button) findViewById(R.id.btnBarcode);
        btnBarcodeOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator scanIntegrator = new IntentIntegrator(DemoCapture.this);
                scanIntegrator.initiateScan();
            }
        });

        /*
        Intent Service = new Intent(DemoCapture.this, HandleClickedImageService.class);

        Handler handler1 = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                locationData = msg.getData();
                String lat1 = locationData.getString("lat");
                String lon1 = locationData.getString("lon");
                String add = locationData.getString("address");
                lat = lat1;
                lon = lon1;
                address = add;
                Log.e("In DemoCap","inside loaction data Handler "+lat1+" "+lon1+ " "+add);
            }
        };

        Service.putExtra("msg",new Messenger(handler1));
        startService(Service);
        */

    }

    //method used to get the option icon for the navigation drawer on the action bar
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        //drawerListener.syncState();
    }

    // method used to read and write from the preferences
    public static void saveToPrefrence(Context context, String prefrenceName, String preferenceValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(prefrenceName, preferenceValue);
        editor.apply();
    }

    public static String readFromPrefrence(Context context, String prefrenceName, String defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(prefrenceName, defaultValue);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    //method used to listen to the click of the items in the navigation drawer


    private void selectItemPosition(int position) {
        listView.setItemChecked(position, true);
    }

    public void upload(View v) {



        Log.e("In DemoCapture Upload","This method is called ");

        // take comment from drop down now
        Spinner spComment = (Spinner)findViewById(R.id.spComment);
        comment = (String)spComment.getSelectedItem();
        Log.d("got comment", comment);

        if(MODE == 1){

            Handler handler = new Handler(){
                @Override
                public void handleMessage(Message msg) {
                    Bundle reply = msg.getData();
                    String replyString = reply.getString("msg");
                    //Log.e("Inside the Handle cap",replyString);
                    Toast.makeText(getApplicationContext(),replyString,Toast.LENGTH_LONG).show();
                    sendMsgToUpdateUi();
                }
            };

            //todo this sould be an update
            //make data to insert in table
            ContentValues contentValues = new ContentValues();
            //contentValues.put(QuickImageCaptureTable.KEY_SUPPLIER_NAME,supplierName);
            contentValues.put(QuickImageCapturePathTable.KEY_IMAGE_DATE,Utils.getCurrentDateString());
            contentValues.put(QuickImageCapturePathTable.KEY_IMAGE_NAME,pic_name);
            contentValues.put(QuickImageCapturePathTable.KEY_LOCAL_PATH,imageUri.getPath());
            contentValues.put(QuickImageCapturePathTable.KEY_IS_AMAZON_UPLOADED,"false");
            contentValues.put(QuickImageCapturePathTable.KEY_QuickImageCaptureID,QuickImageCaptureTable_ID);

            DataBaseHandler db_handle = DataBaseHandler.getInstance(this);
            SQLiteDatabase db = db_handle.getWritableDatabase();

//            db.update(QuickImageCaptureTable.TABLE_NAME,contentValues,QuickImageCaptureTable.KEY_SUPPLIER_NAME + " = \'"+supplierName+"\' AND "+ QuickImageCaptureTable.KEY_LOCAL_PATH+ " = \'\'",null); //OR "+QuickImageCaptureTable.KEY_LOCAL_PATH +" is null"

            //data inserted
            db.insert(QuickImageCapturePathTable.TABLE_NAME,null,contentValues);

            //print table
            db_handle.printTotalRows(QuickImageCapturePathTable.TABLE_NAME);


            final Intent Service = new Intent(DemoCapture.this, FetchLocationUploadImage.class);
            Service.putExtra("msg",new Messenger(handler));
            Service.putExtra("supplierName",supplierName);
            Service.putExtra("mode",mode);

            Service.putExtra("imageUri",imageUri.toString());
            Service.putExtra("uploadFileName",uploadFileName);
            Service.putExtra("abc",abc);
            Service.putExtra("comment",comment);
            Service.putExtra("pic_name",pic_name);
            Service.putExtra("bucket", bucket);
            Service.putExtra("imagePath",imagePath);

            startService(Service);

        }
        else{

            Map<String, String> data = new HashMap<>();

            data.put(InventoryImagePathTable.KEY_INVENTORY_ACTIVITY_TYPE, activityType);
            data.put(InventoryImagePathTable.KEY_ACTIVITY_DATE, activityDate);
            data.put(InventoryImagePathTable.KEY_COMMENT, comment);
            data.put(InventoryImagePathTable.KEY_IMAGE_PATH, pic_name);
            data.put(InventoryImagePathTable.KEY_IS_AMAZON_UPLOADED, Constants.False);
            data.put(InventoryImagePathTable.KEY_IS_DJANGO_UPLOADED, Constants.False);
            data.put(InventoryImagePathTable.KEY_SHORTLISTED_INVENTORY_ID, shortlistedInventoryDetailsId);
            data.put(InventoryImagePathTable.KEY_LOCAL_IMAGE_PATH, imageUri.getPath());

            // make an entry into db for this image
            inventoryImageId = insertImageData(data);



            //added by lokesh

            final Intent Service = new Intent(DemoCapture.this, FetchLocationUploadImage.class);


            // Getting message from the service that uploads the image
            // make toast if image is uploaded or adress not available
            Handler handler = new Handler(){
                @Override
                public void handleMessage(Message msg) {
                    Bundle reply = msg.getData();
                    String replyString = reply.getString("msg");
                    Log.e("Inside the Handle cap",replyString);
                    Toast.makeText(getApplicationContext(),replyString,Toast.LENGTH_LONG).show();
                    sendMsgToUpdateUi();
                }
            };

            //put the msg handler in intent extra to send it to the service
            Service.putExtra("msg",new Messenger(handler));

            //add other required data to pass to service
            Service.putExtra("comment",comment);
            Service.putExtra("pic_name",pic_name);
            Service.putExtra("bucket", bucket);
            Service.putExtra("inventoryImageId",inventoryImageId);
            Service.putExtra("imagePath",imagePath);
            Service.putExtra("Address",Address);
            Service.putExtra("activityType",activityType);
            Service.putExtra("activityDate",activityDate);
            Service.putExtra("supplierName",supplierName);
            Service.putExtra("inventoryType",inventoryType);
            Service.putExtra("shortlistedInventoryDetailsId",shortlistedInventoryDetailsId);
            Service.putExtra("id",ID);
            Service.putExtra("imageUri",imageUri.toString());
            Service.putExtra("uploadFileName",uploadFileName);
            Service.putExtra("abc",abc);

            //start the service to get Lat lon and upload image
            startService(Service);




        }


        isInternetPresent = cd.isConnectingToInternet();

        /* previous version
        if (isInternetPresent) {
            dialog = ProgressDialog.show(DemoCapture.this, "", "Uploading file...", true);
            try {
                AWSUpload uploadObject = new AWSUpload();
                uploadObject.execute(new String[]{});

                WriteToInventoryActivityImage instance = new WriteToInventoryActivityImage(getApplicationContext());
                instance.execute(Constants.INVENTORY_ACTIVITY_IMAGE_URL, activityDate, activityType,  shortlistedInventoryDetailsId, comment,  pic_name, inventoryImageId);
            }
            catch(Exception e){
                Log.d("error",e.toString());
            }
        } else {
            SubmitStatus="Pending";
            Toast.makeText(getApplicationContext(),"No Internet Connection...Moving to Queued",Toast.LENGTH_LONG).show();
            Log.e("Statement", abc);
        }
        */

        // after submit button clicked change its color and text and
        // click set to false -> only once service is started
        SubmitButton.setBackgroundColor(Color.GRAY);
        SubmitButton.setEnabled(false);
        SubmitButton.setText("SUBMITTED");

    }// END Click function for Submit button


    //Insert Image data in InventoryImage Path Table
    public String  insertImageData(Map<String, String> data) {

        DataBaseHandler db_handle  = DataBaseHandler.getInstance(getBaseContext());
        String id = InventoryImagePathTable.insert(db_handle, data);
        Log.e("InsertingImage", data.get(InventoryImagePathTable.KEY_IMAGE_PATH));
        //print its rows after inserting data
       // db_handle.printTotalRows(InventoryImagePathTable.getTableName());
        return id;
    }

    //old upload asyntask now the service FetchLocation does it
    private class AWSUpload extends AsyncTask<String, Void, String> { // asynctask which runs on dif thread to parse json object from server
        @Override
        protected String doInBackground(String... params) {
            try {

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
                Log.d("PathinAwsUpload", imagePath);

                java.util.logging.Logger.getLogger("com.amazonaws.request").setLevel(java.util.logging.Level.FINEST);
                transferObserver.setTransferListener(new TransferListener() {
                    @Override
                    public void onStateChanged(int id, TransferState state) {
                        if(state == TransferState.COMPLETED){
                            dialog.dismiss();

                            // mark this entry as amazon uploaded
                            DataBaseHandler db_handle = DataBaseHandler.getInstance(getApplicationContext());
                            InventoryImagePathTable.updateIsAmazonUploaded(db_handle, inventoryImageId, Constants.True);
                            Toast.makeText(getApplicationContext(), "uploaded successfully" , Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

                    }

                    @Override
                    public void onError(int id, Exception ex) {
                        dialog.dismiss();
                        Log.e("error","error" + ex.getMessage());
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }


    // uploading is done in service now
    //code to set credentials to upload file
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

    public void insert() {
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

        nameValuePairs.add(new BasicNameValuePair("image_url", uploadFileName));
        nameValuePairs.add(new BasicNameValuePair("sub_by", Name));
        nameValuePairs.add(new BasicNameValuePair("sub_status", "Yes"));
        nameValuePairs.add(new BasicNameValuePair("sub_invent_id", ID));
        nameValuePairs.add(new BasicNameValuePair("coordinates", cords));
        nameValuePairs.add(new BasicNameValuePair("timsestamp", mydate));


        nameValuePairs.add(new BasicNameValuePair("sub_path", "http:/android.infiniteloopsinc.com/audit//media/submitted/"));


        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://coreapi-dev.ap-southeast-1.elasticbeanstalk.com/v0/android/audit/assigned_audits/");
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            String authToken = Constants.AUTH_TOKEN;
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            httppost.setHeader("Authorization", authToken);

            is = entity.getContent();
            Log.e("pass 1", "connection success ");
        } catch (Exception e) {
            Log.e("Fail 1", e.toString());
        }

        try {
            BufferedReader reader = new BufferedReader
                    (new InputStreamReader(is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            result = sb.toString();
            Log.e("pass 2", "connection success ");
        } catch (Exception e) {
            Log.e("Fail 2", e.toString());
        }

        try {
            JSONObject json_data = new JSONObject(result);
            //code = (json_data.getInt("message"));
            String strCode = (json_data.getString("message"));
            if (strCode.equals("Audit saved")) {
                Toast.makeText(getBaseContext(), "Inserted Successfully",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getBaseContext(), "Sorry, Try Again",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.e("Fail 3", e.toString());
        }
    }


    // set a new listener
    private View.OnClickListener cameraListener = new View.OnClickListener() {
        public void onClick(View v) {
            // open the camera and pass in the current view

            takePhoto(v);
        }
    };

    public void takePhoto(View v) {
        // tell the phone we want to use the camera
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");

        pic_name = makeFileName(supplierName, inventoryType,activityType);
        uploadFileName = pic_name;

        File path = new File(Environment.getExternalStorageDirectory()
                + File.separator + "machadalo_temp");
        path.mkdir();

        // create a new temp file called pic.jpg in the "pictures" storage area of the phone
        File photo = new File(Environment.getExternalStorageDirectory().toString()
                + File.separator + "machadalo_temp", pic_name);

        // take the return data and store it in the temp file "pic.jpg"
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
        // stor the temp photo uri so we can find it later
        imageUri = Uri.fromFile(photo);

        // start the camera
        startActivityForResult(intent, TAKE_PICTURE);
    }

    public String makeFileName(String supplierName, String inventoryName ,String activityType){
        supplierName = supplierName.replace(" ","");
        return supplierName + "_" + inventoryName + "_" + activityType+"_"+ Utils.getTimeStamp() + ".jpg";
    }

    // override the original activity result function

    // @Edited by lokesh
    // most of function of this class is moved in the service class which fetches image and uploads it
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        File fileString = new File(Environment.getExternalStorageDirectory().getPath(), "machadalo_temp");

        localImagePath = fileString.getAbsolutePath();
        Log.d("DemCaptLocImaPath", localImagePath);

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DATA, fileString.toString());
        values.put(MediaStore.Images.Media.MIME_TYPE,"image/jpeg");
        getApplicationContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        // call the parent

        //TODO called after the takephoto commenting out certain lines so nit to fecth map nd alterimage
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            // if the requestCode was equal to our camera code (1) then...
            case 1:
                // if the user took a photo and selected the photo to use
                if (resultCode == Activity.RESULT_OK) {
                    // get the image uri from earlier
                    Uri selectedImage = imageUri;
                    // notify any apps of any changes we make
                    getContentResolver().notifyChange(selectedImage, null);
                    // get the imageView we set in our view earlier

                    // create a content resolver object which will allow us to access the image file at the uri above
                    ContentResolver cr = getContentResolver();

                    //TODO Bitmap is initilized here and this bitmap is used to display in screen ahve to resolve this
                    // create an empty bitmap object
                    Bitmap bitmap;
                    try {
                        // get the bitmap from the image uri using the content resolver api to get the image

                        // bitmap= StringToBitMap(compressImage(selectedImage.toString())) ;
                        bitmap = MediaStore.Images.Media.getBitmap(cr, selectedImage);

                        //TODO added in uploadimage
                        /*
                        alteredBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
                        Canvas canvas = new Canvas(alteredBitmap);
                        Paint paint = new Paint();
                        canvas.drawBitmap(bitmap, 0, 0, paint);
                        paint.setColor(Color.WHITE);
                        paint.setTextSize(80);
                        */

                        // String text = GetAddress(lat,lon);

                            //TODO added in uploadimage
                       /*
                        cords = lat + "," + lon;



                        String url = "http://maps.google.com/maps/api/staticmap?markers=color:red%7Clabel:S%7C11211&center=" + lat + "," + lon + "&zoom=17&maptype=hybrid&roadmap=" + lat + "," + lon + "&size=360x190&sensor=false";
                        new DownloadImageTask((ImageView) findViewById(R.id.image_view_map)).execute(url);

                        */

                        mydate = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
                        //Log.d("AddressINonActFrres", Address);
                        /*
                        canvas.drawText(Address, 190, 250, paint);
                        canvas.drawText(mydate, 190, 350, paint);

                        */

                        //canvas.drawText("Oxford Chambers", 190, 370, paint);
                        // set the bitmap to the image view
                        /*Maps recreation to combine two image*/

                        txtDateandTime.setText("Address:" + Address + "\n Date :" + mydate + "\n");
                        /*
                        int nh = (int) ( alteredBitmap.getHeight() * (512.0 / alteredBitmap.getWidth()) );
                        Bitmap scaled = Bitmap.createScaledBitmap(alteredBitmap, 512, nh, true);
                        */
                        //made by lokesh Unedited
                        int nh = (int) (bitmap.getHeight() * (512.0 / bitmap.getWidth()) );
                        Bitmap scaledUnedited = Bitmap.createScaledBitmap(bitmap,512,nh,true);
                        imageView.setImageBitmap(scaledUnedited);

                        /*Out of memory Error*/

                        //End

                        //TODo added in uploadimage

                        /*
                       // imageView.setImageBitmap(alteredBitmap);
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
                              //      alteredBitmap, photo.getPath(), supplierName);
                            //
                        }  catch (OutOfMemoryError e) {
                            // notify the user
                            outOfMemoryException();
                            Toast.makeText(DemoCapture.this, "failed to load", Toast.LENGTH_LONG).show();
                            Log.e(logtag, e.toString());
                        }catch (FileNotFoundException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }



                        //Start:code added to show images in gallery
//                        sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory())));
                        MediaScannerConnection.scanFile(this, new String[] { photo.toString() }, null,
                                new MediaScannerConnection.OnScanCompletedListener() {
                                    public void onScanCompleted(String path, Uri uri) {
                                        Log.i("ExternalStorage", "Scanned " + path + ":");
                                        Log.i("ExternalStorage", "-> uri=" + uri);
                                    }
                                });
//                        MediaScannerConnection.scanFile(context, new String[]{path.getAbsolutePath()}, null, null);

                        */
                        // notify the user
                        //Toast.makeText(DemoCapture.this, selectedImage.toString(), Toast.LENGTH_LONG).show();
                    } catch (OutOfMemoryError e) {
                        // notify the user
                        outOfMemoryException();
                        Toast.makeText(DemoCapture.this, "failed to load", Toast.LENGTH_LONG).show();
                        Log.e(logtag, e.toString());
                    } catch (Exception e) {
                        // notify the user
                        Toast.makeText(DemoCapture.this, "failed to load", Toast.LENGTH_LONG).show();
                        Log.e(logtag, e.toString());
                    }
                }
                default:
                    IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
                    if (scanResult != null) {

                        if (scanResult.getContents().equals(ID)) {
                            Toast.makeText(getApplicationContext(), "Code Scanned Successfully", Toast.LENGTH_SHORT).show();
                            Toast.makeText(getApplicationContext(), "Code Matches with Ad inventory id", Toast.LENGTH_SHORT).show();
                            txtbarcode.setText("Barcode Result SUccessfull: " + scanResult.getContents());
                            txtbarcode.setTextColor(getResources().getColor(R.color.green));
                        } else {
                            Toast.makeText(getApplicationContext(), "Does not match", Toast.LENGTH_SHORT).show();
                            txtbarcode.setText("Scan Again");

                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Barcode does not exist: " + scanResult, Toast.LENGTH_SHORT).show();

                    }

        }

    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "DemoCapture Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.machadalo.audit/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "DemoCapture Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.machadalo.audit/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
                                    /*Maps image is coming from here*/
            bmImage.buildDrawingCache();

        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {

            uploadFileNameMap = BitMapToString(result);
            bmImage.setImageBitmap(result);

        }
    }

    public String BitMapToString(Bitmap bitmap) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }
    public void outOfMemoryException() {

        new AlertDialog.Builder(DemoCapture.this)
                .setMessage("Due to some error app needs to be restart.By tapping 'Restart' app will restart automatically")
                .setPositiveButton("Restart", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent i = getBaseContext().getPackageManager()
                                .getLaunchIntentForPackage( getBaseContext().getPackageName() );
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);

                    }
                }).show();
    }

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
            outOfMemoryException();
        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight,Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
            outOfMemoryException();
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


    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
    String picturename;
    int PHOTO_SELECTED = 0;

    public void uploadAws(String file_path,String file_name){
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image");
            //startActivityForResult(intent, PHOTO_SELECTED);
            String MY_ACCESS_KEY_ID = "AKIAIIGRT3EJEDSRVSFQ";
            String MY_SECRET_KEY = "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";
            String MY_PICTURE_BUCKET = "mdimages-test";
            AmazonS3Client s3Client = new AmazonS3Client(new BasicAWSCredentials(MY_ACCESS_KEY_ID, MY_SECRET_KEY));
            //s3Client.createBucket(MY_PICTURE_BUCKET);
            s3Client.setRegion(Region.getRegion(Regions.AP_SOUTHEAST_1));


            PutObjectRequest por = new PutObjectRequest(MY_PICTURE_BUCKET, file_name, new java.io.File(file_path));
            Log.e("Upload file path", file_path + "/" + file_name);

            s3Client.putObject(por);

            ResponseHeaderOverrides override = new ResponseHeaderOverrides();
            override.setContentType("image/*");

            GeneratePresignedUrlRequest urlRequest = new GeneratePresignedUrlRequest(MY_PICTURE_BUCKET, file_name);
            urlRequest.setExpiration(new Date(System.currentTimeMillis() + 3600000));  // Added an hour's worth of milliseconds to the current time.
            urlRequest.setResponseHeaders(override);

            URL url = s3Client.generatePresignedUrl(urlRequest);
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.valueOf(url)));
            startActivity(browserIntent);
        }
        catch(Exception e){
            Log.e("Upload file path", file_path + "/" + file_name);
        }
    }


    public void openEditDialog(View view){
        // Strings to Show In Dialog with Radio Buttons
        final CharSequence[] items = {" Inventory has torn edges "," Inventory has been stolen "," Inventory is physically dismantled "," Inventory was fallen down "};

        // Creating and Building the Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select The Difficulty Level");
        builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {


                switch(item)
                {
                    case 0:
                        // Your code when first option seletced
                        editResult.setText(items[item].toString());
                        break;
                    case 1:
                        // Your code when 2nd  option seletced
                        editResult.setText(items[item].toString());

                        break;
                    case 2:
                        // Your code when 3rd option seletced
                        editResult.setText(items[item].toString());

                        break;
                    case 3:
                        // Your code when 4th  option seletced
                        editResult.setText(items[item].toString());

                        break;

                }
                levelDialog.dismiss();
            }
        });
        levelDialog = builder.create();
        levelDialog.show();

    }
    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {}
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        }
        else if(dir!= null && dir.isFile())
            return dir.delete();
        else {
            return false;
        }
    }

    @Override
    protected void onDestroy() {
        Drawable drawable = imageView.getDrawable();
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            Bitmap bitmap = bitmapDrawable.getBitmap();
            bitmap.recycle();
            bitmap = null;

        }
        deleteCache(getApplicationContext());
        Log.e("Clearing cache","True");
        super.onDestroy();
    }


//    public void createAmazonS3Client(CognitoCachingCredentialsProvider credentialsProvider){
//
//        // Create an S3 client
//        s3Client = new AmazonS3Client(credentialsProvider);
//
//        // Set the region of your S3 bucket
//        s3Client.setRegion(Region.getRegion(Regions.AP_NORTHEAST_1));
//    }
//
//    public void setTransferUtility(){
//
//        transferUtility = new TransferUtility(s3Client,
//                getApplicationContext());
//    }
//
//    public void s3credentialsProvider(){
//
//        // Initialize the AWS Credential
//        CognitoCachingCredentialsProvider cognitoCachingCredentialsProvider =
//                new CognitoCachingCredentialsProvider(
//                        getApplicationContext(),
//                        "ap-northeast-1:900e0d1f-df62-4fa8-a948-4eb018a361b5", // Identity Pool ID
//                        Regions.AP_NORTHEAST_1 // Region
//                );
//        createAmazonS3Client(cognitoCachingCredentialsProvider);
//    }



//    public void uploadFileToS3(){
//        File file = new File(Environment.getExternalStorageDirectory().toString()
//                + File.separator + "machadalo_temp", supplierName);
//
//        TransferObserver transferObserver = transferUtility.upload(
//                bucket,          /* The bucket to upload to */
//                "Screenshot.png",/* The key for the uploaded object */
//                file               /* The file where the data to upload exists */
//        );
//
//        transferObserverListener(transferObserver);
//    }

//    public void transferObserverListener(TransferObserver transferObserver){
//
//        Log.d("Listener", transferObserver.toString());
//        Log.d("Listner", transferObserver.getBucket().toString());
//        transferObserver.setTransferListener(new TransferListener(){
//
//            @Override
//            public void onStateChanged(int id, TransferState state) {
//                if(TransferState.COMPLETED == state){
//                    dialog.dismiss();
//                    Toast.makeText(getApplicationContext(), "uploaded successfully" ,
//                            Toast.LENGTH_SHORT).show();
//                }
//
//            }
//
//            @Override
//            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
//                int percentage = (int) (bytesCurrent/bytesTotal * 100);
//                Toast.makeText(getApplicationContext(), "Progress in %" + percentage,
//                        Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onError(int id, Exception ex) {
//
//                Log.e("error","error", ex.getMessage());
//            }
//
//        });
//    }


    /*
     this function send msg to the ---> quickImageCapture acitivity that
     image hs been uploaded
     called from the handler in this activity
     */

    public void sendMsgToUpdateUi(){

        if (bundle != null) {

            Messenger messenger = (Messenger) bundle.get("msg");

            if(messenger !=null) {

                Message msg = Message.obtain();
                Bundle bundle1 = new Bundle();
                bundle1.putString("msg", "Image Uploaded Successfully");
                bundle1.putString("Uploaded", "true");
                msg.setData(bundle1);

                try {
                    messenger.send(msg);
                } catch (RemoteException e) {
                    Log.e("error", "error");
                }

            }

        }
        else
        {
            Log.e("DCapt sendMsg","bundle NULL");
        }
    }

}
