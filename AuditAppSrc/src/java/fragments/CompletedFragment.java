package com.machadalo.audit.fragments;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.machadalo.audit.AssignedFragmentAdapter;
import com.machadalo.audit.R;
import com.machadalo.audit.Utils;
import com.machadalo.audit.adapter.PendingDataAdapter;
import com.machadalo.audit.adapter.SubmittedDataAdapter;
import com.machadalo.audit.extras.ConnectionDetector;
import com.machadalo.audit.extras.PendingData;
import com.machadalo.audit.extras.QueueData;
import com.machadalo.audit.sqllite.AuditGS;
import com.machadalo.audit.sqllite.DataBaseHandler;
import com.machadalo.audit.sqllite.PendingTable;

import java.util.ArrayList;
import java.util.List;


// Completed tab is inflated byt this
public class CompletedFragment extends Fragment {

    public CompletedFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    //List data for recyclerview
    public static List<PendingData> mCompletedList = new ArrayList<>();
    private static RecyclerView recyclerView;
    private  static PendingDataAdapter mAdapter;
    public static Context context ;


    // to update the count of completed tab
    static  AssignedFragmentAdapter assignedFragmentAdapter;

    public void setAssignedFragmentAdapter(AssignedFragmentAdapter as){
        assignedFragmentAdapter = as;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_three,container,false);
        recyclerView = (RecyclerView)rootView.findViewById(R.id.recycler_view);
        mAdapter = new PendingDataAdapter(getActivity(), mCompletedList,true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        context = getActivity();

        // prepare and add data in the list
        prepareCompletedData();

        return rootView;
    }

    @Override
    public void onResume() {

        super.onResume();
        prepareCompletedData();
    }

    // class method accessible from pending fragment
    static void  prepareCompletedData(){

        //delte previous data
        mCompletedList.clear();
        mAdapter.notifyDataSetChanged();

        /*
            query data from the Pending Table
         */

        DataBaseHandler db_handle = DataBaseHandler.getInstance(context);
        SQLiteDatabase db = db_handle.getReadableDatabase();

        //db_handle.printTotalRows(PendingTable.TABLE_NAME);

        String Query = "SELECT * FROM "+ PendingTable.TABLE_NAME + " WHERE "+ PendingTable.KEY_isCompleted + " = "+ "\'true\' ;";

        Cursor cursor = db.rawQuery(Query,null);

        while (cursor.moveToNext()){

            String proposalName = cursor.getString(cursor.getColumnIndex(PendingTable.KEY_PROPOSAL_NAME));
            String supplierID = cursor.getString(cursor.getColumnIndex(PendingTable.KEY_SUPPLIER_ID));
            String SupplierName = cursor.getString(cursor.getColumnIndex(PendingTable.KEY_SUPPLIER_NAME));
            String SupplierAdrs = cursor.getString(cursor.getColumnIndex(PendingTable.KEY_SUPPLIER_ADDRESS));
            String shortlistedInventoryDetailsID = cursor.getString(cursor.getColumnIndex(PendingTable.KEY_ShortlistedInventoryID));
            String inventoryID = cursor.getString(cursor.getColumnIndex(PendingTable.KEY_INVENTORY_ID));
            String inventoryType = cursor.getString(cursor.getColumnIndex(PendingTable.KEY_ACTIVTY_TYP));
            String inventoryName = cursor.getString(cursor.getColumnIndex(PendingTable.KEY_INVENTORY_TYP));
            String inventoryDate = cursor.getString(cursor.getColumnIndex(PendingTable.KEY_SCHEDULED_ACTIVITY_DATE));
            String proposalID = cursor.getString(cursor.getColumnIndex(PendingTable.KEY_PROPOSAL_ID));

            //get the pending counr
            String currentDate = Utils.getCurrentDateString();
           // String pendingFromDays = String.valueOf(getPendingDaysCount(currentDate,inventoryDate));
            Log.e("in Preaparedata",proposalID+" "+shortlistedInventoryDetailsID);
            PendingData pendingData = new PendingData(proposalID, supplierID, SupplierName, SupplierAdrs, inventoryID, inventoryName, inventoryType, proposalName, inventoryDate,"completed",shortlistedInventoryDetailsID);
            mCompletedList.add(pendingData);

        }

        cursor.close();
        mAdapter.notifyDataSetChanged();

        // update the count of the completed tab
        assignedFragmentAdapter.setCompletedCount(mCompletedList.size());
        assignedFragmentAdapter.notifyDataSetChanged();

    }// fn ends


