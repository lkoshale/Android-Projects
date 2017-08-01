package com.machadalo.audit;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TaskStackBuilder;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.machadalo.audit.Services.BulkUploadImagesAmazon;
import com.machadalo.audit.Services.BulkUploadImagesDjango;
import com.machadalo.audit.extras.ConnectionDetector;
import com.machadalo.audit.extras.TabFragment;
import com.machadalo.audit.fragments.AssignedFragment;
import com.machadalo.audit.fragments.QueueFragment;
import com.machadalo.audit.sqllite.AuditGS;
import com.machadalo.audit.sqllite.CoordinateAddressMapTable;
import com.machadalo.audit.sqllite.DataBaseHandler;
import com.machadalo.audit.sqllite.InventoryImagePathTable;
import com.machadalo.audit.sqllite.PendingTable;
import com.machadalo.audit.sqllite.ProposalTable;
import com.machadalo.audit.sqllite.QuickImageCapturePathTable;
import com.machadalo.audit.sqllite.QuickImageCaptureTable;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Queue;

import static com.machadalo.audit.extras.TabFragment.viewPager;

public class auditorDashboard extends AppCompatActivity{
    LocationManager l1;
    DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;
    FragmentManager mFragmentManager;
    FragmentTransaction mFragmentTransaction;
    ConnectionDetector cd;
    String Name;
    String MobileNum;
    String  UserName;
    Intent igallery;
    ProgressDialog dialog = null;
    String email;
    private Toolbar toolbar;
    String role;
    private String url = "http://android.infiniteloopsinc.com/audit/media/navcounter.php";
    private String awsURL = "http://coreapi-dev.ap-southeast-1.elasticbeanstalk.com/v0/android/audit/assigned_audits/";

    private String assignedFragmentName = "AssignedFragment";

    // flag for Internet connection status
    Boolean isInternetPresent = true;
    Boolean isGPSEnabled = false;
    ArrayList<String> ad_inventory_id_array= new ArrayList<String>();
    ArrayList<String> socciety_Address_array= new ArrayList<String>();
    ArrayList<String> inventory_address_array= new ArrayList<String>();
    ArrayList<String> map_address_array= new ArrayList<String>();
    ArrayList<String> image_address_array= new ArrayList<String>();
    ArrayList<String> date_address_array= new ArrayList<String>();
    ArrayList<String> submit_address_array= new ArrayList<String>();
    ArrayList<String> ad_inventory_type_array= new ArrayList<String>();
    ArrayList<String> society_name_array= new ArrayList<String>();
    ArrayList<String> ad_type_array= new ArrayList<String>();

    private String jsonResult;

    DataBaseHandler db;

    //for sending mail
    String mproposalText;

    NotificationCompat.Builder mBuilder;
    int notificationID;
    boolean isGPSTrackingEnabled = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // cretae this table if not already created

        DataBaseHandler db_handle = DataBaseHandler.getInstance(getApplicationContext());
        SQLiteDatabase db = db_handle.getWritableDatabase();


        //db.execSQL("DROP TABLE IF EXISTS " + InventoryImagePathTable.getTableName());
        db.execSQL(InventoryImagePathTable.getCreateTableCommand());

        //creating address lat lon map table
        db.execSQL(CoordinateAddressMapTable.getCreateTableCommand());

        //create the pending Table for first time
        db.execSQL(PendingTable.getCreateTableCommand());

        //create QuickCaptureImage Table
        db.execSQL(QuickImageCaptureTable.getCreateTableCommand());

        //create QuickImagePathTable
        db.execSQL(QuickImageCapturePathTable.getCreateTableCommand());


        Log.d("ImagePath table created", "Image path table created");

        try {
            l1 = (LocationManager) this.getSystemService(LOCATION_SERVICE);
            isGPSTrackingEnabled = l1.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if(!isGPSTrackingEnabled){
                showSettingsAlert();
            }
        }
        catch(Exception e){
            Toast.makeText(getApplicationContext(),"Gps Handling Error",Toast.LENGTH_LONG).show();
        }

        //get reference to the text view holding the name of the user in the navigation drawer

        cd = new ConnectionDetector(getApplicationContext());

        //only fetch and insert if connection present
        if (cd.isConnectingToInternet()){
            // fetch and insert data in proposal table
            ProposalTable proposalTable = new ProposalTable(this);
            proposalTable.insertDataFromInternet();

        }


