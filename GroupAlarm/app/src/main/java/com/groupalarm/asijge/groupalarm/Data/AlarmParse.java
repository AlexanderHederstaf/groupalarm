package com.groupalarm.asijge.groupalarm.Data;

import android.content.Context;
import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;


/**
 * Created by sehlstedt on 14/05/15.
 */
public class AlarmParse {

    private static final String TAG = "AlarmParse";

    private static final String TABLE_ALARMS = "Alarms";

    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_MESSAGE = "Message";
    public static final String COLUMN_TIME = "Time";
    public static final String COLUMN_STATUS = "Status";

    public static final String COLUMN_MONDAY = "Monday";
    public static final String COLUMN_TUESDAY = "Tuesday";
    public static final String COLUMN_WEDNESDAY = "Wednesday";
    public static final String COLUMN_THURSDAY = "Thursday";
    public static final String COLUMN_FRIDAY = "Friday";
    public static final String COLUMN_SATURDAY = "Saturday";
    public static final String COLUMN_SUNDAY = "Sunday";

    public static final String COLUMN_SNOOZE = "Snooze";

    public boolean[] tmp;

    private static AlarmParse instance;
    public static void initiate(Context context) {
        if (instance == null) {
            instance = new AlarmParse(context);
        }
    }

    public static AlarmParse getInstance() {
        if (instance == null) {
            throw new InstantiationError("Not instantiated, call initiate().");
        }
        return instance;
    }

    /**
     * A constructor.
     * @param context
     */
    private AlarmParse(Context context) {
        super();
    }

    /**
     * Creates a new Id that does not collide with other
     * elements of the database.
     * @return Integer representing the new Id.
     */
    public int getNewId() throws ParseException {
        int value;

        ParseQuery<ParseObject> query = ParseQuery.getQuery(TABLE_ALARMS);
        query.orderByDescending(COLUMN_ID);
        ParseObject parseAlarm = query.getFirst();

        if (parseAlarm == null) {
            Log.d(TAG, "parseAlarm is null");
            value = 0;
        } else {
            value = parseAlarm.getInt(COLUMN_ID) + 1;
        }

        return value;
    }

    /**
     * A method for saving this Alarm in the database.
     * @param alarm An alarm to add to the database.
     */
    public void addAlarm(Alarm alarm) {

        tmp = alarm.getDays();

        ParseObject values = new ParseObject(TABLE_ALARMS);
        values.put(COLUMN_ID, alarm.getId());
        values.put(COLUMN_MESSAGE, alarm.getMessage());
        values.put(COLUMN_TIME, alarm.toString());
        values.put(COLUMN_STATUS, alarm.getStatus());
        values.put(COLUMN_MONDAY, tmp[0]);
        values.put(COLUMN_TUESDAY, tmp[1]);
        values.put(COLUMN_WEDNESDAY, tmp[2]);
        values.put(COLUMN_THURSDAY, tmp[3]);
        values.put(COLUMN_FRIDAY, tmp[4]);
        values.put(COLUMN_SATURDAY, tmp[5]);
        values.put(COLUMN_SUNDAY, tmp[6]);

        int snoozeValue;
        if (alarm.getSnoozeInterval() != null) {
            snoozeValue = alarm.getSnoozeInterval().getValue();
        } else {
            snoozeValue = 0;
        }

        values.put(COLUMN_SNOOZE, snoozeValue);

        values.saveInBackground();
    }

    /**
     * Get the Alarm object for a given ID
     */
    public Alarm getAlarm(int ID) throws ParseException {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(TABLE_ALARMS);
        query.whereEqualTo(COLUMN_ID, ID);
        ParseObject parseAlarm = query.getFirst();

        Alarm alarm = new Alarm(ID);

        alarm.setMessage(parseAlarm.getString(COLUMN_MESSAGE));

        int hour = Integer.valueOf(parseAlarm.getString(COLUMN_TIME).substring(0, 2));
        int minute = Integer.valueOf(parseAlarm.getString(COLUMN_TIME).substring(5));
        alarm.setTime(hour, minute);

        alarm.setActive(parseAlarm.getBoolean(COLUMN_STATUS));

        alarm.setDay(0, parseAlarm.getBoolean(COLUMN_MONDAY));
        alarm.setDay(1, parseAlarm.getBoolean(COLUMN_TUESDAY));
        alarm.setDay(2, parseAlarm.getBoolean(COLUMN_WEDNESDAY));
        alarm.setDay(3, parseAlarm.getBoolean(COLUMN_THURSDAY));
        alarm.setDay(4, parseAlarm.getBoolean(COLUMN_FRIDAY));
        alarm.setDay(5, parseAlarm.getBoolean(COLUMN_SATURDAY));
        alarm.setDay(6, parseAlarm.getBoolean(COLUMN_SUNDAY));

        alarm.setSnoozeInterval(Alarm.Snooze.valueOf(COLUMN_SNOOZE));

        return alarm;
    }
/*
    private Alarm getAlarmFromCursor(Cursor cursor) {
        Alarm alarm = new Alarm(cursor.getInt(0));
        alarm.setMessage(cursor.getString(1));

        String alarmTime = cursor.getString(2);
        int hour = Integer.valueOf(alarmTime.substring(0, 2));
        int minute = Integer.valueOf(alarmTime.substring(5));
        alarm.setTime(hour, minute);

        alarm.setActive(Boolean.valueOf(cursor.getString(3)));

        for (int i = 4; i < 11; i++) {
            alarm.setDay(i-4, Boolean.valueOf(cursor.getString(i)));
        }

        switch (cursor.getInt(11)) {
            case 0:
                alarm.setSnoozeInterval(Alarm.Snooze.NO_SNOOZE);
                break;
            case 5:
                alarm.setSnoozeInterval(Alarm.Snooze.FIVE);
                break;
            case 10:
                alarm.setSnoozeInterval(Alarm.Snooze.TEN);
                break;
            case 15:
                alarm.setSnoozeInterval(Alarm.Snooze.FIFTEEN);
                break;
            default:
                alarm.setSnoozeInterval(Alarm.Snooze.NO_SNOOZE);
                break;
        }
        return alarm;
    }
*/

    /**
     * Deletes an Alarm from the Alarm database.
     * @param id The Alarms unique ID.
     * @return boolean true if the deletion succeeded and false if the deletion failed.
     */
    public void deleteAlarm(int id) throws ParseException {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(TABLE_ALARMS);
        query.whereEqualTo(COLUMN_ID, id);
        ParseObject parseAlarm = query.getFirst();
        parseAlarm.delete();
    }
}

