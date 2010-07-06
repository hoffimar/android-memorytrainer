package org.hoffimar.android.memorytrainer;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class GenerateNumberActivity extends Activity {

	private TextView generatedNumberTextView;
	private TextView numberHintTextView;
	private Button showHintButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.generate_number);

		int digits = 10;
		String numberString = "";

		// Generate new number
		Long number = new Double(Math.random() * Math.pow(10, digits)).longValue();
		Log.v(Constants.LOG_TAG, "Generated number : " + number);
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

		showHintButton = (Button) findViewById(R.id.ButtonShowHint);
		showHintButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (numberHintTextView.getVisibility() == View.INVISIBLE){
					numberHintTextView.setVisibility(View.VISIBLE);
					showHintButton.setText(getString(R.string.hide_hint));
				} else {
					numberHintTextView.setVisibility(View.INVISIBLE);
					showHintButton.setText(getString(R.string.show_hint));
				}
				
				
			}
		});
		
		numberHintTextView = (TextView) findViewById(R.id.TextViewNumberHint);
		numberHintTextView.setText(getSymbolsForNumber(numberString));
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setContentView(R.layout.generate_number);

		
		SharedPreferences settings = getSharedPreferences(Overview.PREFS_NAME, 0);
		String numberString = settings.getString("lastNumber", "");
		
		generatedNumberTextView = (TextView) findViewById(R.id.TextViewGeneratedNumber);
		generatedNumberTextView.setText(groupNumbersInString(numberString));

		showHintButton = (Button) findViewById(R.id.ButtonShowHint);
		showHintButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (numberHintTextView.getVisibility() == View.INVISIBLE){
					numberHintTextView.setVisibility(View.VISIBLE);
					showHintButton.setText(getString(R.string.hide_hint));
				} else {
					numberHintTextView.setVisibility(View.INVISIBLE);
					showHintButton.setText(getString(R.string.show_hint));
				}
				
				
			}
		});
		
		numberHintTextView = (TextView) findViewById(R.id.TextViewNumberHint);
		numberHintTextView.setText(getSymbolsForNumber(numberString));
	}

	private String groupNumbersInString(String unformattedNumber) {
		StringBuffer formatted = new StringBuffer("");
		for (int i = 0; i < unformattedNumber.length(); i = i + 2) {
			formatted.append(unformattedNumber.substring(i, i + 2));
			formatted.append(" ");
		}
		Log.v(Constants.LOG_TAG, "unformatted: " + unformattedNumber);
		Log.v(Constants.LOG_TAG, "formatted  : " + formatted);

		return formatted.toString();
	}
	
	private String getSymbolsForNumber(String numberString){
		StringBuffer sb = new StringBuffer();
		
		DbAdapter mDbHelper;
		mDbHelper = new DbAdapter(this);
		mDbHelper.open();
		
		for (int i = 0; i < numberString.length(); i = i + 2) {
			int number = Integer.parseInt(numberString.substring(i, i + 2));
			String symbol = mDbHelper.fetchListItem(number).getString(1);
			sb.append(symbol);
			sb.append(", ");
			Log.v(Constants.LOG_TAG, symbol);
		}
		
		mDbHelper.close();
		
		return sb.toString();
	}
	
	

}
