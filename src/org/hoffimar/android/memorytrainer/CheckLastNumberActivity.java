package org.hoffimar.android.memorytrainer;

import java.util.HashMap;
import java.util.Map;

import com.flurry.android.FlurryAgent;

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
				
				DbAdapter mDbHelper;
				mDbHelper = new DbAdapter(getApplicationContext());
				mDbHelper.open();
				
				boolean isInputCorrect = lastNumber.equals(numberUserInput);
				if (isInputCorrect){
					resultView.setText(R.string.result_number_right);
					mDbHelper.createStatsItem(lastNumber, true);
				} else {
					resultView.setText(getString(R.string.result_number_wrong) + "\n" + getString(R.string.check_number_last_number_label) + ": " + lastNumber + "\n" + getString(R.string.check_number_your_input_label) + ": " + numberUserInput);
					mDbHelper.createStatsItem(lastNumber, false);
				}
				
				mDbHelper.close();
				
				
				Map<String, String> map = new HashMap<String, String>();
				map.put("input correct?", Boolean.toString(isInputCorrect));
				FlurryAgent.onEvent(Constants.FLURRY_EVENTID_CHECK_NUMBER_BUTTON, map);
				
			}
		});
	}
	
	@Override
    protected void onStart() {
    	super.onStart();
    	FlurryAgent.onStartSession(this, Constants.FLURRY_ID);
    }
    
    @Override
    protected void onStop() {
    	super.onStop();
    	FlurryAgent.onEndSession(this);
    }

}
