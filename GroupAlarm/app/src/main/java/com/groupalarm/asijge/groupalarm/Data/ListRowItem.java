package com.groupalarm.asijge.groupalarm.Data;

import android.content.Context;
import android.widget.CheckBox;

/**
 * Class intended to represent the data of an item in a ListView.
 */
public class ListRowItem {

    private int imageLocation;
    private Alarm alarm;

    /**
     * Constructor for a ListRowItem. Its purpose is to represent the data in the form of the alarm param,
     * as well as an image resource from the imageLoc param. This should then be used by an item in a ListView.
     *
     * @param imageLoc      The location of the image resource that is to be used.
     * @param alarm         The alarm containing the data that is to be used.
     */
    public ListRowItem(int imageLoc, Alarm alarm) {
        this.imageLocation = imageLoc;
        this.alarm = alarm;
    }

    /**
     * Returns the location of the image resource.
     * @return Returns the image location.
     */
    public Integer getImageLocation() {
        return imageLocation;
    }

    /**
     * Sets the image that is to be used by providing its resource location.
     * @param imageLocation The location of the image resource that is to be used.
     */
    public void setImageLocation(int imageLocation) {
        this.imageLocation = imageLocation;
    }

    /**
     * Returns the alarm, which contains the data.
     * @return Return the alarm.
     */
    public Alarm getAlarm(){
        return this.alarm;
    }

    /**
     * Sets the alarm, and thereby the data, that is to be used for this ListRowItem.
     * @param alarm The Alarm object that is to be linked to this ListRowItem.
     */
    public void setAlarm(Alarm alarm) {
        this.alarm = alarm;
    }
}
