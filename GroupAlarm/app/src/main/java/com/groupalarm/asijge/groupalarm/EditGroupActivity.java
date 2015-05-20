package com.groupalarm.asijge.groupalarm;

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
import com.groupalarm.asijge.groupalarm.Data.Alarm;
import com.groupalarm.asijge.groupalarm.List.AlarmListViewAdapter;
import com.groupalarm.asijge.groupalarm.List.GroupListViewAdapter;
import com.groupalarm.asijge.groupalarm.List.UserListViewAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class EditGroupActivity extends ActionBarActivity {

    private static final int NEW_ALARM_CODE = 999;

    private ListView userListView;
    private ListView alarmListView;

    private List<String> userItems;
    private List<Alarm> alarmItems;

    private AlarmListViewAdapter alarmAdapter;
    private UserListViewAdapter userAdapter;

    private Runnable runListUpdate;

    private String groupName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_group);

        groupName = getIntent().getExtras().getString("group");
        getSupportActionBar().setTitle(groupName);

        userItems = new ArrayList<String>();
        alarmItems = new ArrayList<Alarm>();

        userListView = (ListView) findViewById(R.id.group_members_listView);
        alarmListView = (ListView) findViewById(R.id.group_alarm_listView);

        userAdapter = new UserListViewAdapter(this, R.layout.user_list_item, userItems);
        userListView.setAdapter(userAdapter);

        alarmAdapter = new AlarmListViewAdapter(this, R.layout.alarm_list_item, alarmItems);
        alarmListView.setAdapter(alarmAdapter);
        registerForContextMenu(alarmListView);

        final String[] getUsersForGroupPlaceholder = new String[]{"Conan", "Arnold", "Sarah", "Governator", "Z3B0"};

        runListUpdate = new Runnable() {
            public void run() {
                userItems.clear();
                alarmItems.clear();

                for(String user : getUsersForGroupPlaceholder) {
                    userItems.add(user);
                }

                for(Alarm alarm : ParseHelper.getAlarmsFromGroup(groupName)) {
                    alarmItems.add(alarm);
                }
            }
        };
        runOnUiThread(runListUpdate);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_group, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        if (id == R.id.action_add_alarm) {
            Alarm newAlarm = new Alarm(AlarmHelper.getNewId());
            // Set Alarm default values
            Calendar tmp = Calendar.getInstance();

            newAlarm.setTime(tmp.get(Calendar.HOUR_OF_DAY), tmp.get(Calendar.MINUTE));
            newAlarm.setMessage("");
            newAlarm.setSnoozeInterval(Alarm.Snooze.TEN);
            newAlarm.setActive(true);
            for (int i = 0; i < 7; i++) {
                newAlarm.setDay(i, false);
            }

            Intent newAlarmActivity = new Intent(this, EditAlarmActivity.class);
            newAlarmActivity.putExtra("alarm", newAlarm);
            startActivityForResult(newAlarmActivity, NEW_ALARM_CODE);

            return true;
        }

        if (id == R.id.action_add_member) {
            return true;
        }

        if (id == R.id.action_leave) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.group_alarm_listView) {
            ListView lv = (ListView) v;
            AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) menuInfo;
            Alarm listItem = (Alarm) lv.getItemAtPosition(acmi.position);

            menu.setHeaderTitle(listItem.getMessage());
            menu.add("Edit");
            menu.add("Delete");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Alarm listItem = (Alarm) alarmListView.getItemAtPosition(info.position);
        int alarmId = listItem.getId();
        if (item.getTitle() == "Edit") {
            //editAlarm(alarmId);
            return true;
        }
        else if (item.getTitle() == "Delete") {
            //AlarmHelper.removeAlarm(alarmId, this);

            //runOnUiThread(runListUpdate);
            return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NEW_ALARM_CODE) {
            if (resultCode == RESULT_OK) {
                Alarm alarm = (Alarm) data.getSerializableExtra("EditedAlarm");

                //TODO: Post alarm to network
                ParseHelper.addNewAlarmToGroup(alarm, groupName);

                runOnUiThread(runListUpdate); // update list gui
            }
        }

    }
}