        if ( cd.isConnectingToInternet() ) {

        }
        accessWebService();
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("MyAuditor");
        /*Database Check*/
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("firstTime_One", false);
        editor.putBoolean("firstTime_Two", false);
        editor.putBoolean("firstTime_Three", false);
        editor.putBoolean("firstTime_Four", false);

        editor.commit();

        //get data from previous activity
        Bundle name = getIntent().getExtras();

        if ( name != null) {
            try {

                Name = name.getString("name");
                email = name.getString("email");
                role = name.getString("role");
            } catch (Exception e) {
                Log.e("Bundle", e.toString());

            }
        }

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mNavigationView = (NavigationView) findViewById(R.id.shitstuff) ;

        /**
         * Lets inflate the very first fragment
         * Here , we are inflating the TabFragment as the first Fragment
         */

        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.replace(R.id.containerView, new TabFragment(), "TabFragment").commit();

        /**
         * Setup click events on the Navigation View Items.
         */

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                mDrawerLayout.closeDrawers();

                Intent i1;

                switch (menuItem.getItemId()) {

                    case R.id.nav_item_profile:

                        Intent profileIntent = new Intent(auditorDashboard.this, profile.class);
                        profileIntent.putExtra("name", Name);
                        profileIntent.putExtra("email", email);
                        profileIntent.putExtra("role", role);
                        startActivity(profileIntent);
                        break;

                    case R.id.nav_item_gallery:

                        final int RESULT_GALLERY = 0;
                        Intent galleryIntent = new Intent(
                                Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivity(galleryIntent);
                        break;

                   /* case R.id.nav_item_archive:

                        i1 = new Intent(auditorDashboard.this, profile.class);
                        i1.putExtra("name", Name);
                        i1.putExtra("email", email);
                        i1.putExtra("role", role);
                        startActivity(i1);
                        break;*/

                    case R.id.nav_item_aboutus:

                        i1 = new Intent(auditorDashboard.this, AboutUs.class);
                        i1.putExtra("name", Name);
                        i1.putExtra("email", email);
                        i1.putExtra("role", role);
                        startActivity(i1);
                        break;

                    case R.id.nav_item_logout:

                        String[] prefvalue = {"null", "null", "null", "false"};
                        String[] prefname = {"name", "email", "role", "status"};
                        manageScreens.saveToPrefrence(auditorDashboard.this, prefname, prefvalue);
                        Intent logout = new Intent(auditorDashboard.this, LoginActivity.class);
                        startActivity(logout);
                        break;

                    case R.id.syncQueuedImages:
                        // purpose is to bulk upload images in background which were not uploaded
                        ConnectionDetector connectionDetector = new ConnectionDetector(getApplicationContext());
                        if ( !connectionDetector.isConnectingToInternet() ) {
                            Toast.makeText(getApplicationContext(), Messages.NO_INTERNET_MESSAGE, Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(getApplicationContext(), Messages.SYNCING_QUEUED_IMAGES, Toast.LENGTH_SHORT).show();
                            Log.d("SyncingQuedImages", "Syncing");
                            Intent bulkUploadImageIntent = new Intent(auditorDashboard.this, BulkUploadImagesDjango.class);
                            auditorDashboard.this.startService(bulkUploadImageIntent);

                            Intent intent = new Intent(auditorDashboard.this, BulkUploadImagesAmazon.class);
                            auditorDashboard.this.startService(intent);
                        }

                        break;

                    case R.id.sendMailOfProposal:
                        //for sending mail
                        AlertDialog.Builder builder = new AlertDialog.Builder(auditorDashboard.this);
                        builder.setTitle("Enter Proposal Name : ");
                        final EditText input = new EditText(auditorDashboard.this);
                        input.setInputType(InputType.TYPE_CLASS_TEXT);
                        builder.setView(input);

                        builder.setPositiveButton("OK",new DialogInterface.OnClickListener(){

                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mproposalText = input.getText().toString();

                                if (mproposalText.compareTo("")!=0){

                                    Intent emailIntent = new Intent(Intent.ACTION_SEND);
                                    emailIntent.setType("text/plain");
                                    emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {"anupam@machadalo.com","nikhil.singh@machadalo.com"});
                                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Audit Images For "+mproposalText+" attached ");
                                    emailIntent.putExtra(Intent.EXTRA_TEXT, "Compressed zip files of Images for the Porposal : "+mproposalText);

                                    File file = Utils.makeZipfile(mproposalText,auditorDashboard.this);

                                    if (file == null || !file.exists() || !file.canRead()) {
                                        Log.e("Mail sending ","FILE Error");
                                        return;
                                    }

                                    Uri uri = Uri.fromFile(file);
                                    emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
                                    startActivity(emailIntent);
                                    //Intent.createChooser(emailIntent, "Pick an Email provider")

                                }

                            }
                        });

                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                        builder.show();

                        break;

                    case R.id.CompareImages :

                        Intent intent = new Intent(auditorDashboard.this,ImageCompareActivity.class);
                        startActivity(intent);

                    default:
                        return false;
                }
                return false;
            }

        });

        /**
         * Setup Drawer Toggle of the Toolbar
         */

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout, toolbar,R.string.app_name,
                R.string.app_name);

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mDrawerToggle.syncState();

    }
    //  * Function to show settings alert dialog

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        //Setting Dialog Title
        alertDialog.setTitle("GPS is OFF");

        //Setting Dialog Message
        alertDialog.setMessage("Please turn on GPS");

        //On Pressing Setting button
        alertDialog.setPositiveButton(R.string.action_settings, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });

        //On pressing cancel button
        alertDialog.setNegativeButton("cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    private class JsonReadTask extends AsyncTask<String, Void, String> { // asynctask which runs on dif thread to parse json object from server
        @Override
        protected String doInBackground(String... params) {

            HttpClient httpclient = new DefaultHttpClient();
            String authToken = Constants.AUTH_TOKEN;
            HttpGet httpget = new HttpGet(params[0]); // params[0] contains url string
            httpget.setHeader("Authorization", authToken);

            try {
                HttpResponse response = httpclient.execute(httpget);
                jsonResult = inputStreamToString(
                        response.getEntity().getContent()).toString();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        private StringBuilder inputStreamToString(InputStream is) {
            String rLine = "";
            StringBuilder answer = new StringBuilder();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));

            try {
                while ((rLine = rd.readLine()) != null) {
                    answer.append(rLine);
                }
            } catch (IOException e) {
                e.printStackTrace();
                }
            return answer;
        }

        @Override
        protected void onPostExecute(String result) {
            galleryDataFromServer();
        }
    }// end async task
    public void accessWebService() { // helper function to run jason task

        JsonReadTask task = new JsonReadTask();
        // passes values for the urls string array
        task.execute(new String[]{url});

        GetProfileData data = new GetProfileData();
        data.execute();

    }

    // build hash set for list view
    public void galleryDataFromServer() {

        try {

            // get Internet status
            isInternetPresent = cd.isConnectingToInternet();

            // check for Internet status
            if (isInternetPresent) {
                // Internet Connection is Present
                // make HTTP requests
                if (jsonResult == null) {
                    Toast.makeText(getApplicationContext(), "No response from server due to internet",
                            Toast.LENGTH_SHORT).show();
                } else {
                    try{


                        JSONObject jsonResponse = new JSONObject(jsonResult);
                        JSONArray jsonMainResult = jsonResponse.optJSONArray("assign");// banners is our datable name in mysql database
                        //  total = jsonMainResult.length();
                        if(jsonMainResult== null)
                        {
                            Toast.makeText(getApplicationContext(), "There is no assign inventory!",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else {
                            int i;
                            for (i = 0; i < jsonMainResult.length(); i++) {
                                JSONObject jsonChildNode = jsonMainResult.getJSONObject(i);
                                JSONObject jsonType = jsonChildNode.optJSONObject("type");
                                JSONObject jsonSociety = jsonChildNode.optJSONObject("society");
                                String societyImage = jsonSociety.optString("society_image");
                                String maplongitute = jsonSociety.optString("society_longitude");
                                String maplatitude = jsonSociety.optString("society_latitude");
                                String mapLatiLongi = maplatitude +","+ maplongitute;
                                String id = jsonChildNode.optString("ad_inventory_id");
                                String societyname = jsonSociety.optString("supplierName");
                                String type = jsonType.optString("type");
                                String business_name = "Demo";
                                String date = jsonChildNode.optString("audit_date");
                                String submit = jsonChildNode.optString("submit_status");
                                String inventory_address = jsonChildNode.optString("ad_location");
                                String inventoryType = jsonChildNode.optString("adinventory_type");
                                if(inventoryType.equals("1")){
                                    inventoryType = "Audit";
                                }
                                else{
                                    inventoryType="Release";
                                }
                                String location = jsonSociety.optString("society_address2")+" "+jsonSociety.optString("society_city")+" "+jsonSociety.optString("society_zip");

                                ad_inventory_id_array.add(id);
                                socciety_Address_array.add(location);
                                inventory_address_array.add(inventory_address);
                                image_address_array.add(societyImage);
                                map_address_array.add(mapLatiLongi);
                                date_address_array.add(date);
                                submit_address_array.add(submit);
                                ad_inventory_type_array.add(inventoryType);
                                society_name_array.add(societyname);
                                ad_type_array.add(type);

                                ad_inventory_id_array.get(0);
                                socciety_Address_array.get(0);
                                inventory_address_array.get(0);
                                image_address_array.get(0);
                                map_address_array.get(0);
                                date_address_array.get(0);
                                submit_address_array.get(0);
                                ad_inventory_type_array.get(0);
                                society_name_array.get(0);
                                ad_type_array.get(0);
                            }
                            mBuilder = new NotificationCompat.Builder(this);

                            mBuilder.setSmallIcon(R.mipmap.ic_launcher);
                            mBuilder.setContentTitle("Machadalo");
                            mBuilder.setContentText("Today, You have "+i+" Inventories to Audit");
                            // creating connection detector class instance

                            Intent resultIntent = new Intent(this, auditorDashboard.class);
                            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                            stackBuilder.addParentStack(auditorDashboard.class);

// Adds the Intent that starts the Activity to the top of the stack
                            stackBuilder.addNextIntent(resultIntent);
                            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                            mBuilder.setContentIntent(resultPendingIntent);

                            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

// notificationID allows you to update the notification later on.
                            mNotificationManager.notify(notificationID, mBuilder.build());

                        }
                    }
                    catch(NullPointerException e1){
                        Log.e("Null Exception", String.valueOf(e1));
                    }
                    add();
                }
            } else {
                // Internet connection is not present
                // Ask user to connect to Internet
                showAlertDialog(auditorDashboard.this, "No Internet Connection",
                        "You don't have internet connection.", false);
            }

        } catch (JSONException e) {
            Log.e("Excetion :", String.valueOf(e));
        }

    }
    public void add()
    {

        try {
            JSONObject jsonResponse = new JSONObject(jsonResult);
            JSONArray jsonMainResult = jsonResponse.optJSONArray("result");// banners is our datable name in mysql database
            try{


            for (int i = 0; i < jsonMainResult.length(); i++) {
                db.addAudit(new AuditGS(ad_inventory_id_array.get(i), socciety_Address_array.get(i),
                        inventory_address_array.get(i),
                        map_address_array.get(i), image_address_array.get(i), date_address_array.get(i), submit_address_array.get(i), "1234",ad_inventory_type_array.get(0),society_name_array.get(i),ad_type_array.get(i)));
            }
            }
            catch(NullPointerException e)
            {
                Log.e("Null pointer:", String.valueOf(e));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Toast.makeText(getApplicationContext(),"Data Added to database",Toast.LENGTH_LONG).show();


    }
    public void showAlertDialog(Context context, String title, String message, Boolean status) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        // Setting Dialog Title
        alertDialog.setTitle(title);

        // Setting Dialog Message
        alertDialog.setMessage(message);

        // Setting alert dialog icon
      /*  alertDialog.setIcon((status) ? R.drawable.material_drawer_circle_mask : R.drawable.material_drawer_circle_mask);
*/
        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent=new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS);
                ComponentName cName = new ComponentName("com.android.phone","com.android.phone.Settings");
                intent.setComponent(cName);
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }
    public void refresh(View v){

        // get the TabFragment which controls all the three fragments.
        Fragment  f =  getSupportFragmentManager().findFragmentByTag("TabFragment");
        // get the view
        View tab_view = f.getView();
        if ( tab_view != null) {
            // get the manager for children.
            FragmentManager childFragmentManager = f.getChildFragmentManager();
            // get the view pager for the View of TabFragment
            viewPager = (ViewPager) tab_view.findViewById(R.id.viewpager);
            // get the index of current active fragment
            int current_fragment_index = viewPager.getCurrentItem();
            // get the attached adapter and cast it to AssignedFragmentAdapter class
            AssignedFragmentAdapter my_adapter = (AssignedFragmentAdapter) viewPager.getAdapter();
            // get the current active fragment
            Fragment current_active_fragment = my_adapter.getRegisteredFragment(current_fragment_index);
            // get the fragment name
            String fragmentName = current_active_fragment.getClass().getSimpleName();
            Log.d("Refresh clicked ",  "Active fragment name " + fragmentName);
            Log.d("QFragClassName", QueueFragment.class.getSimpleName());

            // get the target fragment name to which we have to compare the name of current active fragment
            // if it's AssignedFragment, well make the api call again.
            if ( fragmentName.equals(assignedFragmentName) ) {

                // cast it to Assigned fragment instance.
                AssignedFragment assignedFragment = (AssignedFragment)current_active_fragment;
                // detach and reattach the fragment
                FragmentTransaction mChildFragmentTransaction = childFragmentManager.beginTransaction();
                mChildFragmentTransaction.detach(current_active_fragment);
                mChildFragmentTransaction.attach(current_active_fragment);
                mChildFragmentTransaction.commit();
                // hit the api again
                assignedFragment.accessWebService();
                Log.d("Refresh clicked ",  "Accessing web service for Assigned Fragment");
            }
            else if (fragmentName.equals(QueueFragment.class.getSimpleName())) {

                // cast it to Assigned fragment instance.
                QueueFragment queueFragment = (QueueFragment)current_active_fragment;
                // detach and reattach the fragment
                FragmentTransaction mChildFragmentTransaction = childFragmentManager.beginTransaction();
                mChildFragmentTransaction.detach(current_active_fragment);
                mChildFragmentTransaction.attach(current_active_fragment);
                mChildFragmentTransaction.commit();

                Log.d("Refresh clicked ",  "Fetching data for Queued Fragment");

                // fetch data from local db
                queueFragment.accessQueuedDataFromDb();

            }

        }
        /*
        Intent intent = getIntent();
        finish();
        startActivity(intent);*/
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.refreshMenu) {
            View v=null;
            refresh(v);
            return true;
        }
        Log.d("id received ", Integer.toString(id));

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Log.d("On BackPressedAuditor", "");
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public class GetProfileData extends AsyncTask<Void,Void,Void>{

        private HttpURLConnection connection;
        private StringBuffer response;
        private String jsonResult;

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                // get proposal data url
                String url = Constants.GET_USER_INFO+Constants.USER_ID+"/";
                Log.e("in prof",Constants.USER_ID);
               // Log.e("In asyntask",Constants.GET_USER_INFO);
                URL urlInstance = new URL(url);

                String authToken = Constants.AUTH_TOKEN;
                connection = (HttpURLConnection) urlInstance.openConnection();
                connection.setRequestProperty("Authorization", authToken);


                if ( connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    InputStream in = new BufferedInputStream(connection.getInputStream());

                    response = Utils.ConvertInputStreamToStringBuffer(in);

                    if ( response != null ) {
                        jsonResult = response.toString();
                      //  Log.e("In prof Asyntask",jsonResult);
                        //assignedFragment.setJsonResult(response.toString());
                    }
                    else{
                        jsonResult = null;
                        //assignedFragment.setJsonResult(null);
                    }
                }
                else{

                    InputStream in =  new BufferedInputStream(connection.getErrorStream());
                    StringBuffer errorResponse = Utils.ConvertInputStreamToStringBuffer(in);

                    if ( errorResponse == null ){
                        Log.e("ProfileAsynTask", "fetching from input stream resulted in exception");
                    }
                    else {
                        Log.e("ProfileAsynTask ", "error responsse  "+errorResponse.toString());
                    }
                }


            } catch (Exception  e) {
                Log.e("ProfileAsynTask", "Error "+ e.getMessage());
            } finally{

                if ( connection != null ){
                    Log.e("ProfileAsynTask", "disconnecting from " + Constants.GET_PROPOSAL_DATA);
                    connection.disconnect();
                }

            }


            return null;
        }


        // after getting the JSON response
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //parse and insert data
            parseJasonAndInsert();

        }

        public void parseJasonAndInsert() {

            try{

                if (jsonResult == null) {
                    Toast.makeText( auditorDashboard.this ,"No response from server due to internet", Toast.LENGTH_SHORT).show();

                } else {
                    JSONObject jsonResponse = new JSONObject(jsonResult);
                    Log.e("JSON Array", String.valueOf(jsonResponse));

                    JSONObject data = jsonResponse.getJSONObject("data");
                    //JSONArray data = (JSONArray)jsonResponse.get("data");

                    String firstName = data.getString("first_name");
                    String lastName = data.getString("last_name");
                    Name = firstName+" "+lastName;
                    email = data.getString("email");
                    MobileNum = data.getString("mobile");
                    UserName = data.getString("username");

                  //  Log.e("in profile",Name+" "+email+" "+MobileNum+" "+UserName);

                    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("profileName",Name);
                    editor.putString("profileEmail",email);
                    editor.putString("profileUserName",UserName);
                    editor.putString("profileMobile",MobileNum);
                    editor.commit();

                }


            } catch (JSONException e){
                e.printStackTrace();
            }



        }

    }


}