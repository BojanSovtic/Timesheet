package com.bojansovtic.timesheet;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class AppDialog extends DialogFragment  {

    public static final String DIALOG_ID = "id";
    public static final String DIALOG_MESSAGE = "message";
    public static final String DIALOG_POSITIVE_RID = "positive_rid";
    public static final String DIALOG_NEGATIVE_RID = "negative_rid";

    interface DialogEvents {
        void onPositiveDialogResult(int dialogId, Bundle args);
        void onNegativeDialogResult(int dialogId, Bundle args);
        void onDialogCancelled(int dialogId);
    }

    private DialogEvents dialogEvents;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (!(context instanceof DialogEvents)) {
            throw new ClassCastException(context.toString() + " must implement AppDialog.DialogEvents " +
                    "interface");
        }

        dialogEvents = (DialogEvents) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        dialogEvents = null;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final Bundle arguments = getArguments();
        final int dialogId;
        String messageString;
        int positiveStringId;
        int negativeStringId;

        if (arguments != null) {
            dialogId = arguments.getInt(DIALOG_ID);
            messageString = arguments.getString(DIALOG_MESSAGE);

            if (dialogId == 0 || messageString == null) {
                throw new IllegalArgumentException("DIALOG_ID and/or DIALOG_MESSAGE not present in the bundle");
            }

            positiveStringId = arguments.getInt(DIALOG_POSITIVE_RID);
            if (positiveStringId == 0) {
                positiveStringId = R.string.ok;
            }
            negativeStringId = arguments.getInt(DIALOG_NEGATIVE_RID);
            if (negativeStringId == 0) {
                negativeStringId = R.string.cancel;
            }
        } else {
            throw new IllegalArgumentException("Must pass DIALOG_ID and DIALOG_MESSAGE in the bundle");
        }

        builder.setMessage(messageString)
                .setPositiveButton(positiveStringId, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position) {
                        if (dialogEvents != null) {
                            dialogEvents.onPositiveDialogResult(dialogId, arguments);
                        }
                    }
                })
                .setNegativeButton(negativeStringId, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position) {
                        if (dialogEvents != null) {
                            dialogEvents.onNegativeDialogResult(dialogId, arguments);
                        }
                    }
                });

        return builder.create();
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        if (dialogEvents != null) {
            int dialogId = getArguments().getInt(DIALOG_ID);
            dialogEvents.onDialogCancelled(dialogId);
        }
    }
}
