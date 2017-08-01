package com.machadalo.audit.sqllite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.webkit.WebHistoryItem;

import com.machadalo.audit.Constants;
import com.machadalo.audit.Utils;
import com.machadalo.audit.extras.Data;
import com.machadalo.audit.extras.QueueData;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by root on 31/1/17.
 */

/*
 Edit by lokesh
 Lat Lon column added for the lattitude and lonitude for each image taken
 */

public class InventoryImagePathTable {
    /*
    This table stores image path for a shortlisted_inventory_detail id, and on what
    date and under which inventory activity like Release, Audit, closure etc. shortlisted_inventory
     _details_id acts a FK to ShortlistedInventoryDetails Table.
     */
    public static final String TABLE_NAME = "inventory_image_path";
    //  Table Columns names
    public static final String KEY_IMAGE_PATH = "image_path";
    public static final String KEY_INVENTORY_ACTIVITY_TYPE = "inventory_activity_type";
    public static final String KEY_INVENTORY_IMAGE_ID = "id";
    public static final String KEY_LOCAL_IMAGE_PATH = "local_image_path";
    public static final String KEY_IS_DJANGO_UPLOADED = "is_django_uploaded"; // tells weather the image is uploaded to DJANGO
    public static final String KEY_IS_AMAZON_UPLOADED = "is_amazon_uploaded"; // tells weather the image is uploaded to AMAZON
    public static final String KEY_COMMENT = "comment"; //
    public static final String KEY_ACTIVITY_DATE = "activity_date";
    public static final String KEY_SHORTLISTED_INVENTORY_ID = "shortlisted_inventory_id";
    public static final String KEY_LATITUDE = "lat";
    public static final String KEY_LONGITUDE = "lon";

    public String imagePath;
    public String inventoryActivityType;
    public String inventoryImageId;
    public String localImagePath;
    public String isDjangoUploaded;
    public String isAmazonUploaded;
    public String comment;
    public String activityDate;
    public String shortlistedInventoryId;


    public String getImagePath(){
        return imagePath;
    }
    public String getInventoryActivityType(){
        return inventoryActivityType;
    }
    public String getInventoryImageId(){
        return inventoryImageId;
    }
    public String getLocalImagePath(){
        return localImagePath;
    }
    public String getActivityDate(){
        return activityDate;
    }


    public static String getTableName(){
        return TABLE_NAME;
    }

    public void setLocalImagePath( String path ) {
        this.localImagePath = path;
    }
    public void setIsDjangoUploaded( String verdict ) {
        this.isDjangoUploaded = verdict;
    }
    public void setIsAmazonUploaded( String verdict ){
        this.isAmazonUploaded = verdict;
    }
    public void setActivityType( String activityDate ){
        this.activityDate = activityDate;
    }
    public void setComment( String comment ){
        this.comment = comment;
    }


    public InventoryImagePathTable(String shortlistedInventoryId, String inventoryActivityType, String activityDate) {
        this.inventoryActivityType = inventoryActivityType;
        this.activityDate = activityDate;
        this.shortlistedInventoryId = shortlistedInventoryId;
    }

    public static String getCreateTableCommand(){
        return "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ( "
                + KEY_IMAGE_PATH + " TEXT,"
                + KEY_INVENTORY_ACTIVITY_TYPE + " TEXT,"
                + KEY_ACTIVITY_DATE + " TEXT,"
                + KEY_COMMENT + " TEXT,"
                + KEY_IS_AMAZON_UPLOADED + " TEXT,"
                + KEY_IS_DJANGO_UPLOADED + " TEXT,"
                + KEY_LOCAL_IMAGE_PATH + " TEXT,"
                + KEY_SHORTLISTED_INVENTORY_ID + " TEXT,"
                + KEY_INVENTORY_IMAGE_ID + " INTEGER,"
                + KEY_LATITUDE+ " TEXT,"
                + KEY_LONGITUDE + " TEXT,"
                + " PRIMARY KEY("+ KEY_INVENTORY_IMAGE_ID +") )";
    }
    public  static void insertBulk(DataBaseHandler db_handle, List<InventoryImagePathTable> inventoryImagePathTableData) {

        SQLiteDatabase db = db_handle.getWritableDatabase();
        // drop if exist. we don't override data
        db.execSQL("DROP TABLE IF EXISTS " + InventoryImagePathTable.getTableName());
        // create afresh !
        db.execSQL(InventoryImagePathTable.getCreateTableCommand());

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            for ( InventoryImagePathTable instance: inventoryImagePathTableData) {

                values.put(KEY_INVENTORY_ACTIVITY_TYPE, instance.getInventoryActivityType());
                values.put(KEY_ACTIVITY_DATE, instance.getActivityDate());

                db.insert(InventoryImagePathTable.getTableName(), null, values);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();

        }
    }

