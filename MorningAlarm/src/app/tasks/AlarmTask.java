package app.tasks;

import java.util.Calendar;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Vibrator;
import android.util.Log;
import android.view.WindowManager;
import app.alarmmanager.AlarmSetter;
import app.morningalarm.Alarm;

/**
 * clasa abstracta care defineste activity care 
 * arata un dialog cu sarcina si in acelasi timp 
 * face play la ton de alarma si activeaza vibratia dispozitivului
 * @author ALEXANDR
 *
 */
public abstract class AlarmTask extends Activity{

	/**
	 * alarma
	 */
	protected static Alarm alarm;
	/**
	 * variabila ce defineste daca alarma este actva
	 */
	protected static boolean active;
	protected Ringtone ringtone;
	protected Vibrator vibrator;
	protected MediaPlayer mMediaPlayer;
	protected boolean finishAlarm;
	protected boolean snooze;
	protected Dialog dialog;
	private PowerManager pm;

	/**
	 * metoda ce se apeleaza cind se creaza activitatea
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setSignals();
		dialog = new Dialog(this);
		solveCondition();
		
		Thread thread = new Thread() {
			/**
			 * threadul ce ruleaza si verifica daca trebuie de inchis sau nu activitatea
			 * si daca sa indeplinit conditia de inchidere a alarmei sau nu
			 */
			public synchronized void run() {
				Log.d("DEBUG_TAG", "thread running");
				Calendar whenToTurnOff = Calendar.getInstance();
				whenToTurnOff.add(Calendar.MINUTE, 2);
				pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
				finishAlarm = false;
				snooze = false;
				
				try {
					wait(300);
				} catch (InterruptedException e) {
				}
			
				
				while (!(finishAlarm == true || snooze == true 
						|| whenToTurnOff.before(Calendar.getInstance()) || pm.isScreenOn() == false)) {
					try {
						wait(200);
						vibrator.vibrate(100);
					} catch (InterruptedException e) {
					}
				}
				Log.d("DEBUG_TAG", "updating alarm");
				dialog.setCancelable(true);
				dialog.dismiss();
				mMediaPlayer.stop();
				vibrator.cancel();
				if (finishAlarm) {
					Log.d("DEBUG_TAG", "Set alarm on next day");
					AlarmSetter aSetter = new AlarmSetter(AlarmTask.this);
					aSetter.setAlarm(alarm);
				}else{
					Log.d("DEBUG_TAG", "Snooze");
				}
				AlarmTask.this.finish();
				AlarmTask.setInActive();
				Log.d("DEBUG_TAG", "thread over");
			}
		};
		thread.start();

	}

	/**
	 * metoda ce seteaza semnalele sonore si de vibrare
	 */
	protected void setSignals() {
		
		try {
			Uri sound = Uri.parse(alarm.getRingtone());
			mMediaPlayer = new MediaPlayer();
			mMediaPlayer.setDataSource(this, sound);
			final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
			if (audioManager.getStreamVolume(AudioManager.STREAM_RING) != 0) {
				mMediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
				mMediaPlayer.setLooping(true);
				mMediaPlayer.prepare();
				mMediaPlayer.start();
			}
		} catch(Exception e) {
		} 
		/*ringtone = RingtoneManager.getRingtone(AlarmTask.this, sound);
		ringtone.play();*/
		vibrator = (Vibrator) AlarmTask.this.getSystemService(Context.VIBRATOR_SERVICE);
	}

	/**
	 * metoda ce seteaza valoarea variabilei finishAlarm cu true daca 
	 * sa rezolvat sarcina sau da snooze
	 */
	protected abstract void solveCondition();

	/**
	 * metoda ce returneaza ultima alarma din clasa
	 * @return
	 */
	public static Alarm getAlarm() {
		return alarm;
	}

	/**
	 * metoda ce seteaza ultima alarma din clasa
	 * @param newAlarm
	 */
	public static void setAlarm(Alarm newAlarm) {
		alarm = newAlarm;
	}
	
	/**
	 * metoda ce seteaza valoarea variabile active cu true
	 * @param newAlarm
	 */
	synchronized public static void setActive(){
		active = true;
	}
	
	/**
	 * metoda ce seteaza valoarea variabile active cu false
	 */
	synchronized public static void setInActive(){
		active = false;
	}
	
	/**
	 * metoda ce returneaza valoarea variabile active
	 * @return
	 */
	public static boolean isActive(){
		return active;
	}
	
	/**
	 * metoda ce returneaza fereastra de dialog cu sarcina
	 * @return
	 */
	public Dialog getDialog(){
		return this.dialog;
	}
	
	
}
