package com.machadalo.audit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SignupActivity extends Activity {

    EditText fname, email, password, confpassword;
    RadioGroup radioGroup;
    RadioButton typeOfLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Setup the strict mode policy. It is used to catch accidental disk or network access on the application's main thread.
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //set the layout of the activity
        setContentView(R.layout.activity_signup);

        //name
        fname = (EditText) findViewById(R.id.fname);

        //email
        email = (EditText) findViewById(R.id.email);

        //password
        password = (EditText) findViewById(R.id.password);
        confpassword = (EditText) findViewById(R.id.confpassword);

        //type of login
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);

        //signupButton

        Button SignUpButton = (Button) findViewById(R.id.SignUpButton);

        //setting up the onclick listener

        SignUpButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        //select type of login
                        int i = radioGroup.getCheckedRadioButtonId();
                        typeOfLogin = (RadioButton) findViewById(i);
                        //storing values in string variables
                        String fname1 = "" + fname.getText().toString();
                        String email1 = "" + email.getText().toString();
                        String password1 = "" + password.getText().toString();
                        String confpassword1 = "" + confpassword.getText().toString();
                        String typeOfLogin1 = "" + typeOfLogin.getText().toString();
                        if (!fname1.isEmpty() && !email1.isEmpty() && !password1.isEmpty()) {
                            //check for email
                            if (validateEmail(email1)) {
                                //check for password
                                if (validatePassword(password1)) {
                                    if (password1.equals(confpassword1)) {

                                        //setting the name value pairs

                                        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                                        nameValuePairs.add(new BasicNameValuePair("fname", fname1));
                                        nameValuePairs.add(new BasicNameValuePair("email", email1));
                                        nameValuePairs.add(new BasicNameValuePair("password", password1));
                                        nameValuePairs.add(new BasicNameValuePair("typeOfLogin", typeOfLogin1));


                                        //url for posting editors
                                        String url = "http://machadalo.com/android/audit/signup.php";
                                        //setting up the connection inside the try catch block
                                        try {
                                            //setting up default httpclient
                                            HttpClient httpClient = new DefaultHttpClient();

                                            //setting up the http post method passing the url
                                            HttpPost httpPost = new HttpPost(url);
                                            String authorizationString = "Basic " + Base64.encodeToString(("admin" + ":" + "admin").getBytes(), Base64.NO_WRAP);
                                            httpPost.setHeader("Authorization", authorizationString);
                                            String uri = String.valueOf(httpPost.getURI());
                                            Log.i("mytag",uri);

                                            //passing the namevalue pairs inside http post

                                            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                                            //getting the response

                                            HttpResponse httpResponse = httpClient.execute(httpPost);

                                            //setting up the entity

                                            HttpEntity httpEntity = httpResponse.getEntity();
                                            String entityResponse = EntityUtils.toString(httpEntity);

                                            //setting up the content inside an inputstreamReader

                                            //JSON object
                                            final JSONObject jsonObject = new JSONObject(entityResponse);
                                            String abc;
                                            abc = jsonObject.getString("status");
                                            String msg1 = ""+abc;
                                            Toast.makeText(getApplicationContext(), msg1, Toast.LENGTH_LONG).show();

                                            //displaying a toast message if data is entered successfully
                                            if (abc.equals("true")) {

                                                String msg = "you are signed up";
                                                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                                                Intent i2;
                                                if(typeOfLogin1.equals("auditor"))
                                                    i2 = new Intent(SignupActivity.this,auditorDashboard.class);
                                                else if(typeOfLogin1.equals("seller"))
                                                    i2= new Intent(SignupActivity.this,sellerDashboard.class);
                                                else
                                                    i2= new Intent(SignupActivity.this,buyerDashboard.class);
                                                i2.putExtra("name",fname1);
                                                i2.putExtra("email",email1);
                                                i2.putExtra("role",typeOfLogin1);
                                                startActivity(i2);
                                                String[] prefvalue = {fname1,email1,typeOfLogin1,"true"};
                                                String[] prefname={"name","email","role","status"};
                                                manageScreens.saveToPrefrence(SignupActivity.this, prefname, prefvalue);
                                            } else {
                                                String msg = "email already exists ";
                                                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                                            }


                                        } catch (UnsupportedEncodingException e) {
                                            e.printStackTrace();
                                        } catch (ClientProtocolException e) {
                                            e.printStackTrace();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    } else {
                                        String msg = "passwords don't match";
                                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                                    }
                                } else {

                                    String msg = "invalid password";
                                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                                    password.requestFocus();

                                }
                            } else {
                                String msg = "invalid email";
                                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                                email.requestFocus();
                            }
                        } else {
                            String msg = "please fill in all the fields";
                            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );


    }


    //method used to check the password validity
    protected boolean validatePassword(String password) {
        String passwordPattern = "^(?=.*(\\d|\\W)).{8,20}$";
        Pattern pattern = Pattern.compile(passwordPattern);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    //method used to check the email validity
    protected boolean validateEmail(String email) {
        String emailPattern = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(emailPattern);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_signup, menu);
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
