package app.morningalarm;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import app.morningalarm.preferences.AlarmSettingsFragment;

/**
 * preferinte pentru versiuni >=3
 */
public class AlarmFragmentsSettingsActivity extends FragmentActivity{
	@SuppressLint("NewApi")
	@Override
	/**
	 * metode oncreate pentru startul activitatii care va afisa preferintele
	 */
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		AlarmSettingsFragment details = new AlarmSettingsFragment();
        details.setArguments(getIntent().getExtras());
		getFragmentManager().beginTransaction().replace(android.R.id.content,
                details).commit();
	}

}
