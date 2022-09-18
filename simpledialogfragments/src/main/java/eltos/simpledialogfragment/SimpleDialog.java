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

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.CallSuper;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.annotation.StyleRes;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AlertDialog;
import android.text.Html;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

/**
 * An easy to use and extendable dialog fragment that displays a text message.
 * This is the base class of all dialogs in this library.
 *
 * Created by eltos on 03.08.2015.
 */

@SuppressWarnings("unused")
public class SimpleDialog<This extends SimpleDialog<This>> extends DialogFragment {

    public static final String TAG = "SimpleDialog.";


    public static SimpleDialog build(){
        return new SimpleDialog();
    }




    protected static final String TITLE = TAG + "title",
            MESSAGE = TAG + "message",
            POSITIVE_BUTTON_TEXT = TAG + "positiveButtonText",
            NEGATIVE_BUTTON_TEXT = TAG + "negativeButtonText",
            NEUTRAL_BUTTON_TEXT = TAG + "neutralButtonText",
            ICON_RESOURCE = TAG + "iconResource",
            CANCELABLE = TAG + "cancelable",
            THEME = TAG + "theme",
            HTML = TAG + "html",
            BUNDLE = TAG + "bundle";

    public interface OnDialogResultListener {
        int CANCELED = 0;
        int BUTTON_POSITIVE = DialogInterface.BUTTON_POSITIVE;
        int BUTTON_NEGATIVE = DialogInterface.BUTTON_NEGATIVE;
        int BUTTON_NEUTRAL = DialogInterface.BUTTON_NEUTRAL;

        /**
         * Let the hosting fragment or activity implement this interface
         * to receive results from the dialog
         *
         * @param dialogTag the tag passed to {@link SimpleDialog#show}
         * @param which result type, one of {@link #BUTTON_POSITIVE}, {@link #BUTTON_NEGATIVE},
         *              {@link #BUTTON_NEUTRAL} or {@link #CANCELED}
         * @param extras the extras passed to {@link SimpleDialog#extra(Bundle)}
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

    @CallSuper
    protected boolean callResultListener(int which, @Nullable Bundle extras) {
        if (extras == null) extras = new Bundle();
        extras.putAll(getExtras());
        boolean handled = false;
        if (getTag() != null) {
            Fragment resultFragment = getTargetFragment();
            while (!handled && resultFragment != null){
                if (resultFragment instanceof OnDialogResultListener){
                    handled = ((OnDialogResultListener) getTargetFragment())
                            .onResult(getTag(), which, extras);
                }
                resultFragment = resultFragment.getParentFragment();
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
        getArgs(); // init arguments to never equal null
        // positive button default
        pos(android.R.string.ok);
    }

    @SuppressWarnings("unchecked cast")
    protected final This setArg(String key, boolean value){
        getArgs().putBoolean(key, value);
        return (This) this;
    }
    @SuppressWarnings("unchecked cast")
    protected final This setArg(String key, CharSequence value){
        getArgs().putCharSequence(key, value);
        return (This) this;
    }
    @SuppressWarnings("unchecked cast")
    protected final This setArg(String key, int value){
        getArgs().putInt(key, value);
        return (This) this;
    }
    @SuppressWarnings("unchecked cast")
    protected final This setArg(String key, long value){
        getArgs().putLong(key, value);
        return (This) this;
    }
    @Nullable
    protected final CharSequence getArgString(String key){
        Object value = getArgs().get(key);
        if (value instanceof CharSequence){
            return (CharSequence) value;
        } else if (value instanceof Integer){
            return getString((Integer) value);
        }
        return null;
    }

    /**
     * null-save method to get arguments
     * @return dialog arguments bundle
     */
    @NonNull
    protected final Bundle getArgs(){
        Bundle args = getArguments();
        if (args == null){
            args = new Bundle();
            setArguments(args);
        }
        return args;
    }

    /**
     * Sets this dialogs title
     *
     * @param title the title as string
     * @return this instance
     */
    public This title(CharSequence title){ return setArg(TITLE, title); }

    /**
     * Sets this dialogs title
     *
     * @param titleResourceId the title as android string resource
     * @return this instance
     */
    public This title(@StringRes int titleResourceId){ return setArg(TITLE, titleResourceId); }

