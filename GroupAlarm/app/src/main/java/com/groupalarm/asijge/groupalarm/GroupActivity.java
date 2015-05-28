package com.groupalarm.asijge.groupalarm;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.groupalarm.asijge.groupalarm.AlarmManaging.ParseHelper;
import com.groupalarm.asijge.groupalarm.DialogFragment.AddGroupDialogFragment;
import com.groupalarm.asijge.groupalarm.List.GroupListViewAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Activity that displays a list with the groups that the logged in user is a member of.
 * If a group is clicked the user is directed to the EditGroupActivity for that group.
 *
 * @author asijge
 */
public class GroupActivity extends ActionBarActivity {

    private View progress;
    private ListView listView;
    private List<String> rowItems;
    private GroupListViewAdapter adapter;

    private static final String TAG = "GroupActivity";

    /**
     * Runnable to update the view of the groups.
     */
    private class ParseUpdate implements Runnable {

        private List<String> groups = new LinkedList<String>();

        private Runnable runListUpdate = new Runnable() {
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

    /**
     * Runnable to create a new group, and then update the view of the groups.
     */
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

    /**
     * {@inheritDoc}
     */
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
     *
     * Updates the groups lists
     */
    @Override
    public void onResume() {
        super.onResume();
        showProgress(true);
        (new Thread(new ParseUpdate())).start();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_group, menu);
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

        if (id == R.id.action_new) {

            AddGroupDialogFragment dialog = new AddGroupDialogFragment();
            dialog.show(getFragmentManager(), "MyGroupDF");

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Adds a new group to the Parse cloud with the current user as a member.
     *
     * @param groupName The name of the new group.
     */
    public void addGroup(String groupName) {
        NewGroup run = new NewGroup();
        run.setGroupName(groupName);
        (new Thread(run)).start();
    }

    /**
     * Shows the progress animation while the groups are loaded.
     * @param show True if the animation should be shown, false if it should be hidden.
     */
    public void showProgress(boolean show) {
        ViewHelper.showProgress(show, progress, listView, this);
    }
}
