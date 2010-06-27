package org.hoffimar.android.memorytrainer;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class GenerateNumberActivity extends Activity {

	private TextView generatedNumberTextView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.generate_number);

		int digits = 6;
		String numberString = "";

		// Generate new number
		Long number = new Double(Math.random() * Math.pow(10, digits)).longValue();
		Log.v("MemoryTrainer", "Generated number : " + number);
		numberString = number.toString();
		if (numberString.length() < digits){
			StringBuffer s = new StringBuffer(numberString);
			for (int i = 0; i < digits - numberString.length(); i++){
				s.insert(0, '0');
			}
			numberString = s.toString();
		}

		// Save new number to preferences
		SharedPreferences settings = getSharedPreferences(Overview.PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("lastNumber", numberString);
		editor.commit();

		generatedNumberTextView = (TextView) findViewById(R.id.TextViewGeneratedNumber);
		generatedNumberTextView.setText(groupNumbersInString(numberString));

	}

	private String groupNumbersInString(String unformattedNumber) {
		StringBuffer formatted = new StringBuffer("");
		for (int i = 0; i < unformattedNumber.length(); i = i + 2) {
			formatted.append(unformattedNumber.substring(i, i + 2));
			formatted.append(" ");
		}
		Log.v("MemoryTrainer", "unformatted: " + unformattedNumber);
		Log.v("MemoryTrainer", "formatted  : " + formatted);

		return formatted.toString();
	}

}
