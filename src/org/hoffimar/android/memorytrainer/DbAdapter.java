package org.hoffimar.android.memorytrainer;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbAdapter {

	public static final String KEY_SYMBOL = "symbol";
	public static final String KEY_PICURL = "picurl";
	public static final String KEY_ID = "_id";

	private static final String DATABASE_NAME = "data";
	private static final int DATABASE_VERSION = 1;
	private static final String LIST_TABLE_NAME = "list";

	private static final String STATS_TABLE_NAME = "stats";
	private static final String STATS_KEY_TIMESTAMP = "timestamp";
	private static final String STATS_KEY_NUMBER = "number";
	private static final String STATS_KEY_ISCORRECT = "correct";

	private DbOpenHelper mDbHelper;
	private SQLiteDatabase mDb;

	private final Context mCtx;

	private class DbOpenHelper extends SQLiteOpenHelper {

		private static final String LIST_TABLE_CREATE = "CREATE TABLE " + LIST_TABLE_NAME + " ("
				+ KEY_ID + " INTEGER PRIMARY KEY, " + KEY_SYMBOL + " TEXT, " + KEY_PICURL
				+ " TEXT);";

		private static final String STATS_TABLE_CREATE = "CREATE TABLE " + STATS_TABLE_NAME + " ("
				+ KEY_ID + " INTEGER PRIMARY KEY, " + STATS_KEY_TIMESTAMP
				+ " TEXT, " + STATS_KEY_NUMBER + " TEXT, " + STATS_KEY_ISCORRECT + " INTEGER);";

		public DbOpenHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(LIST_TABLE_CREATE);

			for (int i = 1; i <= 100; i++) {
				db.execSQL("INSERT INTO " + LIST_TABLE_NAME + " VALUES(" + i + ", '', '')");
			}

			db.execSQL(STATS_TABLE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(Constants.LOG_TAG, "Upgrading database from version " + oldVersion + " to " + newVersion
					+ ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + LIST_TABLE_NAME);
			db.execSQL("DROP TABLE IF EXISTS " + STATS_TABLE_NAME);
			onCreate(db);

		}

	}

	public DbAdapter(Context context) {
		this.mCtx = context;
	}

	public DbAdapter open() throws SQLException {
		mDbHelper = new DbOpenHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		mDbHelper.close();
	}

	public long createListItem(int id, String symbol, String picurl) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_ID, id);
		initialValues.put(KEY_SYMBOL, symbol);
		initialValues.put(KEY_PICURL, picurl);

		return mDb.insert(LIST_TABLE_NAME, null, initialValues);
	}

	public boolean deleteListItem(long id) {
		return mDb.delete(LIST_TABLE_NAME, KEY_ID + "=" + id, null) > 0;
	}

	public boolean deleteAllItems() {
		return mDb.delete(LIST_TABLE_NAME, null, null) > 0;
	}

	public boolean updateNote(int id, String symbol, String picurl) {
		ContentValues args = new ContentValues();
		args.put(KEY_ID, id);
		args.put(KEY_SYMBOL, symbol);
		args.put(KEY_PICURL, picurl);

		return mDb.update(LIST_TABLE_NAME, args, KEY_ID + "=" + id, null) > 0;
	}

	/**
	 * Return a Cursor over the list of all symbols in the database
	 * 
	 * @return Cursor over all symbols
	 */
	public Cursor fetchAllNotes() {

		return mDb.query(LIST_TABLE_NAME, new String[] { KEY_ID, KEY_SYMBOL, KEY_PICURL }, null,
				null, null, null, null);
	}

	/**
	 * Return a Cursor positioned at the list item that matches the given id
	 * 
	 * @param id
	 *            id of list item to retrieve
	 * @return Cursor positioned to matching item, if found
	 * @throws SQLException
	 *             if item could not be found/retrieved
	 */
	public Cursor fetchListItem(int id) throws SQLException {

		Cursor mCursor = mDb.query(true, LIST_TABLE_NAME, new String[] { KEY_ID, KEY_SYMBOL,
				KEY_PICURL }, KEY_ID + "=" + id, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}
	
	
	public long createStatsItem(String number, boolean isCorrect) {
		// http://www.sqlite.org/datatype3.html
		// TEXT as ISO8601 strings ("YYYY-MM-DD HH:MM:SS.SSS") 
		Date now = new Date();// TODO: fix timezone 'bug'
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		String datetime = dateFormat.format(now);
		Log.v(Constants.LOG_TAG, "timestamp for statistics: " + datetime);
		
		ContentValues initialValues = new ContentValues();
//		initialValues.put(KEY_ID, id);//autoincrement
		initialValues.put(STATS_KEY_TIMESTAMP, datetime);
		initialValues.put(STATS_KEY_NUMBER, number);
		initialValues.put(STATS_KEY_ISCORRECT, isCorrect);

		return mDb.insert(STATS_TABLE_NAME, null, initialValues);
	}
	
	public Cursor fetchAllStats() {
		return mDb.query(STATS_TABLE_NAME, new String[] { KEY_ID, STATS_KEY_TIMESTAMP, STATS_KEY_NUMBER, STATS_KEY_ISCORRECT }, null,
				null, null, null, null);
	}

}
