package app.utils;

/**
 * Created by alexandr on 1/6/14.
 */
public class Constants {
    public static final String SENDER_ID = "1044298355239";
    public static final String TAG = "MORNING_ALARM";
    public static final String SERVER_ADRESS = "http://is-project.comxa.com";
    public static final String SERVER_REGISTER_PAGE = "/register.php";
    public static final String SERVER_SEND_REQUEST_PAGE = "/send_request.php";
    public static final String SERVER_SEND_RESPONSE_PAGE = "/send_response.php";
    public static final String EXTRA_MESSAGE = "message";
    public static final String EXTRA_MESSAGE_TYPE = "type";
    public static final String TYPE_MESSAGE = "message";
    public static final String TYPE_REGISTRATION = "registration";
    public static final String TYPE_ALARM = "alarm";
    public static final String TYPE_REQUEST = "request";
    public static String myRegId = "";

    public static String getMyRegId(){
        return  myRegId;
    }

    public static void setMyRegId(String regId){
        myRegId = regId;
    }


}
