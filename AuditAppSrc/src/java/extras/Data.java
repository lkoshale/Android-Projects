package com.machadalo.audit.extras;

/**
 * Created by Asna Euphoria on 23-01-2016.
 */
public class Data {
    private String adID, adType, societyName,societyAddress,submit_status,date,inventoryType,business_name,audit_type;
    private String supplierId;
    private String supplierContentTypeId;
    private String latitude;
    private String longitude;
    private String address;
    private String proposalId;
    private String shortlistedSpacesId;
    private String selectedDate;
    private String mapUri = null;

    public int distance = -1;

//    private String adID, adType, societyName,societyAddress,submit_status,date,inventoryType,business_name,audit_type;
    private  String campaignName, supplierID, supplierName, supplierAddress1, supplierAddress2;
    String url;
    public Data() {
    }

    public String getAddress(){
        return address;
    }
    public String getLatitude(){
        return latitude;
    }
    public String getLongitude(){
        return longitude;
    }
    public void setAddress( String address ) {
        this.address = address;
    }

    public void setLatitude ( String latitude ) {
        this.latitude = latitude;
    }
    public void setLongitude( String longitude ) {
        this.longitude = longitude;
    }

    public void setSupplierId( String supplierId) {
        this.supplierId = supplierId;
    }


//    public Data(String adID, String societyName, String adType,String societyAddress,String url,String submit_status,String date,String inventoryType,String business_name, String audit_type) {
//        this.adID = adID;
//        this.adType = adType;
//        this.date = date;
//        this.societyName = societyName;
//        this.societyAddress = societyAddress;
//        this.submit_status = submit_status;
//        this.inventoryType = inventoryType;
//        this.business_name = business_name;
//        this.audit_type = audit_type;
//        //this.url = "http://android.infiniteloopsinc.com/audit/media/inventoryAd/"+url;
//        this.url = "http://mdimages.s3.amazonaws.com/"+url;
//
//
//    }

    public Data(String campaignName, String supplierID, String supplierName, String supplierAddress1, String supplierAddress2, String url,String lat,String lon) {
        this.campaignName = campaignName;
        this.supplierID = supplierID;
        this.supplierName = supplierName;
        this.supplierAddress1 = supplierAddress1;
        this.supplierAddress2 = supplierAddress2;
        this.latitude = lat;
        this.longitude = lon;
        //this.url = "http://android.infiniteloopsinc.com/audit/media/inventoryAd/"+url;
        this.url = url;


    }

    public void setBusinessName( String name) {
        this.business_name = name;
    }

    public void setSupplier_Name(String name) {
        this.societyName = name;
    }

    public void setAuditType( String type ){
        this.audit_type = type;
    }

    public String getSupplierId(){
        return supplierId;
    }

    public void setShortlistedSpacesId(String id) {
        this.shortlistedSpacesId = id;
    }

    public  String  getShortlistedSpacesId( ){
        return shortlistedSpacesId;
    }

    public String getSupplierContentTypeId() {
        return supplierContentTypeId;
    }

    public String getProposalId(){
        return proposalId;
    }

    public String getAudit_type() {
        return audit_type;
    }
    public String getInventoryType() {
        return inventoryType;
    }
    public String getDate() {
        return date;
    }
    public String getCampaignName() {
        return campaignName;
    }

    public void setCampaignName(String campaignName) {
        this.campaignName = campaignName;
    }

    public String getSupplierID() {
        return supplierID;
    }

    public void setSupplierID(String supplierID) {
        this.supplierName = supplierName;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getSupplierAddress1() {
        return supplierAddress1;
    }

    public void setSupplierAddress1(String supplierAddress1) {
        this.supplierAddress1 = supplierAddress1;
    }

    public String getSupplierAddress2() {
        return supplierAddress2;
    }

    public void setSupplierAddress2(String supplierAddress2) {
        this.supplierAddress2 = supplierAddress2;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setSelectedDate(String selectedDate){
        this.selectedDate = selectedDate;
    }

    public String getSelectedDate(){
        return this.selectedDate;
    }

    public void setMapUri(String uri){
        this.mapUri = uri;
    }

    public String getMapUri(){
        return this.mapUri;
    }

//    public String getAudit_type() {
//        return audit_type;
//    }
//    public String getInventoryType() {
//        return inventoryType;
//    }
//    public String getDate() {
//        return date;
//    }
//
//    public void setDate(String date) {
//        this.date = date;
//    }
//    public String getBusiness_name() {
//        return business_name;
//    }
//
//    public String getSubmit_status() {
//        return submit_status;
//    }
//
//    public void setSubmit_status(String submit_status) {
//        this.submit_status = submit_status;
//    }
//    public String getadID() {
//        return adID;
//    }
//
//    public void setadID(String name) {
//        this.adID = name;
//    }
//
//    public String getsocietyName() {
//        return societyName;
//    }
//
//    public void setsocietyName(String societyName) {
//        this.societyName = societyName;
//    }
//
//    public String getadType() {
//        return adType;
//    }
//
//    public void setadType(String adType) {
//        this.adType = adType;
//    }
//
//    public String getSocietyAddress() {
//        return societyAddress;
//    }
//
//    public void setSocietyAddress(String societyAddress) {
//        this.societyAddress = societyAddress;
//    }
//
//    public String getUrl() {
//        return url;
//    }
//
//    public void setUrl(String url) {
//        this.url = url;
//    }
}
