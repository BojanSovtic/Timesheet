package com.bojansovtic.timesheet;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Date;
import java.util.GregorianCalendar;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    private static final String TAG = "DatePickerFragment";

    public static final String DATE_PICKER_ID = "ID";
    public static final String DATE_PICKER_TITLE = "TITLE";
    public static final String DATE_PICKER_DATE = "DATE";

    int dialogId = 0;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final GregorianCalendar calendar = new GregorianCalendar();
        String title = null;

        Bundle arguments = getArguments();
        if (arguments != null) {
            dialogId = arguments.getInt(DATE_PICKER_ID);
            title = arguments.getString(DATE_PICKER_TITLE);

            Date givenDate = (Date) arguments.getSerializable(DATE_PICKER_DATE);
            if (givenDate != null) {
                calendar.setTime(givenDate);
            }
        }

        int year = calendar.get(GregorianCalendar.YEAR);
        int month = calendar.get(GregorianCalendar.MONTH);
        int day = calendar.get(GregorianCalendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), this, year, month, day);
        if (title != null) {
            datePickerDialog.setTitle(title);
        }
        return datePickerDialog;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (!(context instanceof DatePickerDialog.OnDateSetListener)) {
            throw new ClassCastException(context.toString() + " must implement atePickerDialog.OnDateSetListener interface");
        }
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
        DatePickerDialog.OnDateSetListener listener = (DatePickerDialog.OnDateSetListener) getActivity();
        if (listener != null) {
            datePicker.setTag(dialogId);
            listener.onDateSet(datePicker, year, month, dayOfMonth);
        }
    }
}