    /**
     * Gets the string representation of the title set
     * @return the dialog title
     */
    public @Nullable CharSequence getTitle(){
        return getArgString(TITLE);
    }

    /**
     * Sets this dialogs message
     *
     * @param message title as string
     * @return this instance
     */
    public This msg(CharSequence message){ setArg(HTML, false); return setArg(MESSAGE, message); }

    /**
     * Sets this dialogs message
     *
     * @param messageResourceId the message as android string resource
     * @return this instance
     */
    public This msg(@StringRes int messageResourceId){ setArg(HTML, false); return setArg(MESSAGE, messageResourceId); }

    /**
     * Sets this dialogs message as html styled string
     *
     * @param message title as html-string
     * @return this instance
     */
    public This msgHtml(String message){ setArg(HTML, true); return setArg(MESSAGE, message); }

    /**
     * Sets this dialogs message as html styled string
     *
     * @param messageResourceId the message as html-styled android string resource
     * @return this instance
     */
    public This msgHtml(@StringRes int messageResourceId){ setArg(HTML, true); return setArg(MESSAGE, messageResourceId); }

    /**
     * Gets the string representation of the message set
     * @return the dialog message
     */
    public @Nullable CharSequence getMessage(){
        return getArgString(MESSAGE);
    }

    /**
     * Sets this dialogs positive button text
     *
     * @param positiveButton the text as string
     * @return this instance
     */
    public This pos(CharSequence positiveButton){ return setArg(POSITIVE_BUTTON_TEXT, positiveButton); }

    /**
     * Sets this dialogs positive button text
     *
     * @param positiveButtonResourceId the text as android string resource
     * @return this instance
     */
    public This pos(@StringRes int positiveButtonResourceId){ return setArg(POSITIVE_BUTTON_TEXT, positiveButtonResourceId); }

    /**
     * Sets this dialogs negative button text
     *
     * @param negativeButton the text as string
     * @return this instance
     */
    public This neg(CharSequence negativeButton){ return setArg(NEGATIVE_BUTTON_TEXT, negativeButton); }

    /**
     * Sets this dialogs negative button text
     *
     * @param negativeButtonResourceId the text as android string resource
     * @return this instance
     */
    public This neg(@StringRes int negativeButtonResourceId){ return setArg(NEGATIVE_BUTTON_TEXT, negativeButtonResourceId); }

    /**
     * Sets this dialogs negative button text to {@link android.R.string#no}
     *
     * @return this instance
     */
    public This neg(){ return neg(android.R.string.no); }

    /**
     * Sets this dialogs neutral button text
     *
     * @param neutralButton the text as string
     * @return this instance
     */
    public This neut(CharSequence neutralButton){ return setArg(NEUTRAL_BUTTON_TEXT, neutralButton); }

    /**
     * Sets this dialogs neutral button text
     *
     * @param neutralButtonResourceId the text as android string resource
     * @return this instance
     */
    public This neut(@StringRes int neutralButtonResourceId){ return setArg(NEUTRAL_BUTTON_TEXT, neutralButtonResourceId); }

    /**
     * Sets this dialogs neutral button text to {@link android.R.string#cancel}
     *
     * @return this instance
     */
    public This neut(){ return neut(android.R.string.cancel); }

    /**
     * Sets this dialogs icon
     *
     * @param iconResourceId the icon as android drawable resource
     * @return this instance
     */
    public This icon(@DrawableRes int iconResourceId){ return setArg(ICON_RESOURCE, iconResourceId); }

    /**
     * Specifies whether this dialog may be canceled by pressing the back button or
     * touching outside of the dialog.
     * The dialog may still be "canceled" by a neutral button.
     *
     * @param cancelable whether this dialog may be canceled
     * @return this instance
     */
    public This cancelable(boolean cancelable){ return setArg(CANCELABLE, cancelable); }

    /**
     * Return whether the dialog was set to be cancelable or not
     *
     * @return whether the dialog is cancelable
     */
    public boolean isCancelable(){
        return getArgs().getBoolean(CANCELABLE, true);
    }

