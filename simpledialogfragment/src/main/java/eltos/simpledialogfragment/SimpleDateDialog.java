/*
 *  Copyright 2018 Philipp Niedermayer (github.com/eltos)
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

package eltos.simpledialogfragment;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;

/**
 * A date-picker dialog
 *
 * Created by eltos on 02.02.2017.
 */
public class SimpleDateDialog extends CustomViewDialog<SimpleDateDialog>
        implements DatePicker.OnDateChangedListener {

    public static final String TAG = "SimpleDateDialog.";

    public static final String
            DATE = TAG + "DATE";


    public static SimpleDateDialog build(){
        return new SimpleDateDialog();
    }


    /**
     * Specify the initially set date
     *
     * @param date initial date
     */
    public SimpleDateDialog date(Date date){ return date(date.getTime()); }

    /**
     * Specify the initially set date as milliseconds
     *
     * @param millis milliseconds since Jan. 1, 1970, midnight GMT.
     */
    public SimpleDateDialog date(long millis){ return setArg(DATE, millis); }

    /**
     * Sets the first date selectable
     *
     * @param date minimal date
     */
    public SimpleDateDialog minDate(Date date){ return minDate(date.getTime()); }

    /**
     * Sets the first date selectable as milliseconds
     *
     * @param millis milliseconds since Jan. 1, 1970, midnight GMT.
     */
    public SimpleDateDialog minDate(long millis){ return setArg(MIN_DATE, millis); }

    /**
     * Sets the last date selectable
     *
     * @param date maximal date
     */
    public SimpleDateDialog maxDate(Date date){ return maxDate(date.getTime()); }

    /**
     * Sets the last date selectable as milliseconds
     *
     * @param millis milliseconds since Jan. 1, 1970, midnight GMT.
     */
    public SimpleDateDialog maxDate(long millis){ return setArg(MAX_DATE, millis); }

    /**
     * Set the first day of the week to display
     *
     * @param day one of {@link Calendar#MONDAY}, {@link Calendar#TUESDAY},
     *            {@link Calendar#WEDNESDAY}, {@link Calendar#THURSDAY}, {@link Calendar#FRIDAY},
     *            {@link Calendar#SATURDAY}, {@link Calendar#SUNDAY},
     */
    public SimpleDateDialog firstDayOfWeek(int day){ return setArg(FIRST_DAY_OF_WEEK, day); }




    protected static final String
            MIN_DATE = TAG + "MIN_DATE",
            MAX_DATE = TAG + "MAX_DATE",
            FIRST_DAY_OF_WEEK = TAG + "FIRST_DAY_OF_WEEK";

    private DatePicker picker;

    @Override
    protected View onCreateContentView(Bundle savedInstanceState) {

        picker = new DatePicker(getContext());

        Calendar c = Calendar.getInstance();
        if (savedInstanceState != null){
            c.setTimeInMillis(savedInstanceState.getLong(DATE));
        } else if (getArguments().containsKey(DATE)) {
            c.setTimeInMillis(getArguments().getLong(DATE));
        }

        picker.init(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), this);

        if (getArguments().containsKey(MIN_DATE)) {
            picker.setMinDate(getArguments().getLong(MIN_DATE));
        }
        if (getArguments().containsKey(MAX_DATE)) {
            picker.setMaxDate(getArguments().getLong(MAX_DATE));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                && getArguments().containsKey(FIRST_DAY_OF_WEEK)) {
            picker.setFirstDayOfWeek(getArguments().getInt(FIRST_DAY_OF_WEEK));
        }



        return picker;
    }

    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        // currently not used
    }

    private long getCurrentMillis(){
        Calendar c = Calendar.getInstance();
        c.set(picker.getYear(), picker.getMonth(), picker.getDayOfMonth(), 0, 0, 0);
        return c.getTimeInMillis();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putLong(DATE, getCurrentMillis());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected Bundle onResult(int which) {
        Bundle results = new Bundle();
        results.putLong(DATE, getCurrentMillis());
        return results;
    }
}
