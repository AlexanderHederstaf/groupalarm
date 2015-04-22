package com.groupalarm.asijge.groupalarm;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class RemoveActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove);
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
