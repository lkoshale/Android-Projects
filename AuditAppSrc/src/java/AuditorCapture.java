package com.machadalo.audit;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.Time;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.machadalo.audit.extras.ConnectionDetector;
import com.machadalo.audit.sqllite.AuditGS;
import com.machadalo.audit.sqllite.DataBaseHandler;

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
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class AuditorCapture extends AppCompatActivity {
    private Toolbar toolbar;

    //variable for drawer layout
    private DrawerLayout drawerLayout;

    //List of option the menu
    private ListView listView;
    private String[] menu;
    Bitmap alteredBitmap;
    private static final int IO_BUFFER_SIZE = 4 * 1024;
    ImageView imageView;
    //variables to get the extra intent data
    String Name;
    String email;
    String role;
    String ID;
    String SubmitStatus="";
    String Date;
    String Location;
    String Address;
    String cords;
    TextView txtID;
    TextView txtLocation;
    TextView txtAddress, txtDateandTime;
    String mydate;
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
    final String uploadFilePath = String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES));


    String imagePath="";
    String activityValue;
    String uploadFileName = "pic.jpg";
    String abc = "";

    String uploadFileNameMap = "picMap.png";
    String imageMapped;
    // flag for Internet connection status
    Boolean isInternetPresent = false;
    ConnectionDetector cd;

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
        Bundle name = getIntent().getExtras();
        ID = name.getString("id");
        Address = name.getString("address");
        Location = name.getString("location");
        imagePath=name.getString("imagepath");
        activityValue=name.getString("actualactivity");
        // SubmitStatus=name.getString("submitStatus");
        Date=name.getString("inventDate");
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
        //edit =  (TextView) findViewById(R.id.edit);

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
            new DownloadImageTask((ImageView) findViewById(R.id.image_view_map)).execute(url);

        }


        GPSTracker gpsTracker = new GPSTracker(this);
        if (gpsTracker.getIsGPSTrackingEnabled()) {
            address = gpsTracker.getAddressLine(this) + ", " + gpsTracker.getLocality(this);
            lat = String.valueOf(gpsTracker.getLatitude());
            lon = String.valueOf(gpsTracker.getLongitude());

            SharedPreferences sharedPreferences = getSharedPreferences("Coordinates",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.putString("Latitude",lat);
            editor.putString("Longitude",lon);
            editor.commit();


        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gpsTracker.showSettingsAlert();
        }
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
                IntentIntegrator scanIntegrator = new IntentIntegrator(AuditorCapture.this);
                scanIntegrator.initiateScan();
            }
        });
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
        isInternetPresent = cd.isConnectingToInternet();
        if (isInternetPresent) {
            upLoadServerUri = "http://machadalo.com/android/audit/media/submittedfile.php";
            dialog = ProgressDialog.show(AuditorCapture.this, "", "Uploading file...", true);
            new Thread(new Runnable() {
                public void run() {
                    runOnUiThread(new Runnable() {
                        public void run() {

                            Toast.makeText(getApplicationContext(),
                                    "uploading started.....", Toast.LENGTH_LONG).show();
                        }
                    });

                    uploadFile( abc);
                    SubmitStatus="Uploaded";
                    db.addUpload(new AuditGS(ID, Address, Location, abc, SubmitStatus, Date));

                    insert();

                }
            }).start();
        } else {
            // Internet connection is not present
            // Ask user to connect to Internet
           /* showAlertDialog(AuditorCapture.this, "No Internet Connection",
                    "You don't have internet connection.", false);*/

            SubmitStatus="Pending";
            Toast.makeText(getApplicationContext(),"No Internet Connection...Moving to Queued",Toast.LENGTH_LONG).show();
            db.addUpload(new AuditGS(ID, Address,Location,abc,SubmitStatus,Date));
            Log.e("Statement", abc);


            //  db.update_byID(x, abc);
        }
    }


    public int uploadFile(String sourceFileUri) {


        String fileName = sourceFileUri;

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(sourceFileUri);

        if (!sourceFile.isFile()) {

            dialog.dismiss();

            Log.e("uploadFile", "Source File not exist :"
                    + abc);

            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(getApplicationContext(),
                            "Source File not exist :", Toast.LENGTH_LONG).show();
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(AuditorCapture.this);

                    // Setting Dialog Title
                    alertDialog.setTitle("File not found");

                    // Setting Dialog Message
                    alertDialog.setMessage("Do you want to capture again?");

                    // Setting Icon to Dialog
                    alertDialog.setIcon(R.drawable.delete);

                    // Setting Positive "Yes" Button
                    alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            View v = null;
                            // Write your code here to invoke YES event
                            //Toast.makeText(getApplicationContext(), "You clicked on YES", Toast.LENGTH_SHORT).show();
                            takePhoto(v);
                        }
                    });

                    // Setting Negative "NO" Button
                    alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Write your code here to invoke NO event
                            //Toast.makeText(getApplicationContext(), "You clicked on NO", Toast.LENGTH_SHORT).show();
                            dialog.cancel();
                        }
                    });

                    // Showing Alert Message
                    alertDialog.show();

                }
            });

            return 0;

        } else {
            try {

                // open a URL connection to the Servlet Image
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(upLoadServerUri);



                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", fileName);

                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\"" + fileName + "\"" + lineEnd);

                dos.writeBytes(lineEnd);

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.i("uploadFile", "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode);

                if (serverResponseCode == 200){

                    runOnUiThread(new Runnable() {
                        public void run() {

                            String msg = "File Upload Completed.\n\n See uploaded file here : \n\n"
                                    + " http:/android.infiniteloopsinc.com/audit//media/submitted/"
                                    + uploadFileName;

                            Log.w("Maps", uploadFileNameMap);
                            /*Toast.makeText(getApplicationContext(),
                                    msg, Toast.LENGTH_LONG).show();*/
                            /*Toast.makeText(AuditorCapture.this, "File Upload Complete.",
                                    Toast.LENGTH_SHORT).show();*/
                            final AlertDialog alertDialog = new AlertDialog.Builder(
                                    AuditorCapture.this).create();

                            // Setting Dialog Title
                            alertDialog.setTitle("Successful");

                            // Setting Dialog Message
                            alertDialog.setMessage("file uploaded successfully");

                            // Setting Icon to Dialog
                            alertDialog.setIcon(R.drawable.tick);

                            // Setting OK Button
                            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Write your code here to execute after dialog closed
                                    Intent intent = new Intent(AuditorCapture.this , AssignedActivity.class);
                                    intent.putExtra("getIntentAct", false);
                                    intent.putExtra("updateAssign", true);

                                    startActivity(intent);
                                    alertDialog.hide();
                                }
                            });

                            // Showing Alert Message
                            alertDialog.show();
                        }
                    });
                }

                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {

                dialog.dismiss();
                ex.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        Log.e("MalformedURLException:", "here");
                    }
                });

                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {

                dialog.dismiss();
                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                    Log.e("Exception","Excetion Occured");
                    }
                });

            }
            dialog.dismiss();
            return serverResponseCode;

        } // End else block
    }


    public void insert() {
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

        nameValuePairs.add(new BasicNameValuePair("sub_pic", uploadFileName));
        nameValuePairs.add(new BasicNameValuePair("sub_by", Name));
        nameValuePairs.add(new BasicNameValuePair("sub_status", "Yes"));
        nameValuePairs.add(new BasicNameValuePair("sub_invent_id", ID));
        nameValuePairs.add(new BasicNameValuePair("coordinates", cords));
        nameValuePairs.add(new BasicNameValuePair("timestamp", mydate));


        nameValuePairs.add(new BasicNameValuePair("sub_path", "http:/android.infiniteloopsinc.com/audit//media/submitted/"));


        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://android.infiniteloopsinc.com/audit/media/submit.php");
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();
            Log.e("pass 1", "connection success ");
        } catch (Exception e) {
            Log.e("Fail 1", e.toString());
            Toast.makeText(getApplicationContext(), "Invalid IP Address",
                    Toast.LENGTH_LONG).show();
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
            code = (json_data.getInt("code"));

            if (code == 1) {
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
        Calendar c = Calendar.getInstance();
        Log.e("Date", String.valueOf(c.getTime()));
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c.getTime());
        pic_name = "abc"+"-"+ID+"(Photo).jpg";
        uploadFileName = pic_name;

//        File path = new File(Environment.getExternalStorageDirectory(), "machadalo_temp");
        File path = new File(Environment.getExternalStorageDirectory()
                + File.separator + "machadalo_temp");
        path.mkdirs();

        // create a new temp file called pic.jpg in the "pictures" storage area of the phone
        File photo = new File(path, pic_name);

        // take the return data and store it in the temp file "pic.jpg"
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
        // stor the temp photo uri so we can find it later
        imageUri = Uri.fromFile(photo);

        // start the camera
        startActivityForResult(intent, TAKE_PICTURE);





    }

    // override the original activity result function
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Barcode Result:
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (!prefs.getBoolean("barcodeActResult", false)) {
            // <---- run your one time code here

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

            // mark first time has runned.
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("barcodeActResult", true);
            editor.commit();
        }
        // call the parent
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
                    // create an empty bitmap object
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
                        cords = lat + "," + lon;



                        String url = "http://maps.google.com/maps/api/staticmap?markers=color:red%7Clabel:S%7C11211&center=" + lat + "," + lon + "&zoom=17&maptype=hybrid&roadmap=" + lat + "," + lon + "&size=360x190&sensor=false";
                        new DownloadImageTask((ImageView) findViewById(R.id.image_view_map)).execute(url);


                        mydate = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
                        canvas.drawText(address, 190, 250, paint);
                        canvas.drawText(mydate, 190, 350, paint);
                        canvas.drawText("Oxford Chambers", 190, 370, paint);
                        // set the bitmap to the image view
                        /*Maps recreation to combine two image*/

                        txtDateandTime.setText("Address:" + address + "\n Date :" + mydate + "\n");

                        try {
                            OutputStream stream = new FileOutputStream(uploadFilePath + "/" + uploadFileName);
                            alteredBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

                            stream.close();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {

                        }

                        int nh = (int) ( alteredBitmap.getHeight() * (512.0 / alteredBitmap.getWidth()) );
                        Bitmap scaled = Bitmap.createScaledBitmap(alteredBitmap, 512, nh, true);
                        imageView.setImageBitmap(scaled);



                       /* imageView.setImageBitmap(alteredBitmap);*/
                        compressimageUri= getImageUri(this,alteredBitmap);
                        compressImage(compressimageUri.toString());
                        File photo = new File(Environment.getExternalStorageDirectory().toString()
                                + File.separator + "machadalo", pic_name);
                        FileOutputStream fos = null;
                        try {
                            fos = new FileOutputStream(photo);
                            alteredBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                            fos.flush();
                            fos.close();
                           /* MediaStore.Images.Media.insertImage(context.getContentResolver(),
                                    alteredBitmap, photo.getPath(), supplierName);
                            */
                        } catch (FileNotFoundException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        // notify the user
                        Toast.makeText(AuditorCapture.this, selectedImage.toString(), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        // notify the user
                        Toast.makeText(AuditorCapture.this, "failed to load", Toast.LENGTH_LONG).show();
                        Log.e(logtag, e.toString());
                    }
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
                "AuditorCapture Page", // TODO: Define a title for the content shown.
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
                "AuditorCapture Page", // TODO: Define a title for the content shown.
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

   /* @Override
    protected void onPause() {
        super.onPause();
        File file = new File(uploadFilePath + "/" + uploadFileName);

        boolean deleted = file.delete();
    }*/

    public void showAlertDialog(Context context, String title, String message, Boolean status) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        // Setting Dialog Title
        alertDialog.setTitle(title);

        // Setting Dialog Message
        alertDialog.setMessage(message);

        // Setting alert dialog icon
       /* alertDialog.setIcon((status) ? R.drawable.material_drawer_circle_mask : R.drawable.material_drawer_circle_mask);
*/
        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS);
                ComponentName cName = new ComponentName("com.android.phone", "com.android.phone.Settings");
                intent.setComponent(cName);
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }


    public String compressImage(String imageUri) {

        String filePath = getRealPathFromURI(imageUri);
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

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight,Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
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


        File file = new File(Environment.getExternalStorageDirectory().getPath(), "machadalo");

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

        String uriSting = (file.getAbsolutePath() + "/" + mainPic);





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
        deleteCache(getApplicationContext());
        super.onDestroy();
    }
}
