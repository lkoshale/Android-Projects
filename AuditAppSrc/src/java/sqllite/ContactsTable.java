package com.machadalo.audit.sqllite;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.location.LocationListener;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarException;

/**
 * Created by Lokesh on 15-07-2017.
 */

public class ContactsTable {

    public static final String TABLE_NAME = "contact";

    public static final String KEY_ID = "_id";
    public static final String KEY_SUPPLIER_ID = "supplier_id";
    public static final String KEY_NAME = "name";
    public static final String KEY_MOBILE = "mobile";
    public static final String KEY_CONTACT_TYPE = "contact_type";
    public static final String KEY_STD_CODE = "std_code";
    public static final String KEY_LAND_LINE = "land_line";

    public ContactsTable(){

    }

    public static String getTableName(){
        return TABLE_NAME;
    }

    public static String getCreateTableCommand() {

        return "CREATE TABLE "+TABLE_NAME + " ( " +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                KEY_NAME + " TEXT, "+
                KEY_CONTACT_TYPE + " TEXT, "+
                KEY_SUPPLIER_ID + " TEXT, "+
                KEY_MOBILE+ " TEXT, "+
                KEY_STD_CODE+ " TEXT, "+
                KEY_LAND_LINE+ " TEXT );";
    }

    public static List<ContentValues> getContactDetails(JSONObject supplierDetail){

        List<ContentValues> insertSet = new ArrayList<>();

        try{

            JSONArray contacts = supplierDetail.getJSONArray("contacts");

            for (int i=0;i<contacts.length();i++){

                JSONObject object = contacts.getJSONObject(i);
                String name = object.getString("name");
                String supplierID = object.getString("object_id");
                String contactType = object.getString("contact_type");
                String mobile = object.getString("mobile");
                String stdCode = object.getString("std_code");
                String landLine = object.getString("landline");

                ContentValues values = new ContentValues();
                values.put(KEY_SUPPLIER_ID,supplierID);
                values.put(KEY_NAME,name);
                values.put(KEY_CONTACT_TYPE,contactType);
                values.put(KEY_MOBILE,mobile);
                values.put(KEY_STD_CODE,stdCode);
                values.put(KEY_LAND_LINE,landLine);

                insertSet.add(values);

            }

        }catch (JSONException e){
            e.printStackTrace();
        }

        return insertSet;

    }

    public static void insertBulk(DataBaseHandler db_handle,List<ContentValues>contactList){

        Log.e("ConTable","insertBulk");

        SQLiteDatabase db = db_handle.getWritableDatabase();

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME +" ;" );
        db.execSQL(getCreateTableCommand());

        for (ContentValues values: contactList){

            db.insert(TABLE_NAME,null,values);

        }

       // db_handle.printTotalRows(TABLE_NAME);

    }


}
