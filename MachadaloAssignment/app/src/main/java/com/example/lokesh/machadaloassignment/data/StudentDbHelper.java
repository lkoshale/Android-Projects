package com.example.lokesh.machadaloassignment.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.lokesh.machadaloassignment.data.StudentContract.Student;

/**
 * Created by Lokesh on 30-05-2017.
 */


// helper Class which exted the SQLiteOpenHelper Class
public class StudentDbHelper extends SQLiteOpenHelper {

    //datbase version 1
    private static final int DATABASE_VERSION = 1;
    //name of datbase
    private static final String DATABASE_NAME = "student.db";

    //for logingi in verbose
    public String LOG_TAG = StudentDbHelper.class.getSimpleName();

    //Constructor
    public StudentDbHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        //sql command to create a table

        // CRAETE TABLE student( <col-name>,<Col2-name>..) VALUES (<adta-type1>...);
        String CreateTable = "CREATE TABLE "+ Student.TABLE_NAME + " ( "+
                                Student._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                                Student.COLUMN_NAME + " TEXT NOT NULL, "+
                                Student.COLUMN_AGE + " INTEGER NOT NULL, "+
                                Student.COLUMN_GENDER + " INTEGER NOT NULL, "+
                                Student.COLUMN_HINDI + " INTEGER NOT NULL DEFAULT 0, "+
                                Student.COLUMN_ENGLISH + " INTEGER NOT NULL DEFAULT 0, "+
                                Student.COLUMN_TOTAL + " INTEGER NOT NULL DEFAULT 0, "+
                                Student.ColUMN_AVG + " REAL DEFAULT 0 );";

        //execute the create table command
        db.execSQL(CreateTable);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Do nothing not upgrading datbase for this app
    }
}
