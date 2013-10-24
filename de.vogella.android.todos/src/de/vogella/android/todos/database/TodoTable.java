package de.vogella.android.todos.database;


import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class TodoTable {

	// Database table
	public static final String TABLE_TODO = "todo";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_TITLE = "title";
	public static final String COLUMN_DESCRIPTION = "description";
	public static final String COLUMN_PHOTO_URI = "photo_uri";
	public static final String COLUMN_EVENT_ID = "event_id";
	public static final String COLUMN_DATE = "date";
	
	// Database creation SQL statement
	private static final String DATABASE_CREATE = "create table " 
			+ TABLE_TODO
			+ "(" + COLUMN_ID + " integer primary key autoincrement, " 
			+ COLUMN_TITLE + " text not null, " 
			+ COLUMN_DESCRIPTION + " text," 
			+ COLUMN_PHOTO_URI + " text not null," 
			+ COLUMN_DATE + " date not null," 
			+ COLUMN_EVENT_ID + " text not null" + ");";

	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}

	public static void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		Log.w(TodoTable.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_TODO);
		onCreate(database);
	}
}
