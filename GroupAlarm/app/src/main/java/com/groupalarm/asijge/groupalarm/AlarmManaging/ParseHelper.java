package com.groupalarm.asijge.groupalarm.AlarmManaging;

import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseObject;
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

    public static void createGroup(String name) {
        final ParseObject newGroup = new ParseObject(TABLE_GROUPS);
        newGroup.put(COLUMN_NAME, name);

        newGroup.saveInBackground(new SaveCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    Log.d(TAG, "Alarm " + newGroup.toString() + " stored in Parse cloud.");
                } else {
                    Log.d(TAG, "Alarm " + newGroup.toString() + " was not stored.");
                }
            }
        });
    }

    public static List<String> getGroupsForUser() {
        List<String> groupList = new LinkedList<String>();

        groupList.add("Grupp1");
        groupList.add("Grupp2");

        return groupList;
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

