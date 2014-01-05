package app.alarmmanager;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import app.database.AlarmDbAdapter;
import app.database.AlarmDbUtilities;
import app.morningalarm.Alarm;
import app.morningalarm.MorningAlarmWidgetProvider;
import app.morningalarm.R;

/**
 * clasa care face management la cum sint setate alarmele 
 * de catre AlarmManager
 * @author ALEXANDR
 *
 */
public class AlarmSetter {

	private Context mContext;
	private AlarmManager mAlarmManager;
	private static final long FIVE_MINUTES = 300000L;
	/**
	 * constructor
	 * @param context
	 */
	public AlarmSetter(Context context){
		mContext = context;
		mAlarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
	}
	
	
	/**
	 * face refresh la toate alarmele, le seteaza din nou
	 */
	public void refreshAllAlarms(){
		ArrayList <Alarm> alarms = AlarmDbUtilities.fetchAllAlarms(mContext);
		Log.d("DEBUG_TAG","REFRESHING ALARMS "+ alarms.size());
		for(Alarm alarm: alarms){
			setAlarm(alarm);
		}
	}
	
	/**
	 * @param alarm
	 * seteaza alarma alarm
	 */
	public void setAlarm(Alarm alarm){
		getAlarmUpToDate(alarm);
		Intent i = new Intent (mContext, OnAlarmReceiver.class);
		
		i.putExtra(AlarmDbAdapter.KEY_ID, alarm.getId());
		PendingIntent pi = PendingIntent.getBroadcast(mContext,Integer.parseInt(alarm.getId()), i, PendingIntent.FLAG_UPDATE_CURRENT);
		
		RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(),
		          R.layout.home_screen_widget);
		remoteViews.setImageViewResource(R.id.home_screen_iv, R.drawable.clock1);
		
		mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarm.getTime(), FIVE_MINUTES, pi);
		//log
		Calendar c = Calendar.getInstance();
    	c.setTimeInMillis(alarm.getTime());
    	DateFormat df=DateFormat.getTimeInstance(DateFormat.SHORT);
		String time=df.format(c.getTime());
		
		MorningAlarmWidgetProvider.updateWidget(mContext,true);
		
		Log.d("DEBUG_TAG", "alarm setted on "+ time);
	}
	
	/**
	 * @param alarm
	 * verifica daca alarma este trecuta de ora curenta atuni o muta pe ziua viitoare
	 */
	private void getAlarmUpToDate(Alarm alarm){
		Calendar c = Calendar.getInstance();
    	c.setTimeInMillis(alarm.getTime());
    	Calendar temp = Calendar.getInstance();
    	if(!temp.before(c)){
    		Log.d("DEBUG_TAG", "actualizeaza alarma");
    		temp.set(Calendar.HOUR_OF_DAY, c.get(Calendar.HOUR_OF_DAY));
    		temp.set(Calendar.MINUTE, c.get(Calendar.MINUTE));
    		if(!Calendar.getInstance().before(temp)){
    			temp.add(Calendar.DAY_OF_YEAR, 1);
    		}
    		temp.set(Calendar.SECOND, 0);
    		alarm.setTime(temp.getTimeInMillis());
    	}
    	c.setTimeInMillis(alarm.getTime());
    	DateFormat df=DateFormat.getDateTimeInstance();
		String time=df.format(c.getTime());
		Log.d("DEBUG_TAG","alarm updated to "+time );
    	AlarmDbUtilities.updateAlarm(mContext, alarm);
	}
	
	/**
	 * @param alarmId
	 * sterge alarma cu id-ul alarmId din sistemul de operare
	 */
	public void removeAlarm(String alarmId){
		Intent i = new Intent (mContext, OnAlarmReceiver.class);
		PendingIntent pi = PendingIntent.getBroadcast(mContext,Integer.parseInt(alarmId), i, PendingIntent.FLAG_CANCEL_CURRENT);
		mAlarmManager.cancel(pi);
		pi.cancel();
		Log.d("DEBUG_TAG", "alarm setted on "+ alarmId + "canceled");
		if(AlarmDbUtilities.fetchEnabledAlarms(mContext).size() == 0){
			MorningAlarmWidgetProvider.updateWidget(mContext,false);
		}
	}
}
