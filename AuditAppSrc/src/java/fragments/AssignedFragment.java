package com.machadalo.audit.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.machadalo.audit.AssignedFragmentAdapter;
import com.machadalo.audit.AsyncTasks.GetCampaignSuppliersInventories;
import com.machadalo.audit.Constants;
import com.machadalo.audit.Messages;
import com.machadalo.audit.R;
import com.machadalo.audit.Services.FetchLocationUploadImage;
import com.machadalo.audit.ShowInventoriesActivity;
import com.machadalo.audit.Utils;
import com.machadalo.audit.ViewImagesActivity;
import com.machadalo.audit.adapter.DataAdapter;
import com.machadalo.audit.extras.Data;
import com.machadalo.audit.sqllite.BasicSupplierTable;
import com.machadalo.audit.sqllite.ContactsTable;
import com.machadalo.audit.sqllite.DataBaseHandler;
import com.machadalo.audit.sqllite.InventoryActivityAssignmentTable;
import com.machadalo.audit.sqllite.InventoryActivityTable;
import com.machadalo.audit.sqllite.ProposalTable;
import com.machadalo.audit.sqllite.ShortlistedInventoryDetailsTable;
import com.machadalo.audit.sqllite.ShortlistedSuppliersTable;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;


public class AssignedFragment extends Fragment {
    /*Main Variable from Assighned*/
    private String jsonResult;

    public final static String API_KEY = "AIzaSyB4h9ZPC_78m80902WtlZXFKmXVEVNZb0o";
    public String mapUrl;

    // flag for Internet connection status
    Boolean isInternetPresent = true;
    /*Main Variable from Assighned*/
    private View myFragmentView;
    private RecyclerView recyclerView;
    private DataAdapter mAdapter;
    private List<Data> dataList = new ArrayList<>();
    private String[] days = new String[]{Constants.Today, Constants.Tomorrow, Constants.DayAfterTomorrow};
    private String TAG = this.getClass().getSimpleName().substring(0, 5);
    private  String selectedDate = Utils.getCurrentDateString(); // default date is current Date
    private Spinner spinner;
    private Button SortButton;



    List<ProposalTable> proposalData = new ArrayList<>();
    List<BasicSupplierTable> basicSupplierData = new ArrayList<>();
    List<ShortlistedSuppliersTable> shortlistedSuppliersData  = new ArrayList<>();
    List<ShortlistedInventoryDetailsTable> shortlistedInventoryDetailsData = new ArrayList<>();
    List<InventoryActivityTable>inventoryActivityData = new ArrayList<>();
    List<InventoryActivityAssignmentTable>inventoryActivityAssignmentData = new ArrayList<>();
    List<ContentValues>contactTableEntryList = new ArrayList<>();

    String supplier_id,supplier_name,supplier_address1,supplier_address2;

    //for showing count in th title of tab
    AssignedFragmentAdapter assignedFragmentAdapter;

    public void setAssignedFragmentAdapter(AssignedFragmentAdapter as){
        this.assignedFragmentAdapter = as;
    }

    public AssignedFragment() {
        // Required empty public constructor
    }

