package com.groupalarm.asijge.groupalarm.Data;

/**
 * Created by Sebastian on 2015-04-22.
 */
public class ListRowItem {

    private String imageLocation;
    private Alarm alarm;

    public ListRowItem(String imageLoc, Alarm alarm) {
        this.imageLocation = imageLoc;
        this.alarm = alarm;
    }

    public String getImageLocation() {
        return imageLocation;
    }

    public void setImageLocation(String imageLocation) {
        this.imageLocation = imageLocation;
    }

    public Alarm getAlarm(){
        return this.alarm;
    }

    public void setAlarm(Alarm alarm) {
        this.alarm = alarm;
    }
}
