package com.groupalarm.asijge.groupalarm.DialogFragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.groupalarm.asijge.groupalarm.AlarmManaging.ParseHelper;
import com.groupalarm.asijge.groupalarm.R;
import com.parse.Parse;

/**
 * Created by Sebastian on 2015-05-20.
 */
public class AddMemberDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View addMemberLayout = View.inflate(getActivity(), R.layout.add_dialog, null);
        final EditText textField = (EditText) addMemberLayout.findViewById(R.id.member_field);
        final String groupName = getArguments().getString("groupname");
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
                        ParseHelper.addUserToGroup(textField.getText().toString().trim(), groupName);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Cancel
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
