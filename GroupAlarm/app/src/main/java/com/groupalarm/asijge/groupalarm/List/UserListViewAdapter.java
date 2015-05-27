package com.groupalarm.asijge.groupalarm.List;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.groupalarm.asijge.groupalarm.AlarmManaging.ParseHelper;
import com.groupalarm.asijge.groupalarm.Data.User;
import com.groupalarm.asijge.groupalarm.R;

import java.util.List;

public class UserListViewAdapter extends ArrayAdapter<User> {

    private Context context;
    private String userGroup;

    public UserListViewAdapter(Context context, int resourceId,
                               List<User> items) {
        super(context, resourceId, items);
        this.context = context;
    }

    /**
     * Private class containing the elements required to construct the View in
     * the getView method.
     */
    private class ViewHolder {
        TextView userName;
        ImageView snoozeIcon;
        ImageView status;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        User user = getItem(position);
        ViewHolder holder = null;

        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        // If convertView is null we need to construct the layout of our holder that is to be used
        // in the View.
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.user_list_item, null);
            holder = new ViewHolder();
            holder.userName = (TextView) convertView.findViewById(R.id.user_name);
            holder.snoozeIcon = (ImageView) convertView.findViewById(R.id.snoozing);
            holder.status = (ImageView) convertView.findViewById(R.id.user_status);
            convertView.setTag(holder);

            // If convertView is not null we can reuse information provided in it for us to construct
            // the new/updated View.
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.userName.setText(user.getName());

        if(user.isPunishable()) {
            holder.snoozeIcon.setEnabled(true);
            holder.snoozeIcon.setVisibility(View.VISIBLE);
        } else {
            holder.snoozeIcon.setEnabled(false);
            holder.snoozeIcon.setVisibility(View.INVISIBLE);
        }

        switch (user.getStatus()) {
            case RING:
                holder.status.setImageResource(R.drawable.ic_action_ring);
                break;
            case SNOOZE:
                holder.status.setImageResource(R.drawable.ic_action_snooze);
                break;
            case STOP:
                holder.status.setImageResource(R.drawable.ic_action_off);
                break;
            case OFF:
                holder.status.setImageResource(R.drawable.ic_action_mute);
                break;
            default:
                break;
        }
        return convertView;
    }

    public void setUserGroup(String group) {
        userGroup = group;
    }
}