    public static String insert(DataBaseHandler db_handle, Map<String, String> data) {

        SQLiteDatabase db = db_handle.getWritableDatabase();

        try {

            String imagePath = data.get(KEY_IMAGE_PATH);
            String inventoryActivityType = data.get(KEY_INVENTORY_ACTIVITY_TYPE);
            String localImagePath = data.get(KEY_LOCAL_IMAGE_PATH);
            String isDjangoUploaded = data.get(KEY_IS_DJANGO_UPLOADED);
            String isAmazonUploaded = data.get(KEY_IS_AMAZON_UPLOADED);
            String comment = data.get(KEY_COMMENT);
            String activityDate = data.get(KEY_ACTIVITY_DATE);
            String shortlistedInventoryId = data.get(KEY_SHORTLISTED_INVENTORY_ID);


            ContentValues values = new ContentValues();

            values.put(KEY_IMAGE_PATH, imagePath);
            values.put(KEY_LOCAL_IMAGE_PATH, localImagePath);
            values.put(KEY_IS_AMAZON_UPLOADED, isAmazonUploaded);
            values.put(KEY_IS_DJANGO_UPLOADED, isDjangoUploaded);
            values.put(KEY_ACTIVITY_DATE, activityDate);
            values.put(KEY_INVENTORY_ACTIVITY_TYPE, inventoryActivityType);
            values.put(KEY_COMMENT, comment);
            values.put(KEY_SHORTLISTED_INVENTORY_ID, shortlistedInventoryId);
            values.put(KEY_LATITUDE," ");
            values.put(KEY_LONGITUDE," ");
            long id = db.insert(InventoryImagePathTable.getTableName(), null, values);
            Log.e("in the imageinsertDb","inserted "+String.valueOf(id));
            return  Long.toString(id);
        }
        catch (Exception e) {
            Log.d("Update", e.getMessage());
        }
        finally {

        }

        return null;
    }

    public static void update(DataBaseHandler db_handle, Map<String, String> data) {

        SQLiteDatabase db = db_handle.getWritableDatabase();

        String imagePath = data.get(KEY_IMAGE_PATH);
        String inventoryActivityType = data.get(KEY_INVENTORY_ACTIVITY_TYPE);
        String localImagePath = data.get(KEY_LOCAL_IMAGE_PATH);
        String isDjangoUploaded = data.get(KEY_IS_DJANGO_UPLOADED);
        String isAmazonUploaded = data.get(KEY_IS_AMAZON_UPLOADED);
        String comment = data.get(KEY_COMMENT);
        String activityDate = data.get(KEY_ACTIVITY_DATE);
        String id = data.get(KEY_INVENTORY_IMAGE_ID);
        String shortlistedInventoryId = data.get(KEY_SHORTLISTED_INVENTORY_ID);

        ContentValues values = new ContentValues();

        values.put(KEY_IMAGE_PATH, imagePath);
        values.put(KEY_LOCAL_IMAGE_PATH, localImagePath);
        values.put(KEY_IS_AMAZON_UPLOADED, isAmazonUploaded);
        values.put(KEY_IS_DJANGO_UPLOADED, isDjangoUploaded);
        values.put(KEY_ACTIVITY_DATE, activityDate);
        values.put(KEY_INVENTORY_ACTIVITY_TYPE, inventoryActivityType);
        values.put(KEY_COMMENT, comment);
        values.put(KEY_SHORTLISTED_INVENTORY_ID, shortlistedInventoryId);

        db.update(InventoryImagePathTable.getTableName(), values, "WHERE " + KEY_INVENTORY_IMAGE_ID + "='" + id + "'", null);

    }

