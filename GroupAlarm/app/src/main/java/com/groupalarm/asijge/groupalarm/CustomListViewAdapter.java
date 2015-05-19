package com.groupalarm.asijge.groupalarm;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.groupalarm.asijge.groupalarm.AlarmManaging.AlarmHelper;
import com.groupalarm.asijge.groupalarm.Data.Alarm;
import com.groupalarm.asijge.groupalarm.Data.ListRowItem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 *  CustomListViewAdapter provides the functionality of the ArrayAdapter class as well
 *  as extended functionality which allows it to contain the desired elements and layout
 *  of the ListView used in the MainActivity class.
 *
 *  @author asijge
 */
public class CustomListViewAdapter extends ArrayAdapter<ListRowItem> {

    public static final String TAG = "CustomListViewAdapter";
    Context context;

    /**
     *  Constructs a new CustomListViewAdapter containing elements based on those in
     *  the list (the "items" param), with a basic layout based on the resourceId.
     *
     * @param context           The Context in which it is used.
     * @param resourceId        The ID of the XML resource that is to be used.
     * @param items             A List containing ListRowItems, which in turn contain the
     *                          data relevant for each item that is to be presented.
     */
    public CustomListViewAdapter(Context context, int resourceId,
                                 List<ListRowItem> items) {
        super(context, resourceId, items);
        this.context = context;
    }

    /**
     * Private class containing the elements required to construct the View in
     * the getView method.
     */
    private class ViewHolder {
        ImageView imageView;
        TextView time;
        TextView eventDesc;
        TextView monday,tuesday,wednesday,thursday,friday,saturday,sunday;
        Switch checkBox;
    }

