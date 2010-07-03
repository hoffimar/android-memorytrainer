package org.hoffimar.android.memorytrainer;

import java.io.IOException;

import org.hoffimar.android.memorytrainer.model.CellEntry;
import org.hoffimar.android.memorytrainer.model.CellFeed;
import org.hoffimar.android.memorytrainer.model.SpreadsheetEntry;
import org.hoffimar.android.memorytrainer.model.UserFeed;
import org.hoffimar.android.memorytrainer.model.WorksheetsFeed;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SimpleCursorAdapter;

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

		transport.setVersionHeader(GoogleSpreadsheets.VERSION);
		AtomParser parser = new AtomParser();
		parser.namespaceDictionary = GoogleSpreadsheetsAtom.NAMESPACE_DICTIONARY;
		transport.addParser(parser);
		transport.applicationName = "memorize-it";
		HttpTransport.setLowLevelHttpTransport(ApacheHttpTransport.INSTANCE);
		gotAccount(false);

		mDbHelper = new DbAdapter(this);
		mDbHelper.open();

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
			new ImportListTask().execute("URL...");// TODO change
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
			// TODO Auto-generated catch block
			Log.e(Constants.LOG_TAG, e.getMessage());
		}
	}

	private void authenticated() {

		HttpRequest request = transport.buildGetRequest();
		request.url = new GoogleUrl(GoogleSpreadsheets.ROOT_URL + "spreadsheets/private/full");

		// Load data
		try {
			HttpResponse response = request.execute();
			UserFeed userFeed = response.parseAs(UserFeed.class);

			for (SpreadsheetEntry se : userFeed.spreadsheets) {
				Log.v(Constants.LOG_TAG, "Spreadsheet title: " + se.title);
				
				if (se.title.equalsIgnoreCase("MemorizeIt")){
					// Get worksheet feed url
					GoogleUrl url = new GoogleUrl(se.getContentLink().replaceFirst("http:", "https:"));
					
					Log.v(Constants.LOG_TAG, url.toString());
					
					
					// Execute request
					HttpRequest request2 = transport.buildGetRequest();
					request2.url = url;
//					request2.url = new GoogleUrl("https://spreadsheets.google.com/feeds/t9cz-0tm0gGzOJCXMVju0Xw/tables");
//					request2.url = new GoogleUrl(GoogleSpreadsheets.ROOT_URL + "spreadsheets/private/full");
					response = request2.execute();
					WorksheetsFeed worksheetsFeed = response.parseAs(WorksheetsFeed.class);
//					String respString = response.parseAsString();
//					Log.v(Constants.LOG_TAG, respString);
					
					// Get list feed
					GoogleUrl listFeedUrl = new GoogleUrl(worksheetsFeed.worksheets.get(0).getCellsLink().replaceFirst("http:", "https:"));
					HttpRequest request3 = transport.buildGetRequest();
					request3.url = listFeedUrl;
					Log.v(Constants.LOG_TAG, listFeedUrl.toString());
					response = request3.execute();
//					String respString = response.parseAsString();
//					Log.v(Constants.LOG_TAG, respString);
					CellFeed cellFeed = response.parseAs(CellFeed.class);
					
					for (CellEntry cell : cellFeed.cells){
						Log.v(Constants.LOG_TAG, cell.content);
					}
					Log.v(Constants.LOG_TAG, "done reading...");
				}
			}

		} catch (IOException e) {
			Log.e(Constants.LOG_TAG, "Message: " + e.getMessage());
		}

	}

	private class ImportListTask extends AsyncTask<String, Integer, Long> {

		@Override
		protected Long doInBackground(String... params) {
			AccountManager accountManager = AccountManager.get(getApplicationContext());
			Account[] accounts = accountManager.getAccountsByType("com.google");
			Account account;
			if (accounts.length > 0) {
				account = accounts[0];
				Log.v(Constants.LOG_TAG, "account name: " + account.name);

				// Call google server
				transport.setVersionHeader(GoogleSpreadsheets.VERSION);
				AtomParser parser = new AtomParser();
				parser.namespaceDictionary = GoogleSpreadsheetsAtom.NAMESPACE_DICTIONARY;
				transport.addParser(parser);
				transport.applicationName = "memorize-it";
				HttpTransport.setLowLevelHttpTransport(ApacheHttpTransport.INSTANCE);

				gotAccount(true);

			} else {
				Log.v(Constants.LOG_TAG, "no account found");
				account = null;
			}

			return null;
		}

		@Override
		protected void onPostExecute(Long result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
		}

	}

}
