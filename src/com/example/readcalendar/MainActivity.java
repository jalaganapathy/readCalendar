package com.example.readcalendar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import java.io.File;
import java.io.FileOutputStream;


public class MainActivity extends Activity implements OnItemSelectedListener {

	private receiveSms mSMSreceiver;
    private IntentFilter mIntentFilter;
	public static String calendarName; 
	public static boolean isSyncActive = false;
	
	Timer myTimer = new Timer();
	syncCal myTimerTask= new syncCal();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Spinner spinner = (Spinner) findViewById(R.id.planets_spinner);
		// Create an ArrayAdapter using the string array and a default spinner layout
		List<String> planets = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.planets_array)));
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, planets);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(this);
		retrieveCalendar.readCalendarAccount(this);
		for (String calName: retrieveCalendar.calendarNames) {
			adapter.add(calName);
		}
				
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void onItemSelected(AdapterView<?> parent, View view, 
            int pos, long id) {
        // An item was selected. You can retrieve the selected item using
		calendarName = parent.getItemAtPosition(pos).toString();
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    /** Called when the user clicks the Export button */
    public void sendExport(View view) {
    	DialogTools.writeToFile(this, calendarName);
    }
    //Called when user clicks sync button
    public void syncCalendarData(View view) {

    	if(isSyncActive == false) {
	    	myTimerTask.getActivity(this);
	    	//It runs every 1 hour
	    	myTimer.scheduleAtFixedRate(myTimerTask, 0, 360000);
	    	
	    	//also sync and poll for incoming call
	    	 PhoneCallListener phoneListener = new PhoneCallListener();
	    	 phoneListener.getActivity(this);
	    	 TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(this.TELEPHONY_SERVICE);
	         telephonyManager.listen(phoneListener,
	    	            PhoneStateListener.LISTEN_CALL_STATE);
	         
	         //also sync incoming sms and poll for it
	         mSMSreceiver = new receiveSms();
	         mSMSreceiver.getActivity(this);
	         mIntentFilter = new IntentFilter();
	         mIntentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
	         registerReceiver(mSMSreceiver, mIntentFilter);
	         isSyncActive = true;
    	}
    	
    }
    
    public void onDestroy()
    {
    	System.out.println("Destroy called ***");
    	super.onDestroy();
        if(isSyncActive == true) {	
        	myTimer.cancel();
        	unregisterReceiver(mSMSreceiver);
        }
    }

   
}

