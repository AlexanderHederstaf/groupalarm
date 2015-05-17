package com.groupalarm.asijge.groupalarm;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.nfc.Tag;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.groupalarm.asijge.groupalarm.Alarm.AlarmManagerHelper;
import com.groupalarm.asijge.groupalarm.Data.Alarm;
import com.groupalarm.asijge.groupalarm.Data.AlarmDB;
import com.groupalarm.asijge.groupalarm.Data.ListRowItem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Sebastian on 2015-04-22.
 */
public class CustomListViewAdapter extends ArrayAdapter<ListRowItem> {

    public static final String TAG = "CustomListViewAdapter";
    Context context;

    public CustomListViewAdapter(Context context, int resourceId,
                                 List<ListRowItem> items) {
        super(context, resourceId, items);
        this.context = context;
    }

    private class ViewHolder {
        ImageView imageView;
        TextView time;
        TextView eventDesc;
        TextView monday,tuesday,wednesday,thursday,friday,saturday,sunday;
        Switch checkBox;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ListRowItem rowItem = getItem(position);
        ViewHolder holder = null;
        ArrayList<TextView> days = new ArrayList<TextView>();

        Log.d(TAG, "GetView for pos: " + position);


        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.alarm_list_item, null);
            holder = new ViewHolder();
            holder.time = (TextView) convertView.findViewById(R.id.time);
            holder.eventDesc = (TextView) convertView.findViewById(R.id.message);
            holder.imageView = (ImageView) convertView.findViewById(R.id.icon);
            holder.checkBox = (Switch) convertView.findViewById(R.id.on_off);
            holder.checkBox.setThumbTextPadding(4);//holder.checkBox.getWidth() / 2);
            holder.checkBox.setOnCheckedChangeListener(alarmCheckedListener(rowItem.getAlarm()));
            holder.monday = (TextView) convertView.findViewById(R.id.monday);
            holder.tuesday = (TextView) convertView.findViewById(R.id.tuesday);
            holder.wednesday = (TextView) convertView.findViewById(R.id.wednesday);
            holder.thursday = (TextView) convertView.findViewById(R.id.thursday);
            holder.friday = (TextView) convertView.findViewById(R.id.friday);
            holder.saturday = (TextView) convertView.findViewById(R.id.saturday);
            holder.sunday = (TextView) convertView.findViewById(R.id.sunday);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            holder.checkBox.setOnCheckedChangeListener(alarmCheckedListener(rowItem.getAlarm()));
        }

        days.add(holder.monday);
        days.add(holder.tuesday);
        days.add(holder.wednesday);
        days.add(holder.thursday);
        days.add(holder.friday);
        days.add(holder.saturday);
        days.add(holder.sunday);

        holder.time.setText(rowItem.getAlarm().toString());
        holder.eventDesc.setText(rowItem.getAlarm().getMessage());
        holder.imageView.setImageResource(R.drawable.ic_alarm_image);

        if(holder.checkBox.isChecked() != rowItem.getAlarm().getStatus()) {
            holder.checkBox.setChecked(rowItem.getAlarm().getStatus());
        }

        if(rowItem.getAlarm().getStatus()) {
            holder.time.setTextColor(Color.BLACK);
            holder.eventDesc.setTextColor(Color.BLACK);
            holder.imageView.setAlpha(1.0f);
        } else {
            holder.time.setTextColor(Color.GRAY);
            holder.eventDesc.setTextColor(Color.GRAY);
            holder.imageView.setAlpha(0.5f);
        }

        for(int i = 0; i < rowItem.getAlarm().getDays().length; i++) {
            if (rowItem.getAlarm().getDays()[i]) {
                days.get(i).setTextColor(Color.BLACK);
            } else {
                days.get(i).setTextColor(Color.GRAY);
            }
        }

        return convertView;
    }

    /**
     * handle check box event
     * @param alarm
     * @return
     */
    private CompoundButton.OnCheckedChangeListener alarmCheckedListener(final Alarm alarm) {
        return new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // This results in toasts only being created for the alarm that was
                    // actually clicked
                    if(!alarm.getStatus()) {
                        AlarmManagerHelper.setActive(alarm.getId(), true);
                        alarm.setActive(true);

                        Toast toast = Toast.makeText(context, resolveToastMessage(AlarmManagerHelper.getNextAlarmTime(alarm)),Toast.LENGTH_SHORT);
                        toast.show();
                    }
                } else {
                    AlarmManagerHelper.setActive(alarm.getId(), false);
                    alarm.setActive(false);
                }
                Log.d(TAG, "listener triggered");
                AlarmManagerHelper.setAlarms(context);
                reDrawUi();
            }
        };
    }

    private void reDrawUi() {
        notifyDataSetChanged();
    }

    private String resolveToastMessage(Calendar cal) {
        Calendar now = Calendar.getInstance();
        // Difference between the set time and now in seconds.
        int seconds = (int) (cal.getTimeInMillis()/1000 - now.getTimeInMillis()/1000);
        String message = "Alarm set ";
        int minute = (seconds % 3600) / 60;
        int hour = (seconds % (3600 * 24)) / 3600;
        int day = seconds / (3600 * 24);


        if (day > 0) {
            message += day + " day";
            if (day > 1) message += "s";
            if (hour + minute > 0) message += ", ";
        }
        if (hour > 0) {
            message += hour + " hour";
            if (hour > 1) message += "s";
            if (minute > 0) message += ", ";
        }
        if (minute > 0) {
            message += minute + " minute";
            if (minute > 1) message += "s";
        }
        if (day + hour + minute == 0) {
            message += "less than 1 minute";
        }
        message += " from now.";

        return message;
    }
}
