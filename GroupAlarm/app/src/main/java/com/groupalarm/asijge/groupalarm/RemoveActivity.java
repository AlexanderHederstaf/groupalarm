package com.groupalarm.asijge.groupalarm;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.groupalarm.asijge.groupalarm.Alarm.AlarmManagerHelper;
import com.groupalarm.asijge.groupalarm.Data.ListRowItem;

import java.util.ArrayList;
import java.util.List;


public class RemoveActivity extends ActionBarActivity {

    private ListView listView;
    private List<ListRowItem> rowItems;
    private RemoveListViewAdapter adapter;
    private Runnable runListUpdate;

    private static final String TAG = "RemoveActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove);

        rowItems = new ArrayList<ListRowItem>();
        for (int i = 0; i < AlarmManagerHelper.getAlarms().size(); i++) {
            ListRowItem item = new ListRowItem(0, AlarmManagerHelper.getAlarms().get(i));
            rowItems.add(item);
        }

        listView = (ListView) findViewById(R.id.removelist);
        adapter = new RemoveListViewAdapter(this, R.layout.activity_remove_item, rowItems);
        listView.setAdapter(adapter);

        runListUpdate = new Runnable() {
            public void run() {
                Log.d(TAG, "runListUpdate");
                //reload content
                rowItems.clear();
                for (int i = 0; i < AlarmManagerHelper.getAlarms().size(); i++) {
                    ListRowItem item = new ListRowItem(R.drawable.ic_alarm_image, AlarmManagerHelper.getAlarms().get(i));
                    rowItems.add(item);
                }
                adapter.notifyDataSetChanged();
                //listView.invalidateViews();
                //listView.refreshDrawableState();
            }
        };
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
                    AlarmManagerHelper.removeAlarm(i, this);
                }
            }
            setResult(RESULT_OK);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);

    }
}

