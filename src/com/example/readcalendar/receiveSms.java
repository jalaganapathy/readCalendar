package com.example.readcalendar;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

public class receiveSms extends BroadcastReceiver
{
    private final String TAG = this.getClass().getSimpleName();
    public Activity mainActivity;
    
    public void getActivity(Activity activity) {
  	  mainActivity = activity;
  	}
    
    @Override
    public void onReceive(Context context, Intent intent)
    {
        Bundle extras = intent.getExtras();

        String strMessage = "";

        if ( extras != null )
        {
            Object[] smsextras = (Object[]) extras.get( "pdus" );

            for ( int i = 0; i < smsextras.length; i++ )
            {
                SmsMessage smsmsg = SmsMessage.createFromPdu((byte[])smsextras[i]);

                String strMsgBody = smsmsg.getMessageBody().toString();
                String strMsgSrc = smsmsg.getOriginatingAddress();

                strMessage += "SMS from " + strMsgSrc + " : " + strMsgBody;                    
                String contactName = PhoneCallListener.getContactName(strMsgSrc);

                DialogTools.isMessageProgress(true, strMsgSrc, strMsgBody, contactName);
                
                DialogTools.writeToFile(mainActivity, MainActivity.calendarName);
                Log.i(TAG, strMessage);

                System.out.println("SMS from " + strMsgSrc + " : " + strMsgBody);
            }

        }

    }

}