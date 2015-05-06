package com.groupalarm.asijge.groupalarm;

import android.app.LauncherActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;

import com.groupalarm.asijge.groupalarm.Alarm.AlarmManagerHelper;
import com.groupalarm.asijge.groupalarm.Data.Alarm;
import com.groupalarm.asijge.groupalarm.Data.AlarmDB;
import com.groupalarm.asijge.groupalarm.Data.ListRowItem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import com.groupalarm.asijge.groupalarm.Data.Alarm;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    private static final int EDIT_ALARM_CODE = 998;
    private static final int NEW_ALARM_CODE = 999;
    private static final int REMOVE_ALARM_CODE = 997;

    private ListView listView;
    private List<ListRowItem> rowItems;
    private CustomListViewAdapter adapter;
    private Runnable runListUpdate;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rowItems = new ArrayList<ListRowItem>();
        for (int i = 0; i < AlarmManagerHelper.getAlarms().size(); i++) {
            ListRowItem item = new ListRowItem(R.drawable.ic_alarm_image, AlarmManagerHelper.getAlarms().get(i));
            rowItems.add(item);
        }

        listView = (ListView) findViewById(R.id.alarmlist);
        adapter = new CustomListViewAdapter(this, R.layout.alarm_list_item, rowItems);
        listView.setAdapter(adapter);
        registerForContextMenu(listView);
        /*listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //TODO: Add code for what happens when you click on a alarm
                Log.d(TAG, "Something was clicked");
                //runOnUiThread(runListUpdate);
                //((CheckBox) findViewById(R.id.on_off)).isChecked();
            }
        });*/

        runListUpdate = new Runnable(){
            public void run(){
                Log.d(TAG, "runListUpdate");
                //reload content
                rowItems.clear();
                for (int i = 0; i < AlarmManagerHelper.getAlarms().size(); i++) {
                    ListRowItem item = new ListRowItem(R.drawable.ic_alarm_image, AlarmManagerHelper.getAlarms().get(i));
                    rowItems.add(item);
                }
                adapter.notifyDataSetChanged();
                //listView.invalidateViews();
                //listView.refreshDrawableState();
            }
        };

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            Alarm newAlarm = new Alarm(AlarmDB.getInstance().getNewId());
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
            //AlarmManagerHelper.cancelAlarms(this);
           // Log.d(TAG, "Alarms cancelled");
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
                AlarmManagerHelper.removeAlarm(editedAlarm.getId(), this);
                AlarmManagerHelper.addAlarm(editedAlarm);
                AlarmManagerHelper.setAlarms(this);
                runOnUiThread(runListUpdate);
            }
        }

        if (requestCode == NEW_ALARM_CODE) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                Alarm updatedNewAlarm = (Alarm) data.getSerializableExtra("EditedAlarm");
                AlarmManagerHelper.addAlarm(updatedNewAlarm);
                Log.d(TAG, "Alarm added");
                AlarmManagerHelper.setAlarms(this);
                Log.d(TAG, "Alarms set");
                runOnUiThread(runListUpdate); // update list gui
            }
        }

        if (requestCode == REMOVE_ALARM_CODE) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {

                Log.d(TAG, "Remove alarm ok");
                runOnUiThread(runListUpdate); // update list gui
            }
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.alarmlist) {
            ListView lv = (ListView) v;
            AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) menuInfo;
            ListRowItem listItem = (ListRowItem) lv.getItemAtPosition(acmi.position);

            menu.setHeaderTitle(listItem.getAlarm().getMessage());
            menu.add("Edit");
            menu.add("Delete");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        ListRowItem listItem = (ListRowItem) listView.getItemAtPosition(info.position);
        int alarmId = listItem.getAlarm().getId();
        if (item.getTitle() == "Edit") {
            editAlarm(alarmId);
            return true;
        }
        else if (item.getTitle() == "Delete") {
            AlarmManagerHelper.removeAlarm(alarmId, this);

            runOnUiThread(runListUpdate);
            return true;
        }
        return super.onContextItemSelected(item);
    }

    private void editAlarm(int alarmId) {
        Alarm alarm = AlarmManagerHelper.getAlarm(alarmId);

        Intent intent = new Intent(this, EditAlarmActivity.class);
        intent.putExtra("alarm", alarm);
        startActivityForResult(intent, EDIT_ALARM_CODE);
    }
}
