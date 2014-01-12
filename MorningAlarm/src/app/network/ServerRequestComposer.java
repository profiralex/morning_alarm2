package app.network;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import app.utils.Alarm;
import app.utils.Constants;
import app.utils.Group;
import app.utils.Person;

/**
 * Created by alexandr on 1/6/14.
 */
public class ServerRequestComposer {

    public static void sendRegistrationIdToBackend(String regId, String email) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(Constants.SERVER_ADRESS + Constants.SERVER_REGISTER_PAGE);
        try {
            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("regId", regId));
            nameValuePairs.add(new BasicNameValuePair("email", email));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);

            if(response.getStatusLine().getStatusCode() == 200){
                Log.d(Constants.TAG, "Email registered :" + email);
            }else{
                Log.d(Constants.TAG, "Status code :" + response.getStatusLine().getStatusCode());
            }



        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendRequestToPerson(Group group, Person person, Alarm alarm, String regId) throws JSONException, IOException {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(Constants.SERVER_ADRESS + Constants.SERVER_SEND_REQUEST_PAGE);
        try {
            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("person_email", person.getEmail()));
            nameValuePairs.add(new BasicNameValuePair("group_id", group.getId()+""));
            nameValuePairs.add(new BasicNameValuePair("group_message", group.getMessage()));
            nameValuePairs.add(new BasicNameValuePair("alarm_days_of_week", alarm.getDaysOfWeek()));
            nameValuePairs.add(new BasicNameValuePair("alarm_time", alarm.getTime() + ""));
            nameValuePairs.add(new BasicNameValuePair("alarm_wake_up_mode",alarm.getWakeUpMode()));
            nameValuePairs.add(new BasicNameValuePair("myRegId", regId));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);

            HttpEntity entity = response.getEntity();
            String responseString = EntityUtils.toString(entity, "UTF-8");
            Log.d(Constants.TAG,responseString);

            if(response.getStatusLine().getStatusCode() == 200){
                Log.d(Constants.TAG, "Request sent");
            }else{
                Log.d(Constants.TAG, "Status code :" + response.getStatusLine().getStatusCode());
            }

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendResponseToPerson(String email, String groupId, String answer, String regId) throws JSONException, IOException {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(Constants.SERVER_ADRESS + Constants.SERVER_SEND_RESPONSE_PAGE);
        try {
            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("person_email", email));
            nameValuePairs.add(new BasicNameValuePair("group_id", groupId));
            nameValuePairs.add(new BasicNameValuePair("response", answer));
            nameValuePairs.add(new BasicNameValuePair("myRegId", regId));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);

            HttpEntity entity = response.getEntity();
            String responseString = EntityUtils.toString(entity, "UTF-8");
            Log.d(Constants.TAG,responseString);

            if(response.getStatusLine().getStatusCode() == 200){
                Log.d(Constants.TAG, "Request sent");
            }else{
                Log.d(Constants.TAG, "Status code :" + response.getStatusLine().getStatusCode());
            }

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
