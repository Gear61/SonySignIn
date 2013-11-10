package com.example.sonysignin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import android.os.Bundle;
import android.os.Environment;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class MainActivity extends Activity
{
	final Context context = this;
	SignInsDataSource datasource = new SignInsDataSource(context);
	
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
	}
	
	public void clearForm()
	{
		EditText editName = (EditText) findViewById(R.id.name);
		EditText editCompany = (EditText) findViewById(R.id.company);
		EditText editSeeking = (EditText) findViewById(R.id.seeking);
		EditText editTimeIn = (EditText) findViewById(R.id.time_in);
		EditText editTimeOut = (EditText) findViewById(R.id.time_out);

		// Clear entered in information
		editName.setText("");
		editCompany.setText("");
		editSeeking.setText("");
		editTimeIn.setText("");
		editTimeOut.setText("");
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
		
		EditText editTimeIn = (EditText) findViewById(R.id.time_in);
		String time_in = editTimeIn.getText().toString();
		
		EditText editTimeOut = (EditText) findViewById(R.id.time_out);
		String time_out = editTimeOut.getText().toString();
		
		String currentTime = new SimpleDateFormat("MMM-dd-yy HH:mm:ss").format(Calendar.getInstance().getTime());
		
		SignIn sign_in = new SignIn(name, company, seeking,
									time_in, time_out, currentTime);
		
		datasource.createSignIn(sign_in);
		
		showDialog("You have successfully signed in.", context);

		// Clear entered in information
		clearForm();
		
		setContentView(R.layout.activity_main);
	}
	
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
			if (!file.exists())
			{
				try
				{
					FileWriter fWriter = new FileWriter(Environment.getExternalStorageDirectory().getPath()
							+ "/SonySignIns/records.csv");
					
					String allRecords = "";
					
					ArrayList<SignIn> sign_ins = datasource.getAllSignIns();
					for (int i = 0; i < sign_ins.size(); i++)
					{
						allRecords += sign_ins.get(i).getName() + ",";
						allRecords += sign_ins.get(i).getCompany() + ",";
						allRecords += sign_ins.get(i).getSeeking() + ",";
						allRecords += sign_ins.get(i).getTimeIn() + ",";
						allRecords += sign_ins.get(i).getTimeOut() + ",";
						allRecords += sign_ins.get(i).getCurrent() + "\n";
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
			}
			file.delete();
			showDialog("You have successfully emailed yourself all of the stored data in this app.", context);
		}
	}
	
	public void deleteAll(View view)
	{
		datasource.deleteAll();
		showDialog("You have successfully deleted all stored records.", context);
	}
	
	public void Admin(View view)
	{
		setContentView(R.layout.admin);
	}
	
	public void switchBack(View view)
	{
		setContentView(R.layout.activity_main);
	}
}
