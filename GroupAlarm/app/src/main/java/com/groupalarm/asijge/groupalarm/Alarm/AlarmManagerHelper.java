package com.groupalarm.asijge.groupalarm.Alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.groupalarm.asijge.groupalarm.Data.Alarm;

import java.util.Calendar;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 *
 */
public class AlarmManagerHelper extends BroadcastReceiver {

    private static final String TAG = "AlarmManagerHelper";

    // A set that contain the alarms.
    private static Set<Alarm> alarms = new HashSet<Alarm>();

    public static void addAlarm(Alarm alarm) {
        Log.d(TAG, "Adding alarm with id: " + alarm.getId());
        alarms.add(alarm);
    }

    public static void removeAlarm(Alarm alarm) {
        Log.d(TAG, "Removing alarm with id: " + alarm.getId());
        alarms.remove(alarm);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        setAlarms(context);
    }

    public static void setAlarms(Context context) {
        Log.d(TAG, "Setting Alarms");
        cancelAlarms(context);

        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        for(Alarm a : alarms) {
            // Set the alarm, if enabled.
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, a.getHour());
            cal.set(Calendar.MINUTE, a.getMinute());
            cal.set(Calendar.SECOND, 0);

            List<Integer> days = a.getActiveDays();

            // find closest day to now.
            Calendar now = Calendar.getInstance();

            int distance = 7;
            // default day is the current day. If the hour has passed the alarm is
            // set for the next day.
            int day = cal.before(now) ? now.get(Calendar.DAY_OF_WEEK) + 1
                    : now.get(Calendar.DAY_OF_WEEK);

            for (int i : days) {
                int d = cal.get(Calendar.DAY_OF_WEEK) - now.get(Calendar.DAY_OF_WEEK);
                if (d < 0) {
                    d += 7; // next week.
                }
                if (d == 0 && (cal.get(Calendar.HOUR_OF_DAY) < now.get(Calendar.HOUR_OF_DAY)
                        || (cal.get(Calendar.HOUR_OF_DAY) == now.get(Calendar.HOUR_OF_DAY))
                        && cal.get(Calendar.MINUTE) < now.get(Calendar.MINUTE))) {
                    // same day
                    // earlier hour
                    // same hour, earlier minute
                    d += 7; // same day, earlier time. next week.
                }

                if (d < distance) { //least distance
                    day = (now.get(Calendar.DAY_OF_WEEK) + d) % 7;
                    distance = d;
                }
            }

            cal.set(Calendar.DAY_OF_WEEK, day);

            manager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
                    createIntent(context, a));
        }
    }

    public static void cancelAlarms(Context context) {
        Log.d(TAG, "Cancelling alarm");
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        for(Alarm a : alarms) {
            // Cancel the alarm.
            manager.cancel(createIntent(context, a));
        }
    }

    private static PendingIntent createIntent(Context context, Alarm alarm){
        Intent intent = new Intent(context, AlarmService.class);
        // put extra
        // start the service and "show" something when Alarm goes off.
        return PendingIntent.getService(context, alarm.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
