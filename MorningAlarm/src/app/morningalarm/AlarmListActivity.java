package app.morningalarm;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import app.alarmmanager.AlarmSetter;
import app.database.AlarmDbAdapter;
import app.database.AlarmDbUtilities;
import app.utils.Constants;
import app.gcm.GcmRegisterer;

/**
 * activitatea principala a aplicatiei
 * care afiseaza lista cu alarme si optiunile lor
 *
 * @author ALEXANDR
 */
public class AlarmListActivity extends Activity {

    private ArrayList<Alarm> alarmList;
    private String lastId;
    private int lastIndex;
    private AlarmListAdapter ad;
    GcmRegisterer gcmRegisterer;

    private OnItemClickListener listItemClickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> adapter, View view, int arg,
                                long position) {
            int pos = (int) position;
            Intent i;
            if (Build.VERSION.SDK_INT < 11) {
                i = new Intent(AlarmListActivity.this, AlarmSettingsActivity.class);
            } else {
                i = new Intent(AlarmListActivity.this, AlarmFragmentsSettingsActivity.class);
            }
            i.putExtra("id", alarmList.get(pos).getId());
            lastId = alarmList.get(pos).getId();
            lastIndex = pos;
            AlarmListActivity.this.startActivityForResult(i, 0);
        }
    };

    /**
     * metoda atribuie listei din vedere adapterul pentru afisarea alarmelor setate
     * si atribuie listeneruri pentru butoane
     */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        alarmList = AlarmDbUtilities.fetchAllAlarms(this);
        ad = new AlarmListAdapter(this, R.layout.list_item_main, alarmList);
        ListView lv = (ListView) this.findViewById(R.id.listView1);
        lv.setAdapter(ad);
        lv.setOnItemClickListener(listItemClickListener);
        emptyTextViewVisibility();

        gcmRegisterer = new GcmRegisterer(this);

        if (gcmRegisterer.setUpGcm()) {
            Log.e(Constants.TAG, "Eroare gcm");
        }

        this.registerForContextMenu(lv);
    }

    // You need to do the Play Services APK check here too.
    @Override
    protected void onResume() {
        super.onResume();
        if (!gcmRegisterer.checkPlayServices()) {
            Log.e(Constants.TAG, "Eroare gcm on resume");
        }
    }

    /**
     * metoda ce determina daca trebuie afisat sau nu textview cu textul ca nu sint alarme
     */
    private void emptyTextViewVisibility() {
        if (alarmList.size() > 0)
            findViewById(R.id.id_empty_list_text_view).setVisibility(View.GONE);
        else
            findViewById(R.id.id_empty_list_text_view).setVisibility(View.VISIBLE);
    }


    @Override
    /**
     * creaza meniu cu optiuni
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.delete_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    /**
     * se apeleaza la alegerea unui element din meniul cu optiuni
     * si sterge toate alarmele
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.menu_delete_all_option:
                removeAllAlarms();
                break;
            case R.id.menu_new_alarm:
                addNewAlarm();
                break;
            case R.id.menu_asociate_alarms:
                Intent i = new Intent(AlarmListActivity.this, AssociateAlarmsActivity.class);
                AlarmListActivity.this.startActivity(i);
                break;
        }
        return true;
    }

    private void addNewAlarm() {
        Alarm newAlarm = AlarmDbUtilities.fetchNewAlarm(AlarmListActivity.this);
        lastIndex = alarmList.size();
        alarmList.add(newAlarm);
        Intent i;
        if (Build.VERSION.SDK_INT < 11) {
            i = new Intent(AlarmListActivity.this, AlarmSettingsActivity.class);
        } else {
            i = new Intent(AlarmListActivity.this, AlarmFragmentsSettingsActivity.class);
        }
        i.putExtra(AlarmDbAdapter.KEY_ID, newAlarm.getId());
        lastId = newAlarm.getId();
        AlarmListActivity.this.startActivityForResult(i, 0);

    }

    private void removeAllAlarms(){
        AlarmDbUtilities.deleteAll(this);
        alarmList.removeAll(alarmList);
        emptyTextViewVisibility();
        ad.notifyDataSetChanged();
    }

    @Override
    /**
     * se apeleaza la crearea de meniu context
     */
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.context_menu, menu);
    }


    @Override
    /**
     * se apeleaza la alegerea unui element din meniul context
     * si sterge elementul ales
     */
    public boolean onContextItemSelected(MenuItem item) {
        super.onContextItemSelected(item);
        switch (item.getItemId()) {
            case R.id.delete_option:
                AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
                Alarm alarm = alarmList.get((int) info.id);
                AlarmDbUtilities.deleteAlarm(this, alarm);
                alarmList.remove(alarm);
                emptyTextViewVisibility();
                ad.notifyDataSetChanged();
                break;
        }
        return true;
    }


    @Override
    /**
     * se apeleaza la revenirea din preferinte
     * seteaza alarma sau actualizeaza pe una existenta
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        SharedPreferences sp = this.getSharedPreferences(lastId, Context.MODE_PRIVATE);

        Alarm alarm = getAlarmFromSharedPreferences(sp);

        if (alarm.isEnabled() == Alarm.ALARM_ENABLED) {
            AlarmSetter aSetter = new AlarmSetter(this);
            aSetter.setAlarm(alarm);
        }
        AlarmDbUtilities.updateAlarm(this, alarm);
        ad.notifyDataSetChanged();
        emptyTextViewVisibility();
    }

    private Alarm getAlarmFromSharedPreferences(SharedPreferences sp) {

        String description = sp.getString("description", null);
        String time = sp.getString("time", null);
        String daysOfWeek = sp.getString("days_of_week", null);
        String wakeUpMode = sp.getString("wake_up_mode", null);
        String ringtone = sp.getString("ringtone", null);

        Calendar when = Calendar.getInstance();
        when.set(Calendar.SECOND, 0);
        if (time != null) {
            String timeArgs[] = time.split(":");
            when.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeArgs[0]));
            when.set(Calendar.MINUTE, Integer.parseInt(timeArgs[1]));
        }

        Alarm alarm = alarmList.get(lastIndex);


        alarm.setDescription(description);
        alarm.setTime(when.getTimeInMillis());
        alarm.setWakeUpMode(wakeUpMode);
        alarm.setDaysOfWeek(daysOfWeek);
        alarm.setRingtone(ringtone);

        return alarm;
    }

    /**
     * clasa adaptor care adapteaza aplicatia la interfata clasei ListView
     *
     * @author ALEXANDR
     */
    public static class AlarmListAdapter extends ArrayAdapter<Alarm> {

        private ArrayList<Alarm> alarms;

        /**
         * constructor
         *
         * @param context
         * @param textViewResourceId
         * @param objects
         */
        public AlarmListAdapter(Context context, int textViewResourceId,
                                ArrayList<Alarm> objects) {
            super(context, textViewResourceId, objects);
            this.alarms = objects;
        }

        /**
         * reprezinta metoda ce adapteaza vederile cu continutul alarmelor
         * la lista de alarme
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) this.getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.list_item_main, null);
            }

            final Alarm li = alarms.get(position);
            if (li != null) {
                ImageView iv = (ImageView) v.findViewById(R.id.alarm_iv);
                TextView tv_big = (TextView) v.findViewById(R.id.alarm_tv_big);
                TextView tv_small = (TextView) v.findViewById(R.id.alarm_tv_small);
                ToggleButton tb = (ToggleButton) v.findViewById(R.id.alarm_tb);
                tb.setOnClickListener(new OnClickListener() {

                    public void onClick(View arg0) {
                        AlarmSetter aSetter = new AlarmSetter(AlarmListAdapter.this.getContext());
                        if (li.isEnabled() == Alarm.ALARM_ENABLED) {
                            li.setEnabled(Alarm.ALARM_DISABLED);
                            AlarmDbUtilities.updateAlarm(AlarmListAdapter.this.getContext(), li);
                            aSetter.removeAlarm(li.getId());
                        } else {
                            li.setEnabled(Alarm.ALARM_ENABLED);
                            AlarmDbUtilities.updateAlarm(AlarmListAdapter.this.getContext(), li);
                            aSetter.setAlarm(li);
                        }
                    }

                });
                if (iv != null) {
                    if (li.getWakeUpMode().equals("0"))
                        iv.setImageResource(R.drawable.simple_test);
                    if (li.getWakeUpMode().equals("1"))
                        iv.setImageResource(R.drawable.mathtest);
                    if (li.getWakeUpMode().equals("2"))
                        iv.setImageResource(R.drawable.logic_test);
                }
                if (tv_big != null) {
                    Calendar c = Calendar.getInstance();
                    c.setTimeInMillis(li.getTime());
                    DateFormat df = DateFormat.getTimeInstance(DateFormat.SHORT);
                    String bigView = "<b>" + df.format(c.getTime()) + "</b>    ";
                    String arr[] = {"S", "M", "T", "W", "T", "F", "S"};
                    String daysOfWeek = li.getDaysOfWeek();
                    if (daysOfWeek.contains("#ALL#")) {
                        for (int i = 1; i < 8; i++) {
                            bigView += "<font color=\"blue\"<u>" + arr[i - 1] + "</u></font> ";
                        }

                    } else {
                        for (int i = 1; i < 8; i++) {
                            if (daysOfWeek.contains(i + "")) {
                                bigView += "<font color=\"blue\"<u>" + arr[i - 1] + "</u></font> ";
                            } else {
                                bigView += "<font color=\"red\"<u>" + arr[i - 1] + "</u></font> ";
                            }

                        }
                    }
                    tv_big.setText(Html.fromHtml(bigView));
                }

                if (tv_small != null) {
                    tv_small.setText(li.getDescription());
                }
                if (tb != null) {
                    if (li.isEnabled() == Alarm.ALARM_ENABLED)
                        tb.setChecked(true);
                    else
                        tb.setChecked(false);
                }
            }

            return v;
        }

    }
}
