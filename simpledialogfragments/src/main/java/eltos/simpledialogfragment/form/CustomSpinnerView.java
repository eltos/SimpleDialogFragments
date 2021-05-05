package eltos.simpledialogfragment.form;


import android.content.Context;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatSpinner;

public class CustomSpinnerView extends AppCompatSpinner {
    private OnSpinnerOpenListener mListener;

    public CustomSpinnerView(Context context, AttributeSet attrs, int defStyleAttr, int mode) {
        super(context, attrs, defStyleAttr, mode);
    }

    public CustomSpinnerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CustomSpinnerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomSpinnerView(Context context, int mode) {
        super(context, mode);
    }

    public CustomSpinnerView(Context context) {
        super(context);
    }

    public interface OnSpinnerOpenListener {
        void onOpen();
    }

    @Override
    public boolean performClick() {
        if (mListener != null) {
            mListener.onOpen();
        }
        return super.performClick();
    }

    public void setSpinnerEventsListener(OnSpinnerOpenListener onSpinnerEventsListener) {
        mListener = onSpinnerEventsListener;
    }

}
