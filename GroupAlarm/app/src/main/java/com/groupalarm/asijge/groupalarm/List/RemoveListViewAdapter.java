/**
 * RemoveListViewAdapter.java
 *
 * Provides the functionality of the Arrayadapter class as well
 * as extended functionality which allows it to contain the desired elements and layout
 * of the listView used used in the RemoveActivity class.
 *
 * @author asijge
 * @copyright (c) 2015, asijge
 *
 */

package com.groupalarm.asijge.groupalarm.List;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.groupalarm.asijge.groupalarm.Data.Alarm;
import com.groupalarm.asijge.groupalarm.R;

import java.util.List;

/**
 *  
 *
 *  @author asijge
 */
public class RemoveListViewAdapter extends ArrayAdapter<Alarm> {

    private static final String TAG = "RemoveListViewAdapter";
    private Context context;
    public boolean[] itemsToRemove;

    /** Constructs a new CustomListViewAdapter containing elements based on those in
     *  the list (the "items" param), with a layout based on the resourceId.
     *
     * @param context           The Context in which it is used.
     * @param resourceId        The ID of the XML resource that is to be used.
     * @param items             A List containing Alarms, which in turn contain the
     *                          data relevant for each item that is to be presented.
     */
    public RemoveListViewAdapter(Context context, int resourceId,
                                 List<Alarm> items) {
        super(context, resourceId, items);
        this.context = context;
        int arraySize = items.size();
        itemsToRemove = new boolean[arraySize];
    }

    /**
     * Private class containing the elements required to construct the View in
     * the getView method.
     */
    private class ViewHolder {
        TextView time;
        TextView eventDesc;
        CheckBox checkBox;
    }

    /** Creates a View containing graphical objects that are based on data
     *  from the List<Alarm> provided in the constructor.
     *
     * @param position          The position of the element which a new View has been requested for.
     * @param convertView       The instructions for how to construct the new View, used to recycle
     *                          views as to not have too many running at once.
     * @param parent            The object which contains this view, in our case a ListView.
     *
     * @return                  Returns the updated View for the desired element.
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        // The rowItem located on the "position" provided as a parameter.
        Alarm rowItem = getItem(position);
        ViewHolder holder = null;

        Log.d(TAG, "GetView for pos: " + position);

        // If convertView is null we need to construct the layout of our holder that is to be used
        // in the View.
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

        // If convertView is not null we can reuse information provided in it for us to construct
        // the new/updated View.
        } else {
            holder = (ViewHolder) convertView.getTag();
            holder.checkBox.setOnCheckedChangeListener(removeAlarmCheckedListener(position));
        }

        // Provide information and set states based on the relevant alarm.
        holder.time.setText(rowItem.toString());
        holder.eventDesc.setText(rowItem.getMessage());

        return convertView;
    }

    /**
     * Creates and returns a OnCheckedChangeListener with the desired functionality for
     * a button that is to be used in the View created in the getView method.
     *
     * @param postition The position of the object tick box change.
     *
     * @return          Returns a CompoundButton.OnCheckedChangeListener
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

    /**
     * Notifies that some data has been changed and that the View needs to be redrawn.
     */
    private void reDrawUi() {
        this.notifyDataSetChanged();
    }
}


