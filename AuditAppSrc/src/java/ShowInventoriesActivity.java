package com.machadalo.audit;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Toast;

import com.machadalo.audit.sqllite.DataBaseHandler;
import com.machadalo.audit.sqllite.InventoryActivityAssignmentTable;
import com.machadalo.audit.sqllite.InventoryActivityTable;
import com.machadalo.audit.sqllite.ShortlistedInventoryDetailsTable;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShowInventoriesActivity extends AppCompatActivity {

    String shortlistedSpacesId;
    String supplierName;
    String supplierLatitude;
    String supplierLongitude;
    String supplierAddress;
    String supplierId;
    String selectedDate;        //this is current date called from assignment fragment to util.currentdatestring

    List<ShortlistedInventoryDetailsTable> shortlistedInventoryDetailsData = new ArrayList<>();
    static final int REQUEST_IMAGE_CAPTURE = 1;
    String mCurrentPhotoPath;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_inventories);

        final Bundle receivedData = getIntent().getExtras();

        shortlistedSpacesId = receivedData.getString("shortlistedSpacesId");
        supplierId = receivedData.getString("supplierId");
        supplierName = receivedData.getString("supplierName");
        supplierAddress = receivedData.getString("supplierAddress");
        supplierLatitude = receivedData.getString("supplierLatitude");
        supplierLongitude = receivedData.getString("supplierLongitude");
        selectedDate = receivedData.getString("selectedDate");
        Log.d("ShowInvAct", "Date recieved is" + selectedDate);
        //shortlistedInventoryDetailsData = ShortlistedInventoryDetailsTable.getShortlistedInventories(DataBaseHandler.getInstance(getBaseContext()), shortlistedSpacesId);
       // shortlistedInventoryDetailsData = ShortlistedInventoryDetailsTable.getShortlistedInventoriesPerDay(DataBaseHandler.getInstance(getBaseContext()), shortlistedSpacesId, selectedDate);
        getShortlistedInventoriesPerDay(DataBaseHandler.getInstance(getBaseContext()), shortlistedSpacesId, selectedDate);

        if (shortlistedInventoryDetailsData.size() == 0) {
            Log.d("ShowInvAct", "No inventories found");
            Toast.makeText(getApplicationContext(), "NO inventories are shortlisted for this supplier", Toast.LENGTH_LONG).show();
            finish();
        }
        Log.d("ShowInvAct", "found some inventories " + shortlistedInventoryDetailsData.size());

        recyclerView = (RecyclerView)findViewById(R.id.show_inventories_recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new ShowInventoryAdapter(ShowInventoriesActivity.this,shortlistedInventoryDetailsData,  this, supplierLatitude, supplierLongitude, supplierName, supplierAddress, selectedDate);
        recyclerView.setAdapter(mAdapter);

    }



    @Override
    protected void onResume() {
        super.onResume();
        shortlistedInventoryDetailsData.clear();
       getShortlistedInventoriesPerDay(DataBaseHandler.getInstance(getBaseContext()), shortlistedSpacesId, selectedDate);
       Log.e("showInveAct",shortlistedInventoryDetailsData.size()+ " value");
       mAdapter.notifyDataSetChanged();
    }


    public void  getShortlistedInventoriesPerDay( DataBaseHandler db_handle,   String shortlistedSupplierId, String requestedDate) {
        /*
        The idea of this function is to fetch all shortlistedInventoryDetails object which have
        either RELEASE, AUDIT or CLOSURE on the given date.
         */

       // List<ShortlistedInventoryDetailsTable> data = new ArrayList<>();
        SQLiteDatabase db = db_handle.getReadableDatabase();

        Log.d("ShortlistedSpacedId", shortlistedSupplierId);

        String query = "select a.id, a.inventory_content_type_id, a.inventory_id, a.inventory_name, " +
                " b.activity_type, " +
                " c.activity_date " +
                " from " + ShortlistedInventoryDetailsTable.getTableName() + " as a " +
                " INNER JOIN " + InventoryActivityTable.getTableName() + " as b "  +
                " ON a.id = b.shortlisted_inventory_detail_id " +
                " INNER JOIN " + InventoryActivityAssignmentTable.getTableName() + " as c " +
                " ON b.id = c.inventory_activity_id "  +
                " WHERE a.shortlisted_spaces_id = '" + shortlistedSupplierId + "'" +
                " AND c.activity_date = '" + requestedDate + "'";


        Cursor cursor = db.rawQuery(query, null);

        Log.d("QueryShortLisInvDetTab", query);

        Log.d("QuerCount",  Integer.toString(cursor.getCount()));

        try {

            Map<String, ShortlistedInventoryDetailsTable> instanceHashMap = new HashMap<>();
            Map<String, List<String>> validActsHashMap = new HashMap<>();


            if (cursor.moveToFirst()) {
                do {

                    List<String> validActs;
                    ShortlistedInventoryDetailsTable instance;

                    String inventoryId = cursor.getString(cursor.getColumnIndex(ShortlistedInventoryDetailsTable.KEY_INVENTORY_ID));
                    String inventoryContentTypeId = cursor.getString(cursor.getColumnIndex(ShortlistedInventoryDetailsTable.KEY_INVENTORY_CONTENT_TYPE_ID));
                    String inventoryName = cursor.getString(cursor.getColumnIndex(ShortlistedInventoryDetailsTable.KEY_INVENTORY_NAME));
                    String shortlistedInventoryDetailsId = cursor.getString(cursor.getColumnIndex(ShortlistedInventoryDetailsTable.KEY_SHORTLISTED_INVENTORY_DETAILS_ID));
                    String inventoryActivityType = cursor.getString(cursor.getColumnIndex(InventoryActivityTable.getKeyActivityType()));

                    if ( instanceHashMap.get(shortlistedInventoryDetailsId) == null) {
                        instance = new ShortlistedInventoryDetailsTable(shortlistedInventoryDetailsId, shortlistedSupplierId, inventoryId, inventoryContentTypeId, inventoryName);
                        instanceHashMap.put(shortlistedInventoryDetailsId, instance);
                        shortlistedInventoryDetailsData.add(instance);
                    }
                    else {
                        instance = instanceHashMap.get(shortlistedInventoryDetailsId);

                    }
                    if ( validActsHashMap.get(shortlistedInventoryDetailsId) == null ) {

                        validActsHashMap.put(shortlistedInventoryDetailsId, new ArrayList<String>());
                        validActs = validActsHashMap.get(shortlistedInventoryDetailsId);
                        validActs.add(inventoryActivityType);

                    }
                    else{
                        validActs = validActsHashMap.get(shortlistedInventoryDetailsId);
                        validActs.add(inventoryActivityType);
                    }

                    if ( instance != null ) {
                        instance.setValidActivities(validActs);
                    }


                } while (cursor.moveToNext());
            }
        } finally {
            cursor.close();
        }

    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // on clicking the refresh button,  finish the current activity so that user lands on
        // the parent fragment
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            default:
        }
        return super.onOptionsItemSelected(item);
    }

}
