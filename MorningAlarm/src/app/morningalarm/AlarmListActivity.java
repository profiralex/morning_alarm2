package app.morningalarm;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
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
        if(GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS){
        	Toast toast = Toast.makeText(this, "merge", Toast.LENGTH_LONG);
            toast.show();
        }else{
        	Toast toast = Toast.makeText(this, "nu merge", Toast.LENGTH_LONG);
            toast.show();
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
