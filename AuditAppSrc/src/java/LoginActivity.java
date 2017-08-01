package com.machadalo.audit;

//this is the code for login

import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class LoginActivity extends Activity{
    // email Id of the user
    EditText emailId;
    //password of the user
    EditText passwordId;
    //button to be clicked when user fills in all the details
    Button loginButtonId, enq;

    ProgressDialog dialog = null;
    // some bookkeeping variables
    String name = "";
    String email1="";
    String password1="";
    String phone="";
    private String jsonResult;

    //code commented after changing the url for login i.e hitting database api i.e amazonaws

    //this is the method which is called when the screen opens
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(manageScreens.readFromPrefrence(LoginActivity.this, "status", "false").equals("true")){

            Intent i=null;
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

            Constants.AUTH_TOKEN = settings.getString("token", "0");
            Constants.USER_ID = settings.getString("userId", "1");

            Log.d("UserId", Constants.USER_ID);
            i = new Intent(LoginActivity.this,auditorDashboard.class);
            startActivity(i);
        }
        else{

            //attaching the layout file with the activity
            setContentView(R.layout.activity_login);

            //taking references to the widgets in the layout
            emailId = (EditText) findViewById(R.id.emailId);
            passwordId = (EditText) findViewById(R.id.passwordId);
            loginButtonId = (Button) findViewById(R.id.loginButtonId);
                //setting a listener to the button
            loginButtonId.setOnClickListener(
                    new Button.OnClickListener() {
                        public void onClick(View v) {
                            email1 = emailId.getText().toString();
                            password1 = passwordId.getText().toString();
                             if(!email1.isEmpty() && !password1.isEmpty()) {
                                 //this method will login the user
                                 dialog = ProgressDialog.show(LoginActivity.this, "", "Please wait...", true);
                                 getAllAuditorsTask task = new getAllAuditorsTask();
                                 task.execute(new String[]{});
                             }
                            else{
                                String msg = "fill in the proper credentials";
                                 Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                             }
                        }
                });
        }
    }

    //this is the on click method for the signUp button
    public void changeActivity(View v){
        Intent i = new Intent(this,SignupActivity.class);
        startActivity(i);

    }
    //this class is used to create  an asynchronous  task of calling the users from the database

       private class getAllAuditorsTask extends AsyncTask<String,Void,String>{
           //this method is run in the background and returns a JSON array of users in the database
            @Override
            protected String doInBackground(String... params) {
//                return params[0].getAllAuditors(email1,password1);
                String url = Constants.LOGIN_URL;
                // get http response object from the url
                HttpEntity httpEntity = null;

                try {
                    DefaultHttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost= new HttpPost(url);

                    String authorizationString = "Basic " + Base64.encodeToString(("admin" + ":" + "admin").getBytes(), Base64.NO_WRAP);
                    httpPost.setHeader("Authorization", authorizationString);
                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                    nameValuePairs.add(new BasicNameValuePair("username", email1));
                    nameValuePairs.add(new BasicNameValuePair("password", password1));
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    String uri = String.valueOf(httpPost.getURI());
                    Log.i("mytag", uri);

                    HttpResponse httpResponse = httpClient.execute(httpPost);

//                    httpEntity = httpResponse.getEntity();
                    jsonResult = inputStreamToString(httpResponse.getEntity().getContent()).toString();

                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            //this method gets executed after the doInBackground returns the JSON array containing all the users


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
           protected void onPostExecute(String result) {
               try {
                   dialog.dismiss();
                   //take the user to his DASHBOARD creating a new intent
                   Intent i1;
                   JSONObject jsonResponse = new JSONObject(jsonResult);
                   if (jsonResult.contains("token")) {
                       String token = "JWT " + jsonResponse.getString("token");
                       Constants.USER_ID = jsonResponse.getString("user_id");
                       Log.e("LOGIN",Constants.USER_ID);
                       Log.d("User id from auth api", Constants.USER_ID);
                       Constants.AUTH_TOKEN = token;
                       i1 = new Intent(LoginActivity.this, auditorDashboard.class);
                       i1.putExtra("email", email1);
                       startActivity(i1);
                       String[] prefvalue = {name,email1,"role","true"};
                       String[] prefname = {"name", "email","role","status"};
                       manageScreens.saveToPrefrence(LoginActivity.this, prefname, prefvalue);

                       //code added to save token used when the user reopen the closed app
                       SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                       SharedPreferences.Editor editor = settings.edit();
                       editor.putString("token", token);
                       editor.putString("userId", Constants.USER_ID);
                       editor.commit();
                   } else {
                       Toast.makeText(getApplicationContext(), "Incorrect username or password", Toast.LENGTH_LONG).show();
                   }
               }
               catch(Exception e){
                   e.printStackTrace();
               }

           }
        }




    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
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
}