    public void setJsonResult( String jsonResult ) {
        this.jsonResult = jsonResult;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = null;
        myFragmentView = inflater.inflate(R.layout.fragment_one, container, false);
        SortButton = (Button) myFragmentView.findViewById(R.id.assignedSortButton);

        try {
            spinner = (Spinner) myFragmentView.findViewById(R.id.day_drop_down);

            ArrayAdapter<String>adapter = new ArrayAdapter<String>(getContext(),
                    android.R.layout.simple_spinner_item,days);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view,
                                           int position, long id) {


                    selectedDate = getDateOnGivenDay(parent.getSelectedItem().toString());
                    DataBaseHandler db_handle = DataBaseHandler.getInstance(getContext());
                    JSONObject mainNode = ShortlistedSuppliersTable.getShortlistedSuppliersPerDay(db_handle, selectedDate);
                    Log.d("Day is selected", " showing supplier data for " + selectedDate) ;
                    showSuppliersData(mainNode);

                    assignedFragmentAdapter.setAssignedCount(getAssignedCount(selectedDate));
                    assignedFragmentAdapter.notifyDataSetChanged();

                    Toast.makeText(getContext(), "Selected Day is " + parent.getItemAtPosition(position).toString(),
                            Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // TODO Auto-generated method stub
                }
            });
        }
        catch (Exception e){
            Log.e("error",e.getMessage());
        }

        // use this setting to improve performance if you know that changes

        // in content do not change the layout size of the RecyclerView
        recyclerView = (RecyclerView) myFragmentView.findViewById(R.id.recycler_view);

        mAdapter = new DataAdapter(getContext(),dataList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        if (!prefs.getBoolean("firstTime_One", false)) {
            // <---- run your one time code here
            accessWebService();
            // mark first time has runned.
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("firstTime_One", true);
            editor.commit();
        }

        /*recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {

                // we launch ShowInventoriesActivity class from here on each click.
                Data data = dataList.get(position);
                Intent intent = new Intent(getContext(), ShowInventoriesActivity.class);

                selectedDate = getDateOnGivenDay(spinner.getSelectedItem().toString());

                intent.putExtra("shortlistedSpacesId", data.getShortlistedSpacesId());
                intent.putExtra("supplierName", data.getSupplierName());
                intent.putExtra("supplierId", data.getSupplierName());
                intent.putExtra("supplierLongitude", data.getLongitude());
                intent.putExtra("supplierLatitude", data.getLatitude());
                intent.putExtra("supplierAddress", data.getSupplierAddress1() + " " + data.getSupplierAddress2());
                intent.putExtra("selectedDate", selectedDate);
                Log.d(TAG, "selected date is before calling inventory activity" + selectedDate);

                Log.d("AssignedFrag", data.getShortlistedSpacesId());
                startActivity(intent);

              *//*  Intent intent = new Intent(getContext(), AssignedActivity.class);
                intent.putExtra("address", dataList.get(position).getSocietyAddress());
                intent.putExtra("society", dataList.get(position).getsocietyName());
                intent.putExtra("id", dataList.get(position).getadID());
                intent.putExtra("location", "Chennai");
                intent.putExtra("date", dataList.get(position).getDate());
                startActivity(intent);*//*
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));*/


        SortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sortButton();
            }
        });

        // Inflate the layout for this fragment
        return myFragmentView;
    }

    public void accessWebService() { // helper function to run jason task

        dataList.clear();

        GetCampaignSuppliersInventories getCampaignSuppliersInventories = new GetCampaignSuppliersInventories(getContext(), this);
        Log.d(TAG, "Making api call");
        getCampaignSuppliersInventories.execute(Constants.LIST_CAMPAIGN_SUPPLIERS_URL);

    }

    public String getDateOnGivenDay(String day) {

        String selectedDate;

        switch (day) {

            case Constants.Today:

                selectedDate = Utils.getCurrentDateString();
                Log.d(Constants.Today, selectedDate);
                break;

            case Constants.Tomorrow:

                selectedDate = Utils.addDays(1);
                Log.d(Constants.Tomorrow, selectedDate);
                break;

            case Constants.DayAfterTomorrow:

                selectedDate = Utils.addDays(2);
                Log.d(Constants.DayAfterTomorrow, selectedDate);
                break;

            default:
                selectedDate = Utils.getCurrentDateString();

        }
        return selectedDate;
    }

    public void ListDrawer() {
        try {
            // check for Internet status
            if (isInternetPresent) {
                // Internet Connection is Present
                // make HTTP requests
                if (jsonResult == null) {

                    if (getActivity()!= null) {
                        Toast.makeText(getActivity(), "No response from server due to internet", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    JSONObject jsonResponse = new JSONObject(jsonResult);
                    Log.e("JSON Array", String.valueOf(jsonResponse));

                    JSONObject jsonMainNode;
                    jsonMainNode = jsonResponse.optJSONObject("data");// assign is our json object name in mysql database

                    insertIntoDb(jsonMainNode);

                    if (jsonMainNode == null || jsonMainNode.length() == 0) {
                        // No suppliers scheduled
                        Utils.displayToastMessage(getContext(), Messages.NO_SUPPLIERS_SCHEDULED_MESSAGE, Toast.LENGTH_LONG);
                    } else {
                        //
                        selectedDate = getDateOnGivenDay(spinner.getSelectedItem().toString());
                        DataBaseHandler db_handle = DataBaseHandler.getInstance(getContext());
                        JSONObject mainNode = ShortlistedSuppliersTable.getShortlistedSuppliersPerDay(db_handle, selectedDate);
                        Log.d(TAG, " automatic showing supplier data for " + selectedDate) ;
                        showSuppliersData(mainNode);
                    }
                }
            } else {
                // Internet connection is not present
                // Ask user to connect to Internet
                showAlertDialog(getContext(), "No Internet Connection",
                        "You don't have internet connection.", false);
            }

        } catch (JSONException e) {
            Toast.makeText(getContext(), "Error" + e.toString(),
                    Toast.LENGTH_SHORT).show();
            Log.e("Json Exception", String.valueOf(e));
        }


    }

    public void showSuppliersData(JSONObject jsonMainNode) {
        // clear everything.
        dataList.clear();


        try {

            JSONObject shortlistedSuppliers = jsonMainNode.getJSONObject("shortlisted_suppliers");
            Iterator<?> shortlistedSupplierKeys = shortlistedSuppliers.keys();

            while (shortlistedSupplierKeys.hasNext()) {

                String shortlistedSupplierId = (String) shortlistedSupplierKeys.next();
                if (shortlistedSuppliers.get(shortlistedSupplierId) instanceof JSONObject) {

                    JSONObject shortlistedSupplier = (JSONObject) shortlistedSuppliers.get(shortlistedSupplierId);
                    String proposalId = shortlistedSupplier.getString("proposal_id");
                    String proposalName = shortlistedSupplier.getString("proposal_name");
                    Log.e("Assigned Fragment",proposalName);

                    ShortlistedSuppliersTable shortlistedSupplierObject =
                            getShortlistedSupplierData(shortlistedSupplier, shortlistedSupplierId);

                    if (shortlistedSupplierObject == null)
                        continue;

                    // work for BasicSupplier Data
                    JSONObject supplierDetail = shortlistedSupplier.getJSONObject("supplier_detail");
                    if (supplierDetail != null) {

                        supplier_id = supplierDetail.getString("supplier_id");
                        supplier_name = supplierDetail.getString("name");
                        supplier_address1 = supplierDetail.getString("address1");
                        supplier_address2 = supplierDetail.getString("address2");
                        String lat = supplierDetail.getString("latitude");
                        String lon = supplierDetail.getString("longitude");

                      //  Log.e("In AssFrag ",supplierDetail.toString());

                        prepareDataData(proposalName, supplier_id, supplier_name, supplier_address1, supplier_address2, Constants.LIST_CAMPAIGN_SUPPLIERS_URL, shortlistedSupplierId,lat,lon);

                    }
                }
            }

        } catch (Exception e) {
            Log.d(TAG, e.getMessage()) ;
        }
    }

    public void showAlertDialog(Context context, String title, String message, Boolean status) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        // Setting Dialog Title
        alertDialog.setTitle(title);

        // Setting Dialog Message
        alertDialog.setMessage(message);

        // Setting alert dialog icon
      /*  alertDialog.setIcon((status) ? R.drawable.material_drawer_circle_mask : R.drawable.material_drawer_circle_mask);

       */ // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS);
                ComponentName cName = new ComponentName("com.android.phone", "com.android.phone.Settings");
                intent.setComponent(cName);
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    private void prepareDataData(String campaignName, String supplierID, String supplierName,  String supplierAddress1, String supplierAddress2, String url, String shortlistedSpacesId,String lat,String lon) {
        //TODO add lat lon in Data for society
        Data data = new Data(campaignName, supplierID, supplierName, supplierAddress1, supplierAddress2, url,lat,lon);
        selectedDate = getDateOnGivenDay(spinner.getSelectedItem().toString());
        data.setShortlistedSpacesId(shortlistedSpacesId);
        data.setSelectedDate(selectedDate);
        dataList.add(data);
        mAdapter.notifyDataSetChanged();
    }


    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private AssignedFragment.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final AssignedFragment.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

    public void insertIntoDb(JSONObject data) {

        // collect all data incoming, insert them in bulk here.
        // clear all data if any before inserting
        shortlistedSuppliersData.clear();
        shortlistedInventoryDetailsData.clear();
        basicSupplierData.clear();
        inventoryActivityAssignmentData.clear();
        inventoryActivityData.clear();

        if ( data == null)
            return;

        try {
            // handling shortlisted suppliers here
            JSONObject shortlistedSuppliers = data.getJSONObject("shortlisted_suppliers");
            Iterator<?> shortlistedSupplierKeys = shortlistedSuppliers.keys();

            while ( shortlistedSupplierKeys.hasNext() ) {

                String shortlistedSupplierId = (String)shortlistedSupplierKeys.next();
                if ( shortlistedSuppliers.get(shortlistedSupplierId) instanceof JSONObject ) {

                    JSONObject shortlistedSupplier = (JSONObject)shortlistedSuppliers.get(shortlistedSupplierId);
                    String supplierContentType = shortlistedSupplier.getString("content_type_id");

                    ShortlistedSuppliersTable shortlistedSupplierObject =
                            getShortlistedSupplierData(shortlistedSupplier, shortlistedSupplierId);

                    if (shortlistedSupplierObject == null)
                        continue;

                    // work for BasicSupplier Data
                    JSONObject supplierDetail = shortlistedSupplier.getJSONObject("supplier_detail");
                    BasicSupplierTable instance = getBasicSupplierData(supplierDetail, supplierContentType);
                    if ( instance != null ) {
                        basicSupplierData.add(instance);
                    }

                    //Log.e("In assigned Frag",supplierDetail.toString());
                    contactTableEntryList = ContactsTable.getContactDetails(supplierDetail);

                    shortlistedSuppliersData.add(shortlistedSupplierObject);
                }
            }

            // handling shortlisted inventories here
            JSONObject shortlistedInventories = data.getJSONObject("shortlisted_inventories");
            Iterator<?> shortlistedInventoryKeys = shortlistedInventories.keys();

            while ( shortlistedInventoryKeys.hasNext()   ) {

                String shortlistedInventoryId = (String)shortlistedInventoryKeys.next();
                JSONObject shortlistedInventory = (JSONObject)shortlistedInventories.get(shortlistedInventoryId);

                ShortlistedInventoryDetailsTable instance = getShortlistedInventoryDetailsTableInstance(shortlistedInventory, shortlistedInventoryId);
                if ( instance == null)
                        continue;
                shortlistedInventoryDetailsData.add(instance);
            }

            // handle inventory activities
            JSONObject inventoryActivities = data.getJSONObject("inventory_activities");
            Iterator<?> inventoryActivityKeys = inventoryActivities.keys();

            while ( inventoryActivityKeys.hasNext() )  {

                String inventoryActivityId = (String)inventoryActivityKeys.next();
                JSONObject inventoryActivity = (JSONObject)inventoryActivities.get(inventoryActivityId);

                InventoryActivityTable inventoryActivityTable = getInventoryActivityInstance(inventoryActivity, inventoryActivityId);
                if (inventoryActivityTable == null)
                    continue;
                inventoryActivityData.add(inventoryActivityTable);
            }

            // handling inventory activity assignment here

            JSONObject inventoryActivityAssignment = data.getJSONObject("inventory_activity_assignment");
            Iterator<?> inventoryActivityAssignmentKeys = inventoryActivityAssignment.keys();

            while(  inventoryActivityAssignmentKeys.hasNext()  ) {

                String inventoryActivityAssignmentId = (String)inventoryActivityAssignmentKeys.next();
                JSONObject inventoryActivityAssignmentObject = (JSONObject)inventoryActivityAssignment.get(inventoryActivityAssignmentId);

                InventoryActivityAssignmentTable instance = getInventoryActivityAssignmentInstance(inventoryActivityAssignmentObject, inventoryActivityAssignmentId);
                if ( instance == null)
                    continue;
                inventoryActivityAssignmentData.add(instance);

            }
        }
        catch (Exception e) {
            Log.d("insertIntoDb", e.getMessage());
        }

        DataBaseHandler db = DataBaseHandler.getInstance(getContext());
        try {
            // bulk insert them all.

            ShortlistedSuppliersTable.insertBulk(db, shortlistedSuppliersData);
            ShortlistedInventoryDetailsTable.insertBulk(db, shortlistedInventoryDetailsData);
            BasicSupplierTable.insertBulk(db, basicSupplierData);
            InventoryActivityTable.insertBulk(db, inventoryActivityData);
            InventoryActivityAssignmentTable.insertBulk(db, inventoryActivityAssignmentData);
            ContactsTable.insertBulk(db,contactTableEntryList);

            Log.d("ShortlistedSuppTable",     Integer.toString( db.getTotalCount(ShortlistedSuppliersTable.getTableName())));
            Log.d("shortInvDetail",  Integer.toString( db.getTotalCount(ShortlistedInventoryDetailsTable.getTableName())))  ;
            Log.d("BasicSuppData",  Integer.toString(db.getTotalCount(BasicSupplierTable.getTableName())))  ;
            Log.d("InvActivity",  Integer.toString(db.getTotalCount(InventoryActivityTable.getTableName()))) ;
            Log.d("InvActivityAssignment",  Integer.toString(db.getTotalCount(InventoryActivityAssignmentTable.getTableName()))) ;

        }
        catch (Exception e) {
            Log.d("insertIntoDb", e.getMessage());
        }
        finally {

        }
    }

    public ShortlistedInventoryDetailsTable getShortlistedInventoryDetailsTableInstance(JSONObject shortlistedInventory, String shortlistedInventoryDetailsID) {
        // returns an instance of ShortlistedInventoryDetails table
       try {

           if ( shortlistedInventory != null ) {
               String inventoryId = shortlistedInventory.getString("inventory_id");
               String inventoryContentTypeId = shortlistedInventory.getString("inventory_content_type_id");
               String inventoryName = shortlistedInventory.getString("inventory_name");
               String shortlistedSpacesId = shortlistedInventory.getString("shortlisted_spaces_id");
               return new ShortlistedInventoryDetailsTable(shortlistedInventoryDetailsID, shortlistedSpacesId, inventoryId, inventoryContentTypeId, inventoryName);
           }
       }
       catch (Exception e) {

           Log.d("getShortlisdInvtoryData", e.getMessage());
       }
        return null;
    }

    public String getCurrentDate(){

        return new SimpleDateFormat("yyyy-MM-dd").format(new Date());

    }

    public InventoryActivityAssignmentTable getInventoryActivityAssignmentInstance( JSONObject inventoryActivityAssignment, String inventoryActivityAssignmentId) {
        try{

            InventoryActivityAssignmentTable instance = new InventoryActivityAssignmentTable();
            instance.setInventoryActivityId(inventoryActivityAssignment.getString("inventory_activity_id"));
            instance.setActivityDate(inventoryActivityAssignment.getString("activity_date"));
            instance.setInventoryReassignedActivityDate(inventoryActivityAssignment.getString("reassigned_activity_date"));
            instance.setInventoryActivityAssignmentId(inventoryActivityAssignmentId);
            return instance;

        }
        catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
        return null;
    }

    public InventoryActivityTable getInventoryActivityInstance( JSONObject inventoryActivity, String inventoryActivityId) {
        try {
            InventoryActivityTable instance = new InventoryActivityTable();
            instance.setInventoryActivityId(inventoryActivityId);
            instance.setActivityType(inventoryActivity.getString("activity_type"));
            instance.setShortlistedInventoryDetailId(inventoryActivity.getString("shortlisted_inventory_id"));
            return instance;
        }
        catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
        return null;
    }
    public ShortlistedSuppliersTable getShortlistedSupplierData(JSONObject shortlistedSupplier, String shortlistedSupplierId) {
        // returns an instance of ShortlistedSuppliersTable
        try {
            if ( shortlistedSupplier != null) {

                String proposalId = shortlistedSupplier.getString("proposal_id");
                String supplierId = shortlistedSupplier.getString("supplier_id");
                String supplierContentTypeId = shortlistedSupplier.getString("content_type_id");
                //String phase = shortlistedSupplier.getString("phase");
                String phase = "1";  // TODO: 23/2/17 hardcoded. remove it
                String lastModified = getCurrentDate();
                return new ShortlistedSuppliersTable(shortlistedSupplierId, proposalId, supplierId, supplierContentTypeId, phase, lastModified);
            }
        }
        catch (Exception e){
            Log.d("getShortlistSuppData", e.getMessage());
        }
        return null;
    }

    public BasicSupplierTable getBasicSupplierData( JSONObject basicSupplierData, String supplierContentType) {

        if ( basicSupplierData.length() == 0)
            return null;
        try{
            String name = basicSupplierData.getString("name");
            String latitude = basicSupplierData.getString("latitude");
            String longitude = basicSupplierData.getString("longitude");
            String address = basicSupplierData.getString("address1");
            String supplierId = basicSupplierData.getString("supplier_id");
            String lastModified = Utils.getCurrentDateString();
            return new BasicSupplierTable(supplierId, supplierContentType, "", latitude, longitude, address, lastModified, name);
        }
        catch (Exception e) {
            Log.d("BasicSupplierData", e.getMessage());
        }

        return null;
    }


    public int getAssignedCount(String selectedDate){
        int count = 0;

        String Query = "SELECT * FROM "+InventoryActivityAssignmentTable.TABLE_NAME + " WHERE "
                + InventoryActivityAssignmentTable.KEY_INVENTORY_ACTIVITY_DATE + " = \'"+selectedDate+"\';";

        DataBaseHandler db_handle = DataBaseHandler.getInstance(getActivity());
        SQLiteDatabase db = db_handle.getReadableDatabase();

        Cursor cursor = db.rawQuery(Query,null);

        while (cursor.moveToNext()){
            count++;
        }
      //  Log.e("In AssignedFrag : ",String.valueOf(count));
        return count;

    }

    public void sortButton(){

        Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                Bundle reply = msg.getData();
                String replyString = reply.getString("msg");
                Log.e("Inside the Handle cap",replyString);

                String lat = reply.getString("lat");
                String lon = reply.getString("lon");

                Log.e("HandleCap",lat+" "+lon);

                if (lat!=null && lon !=null){
                    Sort(lat,lon);
                }

                //Toast.makeText(getApplicationContext(),replyString,Toast.LENGTH_LONG).show();
               // sendMsgToUpdateUi();
            }
        };

        String mode = "onlyLocation";
        Intent location = new Intent(getActivity(), FetchLocationUploadImage.class);
        location.putExtra("msg",new Messenger(handler));
        location.putExtra("mode",mode);
        getActivity().startService(location);



    }


    public void Sort(String currentLat, String currentLon){


        double cLat = Double.parseDouble(currentLat);
        double cLon = Double.parseDouble(currentLon);
       // Log.e("AssignFrag","inside Sort");



        for (int i=0;i<dataList.size();i++){

            double Dlat = Double.parseDouble(dataList.get(i).getLatitude());
            double Dlon = Double.parseDouble(dataList.get(i).getLongitude());
//            double distance = distance(Dlat,Dlon,cLat,cLon);
//            Log.e("Distance",String.valueOf(distance));
//            dataList.get(i).distance = distance*1000;

            String uri = "http://maps.google.com/maps?saddr=" + cLat +","+cLon+"&daddr="+Dlat+","+Dlon;
            dataList.get(i).setMapUri(uri);

        }

        getDistance(String.valueOf(cLat),String.valueOf(cLon));

//        Data[] array = new Data[ dataList.size()];
//        array = dataList.toArray(array);



    }

    //return distance in km
    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    public void getDistance(String cLat,String cLon){
         mapUrl = "https://maps.googleapis.com/maps/api/distancematrix/json?units=metric&origins="+cLat+","+cLon+"&destinations=";

        for (int i=0;i<dataList.size();i++){

            if (i == dataList.size()-1)
                mapUrl+= dataList.get(i).getLatitude()+"%2C"+dataList.get(i).getLongitude();
            else
                mapUrl+= dataList.get(i).getLatitude()+"%2C"+dataList.get(i).getLongitude()+"%7C";

        }

        mapUrl+="6&key="+API_KEY;

        GetGoogleMatrixApi object = new GetGoogleMatrixApi();
        object.execute();

    }


    public class GetGoogleMatrixApi extends AsyncTask<Void,Void,Void>{

        public String mjsonresult;
        ProgressDialog mProgressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(getContext());
            mProgressDialog.setMessage("LOADING .....");
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpGet httpget = new HttpGet(mapUrl);

                HttpResponse response = httpclient.execute(httpget);

                if (response.getStatusLine().getStatusCode() == 200) {
                    mjsonresult = EntityUtils.toString(response.getEntity());
                    parseJson();
                  //  Log.e("Server response", mjsonresult);
                } else {
                    Log.e("Server response", "Failed to get server response");
                }

            }catch (IOException e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            for (int i=0;i<dataList.size()-1;i++){

                for (int j= 0; j< dataList.size()-i-1;j++){

                    //Log.e("for2",String.valueOf(.distance)+ " "+ array[j+1].distance +" "+Double.compare( array[j].distance,array[j+1].distance));

                    if ( Double.compare( dataList.get(j).distance,dataList.get(j+1).distance) > 0  )
                    {
                        //swap
                        Log.e("AssgnFrag","InsideSort");
                        Data temp = dataList.get(j);
                        //array[j] = array[j+1];
                        dataList.set(j,dataList.get(j+1));
                        //array[j+1]=temp;
                        dataList.set(j+1,temp);
                    }
                }

            }



            //dataList = new ArrayList<Data>(Arrays.asList(array));
//
//            for (int i=0;i<dataList.size();i++)
//                Log.e("listData",dataList.get(i).getSupplierName()+" "+dataList.get(i).distance);

            mAdapter.notifyDataSetChanged();


            mProgressDialog.dismiss();
        }

        public void parseJson(){
            try {
                JSONObject data = new JSONObject(mjsonresult);

                JSONArray row = data.getJSONArray("rows");

                for(int i=0;i<row.length();i++){
                    JSONObject obj = row.getJSONObject(i);
                    JSONArray elements = obj.getJSONArray("elements");

                    for (int j=0;j<elements.length();j++){

                        JSONObject object = elements.getJSONObject(j);
                        JSONObject distance = object.getJSONObject("distance");
                        int value = distance.getInt("value");
                        Log.e("parseJsonSort",String.valueOf(value));

                        dataList.get(j).distance = value;

                    }

                }


            }
            catch (JSONException e){
                e.printStackTrace();
            }

        }


    }



}
