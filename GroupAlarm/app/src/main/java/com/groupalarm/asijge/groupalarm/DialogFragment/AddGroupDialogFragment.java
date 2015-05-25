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

/**
 * Created by Sebastian on 2015-05-20.
 */
public class AddGroupDialogFragment extends DialogFragment {

    private String groupName = "";

    public String getGroupName() {
        return groupName;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View addMemberLayout = View.inflate(getActivity(), R.layout.add_dialog, null);
        final EditText textField = (EditText) addMemberLayout.findViewById(R.id.member_field);
        textField.setHint("The new group's name");
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
        builder.setMessage("Create group")
                .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        groupName = textField.getText().toString().trim();
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
