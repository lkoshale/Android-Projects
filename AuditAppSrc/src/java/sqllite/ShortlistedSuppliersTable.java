package com.machadalo.audit.sqllite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by root on 30/1/17.
 */

public class ShortlistedSuppliersTable {

    public static final String TABLE_NAME = "shortlisted_spaces";
    //  Table Columns names
    public static final String KEY_SHORTLISTED_SPACES_ID = "id";
    public static final String KEY_PROPOSAL_ID = "proposal_id";
    public static final String KEY_SUPPLIER_ID = "supplier_id";
    public static final String KEY_SUPPLIER_CONTENT_TYPE_ID = "supplier_content_type_id";
    public static final String KEY_PHASE = "phase";
    public static final String KEY_LAST_MODIFIED = "last_modified";

    // member variables.
    public String shortlistedSpacesId;
    public String proposalId;
    public String supplierId;
    public String supplierContentTypeId;
    public String phase;
    public String lastModified;



    public ShortlistedSuppliersTable(String shortlisted_spaces_id, String proposal_id, String supplier_id, String supplier_content_type_id, String phase, String last_modified){
        this.shortlistedSpacesId = shortlisted_spaces_id;
        this.proposalId = proposal_id;
        this.supplierId = supplier_id;
        this.supplierContentTypeId = supplier_content_type_id;
        this.phase = phase;
        this.lastModified = last_modified;
    }

    public static String getCreateTableCommand() {
        // Creating Tables
        return "CREATE TABLE " + TABLE_NAME + "("
                + KEY_SHORTLISTED_SPACES_ID + " TEXT," + KEY_PROPOSAL_ID + " TEXT," + KEY_SUPPLIER_ID + " TEXT,"
                + KEY_LAST_MODIFIED + " TEXT," + KEY_SUPPLIER_CONTENT_TYPE_ID + " TEXT," + KEY_PHASE +
                " TEXT, PRIMARY KEY("+KEY_SHORTLISTED_SPACES_ID+") )";
    }

    public static String getTableName(){
        return TABLE_NAME;
    }

    public String getShortlistedSpacesId(){
        return shortlistedSpacesId;
    }

