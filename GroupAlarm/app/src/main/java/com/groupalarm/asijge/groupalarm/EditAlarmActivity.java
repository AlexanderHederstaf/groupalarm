package com.groupalarm.asijge.groupalarm;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TimePicker;

import com.groupalarm.asijge.groupalarm.Data.Alarm;


public class EditAlarmActivity extends ActionBarActivity {

    private Intent parentIntent;
    private Alarm newAlarm;

    private TimePicker timePickerAlarm;
    private EditText alarmMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        parentIntent = getIntent();
        newAlarm = (Alarm) parentIntent.getSerializableExtra("alarm");

        timePickerAlarm = (TimePicker) findViewById(R.id.timePickerAlarm);
        alarmMessage = (EditText) findViewById(R.id.editTextAlarmMessage);
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

        // Send the new Alarm object to parent activity if save button has been pressed
        if (id == R.id.editAlarm_save) {

            // Set alarm object with new data
            editMessage(alarmMessage.getText().toString());
            editTime(timePickerAlarm.getCurrentHour(), timePickerAlarm.getCurrentMinute());

            // Add new alarm to parent intent and finish this activity
            parentIntent.putExtra("newAlarm", newAlarm);
            setResult(RESULT_OK, parentIntent);
            finish();

            return true;
        }

        // If cancel button is pressed, finish activity and send back a cancel message
        if (id == R.id.editAlarm_cancel) {
            setResult(RESULT_CANCELED);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Action method for when a radio button has been clicked on.
     * Calling the editSnooze function with data corresponding to snooze radio-boxes status.
     * @param view
     */
    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_snooze_0:
                if (checked)
                    // No snooze
                    editSnooze(Alarm.Snooze.NO_SNOOZE);
                    break;
            case R.id.radio_snooze_5:
                if (checked)
                    // 5 min snooze
                    editSnooze(Alarm.Snooze.FIVE);
                    break;
            case R.id.radio_snooze_10:
                if (checked)
                    // 10 min snooze
                    editSnooze(Alarm.Snooze.TEN);
                    break;
            case R.id.radio_snooze_15:
                if (checked)
                    // 15 min snooze
                    editSnooze(Alarm.Snooze.FIFTEEN);
                    break;
        }
    }

    /**
     * Action method for when a checkbox has been checked or unchecked.
     * Calling the editDay function with data corresponding to weekday checkboxes status.
     * @param view
     */
    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.checkbox_monday:
                if (checked)
                    editDay(0, true);
                else
                    editDay(0, false);
                break;
            case R.id.checkbox_tuesday:
                if (checked)
                    editDay(1, true);
                else
                    editDay(1, false);
                break;
            case R.id.checkbox_wednesday:
                if (checked)
                    editDay(2, true);
                else
                    editDay(2, false);
                break;
            case R.id.checkbox_thursday:
                if (checked)
                    editDay(3, true);
                else
                    editDay(3, false);
                break;
            case R.id.checkbox_friday:
                if (checked)
                    editDay(4, true);
                else
                    editDay(4, false);
                break;
            case R.id.checkbox_saturday:
                if (checked)
                    editDay(5, true);
                else
                    editDay(5, false);
                break;
            case R.id.checkbox_sunday:
                if (checked)
                    editDay(6, true);
                else
                    editDay(6, false);
                break;
        }
    }

    /**
     *
     * @param hour
     * @param min
     */
    public void editTime(int hour, int min) {
        newAlarm.setTime(hour, min);
    }

    /**
     *
     * @param message
     */
    public void editMessage(String message) {
        newAlarm.setMessage(message);
    }

    /**
     *
     * @param value
     */
    public void editSnooze(Alarm.Snooze value) {
        newAlarm.setSnoozeInterval(value);
    }

    /**
     *
     * @param day
     * @param value
     */
    public void editDay(int day, boolean value) {
        newAlarm.setDay(day, value);
    }

    /**
     * 
     * @param active
     */
    public void editActive(boolean active) {
        newAlarm.setActive(active);
    }
}
