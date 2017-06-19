package com.example.readcalendar;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class PhoneCallListener extends PhoneStateListener {

    private boolean isPhoneCalling = false;
    public boolean isPhoneRinging = false;
    public String contactName;

    public static Activity mainActivity;
    
	public void getActivity(Activity activity) {
	  mainActivity = activity;
	}
    @Override
    public void onCallStateChanged(int state, String incomingNumber) {

    	
        if (TelephonyManager.CALL_STATE_RINGING == state) {
            // phone ringing
        	System.out.println("RINGING, number: " + incomingNumber);
        	isPhoneRinging = true;
        	//Get the phone name from directory if it exists
        	contactName =  getContactName(incomingNumber);
        	System.out.println("&&&contact name:" +contactName);
        	DialogTools.isCallingProgress(true, incomingNumber, contactName);
        	DialogTools.writeToFile(mainActivity, MainActivity.calendarName);
        }

        if (TelephonyManager.CALL_STATE_OFFHOOK == state) {
            // active
        	System.out.println("OFFHOOK");
        	DialogTools.isCallingProgress(false, incomingNumber, contactName);
        	DialogTools.writeToFile(mainActivity, MainActivity.calendarName);

            isPhoneCalling = true;
        }
        
        if (TelephonyManager.CALL_STATE_IDLE == state) {
            // active
        	System.out.println("CALL_STATE_IDLE");
        	DialogTools.isCallingProgress(false, incomingNumber, contactName);
        	DialogTools.writeToFile(mainActivity, MainActivity.calendarName);
            isPhoneCalling = true;
        }
    
    }
    
    public static String getContactName(final String phoneNumber) 
    {  
    	String name = null;

        // define the columns I want the query to return
        String[] projection = new String[] {
                ContactsContract.PhoneLookup.DISPLAY_NAME,
                ContactsContract.PhoneLookup._ID};

        // encode the phone number and build the filter URI
        Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));

        // query time
        Cursor cursor = mainActivity.getContentResolver().query(contactUri, projection, null, null, null);

        if(cursor != null) {
            if (cursor.moveToFirst()) {
                name =      cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
                System.out.println("Started uploadcontactphoto: Contact Found @ " + phoneNumber);            
                System.out.println("Started uploadcontactphoto: Contact name  = " + name);
            } else {
            	System.out.println("Contact Not Found @ " + phoneNumber);
            }
            cursor.close();
        }
        return name;
   }
}