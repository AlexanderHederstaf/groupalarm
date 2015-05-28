package com.groupalarm.asijge.groupalarm.AlarmManaging;

import android.content.Context;
import android.util.Log;

import com.groupalarm.asijge.groupalarm.Data.Alarm;

import java.util.List;

/**
 * Created by Alexander on 2015-05-25.
 */
public class SetAlarms implements Runnable {

    // Store context to use in run() to set alarms.
    private Context context;

    /**
     * Create a new SetAlarms runnable. This runnable updates the remote alarms
     * and the local alarms and sets the active ones to ring.
     *
     * @param context The context to use for setting/cancelling alarms.
     */
    public SetAlarms(Context context) {
        this.context = context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {

        // First cancel and remove group alarms
        AlarmHelper.cancelAlarms(context);

        for (Alarm alarm : AlarmHelper.getAlarms()) {
            if (alarm.isGroupAlarm()) {
                AlarmHelper.removeAlarm(alarm.getId());
            }
        }

        // Get all group alarms and add them to the database
        List<Alarm> sharedAlarms = ParseHelper.getAllRemoteAlarmsForUser();

        for (Alarm alarm : sharedAlarms) {
            AlarmHelper.addAlarm(alarm);
        }

        // Set all alarms to ring.
        AlarmHelper.setAlarms(context);
    }
}
