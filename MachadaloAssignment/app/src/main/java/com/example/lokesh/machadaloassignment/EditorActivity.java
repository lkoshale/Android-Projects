package com.example.lokesh.machadaloassignment;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v4.print.PrintHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.lokesh.machadaloassignment.data.StudentContract;


/*
 Editor Activity to Add Update the Student DATA in Database
    this Activity has two mode one for Adding Data
    other for updating data
 */
public class EditorActivity extends AppCompatActivity {

    //the uri class variable to make it access to all
    private Uri currentUri;

    //the first MDOE of the Activity
    // ADd a NEW student data
    private static final String Mode1 = "Add a Student";

    // second mode update existing student data
    private static final String Mode2 = "Update Data";

    // Mode variable
    private int MODE = 0;

    // Edit Text and Texfields
    private EditText mNameEditText;

    private EditText mAgeEditText;

    private EditText mHindiEditText;

    private EditText mEnglishEditText;

    private Spinner mGenderSpinner;

    private TextView mTotalTextView;

    private TextView mAVGTextView;

    private int mGender = StudentContract.Student.GENDER_UNKNOWN;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // find the views of all the required field in UI
        mNameEditText = (EditText)findViewById(R.id.name_editetxt);
        mAgeEditText = (EditText)findViewById(R.id.age_editetxt);
        mHindiEditText = (EditText)findViewById(R.id.hindi_edittext);
        mEnglishEditText = (EditText)findViewById(R.id.english_edittext);
        mGenderSpinner = (Spinner)findViewById(R.id.spinner_gender);
        mTotalTextView = (TextView)findViewById(R.id.total_textview);
        mAVGTextView = (TextView)findViewById(R.id.avg_textview);

        //getting uri data passed from the calling activity
        //only if update mode
        Intent intent = getIntent();
        currentUri = intent.getData();

        //if null add data of new student
        if (currentUri==null){
            //change title of activity
            setTitle(Mode1);
            MODE =1;

        }else {
            //mode 2
            //update date
            MODE=2;
            setTitle(Mode2);
            String selected = StudentContract.Student._ID + " > 0";

            //here i am upadting all the field at once we can also
            // perform checks here which are changed to make it more efficient
            Cursor c = getContentResolver().query(currentUri,null,selected,null,null);
            while (c.moveToNext()) {
                mNameEditText.setText(c.getString(c.getColumnIndexOrThrow(StudentContract.Student.COLUMN_NAME)));
                mAgeEditText.setText(Integer.toString(c.getInt(c.getColumnIndex(StudentContract.Student.COLUMN_AGE))));
                mHindiEditText.setText(Integer.toString(c.getInt(c.getColumnIndex(StudentContract.Student.COLUMN_HINDI))));
                mEnglishEditText.setText(Integer.toString(c.getInt(c.getColumnIndex(StudentContract.Student.COLUMN_ENGLISH))));

                mTotalTextView.setText(Integer.toString(c.getInt(c.getColumnIndex(StudentContract.Student.COLUMN_TOTAL))));

                mAVGTextView.setText(Float.toString(c.getFloat(c.getColumnIndex(StudentContract.Student.ColUMN_AVG))));

            }

            //making the total and avg feild visible
            LinearLayout sp = (LinearLayout)findViewById(R.id.spinner_layout);
            sp.setVisibility(View.INVISIBLE);

            LinearLayout total = (LinearLayout)findViewById(R.id.invisible_total);
            total.setVisibility(View.VISIBLE);
            LinearLayout avg = (LinearLayout)findViewById(R.id.invisible_avg);
            avg.setVisibility(View.VISIBLE);

            // Save the Updated data onclick
            Button save = (Button)findViewById(R.id.save_button);
            save.setVisibility(View.VISIBLE);
            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //this function udpdates the data from Ui to database
                    updateData(currentUri);
                    finish();
                }
            });

            Button delete = (Button)findViewById(R.id.delete_button);
            delete.setVisibility(View.VISIBLE);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   //delete the dta from the database
                    deleteData(currentUri);
                    finish();
                }
            });

        }

        //set the spinner for the Activity on Mode1 only
        setupSpinner();
    }

    //this function sets the spinner
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_gender_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mGenderSpinner.setAdapter(genderSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mGenderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals("Male")) {
                        mGender = StudentContract.Student.GENDER_MALE;
                    } else if (selection.equals("Female")) {
                        mGender = StudentContract.Student.GENDER_FEMALE;
                    } else {
                        mGender = StudentContract.Student.GENDER_UNKNOWN;
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mGender =StudentContract.Student.GENDER_UNKNOWN;
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_save:
                // Save to database
                // Exit activity
                if(MODE==1){
                    saveData();
                }else if (MODE ==2)
                {
                    updateData(currentUri);
                }

                finish();
                return true;

            case R.id.action_delete:
                if (MODE == 2){
                    deleteData(currentUri);
                }
                finish();
                return true;

            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // SAVES the new DATA of Student in the database
    public void saveData(){
        //get all the values from the Activity
        String name = mNameEditText.getText().toString().trim();
        int age = Integer.parseInt(mAgeEditText.getText().toString().trim());
        int hindi_marks = Integer.parseInt(mHindiEditText.getText().toString().trim());
        int english_marks = Integer.parseInt(mEnglishEditText.getText().toString().trim());

        // make the contentvalues
        ContentValues contentValues = new ContentValues();

        contentValues.put(StudentContract.Student.COLUMN_NAME,name);
        contentValues.put(StudentContract.Student.COLUMN_AGE,age);
        contentValues.put(StudentContract.Student.COLUMN_GENDER,mGender);
        contentValues.put(StudentContract.Student.COLUMN_HINDI,hindi_marks);
        contentValues.put(StudentContract.Student.COLUMN_ENGLISH,english_marks);

        //using the Content Resolver to insert the dta in database
        getContentResolver().insert(StudentContract.Student.CONTENT_URI,contentValues);

    }

    //this functions works in mode2 and updates the existing data
    public void updateData(Uri uri){

        //get all value from UI
        String name = mNameEditText.getText().toString().trim();
        int age = Integer.parseInt(mAgeEditText.getText().toString().trim());
        int hindi_marks = Integer.parseInt(mHindiEditText.getText().toString().trim());
        int english_marks = Integer.parseInt(mEnglishEditText.getText().toString().trim());

        //make contenntvalues for insert
        ContentValues contentValues = new ContentValues();

        contentValues.put(StudentContract.Student.COLUMN_NAME,name);
        contentValues.put(StudentContract.Student.COLUMN_AGE,age);
        contentValues.put(StudentContract.Student.COLUMN_HINDI,hindi_marks);
        contentValues.put(StudentContract.Student.COLUMN_ENGLISH,english_marks);

        //get id from the global uri = uri passed to this intent
        long id = ContentUris.parseId(uri);
        String selection = StudentContract.Student._ID  + "=?";

        //ask conetent provider to updates data as  implented in that class
        getContentResolver().update(uri,contentValues,selection,new String[]{Long.toString(id)});
    }

    //delets the the row from the data
    public void  deleteData(Uri uri){
        //get the id
        long id = ContentUris.parseId(uri);
        String selection = StudentContract.Student._ID  + "=?";

        //use content provider to delete already implmented
        getContentResolver().delete(StudentContract.Student.CONTENT_URI,selection,new String[]{Long.toString(id)});

    }

}
