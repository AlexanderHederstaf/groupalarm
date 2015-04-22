package com.groupalarm.asijge.groupalarm.Alarm;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * This class is a service that starts when an alarm goes off.
 * It starts the Alarm Activity and the alarm sound.
 */
public class AlarmService extends Service {


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }
}
