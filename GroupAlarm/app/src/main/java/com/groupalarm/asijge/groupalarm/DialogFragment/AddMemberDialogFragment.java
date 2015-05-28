package com.groupalarm.asijge.groupalarm.DialogFragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.groupalarm.asijge.groupalarm.AlarmManaging.ParseHelper;
import com.groupalarm.asijge.groupalarm.EditGroupActivity;
import com.groupalarm.asijge.groupalarm.R;
import com.parse.Parse;

/**
 * AddMemberDialogFragment provides the functionality of the DialogFragment class as well as
 * a modified version of the onCreateDialog function that creates a dialog suitable for
 * adding a member to a group.
 *
 * @author asijge
 */
public class AddMemberDialogFragment extends DialogFragment {

    /**
     * {@inheritDoc}
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View addMemberLayout = View.inflate(getActivity(), R.layout.add_dialog, null);

        final EditText textField = (EditText) addMemberLayout.findViewById(R.id.member_field);
        textField.setHint("The new member's username");
        textField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });

        addMemberLayout.setTag(textField);

        builder.setView(addMemberLayout);
        builder.setMessage("Add member")
                .setPositiveButton("Invite", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ((EditGroupActivity)getActivity()).addUser(textField.getText().toString().trim());
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Cancel, handles it self through inherited functionality
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
