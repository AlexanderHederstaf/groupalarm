package com.groupalarm.asijge.groupalarm;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TimePicker;

import com.groupalarm.asijge.groupalarm.Data.Alarm;


public class EditAlarmActivity extends ActionBarActivity {

    private TimePicker timePickerAlarm;
    private Intent parentIntent;
    private Alarm newAlarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        parentIntent = getIntent();
        newAlarm = (Alarm) parentIntent.getSerializableExtra("alarm");
        //newAlarm = new Alarm();
        timePickerAlarm = (TimePicker) findViewById(R.id.timePickerAlarm);
        editMessage("test edit message");
        parentIntent.putExtra("newAlarm", newAlarm);
        setResult(RESULT_OK, parentIntent);
        finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_alarm, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void editTime(int hour, int min) {
        newAlarm.setTime(hour, min);
    }

    public void editMessage(String message) {
        newAlarm.setMessage(message);
    }

    public void editSnooze(Alarm.Snooze value) {
        newAlarm.setSnoozeInterval(value);
    }

    public void editDay(int day, boolean value) {
        newAlarm.setDay(day, value);
    }

    public void editActive(boolean active) {
        newAlarm.setActive(active);
    }
}