    public String getProposaId(){
        return proposalId;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public String getKeySupplierContentTypeId(){
        return supplierContentTypeId;
    }
    public String getPhase() {
        return phase;
    }
    public String getLastModified(){
        return lastModified;
    }

    public  static void insertBulk(DataBaseHandler db_handle, List<ShortlistedSuppliersTable> shortlistedSuppliersData){
        /*
            This functions handles Bulk insert.
        */

        SQLiteDatabase db = db_handle.getWritableDatabase();
        // drop if exist. we don't override data
        db.execSQL("DROP TABLE IF EXISTS " + ShortlistedSuppliersTable.getTableName());
        // create afresh !
        db.execSQL(ShortlistedSuppliersTable.getCreateTableCommand());

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            for ( ShortlistedSuppliersTable instance: shortlistedSuppliersData) {
                values.put(KEY_SHORTLISTED_SPACES_ID, instance.getShortlistedSpacesId());
                values.put(KEY_LAST_MODIFIED, instance.getLastModified());
                values.put(KEY_SUPPLIER_ID, instance.getSupplierId());
                values.put(KEY_SUPPLIER_CONTENT_TYPE_ID, instance.getKeySupplierContentTypeId());
                values.put(KEY_PHASE, instance.getPhase());
                values.put(KEY_PROPOSAL_ID, instance.getProposaId());

                db.insert(ShortlistedSuppliersTable.getTableName(), null, values);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

    }

    public static String getKeyShortlistedSpacesId() {
        return KEY_SHORTLISTED_SPACES_ID;
    }

    public static String getKeyProposalId() {
        return KEY_PROPOSAL_ID;
    }

    public static String getKeySupplierId() {
        return KEY_SUPPLIER_ID;
    }

    public static String getKeyPhase() {
        return KEY_PHASE;
    }

    public static String getKeyLastModified() {
        return KEY_LAST_MODIFIED;
    }

    public String getProposalId() {
        return proposalId;
    }

    public String getSupplierContentTypeId() {
        return supplierContentTypeId;
    }

    public static JSONObject getShortlistedSuppliersPerDay(DataBaseHandler db_handle, String requestedDate) {

        SQLiteDatabase db = db_handle.getReadableDatabase();
        Cursor cursor = null;
        List<String> shortlistedSupplierIds = new ArrayList<>();
        JSONObject mainNode = new JSONObject();
        JSONObject childNode = new JSONObject();

        String query = " select " +
                 "a." + KEY_SHORTLISTED_SPACES_ID + " as ssid, " +
                 "a." + KEY_SUPPLIER_ID + ", " +
                 "a." + KEY_PROPOSAL_ID + ", " +
                 "a." + KEY_SUPPLIER_CONTENT_TYPE_ID  +
                " from " + getTableName() + " as a" +
                " INNER JOIN " + ShortlistedInventoryDetailsTable.getTableName() + " as b" +
                " ON a." + getKeyShortlistedSpacesId() + "= b." + ShortlistedInventoryDetailsTable.getKeyShortlistedSpacesId() +
                " INNER JOIN " + InventoryActivityTable.getTableName() + " as c " +
                " ON b." + ShortlistedInventoryDetailsTable.getKeyShortlistedInventoryDetailsId() + "= c." +
                InventoryActivityTable.getKeyShortlistedInventoryDetailId() + " INNER JOIN " + InventoryActivityAssignmentTable.getTableName() + " as d " +
                " ON c." + InventoryActivityTable.getKeyInventoryActivityId() + " = d." + InventoryActivityAssignmentTable.getKeyInventoryActivityId() +
                " WHERE d." + InventoryActivityAssignmentTable.getKeyInventoryActivityDate() + "='" + requestedDate + "'";

        Log.d("Query", query);




        try {

            cursor = db.rawQuery(query, null) ;
            Log.d("RowsFound", Integer.toString(cursor.getCount()));

            if ( cursor.moveToFirst() ) {
                do {

                    String shortlistedSpaceId = cursor.getString(cursor.getColumnIndex("ssid"));
                    String supplierId = cursor.getString(cursor.getColumnIndex(KEY_SUPPLIER_ID));
                    String supplierContentType = cursor.getString(cursor.getColumnIndex(KEY_SUPPLIER_CONTENT_TYPE_ID));
                    String proposalId = cursor.getString(cursor.getColumnIndex(KEY_PROPOSAL_ID));

                    String QueryForProposlName = " SELECT * FROM "+ ProposalTable.TABLE_NAME + " WHERE "
                            + ProposalTable.KEY_PROPOSAL_ID + " = \'"+proposalId+"\' ;";

                    SQLiteDatabase proposalTableDB = db_handle.getReadableDatabase();
                    Cursor cursor1 = proposalTableDB.rawQuery(QueryForProposlName,null);

                    String proposalName = "proposalName";

                    while (cursor1.moveToNext()){
                        proposalName = cursor1.getString(cursor1.getColumnIndex(ProposalTable.KEY_PROPOSAL_NAME));
                        Log.e("IN SSTable",proposalName);
                    }


                    JSONObject content = new JSONObject();
                    content.put("supplier_id", supplierId);
                    content.put("proposal_id", proposalId);
                    content.put("content_type_id", supplierContentType);
                    content.put("proposal_name",proposalName);

                    childNode.put(shortlistedSpaceId, content);
                    shortlistedSupplierIds.add(supplierId);

                }while (cursor.moveToNext());
            }
            mainNode.put("shortlisted_suppliers", childNode);

        }catch (Exception e) {

            Log.d(TABLE_NAME, e.getMessage()) ;

        }
        finally {
            if ( cursor != null) {
                cursor.close();
            }
        }

        JSONObject basicSupplierTableData = BasicSupplierTable.getBasicSupplierTableData(db_handle, shortlistedSupplierIds);

        if ( basicSupplierTableData == null ) {
            Log.d(TABLE_NAME, "No Data got from BasicSupplierTable");
            return mainNode;
        }
        try {

            JSONObject shortlistedSuppliers = mainNode.getJSONObject("shortlisted_suppliers");
            Iterator<?> shortlistedSupplierKeys = shortlistedSuppliers.keys();

            while (shortlistedSupplierKeys.hasNext()) {

                String shortlistedSupplierId = (String) shortlistedSupplierKeys.next();
                if (shortlistedSuppliers.get(shortlistedSupplierId) instanceof JSONObject) {

                    JSONObject shortlistedSupplier = (JSONObject) shortlistedSuppliers.get(shortlistedSupplierId);
                    String supplierId = shortlistedSupplier.getString("supplier_id");
                    shortlistedSupplier.put("supplier_detail", basicSupplierTableData.get(supplierId));

                }
            }

            return mainNode;

        }catch (Exception e ) {
            Log.d(TABLE_NAME, e.getMessage());
        }

        return null;
    }


}

