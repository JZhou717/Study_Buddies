package com.study.bindr;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;

/**
 * Dialog fragment for session request
 */
public class SetSessionDialogFrag extends DialogFragment {

    public static final String MESSAGE_KEY = "message_key";
    public static final String TITLE = "title";


    /**
     * Creates the dialog fragment
     * @param savedInstanceState
     * @return
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        final EditText input = new EditText(getActivity());
        input.setHint("Reminder (Minutes)");
        //Only allow user to input numbers
        input.setInputType(InputType.TYPE_CLASS_NUMBER);

        final Bundle bundle = getArguments();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(bundle.getString(TITLE));
        builder.setMessage(bundle.getString(MESSAGE_KEY))
                .setPositiveButton("Accept",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // get user input
                                String reminderString = input.getText().toString();

                                //to return the result to the Activity, have to cast the activity to the interface:
                                DialogReturn activity = (DialogReturn) getActivity();
                                activity.onPositive(reminderString);

                            }
                        })
                .setNegativeButton("Decline",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //tell Activity to exit game
                                DialogReturn activity = (DialogReturn) getActivity();
                                activity.onNegative(null);
                                dialog.cancel();
                            }
                        });

        builder.setView(input);

        return builder.create();
    }
}