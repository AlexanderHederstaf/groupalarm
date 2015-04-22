package com.groupalarm.asijge.groupalarm;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.groupalarm.asijge.groupalarm.Data.Alarm;
import com.groupalarm.asijge.groupalarm.Data.ListRowItem;

import java.util.ArrayList;
import java.util.HashMap;
import com.groupalarm.asijge.groupalarm.Data.Alarm;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    private List<Alarm> alarms = new LinkedList<Alarm>();

    ListView listView;
    List<ListRowItem> rowItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Alarm alarm = new Alarm();
        alarm.setTime(13,30);

        Alarm alarm2 = new Alarm();
        alarm2.setTime(17,50);

        alarms.add(alarm);
        alarms.add(alarm2);

        String[] timeList = new String[] {"" + alarm.toString(), "13:37"};

        Integer[] imageList = new Integer[] {R.drawable.ic_alarm_image, R.drawable.ic_alarm_image};


        rowItems = new ArrayList<ListRowItem>();
        for (int i = 0; i < timeList.length; i++) {
            ListRowItem item = new ListRowItem(R.drawable.ic_alarm_image, alarms.get(i));
            rowItems.add(item);
        }

        listView = (ListView) findViewById(R.id.alarmlist);
        CustomListViewAdapter adapter = new CustomListViewAdapter(this,
                R.layout.alarm_list_item, rowItems);
        listView.setAdapter(adapter);

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
            Intent newAlarmActivity = new Intent(this, AlarmActivity.class);
            startActivity(newAlarmActivity);
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
}
