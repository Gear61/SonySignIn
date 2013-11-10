package com.example.sonysignin;

//Helper functions class
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
}

