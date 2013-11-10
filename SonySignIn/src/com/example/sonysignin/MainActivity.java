package com.example.sonysignin;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
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
	
	public void Admin(View view)
	{
		ArrayList<SignIn> sign_ins = datasource.getAllSignIns();
		for (int i = 0; i < sign_ins.size(); i++)
		{
			System.out.println("Name: " + sign_ins.get(i).getName());
			System.out.println("Company: " + sign_ins.get(i).getCompany());
			System.out.println("Seeking: " + sign_ins.get(i).getSeeking());
			System.out.println("Time in: " + sign_ins.get(i).getTimeIn());
			System.out.println("Time out: " + sign_ins.get(i).getTimeOut());
			System.out.println("Current time: " + sign_ins.get(i).getCurrent());
			System.out.println(" ");
		}
	}
}
