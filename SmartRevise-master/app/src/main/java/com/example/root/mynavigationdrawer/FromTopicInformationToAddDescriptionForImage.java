package com.example.root.mynavigationdrawer;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class FromTopicInformationToAddDescriptionForImage extends AppCompatActivity {

    String title;
    String description ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_from_topic_information_to_set_image);
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


        try {

            title = getIntent().getExtras().getString("title") ;
            setTitle(title);

        } catch (Exception e) {

            System.out.print( "Error in FromTopicInformationToAddDescriptionImage(): " + e.getMessage().toString() );
        }

       Button mButton = (Button)findViewById(R.id.bDescription);
       final EditText mEdit   = (EditText)findViewById(R.id.tvDescription);

        mButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {

                        description = mEdit.getText().toString() ;
                        print("found description from EditText: " + description );
                        Intent intent = new Intent();
                        intent.putExtra("description", description) ;
                        setResult(RESULT_OK, intent);
                        CommonMethods cm = new CommonMethods( FromTopicInformationToAddDescriptionForImage.this) ;
                        cm.show_message("Your Descrption has been saved !");
                        finish();

                    }
                });


    }

    public void print ( String str ) {

        System.out.println(str) ;
    }



}
