package com.machadalo.audit.sqllite;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.machadalo.audit.Constants;
import com.machadalo.audit.Utils;
import com.machadalo.audit.extras.PendingData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by root on 30/1/17.
 */

/*
  DATA fetching functions added by lokesh
 */

public class ProposalTable {

    public Context context;

    public static final String TABLE_NAME = "proposal";
    //  Table Columns names
    public static final String KEY_PROPOSAL_ID = "proposal_id";
    public static final String KEY_PROPOSAL_NAME = "proposal_name";
    public static final String KEY_START_DATE = "start_date";
    public static final String KEY_END_DATE = "end_date";
    public static final String KEY_LAST_MODIFIED = "last_modified";

    // member variables
    public String proposalId;
    public String proposalName;
    public String startDate;
    public String endDate;
    public String lastModified;

    public ProposalTable(Context context){
            this.context = context;
    }

    public static String getCreateTableCommand(){
        return "CREATE TABLE " + TABLE_NAME + "("
                + KEY_PROPOSAL_ID + " TEXT," + KEY_PROPOSAL_NAME + " TEXT,"
                + KEY_START_DATE + " TEXT," + KEY_END_DATE + " TEXT," + KEY_LAST_MODIFIED + " TEXT," +
                " TEXT, PRIMARY KEY("+ KEY_PROPOSAL_ID +") )";
    }
    public static String getTableName(){
        return TABLE_NAME;
    }

    public ProposalTable(String proposalId, String proposalName, String startDate, String  endDate, String lastModified) {

         this.proposalId = proposalId;
         this.startDate = startDate;
         this.endDate =  endDate;
         this.lastModified = lastModified;
         this.proposalName = proposalName;
    }

    public String getProposalId(){
        return proposalId;
    }
    public String getProposalName(){
        return proposalName;
    }
    public String getStartDate(){
        return startDate;
    }
    public String getEndDate() {
        return endDate;
    }
    public String lastModified() {
        return lastModified;
    }

    public  static void insertBulk(DataBaseHandler db_handle, List<ProposalTable> proposalData){
        /*
            This functions handles Bulk insert.
        */

        SQLiteDatabase db = db_handle.getWritableDatabase();
        // drop if exist. we don't override data
        db.execSQL("DROP TABLE IF EXISTS " + ProposalTable.getTableName());
        // create afresh !
        db.execSQL(ProposalTable.getCreateTableCommand());

        db.beginTransaction();

        try {
            ContentValues values = new ContentValues();
            for ( ProposalTable instance: proposalData) {

                values.put(KEY_PROPOSAL_ID, instance.getProposalId());
                values.put(KEY_PROPOSAL_NAME, instance.getProposalName());
                values.put(KEY_START_DATE, instance.getStartDate());
                values.put(KEY_END_DATE, instance.getEndDate());
                values.put(KEY_LAST_MODIFIED, instance.lastModified());

                db.insert(ProposalTable.getTableName(), null, values);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();

        }

    }

    // called in auditor dashboard acticity
    // fetches data from the internet and update the table
    public void insertDataFromInternet(){

        GetProposalData getProposalData = new GetProposalData();
        getProposalData.execute();

    }


    // AsyncTaks that connects to internet get the JSON and and the parse it and insert it in table
    public class GetProposalData extends AsyncTask<Void,Void,Void> {

        private HttpURLConnection connection;
        private StringBuffer response;
        private String jsonResult;

        // list that contains the data for the table
        // passed to the bulkInsert function
        private List<ProposalTable> mProposalList = new ArrayList<>();

        // gets the JSON response from the internet
        @Override
        protected Void doInBackground(Void... voids) {

            Log.e("In prop Asyntask","Do in Background");

            try {

                // get proposal data url
                String url = Constants.GET_PROPOSAL_DATA;
                URL urlInstance = new URL(url);

                String authToken = Constants.AUTH_TOKEN;
                connection = (HttpURLConnection) urlInstance.openConnection();
                connection.setRequestProperty("Authorization", authToken);


                if ( connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    InputStream in = new BufferedInputStream(connection.getInputStream());

                    response = Utils.ConvertInputStreamToStringBuffer(in);

                    if ( response != null ) {
                        jsonResult = response.toString();
                        Log.e("In prop Asyntask",jsonResult);

                        //assignedFragment.setJsonResult(response.toString());
                    }
                    else{
                        jsonResult = null;
                        //assignedFragment.setJsonResult(null);
                    }
                }
                else{

                    InputStream in =  new BufferedInputStream(connection.getErrorStream());
                    StringBuffer errorResponse = Utils.ConvertInputStreamToStringBuffer(in);

                    if ( errorResponse == null ){
                        Log.e("ProposalTableAsynTask", "fetching from input stream resulted in exception");
                    }
                    else {
                        Log.e("ProposalTableAsynTask ", "error responsse  "+errorResponse.toString());
                    }
                }


            } catch (Exception  e) {
                Log.e("ProposalTableAsynTask", "Error "+ e.getMessage());
            } finally{

                if ( connection != null ){
                    Log.e("ProposalTableAsynTask", "disconnecting from " + Constants.GET_PROPOSAL_DATA);
                    connection.disconnect();
                }

            }


            return null;
        }


        // after getting the JSON response
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //parse and insert data

            parseJasonAndInsert();
        }

        public void parseJasonAndInsert(){

            try {

                if (jsonResult == null) {
                    Toast.makeText( context, "No response from server due to internet", Toast.LENGTH_SHORT).show();

                } else {
                    JSONObject jsonResponse = new JSONObject(jsonResult);
                    Log.e("JSON Array", String.valueOf(jsonResponse));

                    JSONArray data = (JSONArray)jsonResponse.get("data");

                    //iterate through the array of objects
                    // and get the details
                    for (int i=0; i< data.length();i++){

                        JSONObject jsonobject = data.getJSONObject(i);
                        String proposalId =  jsonobject.getString("proposal_id");
                        String proposalName = jsonobject.getString("name");
                        String startDate = jsonobject.getString("tentative_start_date");
                        String endDate = jsonobject.getString("tentative_end_date");

                        //last modified set to null
                        ProposalTable proposalTable = new ProposalTable(proposalId,proposalName,startDate,endDate,null);
                        mProposalList.add(proposalTable);

                    }

                    DataBaseHandler db_handle = DataBaseHandler.getInstance(context);
                    //insert in database
                    insertBulk(db_handle,mProposalList);

                }





            }
            catch (JSONException e){
                e.printStackTrace();
            }
        }

  }//ASyncTask ends


}//class ends
