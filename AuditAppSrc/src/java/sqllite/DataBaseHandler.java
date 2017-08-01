package com.machadalo.audit.sqllite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.Settings;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DataBaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "machadalodb";

    //  table name
    private static final String TABLE_NAME = "audit";
    private static final String TABLE_NAME_UP = "upload";
    private static DataBaseHandler mInstance = null;



    //  Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_AD_ID = "ad_inventory_ID";
    private static final String KEY_SOCIETY_ADDRESS = "society_address";
    private static final String KEY_INVENTORY_ADDRESS = "inventory_address";
    private static final String KEY_MAP = "map";
    private static final String KEY_IMAGE = "image";
    private static final String KEY_DATE = "date";
    private static final String KEY_SUBMIT_STATUS = "status";
    private static final String KEY_TIME = "timestamp";
    private static final String KEY_INVENT_TYPE = "invent_type";
    private static final String KEY_SOCIETY_NAME = "society_name";
    private static final String KEY_AD_TYPE = "ad_type";

    //Uploaded and Queued table
    private static final String KEY_ID_UP = "up_id";
    private static final String KEY_AD_ID_UP = "up_ad_inventory_ID";
    private static final String KEY_SOCIETY_ADDRESS_UP = "up_society_address";
    private static final String KEY_INVENTORY_ADDRESS_UP = "up_inventory_address";
    private static final String KEY_MAP_UP = "up_map";
    private static final String KEY_IMAGE_UP = "up_image";
    private static final String KEY_DATE_UP = "up_date";
    private static final String KEY_SUBMIT_STATUS_UP = "up_status";
    private static final String KEY_TIME_UP = "up_timestamp";
    private static final String KEY_INVENT_TYPE_UP = "invent_type";
    private static final String KEY_SOCIETY_NAME_UP = "society_name";
    private static final String KEY_AD_TYPE_UP = "ad_type";


    public static synchronized DataBaseHandler getInstance(Context ctx) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (mInstance == null) {
            mInstance = new DataBaseHandler(ctx.getApplicationContext());
        }
        return mInstance;
    }

    /**
     * Constructor should be private to prevent direct instantiation.
     * make call to static factory method "getInstance()" instead.
     */
    private DataBaseHandler(Context ctx) {
        super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
    }


    // Creating Tables
    String CREATE_AUDIT_TABLE = "CREATE TABLE " + TABLE_NAME + "("
            + KEY_AD_ID + " TEXT," + KEY_SOCIETY_ADDRESS + " TEXT," + KEY_INVENTORY_ADDRESS + " TEXT,"
            + KEY_MAP + " TEXT," + KEY_DATE + " TEXT," + KEY_SUBMIT_STATUS + " TEXT," + KEY_TIME + " TEXT,"
            + KEY_IMAGE + " TEXT,"+ KEY_INVENT_TYPE + " TEXT,"+ KEY_SOCIETY_NAME + " TEXT,"+ KEY_AD_TYPE + " TEXT,PRIMARY KEY("+KEY_AD_ID+","+KEY_DATE+") )";


    String CREATE_UPLOAD_TABLE = "CREATE TABLE " + TABLE_NAME_UP + "("
            + KEY_AD_ID_UP + " TEXT," + KEY_SOCIETY_ADDRESS_UP + " TEXT," + KEY_INVENTORY_ADDRESS_UP + " TEXT,"
            + KEY_MAP_UP + " TEXT," + KEY_DATE_UP + " TEXT," + KEY_SUBMIT_STATUS_UP + " TEXT," + KEY_TIME_UP + " TEXT,"
            + KEY_IMAGE_UP + " TEXT,"+ KEY_INVENT_TYPE_UP + " TEXT,"+ KEY_SOCIETY_NAME_UP + " TEXT,"+ KEY_AD_TYPE_UP + " TEXT,PRIMARY KEY("+KEY_AD_ID_UP+","+KEY_DATE_UP+") )";

    String CREATE_SHORTLISTED_SPACES_TABLE = ShortlistedSuppliersTable.getCreateTableCommand();

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_UPLOAD_TABLE);
        db.execSQL(CREATE_AUDIT_TABLE);
        Log.e("onCreate", "creating databases. onCreate called");
        db.execSQL(ShortlistedInventoryDetailsTable.getCreateTableCommand());
        db.execSQL(ShortlistedSuppliersTable.getCreateTableCommand());
        db.execSQL(ProposalTable.getCreateTableCommand());
        db.execSQL(BasicSupplierTable.getCreateTableCommand());
        // drop if exist. we don't override data
        db.execSQL("DROP TABLE IF EXISTS " + InventoryActivityTable.getTableName());
        // create afresh !
        db.execSQL(InventoryActivityTable.getCreateTableCommand());

        db.execSQL("DROP TABLE IF EXISTS " + InventoryActivityAssignmentTable.getTableName());
        // create afresh !
        db.execSQL(InventoryActivityAssignmentTable.getCreateTableCommand());
        //create Contacts Table
        db.execSQL("DROP TABLE IF EXISTS " + ContactsTable.getTableName());
        db.execSQL(ContactsTable.getCreateTableCommand());

    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_UP);
        db.execSQL("DROP TABLE IF EXISTS " + ShortlistedInventoryDetailsTable.getTableName());
        db.execSQL("DROP TABLE IF EXISTS " + ShortlistedSuppliersTable.getTableName());
        db.execSQL("DROP TABLE IF EXISTS " + ProposalTable.getTableName());
        db.execSQL("DROP TABLE IF EXISTS " + BasicSupplierTable.getTableName());
        // Create tables again
        onCreate(db);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    public void addAudit(AuditGS audit) {  // Adding new Audit
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_AD_ID, audit._ad_inventory_ID); // Ad Inventory ID
        values.put(KEY_SOCIETY_ADDRESS, audit._society_address); // SocietyAddress
        values.put(KEY_INVENTORY_ADDRESS, audit._inventory_address); // InventoryAddress
        values.put(KEY_MAP, audit._map); // MAP
        values.put(KEY_IMAGE, audit._image); // IMAGE
        values.put(KEY_DATE, audit._date); // Date
        values.put(KEY_SUBMIT_STATUS, audit._submit_status); // Status
        values.put(KEY_TIME, audit._timestamp); // Timestamp
        values.put(KEY_INVENT_TYPE, audit._invent_type); // InventType
        values.put(KEY_SOCIETY_NAME, audit._society_name); // InventType
        values.put(KEY_AD_TYPE, audit._ad_type); // InventType


        // Inserting Row
        db.insertWithOnConflict(TABLE_NAME, null, values,SQLiteDatabase.CONFLICT_REPLACE);

        db.close(); // Closing database connection
    }

    public void addUpload(AuditGS audit) {  // Adding new Audit
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_AD_ID_UP, audit._ad_inventory_ID); // Ad Inventory ID
        values.put(KEY_SOCIETY_ADDRESS_UP, audit._society_address); // SocietyAddress
        values.put(KEY_INVENTORY_ADDRESS_UP, audit._inventory_address); // InventoryAddress
        values.put(KEY_IMAGE_UP, audit._image); // IMAGE
        values.put(KEY_SUBMIT_STATUS_UP, audit._submit_status); // SUBMIT STATUS
        values.put(KEY_DATE_UP, audit._date); // SUBMIT STATUS

        values.put(KEY_INVENT_TYPE_UP, audit._invent_type); // InventType
        values.put(KEY_SOCIETY_NAME_UP, audit._society_name); // InventType
        String selectQueryAudit = "UPDATE "+ TABLE_NAME + " SET "+KEY_SUBMIT_STATUS+" = 'Submitted' WHERE "+KEY_AD_ID+" = '"+audit._ad_inventory_ID+"'";
        String selectQueryUpload = "UPDATE "+ TABLE_NAME_UP + " SET "+KEY_SUBMIT_STATUS_UP+" = 'Submitted' WHERE "+KEY_AD_ID_UP+" = '"+audit._ad_inventory_ID+"'";

        db.execSQL(selectQueryUpload);
        db.execSQL(selectQueryAudit);
        // Inserting Row
        db.insertWithOnConflict(TABLE_NAME_UP, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close(); // Closing database connection

    }


    // Getting All Audit
    public List<AuditGS> getAllPendingAudit() {
        List<AuditGS> contactList = new ArrayList<AuditGS>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_NAME + " WHERE "+KEY_SUBMIT_STATUS+" = 'Pending' ORDER BY ad_inventory_ID";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                AuditGS audit = new AuditGS();
                audit.set_ad_inventory_ID(cursor.getString(0));
                audit.set_society_address(cursor.getString(1));
                audit.set_inventory_address(cursor.getString(2));
                audit.set_map(cursor.getString(3));
                audit.set_image(cursor.getString(4));
                audit.set_date(cursor.getString(5));
                audit.set_submit_status(cursor.getString(6));
                audit.set_timestamp(cursor.getString(7));
                audit.set_invent_type(cursor.getString(8));
                audit.set_society_name(cursor.getString(9));
                audit.set_ad_type(cursor.getString(10));

                // Adding contact to list
                contactList.add(audit);
            } while (cursor.moveToNext());
        }
        // close inserting data from database
        db.close();
        // return audit list
        return contactList;

    }

    // Getting All Audit
    public List<AuditGS> getAllAudit() {
        List<AuditGS> contactList = new ArrayList<AuditGS>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_NAME + " ORDER BY ad_inventory_ID";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                AuditGS audit = new AuditGS();
                audit.set_ad_inventory_ID(cursor.getString(0));
                audit.set_society_address(cursor.getString(1));
                audit.set_inventory_address(cursor.getString(2));
                audit.set_map(cursor.getString(3));
                audit.set_image(cursor.getString(4));
                audit.set_date(cursor.getString(5));
                audit.set_submit_status(cursor.getString(6));
                audit.set_timestamp(cursor.getString(7));
                audit.set_invent_type(cursor.getString(8));
                audit.set_society_name(cursor.getString(9));
                audit.set_ad_type(cursor.getString(10));

                // Adding contact to list
                contactList.add(audit);
            } while (cursor.moveToNext());
        }
        // close inserting data from database
        db.close();
        // return audit list
        return contactList;

    }

    public List<AuditGS> getImageUrl() {
        List<AuditGS> contactList = new ArrayList<AuditGS>();
        // Select All Query
        String selectQuery = "SELECT "+ KEY_AD_ID +"," + KEY_SOCIETY_ADDRESS + "," + KEY_IMAGE + " FROM " + TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                AuditGS audit = new AuditGS();
                audit.set_ad_inventory_ID(cursor.getString(0));
                audit.set_society_address(cursor.getString(1));
                audit.set_image(cursor.getString(2));
                // Adding contact to list
                contactList.add(audit);
            } while (cursor.moveToNext());
        }
        // close inserting data from database
        db.close();
        // return audit list
        return contactList;

    }
    public List<AuditGS> up_getAllSubmittedAudits() {
        List<AuditGS> contactList = new ArrayList<AuditGS>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " +TABLE_NAME_UP +" WHERE " +KEY_SUBMIT_STATUS_UP+ "='Submitted'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                AuditGS audit = new AuditGS();

                audit.set_ad_inventory_ID(cursor.getString(0));
                audit.set_society_address(cursor.getString(1));
                audit.set_inventory_address(cursor.getString(2));
                audit.set_map(cursor.getString(3));
                audit.set_image(cursor.getString(4));
                audit.set_date(cursor.getString(5));
                audit.set_submit_status(cursor.getString(6));
                audit.set_timestamp(cursor.getString(7));
                // Adding contact to list
                contactList.add(audit);
            } while (cursor.moveToNext());
        }
        // close inserting data from database
        db.close();
        // return audit list
        return contactList;

    }
    public List<AuditGS> up_getAllPendingAudits() {
        List<AuditGS> contactList = new ArrayList<AuditGS>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " +TABLE_NAME_UP +" WHERE " +KEY_SUBMIT_STATUS_UP+ "='Pending'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                AuditGS audit = new AuditGS();

                audit.set_ad_inventory_ID(cursor.getString(0));
                audit.set_society_address(cursor.getString(1));
                audit.set_inventory_address(cursor.getString(2));
                audit.set_map(cursor.getString(3));
                audit.set_image(cursor.getString(4));
                audit.set_date(cursor.getString(5));
                audit.set_submit_status(cursor.getString(6));
                audit.set_timestamp(cursor.getString(7));
                // Adding contact to list
                contactList.add(audit);
            } while (cursor.moveToNext());
        }
        // close inserting data from database
        db.close();
        // return audit list
        return contactList;

    }


    // Getting audit Count
    public int getAuditCount() {
        String countQuery = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int auditCount = cursor.getCount();
        cursor.close();

        // return count
        return auditCount;
    }

    //getting pending count
    public int getAuditPendingCount() {
        String countQuery = "SELECT  * FROM " + TABLE_NAME + " WHERE "+KEY_SUBMIT_STATUS+" = 'Pending'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int pendingCount = cursor.getCount();
        cursor.close();

        // return count
        return pendingCount;
    }

    public int getTotalCount(String tableName){

        String selectQuery = "SELECT  * FROM " + tableName ;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public  void printTotalRows( String tableName) {

        String selectQuery = "SELECT  * FROM " + tableName ;
        SQLiteDatabase db = this.getReadableDatabase();

        try {
            Cursor cursor = db.rawQuery(selectQuery, null);
            String tableString = String.format("Table %s:\n", tableName);
            if ( cursor.moveToFirst() ) {
                String [] colColumns = cursor.getColumnNames();
                do {
                    for ( String name: colColumns) {
                        tableString += String.format("%s: %s\n", name, cursor.getString(cursor.getColumnIndex(name)));
                    }
                    tableString += "\n";
                }while (cursor.moveToNext());
            }

            //edited by me to check databse
            Log.e("printToatlRows DBhandle",tableString);
            //System.out.print(tableString);
        }
        catch (Exception e) {
            Log.d("printRows", e.getMessage());
        }

    }



}
