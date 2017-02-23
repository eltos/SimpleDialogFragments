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

package eltos.simpledialogfragment.form;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

import eltos.simpledialogfragment.CustomViewDialog;
import eltos.simpledialogfragment.R;

/**
 * A form dialog to display a number of input fields to the user, such as
 * - Input fields ({@link Input})
 *
 *
 * Created by eltos on 20.02.17.
 */

public class SimpleFormDialog extends CustomViewDialog<SimpleFormDialog> {

    private static final String TAG = "SimpleFormDialog";

    private static final String INPUT_FIELDS = TAG + "inputFields";
    private static final String SAVE_TAG = "form.";


    public static SimpleFormDialog build(){
        return new SimpleFormDialog();
    }


    /**
     * Convenient method to build a form dialog with a single email input
     *
     * @param emailFieldKey the key that can be used to receive the entered text from the bundle
     *                      in {@link SimpleFormDialog.OnDialogResultListener#onResult}
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
     *                         in {@link SimpleFormDialog.OnDialogResultListener#onResult}
     */
    public static SimpleFormDialog buildPasswordInput(String passwordFieldKey){
        return SimpleFormDialog.build()
                .fields(
                        Input.password(passwordFieldKey).required()
                );
    }

    /**
     * Convenient method to build a form dialog with a single number input
     *
     * @param numberFieldKey the key that can be used to receive the entered text from the bundle
     *                       in {@link SimpleFormDialog.OnDialogResultListener#onResult}
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
     *                      in {@link SimpleFormDialog.OnDialogResultListener#onResult}
     * @param passwordFieldKey the key that can be used to receive the entered password from the
     *                         bundle in {@link SimpleFormDialog.OnDialogResultListener#onResult}
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
     *                     in {@link SimpleFormDialog.OnDialogResultListener#onResult}
     * @param passwordFieldKey the key that can be used to receive the entered password from the
     *                         bundle in {@link SimpleFormDialog.OnDialogResultListener#onResult}
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
         * @param extras the extras passed with {@link #extra(Bundle)}
         *
         * @return the error message to display or null if the input is valid
         */
        String validate(String dialogTag, String fieldKey, @Nullable String input, @NonNull Bundle extras);
    }








    ///////////////////////////////////////////////////////////////////////////////////////////


    private String onValidateInput(String fieldKey, @Nullable String input){
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
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        setPositiveButtonEnabled(posButtonEnabled());
        requestFocus(0);
    }

    private boolean posButtonEnabled() {
        return mViews.size() != 1 || mViews.get(0).posButtonEnabled(getContext());
    }


    void openKeyboard(final EditText input){
        input.post(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) getContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);
            }
        });
    }

    void hideKeyboard(){
        View view = getDialog().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    void clearCurrentFocus(){
        mFormContainer.requestFocus();
//        View view = getDialog().getCurrentFocus();
//        if (view != null) {
//            view.clearFocus();
//        }
    }

    protected void requestFocus(int viewIndex){
        if (viewIndex < mViews.size() && viewIndex >= 0) {
            mViews.get(viewIndex).focus();
        }
    }

    @Override
    protected boolean acceptsPositiveButtonPress() {
        boolean okay = true;
        for (ViewHolder holder : mViews){
            if (!holder.validate(getContext())){
                if (okay) holder.focus(); // focus first element that is not valid
                okay = false;
            }
        }
        return okay;
    }

    private FocusActions mFocusActions = new FocusActions();
    class FocusActions {
        /**
         * Helper for opening the soft keyboard on the first edit text found
         */
//        protected void openKeyboard(){
//            for (ViewHolder holder : mViews) {
//                if (holder instanceof InputHolder){
//                    openKeyboard(((InputHolder) holder).input);
//                    break;
//                }
//            }
//        }



    };


    /**
     * A view holder for form elements
     *
     * @param <F> the subclass of {@link FormElement} this holder represents
     */
    static abstract class ViewHolder<F extends FormElement<F>> {

        F field;

        /**
         * Method to save this elements state
         */
        abstract void saveState(Bundle outState, String tag);

        /**
         * Method to restore this elements state
         */
        abstract void recoverState(Bundle inState, String tag);

        /**
         * Method to publish results
         */
        abstract void putResults(Bundle results);

        /**
         * Method to focus this element
         *
         * @return Whether this view or one of its descendants actually took focus.
         */
        abstract boolean focus();

        /**
         * Method to check for empty input, (un-)checked state etc.
         * Only simple (and fast) checks here, no error displaying!
         * This is used only for single element forms.
         *
         * @return true if positive button can be enabled
         */
        abstract boolean posButtonEnabled(Context context);

        /**
         * Method to validate input, state etc. and displaying error messages
         *
         * @return true if the input, state etc. is valid, false otherwise
         */
        abstract boolean validate(Context context);

    }

    /**
     * A view holder for input fields
     */
    class InputHolder extends ViewHolder<Input> {

        AutoCompleteTextView input;
        TextInputLayout inputLayout;

        @Nullable
        String getText(){
            return input.getText() != null ? input.getText().toString().trim() : null;
        }

        boolean isInputEmpty(){
            return getText() == null || getText().isEmpty();
        }

        boolean isLengthExceeded() {
            return field.maxLength > 0 && getText() != null && getText().length() > field.maxLength;
        }

        @Override
        void saveState(Bundle outState, String tag) {
            outState.putString(tag, getText());
        }

        @Override
        void recoverState(Bundle inState, String tag) {
            input.setText(inState.getString(tag));
        }

        @Override
        void putResults(Bundle results) {
            results.putString(field.resultKey, getText());
        }

        @Override
        boolean focus() {
            // Damn, there is an issue that hides the error hint and counter behind the keyboard
            // because only the input EditText gets focused here, not the entire layout
            // See: https://code.google.com/p/android/issues/detail?id=178153
            // Workaround is resizing the dialog
            openKeyboard(input);
            return input.requestFocus();
        }

        @Override
        boolean posButtonEnabled(Context context) {
            return !(field.required && isInputEmpty() || isLengthExceeded());
        }

        @Override
        boolean validate(Context context) {
            // required but empty?
            if (field.required && isInputEmpty()) {
                setError(true, context.getString(R.string.required));
                return false;
            }
            // max length exceeded?
            if (isLengthExceeded()){
                setError(true, null);
                return false;
            }
            // min length not reached?
            if (getText() != null && getText().length() < field.minLength){
                setError(true, context.getResources().getQuantityString(
                        R.plurals.at_least_x_chars, field.minLength, field.minLength));
                return false;
            }
            // input not any of the provided suggestions?
            if (field.forceSuggestion){
                String[] suggestions = field.getSuggestions(context);
                String text = getText();
                boolean match = false;
                if (suggestions != null && text != null && suggestions.length > 0){
                    for (String s : suggestions) {
                        if (s == null) continue;
                        if (text.toLowerCase().equals(s.toLowerCase())){
                            match = true;
                            input.setTextKeepState(s); // correct caps
                            break;
                        }
                    }
                    if (!match){
                        setError(true, context.getString(R.string.input_not_a_given_option));
                        return false;
                    }
                }
            }
            // predefined validation
            String error = field.validatePattern(context, getText());
            // custom validation
            if (error == null){
                error = onValidateInput(field.resultKey, getText());;
            }
            setError(error != null, error);
            return error == null;
        }

        void setError(boolean enabled, @Nullable String error){
            inputLayout.setError(error);
            inputLayout.setErrorEnabled(enabled);
        }
    }

    /**
     * A view holder for check fields
     */
    class CheckHolder extends ViewHolder<Check> {

        CheckBox checkBox;

        @Override
        void saveState(Bundle outState, String tag) {
            outState.putBoolean(tag, checkBox.isChecked());
        }

        @Override
        void recoverState(Bundle inState, String tag) {
            checkBox.setChecked(inState.getBoolean(tag));
        }

        @Override
        void putResults(Bundle results) {
            results.putBoolean(field.resultKey, checkBox.isChecked());
        }

        @Override
        boolean focus() {

            hideKeyboard();

            // Temporary make this view focusable, so that we can scroll to it
            // and show a short flash of the box getting focused
            checkBox.setFocusableInTouchMode(true);

            checkBox.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // finally remove focus
                    checkBox.setFocusable(false);
                    clearCurrentFocus();
                }
            }, 100);

            return checkBox.requestFocus();
        }

        @Override
        boolean posButtonEnabled(Context context) {
            return !field.required || checkBox.isChecked();
        }

        @Override
        boolean validate(Context context) {
            boolean valid = !field.required || checkBox.isChecked();
            if (valid) {
                TypedValue value = new TypedValue();
                checkBox.getContext().getTheme().resolveAttribute(android.R.attr.checkboxStyle, value, true);

                int[] attr = new int[] {android.R.attr.textColor};
                TypedArray a = context.obtainStyledAttributes(value.data, attr);
                checkBox.setTextColor(a.getColor(0, 0xFF00FF00));
                a.recycle();

//                checkBox.setTextColor(value.data);
            } else {
                //noinspection deprecation
                checkBox.setTextColor(context.getResources().getColor(R.color.error_color));
            }
//            checkBox.setError(valid ? null : getString(R.string.required));
            return valid;
        }
    }




    ArrayList<ViewHolder> mViews = new ArrayList<>(0);
    ViewGroup mFormContainer;

    @Override
    public View onCreateContentView(Bundle savedInstanceState) {
        // inflate custom view
        View view = inflate(R.layout.dialog_form);
        mFormContainer = (ViewGroup) view.findViewById(R.id.container);

        ArrayList<FormElement> fields = getArguments().getParcelableArrayList(INPUT_FIELDS);

        if (fields != null) {

            mViews = new ArrayList<>(fields.size());

            for (int i = 0; i < fields.size(); i++) {

                View child = null;
                ViewHolder holder = null;

                if (fields.get(i) instanceof Input) {

                    // Input element
                    holder = new InputHolder();
                    holder.field = fields.get(i);

                    child = inflate(R.layout.dialog_form_item_input, mFormContainer, false);

                    ((InputHolder) holder).input = (AutoCompleteTextView) child.findViewById(R.id.editText);
                    ((InputHolder) holder).inputLayout = (TextInputLayout) child.findViewById(R.id.inputLayout);

                    setupInputView(i, (InputHolder) holder, i == fields.size()-1);

                } else if (fields.get(i) instanceof Check){

                    // Check element
                    holder = new CheckHolder();
                    holder.field = fields.get(i);

                    child = inflate(R.layout.dialog_form_item_check, mFormContainer, false);

                    ((CheckHolder) holder).checkBox = (CheckBox) child.findViewById(R.id.checkBox);

                    setupCheckView(i, (CheckHolder) holder, i == fields.size()-1);
                }


                // saved state
                if (holder != null && savedInstanceState != null){
                    holder.recoverState(savedInstanceState, SAVE_TAG + i);
                }

                mFormContainer.addView(child);
                mViews.add(holder);

            }

        }



        return view;
    }




    private void setupCheckView(int pos, CheckHolder holder, boolean isLast){

        // Label
        holder.checkBox.setText(holder.field.getText(getContext()));

        // Check preset
        holder.checkBox.setChecked(holder.field.getInitialState(getContext()));

        // Positive button state for single element forms
        if (pos == 0 && isLast) {
            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    setPositiveButtonEnabled(posButtonEnabled());
                }
            });
        }

    }

    private void setupInputView(int pos, final InputHolder holder, boolean isLast){

        // Text preset
        holder.input.setText(holder.field.getText(getContext()));
        // Select all on first focus
        holder.input.setSelectAllOnFocus(true);
        holder.input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    holder.input.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            holder.input.setSelectAllOnFocus(false);
                            holder.input.setOnFocusChangeListener(null);
                        }
                    }, 10);
                }
            }
        });

        // Hint
        holder.inputLayout.setHint(holder.field.getHint(getContext()));

        // InputType
        if ((holder.field.inputType & InputType.TYPE_MASK_CLASS) == 0) {
            // Setting TYPE_CLASS_TEXT as default is very important since the field
            // is not editable, if no class is specified.
            holder.field.inputType |= InputType.TYPE_CLASS_TEXT;
        }
        holder.input.setInputType(holder.field.inputType);
        if ((holder.field.inputType & InputType.TYPE_MASK_CLASS) == InputType.TYPE_CLASS_PHONE) {
            // format phone number automatically
            holder.input.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        }

        // PW hide/visible toggle button
        holder.inputLayout.setPasswordVisibilityToggleEnabled(holder.field.passwordToggleVisible);

        // Counter (maxLength)
        if (holder.field.maxLength > 0) {
            holder.inputLayout.setCounterMaxLength(holder.field.maxLength);
            holder.inputLayout.setCounterEnabled(true);
        }

        // IME action
        holder.input.setImeOptions(isLast ? EditorInfo.IME_ACTION_DONE : EditorInfo.IME_ACTION_NEXT);
        final int next = pos+1;
        holder.input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    if (holder.field.forceSuggestion && holder.input.isPopupShowing()
                            && holder.input.getAdapter().getCount() > 0) {
                        holder.input.setText(holder.input.getAdapter().getItem(0).toString());
                    }
                    // focus next input if this is valid
                    holder.validate(getContext());
                    requestFocus(next);
                    return true;
                } else if (actionId == EditorInfo.IME_ACTION_DONE) {
                    holder.input.performCompletion();
                    pressPositiveButton();
                    return true;
                }
                return false;
            }
        });

        // Positive button state for single element forms
        if (pos == 0 && isLast) {
            holder.input.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    setPositiveButtonEnabled(posButtonEnabled());
                }
            });
        }

        // Auto complete suggestions
        String[] suggestions = holder.field.getSuggestions(getContext());
        if (suggestions != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                    // android.R.layout.simple_dropdown_item_1line
                    android.R.layout.simple_list_item_1, suggestions);
            holder.input.setAdapter(adapter);
            holder.input.setThreshold(1);
        }


    }




    @Override
    public Bundle onResult(int which) {
        Bundle result = new Bundle();
        for (ViewHolder holder : mViews) {
            holder.putResults(result);
        }
        return result;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        for (int i = 0; i < mViews.size(); i++) {
            mViews.get(i).saveState(outState, SAVE_TAG + i);
        }
        super.onSaveInstanceState(outState);
    }

}
