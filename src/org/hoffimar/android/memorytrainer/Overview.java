package org.hoffimar.android.memorytrainer;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.flurry.android.FlurryAgent;

public class Overview extends Activity {
	
	public static final int MENU_ABOUT = 1;
	
	public static final String PREFS_NAME = "MyPrefsFile";

	private boolean enableUsageStatistics = true;
	
	private Button treelistButton;
	private Button informationButton;
	private Button maintainListButton;
	private Button generateNumberButton;
	private Button verifyNumberButton;
	private Button statisticsButton;
	private Button settingsButton;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        treelistButton = (Button) findViewById(R.id.TreelistButton);
        treelistButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(v.getContext(), TreeListActivity.class);
				
				FlurryAgent.onEvent(Constants.FLURRY_EVENTID_TREELIST_INTRO);
				
				startActivity(i);
			}
		});
        
        informationButton = (Button) findViewById(R.id.InformationButton);
        informationButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(v.getContext(), InformationActivity.class);
				
				FlurryAgent.onEvent(Constants.FLURRY_EVENTID_FIRST_STEPS);
				
				startActivity(i);
			}
		});
        
        maintainListButton = (Button) findViewById(R.id.maintainListButton);
        maintainListButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(v.getContext(), MaintainListActivity.class);
				
				FlurryAgent.onEvent(Constants.FLURRY_EVENTID_MAINTAIN_LISTS);
				
				startActivity(i);
			}
		});
        
        generateNumberButton = (Button) findViewById(R.id.generateNumberButton);
        generateNumberButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(v.getContext(), GenerateNumberActivity.class);
				startActivity(i);
			}
		});
        
        verifyNumberButton = (Button) findViewById(R.id.verifyNumberButton);
        verifyNumberButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(v.getContext(), CheckLastNumberActivity.class);
				
				FlurryAgent.onEvent(Constants.FLURRY_EVENTID_CHECK_NUMBER_ACTIVITY);
				
				startActivity(i);
			}
		});
        
        statisticsButton = (Button) findViewById(R.id.statisticsOverviewButton);
        statisticsButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(v.getContext(), StatisticsOverviewActivity.class);
				startActivity(i);
			}
		});
        
        settingsButton = (Button) findViewById(R.id.SettingsButton);
        settingsButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(v.getContext(), EditPreferencesActivity.class);
				startActivity(i);
			}
		});
        
        if (!enableUsageStatistics){
        	FlurryAgent.setLogEnabled(false);
        }
        
        FlurryAgent.onEvent(Constants.FLURRY_EVENTID_OVERVIEW);
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
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	menu.add(0, MENU_ABOUT, 0, getString(R.string.main_about_button));
    	menu.findItem(MENU_ABOUT).setIcon(android.R.drawable.ic_menu_info_details);
    	return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    	case MENU_ABOUT:
    		Intent intent = new Intent(this, AboutActivity.class);
    		startActivity(intent);
    		return true;
    	}
    	return false;
    }
    
    
}