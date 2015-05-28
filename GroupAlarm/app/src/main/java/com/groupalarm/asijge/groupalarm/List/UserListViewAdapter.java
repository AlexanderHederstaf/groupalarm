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

/**
 *  UserListViewAdapter provides the functionality of the ArrayAdapter class as well
 *  as extended functionality which allows it to contain the desired user elements and
 *  layout of the ListView used for displaying users in a group.
 *
 *  @author asijge
 */
public class UserListViewAdapter extends ArrayAdapter<User> {

    private Context context;

    /** Constructs a new UserListViewAdapter containing elements based on those in
     *  the list (the "items" param), with a layout based on the resourceId.
     *
     * @param context           The Context in which it is used.
     * @param resourceId        The ID of the XML resource that is to be used.
     * @param items             A List containing Users, which in turn contain the
     *                          data relevant for each item that is to be presented.
     */
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

    /**
     *  Creates a View containing graphical objects that are based on data
     *  from the List<User> provided in the constructor.
     *
     * @param position          The position of the element which a new View has been requested for.
     * @param convertView       The instructions for how to construct the new View, used to recycle
     *                          views as to not have too many running at once.
     * @param parent            The object which contains this view, in our case a ListView.
     *
     * @return                  Returns the new/updated View for the desired element.
     */
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

        // Display an icon when a user is punishable, otherwise do not
        if(user.isPunishable()) {
            holder.snoozeIcon.setVisibility(View.VISIBLE);
        } else {
            holder.snoozeIcon.setVisibility(View.INVISIBLE);
        }

        // Change the icon based on the user's status
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
}
