package com.groupalarm.asijge.groupalarm.AlarmManaging;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.groupalarm.asijge.groupalarm.Data.Alarm;

import java.util.Calendar;
import java.util.List;

/**
 *
 */
public class AlarmHelper extends BroadcastReceiver {

    private static final String TAG = "AlarmHelper";

    private static Alarm snoozeAlarm;

    public static List<Alarm> getAlarms() {
        List<Alarm> list = AlarmDB.getInstance().getAlarms();//new LinkedList<Alarm>();
//        for (Alarm a : alarms) {
//            list.add(a);
//            // TODO: Create copy of alarm instead of the alarm itself.
//        }
        return list;
    }

    public static Alarm getAlarm(int Id) {
//        for (Alarm alarm : alarms) {
//            if (alarm.getId() == Id) {
//                return alarm;
//                //TODO: Create copy of alarm instead of the alarm itself.
//            }
//        }
        return AlarmDB.getInstance().getAlarm(Id); // Could not find the alarm
    }

    public static void addAlarm(Alarm alarm) {
        Log.d(TAG, "Adding alarm with id: " + alarm.getId());
//        alarms.add(alarm);
        AlarmDB.getInstance().addAlarm(alarm);
    }

    public static void removeAlarm(int Id, Context context) {
        Log.d(TAG, "Removing alarm with id: " + Id);

        cancelAlarms(context);
        AlarmDB.getInstance().deleteAlarm(Id);
        setAlarms(context);
    }

    /**
     * Sets a separate snooze alarm in the near future
     * this alarm works separately from the rest of the alarms.
     * @param time The number of minutes to delay the snooze alarm.
     */
    public static void setSnooze(Context context, int time) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, time);

        Intent snoozeIntent = new Intent(context, AlarmService.class);

        // put extra
        // start the service and "show" something when Alarm goes off.

        int snoozeID = -1;
        setAlarm(context, cal, PendingIntent.getService(context, snoozeID, snoozeIntent, PendingIntent.FLAG_UPDATE_CURRENT));
    }

    public static void setActive(int Id, boolean active) {
        AlarmDB.getInstance().setActive(Id, active);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        setAlarms(context);
    }

    public static void setAlarms(Context context) {
        Log.d(TAG, "Setting Alarms");
        cancelAlarms(context);

        for(Alarm a : AlarmDB.getInstance().getAlarms()) {
            // Set the alarm, if enabled.

            Log.d(TAG, "Setting alarm: " + a.toString() +  " msg=" +a.getMessage());
            if (a.getStatus()) {
                Log.d(TAG, "Status active");
                setAlarm(context, getNextAlarmTime(a), createIntent(context, a));
            }
        }
    }

    public static int getNewId() {
        return AlarmDB.getInstance().getNewId();
    }

    /**
     * setExact required for API level 19 an above.
     * does not work on 18 an lower, use set instead.
     */
    @SuppressLint("NewApi")
    private static void setAlarm(Context context, Calendar calendar, PendingIntent pIntent) {

        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            manager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pIntent);
        } else {
            manager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pIntent);
        }
    }

    public static void cancelAlarms(Context context) {
        Log.d(TAG, "Cancelling alarm");
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        for(Alarm a : AlarmDB.getInstance().getAlarms()) {
            // Cancel the alarm.
            manager.cancel(createIntent(context, a));
        }
    }

    // Returns a calendar object that represents the next
    // time the alarm will activate.
    public static Calendar getNextAlarmTime(Alarm alarm) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, alarm.getHour());
        cal.set(Calendar.MINUTE, alarm.getMinute());
        cal.set(Calendar.SECOND, 0);

        List<Integer> days = alarm.getActiveDays();

        // find closest day to now.
        Calendar now = Calendar.getInstance();

        // Distance is used to find the day closest to now
        // All days will be closer than 7 days,
        int maxDistance = 7;

        // default day is the current day. If the hour has passed the alarm is
        // set for the next day.
        // This day will be overwritten if "days" is not empty.
        int day = cal.before(now) ? (now.get(Calendar.DAY_OF_WEEK) + 1 % 7)
                : now.get(Calendar.DAY_OF_WEEK);

        // Loop the "active" days for the alarm
        // This list is empty if the alarm is not repeating
        for (int i : days) {
            int distance = i - ((now.get(Calendar.DAY_OF_WEEK) - 2) % 7);
            Log.d(TAG, "i = " + i + " now.DAY_OF_WEEK = " + now.get(Calendar.DAY_OF_WEEK));
            if (distance < 0) {
                distance += 7; // next week.
            }
            if (distance == 0 && (cal.get(Calendar.HOUR_OF_DAY) < now.get(Calendar.HOUR_OF_DAY)
                    || (cal.get(Calendar.HOUR_OF_DAY) == now.get(Calendar.HOUR_OF_DAY))
                    && cal.get(Calendar.MINUTE) < now.get(Calendar.MINUTE))) {
                distance += 7;
                // Alarm set for today's weekday, but earlier time
                // Instead set alarm for next week.
            }

            // If a new day is found that is closer.
            // Set the day of the alarm to today + distance to
            // that day.
            if (distance < maxDistance) {
                Log.d(TAG, "Distance = " + distance + " MaxDistance = " + maxDistance);
                day = (now.get(Calendar.DAY_OF_WEEK) + distance) % 7;
                maxDistance = distance;
            }
        }

        // Set day of alarm to the calculated day.
        cal.set(Calendar.DAY_OF_WEEK, day);
        Log.d(TAG, "Day = " + day);
        return cal;
    }

    private static PendingIntent createIntent(Context context, Alarm alarm){
        Intent intent = new Intent(context, AlarmService.class);

        intent.putExtra("MESSAGE", alarm.getMessage());
        intent.putExtra("SNOOZE", alarm.getSnoozeInterval().getValue());
        intent.putExtra("ID", alarm.getId());

        // start the service and "show" something when Alarm goes off.
        return PendingIntent.getService(context, alarm.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
