package com.machadalo.audit.sqllite;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;

/**
 * Created by root on 22/2/17.
 */

public class InventoryActivityTable  {

    public static final String TABLE_NAME = "inventory_activity";

    //  Table Columns names
    public static final String KEY_INVENTORY_ACTIVITY_ID = "id";
    public static final String KEY_ACTIVITY_TYPE = "activity_type";
    public static final String KEY_SHORTLISTED_INVENTORY_DETAIL_ID = "shortlisted_inventory_detail_id";


    public String inventoryActivityId;
    public String activityType;
    public String shortlistedInventoryDetailId;

    public  static  String   getTableName(){
        return TABLE_NAME;
    }

    public static String getKeyInventoryActivityId(){
        return KEY_INVENTORY_ACTIVITY_ID;
    }
    public static String getKeyActivityType(){
        return KEY_ACTIVITY_TYPE;
    }
    public static String getKeyShortlistedInventoryDetailId(){
        return KEY_SHORTLISTED_INVENTORY_DETAIL_ID;
    }

    public void setActivityType( String activityType ) {
        this.activityType = activityType;
    }

    public void setShortlistedInventoryDetailId(String shortlistedInventoryDetailId) {
        this.shortlistedInventoryDetailId = shortlistedInventoryDetailId;
    }
    public void setInventoryActivityId( String inventoryActivityId ) {
        this.inventoryActivityId  = inventoryActivityId;
    }

    public String getInventoryActivityId(){
        return inventoryActivityId;
    }

    public String getActivityType(){
        return activityType;
    }
    public String getShortlistedInventoryDetailId(){
        return shortlistedInventoryDetailId;
    }


    public static String getCreateTableCommand(){
        return "CREATE TABLE " + TABLE_NAME + "("
                + " TEXT," + KEY_ACTIVITY_TYPE + " TEXT,"
                + KEY_SHORTLISTED_INVENTORY_DETAIL_ID + " TEXT,"+ KEY_INVENTORY_ACTIVITY_ID  + " INTEGER, PRIMARY KEY("+ KEY_INVENTORY_ACTIVITY_ID+") )";
    }

    public static void insertBulk(DataBaseHandler db_handle, List<InventoryActivityTable> inventoryActivityData) {

        SQLiteDatabase db = db_handle.getWritableDatabase();
        // drop if exist. we don't override data
        db.execSQL("DROP TABLE IF EXISTS " + InventoryActivityTable.getTableName());

        // create afresh !
        db.execSQL(InventoryActivityTable.getCreateTableCommand());

        db.beginTransaction();

        try{
            ContentValues values = new ContentValues();
            for ( InventoryActivityTable instance: inventoryActivityData ) {

                values.put(KEY_INVENTORY_ACTIVITY_ID, instance.getInventoryActivityId());
                values.put(KEY_ACTIVITY_TYPE, instance.getActivityType());
                values.put(KEY_SHORTLISTED_INVENTORY_DETAIL_ID, instance.getShortlistedInventoryDetailId());

                db.insert(InventoryActivityTable.getTableName(), null, values);
            }
            db.setTransactionSuccessful();
        }
        finally {
            db.endTransaction();
        }
    }

    }
