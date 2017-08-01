package com.machadalo.audit.sqllite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.zip.DeflaterOutputStream;

/**
 * Created by Lokesh on 03-07-2017.
 */



/*
* the QuickCapture is to take onThe spot images by entering details by the user
*
*  so to store these images differently there are two tables
 *
*  QuickImageCaptureTable and QuicKImageCapturePathTable
 *
 */

public class QuickImageCaptureTable {

    public static final String TABLE_NAME = "quickImageTable";

    public static final String KEY_ID = "_id";
    public static final String KEY_PROPOSAL_NAME = "proposalName";
    public static final String KEY_SUPPLIER_NAME = "supplierName";
    public static final String KEY_INVENTORY_NAME = "inventoryName";
    public static final String KEY_ACTIVITY_TYPE = "activityType";


    public String proposlaName;
    public String supplierName;
    public String lat ;
    public String lon;
    public Long _ID;

    public String inventoryName;
    public String acitvityType;
    public String isAmazonUploaded;
    public String imageName;
    public String localPath;
    public String date;


    public Context context;


    public QuickImageCaptureTable() {

    }

    //used also as objects to store deatails
    public QuickImageCaptureTable(String proposlaName,String supplierName , String inventoryName, String acitvityType,Long id) {
        this.supplierName = supplierName;
        this.acitvityType = acitvityType;
        this.inventoryName = inventoryName;
        this.proposlaName = proposlaName;
        this._ID = id;

    }

    //create table command
    public static String getCreateTableCommand() {


        return "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ( " +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                KEY_PROPOSAL_NAME+ " TEXT, "+
                KEY_SUPPLIER_NAME + " TEXT, " +
                KEY_INVENTORY_NAME + " TEXT, " +
                KEY_ACTIVITY_TYPE + " TEXT );";

    }


    public String getSupplierName() {
        return this.supplierName;
    }

    public String getLat() {
        return this.lat;
    }

    public String getLon() {
        return this.lon;
    }

    public String getDate() {
        return this.date;
    }

    public String getLocalPath(){
        return this.localPath;
    }

    public String getImageName(){
        return this.imageName;
    }

    public String getInventoryName(){
        return this.inventoryName;
    }

    public String getAcitvityType(){
        return this.acitvityType;
    }

    public String getIsAmazonUploaded(){
        return this.isAmazonUploaded;
    }

    public void setLat(String lat){
        this.lat = lat;
    }

    public void setLon(String Lon){
        this.lon = Lon;
    }

    public void setImageName(String imageName){
        this.imageName = imageName;
    }

    public void setLocalPath(String localPath){
        this.localPath = localPath;
    }

    public void setSupplierName(String supplierName){
        this.supplierName = supplierName;
    }

    public void setIsAmazonUploaded(String isAmazonUploaded){
        this.isAmazonUploaded = isAmazonUploaded;
    }

    public Long getID(){
        return this._ID;
    }

    public void set_ID(Long id){
        this._ID = id;
    }

    public void setAcitvityType(String acitvityType){
        this.acitvityType = acitvityType;
    }

    public void setInventoryName(String inventoryName){
        this.inventoryName = inventoryName;
    }

    public String getProposlaName(){
        return this.proposlaName;
    }

}
