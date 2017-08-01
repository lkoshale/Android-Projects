package com.machadalo.audit.sqllite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.machadalo.audit.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by root on 30/1/17.
 */

public class ShortlistedInventoryDetailsTable {

    public static final String TABLE_NAME = "shortlisted_inventory_details";
    //  Table Columns names
    public static final String KEY_SHORTLISTED_INVENTORY_DETAILS_ID = "id";
    public static final String KEY_INVENTORY_ID = "inventory_id";
    public static final String KEY_INVENTORY_CONTENT_TYPE_ID = "inventory_content_type_id";
    public static final String KEY_INVENTORY_NAME = "inventory_name";
    public static final String KEY_SHORTLISTED_SPACES_ID = "shortlisted_spaces_id";
    public static final String KEY_LAST_MODIFIED = "last_modified";

    public String shortlistedInventoryDetailsID;
    public String inventoryId;
    public String inventoryContentTypeId;
    public String inventoryName;
    public String shortlistedSpacesId;
    public String lastModified;
    public List<String> validActivities;

    public String getShortlistedInventoryDetailsID(){
        return shortlistedInventoryDetailsID;
    }

    public static String getKeyInventoryId() {
        return KEY_INVENTORY_ID;
    }

    public void setValidActivity( String validActivity ) {
        if (validActivities != null) {
            validActivities.add(validActivity);
        }
    }


    public static String getKeyInventoryContentTypeId() {
        return KEY_INVENTORY_CONTENT_TYPE_ID;
    }

    public static String getKeyInventoryName() {
        return KEY_INVENTORY_NAME;
    }

    public static String getKeyShortlistedSpacesId() {
        return KEY_SHORTLISTED_SPACES_ID;
    }

    public static String getKeyLastModified() {
        return KEY_LAST_MODIFIED;
    }

    public void setShortlistedInventoryDetailsID(String shortlistedInventoryDetailsID) {
        this.shortlistedInventoryDetailsID = shortlistedInventoryDetailsID;
    }

    public void setInventoryId(String inventoryId) {
        this.inventoryId = inventoryId;
    }

    public void setInventoryContentTypeId(String inventoryContentTypeId) {
        this.inventoryContentTypeId = inventoryContentTypeId;
    }

    public void setInventoryName(String inventoryName) {
        this.inventoryName = inventoryName;
    }

