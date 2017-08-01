package com.machadalo.audit.Services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.machadalo.audit.Constants;
import com.machadalo.audit.Messages;
import com.machadalo.audit.Utils;
import com.machadalo.audit.extras.QueueData;
import com.machadalo.audit.sqllite.DataBaseHandler;
import com.machadalo.audit.sqllite.InventoryImagePathTable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 12/2/17.
 */

public class BulkUploadImagesDjango extends IntentService {

    // this service is written to upload files to django backend in bulk
    Context context;
    public BulkUploadImagesDjango(){
        super("BulkUploadImagesDjango");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        // make the database query
        // hit the url
        // on response from server, update the status for each row
        DataBaseHandler db_handle = DataBaseHandler.getInstance(getApplicationContext());
        List<QueueData> data = InventoryImagePathTable.getQueuedData(db_handle);
        StringBuffer jsonResponse;
        String jsonResult = "";

        if ( data == null || data.size() == 0 ) {
            Log.d("BulkUploadServ", "No data to upload. All synced up");
            stopSelf();
            return;
        }

        try {

            URL url = new URL(Constants.BULK_IMAGE_UPLOAD_URL);
            JSONArray postData = new JSONArray();

            List<String> inventoryImagePathTableIdList = new ArrayList<>();

            for (int i = 0; i < data.size(); i++) {

                    // process only Django un uploaded files here
                    if (data.get(i).getIsDjangoUploaded().equals(Constants.True))
                        continue;

                    JSONObject currentData = new JSONObject();

                    // the keys of currentData match exactly with django-backend keys.
                    currentData.put("shortlisted_inventory_detail_id", data.get(i).getShortlistedInventoryDetailId());
                    currentData.put("image_path", data.get(i).getImagePath());
                    currentData.put("comment", data.get(i).getComment());
                    currentData.put("activity_type", data.get(i).getActivityType());
                    currentData.put("activity_date", data.get(i).getActivityDate());
                    currentData.put("latitude",data.get(i).getLat());
                    currentData.put("longitude",data.get(i).getLon());


                    // collect these ids as we have to update the status of each one of them later
                    inventoryImagePathTableIdList.add(data.get(i).getInventoryImagePathTableId());

                    postData.put(i, currentData);
                }

            String authToken = Constants.AUTH_TOKEN;
            Log.e("token", authToken);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty ("Authorization", authToken);
            connection.setRequestProperty("Content-Type","application/json;charset=utf-8");
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);

            connection.connect();

            Log.d("UpdjangImag", "Connection done");

            OutputStream os = new BufferedOutputStream(connection.getOutputStream());
            os.write(postData.toString().getBytes());
            os.flush();

            Log.d("updjanImag", "outstream flushed");

            if ( connection.getResponseCode() == Constants.OK_RESPONSE_CODE ) {

                InventoryImagePathTable.bulkUpdateDjangoIsUploaded(db_handle, inventoryImagePathTableIdList.toArray(new String [0]), Constants.True);
                InputStream inputStream = connection.getInputStream();
                jsonResponse = Utils.ConvertInputStreamToStringBuffer(inputStream);
                if ( jsonResponse != null) {
                    jsonResult = jsonResponse.toString();
                }

                Log.d("Response", jsonResult);
                stopSelf();
            }
            else{

                Log.d("Error in Api response", connection.getResponseMessage());
                stopSelf();
            }

        } catch (Exception e){
            Log.d("error in service", e.getMessage());
            stopSelf();
        }
        finally {

        }
    }
}
