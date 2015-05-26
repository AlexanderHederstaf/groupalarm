package com.groupalarm.asijge.groupalarm.AlarmManaging;

import android.content.Context;
import android.util.Log;

import com.groupalarm.asijge.groupalarm.Data.Alarm;

import java.util.List;

/**
 * Created by Alexander on 2015-05-25.
 */
public class SetAlarms implements Runnable {

    private Context context;

    public SetAlarms(Context context) {
        this.context = context;
    }

    @Override
    public void run() {

        Log.d("SetAlarms", "thread started");

        AlarmHelper.cancelAlarms(context);

        for (Alarm alarm : AlarmHelper.getAlarms()) {
            if (alarm.isGroupAlarm()) {
                AlarmHelper.removeAlarm(alarm.getId());
            }
        }

        List<Alarm> sharedAlarms = ParseHelper.getAllRemoteAlarmsForUser();

        for (Alarm alarm : sharedAlarms) {
            AlarmHelper.addAlarm(alarm);
        }

        Log.d("SetAlarms", "set alarm started");

        AlarmHelper.setAlarms(context);

        Log.d("SetAlarms", "set alarm done");

    }
}
