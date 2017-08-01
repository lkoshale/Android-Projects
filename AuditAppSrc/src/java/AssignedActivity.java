package com.machadalo.audit;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.machadalo.audit.extras.ConnectionDetector;
import com.machadalo.audit.sqllite.AuditGS;
import com.machadalo.audit.sqllite.DataBaseHandler;

import java.util.ArrayList;
import java.util.List;

import static java.security.AccessController.getContext;

public class AssignedActivity extends AppCompatActivity {

    ProgressDialog dialog = null;
    private ImageView img;
    private Button btnCapture,btnScan;
    private String jsonResult;
    ArrayList<String> pictures= new ArrayList<String>();//Array to store pictures
    ArrayList<String> locationarray= new ArrayList<String>();
    ArrayList<String> addressarray= new ArrayList<String>();
    ArrayList<String> idarray = new ArrayList<String>();
    ArrayList<String> submitStatusArray = new ArrayList<String>();
    ArrayList<String> inventDateArray = new ArrayList<String>();
    ArrayList<String> societyAddressArray = new ArrayList<String>();
    ArrayList<String> societyNameArray = new ArrayList<String>();
    ArrayList<String> AdTypeArray = new ArrayList<String>();
    ArrayList<String> inventTypeArray = new ArrayList<String>();


    private String url = " http://machadalo.com/android/audit/media/assigned.php"; // our Url to server
    private String urlold = "http://android.infiniteloopsinc.com/audit/media/assigned.php"; // our Url to server
    private ImageLoader imgLoader;
    private TextView txtCount;
    private TextView txtId;
    private TextView txtAddress;
    private TextView txtLocation,txtSubmitStatus,inventDate,txtCocietyAddress,txtSociety,txtAdType;
    private String inventory_id;
    private String addresspass;
    private String locationpass;
    private String submitStatusPass,inventDatePass;
    private Button btnbadge;
    int element,total;
    String inventoryIDRec;
    // flag for Internet connection status
    Boolean isInternetPresent = false;
    ConnectionDetector cd;
    String strAdCount;
    Boolean getIntentRefreshValue;
    private List<AuditGS> assignActData;
    private DataBaseHandler sqliteData;


    String societyname ;
    String type;
    String location;
    String date ;
    String inventoryID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.assigned_layout);
        final Bundle idRec = getIntent().getExtras();
        /*inventoryIDRec = idRec.getString("inventoryIDRec");
        getIntentAct = idRec.getBoolean("getIntentAct");
        getIntentRefreshValue = idRec.getBoolean("updateAssign");
*/
        societyname = idRec.getString("address");
        societyname = idRec.getString("society");
        inventoryID = idRec.getString("id");
        location = idRec.getString("location");
        date = idRec.getString("date");


        Log.e("Value from Capture", String.valueOf(getIntentRefreshValue));

        // creating connection detector class instance
        cd = new ConnectionDetector(getApplicationContext());
        element = 0;
        btnCapture = (Button) findViewById(R.id.btnCapture);
        btnScan = (Button) findViewById(R.id.btnBarcode);
        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i1 = new Intent(AssignedActivity.this, AlertDialogue.class);
                startActivity(i1);
            }
        });

//        btnCapture.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i1 = new Intent(AssignedActivity.this, AuditorCapture.class);
//                i1.putExtra("id", idarray.get(element).toString());
//                i1.putExtra("address", societyAddressArray.get(element).toString());
//                i1.putExtra("location", societyNameArray.get(element).toString());
//                i1.putExtra("submitStatus", "");
//                i1.putExtra("inventDate", inventDateArray.get(element).toString());
//                i1.putExtra("adcounter", strAdCount);
//                i1.putExtra("actualactivity", "AssignedAcitivty");
//                i1.putExtra("imagepath", "MD_MUMPOHNRSOC2_CIv4mT9PlE");
//                startActivity(i1);
//            }
//        });
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i1 = new Intent(AssignedActivity.this, DemoBarcode.class);
                i1.putExtra("barcode", inventoryID);
                startActivity(i1);
            }
        });
        txtAddress = (TextView) findViewById(R.id.textViewAddress);
        txtSociety = (TextView) findViewById(R.id.txtSociety);
        txtLocation = (TextView) findViewById(R.id.textViewLocation);
        txtCocietyAddress = (TextView) findViewById(R.id.txtAddress);
        txtId = (TextView) findViewById(R.id.textViewID);
        txtCount = (TextView) findViewById(R.id.textViewCount);
        txtSubmitStatus = (TextView) findViewById(R.id.submitStatus);
        inventDate = (TextView) findViewById(R.id.txtDate);
        btnbadge = (Button) findViewById(R.id.btn_Badge);
        txtAdType = (TextView) findViewById(R.id.txtAdType);
        txtCount.setText("1" + total);


        sqliteData = DataBaseHandler.getInstance(getApplicationContext());
        assignActData = sqliteData.getAllAudit();

        int i=0;
        for ( i=0; i<assignActData.size();i++) {
            idarray.add(assignActData.get(i).get_ad_inventory_ID());
            societyNameArray.add(assignActData.get(i).get_society_name());
            AdTypeArray.add(assignActData.get(i).get_ad_type());
            societyAddressArray.add(assignActData.get(i).get_society_address());
            pictures.add("MD_MUMPOHNRSOC2_CIv4mT9PlE");
            //assignActData.get(i).get_submit_status();
            inventDateArray.add(assignActData.get(i).get_date());
            inventTypeArray.add(assignActData.get(i).get_invent_type());



        }
        setViewText();
    }




    //method to set textview and image from database
    public void setViewText()
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());



        txtAddress.setText("Mumbai");
        txtSociety.setText(societyname);
        txtId.setText(inventoryID);
        txtLocation.setText(location);
        txtCocietyAddress.setText(societyname);
        inventDate.setText(date);

       /* url = "http://mdimages.s3.amazonaws.com/"+pictures.get(element);
        img = (ImageView) findViewById(R.id.img);
        imgLoader = new ImageLoader(this);
        imgLoader.DisplayImage(url, img);

        // For Count
        txtCount.setText(element + 1 + "of" + total);
        strAdCount = element + 1 + "of" + total;*/


    }

    // build hash set for list view
    public void showAlertDialog(Context context, String title, String message, Boolean status) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        // Setting Dialog Title
        alertDialog.setTitle(title);

        // Setting Dialog Message
        alertDialog.setMessage(message);

        // Setting alert dialog icon
/*
        alertDialog.setIcon((status) ? R.drawable.material_drawer_circle_mask : R.drawable.material_drawer_circle_mask);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.assignmenu, menu);

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
        return super.onOptionsItemSelected(item);
    }
    public void refresh(View v){
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    public void counter_plus(){
        try {
            //  Toast.makeText(getApplicationContext(), "Left Swipe ", Toast.LENGTH_SHORT).show();
            element = element + 1;
            setViewText();  //Call method to set text and image
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Last Image ", Toast.LENGTH_SHORT).show();
            element = element - 1;
        }

    }

    public void counter_minus() {
        try {
            ///   Toast.makeText(getApplicationContext(), "Right Swipe ", Toast.LENGTH_SHORT).show();
            element = element - 1;
            setViewText(); //Call method to set text and image
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "First Image ", Toast.LENGTH_SHORT).show();
            element = 0;
        }

    }

    public void previous(View view){
        counter_minus();
    }

    public void next(View view){
        counter_plus();
    }

    public void map(View view){
        Intent intent = new Intent(AssignedActivity.this , Maps.class);
        startActivity(intent);
    }


}
