package com.groupalarm.asijge.groupalarm.AlarmManaging;

import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by sehlstedt on 19/05/15.
 */
public class ParseHelper {

    private static final String TAG = "ParseHelper";

    private static final String TABLE_GROUPS = "Groups";
    private static final String TABLE_USER = "User";

    private static final String COLUMN_NAME = "Name";
    private static final String COLUMN_USERS = "Users";
    private static final String COLUMN_USERNAME = "username";

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
        ParseUser userObject = null;
        ParseObject groupObject = null;

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo(COLUMN_USERNAME, user);
        try {
            userObject = query.getFirst();
        } catch (ParseException e) {
            Log.d(TAG, "addUserToGroup struggle to query the Parse cloud for user.");
            e.printStackTrace();
        }

        ParseQuery<ParseObject> query1 = ParseQuery.getQuery(TABLE_GROUPS);
        query1.whereEqualTo(COLUMN_NAME, group);
        try {
            groupObject = query1.getFirst();
        } catch (ParseException e) {
            Log.d(TAG, "addUserToGroup struggle to query the Parse cloud for group.");
            e.printStackTrace();
        }

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

    public static void addNewAlarmToGroup() {
        // not implemented
    }

    public static void getAllRemoteAlarmsForUser() {
        // not implemented
    }

    public static void userSnoozedAlarm() {
        // not implemented
    }
}

