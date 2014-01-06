package app.morningalarm;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import app.alarmmanager.AlarmSetter;
import app.database.AlarmDbAdapter;
import app.database.AlarmDbUtilities;

/**
 * activitatea principala a aplicatiei
 * care afiseaza lista cu alarme si optiunile lor
 * @author ALEXANDR
 *
 */
public class AlarmListActivity extends Activity {
	
	private ArrayList<Alarm> alarmList;
	private String lastId;
	private int lastIndex;
	private AlarmListAdapter ad;
    private GoogleCloudMessaging gcm;
    private String SENDER_ID = "1044298355239";
    private String registrationId;
    private Context context;

    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    /**
     * Substitute you own sender ID here. This is the project number you got
     * from the API Console, as described in "Getting Started."
     */

    /**
     * Tag used on log messages.
     */
    static final String TAG = "GCMDemo";

    TextView mDisplay;
    AtomicInteger msgId = new AtomicInteger();
    SharedPreferences prefs;

    String regid;

	
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @Override
    /**
	 * metoda atribuie listei din vedere adapterul pentru afisarea alarmelor setate
	 * si atribuie listeneruri pentru butoane
	 */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        alarmList = AlarmDbUtilities.fetchAllAlarms(this);

        //GCM
        context = this.getApplicationContext();
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            regid = getRegistrationId(context);

