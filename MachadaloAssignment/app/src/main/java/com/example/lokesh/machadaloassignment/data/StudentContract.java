package com.example.lokesh.machadaloassignment.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Lokesh on 30-05-2017.
 */

/*
* final class so cant be extended or
*  implemented
*/

//this is our Contract Class which defines the schema and fields of our SQLite database
public final class StudentContract {

    // this class cannot be initailized
    private StudentContract(){

    }

    //the claas which defines our Schema for the SQLite datbase Table
    public static final class Student implements BaseColumns {

        //Authority
        public static final String AUTHORITY = "com.example.lokesh.machadaloassignment";

        //tAbLE NAME
        public static final String TABLE_NAME = "student";

        // the URi we use to query whole table
        //of form
        //  content://com.example.lokesh.machadaloassignment/student
        public static final Uri CONTENT_URI = Uri.parse("content://"+AUTHORITY+"/"+TABLE_NAME);

        //the URi schema for specific id
        // content://com.example.lokesh.machadaloassignment/student/# ->id
        public static final Uri CONTENT_URI_ID = Uri.parse("content://"+AUTHORITY+"/"+TABLE_NAME +"/#");

        //the _ID column
        public static final String _ID = BaseColumns._ID;

        // NAME COLUMN as TEXT
        public static final String COLUMN_NAME = "name";

        //AGE COlUMN as INTEGER
        public static final String COLUMN_AGE = "age";

        //GENDER COLUMN as INTEGER will map 0,1 with the gender
        public static final String COLUMN_GENDER = "gender";

        //HINDI MARKS
        public static final String COLUMN_HINDI = "hindi";

        //ENGLISH MARkS
        public static final String COLUMN_ENGLISH = "english";

        //TOATL
        public static final String COLUMN_TOTAL = "total";

        //AVERAGE
        public static final String ColUMN_AVG = "avg";

        //Defining the Gender Values for Male and Female and unknown
        public static final int GENDER_MALE = 1;
        public static final int GENDER_FEMALE = 2;
        public static final int GENDER_UNKNOWN = 0;


    }

}
