package com.machadalo.audit;

/**
 * Created by root on 3/2/17.
 */

public class Constants {

    //yogesh sir address
  //  public static String BASE_ADDRESS = "http://192.168.1.19:8000";
    //Nikhil sir address
   //public static String BASE_ADDRESS = "http://192.168.1.16:8000";
    // public static String BASE_ADDRESS = "http://192.168.2.4:8000";
    //public static String BASE_ADDRESS = "http://192.168.0.106:8000";
    //public static String BASE_ADDRESS = "http://192.168.2.9:8000";
    //public static String BASE_ADDRESS = "http://192.168.0.26:8000";
   // public static String BASE_ADDRESS = "http://192.168.1.111:8000";
    //public static String BASE_ADDRESS = "http://192.168.43.10:8000";
   public static String BASE_ADDRESS = "http://coreapi-test.3j6wudg4pu.ap-southeast-1.elasticbeanstalk.com/";
  //  public static String BASE_ADDRESS = "http://192.168.1.8:8000";

    public static String AUTH_TOKEN;
    public static String USER_ID = "";
    public static String  INVENTORY_ACTIVITY_IMAGE_URL =  BASE_ADDRESS + "/v0/ui/website/inventory-activity-image/";
    public static String LIST_CAMPAIGN_SUPPLIERS_URL = BASE_ADDRESS + "/v0/ui/website/campaigns-suppliers-inventory-list/"; // our Url to server
    public static String LOGIN_URL = BASE_ADDRESS + "/api-token-auth/"; // our Url to server
    public static String BULK_IMAGE_UPLOAD_URL = BASE_ADDRESS + "/v0/ui/website/bulk-update-inventory-activity-image/";
    public static  String GET_PROPOSAL_DATA = BASE_ADDRESS + "/v0/ui/website/proposal/"; // <proposal id>

    public static String GET_USER_INFO = BASE_ADDRESS+"v0/user/";
    public static int OK_RESPONSE_CODE = 200;
    public static final String False = "False";
    public static final String True  = "True";
    public static final String bucket = "androidtokyo";

    public static final String Today = "Today";
    public static final String Tomorrow = "Tomorrow";
    public static final String DayAfterTomorrow = "DayAfterTomorrow";

}
