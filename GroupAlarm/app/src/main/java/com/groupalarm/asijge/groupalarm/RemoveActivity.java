package com.groupalarm.asijge.groupalarm;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
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
    private CustomListViewAdapter adapter;
    private Runnable runListUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove);

        rowItems = new ArrayList<ListRowItem>();
        for (int i = 0; i < AlarmManagerHelper.getAlarms().size(); i++) {
            ListRowItem item = new ListRowItem(R.drawable.ic_alarm_image, AlarmManagerHelper.getAlarms().get(i));
            rowItems.add(item);
        }

        listView = (ListView) findViewById(R.id.removelist);
        adapter = new CustomListViewAdapter(this,
                R.layout.alarm_list_item, rowItems);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //TODO: Add code for what happens when you click on a alarm
            }
        });

        runListUpdate = new Runnable(){
            public void run(){
                //reload content
                rowItems.clear();
                for (int i = 0; i < AlarmManagerHelper.getAlarms().size(); i++) {
                    ListRowItem item = new ListRowItem(R.drawable.ic_alarm_image, AlarmManagerHelper.getAlarms().get(i));
                    rowItems.add(item);
                }
                adapter.notifyDataSetChanged();
                listView.invalidateViews();
                listView.refreshDrawableState();
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
            Intent  backToMainActivity = new Intent(this, MainActivity.class);
            startActivity(backToMainActivity);
            return true;
        }

        if (id == R.id.action_delete) {
            // List med rader. Varje rad ska bestå av en checkbox, en tid, en kommentar, av/på
            //Checkbox markerad - ta bort alarm och återvänd sedan till MainActivity

            Intent  backToMainActivity = new Intent(this, MainActivity.class);
            startActivity(backToMainActivity);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
