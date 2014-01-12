package app.database;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;

import app.alarmmanager.AlarmSetter;
import app.utils.Alarm;
import app.utils.Group;
import app.utils.Person;

/**
 * clasa cu utilitati pentru aplicatie
 * @author ALEXANDR
 *
 */
public class AlarmDbUtilities {
	
	/**
	 * returneaza arrayList cu toate alarmele din cursor
	 */
	public static final ArrayList<Alarm> fetchAlarmCursor(Cursor c){
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

    public static final ArrayList<Group> fetchGroupCursor(Cursor c){
        ArrayList<Group> arr = new ArrayList<Group>();
        if (c.moveToFirst()){
            do{
                int id = c.getInt(c.getColumnIndexOrThrow(AlarmDbAdapter.KEY_ID));
                String name = c.getString(c.getColumnIndexOrThrow(AlarmDbAdapter.KEY_GROUP_NAME));
                String message = c.getString(c.getColumnIndexOrThrow(AlarmDbAdapter.KEY_GROUP_INVITATION_MESSAGE));
                String alarmId = c.getString(c.getColumnIndexOrThrow(AlarmDbAdapter.KEY_GROUP_ALARM_ID));
                arr.add(new Group(id, name, message, alarmId));
            }while(c.moveToNext());
        }
        c.close();
        return arr;
    }

    public static final ArrayList<Person> fetchPersonCursor(Cursor c){
        ArrayList<Person> arr = new ArrayList();
        if (c.moveToFirst()){
            do{
                Integer groupId = Integer.parseInt(c.getString(c.getColumnIndexOrThrow(AlarmDbAdapter.KEY_PERSON_GROUP_ID)));
                String email = c.getString(c.getColumnIndexOrThrow(AlarmDbAdapter.KEY_PERSON_EMAIL));
                int intAccepted = c.getInt(c.getColumnIndexOrThrow(AlarmDbAdapter.KEY_PERSON_ACCEPTED));
                boolean accepted = false;
                if(intAccepted == 1){
                    accepted = true;
                }
                arr.add(new Person(groupId, email,accepted));
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
		arr = fetchAlarmCursor(c);
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
        ArrayList<Alarm> arr = fetchAlarmCursor(c);
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
        alarm = fetchAlarmCursor(c).get(0);
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
        mDbHelper.deletAllAlarms();
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
		arr = fetchAlarmCursor(c);
		c.close();
		mDbHelper.close();
		return arr;
	}

    public static final ArrayList<Group> fetchAllGroups(Context context){
        ArrayList<Group> arr = new ArrayList<Group>();
        AlarmDbAdapter mDbHelper = AlarmDbAdapter.getInstance(context);
        mDbHelper.open();
        Cursor c = mDbHelper.fetchAllGroups();
        arr = fetchGroupCursor(c);
        c.close();
        mDbHelper.close();
        return arr;
    }

    public static final Group fetchGroup(Context context, int groupId){
        ArrayList<Group> arr = new ArrayList<Group>();
        AlarmDbAdapter mDbHelper = AlarmDbAdapter.getInstance(context);
        mDbHelper.open();
        Cursor c = mDbHelper.fetchGroup(groupId);
        arr = fetchGroupCursor(c);
        c.close();
        mDbHelper.close();
        return arr.get(0);
    }

    public static final Person fetchPerson(Context context, String email){
        ArrayList<Person> arr = new ArrayList();
        AlarmDbAdapter mDbHelper = AlarmDbAdapter.getInstance(context);
        mDbHelper.open();
        Cursor c = mDbHelper.fetchPerson(email);
        arr = fetchPersonCursor(c);
        c.close();
        mDbHelper.close();
        return arr.get(0);
    }

    public static final Person fetchPerson(Context context, String email, String groupId){
        ArrayList<Person> arr = new ArrayList();
        AlarmDbAdapter mDbHelper = AlarmDbAdapter.getInstance(context);
        mDbHelper.open();
        Cursor c = mDbHelper.fetchPerson(email, groupId);
        arr = fetchPersonCursor(c);
        c.close();
        mDbHelper.close();
        return arr.get(0);
    }

    public static final ArrayList<Person> fetchAllPersonsFromGroup(Context context,int groupId){
        ArrayList<Person> arr = new ArrayList<Person>();
        AlarmDbAdapter mDbHelper = AlarmDbAdapter.getInstance(context);
        mDbHelper.open();
        Cursor c = mDbHelper.fetchAllPersonsFromGroup(groupId);
        arr = fetchPersonCursor(c);
        c.close();
        mDbHelper.close();
        return arr;
    }

    public static final Group createGroup(Context context, String name, String message, String alarmId){
        ArrayList<Group> arr = new ArrayList<Group>();
        AlarmDbAdapter mDbHelper = AlarmDbAdapter.getInstance(context);
        mDbHelper.open();
        mDbHelper.createGroup(name, message, alarmId);
        Cursor c = mDbHelper.fetchGroup(name);
        arr = fetchGroupCursor(c);
        c.close();
        mDbHelper.close();
        return arr.get(0);
    }

    public static final Person createPerson(Context context, String email, int groupId){
        ArrayList<Person> arr = new ArrayList();
        AlarmDbAdapter mDbHelper = AlarmDbAdapter.getInstance(context);
        mDbHelper.open();
        mDbHelper.createPerson(email, groupId);
        Cursor c = mDbHelper.fetchPerson(email, groupId);
        arr = fetchPersonCursor(c);
        c.close();
        mDbHelper.close();
        return arr.get(0);
    }

    public static final void removePerson(Context context, String email, int groupId){
        AlarmDbAdapter mDbHelper = AlarmDbAdapter.getInstance(context);
        mDbHelper.open();
        mDbHelper.deletePerson(email, groupId);
        mDbHelper.close();
    }

    public static final void removeAllPersonsFromGroup(Context context, int groupId){
        AlarmDbAdapter mDbHelper = AlarmDbAdapter.getInstance(context);
        mDbHelper.open();
        mDbHelper.deleteAllPersonsFromGroup(groupId);
        mDbHelper.close();
    }

    public static final void removeGroup(Context context, int id){
        AlarmDbAdapter mDbHelper = AlarmDbAdapter.getInstance(context);
        mDbHelper.open();
        mDbHelper.deleteAllPersonsFromGroup(id);
        mDbHelper.deleteGroup(id);
        mDbHelper.close();
    }

    public static final void removeAllGroups(Context context){
        AlarmDbAdapter mDbHelper = AlarmDbAdapter.getInstance(context);
        mDbHelper.open();
        mDbHelper.deleteAllPersons();
        mDbHelper.deleteAllGroups();
        mDbHelper.close();
    }

    public static final void updatePerson(Context context, Person person){
        AlarmDbAdapter mDbHelper = AlarmDbAdapter.getInstance(context);
        mDbHelper.open();
        mDbHelper.updatePerson(person);
        mDbHelper.close();
    }
	
}
