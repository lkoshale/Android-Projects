package com.machadalo.audit.fragments;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.machadalo.audit.AssignedFragmentAdapter;
import com.machadalo.audit.R;
import com.machadalo.audit.Utils;
import com.machadalo.audit.adapter.PendingDataAdapter;
import com.machadalo.audit.extras.PendingData;
import com.machadalo.audit.sqllite.BasicSupplierTable;
import com.machadalo.audit.sqllite.DataBaseHandler;
import com.machadalo.audit.sqllite.InventoryActivityAssignmentTable;
import com.machadalo.audit.sqllite.InventoryActivityTable;
import com.machadalo.audit.sqllite.InventoryImagePathTable;
import com.machadalo.audit.sqllite.PendingTable;
import com.machadalo.audit.sqllite.ProposalTable;
import com.machadalo.audit.sqllite.ShortlistedInventoryDetailsTable;
import com.machadalo.audit.sqllite.ShortlistedSuppliersTable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class PendingFragment extends Fragment {


    public Handler handler;

    //empty constructor
    public PendingFragment(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.e("IN pendingFragment","OnResume");
//        //printDataPending();
//        populatePendingTable();
//        preparePendingList();
//        mAdapter.notifyDataSetChanged();
//        //prepareListData();

    }


    private RecyclerView recyclerView;
    private PendingDataAdapter mAdapter;

    //list for recyclerview
    List<PendingData> mPendingDataList = new ArrayList<>();

    // move button is to move all the checked
    // items to completed tab
    private Button moveButton;

    //for updating th count at tab tittle
    AssignedFragmentAdapter assignedFragmentAdapter;

    public void setAssignedFragmentAdapter(AssignedFragmentAdapter as){
        this.assignedFragmentAdapter = as;
    }


    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {

        Log.e("PendingFrag","onCreateView");


        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                Bundle reply = msg.getData();
                String replyString = reply.getString("msg");
                String uploaded = reply.getString("Uploaded");
                Log.e("Inside pHANDLER",replyString);

                preparePendingList();
                mAdapter.notifyDataSetChanged();
                //mAdapter.Upload.setBackgroundColor(Color.GRAY);
                //  prepareData();
//                Intent intent = new Intent(context,QuickImageCaptureView.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                context.startActivity(intent);
                //Toast.makeText(getApplicationContext(),replyString,Toast.LENGTH_LONG).show();
            }
        };

        View rootView = inflater.inflate(R.layout.fragment_two, container, false);

        moveButton = (Button)rootView.findViewById(R.id.pendingMoveButton);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mAdapter = new PendingDataAdapter(getActivity(), mPendingDataList,handler);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        // populate the Pending Table
        populatePendingTable();
        //prepareData in the list
        preparePendingList();
        mAdapter.notifyDataSetChanged();



        // when move button is clicked
        moveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.e("Move button","CLICKED");

                /*
                Al the checkboxes that are checked are
                status is changed in pending table
                just re-render the data again to populate list

                 */
                preparePendingList();
                mAdapter.notifyDataSetChanged();

                // changing the count of tab title
                // assigning new value and then notifying
                assignedFragmentAdapter.setPendingCount(mPendingDataList.size());
                assignedFragmentAdapter.notifyDataSetChanged();

                //move to completed
                CompletedFragment.prepareCompletedData();

            }
        });

        //setting the count at the tab
         assignedFragmentAdapter.setPendingCount(mPendingDataList.size());
         Log.e("In pendingFrag",String.valueOf(AssignedFragmentAdapter.PendingCount));
         assignedFragmentAdapter.notifyDataSetChanged();

        ////////////////----- onClick listner for recycler view not being used now -------/////////////////

        /*
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                PendingData pendingData = mPendingDataList.get(position);
                Intent intent = new Intent(getContext(), PendingInventoryDetailsActivity.class);

                intent.putExtra("ProposalId", pendingData.getProposalID());
                intent.putExtra("shortlistedSpacesId", " ");   //data.getShortlistedSpacesId());  //ssid
                intent.putExtra("supplierName", pendingData.getSupplierName());
                intent.putExtra("supplierId", pendingData.getSupplierID());
                intent.putExtra("supplierLongitude", " ");
                intent.putExtra("supplierLatitude", " ");
                intent.putExtra("supplierAddress", pendingData.getSupplierAddress());
                intent.putExtra("selectedDate", " ");

                startActivity(intent);
                Log.v("Clicked", "recyclerview");
                // Toast.makeText(getContext(),pendingData.getProposalID()+"   "+pendingData.getSupplierID(),Toast.LENGTH_LONG).show();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
*/

        return rootView;
    }


    @Override
    public void onStart() {
        super.onStart();

    }

    // prepare the pending list from the Pending Table
    public void preparePendingList(){

        //Delete all the existing Data
        mPendingDataList.clear();
        mAdapter.notifyDataSetChanged();

        /*
            Query from table and add data in the list
         */

        String Query = "SELECT * FROM "+ PendingTable.TABLE_NAME + " WHERE "
                + PendingTable.KEY_isCompleted + " = "+ "\'false\'"+" ;";

        DataBaseHandler db_handle = DataBaseHandler.getInstance(getContext());
        SQLiteDatabase db = db_handle.getReadableDatabase();

        //db_handle.printTotalRows(PendingTable.TABLE_NAME);


        Cursor cursor = db.rawQuery(Query,null);

        while (cursor.moveToNext()){

            String proposalName = cursor.getString(cursor.getColumnIndex(PendingTable.KEY_PROPOSAL_NAME));
            String supplierID = cursor.getString(cursor.getColumnIndex(PendingTable.KEY_SUPPLIER_ID));
            String SupplierName = cursor.getString(cursor.getColumnIndex(PendingTable.KEY_SUPPLIER_NAME));
            String SupplierAdrs = cursor.getString(cursor.getColumnIndex(PendingTable.KEY_SUPPLIER_ADDRESS));
            String shortlistedInventoryDetailsID = cursor.getString(cursor.getColumnIndex(PendingTable.KEY_ShortlistedInventoryID));
            String inventoryID = cursor.getString(cursor.getColumnIndex(PendingTable.KEY_INVENTORY_ID));
            String inventoryType = cursor.getString(cursor.getColumnIndex(PendingTable.KEY_ACTIVTY_TYP));
            String inventoryName = cursor.getString(cursor.getColumnIndex(PendingTable.KEY_INVENTORY_TYP));
            String inventoryDate = cursor.getString(cursor.getColumnIndex(PendingTable.KEY_SCHEDULED_ACTIVITY_DATE));
            String proposalID = cursor.getString(cursor.getColumnIndex(PendingTable.KEY_PROPOSAL_ID));

            //get the pending counr
            String currentDate = Utils.getCurrentDateString();
            String pendingFromDays = String.valueOf(getPendingDaysCount(currentDate,inventoryDate));

            PendingData pendingData = new PendingData(proposalID, supplierID, SupplierName, SupplierAdrs, inventoryID, inventoryName, inventoryType, proposalName, inventoryDate,pendingFromDays,shortlistedInventoryDetailsID);
            mPendingDataList.add(pendingData);

        }

        cursor.close();
        mAdapter.notifyDataSetChanged();

    }//end fn

    // returns the number of days between : currentdate - assigneddate = pendingdays
    public long getPendingDaysCount(String currentDate, String inventoryDate) {

        long count = 0;
        try
        {
            SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date Curr = myFormat.parse(currentDate);
            Date Inv = myFormat.parse(inventoryDate);
            long diff = Curr.getTime() - Inv.getTime();
            long daysbw = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
            count = daysbw;
        }
        catch(ParseException e)
        {
             e.printStackTrace();
        }

        return count;
    }


    //populates the pending Table from the tables JONED Query
    // only if the new Data arrives
    public void populatePendingTable(){

        String LeftOuterJoin = " LEFT OUTER JOIN ";

        //query joins all the tables in manyone way
        String query = "SELECT * FROM " + ShortlistedSuppliersTable.TABLE_NAME + " SST" +
                LeftOuterJoin + ShortlistedInventoryDetailsTable.TABLE_NAME + " SIDT" + " ON " +
                "SST." + ShortlistedSuppliersTable.KEY_SHORTLISTED_SPACES_ID + " = " + "SIDT." + ShortlistedInventoryDetailsTable.KEY_SHORTLISTED_SPACES_ID +
                LeftOuterJoin + InventoryImagePathTable.TABLE_NAME + " IIPT" + " ON " +
                "SIDT." + ShortlistedInventoryDetailsTable.KEY_SHORTLISTED_INVENTORY_DETAILS_ID + " = " + "IIPT." + InventoryImagePathTable.KEY_SHORTLISTED_INVENTORY_ID +
                LeftOuterJoin + InventoryActivityTable.TABLE_NAME + " IAT" + " ON " +
                "SIDT." + ShortlistedInventoryDetailsTable.KEY_SHORTLISTED_INVENTORY_DETAILS_ID + " = " + "IAT." + InventoryActivityTable.KEY_SHORTLISTED_INVENTORY_DETAIL_ID +
                LeftOuterJoin + InventoryActivityAssignmentTable.TABLE_NAME + " IAAT" + " ON " +
                "IAT." + InventoryActivityTable.KEY_INVENTORY_ACTIVITY_ID + " = " + "IAAT." + InventoryActivityAssignmentTable.KEY_INVENTORY_ACTIVITY_ID
                + " ;";

        DataBaseHandler db_handle = DataBaseHandler.getInstance(getContext());
        SQLiteDatabase db = db_handle.getReadableDatabase();

        DataBaseHandler db_handle_pending = DataBaseHandler.getInstance(getContext());
        SQLiteDatabase db_pending = db_handle_pending.getWritableDatabase();


        Cursor cursor = db.rawQuery(query, null);

        while (cursor.moveToNext()){

            String str = cursor.getString(cursor.getColumnIndexOrThrow(InventoryImagePathTable.KEY_IMAGE_PATH));
            //  if (str != null)
            //    Log.e("InWhile ",str);

            String currentDate = Utils.getCurrentDateString();
            String inventoryDate = cursor.getString(cursor.getColumnIndexOrThrow(InventoryActivityAssignmentTable.KEY_INVENTORY_ACTIVITY_DATE));

            long compareDate = getPendingDaysCount(currentDate,inventoryDate);
            //  Log.e("IN WHile",String.valueOf(compareDate)+ " "+currentDate+" "+inventoryDate);


            // check if pending
            //  if (str == null && compareDate > 0)

            // only check the date
            if (str == null && compareDate > 0) {


                String proposalID = cursor.getString(cursor.getColumnIndexOrThrow(ShortlistedSuppliersTable.KEY_PROPOSAL_ID));
                String supplierID = cursor.getString(cursor.getColumnIndexOrThrow(ShortlistedSuppliersTable.KEY_SUPPLIER_ID));

                String inventoryName = cursor.getString(cursor.getColumnIndexOrThrow(ShortlistedInventoryDetailsTable.KEY_INVENTORY_NAME));
                String inventoryType = cursor.getString(cursor.getColumnIndexOrThrow(InventoryActivityTable.KEY_ACTIVITY_TYPE));
                String inventoryID = cursor.getString(cursor.getColumnIndexOrThrow(ShortlistedInventoryDetailsTable.KEY_INVENTORY_ID));
                String shortlistedInventoryDetailsID = cursor.getString(cursor.getColumnIndex(InventoryActivityTable.KEY_SHORTLISTED_INVENTORY_DETAIL_ID));

                String SuplierName = " ";
                String SuplierAdrs = " ";

                // check for diff society
                //TODO can be optimized by making it another function

                try {

                    String SupplierQuery = "SELECT * FROM " + BasicSupplierTable.TABLE_NAME + " WHERE " + BasicSupplierTable.KEY_SUPPLIER_ID + " = " + "\"" + supplierID + "\"" + " ; ";

                    DataBaseHandler db_handle1 = DataBaseHandler.getInstance(getContext());
                    SQLiteDatabase db1 = db_handle1.getReadableDatabase();
                    Cursor readSuplier = db1.rawQuery(SupplierQuery, null);

                    while (readSuplier.moveToNext()) {
                        SuplierName = readSuplier.getString(readSuplier.getColumnIndexOrThrow(BasicSupplierTable.KEY_NAME));
                        SuplierAdrs = readSuplier.getString(readSuplier.getColumnIndexOrThrow(BasicSupplierTable.KEY_ADDRESS));
                    }


                } catch (SQLiteException e) {
                    e.printStackTrace();
                }

                String proposalName = " ";

                try {
                    String ProposalQuery = "SELECT * FROM " + ProposalTable.TABLE_NAME + " WHERE " + ProposalTable.KEY_PROPOSAL_ID + " = " + "\"" + proposalID + "\"" + " ; ";
                    DataBaseHandler db_handle2 = DataBaseHandler.getInstance(getContext());
                    SQLiteDatabase db2 = db_handle2.getReadableDatabase();
                    //db_handle2.printTotalRows(ProposalTable.TABLE_NAME);

                    Cursor readProposal = db2.rawQuery(ProposalQuery, null);

                    while (readProposal.moveToNext()) {
                        proposalName = readProposal.getString(readProposal.getColumnIndex(ProposalTable.KEY_PROPOSAL_NAME));
                        //Log.e("proposal name :" , proposalName);
                    }


                } catch (SQLiteException e) {
                    e.printStackTrace();
                }


                ContentValues values = new ContentValues();
                values.put(PendingTable.KEY_ShortlistedInventoryID,shortlistedInventoryDetailsID);
                values.put(PendingTable.KEY_ACTIVTY_TYP,inventoryType);
                values.put(PendingTable.KEY_INVENTORY_ID,inventoryID);
                values.put(PendingTable.KEY_INVENTORY_TYP,inventoryName);
                values.put(PendingTable.KEY_PROPOSAL_ID,proposalID);
                values.put(PendingTable.KEY_PROPOSAL_NAME,proposalName);
                values.put(PendingTable.KEY_SUPPLIER_NAME,SuplierName);
                values.put(PendingTable.KEY_SUPPLIER_ADDRESS,SuplierAdrs);
                values.put(PendingTable.KEY_SCHEDULED_ACTIVITY_DATE,inventoryDate);
                values.put(PendingTable.KEY_SUPPLIER_ID,supplierID);
                values.put(PendingTable.KEY_isCompleted,"false");




                try {
                    // without this also the data will not be inserted as Primary key are ssid and inventory typ
                    //optimization only inert when data not present
                    if (PendingTableCheck(shortlistedInventoryDetailsID,inventoryType)==false){
                        db_pending.insert(PendingTable.TABLE_NAME, null, values);
                    }

                }
                catch (SQLiteConstraintException e){
                   // e.printStackTrace();
                }

            }//end if


        }// END While


    }//End fun

    //Checks if the data is already present in the Table
    public boolean PendingTableCheck( String ssid,String activityTyp){


        boolean isPresent = false;

        String Query = "SELECT * FROM "+PendingTable.TABLE_NAME+ " WHERE "
                + PendingTable.KEY_ShortlistedInventoryID+ " = \'"+ssid+"\' AND "+PendingTable.KEY_ACTIVTY_TYP+" = \'"+activityTyp+"\' ;";

        DataBaseHandler db_handle = DataBaseHandler.getInstance(getContext());
        SQLiteDatabase db = db_handle.getReadableDatabase();

        Cursor cursor = db.rawQuery(Query,null);

        int count = 0;
        if (cursor!=null) {
            while (cursor.moveToNext()) {
                count++;
            }
        }

        if (count>0){
            isPresent = true;
        }

       // Log.e("In PendFrag ",String.valueOf(isPresent));
        cursor.close();
        return isPresent;
    }

   // this method is called in ShowinventoryAdapter
    public String getAssignedDayByInventoryID(String shortlistedInventoryId, String inventoryTyp) {
        String assignedDate = " ";

        String Query = "SELECT * FROM " + InventoryActivityTable.TABLE_NAME + " IAT LEFT OUTER JOIN " + InventoryActivityAssignmentTable.TABLE_NAME
                + " IAAT ON " + " IAT." + InventoryActivityTable.KEY_INVENTORY_ACTIVITY_ID + " = " + "IAAT." + InventoryActivityAssignmentTable.KEY_INVENTORY_ACTIVITY_ID;

        DataBaseHandler db_handle = DataBaseHandler.getInstance(getContext());
        SQLiteDatabase db = db_handle.getReadableDatabase();

        Cursor cursor = db.rawQuery(Query, null);

        while (cursor.moveToNext()) {
            String AssignDate = " ";
            String shortlistedInventoryIdCheck = cursor.getString(cursor.getColumnIndexOrThrow(InventoryActivityTable.KEY_SHORTLISTED_INVENTORY_DETAIL_ID));
            String InventoryTypcheck = cursor.getString(cursor.getColumnIndexOrThrow(InventoryActivityTable.KEY_ACTIVITY_TYPE));
            if (shortlistedInventoryId.compareTo(shortlistedInventoryIdCheck) == 0 && inventoryTyp.compareTo(InventoryTypcheck) == 0) {
                AssignDate = cursor.getString(cursor.getColumnIndexOrThrow(InventoryActivityAssignmentTable.KEY_INVENTORY_ACTIVITY_DATE));
                Log.e("in IF :", shortlistedInventoryId + " " + inventoryTyp + " " + AssignDate);
                assignedDate = AssignDate;
            }


        }

        cursor.close();
        Log.e("in fn :", shortlistedInventoryId + " " + inventoryTyp + " " + assignedDate);
        return assignedDate;
    }


    /*****------------ Perivious Methods that were used --------------*******/

      /*
    private void prepareListData() {
        //TODO add data into the list

        String LeftOuterJoin = " LEFT OUTER JOIN ";

        //query joins all the tables in manyone way
        String query = "SELECT * FROM " + ShortlistedSuppliersTable.TABLE_NAME + " SST" +
                LeftOuterJoin + ShortlistedInventoryDetailsTable.TABLE_NAME + " SIDT" + " ON " +
                "SST." + ShortlistedSuppliersTable.KEY_SHORTLISTED_SPACES_ID + " = " + "SIDT." + ShortlistedInventoryDetailsTable.KEY_SHORTLISTED_SPACES_ID +
                LeftOuterJoin + InventoryImagePathTable.TABLE_NAME + " IIPT" + " ON " +
                "SIDT." + ShortlistedInventoryDetailsTable.KEY_SHORTLISTED_INVENTORY_DETAILS_ID + " = " + "IIPT." + InventoryImagePathTable.KEY_SHORTLISTED_INVENTORY_ID +
                LeftOuterJoin + InventoryActivityTable.TABLE_NAME + " IAT" + " ON " +
                "SIDT." + ShortlistedInventoryDetailsTable.KEY_SHORTLISTED_INVENTORY_DETAILS_ID + " = " + "IAT." + InventoryActivityTable.KEY_SHORTLISTED_INVENTORY_DETAIL_ID +
                LeftOuterJoin + InventoryActivityAssignmentTable.TABLE_NAME + " IAAT" + " ON " +
                "IAT." + InventoryActivityTable.KEY_INVENTORY_ACTIVITY_ID + " = " + "IAAT." + InventoryActivityAssignmentTable.KEY_INVENTORY_ACTIVITY_ID
                + " ;";

        DataBaseHandler db_handle = DataBaseHandler.getInstance(getContext());
        SQLiteDatabase db = db_handle.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        int pendingCount = 0;




        while (cursor.moveToNext()) {

            String str = cursor.getString(cursor.getColumnIndexOrThrow(InventoryImagePathTable.KEY_IMAGE_PATH));
          //  if (str != null)
            //    Log.e("InWhile ",str);

            String currentDate = Utils.getCurrentDateString();
            String inventoryDate = cursor.getString(cursor.getColumnIndexOrThrow(InventoryActivityAssignmentTable.KEY_INVENTORY_ACTIVITY_DATE));

            long compareDate = getPendingDaysCount(currentDate,inventoryDate);
          //  Log.e("IN WHile",String.valueOf(compareDate)+ " "+currentDate+" "+inventoryDate);


            // check if pending
            //  if (str == null && compareDate > 0)

            // only check the date
            if (str == null && compareDate > 0) {

                String proposalID = cursor.getString(cursor.getColumnIndexOrThrow(ShortlistedSuppliersTable.KEY_PROPOSAL_ID));
                String supplierID = cursor.getString(cursor.getColumnIndexOrThrow(ShortlistedSuppliersTable.KEY_SUPPLIER_ID));

                String inventoryName = cursor.getString(cursor.getColumnIndexOrThrow(ShortlistedInventoryDetailsTable.KEY_INVENTORY_NAME));
                String inventoryType = cursor.getString(cursor.getColumnIndexOrThrow(InventoryActivityTable.KEY_ACTIVITY_TYPE));
                String inventoryID = cursor.getString(cursor.getColumnIndexOrThrow(ShortlistedInventoryDetailsTable.KEY_INVENTORY_ID));
                String shortlistedInventoryDetailsID = cursor.getString(cursor.getColumnIndex(InventoryActivityTable.KEY_SHORTLISTED_INVENTORY_DETAIL_ID));

                String SuplierName = " ";
                String SuplierAdrs = " ";

                // check for diff society
                //TODO can be optimized by making it another function

                try {

                    String SupplierQuery = "SELECT * FROM " + BasicSupplierTable.TABLE_NAME + " WHERE " + BasicSupplierTable.KEY_SUPPLIER_ID + " = " + "\"" + supplierID + "\"" + " ; ";

                    DataBaseHandler db_handle1 = DataBaseHandler.getInstance(getContext());
                    SQLiteDatabase db1 = db_handle1.getReadableDatabase();
                    Cursor readSuplier = db1.rawQuery(SupplierQuery, null);

                    while (readSuplier.moveToNext()) {
                        SuplierName = readSuplier.getString(readSuplier.getColumnIndexOrThrow(BasicSupplierTable.KEY_NAME));
                        SuplierAdrs = readSuplier.getString(readSuplier.getColumnIndexOrThrow(BasicSupplierTable.KEY_ADDRESS));
                    }


                } catch (SQLiteException e) {
                    e.printStackTrace();
                }

                String proposalName = " ";

                try {
                    String ProposalQuery = "SELECT * FROM " + ProposalTable.TABLE_NAME + " WHERE " + ProposalTable.KEY_PROPOSAL_ID + " = " + "\"" + proposalID + "\"" + " ; ";
                    DataBaseHandler db_handle2 = DataBaseHandler.getInstance(getContext());
                    SQLiteDatabase db2 = db_handle2.getReadableDatabase();
                    Cursor readProposal = db2.rawQuery(ProposalQuery, null);

                    while (readProposal.moveToNext()) {
                        proposalName = readProposal.getString(readProposal.getColumnIndex(ProposalTable.KEY_PROPOSAL_NAME));
                        Log.e("proposal name :" , proposalName);
                    }


                } catch (SQLiteException e) {
                    e.printStackTrace();
                }

                // int Count = getPendingCount(proposalID,supplierID);

                // public PendingData(String proposalID,String supplierID,String supplierName,String supplierAddress,String inventoryID,
                // String inventoryName,String inventoryTyp,String proposalName)
                //  PendingData pendingData = new PendingData(proposalID,supplierID,SuplierName,SuplierAdrs,Count);

                String pendingFromDays = String.valueOf(getPendingDaysCount(currentDate,inventoryDate));
               // Log.e("Inside WhileIF: ",pendingFromDays+" "+currentDate+" "+inventoryDate);
                PendingData pendingData = new PendingData(proposalID, supplierID, SuplierName, SuplierAdrs, inventoryID, inventoryName, inventoryType, proposalName, inventoryDate,pendingFromDays,shortlistedInventoryDetailsID);
                mPendingDataList.add(pendingData); //populate list

                pendingCount++;

            }


        }

        Log.e("IN preparePenddata ",String.valueOf(pendingCount)+ " ");
        cursor.close();
        mAdapter.notifyDataSetChanged();
    }
    */


   /* public Cursor getJoinedQueryForPending() {
        String LeftOuterJoin = " LEFT OUTER JOIN ";

        //query joins all the tables in manyone way
        String query = "SELECT * FROM " + ShortlistedSuppliersTable.TABLE_NAME + " SST" +
                LeftOuterJoin + ShortlistedInventoryDetailsTable.TABLE_NAME + " SIDT" + " ON " +
                "SST." + ShortlistedSuppliersTable.KEY_SHORTLISTED_SPACES_ID + " = " + "SIDT." + ShortlistedInventoryDetailsTable.KEY_SHORTLISTED_SPACES_ID +
                LeftOuterJoin + InventoryImagePathTable.TABLE_NAME + " IIPT" + " ON " +
                "SIDT." + ShortlistedInventoryDetailsTable.KEY_SHORTLISTED_INVENTORY_DETAILS_ID + " = " + "IIPT." + InventoryImagePathTable.KEY_SHORTLISTED_INVENTORY_ID +
                LeftOuterJoin + InventoryActivityTable.TABLE_NAME + " IAT" + " ON " +
                "SIDT." + ShortlistedInventoryDetailsTable.KEY_SHORTLISTED_INVENTORY_DETAILS_ID + " = " + "IAT." + InventoryActivityTable.KEY_SHORTLISTED_INVENTORY_DETAIL_ID +
                LeftOuterJoin + InventoryActivityAssignmentTable.TABLE_NAME + " IAAT" + " ON " +
                "IAT." + InventoryActivityTable.KEY_INVENTORY_ACTIVITY_ID + " = " + "IAAT." + InventoryActivityAssignmentTable.KEY_INVENTORY_ACTIVITY_ID
                + " ;";

        DataBaseHandler db_handle = DataBaseHandler.getInstance(getContext());
        SQLiteDatabase db = db_handle.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);
        return cursor;
    }*/


    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    /*

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private PendingFragment.ClickListener clickListener;


        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final PendingFragment.ClickListener clickListener){
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context,new GestureDetector.SimpleOnGestureListener(){
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

    //**************** end of my edit ****************************

*/
    /*Main Variable from Assighned*/
