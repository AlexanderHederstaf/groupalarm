package com.groupalarm.asijge.groupalarm.Alarm;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * This class is a service that starts when an alarm goes off.
 * It starts the Alarm Activity and the alarm sound.
 */
public class AlarmService extends Service {

    public static final String TAG = "AlarmService";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "AlarmService Started");
        Intent i = new Intent();
        i.setClass(this, AlarmScreenActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        return super.onStartCommand(intent, flags, startId);
    }
}
