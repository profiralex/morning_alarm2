package app.network;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import app.utils.Constants;

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

}
