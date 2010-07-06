package org.hoffimar.android.memorytrainer;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class CheckLastNumberActivity extends Activity {

	EditText inputNumber;
	Button verifyNumberButton;
	TextView resultView;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.check_number);

		inputNumber = (EditText) findViewById(R.id.EditTextInputNumberFromMemory);
		inputNumber.setInputType(InputType.TYPE_CLASS_NUMBER);
		resultView = (TextView) findViewById(R.id.TextViewNumberVerification);
		
		verifyNumberButton = (Button) findViewById(R.id.ButtonVerifyNumber);
		verifyNumberButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				SharedPreferences settings = getSharedPreferences(Overview.PREFS_NAME, 0);
				String lastNumber = settings.getString("lastNumber", "");
				String numberUserInput = inputNumber.getText().toString();
				
				if (lastNumber.equals(numberUserInput)){
					resultView.setText(R.string.result_number_right);
				} else {
					resultView.setText(getString(R.string.result_number_wrong) + "\nLast number: " + lastNumber + "\nYour input: " + numberUserInput);
				}
				
			}
		});
	}

}
