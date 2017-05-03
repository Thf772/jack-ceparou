package io.jawg.osmcontributor.setting.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.widget.Button;

import io.jawg.osmcontributor.R;

public class NoticeDialogFragment extends DialogFragment {



    /* The activity that creates an instance of this dialog fragment must
         * implement this interface in order to receive event callbacks.
         * Each method passes the DialogFragment in case the host needs to query it. */
    public interface NoticeDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    NoticeDialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
//        Application.init(this.getContext());

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.notice_dialog, null), 0, 0, 0, 0);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
//                        mListener.onDialogPositiveClick(NoticeDialogFragment.this);
                        dialog.dismiss();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
        Button button = alert.getButton(DialogInterface.BUTTON_POSITIVE);
        button.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        // Create the AlertDialog object and return it
        return alert;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        mListener.onDialogPositiveClick(NoticeDialogFragment.this);
    }
}
