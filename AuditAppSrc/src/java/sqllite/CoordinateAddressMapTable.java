package com.machadalo.audit.sqllite;

import android.content.ContentValues;

import java.security.PublicKey;
import java.security.cert.PKIXParameters;

/**
 * Created by Lokesh on 21-06-2017.
 */

// THIS TABLE is creted to map the lat lon with the address

    // this table is created at the start of the app only once and is dropped when th app is uninstalled
    // like inventoryImage Path table
public class CoordinateAddressMapTable {

    public static String TABLE_NAME = "CoordinateAddressMapTable";

    public static String KEY_LAT = "lat";
    public static String KEY_LON = "lon";
    public static String KEY_ADD = "address";

    //empty constructor
    public CoordinateAddressMapTable(){

    }

    //create Tbale query
    public static String getCreateTableCommand(){
        return "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ( "
                + KEY_LAT + " TEXT,"
                + KEY_LON + " TEXT,"
                + KEY_ADD + " TEXT,"
                + " PRIMARY KEY("+KEY_LAT +" , "+KEY_LON+") )";
    }


}
