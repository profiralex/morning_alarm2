package app.alarmmanager;

import android.content.Intent;
import android.util.Log;
import app.database.AlarmDbUtilities;

/**
 * Serviciul ce apeleaza cind se 
 * reseteaza telefonul.
 * @author ALEXANDR
 *
 */
public class AlarmRefreshingService extends WakeAlarmIntentService{
	
	/**
	 * constructor
	 */
	public AlarmRefreshingService(){
		super("AlarmService");
	}
	/**
	 * seteaza din nou toate alarmele.
	 */
	@Override
	void doAlarmWork(Intent intent){
		Log.d("DEBUG_TAG", "alarm got in refreshing service "+ AlarmDbUtilities.fetchAllAlarms(this.getBaseContext()).size());
		AlarmSetter aSetter = new AlarmSetter(this.getBaseContext());
		aSetter.refreshAllAlarms();
	}

}
