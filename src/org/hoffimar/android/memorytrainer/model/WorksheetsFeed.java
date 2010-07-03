package org.hoffimar.android.memorytrainer.model;

import java.util.List;

import com.google.api.client.util.Key;

public class WorksheetsFeed extends Feed {
	
	@Key("entry")
	public List<WorksheetEntry> worksheets;
	
	
}
