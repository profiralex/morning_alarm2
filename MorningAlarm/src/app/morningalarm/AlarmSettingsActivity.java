package app.morningalarm;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * activitatea ce creaza ecranul cu optiubi 
 * pentru versiuni mai mici sau egale cu 3
 * @author ALEXANDR
 *
 */
public class AlarmSettingsActivity extends PreferenceActivity {

	/**
	 * clasa ce creaza ecranul cu preferinte pentru versiuni <=3
	*/
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String id = this.getIntent().getExtras().getString("id");
		if(id==null){
			id="-1";
		}
		getPreferenceManager().setSharedPreferencesName(id);
		addPreferencesFromResource(R.xml.alarm_preferences_screen);
	}
}