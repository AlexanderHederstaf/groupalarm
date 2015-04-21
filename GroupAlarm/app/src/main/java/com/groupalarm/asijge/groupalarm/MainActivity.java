package com.groupalarm.asijge.groupalarm;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.groupalarm.asijge.groupalarm.Data.Alarm;

import java.util.LinkedList;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    private List<Alarm> alarms = new LinkedList<Alarm>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
            // Send list of alarms?
            return true;
        }


        return super.onOptionsItemSelected(item);
    }
}
