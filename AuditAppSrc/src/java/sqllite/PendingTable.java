package com.machadalo.audit.sqllite;

/**
 * Created by Lokesh on 28-06-2017.
 */


/*
        This Table holds the data fro all the inventories that are/were pending
        and is populated from the join of existing table in pending fragment
 */

public class PendingTable {

    //table name
    public static final String TABLE_NAME = "pending_table";

    // required coloumn names
    public static final String KEY_ShortlistedInventoryID = "SSID";
    public static final String KEY_ACTIVTY_TYP = "activity_type";
    public static final String KEY_isCompleted = "is_completed";

    //extra column feilds that are reuired for the pending view nad functions after it
    public static final String KEY_SCHEDULED_ACTIVITY_DATE = "activity_date" ;
    public static final String KEY_INVENTORY_ID = "inventory_id";
    public static final String KEY_PROPOSAL_NAME = "proposal_name";
    public static final String KEY_PROPOSAL_ID = "proposal_id";
    public static final String KEY_INVENTORY_TYP = "inventory_typ";
    public static final String KEY_SUPPLIER_ID = "supplier_id";
    public static final String KEY_SUPPLIER_NAME = "supplier_name";
    public static final String KEY_SUPPLIER_LAT = "sLat";
    public static final String KEY_SUPPLIER_LON = "sLon";
    public static final String KEY_SUPPLIER_ADDRESS = "supplier_address";


    public PendingTable(){

    }


    // create table command
    // only once after the app is installed this table is created
    public static String getCreateTableCommand(){

        String Query = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ( "
                + KEY_ShortlistedInventoryID + " TEXT,"
                + KEY_ACTIVTY_TYP + " TEXT,"
                + KEY_isCompleted + " TEXT,"
                + KEY_PROPOSAL_ID + " TEXT,"
                + KEY_PROPOSAL_NAME + " TEXT,"
                + KEY_SUPPLIER_ID + " TEXT,"
                + KEY_SUPPLIER_NAME + " TEXT,"
                + KEY_SUPPLIER_ADDRESS + " TEXT,"
                + KEY_INVENTORY_ID + " TEXT,"
                + KEY_INVENTORY_TYP + " TEXT,"
                + KEY_SCHEDULED_ACTIVITY_DATE + " TEXT,"
                + " PRIMARY KEY("+ KEY_ShortlistedInventoryID+" ,"+KEY_ACTIVTY_TYP +" ) )";

        return Query;

    }







}
