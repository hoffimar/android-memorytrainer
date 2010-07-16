package org.hoffimar.android.memorytrainer;

import java.io.IOException;

import org.hoffimar.android.memorytrainer.model.CellEntry;
import org.hoffimar.android.memorytrainer.model.CellFeed;
import org.hoffimar.android.memorytrainer.model.SpreadsheetEntry;
import org.hoffimar.android.memorytrainer.model.UserFeed;
import org.hoffimar.android.memorytrainer.model.WorksheetsFeed;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SimpleCursorAdapter;

import com.flurry.android.FlurryAgent;
import com.google.api.client.apache.ApacheHttpTransport;
import com.google.api.client.auth.oauth.OAuthCallbackUrl;
import com.google.api.client.auth.oauth.OAuthCredentialsResponse;
import com.google.api.client.auth.oauth.OAuthHmacSigner;
import com.google.api.client.auth.oauth.OAuthParameters;
import com.google.api.client.googleapis.GoogleTransport;
import com.google.api.client.googleapis.GoogleUrl;
import com.google.api.client.googleapis.auth.oauth.GoogleOAuthAuthorizeTemporaryTokenUrl;
import com.google.api.client.googleapis.auth.oauth.GoogleOAuthGetAccessToken;
import com.google.api.client.googleapis.auth.oauth.GoogleOAuthGetTemporaryToken;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.xml.atom.AtomParser;
import com.google.api.data.spreadsheet.v3.GoogleSpreadsheets;
import com.google.api.data.spreadsheet.v3.atom.GoogleSpreadsheetsAtom;

public class ListHundredActivity extends ListActivity {

	private static final int MENU_IMPORT_SPREADSHEETS = 0;

	private DbAdapter mDbHelper;
	private Cursor mCursor;

	// Needed for Google Spreadsheets
	private static final GoogleTransport transport = new GoogleTransport();
	private static OAuthCredentialsResponse credentials;
	private static boolean isTemporary;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// setContentView(R.layout.listhundred_item);

		mDbHelper = new DbAdapter(this);
		mDbHelper.open();

		transport.setVersionHeader(GoogleSpreadsheets.VERSION);
		AtomParser parser = new AtomParser();
		parser.namespaceDictionary = GoogleSpreadsheetsAtom.NAMESPACE_DICTIONARY;
		transport.addParser(parser);
		transport.applicationName = "memorize-it";
		HttpTransport.setLowLevelHttpTransport(ApacheHttpTransport.INSTANCE);
		gotAccount(false);

