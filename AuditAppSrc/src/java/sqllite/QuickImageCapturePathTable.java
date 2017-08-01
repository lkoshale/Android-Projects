package com.machadalo.audit.sqllite;

import android.content.Context;

/**
 * Created by Lokesh on 05-07-2017.
 */

/*
*  this table store the path of images from QuickCaptureTable
*
*  and a foriegnKey QuickImageCaptureID ---->(Id of QuickImgaeCapture) to link both the tables
 */

public class QuickImageCapturePathTable {

    public static final String TABLE_NAME = "quickImagePathTable";

    public static final String KEY_ID = "id";
    public static final String KEY_LOCAL_PATH = "localPath";
    public static final String KEY_IMAGE_NAME = "imageName";
    public static final String KEY_LAT = "lat";
    public static final String KEY_LON = "lon";
    public static final String KEY_IS_AMAZON_UPLOADED = "isAmazonUploaded";
    public static final String KEY_IMAGE_DATE = "date";
    public static final String KEY_QuickImageCaptureID = "Q_ID";


    public String date;
    public String lat ;
    public String lon;

    public Context context;


    public QuickImageCapturePathTable() {

    }

    //create table command
    public static String getCreateTableCommand() {

        return "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ( " +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                KEY_IMAGE_DATE + " TEXT, " +
                KEY_IMAGE_NAME + " TEXT, " +
                KEY_LOCAL_PATH + " TEXT, " +
                KEY_LAT + " TEXT, " +
                KEY_LON + " TEXT, " +
                KEY_QuickImageCaptureID +" TEXT, "+
                KEY_IS_AMAZON_UPLOADED + " TEXT );";

    }

}
