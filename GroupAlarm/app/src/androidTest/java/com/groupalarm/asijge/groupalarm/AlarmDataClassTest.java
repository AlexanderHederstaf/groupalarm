package com.groupalarm.asijge.groupalarm;

import android.test.InstrumentationTestCase;

import com.groupalarm.asijge.groupalarm.Data.Alarm;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gabriellahallams on 2015-04-22.
 */
public class AlarmDataClassTest extends InstrumentationTestCase {


    public void test() throws Exception {
        final int hour = 8;
        final int minute = 5;

        final String message = "Time to get up!";

        final boolean active = true;
        final boolean deactivated = false;

        final int dayOfWeek = 1;
        final int dayOfWeek2 = 3;

        final boolean[] day = new boolean[7];

        final List<Integer> days = new ArrayList<Integer>();
        days.add(dayOfWeek);
        days.add(dayOfWeek2);

        Alarm alarm = new Alarm();

        // Checking to see if the message gets set to this Alarm
        alarm.setMessage(message);
        assertEquals(message, alarm.getMessage());

        // Checking to see if this Alarm gets set to the right hour and minute
        alarm.setTime(hour,minute);
        assertEquals(hour, alarm.getHour());
        assertEquals(minute, alarm.getMinute());

        // Checking to see if this Alarm gets set to the right hour and minute. This should not work.
        //assertException(alarm.setTime(999,....
        alarm.setTime(34,63);
        if (alarm.getHour() < 0 || alarm.getHour() > 23) {
            throw new IllegalArgumentException("Invalid hour:" + alarm.getHour());
        }
        if (alarm.getMinute() < 0 || alarm.getMinute() > 59) {
            throw new IllegalArgumentException("Invalid minute:" + alarm.getMinute());
        }

        // Checking to see if the Alarm gets set to active
        alarm.setActive(active);
        assertEquals(active, alarm.getStatus());

        // Checking to see if the method for getting a field with days and status works
        alarm.setDay(dayOfWeek,active);
        day[dayOfWeek] = active;
        for (int i = 0; i < 7; ++i) {
                assertEquals(day[i], alarm.getDays()[i]);
        }

        // Checking to see if the method for getting a list of only active days works
        alarm.setDay(dayOfWeek2,active);
        assertEquals(days, alarm.getActiveDays());
    }
}
