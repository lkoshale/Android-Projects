package com.machadalo.audit.sqllite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.machadalo.audit.Utils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 30/1/17.
 */

public class BasicSupplierTable {

    public static final String TABLE_NAME = "basic_supplier";
    //  Table Columns names
    public static final String KEY_SUPPLIER_ID = "supplier_id";
    public static final String KEY_NAME = "name";
    public static final String KEY_SUPPLIER_CONTENT_TYPE_ID = "supplier_content_type_id";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_LATITUDE = "latitude";
    public static final String KEY_LONGITUDE = "longitude";
    public static final String KEY_SUPPLIER_IMAGE_PATH = "supplier_image_path";
    public static final String KEY_LAST_MODIFIED = "last_modified";

    // member names.
    public String supplierId;
    public String name;
    public String supplierContentTypeID;
    public String address;
    public String latitude;
    public String longitude;
    public String supplierImagePath;
    public String lastModified;

    public String getSupplierId(){
        return supplierId;
    }

    public String getName(){
        return name;
    }

    public String getSupplierContentTypeID(){
        return supplierContentTypeID;
    }

    public String getAddress(){
        return address;
    }

    public String getLatitude(){
        return latitude;
    }

    public String getLongitude(){
        return longitude;
    }

    public String getSupplierImagePath(){
        return supplierImagePath;
    }

    public String getLastModified(){
        return lastModified;
    }

    public static String getKeySupplierId(){
        return KEY_SUPPLIER_ID;
    }

    public  static String getTableName(){
        return TABLE_NAME;
    }

    public BasicSupplierTable(String supplierId, String supplierContentTypeID, String supplierImagePath, String latitude, String longitude, String address, String lastModified, String name) {

        this.supplierId = supplierId;
        this.supplierContentTypeID = supplierContentTypeID;
        this.supplierImagePath = supplierImagePath;
        this.latitude = latitude;
        this.longitude  =  longitude;
        this.address = address;
        this.lastModified = lastModified;
        this.name = name;
    }

    public static String getCreateTableCommand(){
        return "CREATE TABLE " + TABLE_NAME + "("
                + KEY_SUPPLIER_ID + " TEXT," + KEY_NAME + " TEXT,"
                + KEY_SUPPLIER_CONTENT_TYPE_ID + " TEXT," + KEY_ADDRESS + " TEXT," +
                KEY_LATITUDE + " TEXT," + KEY_LONGITUDE + " TEXT," + KEY_SUPPLIER_IMAGE_PATH + " TEXT," +
                KEY_LAST_MODIFIED + " TEXT," + " TEXT, PRIMARY KEY("+ KEY_SUPPLIER_ID +") )";
    }

    public  static void insertBulk(DataBaseHandler db_handle, List<BasicSupplierTable> basicSupplierData){
        /*
            This functions handles Bulk insert.
        */

        SQLiteDatabase db = db_handle.getWritableDatabase();
        // drop if exist. we don't override data
        db.execSQL("DROP TABLE IF EXISTS " + BasicSupplierTable.getTableName());
        // create afresh !
        db.execSQL(BasicSupplierTable.getCreateTableCommand());

        db.beginTransaction();
        Log.d("insertBulk", "recieved basicSupplierData size " + basicSupplierData.size()) ;

        basicSupplierData = getUniqueBasicSupplierData(basicSupplierData);

        Log.d("insertBulk", "new basicSupplierData size " + basicSupplierData.size()) ;

        try {
            ContentValues values = new ContentValues();
            for ( BasicSupplierTable instance: basicSupplierData) {

                values.put(KEY_SUPPLIER_ID, instance.getSupplierId());
                values.put(KEY_SUPPLIER_CONTENT_TYPE_ID, instance.getSupplierContentTypeID());
                values.put(KEY_ADDRESS, instance.getAddress());
                values.put(KEY_NAME, instance.getName());
                values.put(KEY_LATITUDE, instance.getLatitude());
                values.put(KEY_LONGITUDE, instance.getLongitude());
                values.put(KEY_LAST_MODIFIED, instance.getLastModified());
                values.put(KEY_SUPPLIER_IMAGE_PATH, instance.getSupplierImagePath());

                db.insert(BasicSupplierTable.getTableName(), null, values);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

    }

    public  static  List<BasicSupplierTable> getUniqueBasicSupplierData(List<BasicSupplierTable> basicSupplierTableData) {

        List<String> supplierIds = new ArrayList<>();
        List<BasicSupplierTable> basicUniqueData =  new ArrayList<>();

        try {

            for ( int i = 0; i < basicSupplierTableData.size(); i++ ) {

                BasicSupplierTable instance  = basicSupplierTableData.get(i);
                String inputSupplierId = instance.getSupplierId();

                if ( supplierIds.size() != 0 && supplierIds.contains(inputSupplierId)) {
                   continue;
                }
                supplierIds.add(inputSupplierId);
                basicUniqueData.add(instance);
            }
        }  catch (Exception e) {
            Log.d("getSupplierIds", e.getMessage());

        }
        return  basicUniqueData;
    }

    public static JSONObject getBasicSupplierTableData( DataBaseHandler db_handle,  List<String> supplierIdList) {
        Cursor cursor = null;
        try {

            SQLiteDatabase db = db_handle.getReadableDatabase();

            String query = " select * from " + getTableName() + " WHERE " + getKeySupplierId() + " IN ( " +
                    Utils.makePlaceholders(supplierIdList.size()) + " ) ";

            cursor = db.rawQuery(query, supplierIdList.toArray(new String[0]));
            JSONObject mainNode = new JSONObject();

            if (cursor.moveToFirst()) {
                do {

                    JSONObject childNode = new JSONObject();

                    String supplierId = cursor.getString(cursor.getColumnIndex(KEY_SUPPLIER_ID));
                    String supplierContentType = cursor.getString(cursor.getColumnIndex(KEY_SUPPLIER_CONTENT_TYPE_ID));
                    String supplierName = cursor.getString(cursor.getColumnIndex(KEY_NAME));
                    String supplierAddress = cursor.getString(cursor.getColumnIndex(KEY_ADDRESS));
                    String supplierLatitude = cursor.getString(cursor.getColumnIndex(KEY_LATITUDE));
                    String supplierLongitude = cursor.getString(cursor.getColumnIndex(KEY_LONGITUDE));

                    childNode.put("supplier_id", supplierId);
                    childNode.put("name", supplierName);
                    childNode.put("address1", supplierAddress);
                    childNode.put("address2", "");
                    childNode.put("latitude", supplierLatitude);
                    childNode.put("longitude", supplierLongitude);
                    childNode.put("supplier_content_type_id", supplierContentType);

                    mainNode.put(supplierId, childNode);

                }
                while (cursor.moveToNext());
            }
            return mainNode;
        }catch (Exception e) {

            Log.d(TABLE_NAME, e.getMessage());
        }

        finally {
            if ( cursor != null ){
                cursor.close();
            }
        }
        return null;
    }




    // add insert bulk code here.

}
