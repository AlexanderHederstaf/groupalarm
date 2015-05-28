package com.groupalarm.asijge.groupalarm.Data;

/**
 * Data class containing the information required to display a User of a group
 */
public class User implements Comparable<User> {

    /**
     * The status of this user's last alarm is "stopped"
     */
    public static final String STOP = "stopped";
    /**
     * The status of this user's last alarm is "off"
     */
    public static final String OFF = "off";
    /**
     * The status of this user's last alarm is "snoozed"
     */
    public static final String SNOOZE = "snoozed";
    /**
     * The status of this user's last alarm is "ringing"
     */
    public static final String RING = "ringing";

    /**
     * The different statuses of an alarm
     */
    public enum Status {
        RING,
        SNOOZE,
        STOP,
        OFF
    }

    /**
     * The name of the User.
     */
    private String name;

    /**
     * The current status of the User.
     */
    private Status status;

    /**
     * The current punishability of the User.
     */
    private boolean punishable;

    /**
     * Creates a new User with a given name and stopped status.
     * @param name The name of the user.
     */
    public User(String name) {
        this.name = name;
        status = Status.STOP;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(User another) {
        return name.compareTo(another.getName());
    }

    /**
     * Retrieve the name of the User.
     * @return the user's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Get the current alarm status of the User.
     * @see Status
     * @return The last status of the user.
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Changes the status of the User.
     * @param status The new status.
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * Set the punishable status of the User. A punishable User can have the alarm
     * signal of their next snooze changed.
     * @param punishable True if the user should be punishable, false otherwise.
     */
    public void setPunishable(boolean punishable) {
        this.punishable = punishable;
    }

    /**
     * Get the punishable status of the User. If the user is punishable their next alarm signal
     * can be changed by a group member.
     * @return The punishable status of the User.
     */
    public boolean isPunishable() {
        return punishable;
    }
}
