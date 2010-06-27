package org.hoffimar.android.memorytrainer;





import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SimpleCursorAdapter;

public class ListHundredActivity extends ListActivity {

	private DbAdapter mDbHelper;
	private Cursor mCursor;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	
//	    setContentView(R.layout.listhundred_item);
	    
        mDbHelper = new DbAdapter(this);
        mDbHelper.open();
        
        fillData();
	}
	
	private void fillData() {
        // Get all of the rows from the database and create the item list
        mCursor = mDbHelper.fetchAllNotes();
        startManagingCursor(mCursor);
        
        // Create an array to specify the fields we want to display in the list (only TITLE)
        String[] from = new String[]{DbAdapter.KEY_ID, DbAdapter.KEY_SYMBOL};
        
        // and an array of the fields we want to bind those fields to (in this case just text1)
        int[] to = new int[]{R.id.TextViewID, R.id.TextViewSymbol};
        
        // Now create a simple cursor adapter and set it to display
        SimpleCursorAdapter items = 
        	    new SimpleCursorAdapter(this, R.layout.listhundred, mCursor, from, to);
        setListAdapter(items);
    }
	
	

}
