package org.hoffimar.android.memorytrainer.model;

import com.google.api.client.util.Key;

public class SpreadsheetEntry extends Entry {

	@Key("content")
	  public Content content;
	
	public String getTablesLink() {
		return Link.find(links, "http://schemas.google.com/spreadsheets/2006#tablesfeed");
	}

	public String getContentLink() {
		return content.src;
	}
}
