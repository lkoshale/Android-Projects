package com.machadalo.audit.extras;

/**
 * Created by DELL-PC on 2/8/2016.
 */
public class QueueData {

    // This is the data structure to hold data that is to be displayed on Queued tab of the app
    // This class is also data structure to pull queued data from db and upload to server when phone
    // connects to internet.

    private  String adID;
    private String adType;
    private String societyName;
    private String societyAddress;
    private String url;

    private String shortlistedInventoryDetailId;
    private String imagePath;
    private String comment;
    private String activityType;
    private String activityDate;
    private String inventoryName;
    private String inventoryId;
    private String localImagePath;
    private String inventoryImagePathTableId;
    private String isDjangoUploaded;
    private String isAmazonUploaded;
    private String lat;
    private String lon;

    private String proposalName;
    private String proposalId;
    private String distance;


    public QueueData(  ) {
    }

    public void setShortlistedInventoryDetailId(String shortlistedInventoryDetailId){
        this.shortlistedInventoryDetailId = shortlistedInventoryDetailId;
    }
    public void setInventoryImagePathTableId( String inventoryImagePathTableId) {
        this.inventoryImagePathTableId = inventoryImagePathTableId;
    }

    public void setIsDjangoUploaded( String verdict ) {
        this.isDjangoUploaded = verdict;
    }

    public void setIsAmazonUploaded( String verdict ) {
        this.isAmazonUploaded = verdict;
    }

    public void setLocalImagePath(String localImagePath ) {
        this.localImagePath = localImagePath;
    }

    public void setImagePath( String imagePath) {
        this.imagePath = imagePath;
    }

    public void setComment ( String comment ) {
        this.comment = comment;
    }

    public void setActivityType( String activityType) {
        this.activityType = activityType;
    }

    public void setActivityDate ( String activityDate ){
        this.activityDate = activityDate;
    }
    public void setInventoryId( String inventoryId) {
        this.inventoryId = inventoryId;
    }

    public String getShortlistedInventoryDetailId(){
        return shortlistedInventoryDetailId;
    }
    public String getImagePath() {
        return imagePath;
    }
    public String getActivityType(){
        return activityType;
    }
    public String getActivityDate(){
        return activityDate;
    }
    public String getComment(){
        return comment;
    }
    public String getInventoryId() {
        return inventoryId;
    }
    public String getIsDjangoUploaded(){
        return isDjangoUploaded;
    }

    public String getIsAmazonUploaded() {
        return  isAmazonUploaded;
    }

    public String getInventoryImagePathTableId(){
        return inventoryImagePathTableId;
    }

    public String getLocalImagePath(){
        return localImagePath;
    }

    public QueueData(String adID, String societyName, String adType, String societyAddress, String url) {
        this.adID = adID;
        this.adType = adType;
        this.societyName = societyName;
        this.societyAddress = societyAddress;
        this.url = url;

    }

    public  String getAdID() {
        return adID;
    }

    public void setAdID(String adID) {
        this.adID = adID;
    }

    public String getAdType() {
        return adType;
    }

    public void setAdType(String adType) {
        this.adType = adType;
    }

    public String getSocietyName() {
        return societyName;
    }

    public void setSocietyName(String societyName) {
        this.societyName = societyName;
    }

    public  String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSocietyAddress() {
        return societyAddress;
    }

    public void setSocietyAddress(String societyAddress) {
        this.societyAddress = societyAddress;
    }

    public void setLat(String lat){
        this.lat = lat;
    }
    public void setLon(String Lon){
        this.lon = Lon;
    }

    public String getLat(){
        return this.lat;
    }

    public String getLon(){
        return this.lon;
    }

    public void setProposalName(String proposalName){
        this.proposalName = proposalName;
    }

    public String getProposalName(){
        return this.proposalName;
    }

    public void setInventoryName(String inventoryName){
        this.inventoryName = inventoryName;
    }

    public String getInventoryName(){
        return this.inventoryName;
    }

    public void setDistance(String distance){
        this.distance = distance;
    }
    public String getDistance(){
        return this.distance;
    }

}