    /**
     * Pass extras to the dialog to retain specific information across configuration changes.
     * All extras supplied here will be contained in the extras bundle passed to
     * {@link OnDialogResultListener#onResult}
     *
     * @param extras a bundle of extras to store
     * @return this instance
     */
    @SuppressWarnings("unchecked cast")
    public This extra(Bundle extras){ getArgs().putBundle(BUNDLE, extras); return (This) this; }

    /**
     * Gets the extras bundle provided
     *
     * @return the extras bundle (which may be empty)
     */
    public @NonNull Bundle getExtras(){
        Bundle bundle = getArgs().getBundle(BUNDLE);
        return bundle != null ? bundle : new Bundle();
    }

    /**
     * Set a custom theme.
     * Default is using the theme defined by the 'simpleDialogTheme'-attribute
     * or the 'alertDialogTheme'-attribute.
     *
     * @param theme the android style resource id of the custom theme
     * @return this instance
     */
    public This theme(@StyleRes int theme){ return setArg(THEME, theme); }


    /**
     * Shows the dialog. The {@link OnDialogResultListener} won't be called.
     *
     * @param fragment the hosting fragment
     */
    public void show(Fragment fragment){
        // Default tag is given by static field TAG
        String tag = null;
        try {
            tag = (String) this.getClass().getField("TAG").get(null);
        } catch (Exception ignored) { }
        show(fragment, tag);
    }

    /**
     * Shows the dialog. Results will be forwarded to the fragment supplied.
     * The tag can be used to identify the dialog in {@link OnDialogResultListener#onResult}
     *
     * @param fragment the hosting fragment
     * @param tag the dialogs tag
     */
    public void show(Fragment fragment, String tag){
        show(fragment, tag, null);
    }

    /**
     * Shows the dialog. Results will be forwarded to the fragment supplied.
     * The tag can be used to identify the dialog in {@link OnDialogResultListener#onResult}
     * An optional argument can be used to remove a previously shown dialog with the tag given
     * prior to showing this one.
     *
     * @param fragment the hosting fragment
     * @param tag the dialogs tag
     * @param replaceTag removes the dialog with the given tag if specified
     */
    public void show(Fragment fragment, String tag, String replaceTag){
        FragmentManager manager = fragment.getFragmentManager();
        if (manager != null) {
            Fragment existing = manager.findFragmentByTag(replaceTag);
            if (existing != null){
                FragmentManager otherManager = existing.getFragmentManager();
                if (otherManager != null) {
                    FragmentTransaction ft = otherManager.beginTransaction();
                    ft.remove(existing);
                    ft.commit();
                }
            }
            setTargetFragment(fragment, -1);
            try {
                super.show(manager, tag);
            } catch (IllegalStateException ignored) {
            }
        }
    }

    /**
     * Shows the dialog. The {@link OnDialogResultListener} won't be called.
     *
     * @param activity the hosting activity
     */
    public void show(FragmentActivity activity){
        // Default tag is given by static field TAG
        String tag = null;
        try {
            tag = (String) this.getClass().getField("TAG").get(null);
        } catch (Exception ignored) { }
        show(activity, tag);
    }

    /**
     * Shows the dialog. Results will be forwarded to the activity supplied.
     * The tag can be used to identify the dialog in {@link OnDialogResultListener#onResult}
     *
     * @param activity the hosting activity
     * @param tag the dialogs tag
     */
    public void show(FragmentActivity activity, String tag){
        show(activity, tag, null);
    }

    /**
     * Shows the dialog. Results will be forwarded to the activity supplied.
     * The tag can be used to identify the dialog in {@link OnDialogResultListener#onResult}
     * An optional argument can be used to remove a previously shown dialog with the tag given
     * prior to showing this one.
     *
     * @param activity the hosting activity
     * @param tag the dialogs tag
     * @param replaceTag removes the dialog with the given tag if specified
     */
    public void show(FragmentActivity activity, String tag, String replaceTag){
        FragmentManager manager = activity.getSupportFragmentManager();
        Fragment existing = manager.findFragmentByTag(replaceTag);
        if (existing != null) {
            FragmentManager otherManager = existing.getFragmentManager();
            if (otherManager != null) {
                FragmentTransaction ft = otherManager.beginTransaction();
                ft.remove(existing);
                ft.commit();
            }
        }
        try {
            super.show(manager, tag);
        } catch (IllegalStateException ignored) {
        }
    }





