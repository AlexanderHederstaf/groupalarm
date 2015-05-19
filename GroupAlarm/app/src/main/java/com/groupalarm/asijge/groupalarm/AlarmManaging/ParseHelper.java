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

    private static final String COLUMN_NAME = "Name";
    private static final String COLUMN_USERS = "Users";

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

    public static List<String> getGroupsForUser() throws ParseException {

        ParseUser user = ParseUser.getCurrentUser();

        ParseQuery<ParseObject> query = ParseQuery.getQuery(TABLE_GROUPS);
        query.whereEqualTo(COLUMN_USERS, user);
        List<ParseObject> groupObjectList = query.find();

        List<String> groupStringList = new LinkedList<String>();
        for (ParseObject parseObject : groupObjectList) {
            groupStringList.add(parseObject.getString(COLUMN_NAME));
        }

        return groupStringList;
    }

    public static void addUserToGroup() {
        // not implemented
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