    /****************--------OLD COMPLETED FRAGMENT CODE --------*****************/

/*

    ArrayList<String> pictures= new ArrayList<String>();//Array to store pictures
    ArrayList<String> locationarray= new ArrayList<String>();
    ArrayList<String> addressarray= new ArrayList<String>();
    ArrayList<String> idarray = new ArrayList<String>();
    ArrayList<String> submitStatusArray = new ArrayList<String>();
    ArrayList<String> inventDateArray = new ArrayList<String>();
    String getIntActivity;

    //TODO Change URL
    private String url = "http://machadalo.com/android/audit/media/submitted.php"; // our Url to server

    DataBaseHandler db;
    int element,total;
    // flag for Internet connection status
    Boolean isInternetPresent = true;
    ConnectionDetector cd;
    String strAdCount;
    int getIntentAct;
    */
/*Main Variable from Assighned*//*

    private View myFragmentView;
  //  private RecyclerView recyclerView;
   // private SubmittedDataAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<QueueData> submitted = new ArrayList<>();
    private List<AuditGS> queueData = new ArrayList<>();
    String lat;
    String lon;
*/


    /*
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = DataBaseHandler.getInstance(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = null;
        myFragmentView = inflater.inflate(R.layout.fragment_three, container, false);

        // use this setting to improve performance if you know that changes

        // in content do not change the layout size of the RecyclerView
        recyclerView = (RecyclerView) myFragmentView.findViewById(R.id.recycler_view);

        mAdapter = new SubmittedDataAdapter(submitted);
        *//*RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);*//*
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        if (!prefs.getBoolean("firstTime_Three", false)) {
            // <---- run your one time code here
            accessWebService();
            // mark first time has runned.
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("firstTime_Three", true);
            editor.commit();
        }
        return myFragmentView;
    }
    public void accessWebService() { // helper function to run jason task
        JsonReadTask task = new JsonReadTask();
        // passes values for the urls string array
        task.execute(new String[] { url });
    }
    private class JsonReadTask extends AsyncTask<String, Void, String> { // asynctask which runs on dif thread to parse json object from server
        @Override
        protected String doInBackground(String... params) {

            return null;
        }


        @Override
        protected void onPostExecute(String result) {
            ListDrwaer();
        }
    }// end async task
    public void ListDrwaer() {



        queueData = db.up_getAllSubmittedAudits();
        int size= queueData.size();
        Log.e("Submitted size", Integer.toString(size));

        int i=0;
        for ( i=0; i<queueData.size();i++) {
            prepareDataData(queueData.get(i).get_ad_inventory_ID(), queueData.get(i).get_society_address(), "Poster", queueData.get(i).get_society_address(), queueData.get(i).get_timestamp());


            Log.e("Inventory ID", queueData.get(i).get_ad_inventory_ID());
            Log.e("Society Address", queueData.get(i).get_society_address());
          *//*  Log.e("Maps", queueData.get(i).get_map());*//*
          *//*  Log.e("Date", queueData.get(i).get_date());*//*
          *//*  Log.e("Image url", queueData.get(i).get_image());*//*
            Log.e("Inventory Address", queueData.get(i).get_inventory_address());
          *//*  Log.e("Maps", queueData.get(i).get_map());*//*
        *//*    Log.e("Submit Status", queueData.get(i).get_submit_status());*//*
            Log.e("Time stamp", queueData.get(i).get_timestamp());
            Log.e("Date", queueData.get(i).get_date());
            Log.e("Image", queueData.get(i).get_image());


        }
    }

    *//*Framen Work*//*
    private void prepareDataData(String adID, String societyName, String adType,String societyAddress, String image) {
        QueueData quedata = new QueueData(adID, societyName, adType,societyAddress,image);
        submitted.add(quedata);
        mAdapter.notifyDataSetChanged();
    }

*/

}
