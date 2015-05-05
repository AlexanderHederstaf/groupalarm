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

import com.groupalarm.asijge.groupalarm.Data.ListRowItem;

import java.util.List;

/**
 * Created by Sebastian on 2015-04-22.
 */
public class CustomListViewAdapter extends ArrayAdapter<ListRowItem> {

    public static final String TAG = "CustomListViewAdapter";
    Context context;
    private ViewHolder holder;

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
        holder = new ViewHolder();
        final ListRowItem rowItem = getItem(position);
        View row = convertView;

        Log.d(TAG, "GetView for pos: " + position);


        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (row == null) {
            row = mInflater.inflate(R.layout.alarm_list_item, null);
            holder.time = (TextView) row.findViewById(R.id.time);
            //holder.eventDesc = (TextView) convertView.findViewById(R.id.eventDescription);
            holder.imageView = (ImageView) row.findViewById(R.id.icon);
            holder.checkBox = (CheckBox) row.findViewById(R.id.on_off);
            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        holder.imageView.setAlpha(1.0f);
                        holder.time.setTextColor(Color.BLACK);
                    } else {
                        holder.imageView.setAlpha(0.4f);
                        holder.time.setTextColor(Color.GRAY);
                    }
                    rowItem.getAlarm().setActive(isChecked);
                }
            });
        } else {
            holder = (ViewHolder) convertView.getTag();
            holder.checkBox.setOnCheckedChangeListener(null);
        }

        holder.time.setText(rowItem.getAlarm().toString());
        //holder.eventDesc.setText(rowItem.getAlarm().getMessage());
        holder.imageView.setImageResource(R.drawable.ic_alarm_image);

        holder.checkBox.setChecked(rowItem.getAlarm().getStatus());


        return row;
    }
}
