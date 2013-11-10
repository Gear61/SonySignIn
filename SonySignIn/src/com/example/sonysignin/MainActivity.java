package com.example.sonysignin;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;

public class MainActivity extends ListActivity
{
	final Context context = this;
	CommentsDataSource datasource;
	
	public void showDialog(String message, Context context)
	{
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

		// set dialog message
		alertDialogBuilder.setMessage(message).setCancelable(false)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int id)
					{
						// if this button is clicked, close
						// current activity
						dialog.cancel();
					}
				});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		datasource = new CommentsDataSource(this);
		datasource.open();

		ArrayList<Item> values = datasource.getAllItems();

		// use the SimpleCursorAdapter to show the
		// elements in a ListView
		ArrayAdapter<Item> adapter = new ArrayAdapter<Item>(this, android.R.layout.simple_list_item_1, values);
		setListAdapter(adapter);
	}

	// Will be called via the onClick attribute
	// of the buttons in main.xml
	public void goToAddPage(View view)
	{
		// setContentView(R.layout.add_item);
	}
	
	public void goToWatchlist(View view)
	{
		datasource.getAllItems();
	}
	
	public void addItem(View view)
	{
		EditText editName = (EditText) findViewById(R.id.item_name);
		EditText editPrice = (EditText) findViewById(R.id.item_price);
		String name = editName.getText().toString();
		double item_price = 0;
		
		try
		{
			item_price = Double.parseDouble(editPrice.getText().toString());
		}	
		catch(NumberFormatException e)
		{
			showDialog("Please enter a valid price.", context);
			return;
		}
		
		// If entered string is empty string or entirely white space, complain
		if (name.trim().length() == 0)
		{
			showDialog("Please enter an item name.", context);
			return;
		}
		if (item_price < 0)
		{
			showDialog("Please enter a non-negative item price.", context);
			return;
		}
		
		String stringPrice = editPrice.getText().toString();
		int integerPlaces = stringPrice.indexOf('.');
		if (integerPlaces != -1)
		{
			int decimalPlaces = stringPrice.length() - integerPlaces - 1;
			if (decimalPlaces > 2)
			{
				showDialog("Please enter a cent amount that is 2 or less digits long.", context);
				return;
			}
		}
		
		NumberFormat fmt = NumberFormat.getCurrencyInstance();
		String formattedPrice = fmt.format(item_price);
		
		@SuppressWarnings("unchecked")
		ArrayAdapter<Item> adapter = (ArrayAdapter<Item>) getListAdapter();
		Item item = new Item(formattedPrice, name);
		datasource.createItem(item);
		adapter.add(item);
		adapter.notifyDataSetChanged();
		
		showDialog("Item successfully added to watchlist.", context);
		setContentView(R.layout.activity_main);
	}
	
	@Override
	protected void onResume()
	{
		datasource.open();
		super.onResume();
	}

	@Override
	protected void onPause()
	{
		datasource.close();
		super.onPause();
	}
}
