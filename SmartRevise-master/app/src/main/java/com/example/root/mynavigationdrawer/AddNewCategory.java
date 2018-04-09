package com.example.root.mynavigationdrawer;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class AddNewCategory extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_category);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final TextView tv = (TextView) findViewById(R.id.tvAddNewCategory) ;
        Button b =  (Button) findViewById( R.id.bAddNewCategory) ;
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String text = tv.getText().toString();
                if ( text.contentEquals("")) {
                }
                else {

                    DatabaseHelper helper = new DatabaseHelper( getBaseContext()) ;
                    if ( helper.AddNewCategory(helper, text)  ) {

                        show_message("Your Category has been added");
                        Intent in = new Intent(AddNewCategory.this,MainActivity.class);
                        startActivity(in);

                    }
                    else {

                        show_message("failed to add new Category.");

                    }
                }

            }
        });


    }

    public void show_message( CharSequence text) {

        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText( this, text, duration);
        toast.show();

    }

}
