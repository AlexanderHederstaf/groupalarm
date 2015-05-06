package com.groupalarm.asijge.groupalarm;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.groupalarm.asijge.groupalarm.Data.Alarm;
import com.groupalarm.asijge.groupalarm.Data.AlarmDB;

import java.sql.SQLException;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
        try {
            testDatabase();
        } catch(SQLException e) {
            // Error not expected
            fail("Database exception.");
        }

    }

    private AlarmDB database;

    public void testDatabase() throws SQLException {
        database = new AlarmDB(getContext());
        Alarm testAlarm = new Alarm();

        int sizeBefore = database.getAlarms().size();

        database.addAlarm(testAlarm);

        assertTrue(sizeBefore + 1 == database.getAlarms().size());

        assertEquals(database.getAlarm(8), testAlarm);
        database.deleteAlarm(testAlarm.getId());
    }
}