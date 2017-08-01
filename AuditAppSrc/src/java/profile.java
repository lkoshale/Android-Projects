package com.machadalo.audit;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class profile extends AppCompatActivity {
    Toolbar toolbar;
    public static String Name;
    public static String email;
    public static String MobileNum;
    public static String UserName;

    private TextView profileName;
    private TextView profileEmail;
    private TextView profileUserName;
    private TextView profileMobileNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileName = (TextView)findViewById(R.id.profileNameText);
        profileMobileNum =(TextView)findViewById(R.id.profileMobileText);
        profileUserName = (TextView) findViewById(R.id.profileIDtext);
        profileEmail = (TextView)findViewById(R.id.profileEmailText);

        String prefName = manageScreens.readFromPrefrence(getApplicationContext(), "name", "default");
        String prefEmail = manageScreens.readFromPrefrence(getApplicationContext(), "email", "default");
        String prefRole = manageScreens.readFromPrefrence(getApplicationContext(), "role", "default");

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Name = settings.getString("profileName","not available");
        UserName = settings.getString("profileUserName","not available");
        email = settings.getString("profileEmail","not available");
        MobileNum = settings.getString("profileMobile","not available");


        if (Name != null)
            profileName.setText(": "+Name);
        else
            profileName.setText("Not available !!");

        if (email!=null)
            profileEmail.setText(": "+email);
        else
            profileEmail.setText("Not available !!");

        if (UserName!=null)
            profileUserName.setText(": "+UserName);
        else
            profileUserName.setText("Not available !!");

        if (MobileNum!=null)
            profileMobileNum.setText(": "+MobileNum);
        else
            profileMobileNum.setText("Not available !!");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }




}
