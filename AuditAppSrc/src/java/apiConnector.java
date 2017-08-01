package com.machadalo.audit;

/**
 * Created by admin on 6/26/2015.
 */

import android.util.Base64;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// class to handle the login operation
public class apiConnector {

    public JSONArray getAllAuditors(String email, String Password)
    {
        //url for getting all editors
        //TODO: URL Change
        String url = "http://machadalo.com/android/audit/login.php";
        // get http response object from the url
        HttpEntity httpEntity = null;

        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost= new HttpPost(url);

            String authorizationString = "Basic " + Base64.encodeToString(("admin" + ":" + "admin").getBytes(), Base64.NO_WRAP);
            httpPost.setHeader("Authorization", authorizationString);
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
            nameValuePairs.add(new BasicNameValuePair("email", email));
            nameValuePairs.add(new BasicNameValuePair("password", Password));
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            String uri = String.valueOf(httpPost.getURI());
            Log.i("mytag", uri);

            HttpResponse httpResponse = httpClient.execute(httpPost);

            httpEntity = httpResponse.getEntity();

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //convert httpEntity into json array
        JSONArray jsonArray = null;
        if(httpEntity!= null){
            try {
                String EntityResponse = EntityUtils.toString(httpEntity);
                Log.e("Entity Response : ", EntityResponse);
                jsonArray = new JSONArray(EntityResponse);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonArray;
    }
}
