package org.hoffimar.android.memorytrainer.model;

import java.util.List;

import com.google.api.client.util.Key;

public class UserFeed extends Feed {

	@Key("entry")
	public List<SpreadsheetEntry> spreadsheets;

}
