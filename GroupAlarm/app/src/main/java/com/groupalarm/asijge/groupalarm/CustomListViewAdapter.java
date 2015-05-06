package com.groupalarm.asijge.groupalarm;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.nfc.Tag;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.groupalarm.asijge.groupalarm.Data.Alarm;
import com.groupalarm.asijge.groupalarm.Data.ListRowItem;

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
        CheckBox checkBox;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ListRowItem rowItem = getItem(position);
        ViewHolder holder = null;

        Log.d(TAG, "GetView for pos: " + position);


        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            Log.d(TAG, "creating new row for" + position);
            convertView = mInflater.inflate(R.layout.alarm_list_item, null);
            holder = new ViewHolder();
            holder.time = (TextView) convertView.findViewById(R.id.time);
            //holder.eventDesc = (TextView) convertView.findViewById(R.id.eventDescription);
            holder.imageView = (ImageView) convertView.findViewById(R.id.icon);
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.on_off);
            holder.checkBox.setOnCheckedChangeListener(alarmCheckedListener(rowItem.getAlarm()));
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            holder.checkBox.setOnCheckedChangeListener(alarmCheckedListener(rowItem.getAlarm()));
        }

        holder.time.setText(rowItem.getAlarm().toString());
        //holder.eventDesc.setText(rowItem.getAlarm().getMessage());
        holder.imageView.setImageResource(R.drawable.ic_alarm_image);
        holder.checkBox.setChecked(rowItem.getAlarm().getStatus());

        if(rowItem.getAlarm().getStatus()) {
            holder.time.setTextColor(Color.BLACK);
            holder.imageView.setAlpha(1.0f);
        } else {
            holder.time.setTextColor(Color.GRAY);
            holder.imageView.setAlpha(0.5f);
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
                    alarm.setActive(true);
                } else {
                    alarm.setActive(false);
                }
                Log.d(TAG, "Alarm " + alarm.toString() + " was clicked");
                reDrawUi();
            }
        };
    }

    private void reDrawUi() {
        this.notifyDataSetChanged();
    }
}
