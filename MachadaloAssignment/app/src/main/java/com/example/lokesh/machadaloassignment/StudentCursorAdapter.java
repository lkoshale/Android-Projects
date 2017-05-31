package com.example.lokesh.machadaloassignment;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.lokesh.machadaloassignment.data.StudentContract.Student;

/**
 * Created by Lokesh on 30-05-2017.
 */

// Our Custom Cursor Adapter class for the cursor loader in listview
public class StudentCursorAdapter extends CursorAdapter {

    //constructor
    public  StudentCursorAdapter(Context context,Cursor c){
        super(context,c,0);
    }


    //inflate the layout of each item of the list view so the cursorAdapter knows the UI
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        //inflate
        return LayoutInflater.from(context).inflate(R.layout.list_item_layout,parent,false);
    }


    //Bind the Vlaues from the cursor to the UI elements
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        // all the UI elments of the single item of list
        TextView nameTextview = (TextView)view.findViewById(R.id.list_name);

        TextView ageTextview = (TextView)view.findViewById(R.id.list_age);

        TextView hindiTextview = (TextView)view.findViewById(R.id.list_hindi);

        TextView englishTextview = (TextView)view.findViewById(R.id.list_english);

        TextView totalTextview = (TextView)view.findViewById(R.id.list_total);

        TextView avgTextview = (TextView)view.findViewById(R.id.list_avg);

        TextView genderTextview = (TextView)view.findViewById(R.id.list_gender);

        // getting all the attributes of a row from the cursor
        String name = cursor.getString(cursor.getColumnIndex(Student.COLUMN_NAME));

        int age = cursor.getInt(cursor.getColumnIndex(Student.COLUMN_AGE));

        int hindi_marks  = cursor.getInt(cursor.getColumnIndex(Student.COLUMN_HINDI));

        int english_marks = cursor.getInt(cursor.getColumnIndex(Student.COLUMN_ENGLISH));

        int gender = cursor.getInt(cursor.getColumnIndex(Student.COLUMN_GENDER));

        String G_str ;
        //resolving the Gender int
        if (gender == Student.GENDER_MALE){
            G_str = "Male";
        }else if (gender == Student.GENDER_FEMALE){
            G_str = "Female";
        }else{
            G_str = "Unknown";
        }

        int total = cursor.getInt(cursor.getColumnIndex(Student.COLUMN_TOTAL));

        float avg = cursor.getFloat(cursor.getColumnIndex(Student.ColUMN_AVG));

        /**
         *  Add all the datbase strings with the Ui
         *  of the layout
         * */

        nameTextview.setText("Name : "+name);
        ageTextview.setText("Age :  "+Integer.toString(age));
        hindiTextview.setText( "Hindi :  "+Integer.toString(hindi_marks));
        englishTextview.setText("English : "+Integer.toString(english_marks));
        genderTextview.setText(G_str);
        totalTextview.setText("Total :  "+Integer.toString(total));
        avgTextview.setText("Avg  :  "+Float.toString(avg));

    }
}
