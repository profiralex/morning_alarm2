package app.database;

import java.util.ArrayList;
import java.util.Calendar;

import android.content.Context;
import android.database.Cursor;
import app.alarmmanager.AlarmSetter;
import app.morningalarm.Alarm;

/**
 * clasa cu utilitati pentru aplicatie
 * @author ALEXANDR
 *
 */
public class AlarmDbUtilities {
	
	/**
	 * returneaza arrayList cu toate alarmele din cursor
	 */
	public static final ArrayList<Alarm> fetchCursor(Cursor c){
		ArrayList<Alarm> arr = new ArrayList<Alarm>();
		if (c.moveToFirst()){
			do{
				String id = c.getString(c.getColumnIndexOrThrow(AlarmDbAdapter.KEY_ID));
				Integer enabled = Integer.parseInt(c.getString(c.getColumnIndexOrThrow(AlarmDbAdapter.KEY_ENABLED)));
				long time = c.getLong(c.getColumnIndexOrThrow(AlarmDbAdapter.KEY_TIME));
				String daysOfWeek = c.getString(c.getColumnIndexOrThrow(AlarmDbAdapter.KEY_DAYS_OF_WEEK));
				String wakeUpMode = c.getString(c.getColumnIndexOrThrow(AlarmDbAdapter.KEY_WAKE_UP_MODE));
				String ringtone = c.getString(c.getColumnIndexOrThrow(AlarmDbAdapter.KEY_RINGTONE));
				String description = c.getString(c.getColumnIndexOrThrow(AlarmDbAdapter.KEY_DESCRIPTION));
				arr.add(new Alarm(id, enabled, description, time, daysOfWeek, wakeUpMode, ringtone));
			}while(c.moveToNext());
		}
		c.close();
		return arr;
	}
	
	/**
	 * returneaza toate alarmele din baza de date
	 */
	public static final ArrayList<Alarm> fetchAllAlarms(Context context){
		ArrayList<Alarm> arr = new ArrayList<Alarm>();
		AlarmDbAdapter mDbHelper = AlarmDbAdapter.getInstance(context);
        mDbHelper.open();
        Cursor c = mDbHelper.fetchAllAlarms();
		arr = fetchCursor(c);
		c.close();
		mDbHelper.close();
		return arr;
	}

	/**
	 * returneaza Alarma cu id-ul alarmId din baza de date
	 */
	public static final Alarm fetchAlarm(Context context, String alarmId){
		Alarm alarm = null;
		AlarmDbAdapter mDbHelper = AlarmDbAdapter.getInstance(context);
        mDbHelper.open();
        Cursor c = mDbHelper.fetchAlarm(alarmId);
        ArrayList<Alarm> arr = fetchCursor(c);
		c.close();
		mDbHelper.close();
		if (arr.size() > 0){
			alarm = arr.get(0);
		}
		return alarm;
	}

	/**
	 * returneaza din baza de date alarma nou creata
	 */
	public static final Alarm fetchNewAlarm(Context context){
		Alarm alarm = null;
		AlarmDbAdapter mDbHelper = AlarmDbAdapter.getInstance(context);
        mDbHelper.open();
        mDbHelper.createAlarm();
        Cursor c = mDbHelper.fetchNewAlarm();
        alarm = fetchCursor(c).get(0);
		c.close();
		mDbHelper.close();
		return alarm;
	}
	
	/**
	 * sterge din baza de date alarma
	 */
	public static final void deleteAlarm(Context context, Alarm alarm){
		AlarmDbAdapter mDbHelper = AlarmDbAdapter.getInstance(context);
        mDbHelper.open();
        mDbHelper.deleteAlarm(alarm);
        mDbHelper.close();
        AlarmSetter aSetter = new AlarmSetter(context);
        aSetter.removeAlarm(alarm.getId());
	}
	/**
	 * sterge toate alarmele din baza de date
	 */
	public static final void deleteAll(Context context){
		ArrayList<Alarm> alarms = fetchAllAlarms(context);
		AlarmSetter aSetter = new AlarmSetter(context);
		for(Alarm a: alarms ){
	        aSetter.removeAlarm(a.getId());
		}
		AlarmDbAdapter mDbHelper = AlarmDbAdapter.getInstance(context);
        mDbHelper.open();
        mDbHelper.deletAll();
        mDbHelper.close();
        
	}
	
	/**
	 * face update la alarma in baza de date
	 */
	public static final void updateAlarm(Context context, Alarm alarm){
		
		AlarmDbAdapter mDbHelper = AlarmDbAdapter.getInstance(context);
        mDbHelper.open();
        mDbHelper.updateAlarm(alarm);
        mDbHelper.close();
	}
	
	/**
	 * returneaza arrayList cu toate alarmele care sunt enabled
	 * @param context
	 * @return
	 */
	public static final ArrayList<Alarm> fetchEnabledAlarms(Context context){
		ArrayList<Alarm> arr = new ArrayList<Alarm>();
		AlarmDbAdapter mDbHelper = AlarmDbAdapter.getInstance(context);
        mDbHelper.open();
        Cursor c = mDbHelper.fetchEnabledAlarms();
		arr = fetchCursor(c);
		c.close();
		mDbHelper.close();
		return arr;
	}
	
}
