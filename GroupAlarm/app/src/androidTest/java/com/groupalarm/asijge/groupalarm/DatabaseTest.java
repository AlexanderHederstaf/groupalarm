package com.groupalarm.asijge.groupalarm;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.test.ActivityTestCase;
import android.test.AndroidTestCase;
import android.test.ApplicationTestCase;
import android.test.InstrumentationTestCase;
import android.test.IsolatedContext;
import android.test.RenamingDelegatingContext;
import android.test.mock.MockContentResolver;
import android.test.mock.MockContext;

import com.groupalarm.asijge.groupalarm.Data.Alarm;
import com.groupalarm.asijge.groupalarm.Data.AlarmDB;

import java.sql.SQLException;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class DatabaseTest extends AndroidTestCase {
    public DatabaseTest() {
        try {
            testDatabase();
        } catch(SQLException e) {
            // Error not expected
            fail("Database exception.");
        }

    }

    @Override
    protected void setUp() {
        MockContentResolver resolver = new MockContentResolver();
        RenamingDelegatingContext targetContextWrapper = new RenamingDelegatingContext(
                new MockContext(), // The context that most methods are delegated to
                getContext(), // The context that file methods are delegated to
                "test_");
        Context context = new IsolatedContext(resolver, targetContextWrapper);
        setContext(context);
    }

    private AlarmDB database;

    public void testDatabase() throws SQLException {

        Context c = getContext();

        assertNotNull(c);

        AlarmDB.initiate(c);

        database = AlarmDB.getInstance();
        Alarm testAlarm = new Alarm(database.getNewId());

        int sizeBefore = database.getAlarms().size();

        database.addAlarm(testAlarm);

        assertTrue(sizeBefore + 1 == database.getAlarms().size());

        assertEquals(database.getAlarm(8), testAlarm);

        database.deleteAlarm(testAlarm.getId());
    }
}