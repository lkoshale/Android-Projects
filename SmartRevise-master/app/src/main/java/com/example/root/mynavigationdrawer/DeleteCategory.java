package com.example.root.mynavigationdrawer;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.HashMap;

public class DeleteCategory extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_category);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //String [] to_display;
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final String [] to_display ;
        final HashMap data = (HashMap) getIntent().getSerializableExtra("data");
        if (data != null) {

            to_display = new String[data.size()];
            data.keySet().toArray(to_display);
            Spinner dropdown = (Spinner)findViewById(R.id.spinner1);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, to_display);
            dropdown.setAdapter(adapter);
            dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id ) {

                    Button delete = (Button) findViewById( R.id.DeleteCategory) ;
                    final int pos = position;
                    delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            String category = to_display[pos];
                            System.out.println("this category was  selected : " + category);
                            String category_id = (String) data.get(category);
                            System.out.println("the  category id  was: " + category_id);
                            DatabaseHelper helper = new DatabaseHelper(DeleteCategory.this);
                            helper.DeleteCategoryCascade(helper, category_id);
                            //finish(); // terminate this activity
                            Intent in = new Intent(DeleteCategory.this,MainActivity.class);
                            startActivity(in);

                        }
                    });


                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });


        }
        else {

            String [] None =  { "no category yet"} ;
            Spinner dropdown = (Spinner)findViewById(R.id.spinner1);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, None );
            dropdown.setAdapter(adapter);
        }





    }

}
