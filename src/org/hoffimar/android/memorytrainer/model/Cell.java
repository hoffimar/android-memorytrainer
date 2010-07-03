package org.hoffimar.android.memorytrainer.model;

import com.google.api.client.util.Key;

public class Cell {

	@Key("@row")
	public String row;

	@Key("@col")
	public String col;
	
	@Key("@inputValue")
	public String inputValue;
}
