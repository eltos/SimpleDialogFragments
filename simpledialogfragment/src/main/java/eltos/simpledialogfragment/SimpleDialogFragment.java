package eltos.simpledialogfragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

/**
 * An easy to use and extendable dialog fragment that displays a text message.
 *
 * Created by eltos on 03.08.2015.
 */
@SuppressWarnings("unused")
public class SimpleDialogFragment<This extends SimpleDialogFragment<This>> extends DialogFragment {

    protected static final String TITLE = "simpleDialogFragment.title";
    protected static final String MESSAGE = "simpleDialogFragment.message";
    protected static final String POSITIVE_BUTTON_TEXT = "simpleDialogFragment.positiveButtonText";
    protected static final String NEGATIVE_BUTTON_TEXT = "simpleDialogFragment.negativeButtonText";
    protected static final String NEUTRAL_BUTTON_TEXT = "simpleDialogFragment.neutralButtonText";
    protected static final String ICON_RESOURCE = "simpleDialogFragment.iconResource";
    protected static final String CANCELABLE = "simpleDialogFragment.cancelable";
    protected static final String BUNDLE = "simpleDialogFragment.bundle";
    protected static final String STYLE = "simpleDialogFragment.style";

    public interface OnDialogFragmentResultListener {
        int CANCELED = 0;
        int BUTTON_POSITIVE = DialogInterface.BUTTON_POSITIVE;
        int BUTTON_NEGATIVE = DialogInterface.BUTTON_NEGATIVE;
        int BUTTON_NEUTRAL = DialogInterface.BUTTON_NEUTRAL;

        /**
         * Let the hosting fragment or activity implement this interface
         * to receive results from the dialog
         *
         * @param dialogTag the tag passed with {@link #show}
         * @param which result type, one of
         *              {@link #BUTTON_POSITIVE}, {@link #BUTTON_NEGATIVE},
         *              {@link #BUTTON_NEUTRAL} or {@link #CANCELED}
         * @param extras the extras passed with {@link #extra(Bundle)}
         * @return true if the result was handled, false otherwise
         */
        boolean onDialogFragmentResult(@NonNull String dialogTag, int which, @NonNull Bundle extras);
    }

    protected DialogInterface.OnClickListener forwardOnClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            callResultListener(which, null);
        }
    };

    protected boolean callResultListener(int which, Bundle extras) {
        if (extras == null) extras = new Bundle();
        if (getArguments().getBundle(BUNDLE) != null) {
            extras.putAll(getArguments().getBundle(BUNDLE));
        }
        boolean handled = false;
        if (getTag() != null) {
            if (getTargetFragment() instanceof OnDialogFragmentResultListener) {
                handled = ((OnDialogFragmentResultListener) getTargetFragment())
                        .onDialogFragmentResult(getTag(), which, extras);
            }
            if (!handled && getActivity() instanceof OnDialogFragmentResultListener) {
                handled = ((OnDialogFragmentResultListener) getActivity())
                        .onDialogFragmentResult(getTag(), which, extras);
            }
        }
        return handled;
    }



    private AlertDialog dialog;

    public SimpleDialogFragment(){
        Bundle args = getArguments();
        if (args == null) args = new Bundle();
        setArguments(args);
    }

    public static SimpleDialogFragment build(){
        return new SimpleDialogFragment();
    }

