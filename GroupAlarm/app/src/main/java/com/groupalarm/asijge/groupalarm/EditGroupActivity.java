package com.groupalarm.asijge.groupalarm;

import android.content.Context;
import android.content.Intent;
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
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * An activity for a group that displays the users in the group and the alarms for the group.
 *
 * The users of the group can be interacted with if they snooze an alarm.
 *
 * The ActionBar has options to invite more members, create new alarms and leaving the group.
 *
 * @author asijge
 */
public class EditGroupActivity extends ActionBarActivity {

    /**
     * Debug TAG for the EditGroupActivity class.
     */
    private static final String TAG = "EditGroupActivity";

    /**
     * Result code for a new alarm being created.
     */
    private static final int NEW_ALARM_CODE = 999;

    /**
     * Result code for an alarm being edited.
     */
    private static final int EDIT_ALARM_CODE = 998;

    // List Views to display users and alarms
    private ListView userListView;
    private ListView alarmListView;

    // Progress view for when users and alarms are loading.
    private View userProgress;
    private View alarmProgress;

    // The items to display in the list.
    private List<User> userItems;
    private List<Alarm> alarmItems;

    // The adapters that contain the data from the Lists.
    private AlarmListViewAdapter alarmAdapter;
    private UserListViewAdapter userAdapter;

    // The temporary storage containers for data obtained from the Parse cloud.
    private List<Alarm> alarms = new LinkedList<>();
    private List<String> users = new LinkedList<>();
    private Map<String, Boolean> punishable = new HashMap<>();

    // The name of the group the activity is currently focused on.
    private String groupName;

    // A SetAlarms thread used to update the alarm ListView.
    private SetAlarms setAlarms;

    /*
     * Runnable used to update the User statuses.
     * defined in onCreate.
     */
    private Runnable updateStatusNotification;


    // Threads to update Parse data
    private class ParseUpdate implements Runnable {

        Runnable runListUpdate = new Runnable() {
            /**
             * {@inheritDoc}
             *
             * Updates the listViews of alarms with the data stored in the temporary
             * storage lists and map.
             */
            @Override
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

        /**
         * Updates the temporary data using the Parse Cloud.
         *
         * Then updates the UI with this data.
         */
        @Override
        public void run() {
            alarms = ParseHelper.getAlarmsFromGroup(groupName);
            users = ParseHelper.getUsersInGroup(groupName);

            runOnUiThread(runListUpdate);
        }
    }

    /**
     * ParseUpdate thread with saved Alarm Object to use when changing Alarms.
     */
    private class AlarmParseUpdate extends ParseUpdate {
        protected Alarm alarm;

        public void setAlarm(Alarm alarm) {
            this.alarm = alarm;
        }
    }

    /**
     * Thread that adds a new alarm to the group, and then updates the view and sets alarms.
     */
    private class NewAlarm extends AlarmParseUpdate {
        @Override
        public void run() {
            ParseHelper.addNewAlarmToGroup(alarm, groupName);
            sendNotificationToGroup("New alarm added to group: " + groupName);
            super.run();
            (new Thread(setAlarms)).start();
        }
    }

    /**
     * Thread that removes an alarm from the group, and then updates the view and sets alarms.
     */
    private class DeleteAlarm extends AlarmParseUpdate {
        @Override
        public void run() {
            ParseHelper.deleteAlarm(alarm);
            sendNotificationToGroup("Alarm removed from group: " + groupName);
            super.run();
            (new Thread(setAlarms)).start();
        }
    }

    /**
     * Thread that edits and alarm in the group, and then updates the view and sets alarms.
     */
    private class EditAlarm extends AlarmParseUpdate {
        @Override
        public void run() {
            ParseHelper.editAlarm(alarm);
            sendNotificationToGroup("Alarm changed in group: " + groupName);
            super.run();
            (new Thread(setAlarms)).start();
        }
    }


    /**
     * Thread that adds a new user to the group, and then updates the view.
     */
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

    /**
     * {@inheritDoc}
     *  Creates the listViews and threads that are used to update the views.
     */
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

        final Context context = this;
        alarmListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent editAlarmActivity = new Intent(context, EditAlarmActivity.class);
                editAlarmActivity.putExtra("alarm", alarmItems.get(position));
                startActivityForResult(editAlarmActivity, EDIT_ALARM_CODE);
            }
        });


        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                User user = userAdapter.getItem(position);
                if(user.isPunishable()) {
                    Intent intent = new Intent(context, SignalChangeActivity.class);
                    intent.putExtra("user", userItems.get(position).getName());
                    intent.putExtra("groupname", groupName);
                    startActivity(intent);
                }
            }
        });

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

                    user.setPunishable(ParseHelper.getPunishable(groupName, user.getName()));

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

    // refresher service for the status of users in the group.
    private ExecutorService refresh;
    private ScheduledExecutorService scheduledRefresh;

    /**
     * @{inheritDoc}
     *
     * Updates the lists with data from the ParseCloud.
     * Starts a schedule to refresh status of the Users every few seconds.
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
        }, 3, 4, TimeUnit.SECONDS);
    }

    /**
     * @{inheritDoc}
     *
     * Cancels the refreshing of status of the Users.
     */
    @Override
    public void onPause() {
        scheduledRefresh.shutdown();
        refresh.shutdown();
        super.onPause();
    }

    /**
     * @{inheritDoc}
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_group, menu);
        return true;
    }

    /**
     * @{inheritDoc}
     */
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
     * Used by dialog to add a new User to the group.
     * @param user The name of the user to add.
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

    /**
     * @{inheritDoc}
     */
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

    // shows progress bar for the two lists.
    private void showProgress(boolean show) {
        ViewHelper.showProgress(show, userProgress, userListView, this);
        ViewHelper.showProgress(show, alarmProgress, alarmListView, this);
    }

    /**
     * Sends a notification to the cloud.
     * @param message The message of the notification.
     */
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
}
