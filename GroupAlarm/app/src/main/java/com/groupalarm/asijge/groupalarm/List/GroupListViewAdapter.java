package com.groupalarm.asijge.groupalarm.List;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.groupalarm.asijge.groupalarm.R;

import java.util.List;

public class GroupListViewAdapter extends ArrayAdapter<String> {

    private Context context;

    public GroupListViewAdapter(Context context, int resourceId,
                                List<String> items) {
        super(context, resourceId, items);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String groupName = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        // If convertView is null we need to construct the layout of our holder that is to be used
        // in the View.
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.group_list_item, null);
        }

        TextView view = (TextView) convertView.findViewById(R.id.group_name);

        view.setText(groupName);

        return convertView;
    }
}
