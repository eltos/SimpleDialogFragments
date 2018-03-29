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

package eltos.simpledialogfragment.form;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import java.util.ArrayList;
import java.util.Collections;

import eltos.simpledialogfragment.CustomViewDialog;
import eltos.simpledialogfragment.R;

/**
 * A form dialog to display a number of input fields to the user, such as
 * - Input fields ({@link Input})
 * - Check-boxes ({@link Check})
 * - Dropdown-menus ({@link Spinner})
 *
 * Created by eltos on 20.02.17.
 */

@SuppressWarnings({"WeakerAccess", "unused"})
public class SimpleFormDialog extends CustomViewDialog<SimpleFormDialog> {

    public static final String TAG = "SimpleFormDialog.";


    public static SimpleFormDialog build(){
        return new SimpleFormDialog();
    }


    /**
     * Convenient method to build a form dialog with a single email input
     *
     * @param emailFieldKey the key that can be used to receive the entered text from the bundle in
     *                      {@link eltos.simpledialogfragment.SimpleDialog.OnDialogResultListener#onResult}
     */
    public static SimpleFormDialog buildEmailInput(String emailFieldKey){
        return SimpleFormDialog.build()
                .fields(
                        Input.email(emailFieldKey).required()
                );
    }

    /**
     * Convenient method to build a form dialog with a single password input
     *
     * @param passwordFieldKey the key that can be used to receive the entered text from the bundle
     *                         in {@link eltos.simpledialogfragment.SimpleDialog.OnDialogResultListener#onResult}
     */
    public static SimpleFormDialog buildPasswordInput(String passwordFieldKey){
        return SimpleFormDialog.build()
                .fields(
                        Input.password(passwordFieldKey).required()
                );
    }

    /**
     * Convenient method to build a form dialog with a single pin code input
     *
     * @param pinFieldKey the key that can be used to receive the entered text from the bundle
     *                         in {@link eltos.simpledialogfragment.SimpleDialog.OnDialogResultListener#onResult}
     */
    public static SimpleFormDialog buildPinCodeInput(String pinFieldKey){
        return SimpleFormDialog.build()
                .fields(
                        Input.pin(pinFieldKey).required()
                );
    }

    /**
     * Convenient method to build a form dialog with a single pin code input
     *
     * @param pinFieldKey the key that can be used to receive the entered text from the bundle
     *                         in {@link eltos.simpledialogfragment.SimpleDialog.OnDialogResultListener#onResult}
     * @param digits the length of the pin code
     */
    public static SimpleFormDialog buildPinCodeInput(String pinFieldKey, int digits){
        return SimpleFormDialog.build()
                .fields(
                        Input.pin(pinFieldKey).required().min(digits).max(digits)
                );
    }

    /**
     * Convenient method to build a form dialog with a single number input
     *
     * @param numberFieldKey the key that can be used to receive the entered text from the bundle
     *                       in {@link eltos.simpledialogfragment.SimpleDialog.OnDialogResultListener#onResult}
     */
    public static SimpleFormDialog buildNumberInput(String numberFieldKey){
        return SimpleFormDialog.build()
                .fields(
                        Input.phone(numberFieldKey).required()
                );
    }

    /**
     * Convenient method to build a form dialog with an email input alongside
     * a password input for login with email address and password
     *
     * @param emailFieldKey the key that can be used to receive the entered email from the bundle
     *                      in {@link eltos.simpledialogfragment.SimpleDialog.OnDialogResultListener#onResult}
     * @param passwordFieldKey the key that can be used to receive the entered password from the
     *                         bundle in {@link eltos.simpledialogfragment.SimpleDialog.OnDialogResultListener#onResult}
     */
    public static SimpleFormDialog buildLoginEmail(String emailFieldKey, String passwordFieldKey){
        return SimpleFormDialog.build()
                .title(R.string.login)
                .pos(R.string.login)
                .fields(
                        Input.email(emailFieldKey).required(),
                        Input.password(passwordFieldKey).required()
                );
    }

    /**
     * Convenient method to build a form dialog with a plain input alongside
     * a password input for login with username and password
     *
     * @param userFieldKey the key that can be used to receive the entered username from the bundle
     *                     in {@link eltos.simpledialogfragment.SimpleDialog.OnDialogResultListener#onResult}
     * @param passwordFieldKey the key that can be used to receive the entered password from the
     *                         bundle in {@link eltos.simpledialogfragment.SimpleDialog.OnDialogResultListener#onResult}
     */
    public static SimpleFormDialog buildLogin(String userFieldKey, String passwordFieldKey){
        return SimpleFormDialog.build()
                .title(R.string.login)
                .pos(R.string.login)
                .fields(
                        Input.plain(userFieldKey).hint(R.string.user).required(),
                        Input.password(passwordFieldKey).required()
                );
    }



    /**
     * Convenient method to populate the form with form elements
     *
     * @param elements the {@link FormElement}s that form should contain
     */
    public SimpleFormDialog fields(FormElement... elements){
        ArrayList<FormElement> list = new ArrayList<>(elements.length);
        Collections.addAll(list, elements);
        getArguments().putParcelableArrayList(INPUT_FIELDS, list);
        return this;
    }



    public interface InputValidator {
        /**
         * Let the hosting fragment or activity implement this interface to make
         * custom validations for {@link Input} fields.
         * You may also use {@link Input#validatePattern} with a custom or predefined
         * pattern.
         * The method is called every time the user hits the positive button or next key.
         *
         * @param dialogTag the tag of this fragment
         * @param fieldKey the key of the field as supplied when the corresponding
         *                 {@link Input} was created (see {@link Input#plain(String)} etc)
         * @param input the text entered by the user
         * @param extras the extras passed with {@link SimpleFormDialog#extra(Bundle)}
         *
         * @return the error message to display or null if the input is valid
         */
        String validate(String dialogTag, String fieldKey, @Nullable String input, @NonNull Bundle extras);
    }








