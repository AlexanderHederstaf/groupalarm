package com.groupalarm.asijge.groupalarm;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.groupalarm.asijge.groupalarm.Data.ListRowItem;

import java.util.List;

/**
 * Created by Sebastian on 2015-04-22.
 */
public class CustomListViewAdapter extends ArrayAdapter<ListRowItem> {

    Context context;

    public CustomListViewAdapter(Context context, int resourceId,
                                 List<ListRowItem> items) {
        super(context, resourceId, items);
        this.context = context;
    }

    /*private view holder class*/
    private class ViewHolder {
        ImageView imageView;
        TextView time;
        TextView eventDesc;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        ListRowItem rowItem = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.alarm_list_item, null);
            holder = new ViewHolder();
            holder.time = (TextView) convertView.findViewById(R.id.time);
            //holder.eventDesc = (TextView) convertView.findViewById(R.id.eventDescription);
            holder.imageView = (ImageView) convertView.findViewById(R.id.icon);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        holder.time.setText(rowItem.getAlarm().toString());
        //holder.eventDesc.setText(rowItem.getAlarm().getMessage());
        holder.imageView.setImageResource(R.drawable.ic_alarm_image);

        return convertView;
    }
}
