package app.morningalarm.preferences;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import app.morningalarm.R;
import app.morningalarm.R.xml;

@SuppressLint("NewApi")
public class AlarmSettingsFragment extends PreferenceFragment {

	/**
	 * fragmentul ce afiseaza preferintele pentru versiuni >=3
	 */
	@SuppressLint("NewApi")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String id = (String) this.getArguments().get("id");
		if (id == null) {
			id = "-1";
		}
		this.getPreferenceManager().setSharedPreferencesName(id);
		addPreferencesFromResource(R.xml.alarm_preferences_screen);
	}

}