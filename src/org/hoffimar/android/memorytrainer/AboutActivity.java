package org.hoffimar.android.memorytrainer;

import com.flurry.android.FlurryAgent;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class AboutActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    setContentView(R.layout.about);
	    
	    Button wikipediaButton = (Button) findViewById(R.id.ButtonWikipedia);
	    
	    wikipediaButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.about_link_wikipedia))));
			}
		});
	    
	    TextView textViewWiki = (TextView) findViewById(R.id.AboutTextViewWiki);
	    textViewWiki.setText(getString(R.string.about_description_wiki) + " " + getString(R.string.about_link_wiki));
	}
	
	@Override
    protected void onStart() {
    	super.onStart();
    	FlurryAgent.onStartSession(this, "U7X84RNCY4CR1ZEP6G6Y");
    }
    
    @Override
    protected void onStop() {
    	super.onStop();
    	FlurryAgent.onEndSession(this);
    }

}
