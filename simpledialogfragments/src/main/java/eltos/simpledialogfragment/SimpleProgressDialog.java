package eltos.simpledialogfragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.NumberFormat;

public class SimpleProgressDialog extends CustomViewDialog<SimpleProgressDialog> {


    public static final String TAG = "SimpleProgressDialog.";

    private static final String
            INDETERMINATE = TAG + "indeterminate",
            MAX = TAG + "max",
            PROGRESS = TAG + "progress",
            PROGRESS2 = TAG + "progress2",
            PERCENTAGE = TAG + "percentage",
            TEXT_PROGRESS = TAG + "text_progress",
            TEXT_INFO = TAG + "text_info",
            TYPE = TAG + "type";

    /**
     * Enum for various progress bar types.
     * Can be set via {@link SimpleProgressDialog#type(Type)}
     * Please note that some features might not be available depending on the chosen type
     */
    public enum Type {
        /**
         * A horizontal progress bar
         * {@link R.layout#simpledialogfragment_progress}
         */
        BAR,

        /**
         * A circle (only indeterminate)
         * {@link R.layout#simpledialogfragment_progress_circle}
         */
        CIRCLE,
    };


    public static SimpleProgressDialog build(){
        return new SimpleProgressDialog();
    }
    public static SimpleProgressDialog bar(){
        return SimpleProgressDialog.build()
                .type(Type.BAR)
                .percentage(true);
    }
    public static SimpleProgressDialog indeterminateCircle(){
        return SimpleProgressDialog.build()
                .type(Type.CIRCLE)
                .percentage(false);
    }

    public SimpleProgressDialog(){
        // defaults
        cancelable(false); // not cancelable by back-button press
        neut(android.R.string.cancel); // cancelable by cancel button
        pos(null);
    }


    /**
     * Set the progress bar type to any of {@link SimpleProgressDialog.Type}
     * Please note that some features might not be available depending on the chosen type
     *
     * @param type The progress bar type {@link SimpleProgressDialog.Type}
     * @return this instance
     */
    public SimpleProgressDialog type(Type type){
        return setArg(TYPE, type.ordinal());
    }

    /**
     * Whether to show the percentage text or not
     *
     * @param visible percentage text visible or not
     * @return this instance
     */
    public SimpleProgressDialog percentage(boolean visible){
        return setArg(PERCENTAGE, visible);
    }