/*
    private String jsonResult;
    ArrayList<String> pictures= new ArrayList<String>();//Array to store pictures
    ArrayList<String> locationarray= new ArrayList<String>();
    ArrayList<String> addressarray= new ArrayList<String>();
    ArrayList<String> idarray = new ArrayList<String>();
    ArrayList<String> submitStatusArray = new ArrayList<String>();
    ArrayList<String> inventDateArray = new ArrayList<String>();
    String getIntActivity;


    private String url = "http://machadalo.com//android/audit/media/assigned.php"; // our Url to server


    int element,total;
    // flag for Internet connection status
    Boolean isInternetPresent = true;
    ConnectionDetector cd;
    String strAdCount;
    int getIntentAct;
    // /*Main Variable from Assighned
    private View myFragmentView;
    private RecyclerView recyclerView;
    private DataAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<Data> dataList = new ArrayList<>();
    private List<AuditGS> pendingData;
    private DataBaseHandler sqliteData;
    public PendingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       getPendingData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
      //  getPendingData();
        View rootView = null;
        myFragmentView = inflater.inflate(R.layout.fragment_two, container, false);

        // use this setting to improve performance if you know that changes
        sqliteData = DataBaseHandler.getInstance(getContext());
        pendingData = sqliteData.getAllPendingAudit();

        // in content do not change the layout size of the RecyclerView
        recyclerView = (RecyclerView) myFragmentView.findViewById(R.id.recycler_view);

        mAdapter = new DataAdapter(dataList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());

        if(!prefs.getBoolean("firstTime_Two", false)) {
            // <---- run your one time code here
            accessWebService();
            // mark first time has runned.
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("firstTime_Two", true);
            editor.commit();
        }
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Data data = dataList.get(position);
                //Toast.makeText(getContext(), data.getsocietyName() + " is selected!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getContext(), AssignedActivity.class);
//                intent.putExtra("inventoryIDRec",data.getadID());
//                intent.putExtra("getIntentAct",false);

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
        task.execute(new String[]{url});
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



        int i=0;
        for ( i=0; i<pendingData.size();i++) {
            String inventID = pendingData.get(i).get_ad_inventory_ID();
            String societyName = pendingData.get(i).get_society_name();
            String adType = pendingData.get(i).get_ad_type();
            String societyAddress = pendingData.get(i).get_society_address();
            String imageURL = "MD_MUMPOHNRSOC2_CIv4mT9PlE";
            String submitStatus = pendingData.get(i).get_submit_status();
            String adDate = pendingData.get(i).get_date();
            String inventType = pendingData.get(i).get_invent_type();
//            prepareDataData(inventID,societyName,adType,societyAddress,imageURL,submitStatus,adDate,inventType,null,adType);




        }
    }

//    private void prepareDataData(String adID, String societyName, String adType,String societyAddress,String url,String sbmit_status,String date,String inventoryType, String business_name,String audit_type) {
//        Data Data = new Data(adID, societyName, adType,societyAddress,url,sbmit_status,date,inventoryType,business_name,audit_type);
//        dataList.add(Data);
//        mAdapter.notifyDataSetChanged();
//    }
    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private PendingFragment.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final PendingFragment.ClickListener clickListener) {
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
*/

}
