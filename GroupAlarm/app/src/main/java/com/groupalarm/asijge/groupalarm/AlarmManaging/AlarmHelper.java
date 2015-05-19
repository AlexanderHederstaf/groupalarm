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
     * Sets a separate snooze alarm in the near future this alarm works separately from the rest of the alarms.
     *
     * @param context The Application Context.
     * @param time The number of minutes to delay the snooze alarm.
     */
    public static void setSnooze(Context context, int time) {
        if (time <= 0) {
            return;
        }
        int snoozeID = -1;
        snoozeAlarm = new Alarm(snoozeID);
        snoozeAlarm.setActive(true);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, time);

        snoozeAlarm.setTime(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
        snoozeAlarm.setMessage("Snooze");

        switch (time) {
            case 5:
                snoozeAlarm.setSnoozeInterval(Alarm.Snooze.FIVE);
                break;
            case 10:
                snoozeAlarm.setSnoozeInterval(Alarm.Snooze.TEN);
                break;
            case 15:
                snoozeAlarm.setSnoozeInterval(Alarm.Snooze.FIFTEEN);
                break;
            default:
                snoozeAlarm.setSnoozeInterval(Alarm.Snooze.NO_SNOOZE);
                break;
        }

        PendingIntent snoozeIntent = createIntent(context, snoozeAlarm);

        setAlarm(context, cal, snoozeIntent);
    }

    /**
     * Cancels the separate snooze Alarm.
     *
     * @param context The Application Context
     */
    public static void cancelSnooze(Context context) {
        if (snoozeAlarm != null) {
            AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            manager.cancel(createIntent(context, snoozeAlarm));
            snoozeAlarm = null;
        }
    }

    /**
     * Sets the Alarm with id ID to the given status. A call must be done separately to setAlarms
     * to register the change in the system.
     *
     * @param Id The ID of the Alarm to change status of.
     * @param active The new status.
     * @see AlarmHelper#setAlarms(Context)
     */
    public static void setActive(int Id, boolean active) {
        AlarmDB.getInstance().setActive(Id, active);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        setAlarms(context);
    }

    /**
     * Sets up the system to "ring" the Alarms that have Active status.
     *
     * @param context The Application Context.
     */
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

    /**
     * Provides a new Id for an alarm, the ID is unique with respect to the stored alarms.
     * @return A new unique Id.
     */
    public static int getNewId() {
        return AlarmDB.getInstance().getNewId();
    }

    /*
     * Work around for setting Alarms on different Android versions
     * The new versions have more battery saving features.
     *
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

    /**
     * Cancels all alarms in the system using the data of the currently stored Alarms
     * If an alarm is active in the system and it's original Alarm data has been altered it
     * can not be cancelled.
     *
     * @param context The Application Context.
     */
    public static void cancelAlarms(Context context) {
        Log.d(TAG, "Cancelling alarm");
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        for(Alarm a : AlarmDB.getInstance().getAlarms()) {
            // Cancel the alarm.
            manager.cancel(createIntent(context, a));
        }
    }

    /**
     * Gives a Calendar object representing the next time the Alarm is set to go off.
     * For repeating alarms this gives the closest day and time.
     *
     * @param alarm The alarm to get the time for.
     * @return A Calendar object set for the time the alarm will ring.
     */
    public static Calendar getNextAlarmTime(Alarm alarm) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, alarm.getHour());
        cal.set(Calendar.MINUTE, alarm.getMinute());
        cal.set(Calendar.SECOND, 0);

        List<Integer> days = alarm.getActiveDays();

        // Obtain Calendar object for the current time.
        Calendar now = Calendar.getInstance();

        // Distance is used to find the day closest to now
        // All days will be closer than 7 days,
        int maxDistance = 7;

        // The default day is the current day. If the hour has passed the alarm is
        // set for the next day.
        // This day will be overwritten only for repeating alarms.
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