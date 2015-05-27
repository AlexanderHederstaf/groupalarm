/**
 * RemoveActivity.java
 *
 * Activity for removing alarms from the database in a listview configuration.
 * 
 * @author asijge
 * @copyright (c) 2015, asijge
 *
 */

package com.groupalarm.asijge.groupalarm;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.groupalarm.asijge.groupalarm.AlarmManaging.AlarmHelper;
import com.groupalarm.asijge.groupalarm.Data.Alarm;
import com.groupalarm.asijge.groupalarm.List.RemoveListViewAdapter;

import java.util.ArrayList;
import java.util.List;


public class RemoveActivity extends ActionBarActivity {

    private ListView listView;
    private List<Alarm> rowItems;
    private RemoveListViewAdapter adapter;

    private static final String TAG = "RemoveActivity";

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove);

        // Create List with current alarms from database
        rowItems = new ArrayList<Alarm>();
        for (Alarm alarm : AlarmHelper.getAlarms()) {
            if (!alarm.isGroupAlarm()) {
                rowItems.add(alarm);
            }
        }

        // Connect with UI ListView and assign appropriate adapter
        listView = (ListView) findViewById(R.id.removelist);
        adapter = new RemoveListViewAdapter(this, R.layout.activity_remove_item, rowItems);
        listView.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_remove, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_cancel) {
            finish();
            return true;
        }

        if (id == R.id.action_delete) {
            for (int i = 0; i < adapter.itemsToRemove.length; i++) {
                if (adapter.itemsToRemove[i] == true) {
                    AlarmHelper.cancelAlarm(this, rowItems.get(i).getId());
                    AlarmHelper.removeAlarm(rowItems.get(i).getId());
                }
            }
            setResult(RESULT_OK);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

