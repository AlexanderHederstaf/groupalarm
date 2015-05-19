package com.groupalarm.asijge.groupalarm;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.groupalarm.asijge.groupalarm.AlarmManaging.AlarmHelper;
import com.groupalarm.asijge.groupalarm.Data.Alarm;
import com.groupalarm.asijge.groupalarm.Data.ListRowItem;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class GroupActivity extends ActionBarActivity {

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

    private static final int EDIT_ALARM_CODE = 998;
    private static final int NEW_ALARM_CODE = 999;
    private static final int REMOVE_ALARM_CODE = 997;

    private ListView listView;
    private List<ListRowItem> rowItems;
    private CustomListViewAdapter adapter;
    private Runnable runParseListUpdate;

    private static final String TAG = "GroupActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        getSupportActionBar().setTitle("Cloud Alarms");

        rowItems = new ArrayList<ListRowItem>();
        listView = (ListView) findViewById(R.id.group_alarmlist);
        adapter = new CustomListViewAdapter(this, R.layout.alarm_list_item, rowItems);
        listView.setAdapter(adapter);

        //registerForContextMenu(listView);

        runParseListUpdate = new Runnable() {
            public void run() {
                Log.d(TAG, "runParseListUpdate");
                //reload content
                rowItems.clear();
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Alarms");
                query.findInBackground(new FindCallback<ParseObject>() {
                    public void done(List<ParseObject> alarmList, ParseException e) {
                        if (e == null) {
                            Log.d(TAG, "Retrieved " + alarmList.size() + " alarms");
                            for (int i = 0; i < alarmList.size(); i++) {
                                Log.d(TAG, "ParseObject index: " + i + " has id: " + alarmList.get(i).getInt("ID"));
                                Alarm alarmToAdd = getAlarmFromParseObject(alarmList.get(i));
                                ListRowItem item = new ListRowItem(R.drawable.ic_alarm_image, alarmToAdd);
                                rowItems.add(item);
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "Error: " + e.getMessage());
                        }
                    }
                });
            }
        };
        runOnUiThread(runParseListUpdate);
    }


    private Alarm getAlarmFromParseObject(ParseObject parseObject) {

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_group, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_new) {
            // Start the editor activity with a new Alarm.
            Alarm newAlarm = null;

            newAlarm = new Alarm(getNewAlarmId());
            Log.d(TAG, "New alarm created. " + newAlarm.toString() + " Id:" + newAlarm.getId());

            // Set Alarm default values
            Calendar tmp = Calendar.getInstance();

            newAlarm.setTime(tmp.get(Calendar.HOUR_OF_DAY), tmp.get(Calendar.MINUTE));
            newAlarm.setMessage("");
            newAlarm.setSnoozeInterval(Alarm.Snooze.TEN);
            newAlarm.setActive(false);
            for (int i = 0; i < 7; i++) {
                newAlarm.setDay(i, false);
            }

            Intent newAlarmActivity = new Intent(this, EditAlarmActivity.class);
            newAlarmActivity.putExtra("alarm", newAlarm);
            startActivityForResult(newAlarmActivity, NEW_ALARM_CODE);

            return true;
        }

        if (id == R.id.action_delete) {
            // Start the remove activity.
            Intent removeAlarmActivity = new Intent(this, RemoveActivity.class);
            startActivityForResult(removeAlarmActivity, REMOVE_ALARM_CODE);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // Check which request we're responding to
        if (requestCode == EDIT_ALARM_CODE) {
            if (resultCode == RESULT_OK) {
                Alarm editedAlarm = (Alarm) data.getSerializableExtra("EditedAlarm");
                AlarmHelper.removeAlarm(editedAlarm.getId(), this);
                AlarmHelper.addAlarm(editedAlarm);
                AlarmHelper.setAlarms(this);
                runOnUiThread(runParseListUpdate);
            }
        }

        if (requestCode == NEW_ALARM_CODE) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                Alarm updatedNewAlarm = (Alarm) data.getSerializableExtra("EditedAlarm");
                storeAlarmInParse(updatedNewAlarm);
            }
        }

        if (requestCode == REMOVE_ALARM_CODE) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {

                Log.d(TAG, "Remove alarm ok");
                runOnUiThread(runParseListUpdate); // update list gui
            }
        }
    }

    private void storeAlarmInParse(Alarm updatedNewAlarm) {
        final ParseObject alarmToStore = getParseObjectFromAlarm(updatedNewAlarm);
        alarmToStore.saveInBackground(new SaveCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    Log.d(TAG, "Alarm " + alarmToStore.toString() + " stored in Parse cloud.");
                    runOnUiThread(runParseListUpdate); // update list gui
                } else {
                    Log.d(TAG, "Alarm " + alarmToStore.toString() + " was not stored.");
                }
            }
        });
    }

    private ParseObject getParseObjectFromAlarm(Alarm alarm) {

        boolean[] tmp = alarm.getDays();

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

        return values;
    }

    public int getNewAlarmId() {
        int value;

        ParseQuery<ParseObject> query = ParseQuery.getQuery(TABLE_ALARMS);
        query.orderByDescending(COLUMN_ID);

        ParseObject parseAlarm = null;

        try {
            parseAlarm = query.getFirst();
        } catch (ParseException e) {
            Log.d(TAG, "Couldn't find any Alarms in Parse cloud.");
        }

        if (parseAlarm == null) {
            Log.d(TAG, "parseAlarm is null");
            value = 0;
        } else {
            value = parseAlarm.getInt(COLUMN_ID) + 1;
        }

        return value;
    }

}