		fillData();
	}

	private void fillData() {
		// Get all of the rows from the database and create the item list
		mCursor = mDbHelper.fetchAllNotes();
		startManagingCursor(mCursor);

		// Create an array to specify the fields we want to display in the list
		// (only TITLE)
		String[] from = new String[] { DbAdapter.KEY_ID, DbAdapter.KEY_SYMBOL };

		// and an array of the fields we want to bind those fields to (in this
		// case just text1)
		int[] to = new int[] { R.id.TextViewID, R.id.TextViewSymbol };

		// Now create a simple cursor adapter and set it to display
		SimpleCursorAdapter items = new SimpleCursorAdapter(this, R.layout.listhundred, mCursor,
				from, to);
		setListAdapter(items);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_IMPORT_SPREADSHEETS, 0, "Import from Google Spreadsheets");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_IMPORT_SPREADSHEETS:
			// TODO do something?
			return true;
		}
		return false;
	}

	private static OAuthHmacSigner createOAuthSigner() {
		OAuthHmacSigner result = new OAuthHmacSigner();
		if (credentials != null) {
			result.tokenSharedSecret = credentials.tokenSecret;
		}
		result.clientSharedSecret = "anonymous";
		return result;
	}

	private static OAuthParameters createOAuthParameters() {
		OAuthParameters authorizer = new OAuthParameters();
		authorizer.consumerKey = "anonymous";
		authorizer.signer = createOAuthSigner();
		authorizer.token = credentials.token;
		return authorizer;
	}

	private void gotAccount(boolean tokenExpired) {
		try {
			boolean isViewAction = Intent.ACTION_VIEW.equals(getIntent().getAction());

			if (tokenExpired && !isTemporary && credentials != null) {
				GoogleOAuthGetAccessToken.revokeAccessToken(createOAuthParameters());
				credentials = null;
			}
			if (tokenExpired || !isViewAction && (isTemporary || credentials == null)) {
				GoogleOAuthGetTemporaryToken temporaryToken = new GoogleOAuthGetTemporaryToken();
				temporaryToken.signer = createOAuthSigner();
				temporaryToken.consumerKey = "anonymous";
				temporaryToken.scope = GoogleSpreadsheets.ROOT_URL;
				temporaryToken.displayName = "Spreadsheets Atom XML Sample for the GData Java library";
				temporaryToken.callback = "memorize-it:///";
				isTemporary = true;
				credentials = temporaryToken.execute();
				GoogleOAuthAuthorizeTemporaryTokenUrl authorizeUrl = new GoogleOAuthAuthorizeTemporaryTokenUrl();
				authorizeUrl.temporaryToken = credentials.token;
				Intent webIntent = new Intent(Intent.ACTION_VIEW);
				webIntent.setData(Uri.parse(authorizeUrl.build()));
				startActivity(webIntent);
			} else {
				if (isViewAction) {
					Uri uri = this.getIntent().getData();
					OAuthCallbackUrl callbackUrl = new OAuthCallbackUrl(uri.toString());
					GoogleOAuthGetAccessToken accessToken = new GoogleOAuthGetAccessToken();
					accessToken.temporaryToken = callbackUrl.token;
					accessToken.verifier = callbackUrl.verifier;
					accessToken.signer = createOAuthSigner();
					accessToken.consumerKey = "anonymous";
					isTemporary = false;

					credentials = accessToken.execute();

					createOAuthParameters().signRequestsUsingAuthorizationHeader(transport);

					authenticated();
				}
			}
		} catch (IOException e) {
			Log.e(Constants.LOG_TAG, e.getMessage());
		}
	}

	private void authenticated() {

		HttpRequest request = transport.buildGetRequest();
		request.url = new GoogleUrl(GoogleSpreadsheets.ROOT_URL + "spreadsheets/private/full");

		// Load data
		try {

			// Get spreadsheet feed for user
			long timeBeforeRequest = System.currentTimeMillis();
			HttpResponse response = request.execute();
			long timeAfterRequest = System.currentTimeMillis();
			Log.v(Constants.LOG_TAG_PERF, "Time for spreadsheet request: "
					+ (timeAfterRequest - timeBeforeRequest) + "ms");

			long timeBeforeParsing = System.currentTimeMillis();
			UserFeed userFeed = response.parseAs(UserFeed.class);
			long timeAfterParsing = System.currentTimeMillis();
			Log.v(Constants.LOG_TAG_PERF, "Time for spreadsheet parsing: "
					+ (timeAfterParsing - timeBeforeParsing) + "ms");

			if (userFeed.spreadsheets != null) {
				for (SpreadsheetEntry se : userFeed.spreadsheets) {
					Log.v(Constants.LOG_TAG, "Spreadsheet title: " + se.title);

					if (se.title.equalsIgnoreCase("MemorizeIt")) {
						// Get worksheet feed
						GoogleUrl url = new GoogleUrl(se.getContentLink().replaceFirst("http:",
								"https:"));
						Log.v(Constants.LOG_TAG, url.toString());

						HttpRequest requestWorksheetFeed = transport.buildGetRequest();
						requestWorksheetFeed.url = url;

						timeBeforeRequest = System.currentTimeMillis();
						response = requestWorksheetFeed.execute();
						timeAfterRequest = System.currentTimeMillis();
						Log.v(Constants.LOG_TAG_PERF, "Time for worksheet request: "
								+ (timeAfterRequest - timeBeforeRequest) + "ms");

						timeBeforeParsing = System.currentTimeMillis();
						WorksheetsFeed worksheetsFeed = response.parseAs(WorksheetsFeed.class);
						timeAfterParsing = System.currentTimeMillis();
						Log.v(Constants.LOG_TAG_PERF, "Time for worksheet parsing: "
								+ (timeAfterParsing - timeBeforeParsing) + "ms");

						// Get cells feed
						GoogleUrl listFeedUrl = new GoogleUrl(worksheetsFeed.worksheets.get(0)
								.getCellsLink().replaceFirst("http:", "https:"));
						HttpRequest requestCellsFeed = transport.buildGetRequest();
						requestCellsFeed.url = listFeedUrl;
						Log.v(Constants.LOG_TAG, listFeedUrl.toString());

						timeBeforeRequest = System.currentTimeMillis();
						response = requestCellsFeed.execute();
						timeAfterRequest = System.currentTimeMillis();
						Log.v(Constants.LOG_TAG_PERF, "Time for cells request: "
								+ (timeAfterRequest - timeBeforeRequest) + "ms");

						timeBeforeParsing = System.currentTimeMillis();
						CellFeed cellFeed = response.parseAs(CellFeed.class);
						timeAfterParsing = System.currentTimeMillis();
						Log.v(Constants.LOG_TAG_PERF, "Time for cells parsing: "
								+ (timeAfterParsing - timeBeforeParsing) + "ms");

						int i = 0;
						for (CellEntry cell : cellFeed.cells) {
							if (cell.cell.col.equals("1")) {
								i++;
								mDbHelper.updateNote(i, cell.cell.inputValue, "");
							}

						}
						Log.v(Constants.LOG_TAG, "done reading...");
					}
				}
			} else {
				// TODO: no spreadsheets available for user
			}
		} catch (IOException e) {
			Log.e(Constants.LOG_TAG, "Message: " + e.getMessage());
		}

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
