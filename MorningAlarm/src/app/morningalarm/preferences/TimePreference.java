package app.morningalarm.preferences;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TimePicker;

import java.text.DateFormat;
import java.util.Calendar;

import app.database.AlarmDbAdapter;
import app.database.AlarmDbUtilities;
import app.utils.Alarm;
import app.utils.Constants;

/**
 * clasa ce arata fereastra de dialog
 * cu timepicker pentru selectarea orei si minutelor 
 * @author ALEXANDR
 *
 */
public class TimePreference extends DialogPreference {

	TimePicker tp;
	int xxx;
    Context mContext;
	/**
	 * initializeaza preferinta
	 */
	public void initialize(){
        Log.d(Constants.TAG,"1");
        //
		this.setPersistent(true);
        //setUpTimePicker();
	}
	
	/**
	 * constructor
	 * @param context contextul aplicatiei
	 * @param attrs atribute
	 */
	public TimePreference(Context context, AttributeSet attrs) {
		super(context, attrs);
        Log.d(Constants.TAG,"2");
        mContext = context;
		initialize();
	}
	
	/**
	 * constructor
	 * @param context context
	 * @param attrs atribute
	 * @param def default
	 */
	public TimePreference(Context context, AttributeSet attrs, int def) {
		super(context, attrs, def);
        Log.d(Constants.TAG,"3");
        mContext = context;
		initialize();
	}
	
	/**
	 * creaza fereastra de dialog cu timepicker
	 */
	@Override
	protected View onCreateDialogView() {
        Log.d(Constants.TAG,"4");
        setUpTimePicker();
	    return this.tp;
	}

    private void setUpTimePicker(){
        Log.d(Constants.TAG,"5");
        this.tp = new TimePicker(getContext());
        this.tp.setIs24HourView(true);
        Calendar c = Calendar.getInstance();
        String alarmId = this.getPreferenceManager().getSharedPreferencesName();
        Alarm alarm = AlarmDbUtilities.fetchAlarm(mContext, alarmId);
        if(!alarm.getDescription().equals(AlarmDbAdapter.DATABASE_NEW_RECORD_CODE)){
            c.setTimeInMillis(alarm.getTime());
        }
        DateFormat df=DateFormat.getTimeInstance(DateFormat.SHORT);
        String time=df.format(c.getTime());
        final String storedValue = getPersistedString(time);
        final String[] split = storedValue.split(":");
        this.tp.setCurrentHour(Integer.parseInt(split[0]));
        final String[] split2 =split[1].split(" ");
        this.tp.setCurrentMinute(Integer.parseInt(split2[0]));
    }

	/**
	 * salveaza starea la inchiderea ferestrei de dialog
	 */
	@Override
	public void onDialogClosed(boolean positiveResult) {
	    super.onDialogClosed(positiveResult);
        Log.d(Constants.TAG,"6");
	    if (positiveResult) {
            Log.d(Constants.TAG,"7");
	        final String result = this.tp.getCurrentHour() + ":" + this.tp.getCurrentMinute();
	        persistString(result);
	    }
	}
	
}