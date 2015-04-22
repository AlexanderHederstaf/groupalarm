package com.groupalarm.asijge.groupalarm.Data;

import android.util.Pair;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sebastian on 2015-04-21.
 * Edited by Gabriella on 2015-04-21.
 *
 * This class contains the data relevant for setting Alarms.
 */
public class Alarm {
    private Pair<Integer,Integer> time;

    private String message;

    private boolean active;

    private List<Pair<Days, Boolean>> day;


    /**
     * Enum representing the days of the week.
     */
    public enum Days {
        MONDAY, TUESDAY, WEDNESDAY,
        THURSDAY, FRIDAY, SATURDAY, SUNDAY
    }

    /**
     * Enum representing the intervals to snooze.
     */
    public enum Snooze {
        NO_SNOOZE(0),
        FIVE(5),
        TEN(10),
        FIFTEEN(15);

        private final int value;
        private Snooze(int val) {
            this.value = val;
        }
        public int getValue() {
            return value;
        }
    }

    /**
     * An empty constructor. This constructor sets the Alarm to default values.
     * time = 0,0
     * message = ""
     * active = false
     * day = empty
     */
    public Alarm() {
    //
        time = new Pair<Integer,Integer>(0,0);
        message = "";
        active = false;
        day = new ArrayList<Pair<Days, Boolean>>();
    }

    /**
     * A method for setting this Alarm to this chosen time.
     * @param time A pair of hours, minutes.
     */
    public void setTime(Pair<Integer,Integer> time) {
        this.time = time;
    }

    /**
     * A method for setting the message shown when this Alarm goes off.
     * @param message A string message.
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * A method for activating or deactivating this Alarm.
     * @param active A boolean, true to activate the alarm. false to deactivate.
     */
    public void setActive (boolean active) {
        this.active = active;
    }

    /**
     * A method for getting access to the hour to which the Alarm is set to ring.
     * @return The hour to which this Alarm is set.
     */
    public int getHour() {
        return time.first;
    }

    /**
     * A method for getting access to the minute to which the Alarm is set to ring.
     * @return The minute to which this Alarm is set.
     */
    public int getMinute() {
        return time.second;
    }

    /**
     * A method for getting access to the message which is to be shown when the Alarm is set to ring.
     * @return The message to be shown when this Alarm rings.
     */
    public String getMessage() {
        return message;
    }

    /**
     * A method for getting access to the status, activated or deactivated, on this Alarm.
     * @return The status, true or false, representing if the Alarm is activated or not.
     */
    public boolean getStatus() {
        return active;
    }

    /**
     * A method that returns a list of all the Days and the Status on that day.
     * @return A list of the Days and the Status on that day.
     */
    public List<Pair<Days, Boolean>> getDays() {
        List<Pair<Days, Boolean>> copyOfDay = new ArrayList<Pair<Days, Boolean>>();
        for (Pair<Days, Boolean> pair: day) {
            copyOfDay.add(new Pair<Days, Boolean>(pair.first, pair.second));
        }
            return copyOfDay;  // safe copy
    }

    /**
     * A method that returns a list of the Days this Alarm is set to active.
     * @return A list of the Days this Alarm is set to active.
     */
    public List<Days> getActiveDays() {
        List<Days> activeDays = new ArrayList<Days>();
        for (Pair<Days, Boolean> pair: day) {
            if (pair.second == true) {
                activeDays.add(pair.first);
            }
        }
        return activeDays;  // safe copy with only active days, i.e. days where the alarm is set.
    }

    @Override
    public String toString() {
        return getHour() + " : " + getMinute();
    }
}
