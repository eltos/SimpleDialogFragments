package eltos.simpledialogfragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;


/**
 * The base class for all custom dialogs.
 * Extend this class and implement/overwrite the methods to use your custom view
 * See {@link SimpleCheckDialog} as an example.
 *
 * Created by eltos on 11.10.2015.
 */

public abstract class CustomViewDialog<This extends CustomViewDialog<This>>
        extends SimpleDialog<This> {



    /**
     * Inflate your custom view here.
     *
     * @param savedInstanceState The last saved instance state of the Fragment,
     * or null if this fragment is created for the first time.
     *
     * @return Return a new View to be displayed by the Fragment.
     */
    protected abstract View onCreateContentView(Bundle savedInstanceState);

    /**
     * Overwrite this method to provide additional results from your custom view
     * to be passed to the {@link OnDialogResultListener#onResult}
     *
     * @param which see {@link OnDialogResultListener}
     * @return the bundle to merge with the results or null
     */
    protected Bundle onResult(int which){
        return null;
    }



    /**
     * Call this method to enable or disable the positive button,
     * e.g. if you want to consider for preconditions to be fulfilled
     *
     * Note: call this in {@link #onDialogShown} rather than {@link #onCreateContentView}
     */
    protected void setPositiveButtonEnabled(boolean enabled){
        if (positiveButton != null) {
            positiveButton.setEnabled(enabled);
        }
    }

    /**
     * Overwrite this method to catch positive button presses,
     * e.g. if you need to verify input by the user
     *
     * Note: do not call {@link #pressPositiveButton} here!
     *
     * @return false to ignore the press, true to process normally
     */
    protected boolean acceptsPositiveButtonPress(){
        return true;
    }

    /**
     * Overwrite this method to take action once the dialog is shown
     * such as settings an input focus, showing the keyboard or
     * setting the initial positiveButtonState
     *
     */
    protected void onDialogShown(){

    }

    /**
     * Simulates a positive button press.
     * You may use this method in combination with
     * ImeOptions such as {@link EditorInfo#IME_ACTION_DONE}
     *
     * Note: do not call this method from {@link #acceptsPositiveButtonPress} !!!
     */
    protected void pressPositiveButton(){
        if (acceptsPositiveButtonPress()) {
            getDialog().dismiss();
            callResultListener(DialogInterface.BUTTON_POSITIVE, null);
        }
    }


    //////////////////////////////////////////////////////////////////////////////////////////
    // Internal


    private Button positiveButton;


    @Override
    @CallSuper
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(false);
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog dialog = (AlertDialog) super.onCreateDialog(savedInstanceState);
        View content = onCreateContentView(savedInstanceState);
        dialog.setView(content);

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface d) {
                positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pressPositiveButton();
                    }
                });
                onDialogShown();

            }
        });

        return dialog;
    }

    @Override
    boolean callResultListener(int which, Bundle extras) {
        Bundle results = onResult(which);
        if (extras == null) extras = new Bundle();
        if (results != null) extras.putAll(results);
        return super.callResultListener(which, extras);
    }


}




