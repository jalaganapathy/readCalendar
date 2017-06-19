package com.example.readcalendar;

import java.util.TimerTask;

import android.app.Activity;


public class syncCal extends TimerTask {

	public Activity mainActivity;
	public void getActivity(Activity activity) {
	  mainActivity = activity;
	}
    public void run(){
     //your function // 
     
     DialogTools.writeToFile(mainActivity, MainActivity.calendarName);
   
    }

}