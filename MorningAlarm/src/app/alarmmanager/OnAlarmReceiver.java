package app.alarmmanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import app.database.AlarmDbAdapter;
/**
 * clasa ce se apeleaza cind apare evenimentul 
 * de la alarmmanager
 * @author ALEXANDR
 *
 */
public class OnAlarmReceiver extends BroadcastReceiver{

	/**
	 * este apelat atunci cind un semnal de alarma a fost primit
	 * primeste id-ul alarmei si il transmite mai departe la service-ul AlarmService ca sa faca actiunea necesara
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		String alarmId = intent.getExtras().getString(AlarmDbAdapter.KEY_ID);
		Log.d("DEBUG_TAG", "alarm got in receiver "+ alarmId);
		WakeAlarmIntentService.acquireStaticLock(context);
		
		Intent i = new Intent(context, AlarmService.class);
		i.putExtra(AlarmDbAdapter.KEY_ID, alarmId);
		context.startService(i);
		
	}
	

}
