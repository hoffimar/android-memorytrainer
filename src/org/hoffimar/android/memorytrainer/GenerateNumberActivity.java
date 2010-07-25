package org.hoffimar.android.memorytrainer;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;

public class GenerateNumberActivity extends Activity {

	private static final int MENU_DIGITS = 0;

	private static final int DIALOG_DIGITS = 0;

	private TextView generatedNumberTextView;
	private TextView numberHintTextView;
	private Button showHintButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.generate_number);

		int digits = getNumberOfDigits();

		String numberString = generateNumberString(digits);

		// Flurry
		Map<String, String> map = new HashMap<String, String>();
		map.put("generated number", numberString);
		FlurryAgent.onEvent(Constants.FLURRY_EVENTID_GENERATE_NUMBER, map);

		// Save new number to preferences
		saveNumberToPrefs(numberString);

		initUIElements(numberString);
	}

	private String generateNumberString(int digits) {
		String numberString = "";
		// Generate pairs of digits instead of 1 long
		StringBuffer s = new StringBuffer(numberString);
		for (int i = 0; i < digits / 2; i++) {
			int twoDigits = new Double(Math.floor(Math.random() * 100 + 1)).intValue();
			String asString = Integer.toString(twoDigits);
			if (twoDigits < 10) {
				s.append('0' + asString);
			} else {
				if (twoDigits == 100) {
					s.append("99");
				} else {
					s.append(asString);
				}
			}
		}
		numberString = s.toString();
		return numberString;
	}

	private void saveNumberToPrefs(String numberString) {
		SharedPreferences settings = getSharedPreferences(Overview.PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("lastNumber", numberString);
		editor.commit();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setContentView(R.layout.generate_number);

		SharedPreferences settings = getSharedPreferences(Overview.PREFS_NAME, 0);
		String numberString = settings.getString("lastNumber", "");

		initUIElements(numberString);
	}

	private void initUIElements(String numberString) {
		generatedNumberTextView = (TextView) findViewById(R.id.TextViewGeneratedNumber);
		generatedNumberTextView.setText(groupNumbersInString(numberString));

		showHintButton = (Button) findViewById(R.id.ButtonShowHint);
		showHintButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (numberHintTextView.getVisibility() == View.INVISIBLE) {
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
	protected void onStart() {
		super.onStart();
		FlurryAgent.onStartSession(this, Constants.FLURRY_ID);
	}

	@Override
	protected void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(this);
	}

	private String groupNumbersInString(String unformattedNumber) {
		StringBuffer formatted = new StringBuffer("");
		for (int i = 0; i < unformattedNumber.length(); i = i + 2) {
			formatted.append(unformattedNumber.substring(i, i + 2));
			formatted.append(" ");
		}

		return formatted.toString();
	}

	private String getSymbolsForNumber(String numberString) {
		StringBuffer sb = new StringBuffer("");

		DbAdapter mDbHelper;
		mDbHelper = new DbAdapter(this);
		mDbHelper.open();

		if (numberString.length() > 1) {
			for (int i = 0; i < numberString.length(); i = i + 2) {
				int number = Integer.parseInt(numberString.substring(i, i + 2));
				String symbol = mDbHelper.fetchListItem(number).getString(1);
				sb.append(symbol);
				if (i < numberString.length() - 2) {
					sb.append(", ");
				}
			}
		}

		mDbHelper.close();

		return sb.toString();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		menu.add(0, MENU_DIGITS, 0, getString(R.string.button_configure_digits));
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_DIGITS:
			showDialog(DIALOG_DIGITS);
			return true;
		}
		return false;
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog;
		switch (id) {
		case DIALOG_DIGITS:
			dialog = null;
			AlertDialog.Builder builder = new AlertDialog.Builder(this);

			builder.setTitle("Choose number of digits");
			final String[] items = new String[49];
			for (int i = 2; i < 100; i = i + 2) {
				items[(i / 2) - 1] = Integer.toString(i);
			}
			builder.setSingleChoiceItems(items, 4, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// Save number of digits to preferences
					SharedPreferences settings = getSharedPreferences(Overview.PREFS_NAME, 0);
					SharedPreferences.Editor editor = settings.edit();
					editor.putInt("digits", Integer.parseInt(items[which]));
					editor.commit();

					Map<String, String> map = new HashMap<String, String>();
					map.put("digits", items[which]);
					FlurryAgent.onEvent(Constants.FLURRY_EVENTID_CHANGE_NUMBER_DIGITS, map);

					String numberString = generateNumberString(Integer.parseInt(items[which]));
					saveNumberToPrefs(numberString);
					initUIElements(numberString);

					dialog.dismiss();
				}
			});
			dialog = builder.create();
			break;
		default:
			dialog = null;
		}
		return dialog;
	}

	/**
	 * 
	 * @return the length of the number to be remembered as stored in the shared
	 *         preferences
	 */
	private int getNumberOfDigits() {
		SharedPreferences settings = getSharedPreferences(Overview.PREFS_NAME, 0);
		int digits = settings.getInt("digits", 10);
		return digits;
	}

}
