package com.groupalarm.asijge.groupalarm;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.groupalarm.asijge.groupalarm.AlarmManaging.AlarmHelper;
import com.groupalarm.asijge.groupalarm.AlarmManaging.ParseHelper;
import com.groupalarm.asijge.groupalarm.AlarmManaging.SetAlarms;
import com.groupalarm.asijge.groupalarm.Data.Alarm;
import com.groupalarm.asijge.groupalarm.Data.User;
import com.groupalarm.asijge.groupalarm.DialogFragment.AddMemberDialogFragment;
import com.groupalarm.asijge.groupalarm.List.AlarmListViewAdapter;
import com.groupalarm.asijge.groupalarm.List.UserListViewAdapter;
import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class EditGroupActivity extends ActionBarActivity {

    private static final String TAG = "EditGroupActivity";

    private static final int NEW_ALARM_CODE = 999;
    private static final int EDIT_ALARM_CODE = 998;

    private ListView userListView;
    private ListView alarmListView;

    private View userProgress;
    private View alarmProgress;

    private List<User> userItems;
    private List<Alarm> alarmItems;

    private AlarmListViewAdapter alarmAdapter;
    private UserListViewAdapter userAdapter;

    private List<Alarm> alarms = new LinkedList<>();
    private List<String> users = new LinkedList<>();

    //private Runnable runParseUpdate;

    private String groupName;

    private SetAlarms setAlarms;

    // Threads to update Parse data
    private class ParseUpdate implements Runnable {
        Runnable runListUpdate = new Runnable() {
            public void run() {
                userItems.clear();
                alarmItems.clear();

                for(String userName : users) {
                    userItems.add(new User(userName));
                }
                Collections.sort(userItems);
                userAdapter.notifyDataSetChanged();

                for(Alarm alarm : alarms) {
                    alarmItems.add(alarm);
                }
                Collections.sort(alarmItems);
                alarmAdapter.notifyDataSetChanged();
                showProgress(false);
            }
        };

        public void run() {
            alarms = ParseHelper.getAlarmsFromGroup(groupName);
            users = ParseHelper.getUsersInGroup(groupName);
            runOnUiThread(runListUpdate);
            (new Thread(updateStatusNotification)).start();
        }
    }

    private class AlarmParseUpdate extends ParseUpdate {
        protected Alarm alarm;

        public void setAlarm(Alarm alarm) {
            this.alarm = alarm;
        }
    }

    private void sendNotificationToGroup(String message) {
        Log.d("EditGroupActivity", "Installation user: " + ParseInstallation.getCurrentInstallation().get("user"));

        // Find users in the group
        ParseObject groupObject = ParseHelper.getGroupFromString(groupName);

        ParseRelation relation = groupObject.getRelation("Users");
        ParseQuery queryRelation = relation.getQuery();
        queryRelation.whereNotEqualTo("objectId", ParseUser.getCurrentUser().getObjectId());

        // Find devices associated with these users
        ParseQuery pushQuery = ParseInstallation.getQuery();
        pushQuery.whereMatchesQuery("user", queryRelation);

        // Send push notification to query
        ParsePush push = new ParsePush();
        push.setQuery(pushQuery); // Set our Installation query
        push.setMessage(message);
        push.sendInBackground();
    }

    private class NewAlarm extends AlarmParseUpdate {
        @Override
        public void run() {
            ParseHelper.addNewAlarmToGroup(alarm, groupName);
            sendNotificationToGroup("New alarm added to group: " + groupName);
            super.run();
            (new Thread(setAlarms)).start();
        }
    }


    private class DeleteAlarm extends AlarmParseUpdate {
        @Override
        public void run() {
            ParseHelper.deleteAlarm(alarm);
            sendNotificationToGroup("Alarm removed from group: " + groupName);
            super.run();
            (new Thread(setAlarms)).start();
        }
    }

    private class EditAlarm extends AlarmParseUpdate {
        @Override
        public void run() {
            ParseHelper.editAlarm(alarm);
            sendNotificationToGroup("Alarm changed in group: " + groupName);
            super.run();
            (new Thread(setAlarms)).start();
        }
    }

    Runnable updateStatusNotification;

    private class NewUser extends ParseUpdate {

        private String user;

        public void setUser(String user) {
            this.user = user;
        }

        @Override
        public void run() {
            ParseHelper.addUserToGroup(user, groupName);
            sendNotificationToGroup("New member added to group: " + user);
            super.run();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_group);

        groupName = getIntent().getExtras().getString("group");
        getSupportActionBar().setTitle(groupName);

        setAlarms = new SetAlarms(this);

        userItems = new ArrayList<>();
        alarmItems = new ArrayList<>();

        userListView = (ListView) findViewById(R.id.group_members_listView);
        alarmListView = (ListView) findViewById(R.id.group_alarm_listView);

        userProgress = findViewById(R.id.user_fetch_progress);
        alarmProgress = findViewById(R.id.alarm_fetch_progress);

        userAdapter = new UserListViewAdapter(this, R.layout.user_list_item, userItems);
        userListView.setAdapter(userAdapter);

        alarmAdapter = new AlarmListViewAdapter(this, R.layout.alarm_list_item, alarmItems);
        alarmListView.setAdapter(alarmAdapter);
        registerForContextMenu(alarmListView);

        updateStatusNotification = new Runnable() {
            @Override
            public void run() {
                for (User user : userItems) {
                    switch (ParseHelper.getAlarmStatusUserPerGroup(user.getName(), groupName)) {
                        case User.OFF:
                            user.setStatus(User.Status.OFF);
                            break;
                        case User.STOP:
                            user.setStatus(User.Status.STOP);
                            break;
                        case User.RING:
                            user.setStatus(User.Status.RING);
                            break;
                        case User.SNOOZE:
                            user.setStatus(User.Status.SNOOZE);
                            break;
                        default:
                            break;
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        userAdapter.notifyDataSetChanged();
                    }
                });
            }
        };
    }


    // refresh service for the status of users in the group.
    private ExecutorService refresh;
    private ScheduledExecutorService scheduledRefresh;

    /**
     * @{inheritDoc}
     */
    @Override
    public void onResume() {
        super.onResume();
        showProgress(true);
        (new Thread(new AlarmParseUpdate())).start();

        // Update notifications every 15 seconds.
        refresh = Executors.newCachedThreadPool();
        scheduledRefresh = Executors.newSingleThreadScheduledExecutor();
        scheduledRefresh.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                refresh.submit(updateStatusNotification);
            }
        }, 15, 15, TimeUnit.SECONDS);
    }

    @Override
    public void onPause() {
        scheduledRefresh.shutdown();
        refresh.shutdown();
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_group, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        if (id == R.id.action_add_alarm) {
            Alarm newAlarm = new Alarm(AlarmHelper.getNewId());
            // Set Alarm default values
            Calendar tmp = Calendar.getInstance();

            newAlarm.setTime(tmp.get(Calendar.HOUR_OF_DAY), tmp.get(Calendar.MINUTE));
            newAlarm.setMessage("");
            newAlarm.setSnoozeInterval(Alarm.Snooze.TEN);
            newAlarm.setActive(true);
            for (int i = 0; i < 7; i++) {
                newAlarm.setDay(i, false);
            }

            Intent newAlarmActivity = new Intent(this, EditAlarmActivity.class);
            newAlarmActivity.putExtra("alarm", newAlarm);
            startActivityForResult(newAlarmActivity, NEW_ALARM_CODE);

            return true;
        }

        if (id == R.id.action_add_member) {
            AddMemberDialogFragment dialog = new AddMemberDialogFragment();
            Bundle args = new Bundle();
            dialog.show(getFragmentManager(), "MyAddMemberDF");

            return true;
        }

        if (id == R.id.action_leave) {
            ParseHelper.leaveGroup(groupName);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Used by dialog to set data.
     * @param user
     */
    public void addUser(String user) {
        if (user != "") {
            NewUser run = new NewUser();
            run.setUser(user);
            (new Thread(run)).start();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.group_alarm_listView) {
            ListView lv = (ListView) v;
            AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) menuInfo;
            Alarm listItem = (Alarm) lv.getItemAtPosition(acmi.position);

            menu.setHeaderTitle(listItem.getMessage());
            menu.add("Edit");
            menu.add("Delete");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Alarm alarm = (Alarm) alarmListView.getItemAtPosition(info.position);

        if (item.getTitle() == "Edit") {
            Intent newAlarmActivity = new Intent(this, EditAlarmActivity.class);
            newAlarmActivity.putExtra("alarm", alarm);
            startActivityForResult(newAlarmActivity, EDIT_ALARM_CODE);
            return true;
        }
        else if (item.getTitle() == "Delete") {

            DeleteAlarm run = new DeleteAlarm();
            run.setAlarm(alarm);
            (new Thread(run)).start();

            return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NEW_ALARM_CODE) {
            if (resultCode == RESULT_OK) {
                Alarm alarm = (Alarm) data.getSerializableExtra("EditedAlarm");

                NewAlarm run = new NewAlarm();
                run.setAlarm(alarm);
                (new Thread(run)).start();
            }
        }
        if (requestCode == EDIT_ALARM_CODE) {
            if (resultCode == RESULT_OK) {
                Alarm alarm = (Alarm) data.getSerializableExtra("EditedAlarm");

                EditAlarm run = new EditAlarm();
                run.setAlarm(alarm);
                (new Thread(run)).start();
            }
        }
    }

    private void showProgress(boolean show) {
        ViewHelper.showProgress(show, userProgress, userListView, this);
        ViewHelper.showProgress(show, alarmProgress, alarmListView, this);
    }
}
