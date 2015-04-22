package com.groupalarm.asijge.groupalarm.Data;

/**
 * Created by Sebastian on 2015-04-22.
 */
public class ListRowItem {

    private int imageLocation;
    private Alarm alarm;

    public ListRowItem(int imageLoc, Alarm alarm) {
        this.imageLocation = imageLoc;
        this.alarm = alarm;
    }

    public Integer getImageLocation() {
        return imageLocation;
    }

    public void setImageLocation(int imageLocation) {
        this.imageLocation = imageLocation;
    }

    public Alarm getAlarm(){
        return this.alarm;
    }

    public void setAlarm(Alarm alarm) {
        this.alarm = alarm;
    }
}
