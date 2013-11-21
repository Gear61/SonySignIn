package com.example.sonysignin;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends Activity
{
	final Context context = this;
	SignInsDataSource datasource = new SignInsDataSource(context);
	boolean locked = true;
	String revert = "";
	StableArrayAdapter adapter;
	ArrayList<String> list;

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
	
	// Dialog that appears upon trying to checkout
	// If yes is selected, the timeout for that record is set to current time and the item disappears from list
	// If no is selected, nothing happens
	public void showConfirmationDialog(String message, final Context context, final View view, final String item)
	{
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

		// set dialog message
		alertDialogBuilder.setMessage(message).setCancelable(false)
				.setPositiveButton("No", new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int whichButton)
					{
						dialog.dismiss();
					}
				}).setNegativeButton("Yes", new DialogInterface.OnClickListener()
				{
					public void onClick(final DialogInterface dialog, int id)
					{
						final SignIn signin = Util.parseData(item);
						view.animate().setDuration(2000).alpha(0).withEndAction(new Runnable()
						{
							@SuppressLint("SimpleDateFormat")
							@Override
							public void run()
							{
								String time_out = Util.getCurrentTime();
								
								String query = "UPDATE " + MySQLiteHelper.TABLE_NAME;
								query += " SET " + MySQLiteHelper.COLUMN_TIMEOUT + "=" + "\"" + time_out + "\"";
								query += " WHERE " + MySQLiteHelper.COLUMN_TIMEIN + "=" + "\"" + signin.getTimeIn() + "\"";
								query += " AND " + MySQLiteHelper.COLUMN_NAME + "=" + "\"" + signin.getName() + "\"";
								query += " AND " + MySQLiteHelper.COLUMN_SEEKING + "=" + "\"" + signin.getSeeking() + "\"";
								query += " AND " + MySQLiteHelper.COLUMN_COMPANY + "=" + "\"" + signin.getCompany() + "\"";
								query += " AND " + MySQLiteHelper.COLUMN_TIMEOUT + "=" + "\"" + signin.getTimeOut() + "\"";
								
								System.out.println(query);
								
								datasource.execQuery(query);
								
								list.remove(item);
								adapter.notifyDataSetChanged();
								view.setAlpha(1);
								dialog.cancel();
								
								showDialog("You have successfully checked out.", context);
							}
						});
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
	}

	// Function used to clear the form after sign in
	public void clearForm()
	{
		EditText editName = (EditText) findViewById(R.id.name);
		EditText editCompany = (EditText) findViewById(R.id.company);
		EditText editSeeking = (EditText) findViewById(R.id.seeking);

		// Clear entered in information
		editName.setText("");
		editCompany.setText("");
		editSeeking.setText("");
	}

	@SuppressLint("SimpleDateFormat")
	public void signIn(View view)
	{
		// Hide keyboard after submitting sign in
		InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

		// Grab all user inputted parameters
		EditText editName = (EditText) findViewById(R.id.name);
		String name = editName.getText().toString();

		EditText editCompany = (EditText) findViewById(R.id.company);
		String company = editCompany.getText().toString();

		EditText editSeeking = (EditText) findViewById(R.id.seeking);
		String seeking = editSeeking.getText().toString();
		
		// If entered string is empty string or entirely white space, complain
		if (name.trim().length() == 0)
		{
			showDialog("Please enter a name.", context);
			return;
		}
		if (company.trim().length() == 0)
		{
			showDialog("Please tell us which company you represent.", context);
			return;
		}
		if (seeking.trim().length() == 0)
		{
			showDialog("Please tell us who you came here to see.", context);
			return;
		}

		String time_in = Util.getCurrentTime();
		SignIn sign_in = new SignIn(name, company, seeking, time_in, "NULL");

		// Insert new sign in record into database
		datasource.createSignIn(sign_in);

		showDialog("You have successfully signed in.", context);
				
		// Clear entered in information
		clearForm();

		setContentView(R.layout.activity_main);
	}

	@SuppressLint("SimpleDateFormat")
	public void sendData(View view)
	{
		// Create folder in external storage for us to store things in
		// Check if SD card is mounted
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
		{
			File Dir = new File(android.os.Environment.getExternalStorageDirectory(), "SonySignIns");
			if (!Dir.exists()) // if directory is not here
			{
				Dir.mkdirs(); // make directory
			}
		}

		// Create .csv file
		if (Util.isExternalStorageWritable())
		{
			File file = new File(Environment.getExternalStorageDirectory().getPath() + "/SonySignIns/", "records.csv");

			if (file.exists())
			{
				file.delete();
			}

			try
			{
				FileWriter fWriter = new FileWriter(Environment.getExternalStorageDirectory().getPath()
						+ "/SonySignIns/records.csv");

				String allRecords = "";

				// Row defining each column
				allRecords += "Name, Company, Seeking, Time In, Time Out\n";

				ArrayList<SignIn> sign_ins = datasource.getAllSignIns();
				for (int i = 0; i < sign_ins.size(); i++)
				{
					allRecords += sign_ins.get(i).getName() + ",";
					allRecords += sign_ins.get(i).getCompany() + ",";
					allRecords += sign_ins.get(i).getSeeking() + ",";
					allRecords += sign_ins.get(i).getTimeIn() + ",";
					allRecords += sign_ins.get(i).getTimeOut() + ",";
				}

				fWriter.write(allRecords);
				fWriter.close();
			}
			catch (Exception e)
			{
				showDialog("Creating a .csv file in external memory failed.", context);
				setContentView(R.layout.activity_main);
				return;
			}

			// MY CODE HURR

			Uri u1 = null;
			u1 = Uri.fromFile(file);

			String currentTime = new SimpleDateFormat("MMM-dd-yy HH:mm:ss").format(Calendar.getInstance().getTime());

			Intent sendIntent = new Intent(Intent.ACTION_SEND);
			sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Sign-in Record as of " + currentTime);
			sendIntent.putExtra(Intent.EXTRA_STREAM, u1);
			sendIntent.setType("text/html");
			startActivity(sendIntent);
		}
	}
	
	public void initTable(TableLayout table)
	{
		TableLayout.LayoutParams params1 = 
				new TableLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 0.1f);
		
		TextView text = new TextView(this);
        text.setText("Name");
        text.setLayoutParams(params1);
        text.setTypeface(null, Typeface.BOLD);
        
        TextView text2 = new TextView(this);
        text2.setText("Company");
        text2.setLayoutParams(params1);
        text2.setTypeface(null, Typeface.BOLD);
        
        TextView text3 = new TextView(this);
        text3.setText("Seeking");
        text3.setLayoutParams(params1);
        text3.setTypeface(null, Typeface.BOLD);
        
        TextView text4 = new TextView(this);
        text4.setText("Time In");
        text4.setLayoutParams(params1);
        text4.setTypeface(null, Typeface.BOLD);
        
        TextView text5 = new TextView(this);
        text5.setText("Time Out");
        text5.setLayoutParams(params1);
        text5.setTypeface(null, Typeface.BOLD);
        
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.addView(text);
        layout.addView(text2);
        layout.addView(text3);
		layout.addView(text4);
		layout.addView(text5);
		
		// add the TableRow to the TableLayout
		table.addView(layout);
	}

	public void viewTable(View view)
	{
		setContentView(R.layout.view_data);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		revert = "admin";

		// get a reference for the TableLayout
		TableLayout table = (TableLayout) findViewById(R.id.my_table_layout);
		
		initTable(table);

		ArrayList<SignIn> sign_ins = datasource.getAllSignIns();
		for (int i = 0; i < sign_ins.size(); i++)
		{
			TableLayout.LayoutParams params1 = 
					new TableLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 0.1f);
	 
	        TextView text = new TextView(this);
	        text.setText(sign_ins.get(i).getName());
	        text.setLayoutParams(params1);
	        
	        TextView text2 = new TextView(this);
	        text2.setText(sign_ins.get(i).getCompany());
	        text2.setLayoutParams(params1);
	        
	        TextView text3 = new TextView(this);
	        text3.setText(sign_ins.get(i).getSeeking());
	        text3.setLayoutParams(params1);
	        
	        TextView text4 = new TextView(this);
	        text4.setText(sign_ins.get(i).getTimeIn());
	        text4.setLayoutParams(params1);
	        
	        TextView text5 = new TextView(this);
	        text5.setText(sign_ins.get(i).getTimeOut());
	        text5.setLayoutParams(params1);
	        
	        LinearLayout layout = new LinearLayout(this);
	        layout.setOrientation(LinearLayout.HORIZONTAL);
	        layout.setPadding(0, 10, 0, 10);
	        layout.addView(text);
	        layout.addView(text2);
	        layout.addView(text3);
			layout.addView(text4);
			layout.addView(text5);
	        
			// add the TableRow to the TableLayout
			table.addView(layout);
		}
	}

	public void deleteAll(View view)
	{
		datasource.deleteAll();
		showDialog("You have successfully deleted all stored records.", context);
	}

	public void admin(View view)
	{
		setContentView(R.layout.admin);
	}

	public void switchBack(View view)
	{
		setContentView(R.layout.activity_main);
	}
	
	public void checkOut(View view)
	{
		// Enable back button
		getActionBar().setDisplayHomeAsUpEnabled(true);

		String[] tableColumns = datasource.allColumns;
		String whereClause = MySQLiteHelper.COLUMN_TIMEOUT + " = ?";
		String[] whereArgs = new String[] { "NULL" };
		String orderBy = MySQLiteHelper.COLUMN_TIMEIN;

		// Set variable so we know how to redirect when back button is pressed
		revert = "main";
		
		// Go to list view page
		setContentView(R.layout.checkout);

		final ListView listview = (ListView) findViewById(R.id.listview);

		final ArrayList<SignIn> signins = datasource.runQuery(tableColumns, whereClause, whereArgs, orderBy);
		list = new ArrayList<String>();
		for (int i = 0; i < signins.size(); ++i)
		{
			String insert = "\nName: " + signins.get(i).getName() + "\n";
			insert += "Seeking: " + signins.get(i).getSeeking() + "\n";
			insert += "Company: " + signins.get(i).getCompany() + "\n";
			insert += "Sign in time: " + signins.get(i).getTimeIn() + "\n";
			list.add(insert);
		}
		
		adapter = new StableArrayAdapter(this, android.R.layout.simple_list_item_1, list);
		listview.setAdapter(adapter);

		listview.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, final View view, int position, long id)
			{
				final String item = (String) parent.getItemAtPosition(position);
				SignIn signin = Util.parseData(item);
				String message = "Are you sure you want to check out for " + signin.getName();
				message += " who came in to see " + signin.getSeeking() + " and represents " + signin.getCompany() + "?";
				showConfirmationDialog(message, context, view, item);
			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case android.R.id.home:
				if (revert.equals("admin"))
				{
					setContentView(R.layout.admin);
				}
				if (revert.equals("main"))
				{
					setContentView(R.layout.activity_main);
				}
				getActionBar().setDisplayHomeAsUpEnabled(false);
				break;
			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	}
}
