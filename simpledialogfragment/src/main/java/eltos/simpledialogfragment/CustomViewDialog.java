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
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.CallSuper;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import android.text.Html;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;


/**
 * The base class for all custom dialogs.
 * Extend this class and implement/overwrite the methods to use your custom view
 * See {@link SimpleCheckDialog} as an example.
 *
 * Created by eltos on 11.10.2015.
 */

public abstract class CustomViewDialog<This extends CustomViewDialog<This>>
        extends SimpleDialog<This> {


    public static CustomViewDialog build(){
        throw new java.lang.InstantiationError("Unintended abuse of the builder method. " +
                "Have you created your own build() method in your custom dialog?");
    }

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
     * to be passed to the {@link SimpleDialog.OnDialogResultListener#onResult}
     *
     * @param which see {@link SimpleDialog.OnDialogResultListener}
     * @return the bundle to merge with the results or null
     */
    protected Bundle onResult(int which){
        return null;
    }



    /**
     * Call this method to enable or disable the positive button,
     * e.g. if you want to consider for preconditions to be fulfilled
     *
     * Note: call this in {@link CustomViewDialog#onDialogShown} rather than {@link CustomViewDialog#onCreateContentView}
	 * 
	 * @param enabled weather to en- or disable the button
     */
    protected final void setPositiveButtonEnabled(boolean enabled){
        if (positiveButton != null) {
            positiveButton.setEnabled(enabled);
        }
    }

    /**
     * Overwrite this method to catch positive button presses,
     * e.g. if you need to verify input by the user
     *
     * Note: do not call {@link CustomViewDialog#pressPositiveButton} here!
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
     * Note: do not call this method from {@link CustomViewDialog#acceptsPositiveButtonPress} !!!
     */
    protected final void pressPositiveButton(){
        if (acceptsPositiveButtonPress()) {
            getDialog().dismiss();
            callResultListener(DialogInterface.BUTTON_POSITIVE, null);
        }
    }

    /**
     * Method to inflate your custom View from {@link CustomViewDialog#onCreateContentView}
     *
     * @param resource The resource to be inflated
     * @return The inflated view
     */
    protected final View inflate(@LayoutRes int resource) {
        return inflate(resource, null, false);
    }

    /**
     * Method to inflate your custom View from {@link CustomViewDialog#onCreateContentView}. Throws
     * {@link InflateException} if there is an error.
     *
     * @param resource ID for an XML layout resource to load
     * @param root Optional view to be the parent of the generated hierarchy (if
     *        <em>attachToRoot</em> is true), or else simply an object that
     *        provides a set of LayoutParams values for root of the returned
     *        hierarchy (if <em>attachToRoot</em> is false.)
     * @param attachToRoot Whether the inflated hierarchy should be attached to
     *        the root parameter? If false, root is only used to create the
     *        correct subclass of LayoutParams for the root view in the XML.
     * @return The root View of the inflated hierarchy. If root was supplied and
     *         attachToRoot is true, this is root; otherwise it is the root of
     *         the inflated XML file.
     */
    protected final View inflate(@LayoutRes int resource, ViewGroup root, boolean attachToRoot) {
        return layoutInflater.inflate(resource, root, attachToRoot);
    }


    /**
     * Method to extract the underlying content view.
     *
     * @param savedInstanceState The saved instance state
     * @return the extracted view
     */
    public final View extractContentView(Bundle savedInstanceState){
        final AlertDialog dialog = (AlertDialog) super.onCreateDialog(savedInstanceState);
        layoutInflater = dialog.getLayoutInflater();
        return onCreateContentView(savedInstanceState);
    }


    //////////////////////////////////////////////////////////////////////////////////////////
    // Internal


    private Button positiveButton;
    private LayoutInflater layoutInflater;


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

        layoutInflater = dialog.getLayoutInflater();

        View content = onCreateContentView(savedInstanceState);

        // Intermediate view with custom message TextView
        View intermediate = inflate(R.layout.simpledialogfragment_custom_view);
        TextView textView = (TextView) intermediate.findViewById(R.id.customMessage);
        View topSpacer = intermediate.findViewById(R.id.textSpacerNoTitle);
        ViewGroup container = (ViewGroup) intermediate.findViewById(R.id.customView);
        container.addView(content);

        dialog.setView(intermediate);


        String msg = getMessage();
        if (msg != null) {
            CharSequence message;
            if (getArguments().getBoolean(HTML)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    message = Html.fromHtml(msg, 0);
                } else {
                    //noinspection deprecation
                    message = Html.fromHtml(msg);
                }
            } else {
                message = msg;
            }
            textView.setText(message);

        } else {
            textView.setVisibility(View.GONE);
        }
        dialog.setMessage(null);

        topSpacer.setVisibility(getTitle() == null && msg != null ? View.VISIBLE : View.GONE);


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
    @CallSuper
    protected boolean callResultListener(int which, Bundle extras) {
        Bundle results = onResult(which);
        if (extras == null) extras = new Bundle();
        if (results != null) extras.putAll(results);
        return super.callResultListener(which, extras);
    }


}




