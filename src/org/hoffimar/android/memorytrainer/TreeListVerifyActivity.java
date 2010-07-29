package org.hoffimar.android.memorytrainer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.flurry.android.FlurryAgent;

public class TreeListVerifyActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle extras = getIntent().getExtras();
		final String no1 = extras.getString("no1");
		final String no2 = extras.getString("no2");
		final String no3 = extras.getString("no3");
		final String no4 = extras.getString("no4");
		final String no5 = extras.getString("no5");

		setContentView(R.layout.treelist_verify);

		Button verifyButton = (Button) findViewById(R.id.TreeListVerifyButton);
		verifyButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean inputCorrect = true;
				String input1 = ((EditText) findViewById(R.id.TreeListVerifyField1)).getText()
						.toString();
				if (!input1.equals(no1))
					inputCorrect = false;
				String input2 = ((EditText) findViewById(R.id.TreeListVerifyField2)).getText()
						.toString();
				if (!input2.equals(no2))
					inputCorrect = false;
				String input3 = ((EditText) findViewById(R.id.TreeListVerifyField3)).getText()
						.toString();
				if (!input3.equals(no3))
					inputCorrect = false;
				String input4 = ((EditText) findViewById(R.id.TreeListVerifyField4)).getText()
						.toString();
				if (!input4.equals(no4))
					inputCorrect = false;
				String input5 = ((EditText) findViewById(R.id.TreeListVerifyField5)).getText()
						.toString();
				if (!input5.equals(no5))
					inputCorrect = false;

				FlurryAgent.onEvent(Constants.FLURRY_EVENTID_TREELIST_VERIFY_CLICKVERIFY);

				AlertDialog.Builder builder = new AlertDialog.Builder(TreeListVerifyActivity.this);
				String message;
				if (inputCorrect) {
					message = getString(R.string.treelist_verify_dialog_positive);
				} else {
					message = getString(R.string.treelist_verify_dialog_negative);
				}
				
				builder.setMessage(message).setCancelable(true).setPositiveButton("OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.dismiss();
							}
						});

				AlertDialog alert = builder.create();
				alert.show();

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
