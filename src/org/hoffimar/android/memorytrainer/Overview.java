package org.hoffimar.android.memorytrainer;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class Overview extends Activity {
	
	public static final int MENU_ABOUT = 1;
	
	public static final String PREFS_NAME = "MyPrefsFile";

	
	private Button maintainListButton;
	private Button generateNumberButton;
	private Button verifyNumberButton;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        
        maintainListButton = (Button) findViewById(R.id.maintainListButton);
        maintainListButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(v.getContext(), ListHundredActivity.class);
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
				startActivity(i);
			}
		});
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	menu.add(0, MENU_ABOUT, 0, "About");
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