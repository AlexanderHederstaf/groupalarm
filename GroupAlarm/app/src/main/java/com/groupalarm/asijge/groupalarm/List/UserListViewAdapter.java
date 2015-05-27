package com.groupalarm.asijge.groupalarm.List;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.groupalarm.asijge.groupalarm.Data.User;
import com.groupalarm.asijge.groupalarm.R;

import java.util.List;

public class UserListViewAdapter extends ArrayAdapter<User> {

    private Context context;

    public UserListViewAdapter(Context context, int resourceId,
                               List<User> items) {
        super(context, resourceId, items);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        User user = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        // If convertView is null we need to construct the layout of our holder that is to be used
        // in the View.
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.user_list_item, null);
        }

        TextView view = (TextView) convertView.findViewById(R.id.user_name);
        view.setText(user.getName());

        ImageView snoozing = (ImageView) convertView.findViewById(R.id.snoozing);
        ImageView status = (ImageView) convertView.findViewById(R.id.user_status);

        switch (user.getStatus()) {
            case RING:
                status.setImageResource(R.drawable.ic_action_ring);
                break;
            case SNOOZE:
                status.setImageResource(R.drawable.ic_action_snooze);
                break;
            case STOP:
                status.setImageResource(R.drawable.ic_action_off);
                break;
            case OFF:
                status.setImageResource(R.drawable.ic_action_mute);
                break;
            default:
                break;
        }
        return convertView;
    }
}
