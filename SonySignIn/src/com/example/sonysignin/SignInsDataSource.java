package com.example.sonysignin;

import java.util.ArrayList;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class SignInsDataSource
{
	// Database fields
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	public String[] allColumns =
	{ MySQLiteHelper.COLUMN_NAME, MySQLiteHelper.COLUMN_COMPANY, MySQLiteHelper.COLUMN_SEEKING,
			MySQLiteHelper.COLUMN_TIMEIN, MySQLiteHelper.COLUMN_TIMEOUT, };

	// Constructor
	public SignInsDataSource(Context context)
	{
		dbHelper = new MySQLiteHelper(context);
	}

	// Open connection to database
	public void open() throws SQLException
	{
		database = dbHelper.getWritableDatabase();
	}

	// Terminate connection to database
	public void close()
	{
		dbHelper.close();
	}

	// This does an insert
	public void createSignIn(SignIn record)
	{
		System.out.println("Updating database...");

		ContentValues values = new ContentValues();

		values.put(MySQLiteHelper.COLUMN_NAME, record.getName());
		values.put(MySQLiteHelper.COLUMN_COMPANY, record.getCompany());
		values.put(MySQLiteHelper.COLUMN_SEEKING, record.getSeeking());
		values.put(MySQLiteHelper.COLUMN_TIMEIN, record.getTimeIn());
		values.put(MySQLiteHelper.COLUMN_TIMEOUT, record.getTimeOut());

		open();

		database.insert(MySQLiteHelper.TABLE_NAME, null, values);
	}

	public void deleteAll()
	{
		open();
		database.delete(MySQLiteHelper.TABLE_NAME, null, null);
	}

	public ArrayList<SignIn> getAllSignIns()
	{
		// Create array list of comments to return
		ArrayList<SignIn> sign_ins = new ArrayList<SignIn>();

		// Open connection to database
		open();

		// Cursor to point to beginning of what query returns
		Cursor cursor = database.query(MySQLiteHelper.TABLE_NAME, allColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast())
		{
			SignIn sign_in = cursorToItem(cursor);
			sign_ins.add(sign_in);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return sign_ins;
	}

	public ArrayList<SignIn> runQuery(String[] tableColumns, String whereClause, String[] whereArgs, String orderBy)
	{
		// Create array list of comments to return
		ArrayList<SignIn> sign_ins = new ArrayList<SignIn>();

		// Open connection to database
		open();

		// Cursor to point to beginning of what query returns
		Cursor cursor = database.query(MySQLiteHelper.TABLE_NAME, tableColumns, whereClause, whereArgs, null, null, orderBy);

		cursor.moveToFirst();
		while (!cursor.isAfterLast())
		{
			SignIn sign_in = cursorToItem(cursor);
			sign_ins.add(sign_in);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return sign_ins;
	}
	
	public void execQuery(String query)
	{
		database.execSQL(query);
	}

	// Creates a comment object to be returned
	private SignIn cursorToItem(Cursor cursor)
	{
		SignIn sign_in = new SignIn(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3),
				cursor.getString(4));
		return sign_in;
	}
}