            if (regid.isEmpty()) {
                registerInBackground();
            }else{
                Log.d("TAG",""+regid);
            }
        } else {
            Log.i(TAG, "No valid Google Play Services APK found.");
        }

        Button b = (Button)this.findViewById(R.id.add_btn);
        b.setOnClickListener(new OnClickListener(){
        	/**
        	 * listener onclick pentru butonul add Alarm
        	 */
			public void onClick(View arg0) {
				Alarm newAlarm = AlarmDbUtilities.fetchNewAlarm(AlarmListActivity.this);
				lastIndex = alarmList.size();
				alarmList.add(newAlarm);
				Intent i;
				if (Build.VERSION.SDK_INT < 11) {
				    i = new Intent(AlarmListActivity.this, AlarmSettingsActivity.class);
				} else {
					i= new Intent(AlarmListActivity.this, AlarmFragmentsSettingsActivity.class);
				}
				i.putExtra(AlarmDbAdapter.KEY_ID, newAlarm.getId());
				lastId = newAlarm.getId();
				AlarmListActivity.this.startActivityForResult(i,0);
				
			}
        	
        });
        
        ad=new AlarmListAdapter(this,R.layout.list_item_main,alarmList);
        ListView lv=(ListView)this.findViewById(R.id.listView1);
        lv.setAdapter(ad);
        
        lv.setOnItemClickListener(new OnItemClickListener(){
        	/**
        	 * metoda onclick pentru Listenerul al elementului din lista
        	 */
			@SuppressLint("NewApi")
			public void onItemClick(AdapterView<?> adapter, View view, int arg,
					long position) {
				int pos = (int) position;
				Intent i;
				if (Build.VERSION.SDK_INT < 11) {
				    i = new Intent(AlarmListActivity.this, AlarmSettingsActivity.class);
				} else {
					i= new Intent(AlarmListActivity.this, AlarmFragmentsSettingsActivity.class);
				}
				i.putExtra("id", alarmList.get(pos).getId());
				lastId = alarmList.get(pos).getId();
				lastIndex = pos;
				AlarmListActivity.this.startActivityForResult(i,0);
			}
        });
        
        emptyTextViewVisibility();
        this.registerForContextMenu(lv);
    }



    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i("TAG", "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    // You need to do the Play Services APK check here too.
    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
    }

    /**
            * Gets the current registration ID for application on GCM service.
            * <p>
    * If result is empty, the app needs to register.
            *
            * @return registration ID, or empty string if there is no existing
    *         registration ID.
            */
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = "";//prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGCMPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return getSharedPreferences(AlarmListActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p>
     * Stores the registration ID and app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() {
        new AsyncTask<Void,Void,String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;

                    // You should send the registration ID to your server over HTTP,
                    // so it can use GCM/HTTP or CCS to send messages to your app.
                    // The request to your server should be authenticated if your app
                    // is using accounts.
                    sendRegistrationIdToBackend();

                    // For this demo: we don't need to send it because the device
                    // will send upstream messages to a server that echo back the
                    // message using the 'from' address in the message.

                    // Persist the regID - no need to register again.
                    //storeRegistrationId(context, regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Log.i(TAG, msg);
            }
        }.execute(null, null, null);
    }

    /**
     * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP
     * or CCS to send messages to your app. Not needed for this demo since the
     * device sends upstream messages to a server that echoes back the message
     * using the 'from' address in the message.
     */
    private void sendRegistrationIdToBackend() {
        // Your implementation here.
        // Create a new HttpClient and Post Header
        /*HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://www.yoursite.com/script.php");

        try {
            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("id", "12345"));
            nameValuePairs.add(new BasicNameValuePair("stringdata", "AndDev is Cool!"));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);

        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
        } catch (IOException e) {
            // TODO Auto-generated catch block
        }*/
    }

    /**
     * Stores the registration ID and app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId registration ID
     */
    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    /**
     * metoda ce determina daca trebuie afisat sau nu textview cu textul ca nu sint alarme
     */
    private void emptyTextViewVisibility(){
    	if(alarmList.size()>0)
        	findViewById(R.id.id_empty_list_text_view).setVisibility(View.GONE);
    	else
    		findViewById(R.id.id_empty_list_text_view).setVisibility(View.VISIBLE);
    }
    
    
    @Override
     /**
      * creaza meniu cu optiuni
      */
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.delete_menu, menu);
        return true;
    }
    
   
    @Override
    /**
     * se apeleaza la alegerea unui element din meniul cu optiuni
     * si sterge toate alarmele
     */
    public boolean onOptionsItemSelected(MenuItem item){
    	super.onOptionsItemSelected(item);
    	switch(item.getItemId()){
    		case R.id.menu_delete_all_option: 
    			AlarmDbUtilities.deleteAll(this);
    			alarmList.removeAll(alarmList);
    			emptyTextViewVisibility();
    			ad.notifyDataSetChanged();
    			break;
    	}
    	return true;
    }
    
    
    @Override
    /**
     * se apeleaza la crearea de meniu context
     */
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
    	super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.context_menu, menu);
    }
    
    
    @Override
    /**
     * se apeleaza la alegerea unui element din meniul context
     * si sterge elementul ales
     */
    public boolean onContextItemSelected(MenuItem item){
    	super.onContextItemSelected(item);
    	switch(item.getItemId()){
    		case R.id.delete_option:
    			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
    			Alarm alarm = alarmList.get((int)info.id);
    			AlarmDbUtilities.deleteAlarm(this, alarm);
    			alarmList.remove(alarm);
    			emptyTextViewVisibility();
    			ad.notifyDataSetChanged();
    			break;
    	}
    	return true;
    }
    
   
    @Override
    /**
     * se apeleaza la revenirea din preferinte
     * seteaza alarma sau actualizeaza pe una existenta
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
    	super.onActivityResult(requestCode, resultCode, data);
    	SharedPreferences sp= this.getSharedPreferences(lastId, Context.MODE_PRIVATE);
		String description = sp.getString("description", null);
		String time = sp.getString("time", null);
		String daysOfWeek = sp.getString("days_of_week", null);
		String wakeUpMode = sp.getString("wake_up_mode", null);
		String ringtone = sp.getString("ringtone", null);
		Alarm alarm = alarmList.get(lastIndex);
		Calendar when = Calendar.getInstance();
		when.set(Calendar.SECOND,0);
		if(time != null){
			String timeArgs[] =time.split(":");
			when.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeArgs[0]));
			when.set(Calendar.MINUTE, Integer.parseInt(timeArgs[1]));
		}
    	DateFormat df=DateFormat.getDateTimeInstance();
		Log.d("DEBUG_TAG", "din preferinte a iesit cu :"+ df.format(when.getTime()));
		alarm.setDescription(description);
		alarm.setTime(when.getTimeInMillis());
		alarm.setWakeUpMode(wakeUpMode);
		alarm.setDaysOfWeek(daysOfWeek);
		alarm.setRingtone(ringtone);
		if(alarm.isEnabled() == Alarm.ALARM_ENABLED){
			AlarmSetter  aSetter = new AlarmSetter(this);
			aSetter.setAlarm(alarm);
		}
		AlarmDbUtilities.updateAlarm(this, alarm);
		ad.notifyDataSetChanged();
		emptyTextViewVisibility();
    }
}
