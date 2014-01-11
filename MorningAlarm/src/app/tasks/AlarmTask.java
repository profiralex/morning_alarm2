package app.tasks;

import java.util.Calendar;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
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
import app.utils.Alarm;
import app.utils.Constants;

/**
 * clasa abstracta care defineste activity care
 * arata un dialog cu sarcina si in acelasi timp
 * face play la ton de alarma si activeaza vibratia dispozitivului
 *
 * @author ALEXANDR
 */
public abstract class AlarmTask extends Activity implements SensorEventListener {

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
    private SensorManager sensorManager;
    private Float startSenzorZ;

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
        beginAlarming();
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_GRAVITY) {
            getAccelerometer(event);
        }

    }

    private void getAccelerometer(SensorEvent event) {
        float[] values = event.values;
        // Movement
        float x = values[0];
        float y = values[1];
        float z = values[2];

        if(startSenzorZ == null){
            startSenzorZ = z;
        }

        if(startSenzorZ < 0 && z < 0){
            return;
        }

        if(startSenzorZ < 0 && z > 0){
            startSenzorZ = z;
        }

        if(startSenzorZ > 0 && z < -9){
            snooze = true;
        }

        Log.d(Constants.TAG, "x=" + x + " y=" + y + " z=" + z);

    }

    private void beginAlarming() {
        new Thread() {
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
                } else {
                    Log.d("DEBUG_TAG", "Snooze");
                }
                sensorManager.unregisterListener(AlarmTask.this);
                AlarmTask.setInActive();
                Log.d("DEBUG_TAG", "thread over");
                AlarmTask.this.finish();
            }
        }.start();
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
        } catch (Exception e) {
        }
        vibrator = (Vibrator) AlarmTask.this.getSystemService(Context.VIBRATOR_SERVICE);
    }

    /**
     * metoda ce seteaza valoarea variabilei finishAlarm cu true daca
     * sa rezolvat sarcina sau da snooze
     */
    protected abstract void solveCondition();

    /**
     * metoda ce returneaza ultima alarma din clasa
     *
     * @return
     */
    public static Alarm getAlarm() {
        return alarm;
    }

    /**
     * metoda ce seteaza ultima alarma din clasa
     *
     * @param newAlarm
     */
    public static void setAlarm(Alarm newAlarm) {
        alarm = newAlarm;
    }

    /**
     * metoda ce seteaza valoarea variabile active cu true
     */
    synchronized public static void setActive() {
        active = true;
    }

    /**
     * metoda ce seteaza valoarea variabile active cu false
     */
    synchronized public static void setInActive() {
        active = false;
    }

    /**
     * metoda ce returneaza valoarea variabile active
     *
     * @return
     */
    public static boolean isActive() {
        return active;
    }

    /**
     * metoda ce returneaza fereastra de dialog cu sarcina
     *
     * @return
     */
    public Dialog getDialog() {
        return this.dialog;
    }


}
