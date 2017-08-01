package com.machadalo.audit.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.machadalo.audit.AssignedFragmentAdapter;
import com.machadalo.audit.AuditorCapture;
import com.machadalo.audit.QueueImageViewActivity;
import com.machadalo.audit.R;
import com.machadalo.audit.adapter.QueueAdapter;
import com.machadalo.audit.extras.ConnectionDetector;
import com.machadalo.audit.extras.QueueData;
import com.machadalo.audit.sqllite.AuditGS;
import com.machadalo.audit.sqllite.DataBaseHandler;
import com.machadalo.audit.sqllite.InventoryImagePathTable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class QueueFragment extends Fragment {

    ArrayList<String> pictures= new ArrayList<String>();//Array to store pictures
    ArrayList<String> locationarray= new ArrayList<String>();
    ArrayList<String> addressarray= new ArrayList<String>();
    ArrayList<String> idarray = new ArrayList<String>();
    ArrayList<String> submitStatusArray = new ArrayList<String>();
    ArrayList<String> inventDateArray = new ArrayList<String>();

    String getIntActivity;
    //TODO Change URL
    private String url = "http://machadalo.com/android/audit/media/submitted.php"; // our Url to server

    DataBaseHandler db;
    int element,total;
    // flag for Internet connection status
    Boolean isInternetPresent = true;
    ConnectionDetector cd;
    String strAdCount;
    int getIntentAct;
    /*Main Variable from Assighned*/
    private View myFragmentView;
    private RecyclerView recyclerView;
    private QueueAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<QueueData> queuedData = new ArrayList<>();
    private List<AuditGS> queueData = new ArrayList<>();
    String lat;
    String lon;


    // added by lokesh : to show the qued count at the top
    public AssignedFragmentAdapter assignedFragmentAdapter;

    public void setAssignedFragmentAdapter(AssignedFragmentAdapter as){
        assignedFragmentAdapter = as;
    }

    public QueueFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = DataBaseHandler.getInstance(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        myFragmentView = inflater.inflate(R.layout.fragment_four, container, false);

        // use this setting to improve performance if you know that changes

        // in content do not change the layout size of the RecyclerView
        recyclerView = (RecyclerView) myFragmentView.findViewById(R.id.recycler_view);


        mAdapter = new QueueAdapter(queuedData);
        /*RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);*/
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        //accessWebService();
        accessQueuedDataFromDb();

        // populate data from db
/*        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());

        if (!prefs.getBoolean("firstTime_Four", false)) {
            // <---- run your one time code here
            accessWebService();

            accessQueuedDataFromDb();

            // accessQueuedDataFromDb();
            //mAdapter.notifyDataSetChanged();

            // mark first time has runned.
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("firstTime_Four", true);
            editor.commit();
        }*/

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(getContext(), QueueImageViewActivity.class);
                intent.putExtra("path",queuedData.get(position).getLocalImagePath());
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        // Inflate the layout for this fragment
        return myFragmentView;
    }


    public void accessWebService() { // helper function to run jason task
        JsonReadTask task = new JsonReadTask();
        // passes values for the urls string array
        task.execute(new String[] { url });
    }
    private class JsonReadTask extends AsyncTask<String, Void, String> { // asynctask which runs on dif thread to parse json object from server
        @Override
        protected String doInBackground(String... params) {

            return null;
        }


        @Override
        protected void onPostExecute(String result) {
            ListDrwaer();
        }
    }// end async task
    public void ListDrwaer() {

      queueData = db.up_getAllPendingAudits();
        int size= queueData.size();
        Log.e("queuedata size", Integer.toString(size));

        int i=0;
        for ( i=0; i<queueData.size();i++) {
            prepareDataData(queueData.get(i).get_ad_inventory_ID(), queueData.get(i).get_society_address(), "Poster", queueData.get(i).get_society_address(), queueData.get(i).get_timestamp());


            Log.e("Inventory ID", queueData.get(i).get_ad_inventory_ID());
            Log.e("Society Address", queueData.get(i).get_society_address());
          /*  Log.e("Maps", queueData.get(i).get_map());*/
          /*  Log.e("Date", queueData.get(i).get_date());*/
          /*  Log.e("Image url", queueData.get(i).get_image());*/
               Log.e("Inventory Address", queueData.get(i).get_inventory_address());
          /*  Log.e("Maps", queueData.get(i).get_map());*/
        /*    Log.e("Submit Status", queueData.get(i).get_submit_status());*/
            Log.e("Time stamp", queueData.get(i).get_timestamp());
            Log.e("Date", queueData.get(i).get_date());
            Log.e("Image", queueData.get(i).get_image());


        }
    }

    /*Framen Work*/
    private void prepareDataData(String adID, String societyName, String adType,String societyAddress, String image) {
        QueueData quedata = new QueueData(adID, societyName, adType,societyAddress,image);
        queuedData.add(quedata);
        mAdapter.notifyDataSetChanged();
        //change the count of tab and notify its adapter
        Log.e("QueedFrag",String.valueOf(queuedData.size()));
        assignedFragmentAdapter.setQueedCount(queuedData.size());
        assignedFragmentAdapter.notifyDataSetChanged();

    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private QueueFragment.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final QueueFragment.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }
    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public void accessQueuedDataFromDb(){



        // used to fetch data from db and populate queuedData member variable with fresh data
        DataBaseHandler db_handle = DataBaseHandler.getInstance(getActivity());

        queuedData.clear();

      //  List<QueueData> data =  InventoryImagePathTable.getQueuedData(db_handle);
        List<QueueData>data = InventoryImagePathTable.GetQueueData(db_handle);

        if ( data != null ) {

            Log.e("SizeOfQData", Integer.toString(queuedData.size()));

            for ( int i = 0; i < data.size(); i++ ) {

                queuedData.add(data.get(i));
            }

            mAdapter.notifyDataSetChanged();
            assignedFragmentAdapter.setQueedCount(queuedData.size());
            assignedFragmentAdapter.notifyDataSetChanged();

            }
        else {
            Log.e("SizeOfQData", "null q data");
        }
    }


}
