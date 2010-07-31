package org.hoffimar.android.memorytrainer;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;

public class EditItemActivity extends Activity {

	private TextView tv;
	private EditText itemField;
	private Button saveButton;

	private DbAdapter mDbHelper;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.edit_item);

		tv = (TextView) findViewById(R.id.EditItemTextView01);
		itemField = (EditText) findViewById(R.id.EditItemEditText01);
		saveButton = (Button) findViewById(R.id.EditItemButtonSave);

		Bundle extras = getIntent().getExtras();
		final int id = extras.getInt("id");

		tv.setText(getString(R.string.edit_item_label) + " " + id + ":");

		saveButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				mDbHelper = new DbAdapter(v.getContext());
				mDbHelper.open();

				mDbHelper.updateNote(id, itemField.getText().toString(), "");
				
				FlurryAgent.onEvent(Constants.FLURRY_EVENTID_MAINTAIN_LIST_EDIT_ITEM_SAVE);

				finish();
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
