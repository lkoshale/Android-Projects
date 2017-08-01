package com.machadalo.audit.sqllite;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.machadalo.audit.Utils;

import java.util.List;

/**
 * Created by root on 23/2/17.
 */

public class InventoryActivityAssignmentTable {

    public static final String TABLE_NAME = "inventory_activity_assignment";

    //  Table Columns names
    public static final String KEY_INVENTORY_ACTIVITY_ASSIGNMENT_ID = "id";
    public static final String KEY_INVENTORY_ACTIVITY_DATE = "activity_date";
    public static final String KEY_INVENTORY_REASSIGNED_ACTIVITY_DATE = "reassigned_activity_date";
    public static final String KEY_INVENTORY_ACTIVITY_ID = "inventory_activity_id";

    public String inventoryActivityAssignmentId;
    public String activityDate;
    public String inventoryActivityId;
    public String inventoryReassignedActivityDate;

    public static String getKeyInventoryActivityAssignmentId() {
        return KEY_INVENTORY_ACTIVITY_ASSIGNMENT_ID;
    }

    public static String getKeyInventoryActivityDate() {
        return KEY_INVENTORY_ACTIVITY_DATE;
    }

    public static String getKeyInventoryReassignedActivityDate() {
        return KEY_INVENTORY_REASSIGNED_ACTIVITY_DATE;
    }

    public static String getKeyInventoryActivityId() {
        return KEY_INVENTORY_ACTIVITY_ID;
    }

    public String getInventoryActivityAssignmentId() {
        return inventoryActivityAssignmentId;
    }

    public String getActivityDate() {
        return activityDate;
    }

    public String getInventoryActivityId() {
        return inventoryActivityId;
    }

    public String getInventoryReassignedActivityDate() {
        return inventoryReassignedActivityDate;
    }

    public void setInventoryActivityAssignmentId(String inventoryActivityAssignmentId) {
        this.inventoryActivityAssignmentId = inventoryActivityAssignmentId;
    }

    public void setActivityDate(String activityDate) {
        this.activityDate = activityDate;
    }

    public void setInventoryReassignedActivityDate(String inventoryReassignedActivityDate) {
        this.inventoryReassignedActivityDate = inventoryReassignedActivityDate;
    }

    public void setInventoryActivityId(String inventoryActivityId) {
        this.inventoryActivityId = inventoryActivityId;
    }

    public static String getTableName() {
        return TABLE_NAME;
    }

    public static String getCreateTableCommand(){

        return "CREATE TABLE " + TABLE_NAME + "("
                + " TEXT,"
                + KEY_INVENTORY_ACTIVITY_DATE  + " TEXT,"
                + KEY_INVENTORY_REASSIGNED_ACTIVITY_DATE  + " TEXT,"
                + KEY_INVENTORY_ACTIVITY_ID  + " TEXT,"
                + KEY_INVENTORY_ACTIVITY_ASSIGNMENT_ID + " INTEGER, PRIMARY KEY("+ KEY_INVENTORY_ACTIVITY_ASSIGNMENT_ID+") )";
    }

    public static  void insertBulk(DataBaseHandler db_handle, List<InventoryActivityAssignmentTable> inventoryActivityAssignmentTableList) {

        SQLiteDatabase db = db_handle.getWritableDatabase();
        // drop if exist. we don't override data
        db.execSQL("DROP TABLE IF EXISTS " + InventoryActivityAssignmentTable.getTableName());
        // create afresh !
        db.execSQL(InventoryActivityAssignmentTable.getCreateTableCommand());



        db.beginTransaction();

        try{
            ContentValues values = new ContentValues();
            for ( InventoryActivityAssignmentTable instance: inventoryActivityAssignmentTableList ) {

                values.put(KEY_INVENTORY_ACTIVITY_ID, instance.getInventoryActivityId());
                values.put(KEY_INVENTORY_ACTIVITY_ASSIGNMENT_ID, instance.getInventoryActivityAssignmentId());
                values.put(KEY_INVENTORY_ACTIVITY_DATE, instance.getActivityDate());
                values.put(KEY_INVENTORY_REASSIGNED_ACTIVITY_DATE, instance.getInventoryReassignedActivityDate());

                db.insert(getTableName(), null, values);
            }
            db.setTransactionSuccessful();
        }
        finally {
            db.endTransaction();
        }
    }

}