    /**
     * Set or update the progress before or while the dialog is shown.
     *
     * @param indeterminate indeterminate mode (or null to keep the previous value)
     * @param progress primary progress (or null to keep the previous value)
     * @param secondaryProgress secondary progress (if any, or null to keep the previous value)
     * @param max maximum for progress (or null to keep the previous value)
     */
    public void updateProgress(@Nullable Boolean indeterminate, @Nullable Integer progress,
                               @Nullable Integer secondaryProgress, @Nullable Integer max){
        if (mProgressBar == null) {
            // not yet shown, update arguments instead
            if (indeterminate != null) setArg(INDETERMINATE, indeterminate);
            if (progress != null) setArg(PROGRESS, progress);
            if (secondaryProgress != null) setArg(PROGRESS2, secondaryProgress);
            if (max != null) setArg(MAX, max);
        } else {
            // already shown, update progress bar
            if (indeterminate != null) mProgressBar.setIndeterminate(indeterminate);
            if (progress != null) mProgressBar.setProgress(progress);
            if (secondaryProgress != null) mProgressBar.setSecondaryProgress(secondaryProgress);
            if (max != null) mProgressBar.setMax(max);

            // set percentage text if not disabled
            if (getArgs().getBoolean(PERCENTAGE)){
                double percent;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    percent = 1.0 * (mProgressBar.getProgress() - mProgressBar.getMin()) / (mProgressBar.getMax() -mProgressBar.getMin());
                } else {
                    percent = 1.0 * mProgressBar.getProgress() / mProgressBar.getMax();
                }
                updateProgressTextInternal(NumberFormat.getPercentInstance().format(percent));
            }
        }

    }
    /**
     * Set or update the progress before or while the dialog is shown.
     * This also sets indeterminate to false.
     *
     * @param progress primary progress
     */
    public void updateProgress(int progress){
        updateProgress(false, progress, null, null);
    }
    /**
     * Set or update the progress before or while the dialog is shown.
     * This also sets indeterminate to false.
     *
     * @param progress primary progress
     * @param max maximum for progress
     */
    public void updateProgress(int progress, int max){
        updateProgress(false, progress, null, max);
    }
    /**
     * Set or update the progress before or while the dialog is shown.
     *
     * @param max maximum for progress
     */
    public void updateMax(int max){
        updateProgress(null, null, null, max);
    }
    /**
     * Set or update the progress before or while the dialog is shown.
     * This also sets indeterminate to false.
     *
     * @param progress secondary progress
     */
    public void updateSecondaryProgress(int progress){
        updateProgress(false, null, progress, null);
    }
    /**
     * Set or update the progress to be indeterminate before or while the dialog is shown.
     * To set indeterminate to false, use {@link SimpleProgressDialog#updateProgress}
     */
    public void updateIndeterminate(){
        updateProgress(true, null, null, null);
    }

    /**
     * Set or update the progress text at the start of the progress bar /
     * in the center of the circle
     *
     * @param text The text to show
     */
    public void updateProgressText(String text){
        percentage(false);
        updateProgressTextInternal(text);
    }

    private void updateProgressTextInternal(String text){
        if (mProgressText != null) {
            mProgressText.setText(text);
            mProgressText.setVisibility(TextUtils.isEmpty(text) ? View.GONE : View.VISIBLE);
        } else { // not yet shown, update arguments instead
            setArg(TEXT_PROGRESS, text);
        }
    }

    /**
     * Set or update the info text at the end of the bar / next to the circle
     *
     * @param text The text to show
     */
    public void updateInfoText(String text){
        if (mInfoText != null) {
            mInfoText.setText(text);
            mInfoText.setVisibility(TextUtils.isEmpty(text) ? View.GONE : View.VISIBLE);
        } else { // not yet shown, update arguments instead
            setArg(TEXT_INFO, text);
        }
    }




    protected ProgressBar mProgressBar;
    protected TextView mProgressText;
    protected TextView mInfoText;




    @Override
    protected View onCreateContentView(Bundle savedInstanceState) {
        // inflate and set your custom view here

        View view;
        if (getArgs().getInt(TYPE) == Type.CIRCLE.ordinal()) {
            view = inflate(R.layout.simpledialogfragment_progress_circle);
        } else { // default: Type.BAR
            view = inflate(R.layout.simpledialogfragment_progress);
        }
        mProgressBar = view.findViewById(R.id.progress);
        mProgressText = view.findViewById(R.id.progressText);
        mInfoText = view.findViewById(R.id.infoText);


        if (savedInstanceState == null){
            savedInstanceState = getArgs();
        }

        updateInfoText(savedInstanceState.getString(TEXT_INFO));
        updateProgressTextInternal(savedInstanceState.getString(TEXT_PROGRESS));
        updateProgress(savedInstanceState.getBoolean(INDETERMINATE, true),
                       savedInstanceState.getInt(PROGRESS, 0),
                       savedInstanceState.getInt(PROGRESS2, 0),
                       savedInstanceState.getInt(MAX, 100));

        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(INDETERMINATE, mProgressBar.isIndeterminate());
        outState.putInt(MAX, mProgressBar.getMax());
        outState.putInt(PROGRESS, mProgressBar.getProgress());
        outState.putInt(PROGRESS2, mProgressBar.getSecondaryProgress());
        outState.putString(TEXT_INFO, String.valueOf(mInfoText.getText()));
        outState.putString(TEXT_PROGRESS, String.valueOf(mProgressText.getText()));
    }

}
