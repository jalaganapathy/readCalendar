package com.example.readcalendar;
import java.util.Date;
import java.util.HashSet;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;


public class retrieveCalendar {
	static HashSet<String> calendarIds = new HashSet<String>();
	static HashSet<String> calendarNames = new HashSet<String>();
	
public static void readCalendarAccount(Context context) {
		
		ContentResolver contentResolver = context.getContentResolver();

		// Fetch a list of all calendars synced with the device, their display names and whether the
		// user has them selected for display.
		final Cursor cursor = contentResolver.query(Uri.parse("content://com.android.calendar/calendars"),
				(new String[] { "_id", "account_name"}), null, null, null);

		while (cursor.moveToNext()) {

			final String _id = cursor.getString(0);
			final String displayName = cursor.getString(1);
			calendarIds.add(_id);
			calendarNames.add(displayName);
		}
		
}

}

