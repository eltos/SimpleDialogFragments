package eltos.simpledialogfragment;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;

/**
 *
 * Created by eltos on 02.02.2017.
 */
public class SimpleDateDialog extends CustomViewDialog<SimpleDateDialog>
        implements DatePicker.OnDateChangedListener {

    private static final String TAG = "simpleDateDialog";

    public static final String DATE = TAG + "date";
    public static final String MIN_DATE = TAG + "minDate";
    public static final String MAX_DATE = TAG + "maxDate";
    public static final String FIRST_DAY_OF_WEEK = TAG + "firstDayOfWeek";
    private DatePicker picker;

    public static SimpleDateDialog build(){
        return new SimpleDateDialog();
    }


    public SimpleDateDialog date(Date date){ return date(date.getTime()); }
    public SimpleDateDialog date(long millis){ return setArg(DATE, millis); }
    public SimpleDateDialog minDate(Date date){ return minDate(date.getTime()); }
    public SimpleDateDialog minDate(long millis){ return setArg(MIN_DATE, millis); }
    public SimpleDateDialog maxDate(Date date){ return maxDate(date.getTime()); }
    public SimpleDateDialog maxDate(long millis){ return setArg(MAX_DATE, millis); }
    public SimpleDateDialog firstDayOfWeek(Date date){ return setArg(FIRST_DAY_OF_WEEK, date.getTime()); }



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
