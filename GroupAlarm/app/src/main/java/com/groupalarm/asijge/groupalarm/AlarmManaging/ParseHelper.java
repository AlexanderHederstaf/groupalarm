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
 * ParseHelper has got a selection of methods for querying the Parse.com cloud service.
 *
 * @author asijge
 */
public class ParseHelper {

    private static final String TAG = "ParseHelper";

    private static final String TABLE_GROUPS = "Groups";
    private static final String TABLE_ALARMS = "Alarms";
    private static final String TABLE_ALARMSTATUS = "alarmStatus";

    private static final String COLUMN_NAME = "Name";
    private static final String COLUMN_USERS = "Users";
    private static final String COLUMN_ALARMS = "Alarms";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_GROUP = "Group";
    private static final String COLUMN_ALARMSTATUS = "alarmStatus";
    private static final String COLUMN_ALARMSIGNAL = "alarmSignal";
    private static final String COLUMN_PUNISHABLE = "punishable";

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

    /**
     * Create an new group.
     *
     * @param name String for groupname, must be a unique string.
     */
    public static void createGroup(String name) {

        // Check if name is unique, return if not.
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

        // If name is unique, create a new row in the groups table.
        ParseObject newGroup = new ParseObject(TABLE_GROUPS);
        newGroup.put(COLUMN_NAME, name);

        // Add current user to the new group
        ParseUser user = ParseUser.getCurrentUser();
        ParseRelation<ParseUser> relation = newGroup.getRelation(COLUMN_USERS);
        relation.add(user);

        try {
            newGroup.save();

            // create an entry in the alarmStatus table with defaults
            ParseObject alarmStatus = new ParseObject(TABLE_ALARMSTATUS);
            alarmStatus.put(COLUMN_USERNAME, user.getUsername());
            alarmStatus.put(COLUMN_GROUP, name);
            alarmStatus.put(COLUMN_ALARMSTATUS, "stopped");
            alarmStatus.put(COLUMN_ALARMSIGNAL, "classic_alarm");
            alarmStatus.put(COLUMN_PUNISHABLE, false);

            try {
                alarmStatus.save();
                Log.d(TAG, "User: " + user + " in group: " + name + " alarmStatus: stopped");
            } catch (ParseException e) {
                Log.d(TAG, "Could not set alarmStatus");
            }
        } catch (ParseException e) {
            Log.d(TAG, "createGroup was not able to store group in cloud");
        }
    }

    /**
     * Get a list of groupnames(strings) associated with the current user.
     *
     * @return A list of strings, names of groups.
     */
    public static List<String> getGroupsForUser() {

        ParseUser user = ParseUser.getCurrentUser();

        // Create groups table query based on current user
        ParseQuery<ParseObject> query = ParseQuery.getQuery(TABLE_GROUPS);
        query.whereEqualTo(COLUMN_USERS, user);

        List<ParseObject> groupObjectList = null;

        try {
            groupObjectList = query.find();
        } catch (ParseException e) {
            Log.d(TAG, "getGroupsForUser struggle to query the Parse cloud.");
            e.printStackTrace();
        }

        // Iterate through list to get all the names
        List<String> groupStringList = new LinkedList<String>();
        for (ParseObject parseObject : groupObjectList) {
            groupStringList.add(parseObject.getString(COLUMN_NAME));
        }

        return groupStringList;
    }

