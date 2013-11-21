package com.example.sonysignin;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import android.annotation.SuppressLint;
// Helper functions class
import android.os.Environment;

public class Util
{
	/* Checks if external storage is available for read and write */
	public static boolean isExternalStorageWritable()
	{
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state))
		{
			return true;
		}
		return false;
	}
	
	public static SignIn parseData(String rawList)
	{
		SignIn response = new SignIn();
		String delims = "[\n]";
		String[] tokens = rawList.split(delims);
		
		response.setName(tokens[1].replace("Name: ", ""));
		response.setSeeking(tokens[2].replace("Seeking: ", ""));
		response.setCompany(tokens[3].replace("Company: ", ""));
		response.setTimeIn(tokens[4].replace("Sign in time: ", ""));
		
		response.setTimeOut("NULL");
		return response;
	}
	
	@SuppressLint("SimpleDateFormat")
	public static String getCurrentTime()
	{
		return new SimpleDateFormat("MMM-dd-yyyy h:mm aa").format(Calendar.getInstance().getTime());
	}
}