    @Override
    @CallSuper
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retain this fragment across configuration changes.
        setRetainInstance(true);

        setCancelable(isCancelable());

    }


    private Context context;
    @Override
    public Context getContext() {
        return context != null ? context : super.getContext();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // dialog theme
        @StyleRes Integer theme = null;
        if (getArgs().containsKey(THEME)){  // per-dialog theme
            theme = getArgs().getInt(THEME);
        } else {  // theme specified by 'simpleDialogTheme' attribute
            TypedValue outValue = new TypedValue();
            getContext().getTheme().resolveAttribute(R.attr.simpleDialogTheme, outValue, true);
            if (outValue.type == TypedValue.TYPE_REFERENCE) {
                theme = outValue.resourceId;
            }
        }
        if (theme != null) {
            dialog = new AlertDialog.Builder(getContext(), theme).create();
            setStyle(STYLE_NORMAL, theme);
        } else {
            // default theme or 'alertDialogTheme'
            dialog = new AlertDialog.Builder(getContext()).create();
        }

        context = dialog.getContext();

        dialog.setTitle(getTitle());
        CharSequence msg = getMessage();
        if (msg != null) {
            if (getArgs().getBoolean(HTML) && msg instanceof String) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    dialog.setMessage(Html.fromHtml((String) msg, 0));
                } else {
                    //noinspection deprecation
                    dialog.setMessage(Html.fromHtml((String) msg));
                }
            } else {
                dialog.setMessage(msg);
            }
        }
        CharSequence positiveButtonText = getArgString(POSITIVE_BUTTON_TEXT);
        if (positiveButtonText != null) {
            dialog.setButton(DialogInterface.BUTTON_POSITIVE,
                    positiveButtonText, forwardOnClickListener);
        }
        CharSequence negativeButtonText = getArgString(NEGATIVE_BUTTON_TEXT);
        if (negativeButtonText != null) {
            dialog.setButton(DialogInterface.BUTTON_NEGATIVE,
                    negativeButtonText, forwardOnClickListener);
        }
        CharSequence neutralButtonText = getArgString(NEUTRAL_BUTTON_TEXT);
        if (neutralButtonText != null) {
            dialog.setButton(DialogInterface.BUTTON_NEUTRAL,
                    neutralButtonText, forwardOnClickListener);
        }
        if (getArgs().containsKey(ICON_RESOURCE)){
            dialog.setIcon(getArgs().getInt(ICON_RESOURCE));
        }
        dialog.setCancelable(isCancelable());

        return dialog;
    }

    protected @Nullable Button getPositiveButton(){
        return dialog == null ? null : dialog.getButton(DialogInterface.BUTTON_POSITIVE);
    }

    protected @Nullable Button getNegativeButton(){
        return dialog == null ? null : dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
    }

    protected @Nullable Button getNeutralButton(){
        return dialog == null ? null : dialog.getButton(DialogInterface.BUTTON_NEUTRAL);
    }

    /**
     * Helper for opening the soft keyboard on a specified view
     * @param view the view to be focused and receive keyboard input
     */
    public void showKeyboard(final View view){
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                view.requestFocus();
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
                }
            }
        }, 100);
    }



    /**
     * Deprecated, use {@link SimpleDialog#show(FragmentActivity)} or
     * {@link SimpleDialog#show(Fragment)} instead
     */
    @Deprecated
    @Override
    public void show(FragmentManager manager, String tag) {
        try {
            super.show(manager, tag);
        } catch (IllegalStateException ignored){}
    }

    /**
     * Deprecated, use {@link SimpleDialog#show(FragmentActivity, String)} or
     * {@link SimpleDialog#show(Fragment, String)} instead
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
    @CallSuper
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        callResultListener(OnDialogResultListener.CANCELED, null);
    }

    // This is to work around what is apparently a bug. If you don't have it
    // here the dialog will be dismissed on rotation, so tell it not to dismiss.
    @Override
    @CallSuper
    public void onDestroyView() {
        if (dialog != null && getRetainInstance()) {
            dialog.setDismissMessage(null);
        }
        super.onDestroyView();
    }
}
