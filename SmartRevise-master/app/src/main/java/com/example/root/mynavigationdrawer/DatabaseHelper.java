package com.example.root.mynavigationdrawer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;

import java.util.HashMap;
import java.util.Vector;

/**
 * Created by root on 24/12/15.
 */


/*
public class FeedReaderDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "FeedReader.db";

    public FeedReaderDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
*/

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 6;
    public static final String DATABASE_NAME = "smartrevise.db";
    private static final String TEXT_TYPE = " TEXT ";
    private static final String INT_TYPE = " INTEGER ";
    private static final String FK = " FOREIGN KEY ";
    private static final String ON_DELETE_CASCADE = " ON DELETE CASCADE ";
    private static  final String SWITCH_ON_FK = "PRAGMA foreign_keys=ON;";


    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + CategoryContract.Category.TABLE_NAME + " (" + CategoryContract.Category._ID + " INTEGER PRIMARY KEY," +
            CategoryContract.Category.COLUMN_TITLE + TEXT_TYPE  + " )" ;

    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + CategoryContract.Category.TABLE_NAME;


    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if ( !db.isReadOnly() ) {

            db.execSQL( SWITCH_ON_FK ); // switch on  FK

        }
    }

    private static final String SQL_CREATE_ENTRIES_IMAGE_DETAILS = "CREATE TABLE " + ImageContract.ImageDetails.TABLE_NAME + "("+
            // FOREIGN KEY (P_Id) REFERENCES Persons(P_Id)
            ImageContract.ImageDetails._ID +  " INTEGER PRIMARY KEY" + COMMA_SEP +
            ImageContract.ImageDetails.KEY_NAME  + TEXT_TYPE + COMMA_SEP +
            ImageContract.ImageDetails.KEY_IMAGE + " BLOB" + COMMA_SEP +
            ImageContract.ImageDetails.COLUMN_CATEGORY_ID  + INT_TYPE + COMMA_SEP +
            ImageContract.ImageDetails.COLUMN_DESCRIPTION + TEXT_TYPE + COMMA_SEP +
            FK + " (" + ImageContract.ImageDetails.COLUMN_CATEGORY_ID + " )" + "REFERENCES " + CategoryContract.Category.TABLE_NAME + "( " + CategoryContract.Category._ID + " )" +

            ON_DELETE_CASCADE +

            " )";

    private static final String SQL_DELETE_ENTRIES_IMAGE_DETAILS = "DROP TABLE IF EXISTS " + ImageContract.ImageDetails.TABLE_NAME ;

    private static final String SQL_CREATE_ENTRIES_IMAGE_TAGS = "CREATE TABLE " + ImageTag.ImageDetails.TABLE_NAME + " (" +
            ImageTag.ImageDetails.IMAGE_ID + INT_TYPE + COMMA_SEP +
            ImageTag.ImageDetails.TagName  + TEXT_TYPE + COMMA_SEP +
            ImageTag.ImageDetails._ID +  " INTEGER PRIMARY KEY" + COMMA_SEP +
            FK + " (" + ImageTag.ImageDetails.IMAGE_ID + " )" + " REFERENCES " + ImageContract.ImageDetails.TABLE_NAME + "( " + ImageContract.ImageDetails._ID + " )" +
            ON_DELETE_CASCADE +

            " )" ;

    private static final String SQL_DELETE_ENTRIES_IMAGE_TAGS = "DROP TABLE IF EXISTS " + ImageTag.ImageDetails.TABLE_NAME ;


    public DatabaseHelper(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(SQL_DELETE_ENTRIES);
        db.execSQL( SQL_DELETE_ENTRIES_IMAGE_DETAILS);
        db.execSQL(SQL_DELETE_ENTRIES_IMAGE_TAGS);

        db.execSQL(SQL_CREATE_ENTRIES);
        db.execSQL(SQL_CREATE_ENTRIES_IMAGE_DETAILS);
        db.execSQL(SQL_CREATE_ENTRIES_IMAGE_TAGS);
        System.out.print("onCreate() called....");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL(SQL_DELETE_ENTRIES);
        db.execSQL( SQL_DELETE_ENTRIES_IMAGE_DETAILS);
        db.execSQL(SQL_DELETE_ENTRIES_IMAGE_TAGS);

        System.out.print("onUpgrade() called...");
        onCreate(db);

    }

    public void InitializeCategoryDatabase( DatabaseHelper helper, String [] topics) {


/*
        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

// Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(FeedEntry.COLUMN_NAME_ENTRY_ID, id);
        values.put(FeedEntry.COLUMN_NAME_TITLE, title);
        values.put(FeedEntry.COLUMN_NAME_CONTENT, content);

// Insert the new row, returning the primary key value of the new row
        long newRowId;
        newRowId = db.insert(
                FeedEntry.TABLE_NAME,
                FeedEntry.COLUMN_NAME_NULLABLE,
                values);*/

        try {

            SQLiteDatabase db = helper.getWritableDatabase();
            ContentValues values = new ContentValues();

            for ( int i = 0; i < topics.length; i++ ) {

                values.put(CategoryContract.Category.COLUMN_TITLE, topics[i] );
                Long newRowId = db.insert(CategoryContract.Category.TABLE_NAME, null, values);

            }

        }
        catch ( Exception e ) {

            System.out.println( "Error in CreateDatabase() function : " + e.getMessage().toString() ) ;
        }


    }

    public HashMap ReadAndReturnCategoryDatabase( DatabaseHelper helper) {

        HashMap topics = new HashMap<>();


        /*SQLiteDatabase db = mDbHelper.getReadableDatabase();

   // Define a projection that specifies which columns from the database
// you will actually use after this query.
        String[] projection = {
                FeedEntry._ID,
                FeedEntry.COLUMN_NAME_TITLE,
                FeedEntry.COLUMN_NAME_UPDATED,
        ...
        };

// How you want the results sorted in the resulting Cursor
        String sortOrder =
                FeedEntry.COLUMN_NAME_UPDATED + " DESC";

        Cursor c = db.query(
                FeedEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        cursor.moveToFirst();
long itemId = cursor.getLong(
    cursor.getColumnIndexOrThrow(FeedEntry._ID)
);

        */
        try {

            SQLiteDatabase db = helper.getReadableDatabase();
            String[] projection = {  // These coloumns will be returned after successfull match

                    CategoryContract.Category._ID,
                    CategoryContract.Category.COLUMN_TITLE,
            };

            String selection = ""; // columns for WHERE clause
            String[] selectionArgs = {}; // Array of Values for WHERE CLAUSE

            Cursor c = db.query(CategoryContract.Category.TABLE_NAME, projection, selection, selectionArgs, null, null, null);

            c.moveToFirst();

            do {

                Long itemid = c.getLong(c.getColumnIndexOrThrow(CategoryContract.Category._ID));
                String title = c.getString(c.getColumnIndexOrThrow(CategoryContract.Category.COLUMN_TITLE));

                topics.put ( title, String.valueOf( itemid)) ;
                System.out.println("id= " + itemid + " Title= " + title);

            } while ( c.moveToNext() != false);


        }
        catch ( Exception e ) {

            System.out.println( "Error in ReadDatabase() function : " + e.getMessage().toString() ) ;

        }

        return topics;

    }

    public Long getCategoryIdFromName( DatabaseHelper helper , String name) {

        try {


            Long itemid;
            SQLiteDatabase db = helper.getReadableDatabase();
            String[] projection = {  // These coloumns will be returned after successfull match
                    CategoryContract.Category._ID
            };

            String selection = "title"; // columns for WHERE clause
            String[] selectionArgs = { name}; // Array of Values for WHERE CLAUSE

            Cursor c = db.query(CategoryContract.Category.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
            if ( c != null ) {


                c.moveToFirst();
                itemid = c.getLong(c.getColumnIndexOrThrow(CategoryContract.Category._ID));
                System.out.println("found item id= " + itemid);
                return itemid;

            }
            else {

                return  -1L;

            }

        }
        catch ( SQLiteException e ) {
            System.out.print("Error in getCategoryFromName() function: " + e.getMessage().toString());


        }

        return  -1L;

    }

    public void DeleteCategoryDatabase( DatabaseHelper helper ) {

        /*
                // Define 'where' part of query.
                String selection = FeedEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?";
        // Specify arguments in placeholder order.
                String[] selectionArgs = { String.valueOf(rowId) };
        // Issue SQL statement.
                db.delete(table_name, selection, selectionArgs);

                */

        try {

            SQLiteDatabase db = helper.getReadableDatabase();
            db.delete( CategoryContract.Category.TABLE_NAME, null, null) ;



        }
        catch ( Exception e ) {

            System.out.println("Error in DeleteDatabase(): " + e.getMessage().toString()
            ) ;

        }


    }


    public void addImage( DatabaseHelper helper, String name, byte[] image, String description, String category_id) throws SQLiteException  { //
        SQLiteDatabase db = helper.getWritableDatabase();

        try {

            db.beginTransaction();

            ContentValues cv = new ContentValues();
            cv.put(ImageContract.ImageDetails.KEY_NAME,    name);
            //cv.put(ImageContract.ImageDetails.KEY_IMAGE, image);
            cv.put(ImageContract.ImageDetails.COLUMN_DESCRIPTION, description) ; // adding Description

            if ( category_id != "") {

                cv.put(ImageContract.ImageDetails.COLUMN_CATEGORY_ID, category_id);
            }
            else {

                System.out.print( "null found as category_id for this image.");
            }
            db.insert(ImageContract.ImageDetails.TABLE_NAME, null, cv);
            System.out.print("AddImage() executed successfully....");
            System.out.print("\n name= " + name + "\n image=" + image + "\n description=" + description + "\n category_id=" + category_id);

            db.setTransactionSuccessful();

        } catch (Exception e) {
            System.out.print("Error in addImage(): " + e.getMessage().toString())  ;
        }

        finally {

            db.endTransaction();

        }

    }



    public Bitmap getImageFromName( DatabaseHelper helper, String name ) throws SQLiteException {


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

    public ImageDataStructure[]  getImageDetails( DatabaseHelper helper, String id ) throws SQLiteException {


        try {


            SQLiteDatabase db = helper.getReadableDatabase();
            String[] projection = {  // These coloumns will be returned after successfull match

                    ImageContract.ImageDetails.KEY_NAME,
                    //ImageContract.ImageDetails.COLUMN_CATEGORY_ID,
                    ImageContract.ImageDetails.COLUMN_DESCRIPTION,
                    ImageContract.ImageDetails._ID

            };

            String selection = ImageContract.ImageDetails.COLUMN_CATEGORY_ID + "=?"; // columns for WHERE clause
            String[] selectionArgs = {id}; // Array of Values for WHERE CLAUSE

            Cursor c = db.query(ImageContract.ImageDetails.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
            c.moveToFirst();
            int i = 0;

            ImageDataStructure [] arr = new ImageDataStructure[ c.getCount() ] ;
            System.out.println ( "size of cursor : " + c.getCount() ) ;

            for ( i = 0; i < c.getCount() ; i++ ) {

                arr[i] = new ImageDataStructure();

            }
            i =  0;


            do {

                //Long itemid = c.getLong(c.getColumnIndexOrThrow(ImageContract.ImageDetails._ID));
                String key_name = c.getString(c.getColumnIndexOrThrow(ImageContract.ImageDetails.KEY_NAME));
                //String category_id = c.getString(c.getColumnIndexOrThrow(ImageContract.ImageDetails.COLUMN_CATEGORY_ID));
                String description =  c.getString(c.getColumnIndexOrThrow(ImageContract.ImageDetails.COLUMN_DESCRIPTION));
                String image_id =    c.getString(c.getColumnIndexOrThrow(ImageContract.ImageDetails._ID));

                System.out.println("printing row:---> " + i);
                // System.out.println("id=" + itemid );
                System.out.println("fetched key_name=" + key_name );
                //System.out.println("fetched category_id=" + category_id );
                System.out.println("fetched description=" + description );
                System.out.println("fetched id =" + image_id );
                System.out.println ( "--------------------------------------------------") ;
                arr[i].description = description;
                //arr[i].id = image_id;
                //arr[i].img_byte_array = img;  // image in byte[] form
                arr[i].path  = key_name; // path of the image
                arr[i].id = image_id;
                //arr[i].img = BitMapUtility.getImage( img ) ; // image in BitMap  form .
                i = i + 1;

            } while ( c.moveToNext() != false);

            return arr;

        }
        catch ( Exception e ) {

            System.out.println("Error in ReadDatabase() function : " + e.getMessage().toString()) ;

        }



        return  null;


    }


    public void PrintImageDatabase( DatabaseHelper helper) {


        /*SQLiteDatabase db = mDbHelper.getReadableDatabase();

   // Define a projection that specifies which columns from the database
// you will actually use after this query.
        String[] projection = {
                FeedEntry._ID,
                FeedEntry.COLUMN_NAME_TITLE,
                FeedEntry.COLUMN_NAME_UPDATED,
        ...
        };

// How you want the results sorted in the resulting Cursor
        String sortOrder =
                FeedEntry.COLUMN_NAME_UPDATED + " DESC";

        Cursor c = db.query(
                FeedEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        cursor.moveToFirst();
long itemId = cursor.getLong(
    cursor.getColumnIndexOrThrow(FeedEntry._ID)
);

        */
        try {

            SQLiteDatabase db = helper.getReadableDatabase();
            String[] projection = {  // These coloumns will be returned after successfull match

                    ImageContract.ImageDetails.KEY_NAME,
                    ImageContract.ImageDetails.KEY_IMAGE,
                    ImageContract.ImageDetails.COLUMN_CATEGORY_ID,
                    ImageContract.ImageDetails.COLUMN_DESCRIPTION,

            };

            String selection = ""; // columns for WHERE clause
            String[] selectionArgs = {}; // Array of Values for WHERE CLAUSE

            Cursor c = db.query(ImageContract.ImageDetails.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
            c.moveToFirst();
            int i = 1;

            do {

                //Long itemid = c.getLong(c.getColumnIndexOrThrow(ImageContract.ImageDetails._ID));
                String key_name = c.getString(c.getColumnIndexOrThrow(ImageContract.ImageDetails.KEY_NAME));
                String key_image = c.getString(c.getColumnIndexOrThrow(ImageContract.ImageDetails.KEY_IMAGE));
                String category_id = c.getString(c.getColumnIndexOrThrow(ImageContract.ImageDetails.COLUMN_CATEGORY_ID));
                String description =  c.getString(c.getColumnIndexOrThrow(ImageContract.ImageDetails.COLUMN_DESCRIPTION));

                System.out.println("printing row " + i);
               // System.out.println("id=" + itemid );
                System.out.println("key_name=" + key_name );
                System.out.println("key_image=" + key_image );
                System.out.println("category_id=" + category_id );
                System.out.println("description=" + description );
                i = i + 1;
                System.out.println ( "--------------------------------------------------") ;

            } while ( c.moveToNext() != false);


        }
        catch ( Exception e ) {

            System.out.println("Error in ReadDatabase() function : " + e.getMessage().toString()) ;

        }


    }

    public void DeleteImageDatabase( DatabaseHelper helper ) {

        /*
                // Define 'where' part of query.
                String selection = FeedEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?";
        // Specify arguments in placeholder order.
                String[] selectionArgs = { String.valueOf(rowId) };
        // Issue SQL statement.
                db.delete(table_name, selection, selectionArgs);

                */

        try {

            SQLiteDatabase db = helper.getReadableDatabase();
            db.delete( ImageContract.ImageDetails.TABLE_NAME, null, null) ;


        }
        catch ( Exception e ) {

            System.out.println("Error in DeleteImageDatabase(): " + e.getMessage().toString()
            ) ;

        }


    }

    public  void InsertTagsForImage ( DatabaseHelper helper, String image_id, Vector<String> tags) {


        SQLiteDatabase db = helper.getWritableDatabase();

        try {

            db.beginTransaction();
            ContentValues values = new ContentValues();

            for ( int i = 0; i < tags.size(); i++ ) {

                values.put(ImageTag.ImageDetails.IMAGE_ID, image_id );
                values.put(ImageTag.ImageDetails.TagName, tags.elementAt(i) ) ;
                Long newRowId = db.insert(ImageTag.ImageDetails.TABLE_NAME, null, values);

            }
            db.setTransactionSuccessful();

        }
        catch ( Exception e ) {

            System.out.println( "Error in InsertTagsForImage() function : " + e.getMessage().toString() ) ;
        }
        finally {

            db.endTransaction();

        }

    }

    public String [] getTagsForImage ( DatabaseHelper helper , String image_id ) { // get all tags associated with this image id


        try {

            SQLiteDatabase db = helper.getReadableDatabase();
            String[] projection = {  // These coloumns will be returned after successfull match

                    ImageTag.ImageDetails.TagName

            };

            String selection = ImageTag.ImageDetails.IMAGE_ID + "=?" ;                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           // columns for WHERE clause
            String[] selectionArgs = {image_id}; // Array of Values for WHERE CLAUSE

            Cursor c = db.query(ImageTag.ImageDetails.TABLE_NAME,  projection, selection, selectionArgs, null, null, null);
            c.moveToFirst();
            System.out.println ( "count = " + c.getCount() ) ;
            String [] tags  = new String [c.getCount()] ;
            int i = 0;

            do {

                //Long itemid = c.getLong(c.getColumnIndexOrThrow(ImageContract.ImageDetails._ID));
                String tag  = c.getString(c.getColumnIndexOrThrow( ImageTag.ImageDetails.TagName));

                System.out.println("printing row " + i);
                tags[i] = tag ;
                // System.out.println("id=" + itemid );
                System.out.println("tag name for image with image id =" + image_id  +  "   is=" + tag );

                i = i + 1;

            } while ( c.moveToNext() != false);

            return tags;

        }
        catch ( Exception e ) {

            System.out.println("Error in getTagsForImage() function : " + e.getMessage().toString()) ;

        }

        return null;


    }


    public void update_description ( DatabaseHelper helper, String image_id, String desc) {


        try {

            SQLiteDatabase db = helper.getWritableDatabase();
            String[] projection = {  // These coloumns will be returned after successfull match

            };

            String selection = ImageContract.ImageDetails._ID + "=?" ;                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           // columns for WHERE clause
            String[] selectionArgs = {image_id}; // Array of Values for WHERE CLAUSE
            ContentValues cv = new ContentValues();
            cv.put(ImageContract.ImageDetails.COLUMN_DESCRIPTION, desc); //These Fields should be your String values of actual column names
            db.update(ImageContract.ImageDetails.TABLE_NAME, cv, selection, selectionArgs) ;
            System.out.print("Updated the new Description");


        }
        catch ( Exception e ) {

            System.out.println("Error in update_description function() : " + e.getMessage().toString()) ;

        }



    }


    public String getImageDescription( DatabaseHelper helper, String image_id) { // retunrs description of image, given image_id


        try {

            SQLiteDatabase db = helper.getReadableDatabase();
            String[] projection = {  // These coloumns will be returned after successfull match

                    ImageContract.ImageDetails.COLUMN_DESCRIPTION,

            };

            String selection = ImageContract.ImageDetails._ID + "=?"; // columns for WHERE clause
            String[] selectionArgs = {image_id}; // Array of Values for WHERE CLAUSE

            Cursor c = db.query(ImageContract.ImageDetails.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
            c.moveToFirst();
            String description = c.getString(c.getColumnIndexOrThrow(ImageContract.ImageDetails.COLUMN_DESCRIPTION));

            return description;


        } catch (Exception e) {

            System.out.println("Error in getImageDescription() function : " + e.getMessage().toString());

        }

        return null;

    }

    public Boolean DeleteAlltagsForImage( DatabaseHelper helper, String image_id) { // deletes all tags for given image id

        try {

            SQLiteDatabase db = helper.getReadableDatabase();
            String WhereClause = ImageTag.ImageDetails.IMAGE_ID + "=?";
            String  []  WhereArgs  = { image_id } ;
            db.delete( ImageTag.ImageDetails.TABLE_NAME, WhereClause, WhereArgs) ;
            return true;

        }
        catch ( Exception e ) {

            System.out.println("Error in DeleteAllTagsForImage): " + e.getMessage().toString() ) ;
            return false;

        }

    }


    public Boolean DeleteDescription( DatabaseHelper helper, String image_id) { /// deletes description correspinding to given image id; sets to null ofcourse !

        try{

            SQLiteDatabase db = helper.getWritableDatabase();
            String WhereClause = ImageContract.ImageDetails._ID  + "=?";
            String []  WhereArgs  = {image_id} ;
            ContentValues cv = new ContentValues( ) ;
            cv.put(ImageContract.ImageDetails.COLUMN_DESCRIPTION, "");
            db.update(ImageContract.ImageDetails.TABLE_NAME, cv, WhereClause, WhereArgs) ;
            return true;
        }

        catch ( Exception e ) {


            System.out.println("Error in DeleteDescription() : " + e.getMessage().toString() );
            return false;

        }


    }
    public Boolean deleteAnImage( DatabaseHelper helper, String image_id) { /// deletes description correspinding to given image id; sets to null ofcourse !


        try{
            SQLiteDatabase db = helper.getWritableDatabase();
            String WhereClause = ImageContract.ImageDetails._ID  + "=?";
            String []  WhereArgs  = {image_id} ;
            db.delete(ImageContract.ImageDetails.TABLE_NAME, WhereClause, WhereArgs) ; // deletes all images associated with this category , deletes all tags associated with those images
            return true;
        }
        catch ( Exception e ) {


            System.out.println("Error in deleteAnImage function () : " + e.getMessage().toString() );
            return false;

        }

    }


    public Boolean AddNewCategory ( DatabaseHelper helper, String name) {

        try {

            SQLiteDatabase db = helper.getWritableDatabase();
            ContentValues cv = new ContentValues( ) ;
            cv.put(CategoryContract.Category.COLUMN_TITLE, name);
            db.insert(CategoryContract.Category.TABLE_NAME, null, cv) ;
            return true;

        }
        catch ( Exception e ) {

            System.out.println ( "error in AddNewCategory() " + e.getMessage().toString() ) ;
            return false;

        }


    }

    public Boolean DeleteImageForCategory ( DatabaseHelper helper, String category_id) {


            try{

                SQLiteDatabase db = helper.getWritableDatabase(); // deletets all image rows where category id = category_id
                String WhereClause = ImageContract.ImageDetails.COLUMN_CATEGORY_ID  + "=?";
                String []  WhereArgs  = {category_id} ;
                db.delete(ImageContract.ImageDetails.TABLE_NAME, WhereClause, WhereArgs) ;
                return true;
            }

            catch ( Exception e ) {


                System.out.println("Error in DeleteImageForCategory() : " + e.getMessage().toString() );
                return false;

            }

    }

    public Boolean DeleteCategoryCascade( DatabaseHelper helper, String category_id) { // cascade delete on Category Table

        try{

            SQLiteDatabase db = helper.getWritableDatabase(); // deletets all image rows where category id = category_id
            String WhereClause = CategoryContract.Category._ID + "=?";
            String []  WhereArgs  = {category_id} ;
            db.delete(CategoryContract.Category.TABLE_NAME, WhereClause, WhereArgs) ; // deletes all images associated with this category , deletes all tags associated with those images
            return true;
        }

        catch ( Exception e ) {

            System.out.println("Error in DeleteCategoryCascade : " + e.getMessage().toString() );
            return false;

        }

    }



    public void addBulkImage( DatabaseHelper helper, Vector<String> v ,  String description, String category_id) throws SQLiteException  { //
        SQLiteDatabase db = helper.getWritableDatabase();

        try {

            db.beginTransaction();

            ContentValues cv = new ContentValues();

            for ( int i = 0; i < v.size(); i++ ) {

                cv.put(ImageContract.ImageDetails.KEY_NAME, v.get(i) );;
                cv.put(ImageContract.ImageDetails.COLUMN_DESCRIPTION, description); // adding Description
                cv.put(ImageContract.ImageDetails.COLUMN_CATEGORY_ID, category_id);
                db.insert(ImageContract.ImageDetails.TABLE_NAME, null, cv);

            }


            db.setTransactionSuccessful();

        } catch (Exception e) {
            System.out.print("Error in addBulkImage(): " + e.getMessage().toString())  ;
        }

        finally {

            db.endTransaction();

        }

    }



}
