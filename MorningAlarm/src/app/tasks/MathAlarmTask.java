package app.tasks;

import java.util.Random;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import app.morningalarm.R;

/**
 * clasa care defineste alarma cu testul matematic
 * @author ALEXANDR
 *
 */
public class MathAlarmTask extends AlarmTask {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
		
	}
	/**
	 * metoda ce seteaza valoarea variabilei finishAlarm cu true daca 
	 * sa rezolvat sarcina sau da snooze
	 */
	protected void solveCondition() {
		dialog.setContentView(R.layout.math_test_layout);
		dialog.setTitle("Math Test");
		dialog.setCancelable(false);
		dialog.show();
		
		TextView textView = (TextView) dialog.findViewById(R.id.math_test_task);
		final EditText editText = (EditText) dialog.findViewById(R.id.math_test_answer);
		Button solveButton = (Button) dialog.findViewById(R.id.math_test_button);
		Random rand = new Random();
		int	nums[] = new int[5];
		for (int i = 0; i < 5; i++) {
			nums[i] = rand.nextInt(5) + 1;
		}
		textView.setText(nums[0] + " + " + nums[1] + " * " + nums[2] + " + "
				+ nums[3] + " * " + nums[4]);
		final int result = nums[0] + nums[1] * nums[2] + nums[3] * nums[4];
		solveButton.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				if (editText.getText().toString().equals(result + "")) {
					finishAlarm = true;
					Log.d("DEBUG_TAG", "condition solved");
				} else
					Toast.makeText(dialog.getContext(), "Not Corect!",
							Toast.LENGTH_SHORT).show();

			}

		});
		Log.d("DEBUG_TAG", "exit solvecondition method");
	}

}
