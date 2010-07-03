package org.hoffimar.android.memorytrainer.model;

import java.util.List;

import com.google.api.client.util.Key;

public class CellFeed extends Feed {

	@Key("entry")
	public List<CellEntry> cells;
	
}
