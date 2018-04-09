package com.example.root.sortvisualizer;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.example.root.sortvisualizer.adapters.HeapSortListAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;

import android.app.ListActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

public class HeapSortActivity extends AppCompatActivity implements Sorter{
    /*

     This Activity handles HeapSort and displays progress to UI
     RV ---> RecyclerView

     */
    List<HeapSortModel> mNumbersList = new ArrayList<>(11); // a list of data type 'HeapSortModel' which is a data structure to store element and it's color.
    List<HeapSortModel>data = new ArrayList<>();
    RecyclerView mNumberListView;  // using recycler view to display elements
    private LinearLayoutManager mLinearLayoutManger; // layout manager for RV
    private HeapSortListAdapter mHeapSortAdapter; // Adaptor class for RV
    List<HeapSortModel> heap_array = new ArrayList<HeapSortModel>();
    HeapSort heap;
    private TextView mListTextView; // Text View to display unsorted array at the top of the screen
    int min_index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             // things start when u click the floating button
                AsyncTaskRunner runner = new AsyncTaskRunner(); // process sorting in background
                runner.execute();
                Snackbar.make(view, "Heap sort Started", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show(); // display a message when it all starts
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent in = getIntent();
        String size = in.getStringExtra("Size"); // get the size of List from main activity

        mNumbersList.clear(); // clear the list before proceeding

        populate_list(size); // populate the list with random numbers from max to min

        mListTextView = (TextView) findViewById(R.id.unsorted_list);
        display_numbers();

        for ( int i = 0; i < mNumbersList.size(); i++ ) {
            HeapSortModel h = new HeapSortModel(0,0);
            data.add(h);
        }

        mNumberListView = (RecyclerView) findViewById(R.id.numbers_list_view); // find RecyclerView
        mLinearLayoutManger = new LinearLayoutManager(this); // get LinearLayout
        mLinearLayoutManger.setOrientation(LinearLayoutManager.VERTICAL); // set orientation
        mNumberListView.setLayoutManager(mLinearLayoutManger); // Tell the RecyclerView about Linear Layout manager
        mHeapSortAdapter = new HeapSortListAdapter(data,this); // get The adaptor
        mNumberListView.setAdapter(mHeapSortAdapter); // set RV  view with the adaptor

        run_algorithm(); // run heap sort algorithm

    }

    @Override
    public void display_numbers() {

        StringBuilder sb = new StringBuilder(); // String builder to display the  elements at the top of the screen
        for(int i = 0; i< mNumbersList.size();i++) {
            if(i==mNumbersList.size()-1) {
                sb.append(mNumbersList.get(i).getmNumber()+"");
            } else {
                sb.append(mNumbersList.get(i).getmNumber() + ", ");
            }
        }
        mListTextView.setText(sb); // display the unsorted numbers at the top of screen .
    }

    @Override
    public void populate_list( String size) {
        Log.d("DEBUG", "populating list..");
        final Random rn = new Random(); // we will deal with random numbers
        final int max = 100; // numbers max limit
        final int min = 1; // numbers min limit

        for( int  i = 0; i < Integer.parseInt(size); i++ ) {
            mNumbersList.add(new HeapSortModel(rn.nextInt(max - min + 1) + min,0)); // populate the List with instances of heapSortModel , color of each element is balck(0) initially.
        }
    }

    @Override
    public void run_algorithm() {
        Log.d("run_algorithm()", "in run_algorithm");
        heap_array.clear();
        for ( int i = 0; i < mNumbersList.size(); i++) {
            HeapSortModel h = new HeapSortModel(mNumbersList.get(i).getmNumber(), i);
            heap_array.add(h);
        }
        heap = new HeapSort(heap_array);
    }


    private class AsyncTaskRunner extends AsyncTask<String, String, String> {
        /*
         This class runs processing of elements in background and displays the in-between results to android UI
         */

        private String resp;
        private int index;

        @Override
        protected String doInBackground(String... params) {
            try {
                // Do your long operations here and return the result
                // Sleeping for given time period
                for (int i = 0; i < mNumbersList.size(); i++) {
//            i = 1;'
                    //do your stuff here after DELAY sec

                    //min_index = get_min(i); // find the minimum element index in list (i....size)
                    Log.d("DEBUG", "extracting min...");
                    HeapSortModel m = heap.extract_min();
                    min_index = m.getColor();
                    HeapSortModel h = new HeapSortModel( mNumbersList.get(min_index).getmNumber(), 2);
                    System.out.println("MIN: " + m.getmNumber());
                    mHeapSortAdapter.getmList().set(i, h);
                    index = i;
                    publishProgress(); // display to UI
                    Thread.sleep(500); // sleep
                    resp = "Slept for 2000 milliseconds";
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                resp = e.getMessage();
            } catch (Exception e) {
                e.printStackTrace();
                resp = e.getMessage();
            }
            return resp;
        }

        @Override
        protected void onPostExecute(String result) {
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(String... text) {
            /*
              to display intermediate changes to UI
             */
            mHeapSortAdapter.notifyItemChanged(index);
        }
    }

}
