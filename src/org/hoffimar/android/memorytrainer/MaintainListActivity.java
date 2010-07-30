package org.hoffimar.android.memorytrainer;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.flurry.android.FlurryAgent;

public class MaintainListActivity extends ListActivity {

	private static final int MENU_DOWNLOAD_SPREADSHEET = 0;
	private DbAdapter mDbHelper;
	private Cursor mCursor;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mDbHelper = new DbAdapter(this);
		mDbHelper.open();

		fillData();

	}

	private void fillData() {
		// Get all of the rows from the database and create the item list
		mCursor = mDbHelper.fetchAllNotes();
		startManagingCursor(mCursor);

		// Create an array to specify the fields we want to display in the list
		// (only TITLE)
		String[] from = new String[] { DbAdapter.KEY_ID, DbAdapter.KEY_SYMBOL };

		// and an array of the fields we want to bind those fields to (in this
		// case just text1)
		int[] to = new int[] { R.id.TextViewID, R.id.TextViewSymbol };

		// Now create a simple cursor adapter and set it to display
		SimpleCursorAdapter items = new SimpleCursorAdapter(this, R.layout.listhundred, mCursor,
				from, to);
		setListAdapter(items);

		ListView lv = getListView();
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Log.v(Constants.LOG_TAG, "clicked: " + arg2 + ", " + arg3);

				Intent i = new Intent(arg1.getContext(), EditItemActivity.class);
				i.putExtra("id", arg2 + 1);

				FlurryAgent.onEvent(Constants.FLURRY_EVENTID_MAINTAIN_LIST_EDIT_ITEM);

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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_DOWNLOAD_SPREADSHEET, 0, getString(R.string.download_spreadsheets));
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_DOWNLOAD_SPREADSHEET:

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.download_spreadsheets_warning)
					.setCancelable(false).setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
									Intent intent = new Intent(getApplicationContext(),
											ListHundredActivity.class);
									startActivity(intent);
								}
							}).setNegativeButton("No", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});
			AlertDialog alert = builder.create();
			alert.show();

			return true;
		}
		return false;
	}
}