    public static void updateIsDjangoUploaded(DataBaseHandler db_handle, String inventoryImageId, String isDjangoUploaded) {

        SQLiteDatabase db = db_handle.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_IS_DJANGO_UPLOADED, isDjangoUploaded);
        db.update(InventoryImagePathTable.getTableName(), values, KEY_INVENTORY_IMAGE_ID + " ='" + inventoryImageId + "'", null);
        //db_handle.printTotalRows(getTableName());

    }


    public static void updateIsAmazonUploaded(DataBaseHandler db_handle, String inventoryImageId, String isAmazonUploaded) {

        SQLiteDatabase db = db_handle.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_IS_AMAZON_UPLOADED, isAmazonUploaded);
        db.update(InventoryImagePathTable.getTableName(), values, KEY_INVENTORY_IMAGE_ID + " ='" + inventoryImageId + "'", null);
        //db_handle.printTotalRows(getTableName());
    }

    public static void bulkUpdateDjangoIsUploaded( DataBaseHandler db_handle, String [] inventoryImageIdList, String isDjangoUploaded) {

        SQLiteDatabase db = db_handle.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_IS_DJANGO_UPLOADED, isDjangoUploaded);
        String placeholders= Utils.makePlaceholders(inventoryImageIdList.length);
        db.update(InventoryImagePathTable.getTableName(), values, KEY_INVENTORY_IMAGE_ID + " IN ( " + placeholders  + " ) ", inventoryImageIdList);
        //db_handle.printTotalRows(getTableName());
    }

    public static List<QueueData> getQueuedData (DataBaseHandler db_handle){

        List<QueueData> qData = new ArrayList<>();
        SQLiteDatabase db = db_handle.getReadableDatabase();
        Log.d("SID table", "content");
        //db_handle.printTotalRows( ShortlistedInventoryDetailsTable.getTableName() );

        String query = "select  a." + ShortlistedInventoryDetailsTable.KEY_SHORTLISTED_INVENTORY_DETAILS_ID +  " as SID , " +
                              " a." + ShortlistedInventoryDetailsTable.KEY_INVENTORY_ID +  "," +
                              " b." + KEY_INVENTORY_IMAGE_ID + ", " +
                              " b." + KEY_IMAGE_PATH + ", " +
                              " b." + KEY_LOCAL_IMAGE_PATH + ", " +
                              " b." + KEY_COMMENT + ", " +
                              " b." + KEY_IS_DJANGO_UPLOADED + ", " +
                              " b." + KEY_IS_AMAZON_UPLOADED + ", " +
                              " b." + KEY_INVENTORY_ACTIVITY_TYPE + ", " +
                              " b." + KEY_ACTIVITY_DATE + ", " +
                              " b." + KEY_LATITUDE + ", " +
                              " b." + KEY_LONGITUDE +
                              " from " + ShortlistedInventoryDetailsTable.getTableName() +  " as a " +
                              " INNER JOIN  " + getTableName() + "  as b " +
                              " ON a." + ShortlistedInventoryDetailsTable.KEY_SHORTLISTED_INVENTORY_DETAILS_ID + " = " +
                              " b." + KEY_SHORTLISTED_INVENTORY_ID  +
                              " WHERE b." + KEY_IS_DJANGO_UPLOADED + "='" + Constants.False + "'" +
                              " OR b." + KEY_IS_AMAZON_UPLOADED + "='" + Constants.False + "'" ;
        Cursor cursor = null;
        try {

            Log.d("query", query);
            cursor = db.rawQuery(query, null);
            Log.e("RowsFound", Integer.toString(cursor.getCount()));

            if ( cursor.moveToFirst() ) {
                do {

                    //String shortlistedInventoryId = cursor.getString(cursor.getColumnIndex(ShortlistedInventoryDetailsTable.KEY_SHORTLISTED_INVENTORY_DETAILS_ID));
                    String shortlistedInventoryId = cursor.getString(cursor.getColumnIndex("SID"));
                    String inventoryId = cursor.getString(cursor.getColumnIndex(ShortlistedInventoryDetailsTable.KEY_INVENTORY_ID));
                    String imagePath = cursor.getString(cursor.getColumnIndex(KEY_IMAGE_PATH));
                    String activityType = cursor.getString(cursor.getColumnIndex(KEY_INVENTORY_ACTIVITY_TYPE));
                    String activityDate = cursor.getString(cursor.getColumnIndex(KEY_ACTIVITY_DATE));
                    String localImagePath = cursor.getString(cursor.getColumnIndex(KEY_LOCAL_IMAGE_PATH));
                    String comment = cursor.getString(cursor.getColumnIndex(KEY_COMMENT));
                    String inventoryImagePathTableId = cursor.getString(cursor.getColumnIndex(KEY_INVENTORY_IMAGE_ID));
                    String isDjangoUploaded = cursor.getString(cursor.getColumnIndex(KEY_IS_DJANGO_UPLOADED));
                    String isAmazonUploaded = cursor.getString(cursor.getColumnIndex(KEY_IS_AMAZON_UPLOADED));
                    String lat = cursor.getString(cursor.getColumnIndex(KEY_LATITUDE));
                    String lon = cursor.getString(cursor.getColumnIndex(KEY_LONGITUDE));


                    Log.d("shorlistedInvId", shortlistedInventoryId);
                    Log.d("invImagePathId", inventoryImagePathTableId);
                    Log.d("activityDate", activityDate);
                    Log.d("inventoryId", inventoryId);
                    Log.d("ActivityType", activityType);

                    Utils.printOnScreenNewLine("---------------");

                    QueueData instance = new QueueData();

                    instance.setShortlistedInventoryDetailId(shortlistedInventoryId);
                    instance.setInventoryId(inventoryId);
                    instance.setComment(comment);
                    instance.setImagePath(imagePath);
                    instance.setActivityDate(activityDate);
                    instance.setActivityType(activityType);
                    instance.setLocalImagePath(localImagePath);
                    instance.setInventoryImagePathTableId(inventoryImagePathTableId);
                    instance.setIsAmazonUploaded(isAmazonUploaded);
                    instance.setIsDjangoUploaded(isDjangoUploaded);
                    instance.setLat(lat);
                    instance.setLon(lon);

                    qData.add(instance);

                } while (cursor.moveToNext()) ;
            }
            return qData;
        }
        catch (Exception e) {
            Log.d("getQDat0a", e.getMessage()) ;
        }
        finally {

            if ( cursor != null ) {
                cursor.close();
            }
        }

        return null;
    }

    public static List<QueueData> GetQueueData(DataBaseHandler dataBaseHandler){

        List<QueueData> mList = new ArrayList<>();
        SQLiteDatabase db = dataBaseHandler.getReadableDatabase();
        Set<String> hashset = new HashSet<>();

        String LeftOuterJoin = " LEFT OUTER JOIN ";

        String Query = "SELECT * FROM " + ShortlistedSuppliersTable.TABLE_NAME + " SST" +
                LeftOuterJoin + ShortlistedInventoryDetailsTable.TABLE_NAME + " SIDT" + " ON " +
                "SST." + ShortlistedSuppliersTable.KEY_SHORTLISTED_SPACES_ID + " = " + "SIDT." + ShortlistedInventoryDetailsTable.KEY_SHORTLISTED_SPACES_ID +
                LeftOuterJoin + InventoryImagePathTable.TABLE_NAME + " IIPT" + " ON " +
                "SIDT." + ShortlistedInventoryDetailsTable.KEY_SHORTLISTED_INVENTORY_DETAILS_ID + " = " + "IIPT." + InventoryImagePathTable.KEY_SHORTLISTED_INVENTORY_ID +
                LeftOuterJoin + InventoryActivityTable.TABLE_NAME + " IAT" + " ON " +
                "SIDT." + ShortlistedInventoryDetailsTable.KEY_SHORTLISTED_INVENTORY_DETAILS_ID + " = " + "IAT." + InventoryActivityTable.KEY_SHORTLISTED_INVENTORY_DETAIL_ID +
                LeftOuterJoin + InventoryActivityAssignmentTable.TABLE_NAME + " IAAT" + " ON " +
                "IAT." + InventoryActivityTable.KEY_INVENTORY_ACTIVITY_ID + " = " + "IAAT." + InventoryActivityAssignmentTable.KEY_INVENTORY_ACTIVITY_ID
                + " ;";

        Cursor cursor = db.rawQuery(Query,null);

        while (cursor.moveToNext()) {

            String isDjangoUploaded = cursor.getString(cursor.getColumnIndex(InventoryImagePathTable.KEY_IS_DJANGO_UPLOADED));
            String isAmazonUploaded = cursor.getString(cursor.getColumnIndex(InventoryImagePathTable.KEY_IS_AMAZON_UPLOADED));
            String str = cursor.getString(cursor.getColumnIndexOrThrow(InventoryImagePathTable.KEY_IMAGE_PATH));

            if (isAmazonUploaded != null && isDjangoUploaded != null) {

                if (isAmazonUploaded.compareTo(Constants.False) == 0 || isDjangoUploaded.compareTo(Constants.False) == 0) {

                    String shortlistedInventoryId = cursor.getString(cursor.getColumnIndex(KEY_SHORTLISTED_INVENTORY_ID));

                    String inventoryId = cursor.getString(cursor.getColumnIndex(ShortlistedInventoryDetailsTable.KEY_INVENTORY_ID));
                    String imagePath = cursor.getString(cursor.getColumnIndex(KEY_IMAGE_PATH));
                    String activityType = cursor.getString(cursor.getColumnIndex(KEY_INVENTORY_ACTIVITY_TYPE));
                    String activityDate = cursor.getString(cursor.getColumnIndex(KEY_ACTIVITY_DATE));
                    String localImagePath = cursor.getString(cursor.getColumnIndex(KEY_LOCAL_IMAGE_PATH));
                    String comment = cursor.getString(cursor.getColumnIndex(KEY_COMMENT));
                    String inventoryImagePathTableId = cursor.getString(cursor.getColumnIndex(KEY_INVENTORY_IMAGE_ID));
                    String lat = cursor.getString(cursor.getColumnIndex(KEY_LATITUDE));
                    String lon = cursor.getString(cursor.getColumnIndex(KEY_LONGITUDE));
                    String proposalID = cursor.getString(cursor.getColumnIndexOrThrow(ShortlistedSuppliersTable.KEY_PROPOSAL_ID));
                    String supplierID = cursor.getString(cursor.getColumnIndexOrThrow(ShortlistedSuppliersTable.KEY_SUPPLIER_ID));
                    String inventoryName = cursor.getString(cursor.getColumnIndexOrThrow(ShortlistedInventoryDetailsTable.KEY_INVENTORY_NAME));

                    Log.e("Adding queue",shortlistedInventoryId+" "+activityType+" "+imagePath);

                    if (hashset.contains(imagePath))
                        continue;
                    else
                        hashset.add(imagePath);

                    String proposalName = " ";

                    try {
                        String ProposalQuery = "SELECT * FROM " + ProposalTable.TABLE_NAME + " WHERE " + ProposalTable.KEY_PROPOSAL_ID + " = " + "\"" + proposalID + "\"" + " ; ";
                        //db_handle2.printTotalRows(ProposalTable.TABLE_NAME);

                        Cursor readProposal = db.rawQuery(ProposalQuery, null);
                       // Log.e("proposal name :" , proposalName);

                        while (readProposal.moveToNext()) {
                            proposalName = readProposal.getString(readProposal.getColumnIndex(ProposalTable.KEY_PROPOSAL_NAME));
                         //   Log.e("proposal name :" , proposalName);
                        }


                    } catch (SQLiteException e) {
                        e.printStackTrace();
                    }


                    String SuplierName = " ";
                    String SuplierAdrs = " ";

                    try {

                        String SupplierQuery = "SELECT * FROM " + BasicSupplierTable.TABLE_NAME + " WHERE " + BasicSupplierTable.KEY_SUPPLIER_ID + " = " + "\"" + supplierID + "\"" + " ; ";

                        Cursor readSuplier = db.rawQuery(SupplierQuery, null);

                        while (readSuplier.moveToNext()) {
                            SuplierName = readSuplier.getString(readSuplier.getColumnIndexOrThrow(BasicSupplierTable.KEY_NAME));
                            SuplierAdrs = readSuplier.getString(readSuplier.getColumnIndexOrThrow(BasicSupplierTable.KEY_ADDRESS));
                        }


                    } catch (SQLiteException e) {
                        e.printStackTrace();
                    }

                    QueueData instance = new QueueData();


                    instance.setShortlistedInventoryDetailId(shortlistedInventoryId);
                    instance.setInventoryId(inventoryId);
                    instance.setComment(comment);
                    instance.setImagePath(imagePath);
                    instance.setActivityDate(activityDate);
                    instance.setActivityType(activityType);
                    instance.setLocalImagePath(localImagePath);
                    instance.setInventoryImagePathTableId(inventoryImagePathTableId);
                    instance.setIsAmazonUploaded(isAmazonUploaded);
                    instance.setIsDjangoUploaded(isDjangoUploaded);
                    instance.setLat(lat);
                    instance.setLon(lon);
                    instance.setSocietyName(SuplierName);
                    instance.setSocietyAddress(SuplierAdrs);
                    instance.setProposalName(proposalName);
                    instance.setInventoryName(inventoryName);

                    mList.add(instance);

                }

            }

        }

        return mList;
    }



}



