package org.hoffimar.android.memorytrainer.model;

import java.util.List;

import com.google.api.client.util.DataUtil;
import com.google.api.client.util.Key;


public class Entry {

	@Key("@gd:etag")
	  public String etag;

	  @Key("link")
	  public List<Link> links;
	  
	  

	  @Key
	  public String title;

	  @Override
	  protected Entry clone() {
	    return DataUtil.clone(this);
	  }
}