    /**
     * Add a user to a group.
     *
     * @param user String, the username
     * @param group String, the groupname
     */
    public static void addUserToGroup(String user, String group) {

        // Get the user and group ParseObject based on user and group name.
        ParseUser userObject = getUserFromString(user);
        ParseObject groupObject = getGroupFromString(group);

        // Create a relation between user and group
        if (groupObject != null && userObject != null) {
            ParseRelation<ParseUser> relation = groupObject.getRelation(COLUMN_USERS);
            relation.add(userObject);
            try {
                groupObject.save();

                // create an new entry in the alarmStatus table
                ParseObject alarmStatus = new ParseObject(TABLE_ALARMSTATUS);
                alarmStatus.put(COLUMN_USERNAME, user);
                alarmStatus.put(COLUMN_GROUP, group);
                alarmStatus.put(COLUMN_ALARMSTATUS, "stopped");
                alarmStatus.put(COLUMN_ALARMSIGNAL, "classic_alarm");
                alarmStatus.put(COLUMN_PUNISHABLE, false);

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

    /**
     * Add a new alarm to a group.
     *
     * @param alarm Alarm to include in group
     * @param group String, groupname for the group in question
     */
    public static void addNewAlarmToGroup(Alarm alarm, String group) {

        // Get the alarm and group ParseObject based on Alarm object and group name.
        ParseObject alarmObject = getParseObjectFromAlarm(alarm);
        ParseObject groupObject = getGroupFromString(group);

        try {
            alarmObject.save();
            Log.d(TAG, "addNewAlarmToGroup saved alarmObject " + alarmObject.getObjectId() + " to table " + TABLE_ALARMS);
        } catch (ParseException e) {
            Log.d(TAG, "addNewAlarmToGroup couldn't save alarmObject");
        }

        if (groupObject != null && alarmObject != null) {

            // Create a relation between group and alarm
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

    /**
     * Get all the alarms associated with a specific group.
     *
     * @param group String, name of group
     * @return a list of Alarms
     */
    public static List<Alarm> getAlarmsFromGroup(String group) {

        // Query the group table for the requested group object
        ParseQuery<ParseObject> query = ParseQuery.getQuery(TABLE_GROUPS);
        query.whereEqualTo(COLUMN_NAME, group);

        ParseObject groupObject = null;

        try {
            groupObject = query.getFirst();
            Log.d(TAG, "getAlarmsFromGroup groupObject " + groupObject.getObjectId() + " retrieved from cloud");
        } catch (ParseException e) {
            Log.d(TAG, "getAlarmsFromGroup groupObject not retrieved from cloud");
        }

        // Create lists for query result
        List<ParseObject> alarmObjectList = null;
        List<Alarm> groupAlarmList = new LinkedList<Alarm>();

        // Find correct alarms based on querying the relationship group vs alarms
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

    /**
     * Get all alarms associated with a specific user.
     * @return
     */
    public static List<Alarm> getAllRemoteAlarmsForUser() {

        ParseUser currentUser = ParseUser.getCurrentUser();

        // Query the group table for the requested currentuser object
        ParseQuery<ParseObject> query = ParseQuery.getQuery(TABLE_GROUPS);
        query.whereEqualTo(COLUMN_USERS, currentUser);

        List<ParseObject> groupObjectList = null;
        try {
            groupObjectList = query.find();
        } catch (ParseException e) {
            Log.d(TAG, "getAllRemoteAlarmsForUser struggle to query the Parse cloud for groupObjectList.");
            e.printStackTrace();
        }

        // Create lists for query result
        List<Alarm> usersAllAlarms = new LinkedList<Alarm>();
        List<ParseObject> alarmObjectList = null;

        // Iterate group query result and find all alarms per group
        for (ParseObject groupObject : groupObjectList) {

            ParseRelation relation = groupObject.getRelation(COLUMN_ALARMS);
            ParseQuery queryRelation = relation.getQuery();

            try {
                alarmObjectList = queryRelation.find();

                // Iterate alarms per group and add Alarms to return list
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

    /**
     * Get all users in the requested group.
     *
     * @param group String, the groupname
     * @return A list of usernames.
     */
    public static List<String> getUsersInGroup(String group) {

        // Query the groups table on the groupname
        ParseQuery<ParseObject> query = ParseQuery.getQuery(TABLE_GROUPS);
        query.whereEqualTo(COLUMN_NAME, group);

        ParseObject groupObject = null;

        try {
            groupObject = query.getFirst();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Get the users associated with the group in question
        ParseRelation relation = groupObject.getRelation(COLUMN_USERS);
        ParseQuery queryRelation = relation.getQuery();

        List<ParseUser> parseUserList = null;
        try {
            parseUserList = queryRelation.find();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Iterate through userlist and add username to output string list
        List<String> groupUserList = new LinkedList<String>();
        for (ParseUser parseUser : parseUserList) {
            groupUserList.add(parseUser.getUsername());
        }

        return groupUserList;
    }

    /**
     * Get the name of the group where a specific alarm exists.
     *
     * @param alarm Alarm to check for
     * @return A string, the group name
     */
    public static String getGroupFromAlarm(Alarm alarm) {

        ParseObject alarmObject = null;
        ParseObject groupObject = null;

        // Query the alarms table on the ParseID of the alarm object
        ParseQuery<ParseObject> query1 = ParseQuery.getQuery(TABLE_ALARMS);
        try {
            alarmObject = query1.get(alarm.getParseID());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Query the groups table on the parse alarm object
        ParseQuery<ParseObject> query2 = ParseQuery.getQuery(TABLE_GROUPS);
        query2.whereEqualTo(COLUMN_ALARMS, alarmObject);
        try {
            groupObject = query2.getFirst();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Return group name, return empty string if query result is null
        return groupObject != null ? groupObject.getString(COLUMN_NAME) : "";
    }

    /**
     * Make current user leave a group.
     *
     * @param group The group name, string.
     */
    public static void leaveGroup(String group) {
        // Get the relevant group and current user
        ParseUser userObject = ParseUser.getCurrentUser();
        ParseObject groupObject = getGroupFromString(group);

        // Remove relation and alarm object
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

    /**
     * Edit an alarm saved in cloud.
     *
     * @param alarm Alarm to edit
     */
    public static void editAlarm(Alarm alarm) {
        ParseObject alarmObject = null;

        // Query alarms table with Alarm objects parseID
        ParseQuery<ParseObject> query1 = ParseQuery.getQuery(TABLE_ALARMS);
        try {
            alarmObject = query1.get(alarm.getParseID());
            Log.d(TAG, "editAlarm retrieved alarm with ParseID: " + alarm.getParseID() + " from cloud");
        } catch (ParseException e) {
            Log.d(TAG, "editAlarm, no alarm in cloud with ParseID: " + alarm.getParseID());
        }

        // Update all fields with new alarm object values
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

        // Save new values
        try {
            alarmObject.save();
            Log.d(TAG, "editAlarm updated alarm ok");
        } catch (ParseException e) {
            Log.d(TAG, "editAlarm could not update alarm");
        }
    }

    /**
     * Delete an alarm from cloud database.
     * @param alarm Alarm to delete
     */
    public static void deleteAlarm(Alarm alarm) {
        ParseObject alarmObject = null;

        // Query alarms table on parseID
        ParseQuery<ParseObject> query1 = ParseQuery.getQuery(TABLE_ALARMS);
        try {
            alarmObject = query1.get(alarm.getParseID());
            // Delete alarm
            alarmObject.delete();
            Log.d(TAG, "deleteAlarm successful");
        } catch (ParseException e) {
            Log.d(TAG, "deleteAlarm NOT successful");
        }
    }

    /**
     * Get a ParseUser object from a username string.
     *
     * @param user The username string
     * @return The ParseUser object associated with the username
     */
    private static ParseUser getUserFromString(String user) {
        ParseUser userObject = null;

        // Query user table for the user in question
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

    /**
     * Get the ParseObject related to a groupname from the groups table.
     *
     * @param group Groupname, string
     * @return A ParseObject with grouptable values
     */
    public static ParseObject getGroupFromString(String group) {
        ParseObject groupObject = null;

        // Query the groups table on the group name
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

    /**
     * Get an Alarm class object from a ParseObject with alarm values
     *
     * @param parseObject ParseObject to get Alarm from
     * @param offset When querying for many alarms, adds an offset to match local database
     * @return An Alarm filled with values from the ParseObject
     */
    private static Alarm getAlarmFromParseObject(ParseObject parseObject, int offset) {

        // Create a new alarm with Unique id based on local
        // database with addition of possible offset.
        Alarm alarm = new Alarm(AlarmDB.getInstance().getNewId() + offset);

        // Put values from ParseObject to the new Alarm object
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

        // Add the ParseObject unique id for group alarm identification
        alarm.setGroupAlarm(parseObject.getObjectId());

        return alarm;
    }

    /**
     * Get ParseObject from an Alarm object
     *
     * @param alarm Alarm object to convert from
     * @return ParseObject filled with the Alarm objects values
     */
    private static ParseObject getParseObjectFromAlarm(Alarm alarm) {

        // Put all values from Alarm in a new ParseObject
        ParseObject parseAlarm = new ParseObject(TABLE_ALARMS);

        boolean[] tmp = alarm.getDays();
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

    /**
     * Get the status of an alarm of a specific user in a specific group.
     *
     * @param user Username, string.
     * @param group Groupname, string.
     * @return String, the alarm status.
     */
    public static String getAlarmStatusUserPerGroup (String user, String group) {

        String output = "";
        ParseObject alarmStatusObject = null;

        // Query alarmstatus table on user and group
        ParseQuery<ParseObject> query = ParseQuery.getQuery(TABLE_ALARMSTATUS);
        query.whereEqualTo(COLUMN_USERNAME, user);
        query.whereEqualTo(COLUMN_GROUP, group);

        try {
            alarmStatusObject = query.getFirst();
        } catch (ParseException e) {
            Log.d(TAG, "No row matching query: " + group + ", " + user);
        }

        // Get the alarm status string from alarm status object
        output = alarmStatusObject.getString(COLUMN_ALARMSTATUS);

        Log.d(TAG, "Status is: " + output);

        return output;
    }

    /**
     * Set the currents users alarm status within a specific group.
     * @param group The groupname, string.
     * @param status The status string to set.
     */
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

    /**
     * Set the alarmsignal for a specific user in a specific group.
     * @param group Name of group.
     * @param user Name of user.
     * @param alarmSignal Alarm signal string to set.
     */
    public static void setAlarmSignal (String group, String user, String alarmSignal) {

        ParseObject alarmStatus = null;

        // Find user and group row if exist
        ParseQuery<ParseObject> query = ParseQuery.getQuery(TABLE_ALARMSTATUS);
        query.whereContains(COLUMN_USERNAME, user);
        query.whereContains(COLUMN_GROUP, group);

        try {
            alarmStatus = query.getFirst();
            // Put the requested alarm signal to the alarm status object
            alarmStatus.put(COLUMN_ALARMSIGNAL, alarmSignal);
        } catch (ParseException e) {
            Log.d(TAG, "No row matching query: " + group + ", " + user);
        }

        try {
            // Save status
            alarmStatus.save();
            Log.d(TAG, "User: " + user + ", Group: " + group + ", AlarmSignal: " + alarmSignal);
        } catch (ParseException e) {
            Log.d(TAG, "Could not set alarmSignal");
        }
    }

    /**
     * Set the current alarmsignal for a specific user in a specific group.
     * @param group Name of group.
     * @param user Name of user.
     * @return The current alarm signal, string.
     */
    public static String getAlarmSignal(String group, String user) {

        String output = "";
        ParseObject alarmSignalObject = null;

        // Query the alarmstatus table on user and group
        ParseQuery<ParseObject> query = ParseQuery.getQuery(TABLE_ALARMSTATUS);
        query.whereEqualTo(COLUMN_USERNAME, user);
        query.whereEqualTo(COLUMN_GROUP, group);

        try {
            alarmSignalObject = query.getFirst();
        } catch (ParseException e) {
            Log.d(TAG, "No row matching query: " + group + ", " + user);
        }

        // Get the alarmsignal string from current object
        output = alarmSignalObject.getString(COLUMN_ALARMSIGNAL);

        Log.d(TAG, "Alarm signal is: " + output);

        return output;
    }

    /**
     * Set a specific user has a status that allows the user to be punished or not.
     * @param group Group name.
     * @param user User name.
     * @param punishable Boolean, if can be punished, true, otherwise false.
     */
    public static void setPunishable(String group, String user, Boolean punishable) {
        ParseObject alarmStatus = null;

        // Find user and group row if exist
        ParseQuery<ParseObject> query = ParseQuery.getQuery(TABLE_ALARMSTATUS);
        query.whereContains(COLUMN_USERNAME, user);
        query.whereContains(COLUMN_GROUP, group);

        try {
            alarmStatus = query.getFirst();
            // Set the column to boolean value
            alarmStatus.put(COLUMN_PUNISHABLE, punishable);
        } catch (ParseException e) {
            Log.d(TAG, "No row matching query: " + group + ", " + user);
        }

        try {
            alarmStatus.save();
            Log.d(TAG, "User: " + user + ", Group: " + group + ", Punishable: " + punishable);
        } catch (ParseException e) {
            Log.d(TAG, "Could not set punishable");
        }
    }

    /**
     * Get the status of a specific user if the user can be punished or not.
     * @param group Group name.
     * @param user User name.
     * @return A boolean, True if can be punished, false otherwise.
     */
    public static Boolean getPunishable(String group, String user) {

        Boolean output = false;
        ParseObject alarmSignalObject = null;

        // Query alarmstatus table on user and group.
        ParseQuery<ParseObject> query = ParseQuery.getQuery(TABLE_ALARMSTATUS);
        query.whereEqualTo(COLUMN_USERNAME, user);
        query.whereEqualTo(COLUMN_GROUP, group);

        try {
            alarmSignalObject = query.getFirst();
        } catch (ParseException e) {
            Log.d(TAG, "No row matching query: " + group + ", " + user);
        }

        // Read the punishable column for true or false
        output = alarmSignalObject.getBoolean(COLUMN_PUNISHABLE);

        Log.d(TAG, "Punishable: " + output);

        return output;
    }
}

