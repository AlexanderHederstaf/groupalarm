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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.groupalarm.asijge.groupalarm.AlarmManaging.AlarmHelper;
import com.groupalarm.asijge.groupalarm.AlarmManaging.ParseHelper;
import com.groupalarm.asijge.groupalarm.Data.Alarm;
import com.groupalarm.asijge.groupalarm.DialogFragment.AddGroupDialogFragment;
import com.groupalarm.asijge.groupalarm.List.AlarmListViewAdapter;
import com.groupalarm.asijge.groupalarm.List.GroupListViewAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


public class GroupActivity extends ActionBarActivity {

    private View progress;
    private ListView listView;
    private List<String> rowItems;
    private GroupListViewAdapter adapter;

    private static final String TAG = "GroupActivity";


    private class ParseUpdate implements Runnable {

        private List<String> groups = new LinkedList<String>();

        Runnable runListUpdate = new Runnable() {
            public void run() {
                rowItems.clear();

                for(String group : groups) {
                    rowItems.add(group);
                }
                Collections.sort(rowItems);
                adapter.notifyDataSetChanged();
                showProgress(false);
            }
        };

        @Override
        public void run() {
            groups = ParseHelper.getGroupsForUser();
            runOnUiThread(runListUpdate);
        }
    }

    private class NewGroup extends ParseUpdate {

        private String groupName;

        public void setGroupName(String groupName) {
            this.groupName = groupName;
        }

        @Override
        public void run() {
            ParseHelper.createGroup(groupName);
            super.run();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        getSupportActionBar().setTitle("Groups");

        progress = findViewById(R.id.group_fetch_progress);
        listView = (ListView) findViewById(R.id.group_alarmlist);
        rowItems = new ArrayList<String>();
        adapter = new GroupListViewAdapter(this, R.layout.group_list_item, rowItems);
        listView.setAdapter(adapter);

        final Context context = this;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Start new activity for the clicked group
                Intent intent = new Intent(context, EditGroupActivity.class);
                intent.putExtra("group", rowItems.get(position));
                startActivity(intent);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onResume() {
        super.onResume();
        showProgress(true);
        (new Thread(new ParseUpdate())).start();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_group, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_new) {

            AddGroupDialogFragment dialog = new AddGroupDialogFragment();
            dialog.show(getFragmentManager(), "MyGroupDF");
            String groupName = dialog.getGroupName();

            NewGroup run = new NewGroup();
            run.setGroupName(groupName);
            (new Thread(run)).start();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showProgress(boolean show) {
        ViewHelper.showProgress(show, progress, listView, this);
    }
}
