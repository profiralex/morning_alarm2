package app.morningalarm;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;
import app.alarmmanager.AlarmSetter;
import app.database.AlarmDbUtilities;

/**
 * clasa adaptor care adapteaza aplicatia la interfata clasei ListView
 * @author ALEXANDR
 *
 */
public class AlarmListAdapter extends ArrayAdapter<Alarm> {

	private ArrayList<Alarm> alarms;

	/**
	 * constructor
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
                LayoutInflater vi = (LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.list_item_main, null);
            }
            
            final Alarm li = alarms.get(position);
            if (li != null) {
                    ImageView iv= (ImageView) v.findViewById(R.id.alarm_iv);
                    TextView tv_big = (TextView) v.findViewById(R.id.alarm_tv_big);
                    TextView tv_small = (TextView) v.findViewById(R.id.alarm_tv_small);
                    ToggleButton tb=(ToggleButton) v.findViewById(R.id.alarm_tb);
                    tb.setOnClickListener(new OnClickListener(){

						public void onClick(View arg0) {
							AlarmSetter aSetter = new AlarmSetter(AlarmListAdapter.this.getContext());
							if(li.isEnabled() == Alarm.ALARM_ENABLED){
								li.setEnabled(Alarm.ALARM_DISABLED);
								AlarmDbUtilities.updateAlarm(AlarmListAdapter.this.getContext(), li);
								aSetter.removeAlarm(li.getId());
							}
							else{
								li.setEnabled(Alarm.ALARM_ENABLED);
								AlarmDbUtilities.updateAlarm(AlarmListAdapter.this.getContext(), li);
								aSetter.setAlarm(li);
							}
						}
                    	
                    });
                    if (iv != null) {
                    	if(li.getWakeUpMode().equals("0"))
                    		iv.setImageResource(R.drawable.simple_test);
                    	if(li.getWakeUpMode().equals("1"))
                    		iv.setImageResource(R.drawable.mathtest);
                    	if(li.getWakeUpMode().equals("2"))
                    		iv.setImageResource(R.drawable.logic_test);
                    }
                    if(tv_big != null){
                    	Calendar c = Calendar.getInstance();
                    	c.setTimeInMillis(li.getTime());
                    	DateFormat df=DateFormat.getTimeInstance(DateFormat.SHORT);
                		String bigView="<b>" + df.format(c.getTime())+ "</b>    ";
                		String arr[] = {"S","M","T","W","T","F","S"};
                		String daysOfWeek = li.getDaysOfWeek();
                		if(daysOfWeek.contains("#ALL#")){
                			for(int i=1;i<8;i++){
                				bigView += "<font color=\"blue\"<u>"+arr[i-1]+"</u></font> ";
                			}
                			
                		}
                		else{
                			for(int i=1;i<8;i++){
                				if(daysOfWeek.contains(i+"")){
                					bigView += "<font color=\"blue\"<u>"+arr[i-1]+"</u></font> ";
                				}else{
                					bigView += "<font color=\"red\"<u>"+arr[i-1]+"</u></font> ";
                				}
                					
                			}
                		}
                        tv_big.setText(Html.fromHtml(bigView));
                    }
                    
                    if(tv_small != null){
                        tv_small.setText(li.getDescription());
                    }
                    if(tb!=null){
                    	if(li.isEnabled() == Alarm.ALARM_ENABLED)
                    		tb.setChecked(true);
                    	else
                    		tb.setChecked(false);
                    }
            }
            
            return v;
    }

}
