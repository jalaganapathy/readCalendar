package com.example.readcalendar;


import android.os.Environment;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.format.DateUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.database.Cursor;
import android.util.Xml;
import org.xmlpull.v1.XmlSerializer;
import java.lang.Object;
import java.util.Date;
import java.io.FileOutputStream;
import java.io.File;
import android.net.Uri;

public class DialogTools {

	public static String fileName;
	private static boolean bCallProgress = false;
	private static String inNumber="";
	private static String inName ="";
	private static boolean bSmsProgress = false;
	private static String inSmsNumber = "";
	private static String inMsgBody = "";
	private static String inSmsName = "";
	
	private DialogTools() {

	}
	
	
	public static void questionDialog(Activity activity, int titleResource, int messageResource, int okResource,
			String input, boolean cancelable, int drawableResource, boolean password) {
		 questionDialog(activity, activity.getString(titleResource), activity.getString(messageResource),
				activity.getString(okResource), input, cancelable, drawableResource, password);
	}

	public static void questionDialog(final Activity activity, final CharSequence title, final CharSequence message,
			final CharSequence ok, final String input, final boolean cancelable, final int drawableResource,
			final boolean password) {
		
		final String[] result = new String[2];
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				final Dialog dialog = new Dialog(activity);
				dialog.setTitle(title);
				dialog.requestWindowFeature(Window.FEATURE_LEFT_ICON);

				LinearLayout layout = new LinearLayout(activity);
				layout.setOrientation(LinearLayout.VERTICAL);
				layout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
				layout.setMinimumWidth(300);

				TextView view = new TextView(activity);
				view.setPadding(10, 10, 10, 10);
				view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
				view.setTextSize(16);
				layout.addView(view);
				view.setText(message);

				final EditText editText = new EditText(activity);
				if (password) {
					editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
				} else {
					editText.setSingleLine();
				}
				editText.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
				if (input != null) {
					editText.setText(input);
				}
				layout.addView(editText);

				
				LinearLayout buttons = new LinearLayout(activity);
				buttons.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
				buttons.setBackgroundColor(Color.GRAY);
				buttons.setOrientation(LinearLayout.HORIZONTAL);
				
				layout.addView(buttons);
				
				Button button = new Button(activity);
				button.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
				button.setGravity(Gravity.CENTER_HORIZONTAL);
				button.setText(ok);
				button.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						result[0] = editText.getText().toString();
						result[1] = "";
						//Call a function to export the events to a file
						fileName = result[0];
						dialog.cancel();
					}
				});

				buttons.addView(button);

				if (cancelable) {
					button = new Button(activity);
					button.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
					button.setGravity(Gravity.CENTER_HORIZONTAL);
					button.setText("Cancel");
					button.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							fileName = "";
							result[1] = "";
							dialog.cancel();
						}
					});
				}

				buttons.addView(button);

				dialog.setCancelable(cancelable);
				dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

					@Override
					public void onCancel(DialogInterface dialog) {
						result[1] = "";
					}
				});

				dialog.setContentView(layout);
				dialog.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, drawableResource);
				dialog.show();
			}
		});
		
		
	}
	
	public static void isCallingProgress(boolean bVal, String incomingNumber, String name)
	{
		bCallProgress = bVal;
		inNumber = incomingNumber;
		inName = name;
	}
	
	public static void isMessageProgress(boolean bVal, String incomingNumber, String msgBody, String name) {
		bSmsProgress = bVal;
		inSmsNumber = incomingNumber;
		inMsgBody = msgBody;
		inSmsName = name;
	}
	public static void writeToFile(Activity activity, String calName)
	{
		ContentResolver contentResolver = activity.getContentResolver();
		boolean success = false;
		File sdCardFile = null;
		
		try {
			File folder = new File(Environment.getExternalStorageDirectory() + "/readcalendar");
			
			try {
				if (!folder.exists()) {
				    success = folder.mkdir();
				    if(success) {
				    	sdCardFile = new File(Environment.getExternalStorageDirectory() + "/readcalendar"+"/calendar.xml");
				    }
				}
				else {
					sdCardFile = new File(Environment.getExternalStorageDirectory() + "/readcalendar"+"/calendar.xml");
				}
			}
			catch (Exception e) {
		    	System.out.println("#IO Exception#" + e);
		    }
			
			sdCardFile.createNewFile();
			FileOutputStream fileos = new FileOutputStream(sdCardFile, false);
			
			int iterName = 0;
			Object[] calNameArray = retrieveCalendar.calendarNames.toArray();
			for(int i = 0; i < calNameArray.length; i++) {
				if(calNameArray[i] == calName) {
					iterName = i;
				}
			}
			
			Object[] calIdArray = retrieveCalendar.calendarIds.toArray();
			String calId = calIdArray[iterName].toString();
			
			for (String id : retrieveCalendar.calendarIds) {
				if(id == calId) {
				Uri.Builder builder = Uri.parse("content://com.android.calendar/instances/when").buildUpon();
				long now = new Date().getTime();
				ContentUris.appendId(builder, now - DateUtils.HOUR_IN_MILLIS);
				ContentUris.appendId(builder, now + DateUtils.HOUR_IN_MILLIS);
				
				String selection = "calendar_id=" +id;

				Cursor eventCursor = contentResolver.query(builder.build(),
						new String[] { "title", "dtstart", "dtend", "allDay", "organizer","eventLocation"},
						selection, null, "startDay ASC, startMinute ASC"); 
				int loop = 1;
				XmlSerializer serializer = null;
				boolean anyData = false;
				//Enter the while loop and write data to xml file
				//if there is any event or if any call is in progress
				System.out.println("****cursor size" + eventCursor.getCount());
				while ((eventCursor.moveToNext()) || (bCallProgress == true) || (bSmsProgress == true)) {
					String title = null;
					Date begin = null;
					Date end = null;
					String organizer = null;
					Boolean allDay = null;
					String eventLocation = null;
					
					if(eventCursor.getCount() > 0) {
						title = eventCursor.getString(0);
						begin = new Date(eventCursor.getLong(1));
						end = new Date(eventCursor.getLong(2));
						allDay = !eventCursor.getString(3).equals("0");
						organizer = eventCursor.getString(4);
						eventLocation = eventCursor.getString(5);
					}
					
					System.out.println("Title: " + title + " Begin: " + begin + " End: " + end +
							" All Day: " + allDay + "organizer:" + organizer + "eventLocation:" + eventLocation);
					if(loop ==1) {
						serializer = Xml.newSerializer();
						serializer.setOutput(fileos, "UTF-8");
						serializer.startDocument(null, true);
					}
				    
			        serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
			        if(loop == 1) {
			        	serializer.startTag("", "calendar");
			        	serializer.startTag("", "frequency");
				        serializer.text("360");
				        serializer.endTag("", "frequency");
				        loop = 2;
			        }
			        //update the file when there is incoming call
			        System.out.println("%%%%bCall" + bCallProgress);
			        if(bCallProgress == true) {
			        	System.out.println("Hellooooo%%%%%%%%%%%%%%%%%");
			        	//Date start = new Date();
			        	serializer.startTag("", "incomingCall");
			        	serializer.startTag("", "number");
			        	serializer.text(inNumber);
				        serializer.endTag("", "number");
				        serializer.startTag("", "name");
				        if(inName != null) {
			        	  serializer.text(inName);
				        }
				        else {
				        	serializer.text(" ");
				        }
				        serializer.endTag("", "name");
				        //date
				        /*serializer.startTag("", "startcalltime");
				        serializer.text(start.toString());
				        serializer.endTag("", "startcalltime");*/
				        serializer.endTag("", "incomingCall");
				        bCallProgress = false;
			        }
			        
			        //Update the file if there is incoming sms
			        System.out.println("%%%%bMsg" + bSmsProgress);
			        if(bSmsProgress == true) {
			        //	Date start = new Date();
			        	System.out.println("Inside sms***");
			        	serializer.startTag("", "incomingMessage");
			        	//Update Number
			        	serializer.startTag("", "msgnumber");
			        	serializer.text(inSmsNumber);
				        serializer.endTag("", "msgnumber");
				        
				        System.out.println("Name inside if cond is :" + inSmsName);
				        //Update Name
				        serializer.startTag("", "msgname");
				        if(inSmsName != null) {
			        	  serializer.text(inSmsName);
				        }
				        else {
				        	serializer.text(" ");
				        }
				        serializer.endTag("", "msgname");
				        
				        //Update Text
				        serializer.startTag("", "msgbody");
			        	serializer.text(inMsgBody);
				        serializer.endTag("", "msgbody");
				        
				      //date
				      /*  serializer.startTag("", "startmsgtime");
				        serializer.text(start.toString());
				        serializer.endTag("", "startmsgtime");*/
				        
				        serializer.endTag("", "incomingMessage");
				        bSmsProgress = false;
			        }
			        
			        
			        serializer.startTag("", "event");
			        serializer.startTag("", "title");
			        if(title != null) {
			          serializer.text(title);
			        }
			        else {
			        	serializer.text(" ");
			        }
			        serializer.endTag("", "title");
			        serializer.startTag("", "startTime");
			        if(begin != null) {
			          serializer.text(begin.toString());
			        }
			        else {
			        	serializer.text(" ");
			        }
			        serializer.endTag("", "startTime");
			        serializer.startTag("", "endTime");
			        if(end != null) {
			          serializer.text(end.toString());
			        }
			        else {
			        	serializer.text(" ");
			        }
			        serializer.endTag("","endTime");
			        serializer.startTag("", "allDay");
			        if(allDay != null) {
			          serializer.text(allDay.toString());
			        }
			        else {
			        	serializer.text(" ");
			        }
			        serializer.endTag("","allDay");
			        serializer.startTag("", "organizer");
			        if(organizer != null){
			          serializer.text(organizer);
			        }
			        else {
			        	serializer.text(" ");
			        }
			        serializer.endTag("","organizer");
			        serializer.startTag("", "eventLocation");
			        if(eventLocation != null) {
			           serializer.text(eventLocation);
			        }
			        else {
			        	serializer.text(" ");
			        }
			        serializer.endTag("","eventLocation");
			        serializer.endTag("","event");
			        //loop = 2;
			        anyData = true;
				}
				if(anyData) {
				  serializer.endTag("", "calendar");
				  serializer.endDocument();
		          serializer.flush();
				}
			  }	
			}

			fileos.close();
		}
		catch (Exception e) {
	    	System.out.println("# in reading calendar dATA Exception #" + e);
	    }
		
	}
	
}
