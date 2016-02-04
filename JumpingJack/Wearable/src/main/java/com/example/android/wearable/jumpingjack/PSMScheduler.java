package com.example.android.wearable.jumpingjack;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;


/**
 * Created by ruiwang on 8/26/15.
 */
public class PSMScheduler {

    public static void setSchedule(Context context) {
       /* boolean alarmUp = (PendingIntent.getBroadcast(context, 0,
                new Intent(context,EMAAlarmReceiver.class),
                PendingIntent.FLAG_NO_CREATE) != null);

        if (alarmUp) {
            Log.d("myTag", "Alarm is already active");
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis()+15*60*1000);

            setSchedule(context, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
            Log.d("TAGG", "Time set for --"+calendar.get(Calendar.MINUTE));

        }*/
        setSchedule(context,12,30,0);
        setSchedule(context, 18, 30, 0);
    }

    private static void setSchedule(Context context, int hour, int min, int sec) {

        // the request code distinguish different stress meter schedule instances
        int requestCode = hour * 10000 + min * 100 + sec;
        Intent intent = new Intent(context, EMAAlarmReceiver.class);

        PendingIntent pi = PendingIntent.getBroadcast(context, requestCode, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);
        calendar.set(Calendar.SECOND, sec);

        if(calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DATE, 1);
        }

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pi);
    }

    // start the stress meter
    private static void startPSM(Context context) {
        Intent emaIntent = new Intent(context, PSMFragment.class);
        emaIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(emaIntent);
    }
}
