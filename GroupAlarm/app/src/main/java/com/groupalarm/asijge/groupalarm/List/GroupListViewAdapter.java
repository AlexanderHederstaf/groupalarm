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

/**
 *  GroupListViewAdapter provides the functionality of the ArrayAdapter class as well
 *  as extended functionality which allows it to contain the desired String elements
 *  which represent user groups.
 *
 *  @author asijge
 */
public class GroupListViewAdapter extends ArrayAdapter<String> {

    private Context context;

    /** Constructs a new GroupListViewAdapter containing elements based on those in
     *  the list (the "items" param), with a layout based on the resourceId.
     *
     * @param context           The Context in which it is used.
     * @param resourceId        The ID of the XML resource that is to be used.
     * @param items             A List of Strings, which represent the data
     *                          relevant for each item that is to be presented.
     */
    public GroupListViewAdapter(Context context, int resourceId,
                                List<String> items) {
        super(context, resourceId, items);
        this.context = context;
    }

    /**
     * Private class containing the elements required to construct the View in
     * the getView method.
     */
    private class ViewHolder {
        TextView group;
    }

    /**
     *  Creates a View containing graphical objects that are based on data
     *  from the List<String> provided in the constructor.
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
        String groupName = getItem(position);
        ViewHolder holder = null;

        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        // If convertView is null we need to construct the view.
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.group_list_item, null);
            holder = new ViewHolder();
            holder.group = (TextView) convertView.findViewById(R.id.group_name);
            convertView.setTag(holder);

        // If convertView is not null we can reuse information provided in it for us to construct
        // the new/updated View.
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.group.setText(groupName);

        return convertView;
    }
}
