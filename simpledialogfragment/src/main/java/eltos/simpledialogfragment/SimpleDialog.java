/*
 *  Copyright 2017 Philipp Niedermayer (github.com/eltos)
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

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.CallSuper;
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
import android.support.v7.view.ContextThemeWrapper;
import android.text.Html;

/**
 * An easy to use and extendable dialog fragment that displays a text message.
 * This is the base class of all dialogs in this library.
 *
 * Created by eltos on 03.08.2015.
 */

@SuppressWarnings("unused")
public class SimpleDialog<This extends SimpleDialog<This>> extends DialogFragment {

    static final String TITLE = "simpleDialog.title";
    static final String MESSAGE = "simpleDialog.message";
    static final String POSITIVE_BUTTON_TEXT = "simpleDialog.positiveButtonText";
    static final String NEGATIVE_BUTTON_TEXT = "simpleDialog.negativeButtonText";
    static final String NEUTRAL_BUTTON_TEXT = "simpleDialog.neutralButtonText";
    static final String ICON_RESOURCE = "simpleDialog.iconResource";
    static final String CANCELABLE = "simpleDialog.cancelable";
    static final String THEME = "simpleDialog.theme";
    static final String HTML = "simpleDialog.html";

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
         * @param dialogTag the tag passed to {@link #show}
         * @param which result type, one of {@link #BUTTON_POSITIVE}, {@link #BUTTON_NEGATIVE},
         *              {@link #BUTTON_NEUTRAL} or {@link #CANCELED}
         * @param extras the extras passed to {@link #extra(Bundle)}
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

    /**
     * Sets this dialogs title
     *
     * @param title the title as string
     */
    public This title(String title){ return setArg(TITLE, title); }

    /**
     * Sets this dialogs title
     *
     * @param titleResourceId the title as android string resource
     */
    public This title(@StringRes int titleResourceId){ return setArg(TITLE, titleResourceId); }

    /**
     * Sets this dialogs message
     *
     * @param message title as string
     */
    public This msg(String message){ return setArg(MESSAGE, message); }

    /**
     * Sets this dialogs message
     *
     * @param messageResourceId the message as android string resource
     */
    public This msg(@StringRes int messageResourceId){ return setArg(MESSAGE, messageResourceId); }

    /**
     * Sets this dialogs message as html styled string
     *
     * @param message title as html-string
     */
    public This msgHtml(String message){ setArg(HTML, true); return setArg(MESSAGE, message); }

    /**
     * Sets this dialogs message as html styled string
     *
     * @param messageResourceId the message as html-styled android string resource
     */
    public This msgHtml(@StringRes int messageResourceId){ setArg(HTML, true); return setArg(MESSAGE, messageResourceId); }

    /**
     * Sets this dialogs positive button text
     *
     * @param positiveButton the text as string
     */
    public This pos(String positiveButton){ return setArg(POSITIVE_BUTTON_TEXT, positiveButton); }

    /**
     * Sets this dialogs positive button text
     *
     * @param positiveButtonResourceId the text as android string resource
     */
    public This pos(@StringRes int positiveButtonResourceId){ return setArg(POSITIVE_BUTTON_TEXT, positiveButtonResourceId); }

    /**
     * Set this dialogs positive button text to {@link android.R.string#ok}
     * This is done by default
     */
//    public This pos(){ return pos(android.R.string.ok); }

    /**
     * Sets this dialogs negative button text
     *
     * @param negativeButton the text as string
     */
    public This neg(String negativeButton){ return setArg(NEGATIVE_BUTTON_TEXT, negativeButton); }

    /**
     * Sets this dialogs negative button text
     *
     * @param negativeButtonResourceId the text as android string resource
     */
    public This neg(@StringRes int negativeButtonResourceId){ return setArg(NEGATIVE_BUTTON_TEXT, negativeButtonResourceId); }

    /**
     * Sets this dialogs negative button text to {@link android.R.string#no}
     */
    public This neg(){ return neg(android.R.string.no); }

    /**
     * Sets this dialogs neutral button text
     *
     * @param neutralButton the text as string
     */
    public This neut(String neutralButton){ return setArg(NEUTRAL_BUTTON_TEXT, neutralButton); }

    /**
     * Sets this dialogs neutral button text
     *
     * @param neutralButtonResourceId the text as android string resource
     */
    public This neut(@StringRes int neutralButtonResourceId){ return setArg(NEUTRAL_BUTTON_TEXT, neutralButtonResourceId); }

    /**
     * Sets this dialogs neutral button text to {@link android.R.string#cancel}
     */
    public This neut(){ return neut(android.R.string.cancel); }

    /**
     * Sets this dialogs icon
     *
     * @param iconResourceId the icon as android drawable resource
     */
    public This icon(@DrawableRes int iconResourceId){ return setArg(ICON_RESOURCE, iconResourceId); }

    /**
     * Specifies weather this dialog may be canceled by pressing the back button or
     * touching outside of the dialog.
     * The dialog may still be "canceled" by a neutral button.
     *
     * @param cancelable weather this dialog may be canceled
     */
    public This cancelable(boolean cancelable){ return setArg(CANCELABLE, cancelable); }

    /**
     * Pass extras to the dialog to retain specific information across configuration changes.
     * All extras supplied here will be contained in the extras bundle passed to
     * {@link OnDialogResultListener#onResult}
     *
     * @param extras a bundle of extras to store
     */
    @SuppressWarnings("unchecked cast")
    public This extra(Bundle extras){ getArguments().putBundle(BUNDLE, extras); return (This) this; }

    /**
     * Set a custom theme. Default is using the theme defined by the 'alertDialogTheme'-attribute.
     *
     * @param theme the android style resource id of the custom theme
     */
    public This theme(@StyleRes int theme){ return setArg(THEME, theme); }


    /**
     * Shows the dialog. The {@link OnDialogResultListener} won't be called.
     *
     * @param fragment the hosting fragment
     */
    public void show(Fragment fragment){
        show(fragment, null);
    }

    /**
     * Shows the dialog. Results will be forwarded to the fragment supplied.
     * The tag can be used to identify the dialog in {@link OnDialogResultListener#onResult}
     *
     * @param fragment the hosting fragment
     * @param tag the dialogs tag
     */
    public void show(Fragment fragment, String tag){
        setTargetFragment(fragment, -1);
        try {
            super.show(fragment.getFragmentManager(), tag);
        } catch (IllegalStateException ignored){}
    }

    /**
     * Shows the dialog. The {@link OnDialogResultListener} won't be called.
     *
     * @param activity the hosting activity
     */
    public void show(AppCompatActivity activity){
        show(activity, null);
    }

    /**
     * Shows the dialog. Results will be forwarded to the activity supplied.
     * The tag can be used to identify the dialog in {@link OnDialogResultListener#onResult}
     *
     * @param activity the hosting activity
     * @param tag the dialogs tag
     */
    public void show(AppCompatActivity activity, String tag){
        try {
            super.show(activity.getSupportFragmentManager(), tag);
        } catch (IllegalStateException ignored){}
    }





    @Override
    @CallSuper
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retain this fragment across configuration changes.
        setRetainInstance(true);

        setCancelable(getArguments().getBoolean(CANCELABLE, true));

    }


    private Context context;
    @Override
    public Context getContext() {
        return context != null ? context : super.getContext();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        if (getArguments().containsKey(THEME)){
            dialog = new AlertDialog.Builder(getContext(), getArguments().getInt(THEME)).create();
            setStyle(STYLE_NORMAL, getArguments().getInt(THEME));
        } else {
            // default theme or 'alertDialogTheme'
            dialog = new AlertDialog.Builder(getContext()).create();
        }

        context = dialog.getContext();

        dialog.setTitle(getArgString(TITLE));
        String msg = getArgString(MESSAGE);
        if (msg != null) {
            if (getArguments().getBoolean(HTML)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    dialog.setMessage(Html.fromHtml(msg, 0));
                } else {
                    //noinspection deprecation
                    dialog.setMessage(Html.fromHtml(msg));
                }
            } else {
                dialog.setMessage(msg);
            }
        }
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



    /**
     * Deprecated,
     * use {@link #show(AppCompatActivity)} or {@link #show(Fragment)} instead
     */
    @Deprecated
    @Override
    public void show(FragmentManager manager, String tag) {
        try {
            super.show(manager, tag);
        } catch (IllegalStateException ignored){}
    }

    /**
     * Deprecated,
     * use {@link #show(AppCompatActivity, String)} or {@link #show(Fragment, String)} instead
     */
    @Deprecated
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
