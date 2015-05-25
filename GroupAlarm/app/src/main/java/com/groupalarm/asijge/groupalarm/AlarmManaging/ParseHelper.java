package com.groupalarm.asijge.groupalarm.AlarmManaging;

import android.util.Log;

import com.groupalarm.asijge.groupalarm.Data.Alarm;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

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
    private static final String TABLE_ALARMSTATUS = "alarmStatus";

    private static final String COLUMN_NAME = "Name";
    private static final String COLUMN_USERS = "Users";
    private static final String COLUMN_ALARMS = "Alarms";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_GROUP = "Group";
    private static final String COLUMN_ALARMSTATUS = "alarmStatus";


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

        ParseQuery query = ParseQuery.getQuery(TABLE_GROUPS);
        query.whereEqualTo(COLUMN_NAME, name);
        ParseObject nameNotUnique = null;
        try {
            nameNotUnique = query.getFirst();
            Log.d(TAG, "query ok");
        } catch (ParseException e) {
            Log.d(TAG, "query not ok");
        }
        if (nameNotUnique != null) {
            Log.d(TAG, "Groupname not unique. No group created.");
            return;
        }

        ParseObject newGroup = new ParseObject(TABLE_GROUPS);
        newGroup.put(COLUMN_NAME, name);

        ParseUser user = ParseUser.getCurrentUser();

        ParseRelation<ParseUser> relation = newGroup.getRelation(COLUMN_USERS);
        relation.add(user);

        try {
            newGroup.save();
        } catch (ParseException e) {
            Log.d(TAG, "createGroup was not able to store group in cloud");
        }
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
            try {
                groupObject.save();

                // create an entry in the alarmStatus table
                ParseObject alarmStatus = new ParseObject(TABLE_ALARMSTATUS);
                alarmStatus.put(COLUMN_USERNAME, user);
                alarmStatus.put(COLUMN_GROUP, group);
                alarmStatus.put(COLUMN_ALARMSTATUS, "stopped");

                try {
                    alarmStatus.save();
                    Log.d(TAG, "User: " + user + " in group: " + group + " alarmStatus: stopped");
                } catch (ParseException e) {
                    Log.d(TAG, "Could not set alarmStatus");
                }

                Log.d(TAG, "addUserToGroup successful");
            } catch (ParseException e) {
                Log.d(TAG, "addUserToGroup not successful");
            }
        }
    }

    public static void addNewAlarmToGroup(Alarm alarm, String group) {

        ParseObject alarmObject = getParseObjectFromAlarm(alarm);
        ParseObject groupObject = getGroupFromString(group);

        try {
            alarmObject.save();
            Log.d(TAG, "addNewAlarmToGroup saved alarmObject " + alarmObject.getObjectId() + " to table " + TABLE_ALARMS);
        } catch (ParseException e) {
            Log.d(TAG, "addNewAlarmToGroup couldn't save alarmObject");
        }

        if (groupObject != null && alarmObject != null) {
            ParseRelation<ParseObject> relation = groupObject.getRelation(COLUMN_ALARMS);
            relation.add(alarmObject);

            try {
                groupObject.save();
                Log.d(TAG, "addNewAlarmToGroup related alarmObject " + alarmObject.getObjectId() + "  to groupObject " + groupObject.getObjectId());
            } catch (ParseException e) {
                Log.d(TAG, "addNewAlarmToGroup couldn't relate alarmObject to alarmGroup");
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
            Log.d(TAG, "getAlarmsFromGroup groupObject " + groupObject.getObjectId() + " retrieved from cloud");
        } catch (ParseException e) {
            Log.d(TAG, "getAlarmsFromGroup groupObject not retrieved from cloud");
        }

        List<ParseObject> alarmObjectList = null;

        List<Alarm> groupAlarmList = new LinkedList<Alarm>();

        ParseRelation relation = groupObject.getRelation(COLUMN_ALARMS);
        ParseQuery queryRelation = relation.getQuery();
        try {
            alarmObjectList = queryRelation.find();
            for (ParseObject alarmObject : alarmObjectList) {
                groupAlarmList.add(getAlarmFromParseObject(alarmObject, 0));
            }
        } catch (ParseException e) {
            Log.d(TAG, "getAlarmsFromGroup alarmObjectList empty");
        }

        return groupAlarmList;
    }

    public static List<Alarm> getAllRemoteAlarmsForUser() {

        ParseUser currentUser = ParseUser.getCurrentUser();

        ParseQuery<ParseObject> query = ParseQuery.getQuery(TABLE_GROUPS);
        query.whereEqualTo(COLUMN_USERS, currentUser);

        List<ParseObject> groupObjectList = null;
        try {
            groupObjectList = query.find();
        } catch (ParseException e) {
            Log.d(TAG, "getAllRemoteAlarmsForUser struggle to query the Parse cloud for groupObjectList.");
            e.printStackTrace();
        }

        List<Alarm> usersAllAlarms = new LinkedList<Alarm>();
        List<ParseObject> alarmObjectList = null;

        for (ParseObject groupObject : groupObjectList) {

            ParseRelation relation = groupObject.getRelation(COLUMN_ALARMS);
            ParseQuery queryRelation = relation.getQuery();

            try {
                alarmObjectList = queryRelation.find();

                for (ParseObject alarmObject : alarmObjectList) {
                    try {
                        alarmObject.fetchIfNeeded();
                        usersAllAlarms.add(getAlarmFromParseObject(alarmObject, usersAllAlarms.size()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                        Log.d(TAG, "getAllRemoteAlarmsForUser struggle to query the Parse cloud for alarmObject.");
                    }

                }
            } catch (ParseException e) {
                Log.d(TAG, "getAllRemoteAlarmsForUser alarmObjectList empty");
            }

        }
        Log.d(TAG, "getAllRemoteAlarmsForUser found " + usersAllAlarms.size() + " alarms.");
        return usersAllAlarms;
    }

    public static List<String> getUsersInGroup(String group) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(TABLE_GROUPS);
        query.whereEqualTo(COLUMN_NAME, group);

        ParseObject groupObject = null;

        try {
            groupObject = query.getFirst();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        ParseRelation relation = groupObject.getRelation(COLUMN_USERS);

        ParseQuery queryRelation = relation.getQuery();

        List<ParseUser> parseUserList = null;
        try {
            parseUserList = queryRelation.find();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        List<String> groupUserList = new LinkedList<String>();

        for (ParseUser parseUser : parseUserList) {
            groupUserList.add(parseUser.getUsername());
        }

        return groupUserList;
    }

    public static String getGroupFromAlarm(Alarm alarm) {

        ParseObject alarmObject = null;
        ParseObject groupObject = null;

        ParseQuery<ParseObject> query1 = ParseQuery.getQuery(TABLE_ALARMS);
        try {
            alarmObject = query1.get(alarm.getParseID());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        ParseQuery<ParseObject> query2 = ParseQuery.getQuery(TABLE_GROUPS);
        query2.whereEqualTo(COLUMN_ALARMS, alarmObject);
        try {
            groupObject = query2.getFirst();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return groupObject != null ? groupObject.getString(COLUMN_NAME) : "";
    }

    public static void leaveGroup(String group) {
        ParseUser userObject = ParseUser.getCurrentUser();
        ParseObject groupObject = getGroupFromString(group);

        if (groupObject != null && userObject != null) {
            ParseRelation<ParseUser> relation = groupObject.getRelation(COLUMN_USERS);
            relation.remove(userObject);
            try {
                groupObject.save();
                Log.d(TAG, "leaveGroup " + group + " successful");
            } catch (ParseException e) {
                Log.d(TAG, "Could not leaveGroup " + group);
            }
        }
    }

    public static void editAlarm(Alarm alarm) {
        ParseObject alarmObject = null;

        ParseQuery<ParseObject> query1 = ParseQuery.getQuery(TABLE_ALARMS);
        try {
            alarmObject = query1.get(alarm.getParseID());
            Log.d(TAG, "editAlarm retrieved alarm with ParseID: " + alarm.getParseID() + " from cloud");
        } catch (ParseException e) {
            Log.d(TAG, "editAlarm, no alarm in cloud with ParseID: " + alarm.getParseID());
        }

        boolean[] tmp = alarm.getDays();

        alarmObject.put(COLUMN_MESSAGE, alarm.getMessage());
        alarmObject.put(COLUMN_TIME, alarm.toString());
        alarmObject.put(COLUMN_STATUS, alarm.getStatus());
        alarmObject.put(COLUMN_MONDAY, tmp[0]);
        alarmObject.put(COLUMN_TUESDAY, tmp[1]);
        alarmObject.put(COLUMN_WEDNESDAY, tmp[2]);
        alarmObject.put(COLUMN_THURSDAY, tmp[3]);
        alarmObject.put(COLUMN_FRIDAY, tmp[4]);
        alarmObject.put(COLUMN_SATURDAY, tmp[5]);
        alarmObject.put(COLUMN_SUNDAY, tmp[6]);

        int snoozeValue;
        if (alarm.getSnoozeInterval() != null) {
            snoozeValue = alarm.getSnoozeInterval().getValue();
        } else {
            snoozeValue = 0;
        }
        alarmObject.put(COLUMN_SNOOZE, snoozeValue);

        try {
            alarmObject.save();
            Log.d(TAG, "editAlarm updated alarm ok");
        } catch (ParseException e) {
            Log.d(TAG, "editAlarm could not update alarm");
        }
    }

    public static void deleteAlarm(Alarm alarm) {
        ParseObject alarmObject = null;

        ParseQuery<ParseObject> query1 = ParseQuery.getQuery(TABLE_ALARMS);
        try {
            alarmObject = query1.get(alarm.getParseID());
            alarmObject.delete();
            Log.d(TAG, "deleteAlarm successful");
        } catch (ParseException e) {
            Log.d(TAG, "deleteAlarm NOT successful");
        }
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

    public static ParseObject getGroupFromString(String group) {
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

    private static Alarm getAlarmFromParseObject(ParseObject parseObject, int offset) {

        //Alarm alarm = new Alarm(parseObject.getInt(COLUMN_ID));
        Alarm alarm = new Alarm(AlarmDB.getInstance().getNewId() + offset);

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

        switch (parseObject.getInt(COLUMN_SNOOZE)) {
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

        alarm.setGroupAlarm(parseObject.getObjectId());

        return alarm;
    }

    private static ParseObject getParseObjectFromAlarm(Alarm alarm) {

        boolean[] tmp = alarm.getDays();

        ParseObject parseAlarm = new ParseObject(TABLE_ALARMS);

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

    public static String getAlarmStatusUserPerGroup (String user, String group) {

        String output = "";
        ParseObject alarmStatusObject = null;

        ParseQuery<ParseObject> query = ParseQuery.getQuery(TABLE_ALARMSTATUS);
        query.whereEqualTo(COLUMN_USERNAME, user);
        query.whereEqualTo(COLUMN_GROUP, group);

        try {
            alarmStatusObject = query.getFirst();
        } catch (ParseException e) {
            Log.d(TAG, "No row matching query: " + group + ", " + user);
        }

        output = alarmStatusObject.getString(COLUMN_ALARMSTATUS);

        Log.d(TAG, "Status is: " + output);

        return output;
    }

    public static void setMyAlarmStatusPerGroup (String group, String status) {

        String user = ParseUser.getCurrentUser().getUsername();
        ParseObject alarmStatus = null;

        // Find user and group row if exist
        ParseQuery<ParseObject> query = ParseQuery.getQuery(TABLE_ALARMSTATUS);
        query.whereContains(COLUMN_USERNAME, user);
        query.whereContains(COLUMN_GROUP, group);

        try {
            alarmStatus = query.getFirst();
        } catch (ParseException e) {
            Log.d(TAG, "No row matching query: " + group + ", " + user);
        }

        if (alarmStatus == null) {
            // create an entry in the alarmStatus table
            alarmStatus = new ParseObject(TABLE_ALARMSTATUS);
        }

        alarmStatus.put(COLUMN_USERNAME, user);
        alarmStatus.put(COLUMN_GROUP, group);
        alarmStatus.put(COLUMN_ALARMSTATUS, status);

        try {
            alarmStatus.save();
            Log.d(TAG, "User: " + user + " in group: " + group + " alarmStatus: " + status);
        } catch (ParseException e) {
            Log.d(TAG, "Could not set alarmStatus");
        }
    }
}

