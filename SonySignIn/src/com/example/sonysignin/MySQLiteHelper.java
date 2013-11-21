package com.example.sonysignin;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper
{
	// Table name
	public static final String TABLE_NAME = "SignIns";

	// COLUMNS
	// Item name column
	public static final String COLUMN_NAME = "Name";
	public static final String COLUMN_COMPANY = "Company";
	public static final String COLUMN_SEEKING = "Seeking";
	public static final String COLUMN_TIMEIN = "TimeIn";
	public static final String COLUMN_TIMEOUT = "TimeOut";

	// Some random things fed to a super's method
	private static final String DATABASE_NAME = "items.db";
	private static final int DATABASE_VERSION = 1;

	// Database creation sql statement
	static final String DATABASE_CREATE = "CREATE TABLE " + TABLE_NAME + "(" + COLUMN_NAME
			+ " TEXT, " + COLUMN_COMPANY + " TEXT, " + COLUMN_SEEKING + " TEXT, " 
			+ COLUMN_TIMEIN + " TEXT, " + COLUMN_TIMEOUT + " TEXT);";
	

	static final String DATABASE_DROP = "DROP TABLE " + TABLE_NAME;
	
	public MySQLiteHelper(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database)
	{
		database.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		Log.w(MySQLiteHelper.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(db);
	}
}
