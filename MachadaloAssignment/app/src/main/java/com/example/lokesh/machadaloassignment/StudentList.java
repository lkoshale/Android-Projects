package com.example.lokesh.machadaloassignment;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.app.LoaderManager;
import android.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;


import com.example.lokesh.machadaloassignment.data.StudentContract.Student;

import com.example.lokesh.machadaloassignment.data.StudentContract;
import com.example.lokesh.machadaloassignment.data.StudentDbHelper;
import com.example.lokesh.machadaloassignment.data.StudentProvider;

import java.util.Random;


// The main Activity class of the app which implements the LoaderMangaer with GEnric type
// cursor for the use of cursor loaders to update our activity as Data changes
public class StudentList extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    //The loader version arbitrily defined as 0
    private static final int STUDENT_LOADER = 0;

    //have to use cursorAdapter all over cllass -> a class variable
    StudentCursorAdapter mCursorAdapter;

    //this string defines the ORDER in SQL Command
    // we used as class variable so if we can sort our list based on use prefrnce
    private String orderByColumn = null;

    //The list view of which display the student list
    private  ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Using intent to start ADD STUDENT DETAIL Activity
                //when fab button clicked
                Intent intent = new Intent(StudentList.this,EditorActivity.class);
                startActivity(intent);

            }
        });

        //this is the function which loads Random data
        //if there is no data present in the database
        CreateDATA();

        /*
        *  setting our listview with our own CursorAdapter class
         */
        listView = (ListView)findViewById(R.id.list_view);

        mCursorAdapter = new StudentCursorAdapter(this,null);

        listView.setAdapter(mCursorAdapter);


        // Adding onClick listener on list items
        //if any item is clicked in the list the Update Activity will start
        // withh filled in details and user can change the details as they want
        listView.setOnItemClickListener( new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // intent fto start edit Activity and we
                // also pass the Uri of the item clicked so we can update the
                //item in database
                Intent intent = new Intent(StudentList.this,EditorActivity.class);
                //appending the id
                Uri CurrentUri = ContentUris.withAppendedId(Student.CONTENT_URI,id);
                //adding the uri
                intent.setData(CurrentUri);
                startActivity(intent);
            }
        });

        //starting our loader manager
        getLoaderManager().initLoader(STUDENT_LOADER,null,this);

    }

    // Activity life cycle method
    @Override
    protected void onStart() {
        super.onStart();

        //this function gets and display the name and avg and marks of
        //the highest grosser student
        setUpperText();

        //prefrence manger to get the current list sorting prefrence of user
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String sort = sharedPreferences.getString(getString(R.string.list_pref_key),getString(R.string.default_sort_value));

        //cases to change the order as user prefrnce
        //this sets the string orderby that we used in query
        if (sort.equals("G")){
            orderByColumn = Student.COLUMN_GENDER + " DESC";
            Log.v("Inside the if :" , "G");
        }
        else if (sort.equals("E")){
            orderByColumn = Student.COLUMN_ENGLISH + " DESC";
            Log.v("Inside the if :" , "E");
        }
        else if (sort.equals("H")){
            Log.v("Inside the if :" , "H");
            orderByColumn = Student.COLUMN_HINDI + " DESC";
        }

        //restarting loader manager
        getLoaderManager().restartLoader(STUDENT_LOADER,null,this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_student_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sort_settings) {
            //when sort setting selected open the preference Activity
            Intent intent = new Intent(this,Settings.class);
            startActivity(intent);
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    //when loader is created
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        //we have to reaturn a Cursor loader for the loader manger

        //the columns we need in query from database
        String[] projection = { Student._ID,
                Student.COLUMN_NAME,
                Student.COLUMN_AGE,
                Student.COLUMN_GENDER,
                Student.COLUMN_HINDI,
                Student.COLUMN_ENGLISH,
                Student.COLUMN_TOTAL,
                Student.ColUMN_AVG};

        //initialising the cursor Loader
        return new CursorLoader(this,
                Student.CONTENT_URI,
                projection,
                null,
                null,
                orderByColumn == null ? Student.COLUMN_NAME : orderByColumn );
        // here we use the orderbyColumn string defined earlier to query data in the
        //order user wants so cursorLoader will get the order in same order
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        //on finish swap loader
                mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
            mCursorAdapter.swapCursor(null);
    }

    // This method gets The Student {name marks } array
    // for highest getting student
    public String[] getMaxAvg(){

        //here we are querying directly from the databse without
        //use of content providers

        //our databse helper class object
        StudentDbHelper myHelper1 = new StudentDbHelper(this);

        SQLiteDatabase db1 = myHelper1.getReadableDatabase();

        //columns we need
        String[] projection = {
                Student._ID,
                Student.COLUMN_NAME,
                Student.COLUMN_HINDI,
                Student.COLUMN_ENGLISH,
                Student.ColUMN_AVG};

        //the where clause
        String selection = Student.ColUMN_AVG + " > 0";

        //using datbase query method directly
        Cursor c1 = db1.query(Student.TABLE_NAME,projection,selection,null,null,null,null);

        //some local variable to get the max
        String name1 = "";
        String name2 = "";
        String name3 = "";

        float max = 0;
        int h = 0;
        int e = 0;

        //traversing the databse with cursors
        while (c1.moveToNext()){
            String n = c1.getString(c1.getColumnIndex(Student.COLUMN_NAME));
            float f1 = c1.getFloat(c1.getColumnIndex(Student.ColUMN_AVG));
            int k = c1.getInt(c1.getColumnIndex(Student.COLUMN_HINDI));
            int l = c1.getInt(c1.getColumnIndex(Student.COLUMN_ENGLISH));
            if(f1>max){
                max = f1;
                name1 = n;
            }
            if(k>h){
                h = k;
                name2 = n;
            }
            if (l >e){
                e = l;
                name3 = n;
            }

        }

        // this returns a array of string as name marks name marks name marks for avg hindi and eng. resp.
        String[] str = {name1,Float.toString(max),name2,Integer.toString(h),name3,Integer.toString(e)};
        return str;
    }

    //this fnuses above function and sets the values in desired text fields
    void setUpperText(){

        TextView maxmT = (TextView)findViewById(R.id.main_perf);
        TextView mH = (TextView)findViewById(R.id.hin_perf);
        TextView mE = (TextView)findViewById(R.id.eng_perf);

        String[] str = getMaxAvg();

        maxmT.setText("Over ALL Best : "+str[0]+" with avg "+str[1]);
        mH.setText("In Hindi " +str[2]+ " ("+str[3] +" )");
        mE.setText("In English " +str[4]+ " ("+str[5] +" )");

    }



    //creates 10 random data for the database
    public void CreateDATA(){

        //querying the databse with Content provider
        String[] projection = {
                Student._ID,
                Student.COLUMN_NAME};

        String selection = Student._ID+ "> 0";

        //using content provider for querying in database
        Cursor cursor1 = getContentResolver().query(Student.CONTENT_URI,null,selection,null,null);

        //if not null
        if(cursor1.getCount()>0){
            return;
        }
        else {

            int i = 0;
            //general string name
            String name = "Student-";

            while (i <10) {

                //create new namew
                String str = name+Integer.toString(i);

                //to generate Random Integers
                Random r = new Random();
                int age = r.nextInt(20)+5;
                int hindi = r.nextInt(101);
                int eng = r.nextInt(101);
                int gen = r.nextInt(2)+1;

                //Adding ALL the values in database using Content Provider
                ContentValues contentValues = new ContentValues();

                contentValues.put(Student.COLUMN_NAME,str);
                contentValues.put(Student.COLUMN_AGE, age);
                contentValues.put(Student.COLUMN_GENDER, gen);
                contentValues.put(Student.COLUMN_HINDI, hindi);
                contentValues.put(Student.COLUMN_ENGLISH, eng);

                getContentResolver().insert(Student.CONTENT_URI, contentValues);

                i++;  //increment counter
            }
        }

    }

}//end of activity
