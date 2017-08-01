package com.machadalo.audit;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.widget.SimpleCursorAdapter;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
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
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.machadalo.audit.extras.ConnectionDetector;
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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import android.view.LayoutInflater;
import com.machadalo.audit.DemoCapture;
import com.machadalo.audit.sqllite.QuickImageCaptureTable;

import static java.security.AccessController.getContext;

public class AlertDialogue extends AppCompatActivity {
    final Context c1 = this;
    String supplierName;
    Spinner InventoryTyp;
    Spinner ActivityTyp;
    String proposalName;
    EditText proposalEditText;

   ArrayList<String> inventorySpinnerList = new ArrayList<>(Arrays.asList("STALL","STANDEE","POSTER","FLIER","CAR DISPLAY"));
    ArrayList<String> activitySpinnerList = new ArrayList<>(Arrays.asList("RELEASE","AUDIT","CLOSURE"));

    // just for test, when we click this button, we will see the alert dialog.
//    private Button button;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.input_dialog);

//        button = (Button) findViewById(R.id.btnSetName);
//        showInputDialog();
        final Context c1 = this;
//    void showInputDialog() {

        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(c1);
        View promptView = layoutInflater.inflate(R.layout.input_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(c1);
        alertDialogBuilder.setView(promptView);

        final EditText editText = (EditText) promptView.findViewById(R.id.edittext);
        proposalEditText = (EditText)promptView.findViewById(R.id.QuickProposalName) ;

        InventoryTyp = (Spinner)promptView.findViewById(R.id.AlertInventoryTypSpinner);
        ActivityTyp = (Spinner)promptView.findViewById(R.id.AlertActivityTypSpinner);

        ArrayAdapter<String> inventorySpinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, inventorySpinnerList);
        ArrayAdapter<String> activitySpinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, activitySpinnerList);

        inventorySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        activitySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        InventoryTyp.setAdapter(inventorySpinnerAdapter);
        ActivityTyp.setAdapter(activitySpinnerAdapter);


        // setup a dialog window
        try {
            alertDialogBuilder.setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            supplierName = editText.getText().toString();
                            proposalName = proposalEditText.getText().toString();
                            Log.e("AlertDailouge","ok");

                            if (supplierName != null && supplierName.compareTo("")!=0 && proposalName.compareTo("")!=0 ) {
                                insertSupplierName();
                                //launchActivity();
                            }else
                                finish();
                        }
                    })
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    finish();
                                }
                            });
        }catch (Exception e){
            Log.e("Error", e.getMessage());
        }
        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }
//    private void launchActivity() {
//
//        Intent i1 = new Intent(this, DemoCapture.class);
//        i1.putExtra("supplierName",supplierName);
//        i1.putExtra("mode","CaptureMode");
//        i1.putExtra("actualactivity","AssignedAcitivty");
//        startActivity(i1);
//        finish();
//    }

    private void insertSupplierName(){

        DataBaseHandler db_handle = DataBaseHandler.getInstance(this);
        SQLiteDatabase db = db_handle.getWritableDatabase();

        String inventoryName = InventoryTyp.getSelectedItem().toString();
        String activityTyp = ActivityTyp.getSelectedItem().toString();

        if (checkDetailsPresent(inventoryName,activityTyp) == false) {
            ContentValues values = new ContentValues();
            values.put(QuickImageCaptureTable.KEY_PROPOSAL_NAME,proposalName);
            values.put(QuickImageCaptureTable.KEY_SUPPLIER_NAME, supplierName);
            values.put(QuickImageCaptureTable.KEY_INVENTORY_NAME, inventoryName);
            values.put(QuickImageCaptureTable.KEY_ACTIVITY_TYPE, activityTyp);
//            values.put(QuickImageCaptureTable.KEY_LOCAL_PATH, "");

            db.insert(QuickImageCaptureTable.TABLE_NAME, null, values);
            db_handle.printTotalRows(QuickImageCaptureTable.TABLE_NAME);
            finish();

        }
        else {
            Toast.makeText(getApplicationContext(),"this Inventory and Activity Type is already Present",Toast.LENGTH_LONG).show();
            finish();
        }

    }

    public boolean checkDetailsPresent(String inventoryName, String actiivityTyp ){

        boolean isPresent = false;

        String Query = "SELECT * FROM "+QuickImageCaptureTable.TABLE_NAME+" WHERE "
                +QuickImageCaptureTable.KEY_PROPOSAL_NAME+" = \'"+proposalName+"\' AND "
                +QuickImageCaptureTable.KEY_SUPPLIER_NAME+" = \'"+supplierName+"\' AND "
                +QuickImageCaptureTable.KEY_INVENTORY_NAME + " = \'"+inventoryName+"\';";

        Log.e("in Alert",Query);
        DataBaseHandler db_handle = DataBaseHandler.getInstance(this);
        SQLiteDatabase db = db_handle.getWritableDatabase();

        Cursor cursor = db.rawQuery(Query,null);

        while (cursor.moveToNext()){
            //String CheckInventoryName = cursor.getString(cursor.getColumnIndex(QuickImageCaptureTable.KEY_INVENTORY_NAME));
            String CheckactivityTyp = cursor.getString(cursor.getColumnIndex(QuickImageCaptureTable.KEY_ACTIVITY_TYPE));

           // Log.e("inside alert",CheckactivityTyp+" "+actiivityTyp);
            if (CheckactivityTyp.compareTo(actiivityTyp)==0 ){
                isPresent = true;
                break;
            }
        }

        cursor.close();
        return isPresent;

    }


}
