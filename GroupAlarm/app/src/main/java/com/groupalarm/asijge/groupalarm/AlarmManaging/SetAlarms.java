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

        List<Alarm> sharedAlarms = ParseHelper.getAllRemoteAlarmsForUser();

        AlarmHelper.cancelAlarms(context);

        for (Alarm alarm : AlarmHelper.getAlarms()) {
            if (alarm.isGroupAlarm()) {
                AlarmHelper.removeAlarm(alarm.getId());
            }
        }
        for (Alarm alarm : sharedAlarms) {
            AlarmHelper.addAlarm(alarm);
        }

        AlarmHelper.setAlarms(context);
    }
}
