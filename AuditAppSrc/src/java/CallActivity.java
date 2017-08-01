package com.machadalo.audit;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.machadalo.audit.sqllite.ContactsTable;
import com.machadalo.audit.sqllite.DataBaseHandler;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/*
* this acitivty is used for chosing number nad person for calling
* and it launches as call button is clicked in assigned fragment
*
* uses the contacts table to get the data
 */

public class CallActivity extends AppCompatActivity {

    //spinner to chose name and number
    Spinner nameSpinner ;
    Spinner numberSpinner;
    ArrayList<String> nameAndDesignation = new ArrayList<String>();
    ArrayList<String> numbers = new ArrayList<String>();
    String supplierId;
    ArrayAdapter<String> myNameAdpter;
    ArrayAdapter<String> myNumAdapter;

    //buttons
    Button bcancel;
    Button bDail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);

        nameSpinner = (Spinner)findViewById(R.id.nameDesgSpinner);
        numberSpinner = (Spinner)findViewById(R.id.numberSpinner);
        bcancel = (Button)findViewById(R.id.callExitButton);
        bDail = (Button)findViewById(R.id.callDailButton);

        //passed from assigned Fragment
        supplierId = getIntent().getStringExtra("supplierID");


        //populates the data in the two spinner
        prepareData();

        //setting up the spinners
        if (nameAndDesignation.size()!=0) {

            myNameAdpter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, nameAndDesignation);
            myNameAdpter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            nameSpinner.setAdapter(myNameAdpter);



            myNumAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, numbers);
            myNumAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            numberSpinner.setAdapter(myNumAdapter);

            String nameDes = (String) nameSpinner.getSelectedItem();
            //get numbers for the name and designation
            prepareNum(nameDes);

            nameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    numbers.clear();
                    String selctedName = nameAndDesignation.get(i);
                    prepareNum(selctedName);
                    //Log.e("numberAray",Arrays.toString(numbers.toArray()));
                    //Log.e("CallSelected", selctedName);
                    myNumAdapter.notifyDataSetChanged();
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }

            });



            bDail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String sNUM = (String) numberSpinner.getSelectedItem();

                    if (sNUM != null && sNUM.contains("!")==false) {
                        Intent intent = new Intent(Intent.ACTION_CALL);
                        intent.setData(Uri.parse("tel:" + sNUM));
                        startActivity(intent);
                    }
                }
            });

        }

        //cancel button
        bcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    //get data fro the name and number from table
    void prepareData(){

        DataBaseHandler dataBaseHandler = DataBaseHandler.getInstance(this);
        SQLiteDatabase db = dataBaseHandler.getReadableDatabase();

        String Query = " SELECT * FROM "+ ContactsTable.TABLE_NAME + " WHERE "+ContactsTable.KEY_SUPPLIER_ID+" = \'"+supplierId+"\' ;";

        Cursor cursor = db.rawQuery(Query,null);

        while (cursor.moveToNext()){

            String name = cursor.getString(cursor.getColumnIndex(ContactsTable.KEY_NAME));
            String des  =  cursor.getString(cursor.getColumnIndex(ContactsTable.KEY_CONTACT_TYPE));

            if (name ==null && des == null)
                continue;

            String nameAndDesg = name+ " "+des;
            nameAndDesignation.add(nameAndDesg);


            Log.e("inCallPre",nameAndDesg);

        }

        cursor.close();

    }

    void prepareNum(String namedes){

        numbers.clear();
        myNumAdapter.notifyDataSetChanged();

        DataBaseHandler dataBaseHandler = DataBaseHandler.getInstance(this);
        SQLiteDatabase db = dataBaseHandler.getReadableDatabase();

        String Query = " SELECT * FROM "+ ContactsTable.TABLE_NAME + " WHERE "+ContactsTable.KEY_SUPPLIER_ID+" = \'"+supplierId+"\' ;";

        Cursor cursor = db.rawQuery(Query,null);

        while (cursor.moveToNext()) {

            String name = cursor.getString(cursor.getColumnIndex(ContactsTable.KEY_NAME));
            String des = cursor.getString(cursor.getColumnIndex(ContactsTable.KEY_CONTACT_TYPE));
            String comp = name+" "+des;
            if (comp.compareTo(namedes)==0){
                String mobile = cursor.getString(cursor.getColumnIndex(ContactsTable.KEY_MOBILE));
                String std = cursor.getString(cursor.getColumnIndex(ContactsTable.KEY_STD_CODE));
                String landline = cursor.getString(cursor.getColumnIndex(ContactsTable.KEY_LAND_LINE));

                if (mobile.contains("null")==true)
                    numbers.add("not available !!");
                else
                    numbers.add(mobile);

                if (landline.contains("null"))
                    numbers.add("not available !!");
                else
                    numbers.add(std+landline);


                myNumAdapter.notifyDataSetChanged();

            }

        }
        cursor.close();

    }



}
