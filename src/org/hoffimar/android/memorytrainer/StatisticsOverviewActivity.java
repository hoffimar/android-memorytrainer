package org.hoffimar.android.memorytrainer;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;

public class StatisticsOverviewActivity extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    setContentView(R.layout.stats_overview);
	
	    TextView correctTextView = (TextView) findViewById(R.id.StatsTextView02);
		TextView incorrectTextView = (TextView) findViewById(R.id.StatsTextView04);
		TextView percCorrectTextView = (TextView) findViewById(R.id.StatsTextView06);
	    
	    DbAdapter mDbHelper;
		mDbHelper = new DbAdapter(this);
		mDbHelper.open();
		
		Cursor statsCursor = mDbHelper.fetchAllStats();
		startManagingCursor(statsCursor);
		
		int countCorrect = 0;
		int countIncorrect = 0;
		
		while (statsCursor.moveToNext()){
//			String date = statsCursor.getString(1);
//			String number = statsCursor.getString(2);
			int isCorrect = statsCursor.getInt(3);
			
			if (isCorrect == 0){
				countIncorrect++;
			} else {
				countCorrect++;
			}
			
		}
		
		mDbHelper.close();
		
		double percentageCorrect = 0;
		if (countCorrect + countIncorrect != 0){
			percentageCorrect = (new Double(countCorrect) / (countCorrect + countIncorrect)) * 100;
		}
		
		
		correctTextView.setText(Integer.toString(countCorrect));
		incorrectTextView.setText(Integer.toString(countIncorrect));
		
		if (countCorrect + countIncorrect != 0){
			percCorrectTextView.setText(Long.toString(Math.round(percentageCorrect)));
		} else {
			percCorrectTextView.setText("-");
		}
		
		FlurryAgent.onEvent(Constants.FLURRY_EVENTID_STATS_OVERVIEW);
		
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
