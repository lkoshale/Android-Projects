package com.example.root.sortvisualizer;

import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.root.sortvisualizer.adapters.BucketSortAdapter;
import com.example.root.sortvisualizer.adapters.HeapSortListAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class BucketSortActivity extends AppCompatActivity implements Sorter {
    /*
     This class is responsible for handling bucket sort logic and displaying  progress to UI
     */

    private List<BucketSortModel> mBucketList = new ArrayList<>(); // a list, in which each element is instance of BucketSortModel class
    private List<Integer> mNumbersList = new ArrayList<>(); // Number list
    private TextView mListTextView; // Text View to display unsorted array at the top of the screen
    private TextView mSortedListView; // Text View to display sorted array at the bottom of the screen
    private TextView mFinalText;
    RecyclerView mNumberListView;  // an RV variabe
    private LinearLayoutManager mLinearLayoutManger; // a Manager Variable
    private BucketSortAdapter mBucketSortAdapter; // an Adaptor variable


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bucket_sort);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mListTextView = (TextView)findViewById(R.id.list_items); // find the id  of  Text View which will hold an unsorted array
        mSortedListView = (TextView)findViewById(R.id.sorted_list_items);// find the id  of  Text View which will hold an unsorted array
        mFinalText = (TextView)findViewById(R.id.final_text); // find the id  of  a TV which will contain a final message

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AsyncTaskRunner runner = new AsyncTaskRunner(); // action starts on clicking the floating button
                runner.execute();
                Snackbar.make(view, "Bucket Sort Started", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent in = getIntent();
        String size = in.getStringExtra("Size"); // get the size of main activity

        mNumbersList.clear();

        populate_list(size);
        display_numbers();

        BucketSortModel bucketModel; // instance of BucketSortModel
        List<Integer> list1; // List instance

        // populating the Bucket array with empty buckets
        Log.d("DEBUG", "number of buckets :  " + mNumbersList.size());
        for(int i=0; i< mNumbersList.size();i++) {
            bucketModel = new BucketSortModel();
            list1 = new ArrayList<>();
            bucketModel.setMbucketList(list1);
            bucketModel.setmSize(list1.size());
            mBucketList.add(bucketModel);
        }

        mNumberListView = (RecyclerView) findViewById(R.id.bucket_list_view); // get the Recycler View
        mLinearLayoutManger = new LinearLayoutManager(this); // get the layout manager
        mLinearLayoutManger.setOrientation(LinearLayoutManager.VERTICAL);
        mNumberListView.setLayoutManager(mLinearLayoutManger); // set the Manager for RV
        mBucketSortAdapter = new BucketSortAdapter(mBucketList,this); // make the adaptor
        mNumberListView.setAdapter(mBucketSortAdapter); // set the adaptor for RV

    }


    @Override
    public void display_numbers() {

        StringBuilder sb = new StringBuilder(); // String builder to display the  elements at the top of the screen
        for(int i = 0; i< mNumbersList.size();i++) {
            if(i==mNumbersList.size()-1) {
                sb.append(mNumbersList.get(i)+"");
            } else {
                sb.append(mNumbersList.get(i)+ ", ");
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
            mNumbersList.add(rn.nextInt(max - min + 1) + min); // populate the List with instances of heapSortModel , color of each element is balck(0) initially.
        }
    }


    @Override
    public void run_algorithm() {
        Log.d("run_algorithm()", "in run_algorithm");
    }


    private class AsyncTaskRunner extends AsyncTask<String, String, String> {

        private String resp;
        private int index;
        StringBuilder sb;

        private List<Integer> sortList(List<Integer> list) {
            // instead of merging the individual buckets, i sorted the original array and returned to avoid complexity
            Collections.sort(list);
            return list;
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                // index of the bucket is for an array element is simply element % size. where size is size of the entire array.
                // set each item of the array to appropriate bucket.
                int hashKey = mNumbersList.size(); //
                BucketSortModel model;
                for(int i = 0; i<mNumbersList.size();i++) {
                    int bucketNumber = (mNumbersList.get(i) % hashKey);
                    model = new BucketSortModel(); // create an empty model.
                    List<Integer> list = new ArrayList<>(); // create an empty list
                    list = mBucketSortAdapter.getmList().get(bucketNumber).getMbucketList();// get the list from the adaptor at index BucketNumber
                    list.add(mNumbersList.get(i)); // add the element to fetched list
                    model.setMbucketList(list); // set the model list param
                    model.setmSize(list.size()); // set the model size param
                    index = bucketNumber;// change has happened at this index.
                    mBucketSortAdapter.getmList().set(bucketNumber, model);
                    publishProgress(); // notfiy UI that a progress has been made
                    Thread.sleep(1000);
                }

                // sort each bucket individually  and display to UI

                BucketSortModel model1;
                for(int i=0;i< mBucketSortAdapter.getmList().size();i++) {
                    model1 = new BucketSortModel();
                    model1.setMbucketList(sortList(mBucketSortAdapter.getmList().get(i).getMbucketList()));
                    model1.setmSize(mBucketSortAdapter.getmList().get(i).getmSize());
                    mBucketSortAdapter.getmList().set(i, model1);
                    index = i;
                    publishProgress();
                    Thread.sleep(1000);
                }
                // finally sort the original list and  display to UI . skipping merging of the buckets, as it is not necessary.
                Collections.sort(mNumbersList);
                sb = new StringBuilder();
                for(int i = 0; i<mNumbersList.size();i++) {
                    if(i==mNumbersList.size()-1) {
                        sb.append(mNumbersList.get(i)+"");
                    } else {
                        sb.append(mNumbersList.get(i) + ", ");
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                resp = e.getMessage();
            }
            return resp;
        }

        @Override
        protected void onPostExecute(String result) {
            mFinalText.setVisibility(View.VISIBLE);
            mSortedListView.setVisibility(View.VISIBLE);
            mSortedListView.setText(sb);
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(String... text) {
            // tell the UI about changes
            mBucketSortAdapter.notifyItemChanged(index);
        }
    }

}
