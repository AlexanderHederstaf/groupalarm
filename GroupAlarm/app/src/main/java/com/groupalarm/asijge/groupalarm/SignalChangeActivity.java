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
import com.groupalarm.asijge.groupalarm.List.SignalListViewAdapter;
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


public class SignalChangeActivity extends ActionBarActivity {

    private View progress;
    private ListView listView;
    private List<String> rowItems;
    private SignalListViewAdapter adapter;

    private static final String TAG = "GroupActivity";


    private class ParseUpdate implements Runnable {

        private List<String> signals = new LinkedList<String>();

        Runnable runListUpdate = new Runnable() {
            public void run() {
                rowItems.clear();

                for(String signal : signals) {
                    rowItems.add(signal);
                }
                Collections.sort(rowItems);
                adapter.notifyDataSetChanged();
            }
        };

        @Override
        public void run() {
            //signals = getSignals();
            runOnUiThread(runListUpdate);
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
        adapter = new SignalListViewAdapter(this, R.layout.group_list_item, rowItems);
        listView.setAdapter(adapter);

        final Context context = this;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Set new signal for current snoozing user
                //Intent intent = new Intent(context, EditGroupActivity.class);
                //intent.putExtra("group", rowItems.get(position));
                //startActivity(intent);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onResume() {
        super.onResume();
        //showProgress(true);
        //(new Thread(new ParseUpdate())).start();
    }
}
