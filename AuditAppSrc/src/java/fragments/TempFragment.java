package com.machadalo.audit.fragments;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.machadalo.audit.AssignedActivity;
import com.machadalo.audit.R;
import com.machadalo.audit.adapter.DataAdapter;
import com.machadalo.audit.extras.ConnectionDetector;
import com.machadalo.audit.extras.Data;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class TempFragment extends Fragment{
    /*Main Variable from Assighned*/
    private String jsonResult;
    ArrayList<String> pictures= new ArrayList<String>();//Array to store pictures
    ArrayList<String> locationarray= new ArrayList<String>();
    ArrayList<String> addressarray= new ArrayList<String>();
    ArrayList<String> idarray = new ArrayList<String>();
    ArrayList<String> submitStatusArray = new ArrayList<String>();
    ArrayList<String> inventDateArray = new ArrayList<String>();
    String getIntActivity;
    private String url = "http://192.168.1.106:8108/v0/android/audit/assigned_audits_temp/"; // our Url to server

    private static final String TAG_CONTACTS = "type";
    private static final String TAG_ID = "id";
    private static final String TAG_NAME = "name";
    private static final String TAG_EMAIL = "email";
    private static final String TAG_ADDRESS = "address";
    private static final String TAG_GENDER = "gender";
    private static final String TAG_PHONE = "phone";
    private static final String TAG_PHONE_MOBILE = "mobile";
    private static final String TAG_PHONE_HOME = "home";
    private static final String TAG_PHONE_OFFICE = "office";

    int element,total;
    // flag for Internet connection status
    Boolean isInternetPresent = true;
    ConnectionDetector cd;
    String strAdCount;
    private SwipeRefreshLayout swipeRefreshLayout;
    int getIntentAct;
    /*Main Variable from Assighned*/
    private View myFragmentView;
    private RecyclerView recyclerView;
    private DataAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<Data> dataList = new ArrayList<>();
    public TempFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = null;
        myFragmentView = inflater.inflate(R.layout.fragment_one, container, false);

        // use this setting to improve performance if you know that changes

        // in content do not change the layout size of the RecyclerView
        recyclerView = (RecyclerView) myFragmentView.findViewById(R.id.recycler_view);

        mAdapter = new DataAdapter(getContext(),dataList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        if (!prefs.getBoolean("firstTime_One", false)) {
            // <---- run your one time code here
            accessWebService();
            // mark first time has runned.
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("firstTime_One", true);
            editor.commit();
        }
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Data data = dataList.get(position);
                //Toast.makeText(getContext(), data.getsocietyName() + " is selected!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getContext(), AssignedActivity.class);
//                intent.putExtra("inventoryIDRec",data.getadID());
                intent.putExtra("getIntentAct",true);

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
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httppost = new HttpGet(params[0]); // params[0] contains url string
            try {
                HttpResponse response = httpclient.execute(httppost);
                jsonResult = inputStreamToString(
                        response.getEntity().getContent()).toString();
            }

            catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        private StringBuilder inputStreamToString(InputStream is) {
            String rLine = "";
            StringBuilder answer = new StringBuilder();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));

            try {
                while ((rLine = rd.readLine()) != null) {
                    answer.append(rLine);
                }
            }

            catch (IOException e) {
                e.printStackTrace();
            }
            return answer;
        }

        @Override
        protected void onPostExecute(String result) {
            ListDrwaer();
        }
    }// end async task
    public void ListDrwaer() {


        try {

            // get Internet status
            //isInternetPresent = cd.isConnectingToInternet();

            // check for Internet status
            if (isInternetPresent) {
                // Internet Connection is Present
                // make HTTP requests
                if (jsonResult == null) {
                    Toast.makeText(getContext(), "No response from server due to internet",
                            Toast.LENGTH_SHORT).show();
                } else {
                    JSONObject jsonResponse = new JSONObject(jsonResult);
                    Log.e("JSON Array", String.valueOf(jsonResponse));
                    JSONArray jsonMainNode;
                    jsonMainNode = jsonResponse.optJSONArray("result");// assign is our json object name in mysql database
                    if(jsonMainNode== null)
                    {
                        Toast.makeText(getContext(), "There is no assign inventory!",
                                Toast.LENGTH_SHORT).show();
                    }
                    else {
                        total = jsonMainNode.length();
                        for (int i = 0; i < jsonMainNode.length(); i++) {
                            JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                            String id = jsonChildNode.optString("ad_inventory_id");
                            String inventoryType = jsonChildNode.optString("ad_inventory_type");
                            String societyname = jsonChildNode.optString("supplier_name");
                            String type = jsonChildNode.optString("audit_type");
                            String location = jsonChildNode.optString("ad_location")+jsonChildNode.optString("address");
                            String date = jsonChildNode.optString("date");
                            String picture = jsonChildNode.optString("image_url");
                            String submitStatus = jsonChildNode.optString("submit_status");
                            String business_name = jsonChildNode.optString("business_name");
                            String audit_type= null;
//                            prepareDataData(id, societyname, type, location, picture, submitStatus,date,inventoryType,business_name,audit_type);

                        }


                    }
                }
            } else {
                // Internet connection is not present
                // Ask user to connect to Internet
                showAlertDialog(getContext(), "No Internet Connection",
                        "You don't have internet connection.", false);
            }

        } catch (JSONException e) {
            Log.e("Json Exception", String.valueOf(e));
        }



    }
    public void showAlertDialog(Context context, String title, String message, Boolean status) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        // Setting Dialog Title
        alertDialog.setTitle(title);

        // Setting Dialog Message
        alertDialog.setMessage(message);

        // Setting alert dialog icon
      /*  alertDialog.setIcon((status) ? R.drawable.material_drawer_circle_mask : R.drawable.material_drawer_circle_mask);

       */ // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent=new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS);
                ComponentName cName = new ComponentName("com.android.phone","com.android.phone.Settings");
                intent.setComponent(cName);
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }
    /*Frame Work*/
//    private void prepareDataData(String adID, String societyName, String adType,String societyAddress, String url,String submit_status,String date,String inventoryType,String business_name,String audit_type) {
//        Data Data = new Data(adID, societyName, adType,societyAddress,url,submit_status,date,inventoryType,business_name,audit_type);
//        dataList.add(Data);
//        mAdapter.notifyDataSetChanged();
//    }


    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private TempFragment.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final TempFragment.ClickListener clickListener) {
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


}
