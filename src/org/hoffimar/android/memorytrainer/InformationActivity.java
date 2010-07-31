package org.hoffimar.android.memorytrainer;

import com.flurry.android.FlurryAgent;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class InformationActivity extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.information);

		Button wikipediaButton = (Button) findViewById(R.id.ButtonWikipedia);

		wikipediaButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				FlurryAgent.onEvent(Constants.FLURRY_EVENTID_INFORMATION_WIKIPEDIA_LINK);
				startActivity(new Intent(Intent.ACTION_VIEW, Uri
						.parse(getString(R.string.about_link_wikipedia))));
			}
		});
		
		TextView textView = (TextView) findViewById(R.id.TextViewFirstSteps);
		textView.setText(getString(R.string.first_steps));
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
