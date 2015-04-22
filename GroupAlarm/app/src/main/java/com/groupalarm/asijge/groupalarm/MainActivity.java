package com.groupalarm.asijge.groupalarm;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.groupalarm.asijge.groupalarm.Alarm.AlarmManagerHelper;
import com.groupalarm.asijge.groupalarm.Data.Alarm;
import com.groupalarm.asijge.groupalarm.Data.ListRowItem;

import java.util.ArrayList;
import java.util.HashMap;
import com.groupalarm.asijge.groupalarm.Data.Alarm;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    ListView listView;
    List<ListRowItem> rowItems;
    CustomListViewAdapter adapter;
    Runnable runListUpdate;

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
        adapter = new CustomListViewAdapter(this,
                R.layout.alarm_list_item, rowItems);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                   //TODO: Add code for what happens when you click on a alarm
            }
        });

        runListUpdate = new Runnable(){
            public void run(){
                //reload content
                rowItems.clear();
                for (int i = 0; i < AlarmManagerHelper.getAlarms().size(); i++) {
                    ListRowItem item = new ListRowItem(R.drawable.ic_alarm_image, AlarmManagerHelper.getAlarms().get(i));
                    rowItems.add(item);
                }
                adapter.notifyDataSetChanged();
                listView.invalidateViews();
                listView.refreshDrawableState();
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
            Alarm newAlarm = new Alarm();
            newAlarm.setTime(13,37);

            Intent newAlarmActivity = new Intent(this, EditAlarmActivity.class);
            newAlarmActivity.putExtra("alarm", newAlarm);
            startActivityForResult(newAlarmActivity, 999);

            return true;
        }

        if (id == R.id.action_delete) {
            // Start the remove activity.
            Intent removeAlarmActivity = new Intent(this, RemoveActivity.class);//Oklart
            //removeAlarmActivity.putExtra("Alarms", (Serializable)alarms);//???
            startActivity(removeAlarmActivity);
            // Send list of alarms?
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // Check which request we're responding to
        if (requestCode == 999) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                Alarm updatedNewAlarm = (Alarm) data.getSerializableExtra("newAlarm");
                AlarmManagerHelper.addAlarm(updatedNewAlarm);
                runOnUiThread(runListUpdate); // update list gui
            }
        }
    }

}
