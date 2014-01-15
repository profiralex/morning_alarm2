package app.gcm;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import app.alarmmanager.AlarmSetter;
import app.database.AlarmDbUtilities;
import app.morningalarm.AlarmFragmentsSettingsActivity;
import app.morningalarm.R;
import app.network.ServerRequestComposer;
import app.utils.Alarm;
import app.utils.Constants;

/**
 * Created by alexandr on 1/12/14.
 */
public class GcmActivity extends Activity{

    private Alarm alarm;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Log.d(Constants.TAG, "activitate pornita");
        final Intent intent = getIntent();

        String type = intent.getExtras().getString(Constants.EXTRA_MESSAGE_TYPE);


        if (type.equals(Constants.TYPE_REQUEST)) {

            final String senderEmail = intent.getExtras().getString("sender_email");
            String groupMessage = intent.getExtras().getString("message");
            final String groupId = intent.getExtras().getString("group_id");

            new AlertDialog.Builder(this)
                    .setTitle("You recived an request to register for alarm from " + senderEmail)
                    .setMessage(groupMessage)
                    .setPositiveButton("Accept", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            sendResponseInBackground(senderEmail, groupId, "ok");
                            getAlarm(intent);
                            dialog.cancel();
                        }
                    })
                    .setNegativeButton("Refuse", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            sendResponseInBackground(senderEmail, groupId, "no");
                            dialog.cancel();
                            GcmActivity.this.finish();
                        }
                    })
                    .setIcon(R.drawable.clock1)
                    .show();
        }
    }

    private void getAlarm(Intent intent){
        alarm = AlarmDbUtilities.fetchNewAlarm(GcmActivity.this);
        String time = intent.getStringExtra("alarm_time");
        String description = intent.getStringExtra("message");
        alarm.setTime(Long.parseLong(time));
        alarm.setDescription(description);
        alarm.setEnabled(Alarm.ALARM_ENABLED);

        AlarmDbUtilities.updateGroupAlarm(this, alarm);

        Intent i = new Intent(GcmActivity.this, AlarmFragmentsSettingsActivity.class);
        i.putExtra("id", alarm.getId());
        startActivityForResult(i, 0);
    }

    private void sendResponseInBackground(final String senderEmail, final String groupId,
                                          final String response){

        final GcmRegisterer gcm = new GcmRegisterer(this);
        gcm.setUpGcm();
        Log.d(Constants.TAG,gcm.getRegistrationId(this)+"asdasdasdads\n");

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                try {
                    ServerRequestComposer.sendResponseToPerson(senderEmail,
                            groupId, response, gcm.getRegistrationId(GcmActivity.this));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return "";
            }

            @Override
            protected void onPostExecute(String v) {
                //TODO
            }
        }.execute(null, null, null);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            SharedPreferences sp = this.getSharedPreferences(alarm.getId(), Context.MODE_PRIVATE);

            Alarm alarm = getAlarmFromSharedPreferences(sp);

            if (alarm.isEnabled() == Alarm.ALARM_ENABLED) {
                AlarmSetter aSetter = new AlarmSetter(this);
                aSetter.setAlarm(alarm);
            }
            AlarmDbUtilities.updateAlarm(this, alarm);

        GcmActivity.this.finish();
    }

    private Alarm getAlarmFromSharedPreferences(SharedPreferences sp) {

        String daysOfWeek = sp.getString("days_of_week", null);
        String wakeUpMode = sp.getString("wake_up_mode", null);
        String ringtone = sp.getString("ringtone", null);

        alarm.setWakeUpMode(wakeUpMode);
        alarm.setDaysOfWeek(daysOfWeek);
        alarm.setRingtone(ringtone);

        return alarm;
    }
}
