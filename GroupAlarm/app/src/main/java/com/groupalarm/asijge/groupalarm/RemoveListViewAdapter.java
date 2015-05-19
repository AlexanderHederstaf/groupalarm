package com.groupalarm.asijge.groupalarm;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.groupalarm.asijge.groupalarm.AlarmManaging.AlarmHelper;
import com.groupalarm.asijge.groupalarm.Data.ListRowItem;

import java.util.List;

/**
 * Created by Emma on 2015-05-06.
 */
public class RemoveListViewAdapter extends ArrayAdapter<ListRowItem> {

    public static final String TAG = "RemoveListViewAdapter";
    Context context;
    public boolean[] itemsToRemove;

    public RemoveListViewAdapter(Context context, int resourceId,
                                 List<ListRowItem> items) {
        super(context, resourceId, items);
        this.context = context;
        int arraySize = items.size();
        itemsToRemove = new boolean[arraySize];
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
            convertView = mInflater.inflate(R.layout.activity_remove_item, null);
            holder = new ViewHolder();
            holder.time = (TextView) convertView.findViewById(R.id.remove_time);
            holder.eventDesc = (TextView) convertView.findViewById(R.id.remove_message);
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.remove_checkbox);
            holder.checkBox.setOnCheckedChangeListener(removeAlarmCheckedListener(position));
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            holder.checkBox.setOnCheckedChangeListener(removeAlarmCheckedListener(position));
        }

        holder.time.setText(rowItem.getAlarm().toString());
        holder.eventDesc.setText(rowItem.getAlarm().getMessage());

        return convertView;
    }

    /**
     * handle check box event
     * @param postition
     * @return
     */
    private CompoundButton.OnCheckedChangeListener removeAlarmCheckedListener(final int postition) {
        return new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    itemsToRemove[postition] = true;
                    Log.d(TAG, "checked: " + postition);
                } else {
                    itemsToRemove[postition] = false;
                    Log.d(TAG, "unchecked: " + postition);
                }

                for (int i = 0; i < itemsToRemove.length; i++) {
                    Log.d(TAG, i + ":" + itemsToRemove[i]);
                }
                
                reDrawUi();
            }
        };
    }

    private void reDrawUi() {
        this.notifyDataSetChanged();
    }
}