//    public static SimpleDialogFragment createError(int messageResourceId){
//        return build().title(R.string.error).msg(messageResourceId).pos(R.string.ok);
//    }

    @SuppressWarnings("unchecked cast")
    protected This setArg(String key, boolean value){
        getArguments().putBoolean(key, value);
        return (This) this;
    }
    @SuppressWarnings("unchecked cast")
    protected This setArg(String key, String value){
        getArguments().putString(key, value);
        return (This) this;
    }
    @SuppressWarnings("unchecked cast")
    protected This setArg(String key, int value){
        getArguments().putInt(key, value);
        return (This) this;
    }
    protected String getArgString(String key){
        Object value = getArguments().get(key);
        if (value instanceof String){
            return (String) value;
        } else if (value instanceof Integer){
            return getString((Integer) value);
        }
        return null;
    }

    public This title(String title){ return setArg(TITLE, title); }
    public This title(int titleResourceId){ return setArg(TITLE, titleResourceId); }
    public This msg(String message){ return setArg(MESSAGE, message); }
    public This msg(int messageResourceId){ return setArg(MESSAGE, messageResourceId); }
    public This pos(String positiveButton){ return setArg(POSITIVE_BUTTON_TEXT, positiveButton); }
    public This pos(int positiveButtonResourceId){ return setArg(POSITIVE_BUTTON_TEXT, positiveButtonResourceId); }
    public This pos(){ return pos(android.R.string.ok); }
    public This neg(String negativeButton){ return setArg(NEGATIVE_BUTTON_TEXT, negativeButton); }
    public This neg(int negativeButtonResourceId){ return setArg(NEGATIVE_BUTTON_TEXT, negativeButtonResourceId); }
    public This neg(){ return neg(android.R.string.no); }
    public This neut(String neutralButton){ return setArg(NEUTRAL_BUTTON_TEXT, neutralButton); }
    public This neut(int neutralButtonResourceId){ return setArg(NEUTRAL_BUTTON_TEXT, neutralButtonResourceId); }
    public This neut(){ return neut(android.R.string.cancel); }
    public This icon(int iconResourceId){ return setArg(ICON_RESOURCE, iconResourceId); }
    public This cancelable(boolean cancelable){ return setArg(CANCELABLE, cancelable); }
    @SuppressWarnings("unchecked cast")
    public This extra(Bundle extras){ getArguments().putBundle(BUNDLE, extras); return (This) this; }

    /**
     * Set a custom style. Default is using the "AlertDialogCustom"-Style if it exists
     * @param style the resource id of the custom style
     */
    public This style(int style){ return setArg(STYLE, style); }



    public void show(Fragment fragment){
        show(fragment, null);
    }
    public void show(Fragment fragment, String tag){
        setTargetFragment(fragment, -1);
        try {
            super.show(fragment.getFragmentManager(), tag);
        } catch (IllegalStateException ignored){}
    }

    public void show(AppCompatActivity activity){
        show(activity, null);
    }
    public void show(AppCompatActivity activity, String tag){
        try {
            super.show(activity.getSupportFragmentManager(), tag);
        } catch (IllegalStateException ignored){}
    }





    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retain this fragment across configuration changes.
        setRetainInstance(true);

        setCancelable(getArguments().getBoolean(CANCELABLE, true));

    }





    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int style = getArguments().getInt(STYLE, -1);
        if (style > -1){
            dialog = new AlertDialog.Builder(getActivity(), getArguments().getInt(STYLE)).create();
        } else {
            dialog = new AlertDialog.Builder(getActivity()).create();
        }
        dialog.setTitle(getArgString(TITLE));
        dialog.setMessage(getArgString(MESSAGE));
        String positiveButtonText = getArgString(POSITIVE_BUTTON_TEXT);
        if (positiveButtonText != null) {
            dialog.setButton(DialogInterface.BUTTON_POSITIVE,
                    positiveButtonText, forwardOnClickListener);
        }
        String negativeButtonText = getArgString(NEGATIVE_BUTTON_TEXT);
        if (negativeButtonText != null) {
            dialog.setButton(DialogInterface.BUTTON_NEGATIVE,
                    negativeButtonText, forwardOnClickListener);
        }
        String neutralButtonText = getArgString(NEUTRAL_BUTTON_TEXT);
        if (neutralButtonText != null) {
            dialog.setButton(DialogInterface.BUTTON_NEUTRAL,
                    neutralButtonText, forwardOnClickListener);
        }
        int iconResourceId = getArguments().getInt(ICON_RESOURCE);
        if (iconResourceId != 0) {
            dialog.setIcon(iconResourceId);
        }
        dialog.setCancelable(getArguments().getBoolean(CANCELABLE, true));

        return dialog;
    }




    @Deprecated /** use {@link #show(AppCompatActivity)} or {@link #show(Fragment)}**/
    @Override
    public void show(FragmentManager manager, String tag) {
        try {
            super.show(manager, tag);
        } catch (IllegalStateException ignored){}
    }

    @Deprecated /** use {@link #show(AppCompatActivity)} or {@link #show(Fragment)}**/
    @Override
    public int show(FragmentTransaction transaction, String tag) {
        try {
            return super.show(transaction, tag);
        } catch (IllegalStateException ignored){
            return -1;
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        callResultListener(OnDialogFragmentResultListener.CANCELED, null);
    }

    // This is to work around what is apparently a bug. If you don't have it
    // here the dialog will be dismissed on rotation, so tell it not to dismiss.
    @Override
    public void onDestroyView() {
        if (dialog != null && getRetainInstance()) {
            dialog.setDismissMessage(null);
        }
        super.onDestroyView();
    }
}
