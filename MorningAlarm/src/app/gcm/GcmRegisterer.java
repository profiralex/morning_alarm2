package app.gcm;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Patterns;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import app.network.ServerRequestComposer;
import app.utils.Constants;

/**
 * Created by alexandr on 1/6/14.
 */
public class GcmRegisterer {

    private GoogleCloudMessaging gcm;
    private Activity activity;
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    /**
     * Substitute you own sender ID here. This is the project number you got
     * from the API Console, as described in "Getting Started."
     */


    private AtomicInteger msgId = new AtomicInteger();
    private SharedPreferences prefs;

    private String regId;

    public GcmRegisterer(Activity activity){
        this.activity = activity;
    }

    public boolean setUpGcm(){
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(activity);
            regId = getRegistrationId(activity);

            if (regId.isEmpty()) {
                registerInBackground();
            } else {
                Log.d(Constants.TAG, "" + regId);
            }
        } else {
            Log.i(Constants.TAG, "No valid Google Play Services APK found.");
        }
        return checkPlayServices();
    }

    public boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, activity,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(Constants.TAG, "This device is not supported.");
            }
            return false;
        }
        return true;
    }

    public String getRegistrationId(Context activity) {
        final SharedPreferences prefs = getGCMPreferences(activity);
        String registrationId = "";//prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(Constants.TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(activity);
        if (registeredVersion != currentVersion) {
            Log.i(Constants.TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    public static int getAppVersion(Context activity) {
        try {
            PackageInfo packageInfo = activity.getPackageManager()
                    .getPackageInfo(activity.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p/>
     * Stores the registration ID and app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(activity);
                    }
                    regId = gcm.register(Constants.SENDER_ID);
                    msg = "Device registered, registration ID=" + regId;

                    sendRegistrationIdToBackend();

                    storeRegistrationId(activity, regId);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Log.i(Constants.TAG, msg);
            }
        }.execute(null, null, null);
    }

    private void sendRegistrationIdToBackend(){
        ArrayList<String> emails = getAccountEmail();
        for (String email : emails) {
            ServerRequestComposer.sendRegistrationIdToBackend(regId, email);
        }
    }

    private ArrayList<String> getAccountEmail(){
        Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
        Account[] accounts = AccountManager.get(activity).getAccounts();
        ArrayList<String> emails = new ArrayList();
        for (Account account : accounts) {
            if (emailPattern.matcher(account.name).matches()) {
               emails.add(account.name);
            }
        }
        return emails;
    }

    /**
     * Stores the registration ID and app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param activity application's activity.
     * @param regId   registration ID
     */
    private void storeRegistrationId(Context activity, String regId) {
        final SharedPreferences prefs = getGCMPreferences(activity);
        int appVersion = getAppVersion(activity);
        Log.i(Constants.TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGCMPreferences(Context activity) {
        return activity.getSharedPreferences(activity.getClass().getSimpleName(),
                Context.MODE_PRIVATE);
    }
}