    ///////////////////////////////////////////////////////////////////////////////////////////

    protected static final String INPUT_FIELDS = TAG + "inputFields";
    protected static final String SAVE_TAG = "form.";

    private FocusActions mFocusActions = new FocusActions();
    ArrayList<FormElementViewHolder<?>> mViews = new ArrayList<>(0);
    ViewGroup mFormContainer;



    protected String onValidateInput(String fieldKey, @Nullable String input){
        Bundle extras = getArguments().getBundle(BUNDLE);
        if (extras == null) extras = new Bundle();
        if (getTargetFragment() instanceof InputValidator) {
            return ((InputValidator) getTargetFragment())
                    .validate(getTag(), fieldKey, input, extras);
        }
        if (getActivity() instanceof InputValidator) {
            return ((InputValidator) getActivity())
                    .validate(getTag(), fieldKey, input, extras);
        }
        return null;
    }




    @Override
    protected void onDialogShown() {
        // resize dialog when keyboard is shown to prevent fields from hiding behind the keyboard
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }

        setPositiveButtonEnabled(posButtonEnabled());
        requestFocus(0);
    }


    @Override
    protected boolean acceptsPositiveButtonPress() {
        boolean okay = true;
        for (FormElementViewHolder holder : mViews){
            if (!holder.validate(getContext())){
                if (okay) holder.focus(mFocusActions); // focus first element that is not valid
                okay = false;
            } else if (holder instanceof InputViewHolder){
                // custom validation
                String error = onValidateInput(holder.field.resultKey, ((InputViewHolder) holder).getText());
                if (error != null){
                    ((InputViewHolder) holder).setError(true, error);
                    if (okay) holder.focus(mFocusActions); // focus first element that is not valid
                    okay = false;
                }
            }
        }
        return okay;
    }


    protected boolean posButtonEnabled() {
        return mViews.size() != 1 || mViews.get(0).posButtonEnabled(getContext());
    }


    protected void requestFocus(int viewIndex){
        if (viewIndex < mViews.size() && viewIndex >= 0) {
            mViews.get(viewIndex).focus(mFocusActions);
        }
    }




    /**
     * A Callback Class with useful methods used by {@link FormElementViewHolder#focus}
     */
    public class FocusActions {
        /**
         * Helper to hide the soft keyboard
         */
        public void hideKeyboard(){
            View view = getDialog().getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }

        /**
         * Helper to clear the current focus
         */
        public void clearCurrentFocus(){
            mFormContainer.requestFocus();
        }


    }


    /**
     * A Callback Class with useful methods used by {@link FormElementViewHolder#setUpView}
     */
    public class DialogActions {

        private int index;
        private int lastIndex;

        private DialogActions(int index, int lastIndex){
            this.index = index;
            this.lastIndex = lastIndex;
        }

        /**
         * Helper to request an update of the positive button state
         */
        public void updatePosButtonState(){
            setPositiveButtonEnabled(posButtonEnabled());
        }

        /**
         * Helper to move the focus to the next element or to simulate a positive button
         * press if this is the last element
         */
        public void continueWithNextElement(){
            if (index == lastIndex){
                pressPositiveButton();
            } else {
                requestFocus(index + 1);
            }
        }

    }


    /**
     * Method for view creation. Inflates the layout and calls
     * {@link SimpleFormDialog#populateContainer(ViewGroup, Bundle)}
     * to populate the container with the fields specified
     *
     * @param savedInstanceState The last saved instance state of the Fragment,
     * or null if this fragment is created for the first time.
     *
     * @return inflated view
     */
    @Override
    public View onCreateContentView(Bundle savedInstanceState) {

        // inflate custom view
        View view = inflate(R.layout.simpledialogfragment_form);
        ViewGroup container = (ViewGroup) view.findViewById(R.id.container);

        populateContainer(container, savedInstanceState);

        return view;
    }

    /**
     * Creates FormElements and adds them to the container
     *
     * @param container the container to hold the FormElements
     * @param savedInstanceState saved state
     */
    protected void populateContainer(@NonNull ViewGroup container,
                                     @Nullable Bundle savedInstanceState) {
        mFormContainer = container;

        ArrayList<FormElement> fields = getArguments().getParcelableArrayList(INPUT_FIELDS);

        if (fields != null) {

            mViews = new ArrayList<>(fields.size());

            int lastI = fields.size() - 1;
            for (int i = 0; i <= lastI; i++) {

                FormElementViewHolder<?> viewHolder = fields.get(i).getViewHolder();

                View child = inflate(viewHolder.getContentViewLayout(), mFormContainer, false);

                Bundle savedState = savedInstanceState == null ? null :
                        savedInstanceState.getBundle(SAVE_TAG + i);

                viewHolder.setUpView(child, getContext(), savedState,
                        new DialogActions(i, lastI), i == lastI, lastI == 0);

                mFormContainer.addView(child);
                mViews.add(viewHolder);

            }

        }
    }


    @Override
    public Bundle onResult(int which) {
        Bundle result = new Bundle();
        for (FormElementViewHolder holder : mViews) {
            holder.putResults(result, holder.field.resultKey);
        }
        return result;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        for (int i = 0; i < mViews.size(); i++) {
            Bundle viewState = new Bundle();
            mViews.get(i).saveState(viewState);
            outState.putBundle(SAVE_TAG + i, viewState);
        }
        super.onSaveInstanceState(outState);
    }

}
