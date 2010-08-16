package org.hoffimar.android.memorytrainer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;

public class TreeListGenerateActivity extends Activity {

	private TextView nr1;
	private TextView nr2;
	private TextView nr3;
	private TextView nr4;
	private TextView nr5;

	private TextView hint1;
	private TextView hint2;
	private TextView hint3;
	private TextView hint4;
	private TextView hint5;

	private short numbers[] = { 0, 0, 0, 0, 0 };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.treelist_generate);

		nr1 = (TextView) findViewById(R.id.treelist_generate_tv_1);
		nr2 = (TextView) findViewById(R.id.treelist_generate_tv_2);
		nr3 = (TextView) findViewById(R.id.treelist_generate_tv_3);
		nr4 = (TextView) findViewById(R.id.treelist_generate_tv_4);
		nr5 = (TextView) findViewById(R.id.treelist_generate_tv_5);

		// make sure the same number only gets generated once, otherwise new
		// users might be confused
		nr1.setText(generateNumber(1));
		nr2.setText(generateNumber(2));
		nr3.setText(generateNumber(3));
		nr4.setText(generateNumber(4));
		nr5.setText(generateNumber(5));

		Button continueButton = (Button) findViewById(R.id.TreeListGenerateContinueButton);
		continueButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(v.getContext(), TreeListVerifyActivity.class);
				i.putExtra("no1", nr1.getText());
				i.putExtra("no2", nr2.getText());
				i.putExtra("no3", nr3.getText());
				i.putExtra("no4", nr4.getText());
				i.putExtra("no5", nr5.getText());
				FlurryAgent.onEvent(Constants.FLURRY_EVENTID_TREELIST_VERIFY);

				startActivity(i);
			}
		});

		Button showHintsButton = (Button) findViewById(R.id.TreeListGenerateHintButton);
		showHintsButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				hint1 = (TextView) findViewById(R.id.TreeListGenerateItem01);
				hint2 = (TextView) findViewById(R.id.TreeListGenerateItem02);
				hint3 = (TextView) findViewById(R.id.TreeListGenerateItem03);
				hint4 = (TextView) findViewById(R.id.TreeListGenerateItem04);
				hint5 = (TextView) findViewById(R.id.TreeListGenerateItem05);

				hint1.setText(getItem(nr1.getText().toString()));
				hint2.setText(getItem(nr2.getText().toString()));
				hint3.setText(getItem(nr3.getText().toString()));
				hint4.setText(getItem(nr4.getText().toString()));
				hint5.setText(getItem(nr5.getText().toString()));

				hint1.setVisibility(TextView.VISIBLE);
				hint2.setVisibility(TextView.VISIBLE);
				hint3.setVisibility(TextView.VISIBLE);
				hint4.setVisibility(TextView.VISIBLE);
				hint5.setVisibility(TextView.VISIBLE);

			}
		});
	}

	private String generateNumber(int pos) {
		short number = 0;
		do {
			number = new Double(Math.ceil(Math.random() * 10)).shortValue();
		} while (isNumberInArray(number));
		numbers[pos-1] = number;
		
		return Short.toString(number);
	}
	
	private boolean isNumberInArray(short number){
		 
		for (int i = 0; i < numbers.length; i++){
			if (numbers[i] == number){
				return true;
			}
		}
		return false;
	}

	private String getItem(String number) {
		switch (Integer.parseInt(number)) {// TODO: cleanup ugly coding
		case 1:
			return getString(R.string.treelist_item01);
		case 2:
			return getString(R.string.treelist_item02);
		case 3:
			return getString(R.string.treelist_item03);
		case 4:
			return getString(R.string.treelist_item04);
		case 5:
			return getString(R.string.treelist_item05);
		case 6:
			return getString(R.string.treelist_item06);
		case 7:
			return getString(R.string.treelist_item07);
		case 8:
			return getString(R.string.treelist_item08);
		case 9:
			return getString(R.string.treelist_item09);
		case 10:
			return getString(R.string.treelist_item10);
		default:
			break;
		}
		return "";
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
