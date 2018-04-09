package com.example.root.mynavigationdrawer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;

/**
 * Created by root on 28/12/15.
 */
public class ImageDetailsHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = DatabaseHelper.DATABASE_VERSION;
    public static final String DATABASE_NAME = DatabaseHelper.DATABASE_NAME;
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String INT_TYPE = " INTEGER";

    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + ImageContract.ImageDetails.TABLE_NAME + "("+

            ImageContract.ImageDetails.KEY_NAME  + TEXT_TYPE + COMMA_SEP +
            ImageContract.ImageDetails.KEY_IMAGE + " BLOB" + COMMA_SEP +
            ImageContract.ImageDetails.COLUMN_CATEGORY_ID  + INT_TYPE + COMMA_SEP +
            ImageContract.ImageDetails.COLUMN_DESCRIPTION + TEXT_TYPE +

            " )";

    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + ImageContract.ImageDetails.TABLE_NAME ;
    public ImageDetailsHelper( Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);


    }
    @Override
    public void onCreate(SQLiteDatabase db) {


        db.execSQL(SQL_DELETE_ENTRIES);
        db.execSQL(SQL_CREATE_ENTRIES);
        System.out.print("calling onCreate() of ImageDetailsHelper() ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
        System.out.print("calling onUpgrade() of ImageDetailsHelper() ");


    }


    public void addImage( ImageDetailsHelper helper, String name, byte[] image) throws SQLiteException {


        try {

            SQLiteDatabase db = helper.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(ImageContract.ImageDetails.KEY_NAME,    name);
            cv.put(ImageContract.ImageDetails.KEY_IMAGE, image);
            db.insert(ImageContract.ImageDetails.TABLE_NAME, null, cv);
            System.out.print( "AddImage() executed successfully....");
        } catch (Exception e) {
              System.out.print ( "Error in addImage(): " + e.getMessage().toString() )  ;
        }
    }



    public Bitmap getImageFromName( ImageDetailsHelper helper, String name ) throws SQLiteException {


        try {


            String query = "SELECT image From " + ImageContract.ImageDetails.TABLE_NAME + "WHERE " + ImageContract.ImageDetails.KEY_NAME + "=" + name;
            SQLiteDatabase db = helper.getWritableDatabase();

            Cursor cur = db.rawQuery(query, null);

            if (cur.moveToFirst()) {

                byte[] imgByte = cur.getBlob(0);
                cur.close();
                Bitmap img = BitMapUtility.getImage(imgByte);
                return img;


            }

            if (cur != null && !cur.isClosed()) {
                cur.close();
            }



        } catch (Exception e) {
            System.out.print("Error in getImageFromName()" + e.getMessage().toString());

        }

        return null;

    }


}