    /**
     *  Creates a View containing graphical objects that are based on data
     *  from the List<ListRowItem> provided in the constructor.
     *
     * @param position          The position of the element which a new View has been requested for.
     * @param convertView       The instructions for how to construct the new View, used to recycle
     *                          views as to not have too many running at once.
     * @param parent            The object which contains this view, in our case a ListView.
     *
     * @return                  Returns the new/updated View for the desired element.
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        // The rowItem located on the "position" provided as a parameter.
        ListRowItem rowItem = getItem(position);
        ViewHolder holder = null;

        // A list used for convenience, as to be able to loop through the TextViews
        // representative for each day. Then based on whether the alarm should go off that
        // day change the color of the TextView to either black for activated or gray for deactivated.
        ArrayList<TextView> days = new ArrayList<TextView>();

        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        // If convertView is null we need to construct the layout of our holder that is to be used
        // in the View.
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.alarm_list_item, null);
            holder = new ViewHolder();
            holder.time = (TextView) convertView.findViewById(R.id.time);
            holder.eventDesc = (TextView) convertView.findViewById(R.id.message);
            holder.imageView = (ImageView) convertView.findViewById(R.id.icon);
            holder.checkBox = (Switch) convertView.findViewById(R.id.on_off);
            holder.checkBox.setThumbTextPadding(4);//holder.checkBox.getWidth() / 2);
            holder.checkBox.setOnCheckedChangeListener(alarmCheckedListener(rowItem.getAlarm()));
            holder.monday = (TextView) convertView.findViewById(R.id.monday);
            holder.tuesday = (TextView) convertView.findViewById(R.id.tuesday);
            holder.wednesday = (TextView) convertView.findViewById(R.id.wednesday);
            holder.thursday = (TextView) convertView.findViewById(R.id.thursday);
            holder.friday = (TextView) convertView.findViewById(R.id.friday);
            holder.saturday = (TextView) convertView.findViewById(R.id.saturday);
            holder.sunday = (TextView) convertView.findViewById(R.id.sunday);
            convertView.setTag(holder);

        // If convertView is not null we can reuse information provided in it for us to construct
        // the new/updated View.
        } else {
            holder = (ViewHolder) convertView.getTag();
            holder.checkBox.setOnCheckedChangeListener(alarmCheckedListener(rowItem.getAlarm()));
        }

        // Add the TextViews to the "days" list as to be able to loop through them.
        days.add(holder.monday);
        days.add(holder.tuesday);
        days.add(holder.wednesday);
        days.add(holder.thursday);
        days.add(holder.friday);
        days.add(holder.saturday);
        days.add(holder.sunday);

        // Provide information and set button/object states based on the relevant alarm.

        holder.time.setText(rowItem.getAlarm().toString());
        holder.eventDesc.setText(rowItem.getAlarm().getMessage());
        holder.imageView.setImageResource(R.drawable.ic_alarm_image);

        if(holder.checkBox.isChecked() != rowItem.getAlarm().getStatus()) {
            holder.checkBox.setChecked(rowItem.getAlarm().getStatus());
        }

        if(rowItem.getAlarm().getStatus()) {
            holder.time.setTextColor(Color.BLACK);
            holder.eventDesc.setTextColor(Color.BLACK);
            holder.imageView.setAlpha(1.0f);
        } else {
            holder.time.setTextColor(Color.GRAY);
            holder.eventDesc.setTextColor(Color.GRAY);
            holder.imageView.setAlpha(0.5f);
        }

        // Set the color of the days which the alarm is activated for to black, set the others to gray.
        for(int i = 0; i < rowItem.getAlarm().getDays().length; i++) {
            if (rowItem.getAlarm().getDays()[i]) {
                days.get(i).setTextColor(Color.BLACK);
            } else {
                days.get(i).setTextColor(Color.GRAY);
            }
        }

        return convertView;
    }

    /**
     * Creates and returns a OnCheckedChangeListener with the desired functionality for
     * a button that is to be used in the View created in the getView method.
     *
     * @param alarm     The alarm that is to be linked to the button that has been assigned this listener.
     *
     * @return          Returns a CompoundButton.OnCheckedChangeListener
     */
    private CompoundButton.OnCheckedChangeListener alarmCheckedListener(final Alarm alarm) {
        return new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The purpose of the if(!alarm.getStatus()) check is to make sure that toasts are
                    // only created for the alarm that was actually clicked
                    if(!alarm.getStatus()) {
                        AlarmHelper.setActive(alarm.getId(), true);
                        alarm.setActive(true);

                        // Creates and displays a toast message every time an alarm is activated
                        Toast toast = Toast.makeText(context, resolveToastMessage(AlarmHelper.getNextAlarmTime(alarm)),Toast.LENGTH_SHORT);
                        toast.show();
                    }
                } else {
                    AlarmHelper.setActive(alarm.getId(), false);
                    alarm.setActive(false);
                }
                AlarmHelper.setAlarms(context);
                reDrawUi();
            }
        };
    }

    /**
     * Notifies that some data has been changed and that the View needs to be redrawn.
     */
    private void reDrawUi() {
        notifyDataSetChanged();
    }

    /**
     *  Creates and returns a String that is used in the toast message created in the
     *  alarmCheckedListener method.
     *
     * @param cal       The Calendar object which is to be compared to the current time.
     *
     * @return          Returns a String containing information about how much difference there is
     *                  between the current time and the parameter cal.
     */
    private String resolveToastMessage(Calendar cal) {
        Calendar now = Calendar.getInstance();
        // Difference between the set time and now in seconds.
        int seconds = (int) (cal.getTimeInMillis()/1000 - now.getTimeInMillis()/1000);
        String message = "Alarm set ";
        int minute = (seconds % 3600) / 60;
        int hour = (seconds % (3600 * 24)) / 3600;
        int day = seconds / (3600 * 24);

        // Determine if there is more than one day left until we reach the time provided in cal.
        if (day > 0) {
            message += day + " day";
            if (day > 1) message += "s";
            if (hour + minute > 0) message += ", ";
        }
        // Determine if there is more than one hour in difference between the time provided in cal and now.
        if (hour > 0) {
            message += hour + " hour";
            if (hour > 1) message += "s";
            if (minute > 0) message += ", ";
        }
        // Determine if there is more than one minute in difference between the time provided in cal and now.
        if (minute > 0) {
            message += minute + " minute";
            if (minute > 1) message += "s";
        }
        if (day + hour + minute == 0) {
            message += "less than 1 minute";
        }
        message += " from now.";

        return message;
    }
}
