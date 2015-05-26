package com.groupalarm.asijge.groupalarm;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.groupalarm.asijge.groupalarm.AlarmManaging.AlarmHelper;
import com.groupalarm.asijge.groupalarm.AlarmManaging.ParseHelper;
import com.groupalarm.asijge.groupalarm.AlarmManaging.SetAlarms;
import com.groupalarm.asijge.groupalarm.Data.Alarm;
import com.groupalarm.asijge.groupalarm.AlarmManaging.AlarmDB;
import com.groupalarm.asijge.groupalarm.List.AlarmListViewAdapter;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Calendar;

import java.util.Collections;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    private static final int NEW_ALARM_CODE = 999;
    private static final int EDIT_ALARM_CODE = 998;
    private static final int REMOVE_ALARM_CODE = 997;

    private ListView listView;
    private List<Alarm> rowItems;
    private AlarmListViewAdapter adapter;
    private Runnable runListUpdate;

    private static final String TAG = "MainActivity";

    private Runnable refreshNetAlarm;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AlarmDB.initiate(this);

        rowItems = new ArrayList<Alarm>();
        for (Alarm alarm : AlarmHelper.getAlarms()) {
            rowItems.add(alarm);
        }

        listView = (ListView) findViewById(R.id.alarmlist);
        Collections.sort(rowItems);
        adapter = new AlarmListViewAdapter(this, R.layout.alarm_list_item, rowItems);
        listView.setAdapter(adapter);
        registerForContextMenu(listView);
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Alarm alarm = (Alarm) listView.getItemAtPosition(position);
//                if (!alarm.isGroupAlarm()) {
//                    editAlarm(alarm.getId());
//                }
//            }
//        });

        final Context context = this;

        refreshNetAlarm = new SetAlarms(this) {
            @Override
            public void run() {
                super.run();
                runOnUiThread(runListUpdate);
            }
        };

        runListUpdate = new Runnable(){
            public void run(){
                Log.d(TAG, "runListUpdate");
                //reload content
                rowItems.clear();
                for (Alarm alarm : AlarmHelper.getAlarms()) {
                    rowItems.add(alarm);
                }
                Collections.sort(rowItems);
                Log.d(TAG, "set data changed ");
                adapter.notifyDataSetChanged();
            }
        };

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume Main");
        (new Thread(refreshNetAlarm)).start();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_group) {

            Intent newGroupActivity = new Intent(this, GroupActivity.class);
            startActivity(newGroupActivity);

            return true;
        }


        if (id == R.id.action_new) {
            // Start the editor activity with a new Alarm.
            Alarm newAlarm = new Alarm(AlarmHelper.getNewId());
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

        if (id == R.id.action_logout) {
            ParseUser.logOut();
            Intent i = new Intent(this, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // Check which request we're responding to
        if (requestCode == EDIT_ALARM_CODE) {
            if (resultCode == RESULT_OK) {
                Alarm editedAlarm = (Alarm) data.getSerializableExtra("EditedAlarm");

                AlarmHelper.cancelAlarm(this, editedAlarm.getId());
                AlarmHelper.removeAlarm(editedAlarm.getId());
                AlarmHelper.addAlarm(editedAlarm);
//                AlarmHelper.setAlarms(this);
//                runOnUiThread(runListUpdate);
            }
        }

        if (requestCode == NEW_ALARM_CODE) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                Alarm updatedNewAlarm = (Alarm) data.getSerializableExtra("EditedAlarm");
                AlarmHelper.addAlarm(updatedNewAlarm);
//                Log.d(TAG, "Alarm added");
//                AlarmHelper.setAlarms(this);
//                Log.d(TAG, "Alarms set");
//                runOnUiThread(runListUpdate); // update list gui
            }
        }

        if (requestCode == REMOVE_ALARM_CODE) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {

//                Log.d(TAG, "Remove alarm ok");
//                runOnUiThread(runListUpdate); // update list gui
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.alarmlist) {
            ListView lv = (ListView) v;
            AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) menuInfo;
            Alarm alarm = (Alarm) lv.getItemAtPosition(acmi.position);

            menu.setHeaderTitle(alarm.getMessage());
            if (alarm.isGroupAlarm()) {
                menu.add("Go to group");
            } else {
                menu.add("Edit");
                menu.add("Delete");
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Alarm listItem = (Alarm) listView.getItemAtPosition(info.position);
        int alarmId = listItem.getId();
        if (item.getTitle() == "Edit") {
            editAlarm(alarmId);
            return true;
        }
        else if (item.getTitle() == "Delete") {
            AlarmHelper.cancelAlarm(this, alarmId);
            AlarmHelper.removeAlarm(alarmId);
            runOnUiThread(runListUpdate);
            return true;
        }
        if (item.getTitle() == "Go to group") {
            String groupName = ParseHelper.getGroupFromAlarm(listItem);

            Intent intent = new Intent(this, EditGroupActivity.class);
            intent.putExtra("group", groupName);
            startActivity(intent);
            return true;
        }
        return super.onContextItemSelected(item);
    }

    // Helper function to edit alarms. Start the activity for the Alarm data from ID alarmId.
    private void editAlarm(int alarmId) {
        Alarm alarm = AlarmHelper.getAlarm(alarmId);

        Intent intent = new Intent(this, EditAlarmActivity.class);
        intent.putExtra("alarm", alarm);
        startActivityForResult(intent, EDIT_ALARM_CODE);
    }
}
