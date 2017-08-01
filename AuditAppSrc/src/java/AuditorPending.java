package com.machadalo.audit;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.machadalo.audit.extras.ConnectionDetector;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class AuditorPending extends AppCompatActivity {
    private ImageView img;
    private Button btnCapture;
    private String jsonResult;
    ArrayList<String> pictures= new ArrayList<String>();//Array to store pictures
    ArrayList<String> locationarray= new ArrayList<String>();
    ArrayList<String> addressarray= new ArrayList<String>();
    ArrayList<String> idarray = new ArrayList<String>();
    ArrayList<String> submitStatusArray = new ArrayList<String>();
    ArrayList<String> inventDateArray = new ArrayList<String>();

    private String url = "http://android.infiniteloopsinc.com/audit/media/assigned.php"; // our Url to server
    private ImageLoader imgLoader;
    private TextView txtCount;
    private TextView txtId;
    private TextView txtAddress;
    private TextView txtLocation,txtSubmitStatus,inventDate;
    private String inventory_id;
    private String addresspass;
    private String locationpass;
    private String submitStatusPass,inventDatePass;

    int element,total;
    // flag for Internet connection status
    Boolean isInternetPresent = false;
    ConnectionDetector cd;
    String strAdCount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auditor_pending);
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        // creating connection detector class instance
        cd = new ConnectionDetector(getApplicationContext());
        setSupportActionBar(toolbar);
        element=0;
        btnCapture = (Button) findViewById(R.id.btnCapture);
        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i1 = new Intent(AuditorPending.this, AuditorCapture.class);
                i1.putExtra("id", idarray.get(element).toString());
                i1.putExtra("address", addressarray.get(element).toString());
                i1.putExtra("location", locationarray.get(element).toString());
                i1.putExtra("submitStatus", submitStatusPass);
                i1.putExtra("inventDate", inventDatePass);
                i1.putExtra("adcounter", strAdCount);

                startActivity(i1);
            }
        });
        txtAddress=(TextView)findViewById(R.id.textViewAddress);
        txtLocation=(TextView)findViewById(R.id.textViewLocation);
        txtId=(TextView)findViewById(R.id.textViewID);
        txtCount=(TextView)findViewById(R.id.textViewCount);
        txtSubmitStatus=(TextView)findViewById(R.id.submitStatus);
        inventDate=(TextView)findViewById(R.id.txtDate);

        txtCount.setText("1"+total);

        accessWebService();

    }


    private class JsonReadTask extends AsyncTask<String, Void, String> { // asynctask which runs on dif thread to parse json object from server
        @Override
        protected String doInBackground(String... params) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(params[0]); // params[0] contains url string
            try {
                HttpResponse response = httpclient.execute(httppost);
                jsonResult = inputStreamToString(
                        response.getEntity().getContent()).toString();
            }

            catch (ClientProtocolException e) {
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
            }

            catch (IOException e) {
                e.printStackTrace();
            }
            return answer;
        }

        @Override
        protected void onPostExecute(String result) {
            ListDrwaer();
        }
    }// end async task

    public void accessWebService() { // helper function to run jason task
        JsonReadTask task = new JsonReadTask();
        // passes values for the urls string array
        task.execute(new String[] { url });
    }


    //method to set textview and image from database
    public void setViewText()
    {
        txtAddress.setText("Ad Address : "+addressarray.get(element).toString());
        txtId.setText("Ad Inventory ID : "+idarray.get(element).toString());
        txtLocation.setText("Ad Location : "+locationarray.get(element).toString());
        url = "http://android.infiniteloopsinc.com/audit/media/inventory/"+pictures.get(element);
        img = (ImageView) findViewById(R.id.imgPending);
        imgLoader = new ImageLoader(this);
        imgLoader.DisplayImage(url, img);
        txtCount.setText(element + 1 + "of" + total);
        strAdCount = element + 1 + "of" + total;
        if(submitStatusArray.get(element).toString() == "yes") {
            txtSubmitStatus.setTextColor(Color.parseColor("#00ff00"));
            txtSubmitStatus.setText("Submit Status: " + submitStatusArray.get(element).toString());
        }
        else{
            txtSubmitStatus.setTextColor(Color.parseColor("#ff0000"));
            txtSubmitStatus.setText("Submit Status: " + submitStatusArray.get(element).toString());
        }
        inventDate.setText("Date : " + inventDateArray.get(element).toString());
    }

    // build hash set for list view
    public void ListDrwaer() {


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
                    JSONObject jsonResponse = new JSONObject(jsonResult);
                    JSONArray jsonMainNode = jsonResponse.optJSONArray("pending");// pending is our json object name in mysql database
                    if(jsonMainNode== null)
                    {
                        Toast.makeText(getApplicationContext(), "There is no assign inventory!",
                                Toast.LENGTH_SHORT).show();
                    }
                    else {
                        total = jsonMainNode.length();
                        for (int i = 0; i < jsonMainNode.length(); i++) {
                            JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                            String picture = jsonChildNode.optString("pic_url");
                            String id = jsonChildNode.optString("ad_inventory_id");
                            String location = jsonChildNode.optString("location");
                            String address = jsonChildNode.optString("address");
                            String submitStatus = jsonChildNode.optString("submit_status");
                            String inventDate = jsonChildNode.optString("assigned_date");
                            inventDateArray.add(inventDate);
                            inventDatePass = inventDateArray.get(0);
                            idarray.add(id);
                            inventory_id = idarray.get(0);
                            locationarray.add(location);
                            locationpass = locationarray.get(0);
                            addressarray.add(address);
                            addresspass = addressarray.get(0);
                            pictures.add(picture);
                            submitStatusArray.add(submitStatus);
                            submitStatusPass = submitStatusArray.get(0);
                            setViewText();



                        }
                        img.setOnTouchListener(new OnSwipeTouchListener(this) {
                            @Override

                            public void onSwipeLeft() {
                                try {
                                    //  Toast.makeText(getApplicationContext(), "Left Swipe ", Toast.LENGTH_SHORT).show();
                                    element = element + 1;
                                    setViewText();  //Call method to set text and image
                                } catch (Exception e) {
                                    Toast.makeText(getApplicationContext(), "Last Image ", Toast.LENGTH_SHORT).show();
                                    element = element - 1;
                                }
                            }

                            @Override
                            public void onSwipeRight() {
                                try {
                                    ///   Toast.makeText(getApplicationContext(), "Right Swipe ", Toast.LENGTH_SHORT).show();
                                    element = element - 1;
                                    setViewText(); //Call method to set text and image
                                } catch (Exception e) {
                                    Toast.makeText(getApplicationContext(), "First Image ", Toast.LENGTH_SHORT).show();
                                    element = 0;
                                }
                            }

                        });


                    }
                }
            } else {
                // Internet connection is not present
                // Ask user to connect to Internet
                showAlertDialog(AuditorPending.this, "No Internet Connection",
                        "You don't have internet connection.", false);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }



    }
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
                Intent intent=new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS);
                ComponentName cName = new ComponentName("com.android.phone","com.android.phone.Settings");
                intent.setComponent(cName);
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }


    public void refresh(View v){
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }






}
