package com.groupalarm.asijge.groupalarm;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.PowerManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.groupalarm.asijge.groupalarm.AlarmManaging.AlarmHelper;
import com.groupalarm.asijge.groupalarm.AlarmManaging.ParseHelper;
import com.parse.ParseUser;

/**
 *
 * @author asijge
 */
public class AlarmScreenActivity extends Activity {

    /**
     * Debug TAG for the AlarmScreenActivity.
     */
    public static final String TAG = "AlarmScreenActivity";

    /*
     * String constants for alarmStatus
     */
    private static final String STATUS_STOPPED = "stopped";
    private static final String STATUS_SNOOZED = "snoozed";
    private static final String STATUS_RINGING = "ringing";
    private static final String STATUS_OFF = "off";

    /**
     * Default timeout for the Alarm ringing, if no response within one minute
     * the wakelock is released.
     */
    public static final int WAKELOCK_TIMEOUT = 60 * 1000; // 1 minute
    private PowerManager.WakeLock lock;
    private MediaPlayer mediaPlayer;
    private String group;
    private boolean isGroup;
    private int alarmID;

    /**
     * {@inheritDoc}
     *
     * Creates the alarm screen based on the information passed in the Intent.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_screen);

        TextView message = ((TextView) findViewById(R.id.alarm_textView));
        message.setText(getIntent().getStringExtra("MESSAGE"));

        alarmID = getIntent().getIntExtra("ID", 0);

        isGroup = getIntent().getBooleanExtra("IS_GROUP", false);
        if (isGroup) {
            group = getIntent().getStringExtra("GROUP");
        }
        Log.d(TAG, "AlarmScreen started");

        //Ensure wakelock release
        Runnable releaseWakelock = new Runnable() {
            @Override
            public void run() {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

                if (lock != null && lock.isHeld()) {
                    lock.release();
                }
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = null;
                    if (isGroup) {
                        (new Thread(new Runnable() {
                            @Override
                            public void run() {
                                ParseHelper.setMyAlarmStatusPerGroup(group, STATUS_STOPPED);
                            }
                        })).start();
                    }
                }
            }
        };

        // Set up stop button to cancel sound and exit the Alarm Screen.
        Button wakeUp = (Button) findViewById(R.id.alarm_stop_button);
        wakeUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = null;
                    if (isGroup) {
                        (new Thread(new Runnable() {
                            @Override
                            public void run() {
                                ParseHelper.setMyAlarmStatusPerGroup(group, STATUS_STOPPED);
                                ParseHelper.setAlarmSignal(group, ParseUser.getCurrentUser().getUsername(), "classic_alarm");
                                ParseHelper.setPunishable(group, ParseUser.getCurrentUser().getUsername(), false);
                            }
                        })).start();
                    }
                }
                finish();
            }
        });

        /* Set up snooze button to cancel sound and exit the Alarm Screen, and sheduele a new
         * snooze alarm.
         */
        final int snoozeTime = getIntent().getIntExtra("SNOOZE", 0);
        final Context c = this;
        Button snooze = (Button) findViewById(R.id.alarm_snooze_button);
        snooze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (snoozeTime > 0) {
                    if (isGroup) {
                        AlarmHelper.setSnooze(c, snoozeTime, group);
                        (new Thread(new Runnable() {
                            @Override
                            public void run() {
                                ParseHelper.setPunishable(group, ParseUser.getCurrentUser().getUsername(), true);
                            }
                        })).start();
                    } else {
                        AlarmHelper.setSnooze(c, snoozeTime, null);
                    }
                }
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = null;
                    if (isGroup) {
                        (new Thread(new Runnable() {
                            @Override
                            public void run() {
                                ParseHelper.setMyAlarmStatusPerGroup(group, STATUS_SNOOZED);
                            }
                        })).start();
                    }
                }

                finish();
            }
        });

        // Disable snooze button if the time is 0 == no snooze.
        if (snoozeTime == 0) {
            snooze.setEnabled(false);
            snooze.setVisibility(View.INVISIBLE);
        }

        new Handler().postDelayed(releaseWakelock, WAKELOCK_TIMEOUT);
    }

    /**
     * {@inheritDoc}
     *
     * The alarm has started, creates a thread to play sound based on the set alarm in a group.
     * or plays the default sound for local alarms.
     */
    @Override
    protected void onResume() {
        super.onResume();

        // Set up repeating alarms to ring the next day they are set
        // disable alarms that are not repeating.

        Log.d(TAG, "onResume");

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

        PowerManager manager = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
        if (lock == null) {
            lock = manager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP
                    | PowerManager.ON_AFTER_RELEASE, TAG);
            Log.d(TAG, "lock created");
        }

        if (!lock.isHeld()) {
            lock.acquire();
            Log.d(TAG, "lock acquired");
        }
        if (mediaPlayer == null) {
            final Context context = this;
            (new Thread(new Runnable() {
                @Override
                public void run() {
                    if(isGroup) {
                        switch (ParseHelper.getAlarmSignal(group, ParseUser.getCurrentUser().getUsername())) {
                            case "bomb_siren":
                                mediaPlayer = MediaPlayer.create(context, R.raw.bomb_siren);
                                break;
                            case "classic_alarm":
                                mediaPlayer = MediaPlayer.create(context, R.raw.classic_alarm);
                                break;
                            case "railroad_crossing_bell":
                                mediaPlayer = MediaPlayer.create(context, R.raw.railroad_crossing_bell);
                                break;
                        }
                    } else {
                        mediaPlayer = MediaPlayer.create(context, R.raw.classic_alarm);
                    }
                    mediaPlayer.setLooping(true);
                    mediaPlayer.start();
                }
            })).start();
        }

        if (isGroup) {
            (new Thread(new Runnable() {
                @Override
                public void run() {
                    ParseHelper.setMyAlarmStatusPerGroup(group, STATUS_RINGING);
                }
            })).start();
        }

        if (alarmID < 0) {
            AlarmHelper.removeSnooze(alarmID);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (lock != null && lock.isHeld()) {
            Log.d(TAG, "Attempting release of lock");
            lock.release();
            Log.d(TAG, "Lock released");
        }
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_alarm_screen, menu);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }
}
