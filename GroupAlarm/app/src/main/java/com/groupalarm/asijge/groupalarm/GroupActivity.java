package com.groupalarm.asijge.groupalarm;

import android.content.Context;
import android.content.Intent;
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
import java.util.List;


public class GroupActivity extends ActionBarActivity {

    private ListView listView;
    private List<String> rowItems;
    private GroupListViewAdapter adapter;
    private Runnable runParseListUpdate;

    private static final String TAG = "GroupActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        getSupportActionBar().setTitle("Groups");

        rowItems = new ArrayList<String>();
        listView = (ListView) findViewById(R.id.group_alarmlist);
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

        runParseListUpdate = new Runnable() {
            public void run() {
                Log.d(TAG, "runParseListUpdate");
                rowItems.clear();
                for(String group : ParseHelper.getGroupsForUser()) {
                    rowItems.add(group);
                }
            }
        };
        runOnUiThread(runParseListUpdate);
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

            runOnUiThread(runParseListUpdate); // update list gui

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
