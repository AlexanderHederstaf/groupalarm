package com.groupalarm.asijge.groupalarm.AlarmManaging;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.groupalarm.asijge.groupalarm.AlarmScreenActivity;

/**
 * This class is a service that starts when an alarm goes off.
 * It starts the Alarm Activity which displays a screen and plays a sound.
 */
public class AlarmService extends Service {

    /**
     * Debug TAG for the class.
     */
    public static final String TAG = "AlarmService";

    /**
     * {@inheritDoc}
     */
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "AlarmService Started");
        intent.setClass(this, AlarmScreenActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);


        return super.onStartCommand(intent, flags, startId);
    }
}
