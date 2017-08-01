package com.machadalo.audit.extras;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lokesh on 14-06-2017.
 */

public class PendingData {

    //uses it in shoing list in pendingfragment
    private String SupplierName,SupplierAddress;
    private String proposalID,supplierID,mPendingCount,proposalName,inventoryAssignedDate;
    //to show the Details
    private String inventoryTyp,inventoryID,inventoryName,shortlistedInventoryDetailsId;

    private  String pendingDaysCount = "0";

    private List<String> validActs = new ArrayList<>();

//    public PendingData(String proposalID,String supplierID,String supplierName,String supplierAddress,int count){
//        this.proposalID = proposalID;
//        this.supplierID = supplierID;
//        this.SupplierAddress = supplierAddress;
//        this.SupplierName = supplierName;
//        this.mPendingCount = Integer.toString(count);
//       setPendingDaysCount(this.proposalID,this.supplierID);
//    }

    public PendingData(String proposalID,String supplierID,String supplierName,String supplierAddress,String inventoryID,
                       String inventoryName,String inventoryTyp,String proposalName,String inventoryAssignedDate,String pendingDaysCount,String shortlistedInventoryDetailsId){

        this.proposalID = proposalID;
        this.supplierID = supplierID;
        this.SupplierAddress = supplierAddress;
        this.SupplierName = supplierName;
        this.inventoryID =inventoryID;
        this.inventoryName = inventoryName;
        this.inventoryTyp = inventoryTyp;
        this.proposalName = proposalName;
        this.inventoryAssignedDate = inventoryAssignedDate;
        this.shortlistedInventoryDetailsId = shortlistedInventoryDetailsId;
        this.pendingDaysCount = pendingDaysCount;


        //set data for spinner
      //  setValidActs(inventoryID);
    }

/*
    public void setValidActs(String inventoryID){
        PendingFragment pf = new PendingFragment();
        Cursor cursor = pf.getJoinedQueryForPending();
        String currentDate = "";
        String inventoryDate = "";

        while (cursor.moveToNext()){

            String str = cursor.getString(cursor.getColumnIndexOrThrow(InventoryImagePathTable.KEY_IMAGE_PATH));
            currentDate = Utils.getCurrentDateString();
            inventoryDate = cursor.getString(cursor.getColumnIndexOrThrow(InventoryActivityAssignmentTable.KEY_INVENTORY_ACTIVITY_DATE));

            int compareDate = pf.ComapareDate(currentDate,inventoryDate);

            // check if pending
            if(str == null && compareDate == 1 ) {

                String inventoryIdCheck = cursor.getString(cursor.getColumnIndexOrThrow(ShortlistedInventoryDetailsTable.KEY_INVENTORY_ID));

                if (inventoryID.compareTo(inventoryIdCheck)==0){
                    this.shortlistedInventoryDetailsId =  cursor.getString(cursor.getColumnIndexOrThrow(InventoryImagePathTable.KEY_SHORTLISTED_INVENTORY_ID));
                    String inventoryTyp =  cursor.getString(cursor.getColumnIndexOrThrow(InventoryActivityTable.KEY_ACTIVITY_TYPE));
                    validActs.add(inventoryTyp);
                }
            }

        }



    }

*/


    public List<String> getValidActs(){
        return validActs;
    }

    public String getSupplierName(){
        return this.SupplierName;
    }

    public String getSupplierAddress(){
        return this.SupplierAddress;
    }

    public String getProposalID(){
        return this.proposalID;
    }

    public String getItemPendingCount(){
        return this.mPendingCount;
    }

    public String getSupplierID(){
        return this.supplierID;
    }

    public String getInventoryID(){
        return this.inventoryID;
    }

    public String getShortlistedInventoryDetailsId(){
        return this.shortlistedInventoryDetailsId;
    }

    public String getInventoryName(){
        return this.inventoryName;
    }

    public String getPendingDaysCount(){
        return pendingDaysCount;
    }

    public String getProposalName(){
        return this.proposalName;
    }

    public String getInventoryTyp(){
        return this.inventoryTyp;
    }

    public String getInventoryAssignedDate(){
        return  this.inventoryAssignedDate;
    }
}
