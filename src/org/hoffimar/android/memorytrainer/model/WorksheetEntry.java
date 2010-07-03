package org.hoffimar.android.memorytrainer.model;

import com.google.api.client.util.Key;

public class WorksheetEntry extends Entry {

	@Key("content")
	  public Content content;
	
	public String getContentLink() {
		return content.src;
	}
	
	public String getCellsLink() {
		return Link.find(links, "http://schemas.google.com/spreadsheets/2006#cellsfeed");
	}
}
