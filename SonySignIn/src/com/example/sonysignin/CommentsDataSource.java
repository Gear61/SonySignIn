package com.example.sonysignin;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class CommentsDataSource
{
	// Database fields
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	private String[] allColumns =
	{
			MySQLiteHelper.COLUMN_NAME,
			MySQLiteHelper.COLUMN_PRICE
	};

	// Constructor
	public CommentsDataSource(Context context)
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
	public Item createItem(Item item)
	{
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_NAME, item.getName());
		values.put(MySQLiteHelper.COLUMN_PRICE, item.getPrice());
		
		database.insert(MySQLiteHelper.TABLE_NAME, null, values);
		
		// Return the comment we just inserted to update the list view
		return item;
	}

	public void deleteComment(Comment comment)
	{
		long id = comment.getId();
		System.out.println("Comment deleted with id: " + id);
		
		// Delete comment with given ID. Just deletes top-most one
		database.delete(MySQLiteHelper.TABLE_NAME, MySQLiteHelper.COLUMN_NAME + " = " + id, null);
	}

	public ArrayList<Item> getAllItems()
	{
		// Create array list of comments to return
		ArrayList<Item> items = new ArrayList<Item>();
		
		// Cursor to point to beginning of what query returns
		Cursor cursor = database.query(MySQLiteHelper.TABLE_NAME, allColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast())
		{
			Item item = cursorToItem(cursor);
			items.add(item);
			System.out.println(item.getName() + ": " + item.getPrice());
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return items;
	}

	// Creates a comment object to be returned
	private Item cursorToItem(Cursor cursor)
	{
		Item item = new Item(cursor.getString(1), cursor.getString(0));
		return item;
	}
}