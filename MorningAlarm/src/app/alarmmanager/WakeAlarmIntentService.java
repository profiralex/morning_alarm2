package app.alarmmanager;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

/**
 * clasa abstracta care defineste serviciu ce are face lock la 
 * contextul aplicatie
 * @author ALEXANDR
 *
 */
public abstract class WakeAlarmIntentService extends IntentService{
	/**
	 * metoda abstracta in care se va specifica ce face serviciul
	 * @param i
	 */
	abstract void doAlarmWork(Intent i);
	
	public static final String LOCK_NAME_STATIC = "app.android.morningalarm.Static";
	public static PowerManager.WakeLock lockStatic = null;
	
	/**
	 * @param context
	 * pune lacat la context
	 */
	public static void acquireStaticLock(Context context){
		getLock(context).acquire();
	}
	
	/**
	 * @param context
	 * primeste lacat ca aplicatia sa fie in regim wake
	 */
	synchronized private static PowerManager.WakeLock getLock(Context context){
		if(lockStatic == null){
			PowerManager mgr = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
			lockStatic = mgr.newWakeLock(PowerManager.FULL_WAKE_LOCK|PowerManager.ACQUIRE_CAUSES_WAKEUP, LOCK_NAME_STATIC);
			lockStatic.setReferenceCounted(true);
		}
		return lockStatic;
	}
	
	/**
	 * constructor
	 * @param name
	 */
	public WakeAlarmIntentService(String name){
		super(name);
	}
	
	
	/**
	 * executa metoda doAlarmWork si elibereaza lacatul
	 */
	@Override
	final protected void onHandleIntent(Intent intent){
		try{
			doAlarmWork(intent);
		}finally{
			getLock(this).release();
		}
	}
}
