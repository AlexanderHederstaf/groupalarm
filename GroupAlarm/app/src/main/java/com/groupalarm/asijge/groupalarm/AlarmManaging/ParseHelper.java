package com.groupalarm.asijge.groupalarm.AlarmManaging;

import android.util.Log;

import com.groupalarm.asijge.groupalarm.Data.Alarm;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by sehlstedt on 19/05/15.
 */
public class ParseHelper {

    private static final String TAG = "ParseHelper";

    private static final String TABLE_GROUPS = "Groups";
    private static final String TABLE_USER = "User";
    private static final String TABLE_ALARMS = "Alarms";

    private static final String COLUMN_NAME = "Name";
    private static final String COLUMN_USERS = "Users";
    private static final String COLUMN_ALARMS = "Groupalarms";
    private static final String COLUMN_USERNAME = "username";

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

    public static void createGroup(String name) {

        final ParseObject newGroup = new ParseObject(TABLE_GROUPS);
        newGroup.put(COLUMN_NAME, name);

        ParseUser user = ParseUser.getCurrentUser();
        ParseRelation<ParseUser> relation = newGroup.getRelation(COLUMN_USERS);
        relation.add(user);

        newGroup.saveInBackground(new SaveCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    Log.d(TAG, "Group " + newGroup.toString() + " stored in Parse cloud.");
                } else {
                    Log.d(TAG, "Group " + newGroup.toString() + " was not stored.");
                }
            }
        });
    }

    public static List<String> getGroupsForUser() {

        ParseUser user = ParseUser.getCurrentUser();

        ParseQuery<ParseObject> query = ParseQuery.getQuery(TABLE_GROUPS);
        query.whereEqualTo(COLUMN_USERS, user);

        List<ParseObject> groupObjectList = null;

        try {
            groupObjectList = query.find();
        } catch (ParseException e) {
            Log.d(TAG, "getGroupsForUser struggle to query the Parse cloud.");
            e.printStackTrace();
        }

        List<String> groupStringList = new LinkedList<String>();
        for (ParseObject parseObject : groupObjectList) {
            groupStringList.add(parseObject.getString(COLUMN_NAME));
        }

        return groupStringList;
    }

    public static void addUserToGroup(String user, String group) {

        ParseUser userObject = getUserFromString(user);
        ParseObject groupObject = getGroupFromString(group);

        if (groupObject != null && userObject != null) {
            ParseRelation<ParseUser> relation = groupObject.getRelation(COLUMN_USERS);
            relation.add(userObject);

            groupObject.saveInBackground(new SaveCallback() {
                public void done(ParseException e) {
                    if (e == null) {
                        Log.d(TAG, "User saveInBackground to group");
                    } else {
                        Log.d(TAG, "Could not saveInBackground User to group");
                    }
                }
            });
        }
    }

    public static void addNewAlarmToGroup(Alarm alarm, String group) {

        ParseObject alarmObject = getParseObjectFromAlarm(alarm);
        try {
            alarmObject.save();
            Log.d(TAG, "addNewAlarmToGroup Alarm saved to cloud OK");
        } catch (ParseException e) {
            e.printStackTrace();
            Log.d(TAG, "addNewAlarmToGroup Alarm could not be saved to cloud!");
        }

        ParseObject groupObject = getGroupFromString(group);

        if (groupObject != null && alarmObject != null) {

            ArrayList<ParseObject> alarmsInCloud = (ArrayList<ParseObject>) groupObject.get("Groupalarms");
            if (alarmsInCloud == null) {
                alarmsInCloud = new ArrayList<ParseObject>();
            }

            alarmsInCloud.add(alarmObject);
            groupObject.put(COLUMN_ALARMS, alarmsInCloud);

            try {
                groupObject.save();
                Log.d(TAG, "addNewAlarmToGroup saved alarm to group");
            } catch (ParseException e) {
                e.printStackTrace();
                Log.d(TAG, "addNewAlarmToGroup failed to save alarm to group");
            }

        } else {
            Log.d(TAG, "addNewAlarmToGroup groupObject or alarmObject is null");
        }
    }

    public static List<Alarm> getAlarmsFromGroup(String group) {

        ParseQuery<ParseObject> query = ParseQuery.getQuery(TABLE_GROUPS);
        query.whereEqualTo(COLUMN_NAME, group);

        ParseObject groupObject = null;

        try {
            groupObject = query.getFirst();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        List<ParseObject> alarmObjectList = groupObject.getList(COLUMN_ALARMS);

        List<Alarm> groupAlarmList = new LinkedList<Alarm>();
        for (ParseObject parseObject : alarmObjectList) {
            try {
                parseObject.fetchIfNeeded();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            groupAlarmList.add(getAlarmFromParseObject(parseObject));
        }

        return groupAlarmList;
    }

    public static void getAllRemoteAlarmsForUser() {
        // not implemented
    }

    public static void userSnoozedAlarm() {
        // not implemented
    }

    private static ParseUser getUserFromString(String user) {
        ParseUser userObject = null;

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo(COLUMN_USERNAME, user);
        try {
            userObject = query.getFirst();
        } catch (ParseException e) {
            Log.d(TAG, "getUserFromString struggle to query the Parse cloud for user.");
            e.printStackTrace();
        }
        return userObject;
    }

    private static ParseObject getGroupFromString(String group) {
        ParseObject groupObject = null;
        ParseQuery<ParseObject> query1 = ParseQuery.getQuery(TABLE_GROUPS);
        query1.whereEqualTo(COLUMN_NAME, group);
        try {
            groupObject = query1.getFirst();
        } catch (ParseException e) {
            Log.d(TAG, "getGroupFromString struggle to query the Parse cloud for group.");
            e.printStackTrace();
        }
        return groupObject;
    }

    private static Alarm getAlarmFromParseObject(ParseObject parseObject) {

        Alarm alarm = new Alarm(parseObject.getInt(COLUMN_ID));

        alarm.setMessage(parseObject.getString(COLUMN_MESSAGE));

        int hour = Integer.valueOf(parseObject.getString(COLUMN_TIME).substring(0, 2));
        int minute = Integer.valueOf(parseObject.getString(COLUMN_TIME).substring(5));
        alarm.setTime(hour, minute);

        alarm.setActive(parseObject.getBoolean(COLUMN_STATUS));

        alarm.setDay(0, parseObject.getBoolean(COLUMN_MONDAY));
        alarm.setDay(1, parseObject.getBoolean(COLUMN_TUESDAY));
        alarm.setDay(2, parseObject.getBoolean(COLUMN_WEDNESDAY));
        alarm.setDay(3, parseObject.getBoolean(COLUMN_THURSDAY));
        alarm.setDay(4, parseObject.getBoolean(COLUMN_FRIDAY));
        alarm.setDay(5, parseObject.getBoolean(COLUMN_SATURDAY));
        alarm.setDay(6, parseObject.getBoolean(COLUMN_SUNDAY));

        alarm.setSnoozeInterval(Alarm.Snooze.NO_SNOOZE);

        return alarm;
    }

    private static ParseObject getParseObjectFromAlarm(Alarm alarm) {

        boolean[] tmp = alarm.getDays();

        ParseObject parseAlarm = new ParseObject(TABLE_ALARMS);
        parseAlarm.put(COLUMN_ID, alarm.getId());
        parseAlarm.put(COLUMN_MESSAGE, alarm.getMessage());
        parseAlarm.put(COLUMN_TIME, alarm.toString());
        parseAlarm.put(COLUMN_STATUS, alarm.getStatus());
        parseAlarm.put(COLUMN_MONDAY, tmp[0]);
        parseAlarm.put(COLUMN_TUESDAY, tmp[1]);
        parseAlarm.put(COLUMN_WEDNESDAY, tmp[2]);
        parseAlarm.put(COLUMN_THURSDAY, tmp[3]);
        parseAlarm.put(COLUMN_FRIDAY, tmp[4]);
        parseAlarm.put(COLUMN_SATURDAY, tmp[5]);
        parseAlarm.put(COLUMN_SUNDAY, tmp[6]);

        int snoozeValue;
        if (alarm.getSnoozeInterval() != null) {
            snoozeValue = alarm.getSnoozeInterval().getValue();
        } else {
            snoozeValue = 0;
        }

        parseAlarm.put(COLUMN_SNOOZE, snoozeValue);

        return parseAlarm;
    }
}

