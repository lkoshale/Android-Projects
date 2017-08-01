package com.machadalo.audit.AsyncTasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.machadalo.audit.Constants;
import com.machadalo.audit.Utils;
import com.machadalo.audit.fragments.AssignedFragment;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by root on 19/2/17.
 */

public class GetCampaignSuppliersInventories extends AsyncTask<String, Void, String> {

    // makes call to url and populates the society list on Assigned tab

    private Context context;
    private AssignedFragment assignedFragment;
    private final String TAG = this.getClass().getSimpleName().substring(0, 20);
    private HttpURLConnection connection;
    private StringBuffer response;

    public GetCampaignSuppliersInventories( Context context, AssignedFragment assignedFragment ) {
        this.context = context;
        this.assignedFragment = assignedFragment;
    }

    @Override
    protected String doInBackground(String... strings) {

        String assignedTo = Constants.USER_ID;

        try{
            String url = strings[0] + "?assigned_to=" + assignedTo;
            URL urlInstance =  new URL(url);

            Log.e("inGETCampaign ",urlInstance.toString());

            Log.d(TAG, "connecting to " + Constants.LIST_CAMPAIGN_SUPPLIERS_URL + "?assigned_to="+ assignedTo);

            String authToken = Constants.AUTH_TOKEN;
            connection = (HttpURLConnection)urlInstance.openConnection();
            connection.setRequestProperty ("Authorization", authToken);

            if ( connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream in = new BufferedInputStream(connection.getInputStream());

                response = Utils.ConvertInputStreamToStringBuffer(in);

                if ( response != null ) {
                    assignedFragment.setJsonResult(response.toString());
                }
                else{
                    assignedFragment.setJsonResult(null);
                }
            }
            else{

                InputStream in =  new BufferedInputStream(connection.getErrorStream());
                StringBuffer errorResponse = Utils.ConvertInputStreamToStringBuffer(in);

                if ( errorResponse == null ){
                    Log.d(TAG, "fetching from input stream resulted in exception");
                }
                else {
                    Log.d(TAG, errorResponse.toString());
                }
            }
        }
        catch (Exception  e) {
            Log.d(TAG, e.getMessage());
        }
        finally{

            if ( connection != null ){
                Log.d(TAG, "disconnecting from " + Constants.LIST_CAMPAIGN_SUPPLIERS_URL);
                connection.disconnect();
            }

        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        Log.d(TAG, "Calling onPostExecute");
        this.assignedFragment.ListDrawer();
    }
}
