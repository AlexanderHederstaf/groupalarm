package com.groupalarm.asijge.groupalarm;

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
import com.groupalarm.asijge.groupalarm.Data.Alarm;
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
import java.util.LinkedList;
import java.util.List;


public class EditGroupActivity extends ActionBarActivity {

    private static final int NEW_ALARM_CODE = 999;
    private static final int EDIT_ALARM_CODE = 998;

    private ListView userListView;
    private ListView alarmListView;

    private List<String> userItems;
    private List<Alarm> alarmItems;

    private AlarmListViewAdapter alarmAdapter;
    private UserListViewAdapter userAdapter;

    private List<Alarm> alarms = new LinkedList<>();
    private List<String> users = new LinkedList<>();

    //private Runnable runParseUpdate;

    private String groupName;

    // Threads to update Parse data
    private class ParseUpdate implements Runnable {
        Runnable runListUpdate = new Runnable() {
            public void run() {
                userItems.clear();
                alarmItems.clear();

                for(String user : users) {
                    userItems.add(user);
                }
                userAdapter.notifyDataSetChanged();

                for(Alarm alarm : alarms) {
                    alarmItems.add(alarm);
                }
                alarmAdapter.notifyDataSetChanged();
            }
        };

        public void run() {
            alarms = ParseHelper.getAlarmsFromGroup(groupName);
            users = ParseHelper.getUsersInGroup(groupName);
            runOnUiThread(runListUpdate);
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
        }
    }


    private class DeleteAlarm extends AlarmParseUpdate {
        @Override
        public void run() {
            ParseHelper.deleteAlarm(alarm);
            sendNotificationToGroup("Alarm removed from group: " + groupName);
            super.run();
        }
    }

    private class EditAlarm extends AlarmParseUpdate {
        @Override
        public void run() {
            ParseHelper.editAlarm(alarm);
            sendNotificationToGroup("Alarm changed in group: " + groupName);
            super.run();
        }
    }

    private class NewUser extends ParseUpdate {

        private String user;

        public void setUser(String user) {
            this.user = user;
        }

        @Override
        public void run() {
            ParseHelper.addUserToGroup(user, groupName);
            super.run();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_group);

        groupName = getIntent().getExtras().getString("group");
        getSupportActionBar().setTitle(groupName);

        userItems = new ArrayList<String>();
        alarmItems = new ArrayList<Alarm>();

        userListView = (ListView) findViewById(R.id.group_members_listView);
        alarmListView = (ListView) findViewById(R.id.group_alarm_listView);

        userAdapter = new UserListViewAdapter(this, R.layout.user_list_item, userItems);
        userListView.setAdapter(userAdapter);

        alarmAdapter = new AlarmListViewAdapter(this, R.layout.alarm_list_item, alarmItems);
        alarmListView.setAdapter(alarmAdapter);
        registerForContextMenu(alarmListView);

    }

    /**
     * @{inheritDoc}
     */
    @Override
    public void onResume() {
        super.onResume();
        (new Thread(new AlarmParseUpdate())).start();
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

            String user = dialog.getUserToAdd();
            if (user != "") {
                NewUser run = new NewUser();
                run.setUser(user);
                (new Thread(run)).start();
            }

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
}
