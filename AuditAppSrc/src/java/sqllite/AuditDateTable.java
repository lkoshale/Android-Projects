package com.machadalo.audit.sqllite;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.machadalo.audit.extras.Data;

import java.util.List;

/**
 * Created by root on 2/2/17.
 */

public class AuditDateTable {


    public static final String TABLE_NAME = "audit_date";

    //  Table Columns names
    public static final String KEY_SHORTLISTED_INVENTORY_DETAILS_ID = "shortlisted_inventory_details_id";
    public static final String KEY_AUDIT_DATE = "audit_date";
    public static final String KEY_AUDITED_BY = "audited_by";
    public static final String KEY_AUDIT_ID = "id";

    public String shortlistedInventoryDetailsId;
    public String auditDate;
    public String auditedBy;
    public String auditId;

    public String getShortlistedInventoryDetailsId(){
        return shortlistedInventoryDetailsId;
    }

    public String getAuditDate(){
        return auditDate;
    }

    public String getAuditedBy(){
        return auditedBy;
    }

    public String getAuditId(){
        return auditId;
    }

    public void setShortlistedInventoryDetailsId(String shortlistedInventoryDetailsId){
        this.shortlistedInventoryDetailsId = shortlistedInventoryDetailsId;
    }

    public void setAuditDate( String auditDate ) {

        this.auditDate  = auditDate;
    }
    public void setAuditedBy( String auditedBy) {
        this.auditedBy = auditedBy;
    }

    public static String getTableName(){
        return TABLE_NAME;
    }

    public static String getKeyShortlistedInventoryDetailsId(){
        return KEY_SHORTLISTED_INVENTORY_DETAILS_ID;
    }
    public static String getKeyAuditId(){
        return KEY_AUDIT_ID;
    }
    public static String getKeyAuditDate(){
        return KEY_AUDIT_DATE;
    }

    public static String getCreateTableCommand(){
        return "CREATE TABLE " + TABLE_NAME + "("
                + KEY_SHORTLISTED_INVENTORY_DETAILS_ID + " TEXT," + KEY_AUDIT_DATE + " TEXT,"
                + KEY_AUDITED_BY + " TEXT," + KEY_AUDIT_ID + " INTEGER, PRIMARY KEY("+ KEY_AUDIT_ID+") )";
    }

    public static void insertBulk(DataBaseHandler db_handle, List<AuditDateTable> auditDateData) {

        SQLiteDatabase db = db_handle.getWritableDatabase();
        // drop if exist. we don't override data
        db.execSQL("DROP TABLE IF EXISTS " + AuditDateTable.getTableName());
        // create afresh !
        db.execSQL(AuditDateTable.getCreateTableCommand());

        db.beginTransaction();

        try{
            ContentValues values = new ContentValues();
            for ( AuditDateTable instance: auditDateData ) {

                values.put(KEY_SHORTLISTED_INVENTORY_DETAILS_ID, instance.getShortlistedInventoryDetailsId());
                values.put(KEY_AUDIT_DATE, instance.getAuditDate());
                values.put(KEY_AUDITED_BY, instance.getAuditedBy());

                db.insert(AuditDateTable.getTableName(), null, values);
            }
            db.setTransactionSuccessful();
        }
        finally {
            db.endTransaction();
        }
    }


}
