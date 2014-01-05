package app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import app.morningalarm.Alarm;

/**
 * clasa care este adaptor intre interfata bazei de date 
 * si restul aplicatiei
 * @author ALEXANDR
 *
 */
public class AlarmDbAdapter {

	public static final String DATABASE_NAME = "data";
	public static final String DATABASE_TABLE = "alarms";
	public static final int 	DATABASE_VERSION = 1;
	public static final String DATABASE_NEW_RECORD_CODE = "-1123581321345589"; //primele 11 numere Fibonacci
	
	public static final String KEY_ID = "id";
	public static final String KEY_ENABLED = "enabled";
	public static final String KEY_DESCRIPTION = "description";
	public static final String KEY_TIME = "time";
	public static final String KEY_DAYS_OF_WEEK = "days_of_week";
	public static final String KEY_WAKE_UP_MODE = "wake_up_mode";
	public static final String KEY_RINGTONE = "ringtone";
	
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	
	private static final String DATABASE_CREATE = 
			"create table " + DATABASE_TABLE + " ("
			+ KEY_ID + " integer primary key autoincrement, "
			+ KEY_ENABLED + " integer not null, "
			+ KEY_DESCRIPTION + " text not null, "
			+ KEY_TIME + " integer not null, "
			+ KEY_DAYS_OF_WEEK + " text not null, "
			+ KEY_WAKE_UP_MODE + " text not null, "
			+ KEY_RINGTONE + " text not null );";
			
	private static Context mCtx;
	private static AlarmDbAdapter mAlarmDbAdapter;
	
	/**
	 * @param context
	 * metoda singleton
	 */
	public static AlarmDbAdapter getInstance(Context context){
		if (mAlarmDbAdapter == null || !context.equals(mCtx)){
			mAlarmDbAdapter = new AlarmDbAdapter(context);
		}
		return mAlarmDbAdapter;
	}
	
	
	
	/**
	 * constructor
	 * @param ctx
	 */
	private AlarmDbAdapter(Context ctx){
		mCtx = ctx;
	}
	
	/**
	 * deschide DataBaseHelper si primeste acces la baza de date
	 * @throws SQLException
	 */
	public AlarmDbAdapter open() throws SQLException {
		mDbHelper = new DatabaseHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}
	
	/**
	 * inchide database helper
	 */
	public void close(){
		mDbHelper.close();
	}
	
	/**
	 * introduce alarma in baza de date
	 */
	public long createAlarm(){
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_DESCRIPTION, DATABASE_NEW_RECORD_CODE);
		initialValues.put(KEY_ENABLED, Alarm.ALARM_DISABLED);
		initialValues.put(KEY_DAYS_OF_WEEK, "#ALL#");
		initialValues.put(KEY_TIME, 0);
		initialValues.put(KEY_WAKE_UP_MODE, "0");
		initialValues.put(KEY_RINGTONE, "");
		return mDb.insert(DATABASE_TABLE, null, initialValues);
	}
	
	/**
	 * sterge alarma din baza de date
	 */
	public long deleteAlarm(Alarm alarm){
		return mDb.delete(DATABASE_TABLE, KEY_ID + "=" + alarm.getId() ,null);
	}
	
	/**
	 * sterge toate alarmele din baza de date
	 */
	public long deletAll(){
		return mDb.delete(DATABASE_TABLE, null, null);
	}
	
	/**
	 * returneaza cursor ce contine toate alarmele
	 */
	public Cursor fetchAllAlarms(){
		return mDb.query(DATABASE_TABLE, new String [] {KEY_ID,  KEY_ENABLED, KEY_DESCRIPTION,
				KEY_TIME, KEY_DAYS_OF_WEEK, KEY_WAKE_UP_MODE,KEY_RINGTONE}, null, null, null,null,KEY_TIME);
	}
	
	/**
	 * returneaza cursor ce contine alarma cu id rowId
	 */
	public Cursor fetchAlarm(String rowId) throws SQLException{
		Cursor mCursor = mDb.query(true, DATABASE_TABLE,new String [] {KEY_ID,  KEY_ENABLED, KEY_DESCRIPTION,
				KEY_TIME, KEY_DAYS_OF_WEEK, KEY_WAKE_UP_MODE,KEY_RINGTONE} , KEY_ID + "=" + rowId , null, null, null, null,null);
		if(mCursor != null){
			mCursor.moveToFirst();
		}
		return mCursor;
	}
	
	/**
	 * returneaza alarma cu descrierea de alarma noua
	 */
	public Cursor fetchNewAlarm() throws SQLException{
		Cursor mCursor = mDb.query(DATABASE_TABLE,new String [] {KEY_ID,  KEY_ENABLED, KEY_DESCRIPTION,
				KEY_TIME, KEY_DAYS_OF_WEEK, KEY_WAKE_UP_MODE,
				KEY_RINGTONE} , KEY_DESCRIPTION +"=?", new String[]{DATABASE_NEW_RECORD_CODE}, null, null, null);
		if(mCursor != null){
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	/**
	 * returneaza alarma cu descrierea de alarma noua
	 */
	public Cursor fetchEnabledAlarms() throws SQLException{
		Cursor mCursor = mDb.query(DATABASE_TABLE,new String [] {KEY_ID,  KEY_ENABLED, KEY_DESCRIPTION,
				KEY_TIME, KEY_DAYS_OF_WEEK, KEY_WAKE_UP_MODE,
				KEY_RINGTONE} , KEY_ENABLED +"=?", new String[]{Alarm.ALARM_ENABLED + ""}, null, null, null);
		if(mCursor != null){
			mCursor.moveToFirst();
		}
		return mCursor;
	}
	
	/**
	 * face update la alarma
	 */
	public long updateAlarm(Alarm alarm){
		ContentValues args = new ContentValues();
		args.put(KEY_ID, alarm.getId());
		args.put(KEY_DESCRIPTION, alarm.getDescription());
		args.put(KEY_ENABLED, alarm.isEnabled());
		args.put(KEY_TIME, alarm.getTime());
		args.put(KEY_DAYS_OF_WEEK, alarm.getDaysOfWeek());
		args.put(KEY_WAKE_UP_MODE, alarm.getWakeUpMode());
		args.put(KEY_RINGTONE, alarm.getRingtone());
		return mDb.update(DATABASE_TABLE, args, KEY_ID + " = " + alarm.getId(), null);
	}
	
	
	/**
	 * clasa interna care va fi helperul pentru baza de date
	 * @author ALEXANDR
	 *
	 */
	private static class DatabaseHelper extends SQLiteOpenHelper{
		
		/**
		 * constructor
		 * @param context
		 */
		DatabaseHelper(Context context){
			super(context,DATABASE_NAME,null,DATABASE_VERSION);
		}
		
		/**
		 * metoda apelata la create
		 */
		@Override
		public void onCreate(SQLiteDatabase db){
			db.execSQL(DATABASE_CREATE);
		}
		
		/**
		 * pentru dezvoltare ulterioara
		 */
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
			//pentru dezvoltare ulterioara
		}
		
		
	}

}
