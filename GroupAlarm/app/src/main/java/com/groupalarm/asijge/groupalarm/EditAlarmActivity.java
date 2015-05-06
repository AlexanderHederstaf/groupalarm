package com.groupalarm.asijge.groupalarm;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.groupalarm.asijge.groupalarm.Data.Alarm;


public class EditAlarmActivity extends ActionBarActivity {

    // Constants
    private static final String TAG = "EditAlarmActivity";
    private static final CharSequence[] dayNames = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
    private static final CharSequence[] snoozeTimes = new CharSequence[Alarm.Snooze.values().length];

    // Assorted class fields
    private Intent parentIntent;
    private Alarm newAlarm;
    private boolean[] resultBool;
    private int resultInt;

    // UI elements
    private EditText alarmMessage;
    private TextView alarmTime;
    private TextView alarmDays;
    private TextView snoozeInterval;

    /**
     * Activity starts with this method.
     * Instantiate class fields and update GUI.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        parentIntent = getIntent();
        newAlarm = (Alarm) parentIntent.getSerializableExtra("alarm");

        alarmMessage = (EditText) findViewById(R.id.editTextAlarmMessage);

        alarmTime = (TextView) findViewById(R.id.textTime);
        alarmDays = (TextView) findViewById(R.id.textAlarmDay);
        snoozeInterval = (TextView) findViewById(R.id.textSnoozeInterval);

        updateAlarmTimeTextView();

        if (newAlarm.getDays().length == 7) {
            for (int i = 0; i < 7; i++) {
                if (newAlarm.getDays()[i] == true) {
                    updateAlarmDayTextView();
                    break;
                }
            }
        }

        if (newAlarm.getSnoozeInterval() != null) {
            updateSnoozeIntervalTextView();
        }
    }

    /**
     * Listens for click events.
     * @param v
     */
    public void onClick(View v) {

        if (v.getId() == R.id.textAlarmDay) {
            showAlarmDaysDialog();
        }

        if (v.getId() == R.id.textSnoozeInterval) {
            showSnoozeIntervalDialog();
        }

        if (v.getId() == R.id.textTime) {
            showTimePickerDialog();
        }

    }

    /**
     * Displays the time picker dialog.
     */
    private void showTimePickerDialog() {

        int minute = newAlarm != null ? newAlarm.getMinute() : 0;
        int hour = newAlarm != null ? newAlarm.getHour() : 9;

        // Launch Time Picker Dialog
        TimePickerDialog tpd = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                // Store new time in Alarm object
                newAlarm.setTime(hourOfDay, minute);
                updateAlarmTimeTextView();
            }
        }, hour, minute, true);

        tpd.show();
    }

    /**
     * Displays the current alarm time in text view.
     */
    private void updateAlarmTimeTextView() {
        String output = newAlarm.getHour() + ":";
        // Format the time string for the 0-9 or the 10-59 min span.
        if (newAlarm.getMinute() < 10) {
            output += "0" + newAlarm.getMinute();
        } else {
            output += newAlarm.getMinute();
        }
        alarmTime.setText(output);
    }

    /**
     * Displays the snooze interval dialog.
     */
    private void showSnoozeIntervalDialog() {
        // Use an AlertDialog.Builder to create a custom dialog box.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Check the values of the newAlarm field
        if (newAlarm.getSnoozeInterval() != null) {
            resultInt = newAlarm.getSnoozeInterval().ordinal();
        } else {
            resultInt = 0;
        }

        // Populate the snoozeTimes array from enum values
        for (int i = 0; i < Alarm.Snooze.values().length; i++) {
            snoozeTimes[i] = Alarm.Snooze.values()[i].getValue() + " min";
        }

        // Dialog layout configurations
        builder.setSingleChoiceItems(snoozeTimes, resultInt, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int item) {
                    Log.d(TAG, "New selection: " + item);
                    resultInt = item;
                }
            })
            .setTitle("Set snooze interval")
            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    // Okey'd.
                    Log.d(TAG, "SnoozeIntervalDialog pressed OK button. Choice: " + resultInt);
                    newAlarm.setSnoozeInterval(Alarm.Snooze.values()[resultInt]);
                    updateSnoozeIntervalTextView();
                }
            })
            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    // Cancelled.
                    Log.d(TAG, "SnoozeIntervalDialog pressed OK button");
                }
            });

        // Create and show the configured AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Updates the Snooze interval text view with data from the newAlarm field
     */
    private void updateSnoozeIntervalTextView() {
        snoozeInterval.setText(newAlarm.getSnoozeInterval().getValue() + " min");
    }

    /**
     * Displays the Alarm days dialog.
     */
    private void showAlarmDaysDialog() {
        // Use an AlertDialog.Builder to create a custom dialog box.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Check the values of the newAlarm field
        if (newAlarm.getDays().length == dayNames.length) {
            resultBool = newAlarm.getDays();
        } else {
            resultBool = new boolean[dayNames.length];
        }

        // Dialog layout configurations
        builder.setMultiChoiceItems(dayNames, resultBool, new DialogInterface.OnMultiChoiceClickListener() {
                public void onClick(DialogInterface dialogInterface, int item, boolean b) {
                    Log.d(TAG, String.format("%s: %s", dayNames[item], b));
                    resultBool[item] = b;
                }
            })
            .setTitle("Set alarm days")
            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    // Okey'd.
                    Log.d(TAG, "AlarmDaysDialog pressed OK button");
                    for (int i = 0; i < resultBool.length; i++) {
                        newAlarm.setDay(i, resultBool[i]);
                    }
                    updateAlarmDayTextView();
                }
            })
            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    // Cancelled.
                    Log.d(TAG, "AlarmDaysDialog pressed CANCEL button");
                }
            });

        // Create and show the configured AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Update the Alarm day text view UI element.
     */
    private void updateAlarmDayTextView() {
        boolean[] days = newAlarm.getDays();
        String output = "";
        // Prepare the output string
        for (int i = 0; i < days.length; i++) {
            if (days[i] == true) {
                if (output != "") {
                    output += ", ";
                }
                output += dayNames[i];
            }
        }
        alarmDays.setText(output);
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
            newAlarm.setMessage(alarmMessage.getText().toString());
            //editTime(timePickerAlarm.getCurrentHour(), timePickerAlarm.getCurrentMinute());

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
}
