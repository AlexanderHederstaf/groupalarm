package com.groupalarm.asijge.groupalarm.Data;


public class User implements Comparable<User> {

    public static final String STOP = "stopped";
    public static final String OFF = "off";
    public static final String SNOOZE = "snoozed";
    public static final String RING = "ringing";

    public enum Status {
        RING,
        SNOOZE,
        STOP,
        OFF
    };

    private String name;
    private Status status;

    public User(String name) {
        this.name = name;
        status = Status.OFF;
    }

    @Override
    public int compareTo(User another) {
        return name.compareTo(another.getName());
    }

    public String getName() {
        return name;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
