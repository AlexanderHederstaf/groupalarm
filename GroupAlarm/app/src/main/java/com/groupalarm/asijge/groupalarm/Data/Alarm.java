package com.groupalarm.asijge.groupalarm.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Alarm object contains the data relevant for setting alarms and displaying relevant information
 * when they go off.
 *
 * @author asijge
 */
public class Alarm implements Serializable, Comparable<Alarm> {
    private int hour;
    private int minute;

    private String message;

    private boolean active;

    private boolean[] days;

    private Snooze snoozeInterval;

    private final int uniqueId;

    public final static String NO_PARSE_ID = "";
    private String parseID;

    @Override
    public int compareTo(Alarm other) {
        int oHour = other.hour;
        int oMinute = other.minute;
        return (hour*60 + minute) - (oHour*60 + oMinute);
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
        Snooze(int val) {
            this.value = val;
        }
        public final int getValue() {
            return value;
        }
    }

    /**
     * An empty constructor. This constructor sets the Alarm to default values.
     * time = 00 : 00
     * message = ""
     * active = false
     * day = empty
     * @param Id The unique Id provided to represent this Alarm.
     */
    public Alarm(int Id) {
        hour = 0;
        minute = 0;
        message = "";
        active = false;
        days = new boolean[7];
        uniqueId = Id;
        parseID = NO_PARSE_ID;
    }

    /**
     * Sets the time of day the Alarm will go off in Hour:Minute accuracy.
     *
     * @param hour An int representing the hour in a 24 hour format.
     * @param minute An int representing the minute of the hour.
     */
     public void setTime(int hour, int minute) throws IllegalArgumentException {
         if (hour < 0 || hour > 23) {
                 throw new IllegalArgumentException("Invalid hour:" + hour);
         }
         if (minute < 0 || minute > 59) {
                 throw new IllegalArgumentException("Invalid minute:" + minute);
         }
         this.hour = hour;
         this.minute = minute;
    }

    /**
     * Sets the message of the Alarm.
     * This message is shown in the list of Alarms or when the Alarm goes off.
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
     * A method for setting this Alarm to the day with the chosen boolean, true (active) or false (not active)
     * @param day A list with pairs of integers representing the days of the week
     *            and a boolean representing the status, activated or deactivated.
     */
    public void setDay (int day, boolean value) throws IllegalArgumentException {
        if (day < 0 || day > 6) {
            throw new IllegalArgumentException("Invalid day:" + day);
        }
        this.days[day] = value; }

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
     * A method for getting access to the message of the Alarm.
     * @return The message of the Alarm.
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
     * Get the unique ID representation of this Alarm.
     * @return An int representing the unigue ID for this Alarm.
     */
    public int getId() {
        return uniqueId;
    }

    /**
     * Ask the alarm whether it is a group alarm or if it is individual.
     * @return Returns true if it is a group alarm, otherwise it returns false.
     */
    public boolean isGroupAlarm() {
        return !NO_PARSE_ID.equals(parseID);
    }

    /**
     * Sets the alarm to be either a group alarm or an individual alarm.
     * @param parseID Set the parse ID to identify this Alarm as a group alarm.
     * @see Alarm#NO_PARSE_ID
     */
    public void setGroupAlarm(String parseID) {
        this.parseID = parseID;
    }

    /**
     * Return the parse ID of this alarm if it is a Group Alarm.
     * @return The parse ID or NO_PARSE_ID.
     */
    public String getParseID() {
        return this.parseID;
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
     * Gives the representation of an Alarm as the hour and minute it is set to go off.
     *
     * @return The time of the Alarm on the HH:MM format.
     */
    @Override
    public String toString() {
        int minInt = String.valueOf(minute).length();
        int hourInt = String.valueOf(hour).length();

        if (hourInt == 1 && minInt == 1) {
            return "0" + hour + " : 0" + minute;
        }
        if (hourInt == 1) {
            return "0" + hour + " : " + minute;
        }
        if (minInt == 1) {
            return hour + " : 0" + minute;
        }
        return hour + " : " + minute;
    }

    /**
     * A method that returns the value of the snoozeInterval for this Alarm.
     * @return A value of the enum type Snooze.
     */
    public Snooze getSnoozeInterval() { return snoozeInterval; }

    /**
     * Get the unique ID representation, i.e. hashCode, of this Alarm.
     * @return An int representing the unique ID and hashCode for this Alarm.
     */
    @Override
    public int hashCode() {
        return uniqueId;
    }

    /**
     * Compare the alarms to one another. One alarm is equal to another if they are the same Alarm.
     * As there can be duplicate Alarm objects of the same Alarm they are identified by their unique ID.
     *
     * @param other An Object to compare with.
     * @return A boolean representing if the Alarms are the same (true) or not (false).
     */
    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (other == null)
            return false;
        if (other.getClass() != this.getClass())
            return false;
        Alarm otherAlarm = (Alarm) other;
        return uniqueId == otherAlarm.uniqueId;
    }
}
