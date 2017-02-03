package eltos.simpledialogfragment;

import android.app.TimePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;

/**
 *
 * Created by eltos on 02.02.2017.
 */
public class SimpleTimeDialog extends CustomViewDialog<SimpleTimeDialog> implements TimePicker.OnTimeChangedListener {

    private static final String TAG = "simpleDateDialog";

    public static final String HOUR = TAG + "hour";
    public static final String MINUTE = TAG + "minute";
    public static final String VIEW_24_HOUR = TAG + "24HourView";
    private TimePicker picker;

    public static SimpleTimeDialog build(){
        return new SimpleTimeDialog();
    }


    public SimpleTimeDialog hour(int hour){ return setArg(HOUR, hour); }
    public SimpleTimeDialog minute(int minute){ return setArg(MINUTE, minute); }
    public SimpleTimeDialog set24HourView(boolean view24Hour){ return setArg(VIEW_24_HOUR, view24Hour); }



    @Override
    protected View onCreateContentView(Bundle savedInstanceState) {

        picker = new TimePicker(getContext());

        if (savedInstanceState != null){
            picker.setCurrentHour(savedInstanceState.getInt(HOUR));
            picker.setCurrentMinute(savedInstanceState.getInt(MINUTE));

        } else {
            if (getArguments().containsKey(HOUR)) {
                picker.setCurrentHour(getArguments().getInt(HOUR));
            }
            if (getArguments().containsKey(MINUTE)) {
                picker.setCurrentMinute(getArguments().getInt(MINUTE));
            }
        }

        if (getArguments().containsKey(VIEW_24_HOUR)) {
            picker.setIs24HourView(getArguments().getBoolean(VIEW_24_HOUR));
        } else {
            picker.setIs24HourView(DateFormat.is24HourFormat(getContext())); // system default
        }
        picker.setOnTimeChangedListener(this);

        return picker;
    }


    @Override
    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(HOUR, picker.getCurrentHour());
        outState.putInt(MINUTE, picker.getCurrentMinute());
        super.onSaveInstanceState(outState);
    }


    @Override
    protected Bundle onResult(int which) {
        Bundle results = new Bundle();
        results.putInt(HOUR, picker.getCurrentHour());
        results.putInt(MINUTE, picker.getCurrentMinute());
        return results;
    }
}
