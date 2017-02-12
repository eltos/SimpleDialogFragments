/**
 * Copyright 2017 Philipp Niedermayer (github.com/eltos)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */
package eltos.simpledialogfragment;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.TimePicker;

/**
 * A time-picker dialog
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

    /**
     * Specify the initially set hour
     *
     * @param hour initial hour (0-23)
     */
    public SimpleTimeDialog hour(int hour){ return setArg(HOUR, hour); }

    /**
     * Specify the initially set minute
     *
     * @param minute initial minute (0-59)
     */
    public SimpleTimeDialog minute(int minute){ return setArg(MINUTE, minute); }

    /**
     * Changes the hour display mode between 24 and AM/PM
     *
     * @param view24Hour true to use 24 hour mode, false to use AM/PM
     */
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
        // currently not used
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
