package com.groupalarm.asijge.groupalarm.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sebastian on 2015-04-21.
 * Edited by Gabriella on 2015-04-21.
 *
 * This class contains the data relevant for setting Alarms.
 */
public class Alarm implements Serializable {
    private int hour;
    private int minute;

    private String message;

    private boolean active;

    private boolean[] days;

    private Snooze snoozeInterval;


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
        hour = 0;
        minute = 0;
        message = "";
        active = false;
        days = new boolean[7];
    }

    /**
     * A method for setting this Alarm to this chosen time.
     * @param hour An int representing the hour
     * @param minute An int representing the minute
     */
        public void setTime(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
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
     * A method for setting this Alarm to the days with the boolean true
     * @param day A list with pairs of integers representing the days of the week
     *            and a boolean representing the status, activated or deactivated.
     */
    public void setDay (int day, boolean value) { this.days[day] = value; }

    /**
     * A method for setting the snooze interval for this Alarm.
     * @param snoozeInterval A value of the enum Snooze.
     */
    public void setSnoozeInterval (Snooze snoozeInterval) { this.snoozeInterval = snoozeInterval; }

    /**
     * A method for getting access to the hour to which the Alarm is set to ring.
     * @return The hour to which this Alarm is set.
     */
    public int getHour() {
        return hour;
    }

    /**
     * A method for getting access to the minute to which the Alarm is set to ring.
     * @return The minute to which this Alarm is set.
     */
    public int getMinute() {
        return minute;
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
    public boolean[] getDays() {
        boolean[] tmp = new boolean[7];
        for (int i = 0; i < 7; ++i) {
            tmp[i] = days[i];
        }
        return tmp;
    }

    /**
     * A method that returns a list of the Days this Alarm is set to active.
     * @return A list of the Days this Alarm is set to active.
     */
    public List<Integer> getActiveDays() {
        List<Integer> activeDays = new ArrayList<Integer>();
        for (int i = 0; i < 7; ++i) {
            if (days[i]) {
                activeDays.add(i);
            }
        }
        return activeDays;  // safe copy with only active days, i.e. days where the alarm is set.
    }

    /**
     * A method that returns the value of the snoozeInterval for this Alarm.
     * @return A value of the enum type Snooze.
     */
    public Snooze getSnoozeInterval() { return snoozeInterval; }
}
