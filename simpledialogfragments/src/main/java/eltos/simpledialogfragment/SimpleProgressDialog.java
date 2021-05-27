package eltos.simpledialogfragment;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class SimpleProgressDialog extends CustomViewDialog<SimpleProgressDialog> {


    public static final String TAG = "SimpleProgressDialog.";

    private static final String
            INDETERMINATE = TAG + "indeterminate",
            MAX = TAG + "max",
            PROGRESS = TAG + "progress",
            PROGRESS2 = TAG + "progress2",
            TYPE = TAG + "type";

    /**
     * Enum for various progress bar types.
     * Can be set via {@link SimpleProgressDialog#type(Type)}
     * Please note that some features might not be available depending on the chosen type
     */
    public enum Type {BAR, CIRCLE};


    public static SimpleProgressDialog build(){
        return new SimpleProgressDialog();
    }
    public static SimpleProgressDialog bar(){
        return SimpleProgressDialog.build().type(Type.BAR);
    }
    public static SimpleProgressDialog indeterminateCircle(){
        return SimpleProgressDialog.build().type(Type.CIRCLE);
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






    public ProgressBar mProgressBar;




    @Override
    protected View onCreateContentView(Bundle savedInstanceState) {
        // inflate and set your custom view here

        View view = inflate(R.layout.simpledialogfragment_progress);
        if (getArgs().getInt(TYPE) == Type.CIRCLE.ordinal()) {
            mProgressBar = view.findViewById(R.id.progressCircle);
        } else { // default: Type.BAR
            mProgressBar = view.findViewById(R.id.progress);
        }


        if (savedInstanceState == null){
            savedInstanceState = getArgs();
        }

        mProgressBar.setVisibility(View.VISIBLE);
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
    }

}