    public void setShortlistedSpacesId(String shortlistedSpacesId) {
        this.shortlistedSpacesId = shortlistedSpacesId;
    }

    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }

    public String getInventoryId(){
        return inventoryId;
    }

    public String getInventoryContentTypeId(){
        return inventoryContentTypeId;
    }

    public String getInventoryName(){
        return inventoryName;
    }
    public String getShortlistedSpacesId(){
        return shortlistedSpacesId;
    }

    public String getLastModified(){
        return lastModified;
    }

    public void setValidActivities( List<String> validActivities){
        this.validActivities = validActivities;
    }

    public List<String> getValidActivities(){
        return validActivities;
    }

    public static String getKeyShortlistedInventoryDetailsId(){
        return KEY_SHORTLISTED_INVENTORY_DETAILS_ID;
    }

    public ShortlistedInventoryDetailsTable(String shortlistedInventoryDetailsID, String shortlistedSpacesId, String inventoryId, String inventoryContentTypeId,  String inventoryName){

        this.shortlistedInventoryDetailsID = shortlistedInventoryDetailsID;
        this.shortlistedSpacesId = shortlistedSpacesId;
        this.inventoryContentTypeId = inventoryContentTypeId;
        this.inventoryName = inventoryName;
        this.lastModified = Utils.getCurrentDateString();
        this.inventoryId = inventoryId;
    }

    public static String getCreateTableCommand(){
        return "CREATE TABLE " + TABLE_NAME + "("
                + KEY_SHORTLISTED_INVENTORY_DETAILS_ID + " TEXT,"
                + KEY_INVENTORY_ID + " TEXT,"
                + KEY_SHORTLISTED_SPACES_ID + " TEXT,"
                + KEY_INVENTORY_CONTENT_TYPE_ID + " TEXT,"
                + KEY_LAST_MODIFIED + " TEXT,"
                + KEY_INVENTORY_NAME + " TEXT," +
                " TEXT, PRIMARY KEY("+ KEY_SHORTLISTED_INVENTORY_DETAILS_ID +") )";
    }

    public static String getTableName(){
        return TABLE_NAME;
    }

    public  static void insertBulk(DataBaseHandler db_handle, List<ShortlistedInventoryDetailsTable> shortlistedInventoryDetailsTableData){
        /*
            This functions handles Bulk insert.
         */

        SQLiteDatabase db = db_handle.getWritableDatabase();
        // drop if exist. we don't override data
        db.execSQL("DROP TABLE IF EXISTS " + ShortlistedInventoryDetailsTable.getTableName());
        // create afresh !
        db.execSQL(ShortlistedInventoryDetailsTable.getCreateTableCommand());

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            for ( ShortlistedInventoryDetailsTable instance: shortlistedInventoryDetailsTableData) {
                values.put(KEY_SHORTLISTED_INVENTORY_DETAILS_ID, instance.getShortlistedInventoryDetailsID());
                values.put(KEY_SHORTLISTED_SPACES_ID, instance.getShortlistedSpacesId());
                values.put(KEY_INVENTORY_ID, instance.getInventoryId());
                values.put(KEY_INVENTORY_CONTENT_TYPE_ID, instance.getInventoryContentTypeId());
                values.put(KEY_LAST_MODIFIED, instance.getLastModified());
                values.put(KEY_INVENTORY_NAME, instance.getInventoryName());

                db.insert(ShortlistedInventoryDetailsTable.getTableName(), null, values);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

    }

    public static List<ShortlistedInventoryDetailsTable> getShortlistedInventories( DataBaseHandler db_handle,   String shortlistedSpacesId) {

        // fetch all shortlisted inventories based on given shortlisted spaces id.

        List<ShortlistedInventoryDetailsTable> data = new ArrayList<>();

        SQLiteDatabase db = db_handle.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + getTableName() + " WHERE " + KEY_SHORTLISTED_SPACES_ID +  "= '"+ shortlistedSpacesId + "'";
        Cursor cursor = db.rawQuery(selectQuery, null);
        try {

            if (cursor.moveToFirst()) {
                do {
                    String inventoryId = cursor.getString(cursor.getColumnIndex(KEY_INVENTORY_ID));
                    String inventoryContentTypeId = cursor.getString(cursor.getColumnIndex(KEY_INVENTORY_CONTENT_TYPE_ID));
                    String inventoryName = cursor.getString(cursor.getColumnIndex(KEY_INVENTORY_NAME));
                    String shortlistedInventoryDetailsId = cursor.getString(cursor.getColumnIndex(KEY_SHORTLISTED_INVENTORY_DETAILS_ID));
                    ShortlistedInventoryDetailsTable instance = new ShortlistedInventoryDetailsTable(shortlistedInventoryDetailsId, shortlistedSpacesId, inventoryId, inventoryContentTypeId, inventoryName);
                    data.add(instance);
                } while (cursor.moveToNext());
            }
        } finally {

        }
        return data;
    }


    public static List<ShortlistedInventoryDetailsTable> getShortlistedInventoriesPerDay( DataBaseHandler db_handle,   String shortlistedSupplierId, String requestedDate) {
        /*
        The idea of this function is to fetch all shortlistedInventoryDetails object which have
        either RELEASE, AUDIT or CLOSURE on the given date.
         */

        List<ShortlistedInventoryDetailsTable> data = new ArrayList<>();
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

                    String inventoryId = cursor.getString(cursor.getColumnIndex(KEY_INVENTORY_ID));
                    String inventoryContentTypeId = cursor.getString(cursor.getColumnIndex(KEY_INVENTORY_CONTENT_TYPE_ID));
                    String inventoryName = cursor.getString(cursor.getColumnIndex(KEY_INVENTORY_NAME));
                    String shortlistedInventoryDetailsId = cursor.getString(cursor.getColumnIndex(KEY_SHORTLISTED_INVENTORY_DETAILS_ID));
                    String inventoryActivityType = cursor.getString(cursor.getColumnIndex(InventoryActivityTable.getKeyActivityType()));

                    if ( instanceHashMap.get(shortlistedInventoryDetailsId) == null) {
                        instance = new ShortlistedInventoryDetailsTable(shortlistedInventoryDetailsId, shortlistedSupplierId, inventoryId, inventoryContentTypeId, inventoryName);
                        instanceHashMap.put(shortlistedInventoryDetailsId, instance);
                        data.add(instance);
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
        return data;
    }

}
