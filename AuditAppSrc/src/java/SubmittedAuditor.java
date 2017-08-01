package com.machadalo.audit;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.machadalo.audit.extras.ConnectionDetector;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


public class SubmittedAuditor extends Activity {
    String data = "";

    private String jsonResult; // json array will be stored in this when we get from server
    private String url = "http://android.infiniteloopsinc.com/audit/media/submitted.php"; // our Url to server
    //private ListView listView;
    ArrayList<String> picturesPen= new ArrayList<String>();//Array to store pictures
    ArrayList<String> locationarrayPen= new ArrayList<String>();
    ArrayList<String> addressarrayPen= new ArrayList<String>();
    ArrayList<String> idarrayPen= new ArrayList<String>();
    String upLoadServerUri = null;
    ProgressDialog dialog = null;
    /*final String uploadFilePath = String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES));*/
    final String uploadFilePath = "http://android.infiniteloopsinc.com/audit/media/assigned/";
    String uploadFileName = "1.jpg";
    int serverResponseCode = 0;
    InputStream is=null;
    String line=null;
    String result=null;
    int code;
    int totallength;
    // flag for Internet connection status
    Boolean isInternetPresent = false;
    ConnectionDetector cd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submitted_auditor);
        accessWebService();
        // creating connection detector class instance
        cd = new ConnectionDetector(getApplicationContext());

    }
    public void setListViewHeightBasedOnChildren(ListView listView) {
        SubmittedAdapter listAdapter = (SubmittedAdapter) listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }




    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    // Async Task to access the web
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
            ListDrwaer1();

        }
    }// end async task

    public void accessWebService() { // helper function to run jason task
        JsonReadTask task = new JsonReadTask();
        // passes values for the urls string array
        task.execute(new String[] { url });
    }

    // build hash set for list view Submitted
    public void ListDrwaer() {
        ArrayList<String> pictures= new ArrayList<String>();//Array to store pictures
        ArrayList<String> locationarray= new ArrayList<String>();
        ArrayList<String> addressarray= new ArrayList<String>();
        ArrayList<String> idarray= new ArrayList<String>();

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
                    JSONArray jsonMainNode = jsonResponse.optJSONArray("submitted");// pending is our json object name in mysql database
                    if(jsonMainNode== null)
                    {
                        Toast.makeText(getApplicationContext(), "There is no assign inventory!",
                                Toast.LENGTH_SHORT).show();
                    }
                    else {

                        for (int i = 0; i < jsonMainNode.length(); i++) {
                            JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                            String picture = jsonChildNode.optString("pic_url");
                            String id = jsonChildNode.optString("ad_inventory_id");
                            String location = jsonChildNode.optString("location");
                            String address = jsonChildNode.optString("address");
                            pictures.add(picture);
                            idarray.add(id);
                            locationarray.add(location);
                            addressarray.add(address);



                        }


                    }
                }
            } else {
                // Internet connection is not present
                // Ask user to connect to Internet
                showAlertDialog(SubmittedAuditor.this, "No Internet Connection",
                        "Please turn on you Internet", false);
            }

        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "Error" + e.toString(),
                    Toast.LENGTH_SHORT).show();
        }
        SubmittedAdapter SubmittedAuditorAdapter = new SubmittedAdapter(this,pictures,idarray,addressarray);

         // this custom Adapter which gives ur list view Design
        ListView SubmittedAuditorList = (ListView) findViewById(R.id.listSubmitted);
        SubmittedAuditorList.setAdapter(SubmittedAuditorAdapter);
        setListViewHeightBasedOnChildren(SubmittedAuditorList);

        //setListViewHeightBasedOnChildren(list2);

    }
    // build hash set for list view Pending
    public void ListDrwaer1() {

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

                        for (int i = 0; i < jsonMainNode.length(); i++) {
                            JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                            String picture = jsonChildNode.optString("pic_url");
                            String id = jsonChildNode.optString("ad_inventory_id");
                            String location = jsonChildNode.optString("location");
                            String address = jsonChildNode.optString("address");
                            picturesPen.add(picture);
                            idarrayPen.add(id);
                            locationarrayPen.add(location);
                            addressarrayPen.add(address);



                        }


                    }
                }
            } else {
                // Internet connection is not present
                // Ask user to connect to Internet
                showAlertDialog(SubmittedAuditor.this, "No Internet Connection",
                        "You don't have internet connection.", false);
            }

        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "Error" + e.toString(),
                    Toast.LENGTH_SHORT).show();
        }

        SubmittedAdapter SubmittedAuditorAdapter = new SubmittedAdapter(this,picturesPen,idarrayPen,addressarrayPen);

        // this custom Adapter which gives ur list view Design
        ListView SubmittedAuditorList = (ListView) findViewById(R.id.listPending);
        SubmittedAuditorList.setAdapter(SubmittedAuditorAdapter);
        setListViewHeightBasedOnChildren(SubmittedAuditorList);

        //setListViewHeightBasedOnChildren(list2);

    }
    /*Inserting in table Bulk Value*/


    public void upload(View v){
        upLoadServerUri = "http://android.infiniteloopsinc.com/audit/media/submittedfile.php";
        dialog = ProgressDialog.show(SubmittedAuditor.this, "", "Uploading file...", true);
        new Thread(new Runnable() {
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {

                        Toast.makeText(getApplicationContext(),
                                "uploading started.....", Toast.LENGTH_LONG).show();
                    }
                });

                uploadFile(uploadFilePath + "/" + uploadFileName);

                insert();

            }
        }).start();
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
                    + uploadFilePath + "" + uploadFileName);

            runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(getApplicationContext(),
                            "Source File not exist :", Toast.LENGTH_LONG).show();
                }
            });

            return 0;

        }
        else
        {
            try {

                // open a URL connection to the Servlet
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
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""+ fileName + "\"" + lineEnd);

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

                if(serverResponseCode == 200){

                    runOnUiThread(new Runnable() {
                        public void run() {

                            String msg = "File Upload Completed.\n\n See uploaded file here : \n\n"
                                    +" http:/android.infiniteloopsinc.com/audit//media/submitted/"
                                    +uploadFileName;


                            Toast.makeText(getApplicationContext(),
                                    msg, Toast.LENGTH_LONG).show();
                            Toast.makeText(SubmittedAuditor.this, "File Upload Complete.",
                                    Toast.LENGTH_SHORT).show();
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

                        Toast.makeText(SubmittedAuditor.this, "MalformedURLException",
                                Toast.LENGTH_SHORT).show();
                    }
                });

                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {

                dialog.dismiss();
                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {

                        Toast.makeText(SubmittedAuditor.this, "Got Exception : see logcat ",
                                Toast.LENGTH_SHORT).show();
                    }
                });

            }
            dialog.dismiss();
            return serverResponseCode;

        } // End else block
    }


    public void insert()
    {
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        for (int i=0; i<totallength; i++) {
            nameValuePairs.add(new BasicNameValuePair("sub_pic", picturesPen.get(i)));
            nameValuePairs.add(new BasicNameValuePair("sub_by", "Asna"));
            nameValuePairs.add(new BasicNameValuePair("sub_status", "Yes"));
            nameValuePairs.add(new BasicNameValuePair("sub_invent_id", idarrayPen.get(i)));

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
    }

    /*End Data Insertin Code*/




    public ArrayList<String> parseJSON(String result){
        ArrayList<String> users = new ArrayList<String>();
        try {
            JSONArray jArray = new JSONArray(result);
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject json_data = jArray.getJSONObject(i);
                String picture = json_data.getString("picture"); // get value of picture column of present row
                users.add(picture);
            }
        } catch (JSONException e) {
            Log.e("log_tag", "Error parsing data " + e.toString());
        }

        return users;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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

}

