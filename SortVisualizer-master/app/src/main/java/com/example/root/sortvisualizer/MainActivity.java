package com.example.root.sortvisualizer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private Button mBucketSortBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mBucketSortBtn = (Button)findViewById(R.id.bucket_sort);// call bucketsort activity when second button is clicked
        mBucketSortBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et = (EditText) findViewById(R.id.etSize);
                String size = et.getText().toString();
                if ( size.isEmpty() ) {
                    display_message("NON ZERO SIZE PLEASE");
                }
                else {
                    Intent visualizer = new Intent(MainActivity.this, BucketSortActivity.class);
                    visualizer.putExtra("Size", size);
                    startActivity(visualizer);
                }
            }
        });

    }

    public void setVisualize( View view) { // call heapsort activity when button for heapsort is clicked
        EditText et = (EditText) findViewById(R.id.etSize);
        String size = et.getText().toString();
        if ( size.isEmpty() ) {
            display_message("NON ZERO SIZE PLEASE");
        }
        else {
            Intent visualizer = new Intent(this, HeapSortActivity.class);
            visualizer.putExtra("Size", size);
            startActivity(visualizer);
        }
    }


    public void  display_message( String message ) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(context, message, duration);
        toast.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
            return super.onOptionsItemSelected(item);
    }
}
