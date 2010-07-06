package org.hoffimar.android.memorytrainer;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

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
	}

}
