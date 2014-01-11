package app.tasks;

import java.util.Random;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import app.morningalarm.R;

/**
 * clasa care defineste testul logic pentru alarma
 * @author ALEXANDR
 *
 */
public class LogicAlarmTask extends AlarmTask {
	
	/**
	 * ghicitori
	 */
	private static String riddles[] = {
		"It walks on four legs in the morning, two legs at noon and three legs in the evening. What is it?",
		"At night they come without being fetched. By day they are lost without being stolen. What are they?",
		"What belongs to you but others use it more than you do?",
		"What is in seasons, seconds, centuries and minutes but not in decades, years or days?"
	};
	
	/**
	 * raspunsuri la ghicitori
	 */
	private static String answers[] = {
		"human","stars","name","n"
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
		
	}

	/**
	 * metoda ce seteaza valoarea variabilei finishAlarm cu true daca 
	 * sa rezolvat sarcina sau da snooze
	 */
	protected void solveCondition() {
		dialog.setContentView(R.layout.math_test_layout);
		dialog.setTitle("Logic Test");
		dialog.setCancelable(false);
		dialog.show();
		
		TextView textView = (TextView) dialog.findViewById(R.id.math_test_task);
		final EditText editText = (EditText) dialog.findViewById(R.id.math_test_answer);
		Button solveButton = (Button) dialog.findViewById(R.id.math_test_button);
		Random rand = new Random();
		int	num = rand.nextInt(riddles.length);
		
		textView.setText(riddles[num]);
		final String result = answers[num];
		solveButton.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				if (editText.getText().toString().equalsIgnoreCase(result)) {
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
