package org.hoffimar.android.memorytrainer;

import java.util.ArrayList;
import java.util.List;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;

public class EditPreferencesActivity extends PreferenceActivity implements
		SharedPreferences.OnSharedPreferenceChangeListener {

	private ListPreference lowerBoundaryPreference;
	private ListPreference upperBoundaryPreference;
	private SharedPreferences sharedPreferences;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.preferences);

		sharedPreferences = getSharedPreferences(Overview.PREFS_NAME, 0);
		String lowerString = sharedPreferences.getString("lowerBoundary", "1");
		String upperString = sharedPreferences.getString("upperBoundary", "100");

		lowerBoundaryPreference = (ListPreference) findPreference("lowerBoundary");
		fillPreferenceWithEntries(true, 1, Integer.parseInt(upperString) - 1);

		upperBoundaryPreference = (ListPreference) findPreference("upperBoundary");
		fillPreferenceWithEntries(false, Integer.parseInt(lowerString) + 1, 100);


	}

	private void fillPreferenceWithEntries(boolean lower, int low, int up) {
		List<String> entriesList = new ArrayList<String>();
		List<String> entryValuesList = new ArrayList<String>();

		for (int i = low; i <= up; i++) {
			entriesList.add((new Integer(i)).toString());
			entryValuesList.add((new Integer(i)).toString());
		}

		CharSequence[] entries = new CharSequence[entriesList.size()];
		CharSequence[] entryValues = new CharSequence[entryValuesList.size()];

		if (lower) {
			lowerBoundaryPreference.setEntries(entriesList.toArray(entries));
			lowerBoundaryPreference.setEntryValues(entryValuesList.toArray(entryValues));
		} else {
			upperBoundaryPreference.setEntries(entriesList.toArray(entries));
			upperBoundaryPreference.setEntryValues(entryValuesList.toArray(entryValues));
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		// Set up a listener whenever a key changes
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		// Unregister the listener whenever a key changes
		getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(
				this);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		SharedPreferences.Editor editor = this.sharedPreferences.edit();
		
		if (key.equalsIgnoreCase("lowerBoundary")) {
			String lowerString = sharedPreferences.getString("lowerBoundary", "1");
			fillPreferenceWithEntries(false, Integer.parseInt(lowerString) + 1, 100);
			editor.putString("lowerBoundary", lowerString);
		}
		if (key.equalsIgnoreCase("upperBoundary")) {
			String upperString = sharedPreferences.getString("upperBoundary", "100");
			fillPreferenceWithEntries(true, 1, Integer.parseInt(upperString) - 1);
			editor.putString("upperBoundary", upperString);
		}
		editor.commit();

	}

}
