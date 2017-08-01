package com.machadalo.audit.AsyncTasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.machadalo.audit.Constants;
import com.machadalo.audit.Messages;
import com.machadalo.audit.Utils;
import com.machadalo.audit.sqllite.DataBaseHandler;
import com.machadalo.audit.sqllite.InventoryImagePathTable;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by root on 4/2/17.
 */

    public class WriteToInventoryActivityImage extends AsyncTask<String, Void, String> { // asynctask which runs on dif thread to parse json object from server

        Context context;

        public  WriteToInventoryActivityImage( Context context ){
            this.context = context;
        }

        @Override
        protected String doInBackground(String... params) {

            String jsonResult = "";

            StringBuffer jsonResponse;
            String activityDate;
            String activityType;
            String shortlistedInventoryDetailsId;
            String comment;
            String imagePath;
            String inventoryImageId;
            String lat,lon;
            DataBaseHandler db_handle = null;

            try {

                URL url = new URL(params[0]);

                Log.d("Recved Userid", Constants.USER_ID);

                activityDate = params[1];
                activityType = params[2];
                shortlistedInventoryDetailsId = params[3];
                comment = params[4];
                imagePath = params[5];
                inventoryImageId = params[6];
                lat = params[7];
                lon = params[8];

                Log.d("AboutToMakeAPICall", "");
                Log.d("actDate", activityDate);
                Log.d("actType", activityType);
                Log.d("SID", shortlistedInventoryDetailsId);
                Log.d("comment", comment);
                Log.d("imagePath", imagePath);
                Log.d("inventoryImagePath", inventoryImageId);

                Map<String,String> postMapData = new LinkedHashMap<>();

                postMapData.put("image_path", imagePath);
                postMapData.put("comment", comment);
                postMapData.put("activity_type", activityType);
                postMapData.put("activity_date", activityDate);
                postMapData.put("actual_activity_date", Utils.getCurrentDateString());
                postMapData.put("activity_by", Constants.USER_ID);
                postMapData.put("shortlisted_inventory_detail_id", shortlistedInventoryDetailsId);
                postMapData.put("latitude",lat);
                postMapData.put("longitude",lon);

                postMapData.put("use_assigned_date", "1");
                StringBuilder postData = new StringBuilder();
                for (Map.Entry<String,String> param : postMapData.entrySet()) {

                    if (postData.length() != 0)
                        postData.append('&');

                    postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                    postData.append('=');
                    postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
                }

                byte[] postDataBytes = postData.toString().getBytes("UTF-8");

                String authToken = Constants.AUTH_TOKEN;
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty ("Authorization", authToken);
                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                connection.getOutputStream().write(postDataBytes);

                connection.connect();

                if ( connection.getResponseCode() == Constants.OK_RESPONSE_CODE ) {

                    // mark this entry as django uploaded
                    db_handle = DataBaseHandler.getInstance(context);
                    InventoryImagePathTable.updateIsDjangoUploaded(db_handle, inventoryImageId, Constants.True);

                    InputStream inputStream = connection.getInputStream();
                    jsonResponse = Utils.ConvertInputStreamToStringBuffer(inputStream);
                    if ( jsonResponse != null) {
                        jsonResult = jsonResponse.toString();
                    }
                    Log.d("Response", jsonResult);
                    return jsonResult;
                }
                else {

                    InputStream inputStream = connection.getErrorStream();
                    jsonResponse = Utils.ConvertInputStreamToStringBuffer(inputStream);
                    if ( jsonResponse != null) {
                        jsonResult = jsonResponse.toString();
                        Log.d("ERROR IN API RESPONSE", jsonResult);
                    }

                }

            }
            catch (Exception e) {
                Log.d("WriteToInvActImage", e.getMessage());
            }
            finally {

                if ( db_handle != null) {

                    db_handle.close();
                }
            }

            return null;
        }


        @Override
        protected void onPostExecute(String result) {

        //    Log.d("API call finished", result);

        }
    }// end async task

