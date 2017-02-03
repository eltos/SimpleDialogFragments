package eltos.simpledialogfragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.annotation.StyleRes;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;

/**
 * An easy to use and extendable dialog fragment that displays a text message.
 *
 * Created by eltos on 03.08.2015.
 */
@SuppressWarnings("unused")
public class SimpleDialog<This extends SimpleDialog<This>> extends DialogFragment {

    private static final String TITLE = "simpleDialog.title";
    private static final String MESSAGE = "simpleDialog.message";
    private static final String POSITIVE_BUTTON_TEXT = "simpleDialog.positiveButtonText";
    private static final String NEGATIVE_BUTTON_TEXT = "simpleDialog.negativeButtonText";
    private static final String NEUTRAL_BUTTON_TEXT = "simpleDialog.neutralButtonText";
    private static final String ICON_RESOURCE = "simpleDialog.iconResource";
    private static final String CANCELABLE = "simpleDialog.cancelable";
    private static final String THEME = "simpleDialog.theme";

    protected static final String BUNDLE = "simpleDialog.bundle";


    public interface OnDialogResultListener {
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
        boolean onResult(@NonNull String dialogTag, int which, @NonNull Bundle extras);
    }

    private DialogInterface.OnClickListener forwardOnClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            callResultListener(which, null);
        }
    };

    boolean callResultListener(int which, Bundle extras) {
        if (extras == null) extras = new Bundle();
        if (getArguments().getBundle(BUNDLE) != null) {
            extras.putAll(getArguments().getBundle(BUNDLE));
        }
        boolean handled = false;
        if (getTag() != null) {
            if (getTargetFragment() instanceof OnDialogResultListener) {
                handled = ((OnDialogResultListener) getTargetFragment())
                        .onResult(getTag(), which, extras);
            }
            if (!handled && getActivity() instanceof OnDialogResultListener) {
                handled = ((OnDialogResultListener) getActivity())
                        .onResult(getTag(), which, extras);
            }
        }
        return handled;
    }



    private AlertDialog dialog;

    public SimpleDialog(){
        Bundle args = getArguments();
        if (args == null) args = new Bundle();
        setArguments(args);
        // positive button default
        pos(android.R.string.ok);
    }

    public static SimpleDialog build(){
        return new SimpleDialog();
    }

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
    @SuppressWarnings("unchecked cast")
    protected This setArg(String key, long value){
        getArguments().putLong(key, value);
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
    public This title(@StringRes int titleResourceId){ return setArg(TITLE, titleResourceId); }
    public This msg(String message){ return setArg(MESSAGE, message); }
    public This msg(@StringRes int messageResourceId){ return setArg(MESSAGE, messageResourceId); }
    public This pos(String positiveButton){ return setArg(POSITIVE_BUTTON_TEXT, positiveButton); }
    public This pos(@StringRes int positiveButtonResourceId){ return setArg(POSITIVE_BUTTON_TEXT, positiveButtonResourceId); }
//    public This pos(){ return pos(android.R.string.ok); }
    public This neg(String negativeButton){ return setArg(NEGATIVE_BUTTON_TEXT, negativeButton); }
    public This neg(@StringRes int negativeButtonResourceId){ return setArg(NEGATIVE_BUTTON_TEXT, negativeButtonResourceId); }
    public This neg(){ return neg(android.R.string.no); }
    public This neut(String neutralButton){ return setArg(NEUTRAL_BUTTON_TEXT, neutralButton); }
    public This neut(@StringRes int neutralButtonResourceId){ return setArg(NEUTRAL_BUTTON_TEXT, neutralButtonResourceId); }
    public This neut(){ return neut(android.R.string.cancel); }
    public This icon(@DrawableRes int iconResourceId){ return setArg(ICON_RESOURCE, iconResourceId); }
    public This cancelable(boolean cancelable){ return setArg(CANCELABLE, cancelable); }
    @SuppressWarnings("unchecked cast")
    public This extra(Bundle extras){ getArguments().putBundle(BUNDLE, extras); return (This) this; }

    /**
     * Set a custom theme. Default is using the theme
     * defined by the 'alertDialogTheme'-attribute.
     * @param theme the resource id of the custom theme
     */
    public This theme(@StyleRes int theme){ return setArg(THEME, theme); }



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

        if (getArguments().containsKey(THEME)){
            dialog = new AlertDialog.Builder(getActivity(), getArguments().getInt(THEME)).create();
        } else {
            // default theme or 'alertDialogTheme'
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
        if (getArguments().containsKey(ICON_RESOURCE)){
            dialog.setIcon(getArguments().getInt(ICON_RESOURCE));
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
        callResultListener(OnDialogResultListener.CANCELED, null);
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
