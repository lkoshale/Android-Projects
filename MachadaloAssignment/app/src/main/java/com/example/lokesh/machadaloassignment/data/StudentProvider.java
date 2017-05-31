package com.example.lokesh.machadaloassignment.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.lokesh.machadaloassignment.data.StudentContract.Student;

/**
 * Created by Lokesh on 30-05-2017.
 */


// Custom Content provider class to do CURD operation in datbase
public class StudentProvider extends ContentProvider {

    //This intger is linked with our Uri we use to query
    //one for whole table and for specific id in table

    // content://authority/tablename
    // content://authority/tablename/# ->id

    public static final int WHOLE_TABLE = 101;

    public static final int SINGLE_ROW = 102;

    // uri matcher to check the uri
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        // adding our uri formats
        sUriMatcher.addURI(Student.AUTHORITY, Student.TABLE_NAME , WHOLE_TABLE);
        sUriMatcher.addURI(Student.AUTHORITY, Student.TABLE_NAME+"/#" , SINGLE_ROW);

    }

    //Global varaible for mainataining database
    private StudentDbHelper mdbHelper;

    @Override
    public boolean onCreate() {
        //initalizing helperClass object
        mdbHelper = new StudentDbHelper(getContext());
        return false;
    }


    //the query method is implemented and returns a cursor to the part of datbase
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        SQLiteDatabase db = mdbHelper.getReadableDatabase();

        Cursor cursor;
        // checking the uri
        int match = sUriMatcher.match(uri);

        switch (match){
            //if to query whole table
            case WHOLE_TABLE :
                    cursor = db.query(Student.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case SINGLE_ROW:
                //query a specific id
                selection = Student._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = db.query(Student.TABLE_NAME,
                                    projection,
                                    selection,
                                    selectionArgs,
                                    null,
                                    null,
                                    sortOrder);

                break;
            default:
                //if not throw exception for illegal arguments
                throw  new IllegalArgumentException("Cannot query the UNKNOWN uri :"+uri.toString());
        }

        //this methods attaches the Listener to out Cursor we need this
        // for our cursor loader class to autoupdate

        //setting notification in cursor
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        getContext().getContentResolver().notifyChange(uri,null);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }


    //can only insert in whole table
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        final int match = sUriMatcher.match(uri);

        switch (match){
            case WHOLE_TABLE :
                    return insertData(uri,values);

            default:
                throw new IllegalArgumentException( "Insertion is not Supported  :"+uri);

        }
    }

    public Uri insertData(Uri uri,ContentValues values){

        SQLiteDatabase db = mdbHelper.getWritableDatabase();
        long ID_this =-1;
        //this part calculates the total and average to store in the dtabase for
        //isert method
        int marks_hindi = values.getAsInteger(Student.COLUMN_HINDI);
        int marks_english = values.getAsInteger(Student.COLUMN_ENGLISH);

        // we laso check the bounds on the vlues we get if they are valid
        if(marks_english >= 0 && marks_english <=100 && marks_hindi>=0 && marks_hindi<=100) {

            int total = getTotal(marks_hindi, marks_english) ;
            float avg = getAVG(marks_hindi, marks_english);

            String str = Integer.toString(marks_hindi)+" "+Integer.toString(marks_english)+ " "+Integer.toString(total);

            values.put(Student.COLUMN_TOTAL,total);
            values.put(Student.ColUMN_AVG,avg);

            ID_this = db.insert(Student.TABLE_NAME, null, values);
        }

        //notifies the loader manger if cursor changes or modifies
        getContext().getContentResolver().notifyChange(uri,null);

        return ContentUris.withAppendedId(uri,ID_this);
    }

    // delte method only applicable for whole table
    @Override
    public int delete(@NonNull Uri uri, @Nullable String where, @Nullable String[] whereArgs) {

        SQLiteDatabase db = mdbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match){
            case WHOLE_TABLE:
                getContext().getContentResolver().notifyChange(uri,null);
                return db.delete(Student.TABLE_NAME,where,whereArgs);
            default:
                throw new IllegalArgumentException("could not be deleted check uri :"+uri);
        }


    }


    // in this app we are only updating a singla data at once
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {

        SQLiteDatabase db = mdbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        //vlidate the uri
        switch (match){
            case SINGLE_ROW :
                {
                    // calcluate the new total and average if the marks field updated
                    if(values.containsKey(Student.COLUMN_HINDI) == true && values.containsKey(Student.COLUMN_ENGLISH) ==true) {

                        int hindi_marks = values.getAsInteger(Student.COLUMN_HINDI);
                        int english_marks = values.getAsInteger(Student.COLUMN_ENGLISH);
                        int total = getTotal(hindi_marks, english_marks);
                        float avg = getAVG(hindi_marks, english_marks);

                        values.put(Student.COLUMN_TOTAL, total);
                        values.put(Student.ColUMN_AVG, avg);
                    }

                    //notify the loader manager
                    getContext().getContentResolver().notifyChange(uri,null);
                    return db.update(Student.TABLE_NAME,values,selection,selectionArgs);


                }

            default:
                throw new IllegalArgumentException("could not be updated check uri :"+uri);
        }

    }

    //returns the avg
    public float getAVG(int a,int b){
        return (float)(a+b)/2;
    }

    //returns the total
    public int getTotal(int a,int b){
        return a+b;
    }

}
