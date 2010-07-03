package org.hoffimar.android.memorytrainer.model;

import com.google.api.client.util.Key;

public class CellEntry extends Entry {
	
	@Key
	public String content;
	
	@Key("gs:cell")
	public Cell cell;
	

}
