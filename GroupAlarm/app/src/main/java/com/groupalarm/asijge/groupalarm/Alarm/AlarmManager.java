package com.groupalarm.asijge.groupalarm.Alarm;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.groupalarm.asijge.groupalarm.Data.Alarm;

/**
 *
 */
public class AlarmManager extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

    }

    private static PendingIntent createIntent(Context context, Alarm alarm){
        return null;
    }
}
