package org.hoffimar.android.memorytrainer;

import com.flurry.android.FlurryAgent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class TreeListActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	
	    setContentView(R.layout.treelist);
	    
	    Button continueButton = (Button) findViewById(R.id.TreeListContinueButton);
        continueButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(v.getContext(), TreeListGenerateActivity.class);
				
				FlurryAgent.onEvent(Constants.FLURRY_EVENTID_TREELIST_GENERATE);
				
				startActivity(i);
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
