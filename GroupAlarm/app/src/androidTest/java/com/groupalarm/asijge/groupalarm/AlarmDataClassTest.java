package com.groupalarm.asijge.groupalarm;

import android.test.InstrumentationTestCase;
import com.groupalarm.asijge.groupalarm.Data.Alarm;
import junit.framework.Assert;
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
        try {
            alarm.setTime(34,63);
            Assert.fail("Should have thrown IllegalArgumentException");
        } catch(IllegalArgumentException e) {
              //success
            // The checks below further checks that the illegal arguments are not used.
            assertEquals(hour, alarm.getHour());
            assertEquals(minute, alarm.getMinute());
          }

         // Checking to see if this Alarm gets set to the right day.
        alarm.setDay(dayOfWeek, active);
        assertEquals(active, alarm.getDays()[dayOfWeek]);

        // Checking to see if this Alarm gets set to an existing day, i.e. a number between 0-6. This should not work.
        try {
            alarm.setDay(8, active);
            Assert.fail("Should have thrown IllegalArgumentException");
        } catch(IllegalArgumentException e) {
              //success
          }

        //Checking to see if the Alarm gets set to active
        alarm.setActive(active);
        assertEquals(active, alarm.getStatus());

        // Checking to see if the method for getting a field with days and status works
        day[dayOfWeek] = active;
        for (int i = 0; i < 7; ++i) {
                assertEquals(day[i], alarm.getDays()[i]);
        }

        // Checking to see if the method for getting a list of only active days works
        alarm.setDay(dayOfWeek2,active);
        assertEquals(days, alarm.getActiveDays());


        // Checking to see if the method toString() works as intended
        String messageIntended = "08 : 05";
        assertEquals(messageIntended, alarm.toString());

        alarm.setTime(12, minute);
        String messageIntended2 = "12 : 05";
        assertEquals(messageIntended2, alarm.toString());

        alarm.setTime(hour, 12);
        String messageIntended3 = "08 : 12";
        assertEquals(messageIntended3, alarm.toString());

        alarm.setTime(13, 13);
        String messageIntended4 = "13 : 13";
        assertEquals(messageIntended4, alarm.toString());

        // Checking to see if the method equals(Object) works as intended
        Alarm alarm2 = new Alarm();
        assertFalse(alarm.equals(alarm2));
        assertTrue(alarm.equals(alarm));

        assertFalse(alarm.equals("An Alarm set to 13 : 13"));
    }
}
