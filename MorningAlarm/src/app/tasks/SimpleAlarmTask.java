package app.tasks;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import app.morningalarm.R;

/**
 * clasa care defineste alarma simpla
 * @author ALEXANDR
 *
 */
public class SimpleAlarmTask extends AlarmTask {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
	}
	/**
	 * metoda ce defineste cind a fost indeplinita conditia de oprire a alarmei
	 */
	protected void solveCondition() {
		dialog.setContentView(R.layout.simple_test_layout);
		dialog.setTitle("Cancel Alarm or Snooze");
		dialog.setCancelable(false);
		dialog.show();
		
		
		Button snoozeButton = (Button) dialog.findViewById(R.id.simple_test_snooze_button);
		Button cancelButton = (Button) dialog.findViewById(R.id.simple_test_cancel_button);
		snoozeButton.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				snooze = true;
			}

		});
		cancelButton.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				finishAlarm = true;
			}

		});
		Log.d("DEBUG_TAG", "exit solvecondition method");
	}

}
