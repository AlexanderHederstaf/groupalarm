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
import android.widget.Toast;

import com.groupalarm.asijge.groupalarm.AlarmManaging.ParseHelper;
import com.groupalarm.asijge.groupalarm.List.SignalListViewAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity for selecting alarm signals for group members that are snoozing. The signals that you
 * can choose from are presented in a ListView, to set a signal you simply click on it.
 *
 * @author asijge
 */
public class SignalChangeActivity extends ActionBarActivity {

    private ListView listView;
    private List<String> rowItems;
    private SignalListViewAdapter adapter;

    private static final String TAG = "GroupActivity";

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_signal);

        getSupportActionBar().setTitle("Select alarm signal");

        listView = (ListView) findViewById(R.id.signal_list);

        // Add the representative Strings for the signals at hard coded positions.
        rowItems = new ArrayList<String>();
        rowItems.add("Bomb Siren");
        rowItems.add("Classic Alarm");
        rowItems.add("Railroad Crossing Bell");

        adapter = new SignalListViewAdapter(this, R.layout.signal_list_item, rowItems);
        listView.setAdapter(adapter);

        final Context context = this;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long l) {
                // Set new signal for current snoozing user
                Toast toast = null;
                final Bundle extras = getIntent().getExtras();

                // Handle the network parts in a new Thread, position represents where in the list
                // the selected item is. As this is hard coded, we can simply compare them to integers
                // to find out which element that was clicked.
                (new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (position == 0) {
                            ParseHelper.setAlarmSignal(extras.getString("groupname"), extras.getString("user"), "bomb_siren");
                        } else if (position == 1) {
                            ParseHelper.setAlarmSignal(extras.getString("groupname"), extras.getString("user"), "classic_alarm");
                        } else if (position == 2) {
                            ParseHelper.setAlarmSignal(extras.getString("groupname"), extras.getString("user"), "railroad_crossing_bell");
                        }
                        ParseHelper.setPunishable(extras.getString("groupname"), extras.getString("user"), false);
                    }
                })).start();

                // Create toast message, this can be done on the main thread as it is not connected to
                // any network functionality
                if (position == 0) {
                    toast = Toast.makeText(context, "Set the signal Bomb Siren for " + extras.getString("user"),Toast.LENGTH_SHORT);
                } else if (position == 1) {
                    toast = Toast.makeText(context, "Set the signal Classic Alarm for " + extras.getString("user"),Toast.LENGTH_SHORT);
                } else if (position == 2) {
                    toast = Toast.makeText(context, "Set the signal Railroad Crossing Bell for " + extras.getString("user"),Toast.LENGTH_SHORT);
                }
                toast.show();
                finish();
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_signal_select, menu);
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

        if (id == R.id.changeSignal_cancel) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
