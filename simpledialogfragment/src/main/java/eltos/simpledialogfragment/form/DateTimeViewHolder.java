/*
 *  Copyright 2019 Philipp Niedermayer (github.com/eltos)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package eltos.simpledialogfragment.form;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import eltos.simpledialogfragment.R;
import eltos.simpledialogfragment.SimpleDateDialog;
import eltos.simpledialogfragment.SimpleDialog;
import eltos.simpledialogfragment.SimpleTimeDialog;

/**
 * The ViewHolder class for {@link ColorField}
 * 
 * This class is used to create a Color Box and to maintain it's functionality
 * 
 * Created by eltos on 06.07.2018.
 */

class DateTimeViewHolder extends FormElementViewHolder<DateTime> implements SimpleDialog.OnDialogResultListener {

    protected static final String SAVED_DATE = "date",
            SAVED_HOUR = "hour",
            SAVED_MINUTE = "minute";
    private static final String DATE_DIALOG_TAG = "datePickerDialogTag",
            TIME_DIALOG_TAG = "timePickerDialogTag";
    private TextView label;
    private EditText date;
    private EditText time;
    private Long day;
    private Integer hour, minute;

    public DateTimeViewHolder(DateTime field) {
        super(field);
    }

    @Override
    protected int getContentViewLayout() {
        return R.layout.simpledialogfragment_form_item_datetime;
    }

    @Override
    protected void setUpView(View view, final Context context, Bundle savedInstanceState,
                             final SimpleFormDialog.DialogActions actions) {

        label = (TextView) view.findViewById(R.id.label);
        date = (EditText) view.findViewById(R.id.date);
        time = (EditText) view.findViewById(R.id.time);

        // Label
        String text = field.getText(context);
        label.setText(text);
        label.setVisibility(text == null ? View.GONE : View.VISIBLE);
//        label.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                colorView.performClick();
//            }
//        });

        // Color preset
//        if (savedInstanceState != null) {
//            colorView.setColor(savedInstanceState.getInt(SAVED_COLOR));
//        } else {
//            colorView.setColor(field.getInitialColor(context));
//        }
//        colorView.setOutlineWidth((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
//                2 /*dp*/, context.getResources().getDisplayMetrics()));
//        colorView.setOutlineColor(field.outline);
//        colorView.setStyle(ColorView.Style.PALETTE);
//        colorView.setChecked(true);
//
//
        date.setVisibility(field.type == DateTime.Type.DATETIME
                || field.type == DateTime.Type.DATE ? View.VISIBLE : View.GONE);

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateDialog dialog = SimpleDateDialog.build()
                        .title(field.getText(context))
                        .neut();
                if (field.min != null) dialog.minDate(field.min);
                if (field.max != null) dialog.maxDate(field.max);
                if (!field.required) dialog.neg(R.string.clear);
                if (day != null) dialog.date(day);
                actions.showDialog(dialog, DATE_DIALOG_TAG+field.resultKey);
            }
        });


        time.setVisibility(field.type == DateTime.Type.DATETIME
                || field.type == DateTime.Type.TIME ? View.VISIBLE : View.GONE);

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleTimeDialog dialog = SimpleTimeDialog.build()
                                .title(field.getText(context))
                                .neut();
                if (hour != null) dialog.hour(hour);
                if (minute != null) dialog.minute(minute);
                if (!field.required) dialog.neg(R.string.clear);
                actions.showDialog(dialog, TIME_DIALOG_TAG+field.resultKey);
            }
        });

        // preset
        if (savedInstanceState != null){
            day = savedInstanceState.getLong(SAVED_DATE);
            hour = savedInstanceState.getInt(SAVED_HOUR);
            minute = savedInstanceState.getInt(SAVED_MINUTE);
        } else {
            day = field.date;
            hour = field.hour;
            minute = field.minute;
        }

        updateText();

    }

    private void updateText(){
        date.setText(day == null ? null : SimpleDateFormat.getDateInstance().format(new Date(day)));
        time.setText(hour == null || minute == null ? null :
                SimpleDateFormat.getTimeInstance(DateFormat.SHORT).format(new Date(0, 0, 0, hour, minute)));
    }


    @Override
    protected void saveState(Bundle outState) {
        if (day != null) outState.putLong(SAVED_DATE, day);
        if (hour != null) outState.putInt(SAVED_HOUR, hour);
        if (minute != null) outState.putInt(SAVED_MINUTE, minute);
    }


    @Override
    protected void putResults(Bundle results, String key) {
        Date res = new Date(day == null ? 0 : day);
        res.setHours(hour == null ? 0 : hour);
        res.setMinutes(minute == null ? 0 : minute);
        results.putLong(key, res.getTime());
    }


    @Override
    protected boolean focus(final SimpleFormDialog.FocusActions actions) {
        actions.hideKeyboard();
        if (field.type == DateTime.Type.TIME){
            time.performClick();
        } else {
            date.performClick();
        }
        return true;
    }


    @Override
    protected boolean posButtonEnabled(Context context) {
        return !field.required
                || field.type == DateTime.Type.DATE && day != null
                || field.type == DateTime.Type.TIME && hour != null && minute != null
                || field.type == DateTime.Type.DATETIME && day != null && hour != null && minute != null;
    }


    @Override
    protected boolean validate(Context context) {
        boolean valid = posButtonEnabled(context);
        date.setError(null);
        time.setError(null);
        if (!valid) {
            if (day == null){
                date.setError(context.getString(R.string.required));
            }
            if (hour == null || minute == null){
                time.setError(context.getString(R.string.required));
            }
        }
        return valid;
    }

    @Override
    public boolean onResult(@NonNull String dialogTag, int which, @NonNull Bundle extras) {
        if ((DATE_DIALOG_TAG+field.resultKey).equals(dialogTag)){
            if (which == BUTTON_POSITIVE){
                day = extras.getLong(SimpleDateDialog.DATE);
                if (field.type == DateTime.Type.DATETIME){
                    time.performClick();
                }
            } else if (which == BUTTON_NEGATIVE){
                day = null;
            }
            updateText();
            return true;
        }
        if ((TIME_DIALOG_TAG+field.resultKey).equals(dialogTag)){
            if (which == BUTTON_POSITIVE){
                hour = extras.getInt(SimpleTimeDialog.HOUR);
                minute = extras.getInt(SimpleTimeDialog.MINUTE);
            } else if (which == BUTTON_NEGATIVE){
                hour = null;
                minute = null;
            }
            updateText();
            return true;
        }
        return false;
    }
}
