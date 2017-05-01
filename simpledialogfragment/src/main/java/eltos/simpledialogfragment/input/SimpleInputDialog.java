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

package eltos.simpledialogfragment.input;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import eltos.simpledialogfragment.CustomViewDialog;
import eltos.simpledialogfragment.R;
import eltos.simpledialogfragment.SimpleDialog;

/**
 * An simple dialog with an input field. Supports suggestions, input validations and
 * max length options.
 *
 * Results:
 *      TEXT    String      The entered text
 *
 * Created by eltos on 14.10.2015.
 */
public class SimpleInputDialog extends CustomViewDialog<SimpleInputDialog> {

    private static final String TAG = "simpleInputDialog";

    public static final String TEXT = TAG + "text";

    protected static final String HINT = TAG + "hint";
    protected static final String INPUT_TYPE = TAG + "input_type";
    protected static final String ALLOW_EMPTY = TAG + "allow_empty";
    protected static final String MAX_LENGTH = TAG + "max_length";
    protected static final String SUGGESTIONS = TAG + "suggestions";

    private AutoCompleteTextView mInput;
    private TextInputLayout mInputLayout;

    public static SimpleInputDialog build(){
        return new SimpleInputDialog();
    }

    /**
     * Sets the EditText's hint
     *
     * @param hint the hint as string
     */
    public SimpleInputDialog hint(String hint){ return setArg(HINT, hint); }

    /**
     * Sets the EditText's hint
     *
     * @param hintResourceId the hint as android string resource
     */
    public SimpleInputDialog hint(@StringRes int hintResourceId){ return setArg(HINT, hintResourceId); }

    /**
     * Sets the EditText's initial text
     *
     * @param text initial text as string
     */
    public SimpleInputDialog text(String text){ return setArg(TEXT, text); }

    /**
     * Sets the EditText's initial text
     *
     * @param textResourceId initial text as android string resource
     */
    public SimpleInputDialog text(@StringRes int textResourceId){ return setArg(TEXT, textResourceId); }

    /**
     * Sets the input type
     * The default is {@link InputType#TYPE_CLASS_TEXT}.
     *
     * @param inputType the InputType
     */
    public SimpleInputDialog inputType(int inputType){ return setArg(INPUT_TYPE, inputType); }

    /**
     * Allow empty input. Default is to disable the positive button until text is entered.
     *
     * @param allow weather to allow empty input
     */
    public SimpleInputDialog allowEmpty(boolean allow){ return setArg(ALLOW_EMPTY, allow); }

    /**
     * Sets a max limit to the EditText.
     *
     * @param maxLength the maximum text length
     */
    public SimpleInputDialog max(int maxLength){ return setArg(MAX_LENGTH, maxLength); }

    /**
     * Provide an array of suggestions to be shown while the user is typing
     *
     * @param context a context to resolve the resource ids
     * @param stringResourceIds suggestion array as android string resources
     */
    public SimpleInputDialog suggest(Context context, int[] stringResourceIds){
        String[] strings = new String[stringResourceIds.length];
        for (int i = 0; i < stringResourceIds.length; i++) {
            strings[i] = context.getString(stringResourceIds[i]);
        }
        return suggest(strings);
    }

    /**
     * Provide an array of suggestions to be shown while the user is typing
     *
     * @param strings suggestion string array
     */
    public SimpleInputDialog suggest(String[] strings){
        getArguments().putStringArray(SUGGESTIONS, strings);
        return this;
    }




    public interface InputValidator {
        /**
         * Let the hosting fragment or activity implement this interface to control
         * when a user can proceed or to display an error message on an invalid input.
         * The method is called every time the user hits the positive button
         *
         * @param dialogTag the tag of this fragment
         * @param input the text entered by the user
         * @param extras the extras passed with {@link SimpleInputDialog#extra(Bundle)}
         * @return an error message to display or null if the input is valid
         */
        String validate(String dialogTag, @Nullable String input, @NonNull Bundle extras);
    }

    protected String onValidateInput(@Nullable String input){
        Bundle extras = getArguments().getBundle(BUNDLE);
        if (extras == null) extras = new Bundle();
        if (getTargetFragment() instanceof InputValidator) {
            return ((InputValidator) getTargetFragment())
                    .validate(getTag(), input, extras);
        }
        if (getActivity() instanceof InputValidator) {
            return ((InputValidator) getActivity())
                    .validate(getTag(), input, extras);
        }
        return null;
    }



    /**
     * @return the current text or null
     */
    @Nullable
    public String getText(){
        return mInput.getText() != null ? mInput.getText().toString() : null;
    }

    public boolean isInputEmpty(){
        return getText() == null || getText().trim().isEmpty();
    }

    /**
     * Helper for opening the soft keyboard
     */
    public void openKeyboard(){
        InputMethodManager imm = (InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mInput, InputMethodManager.SHOW_IMPLICIT);
    }

    @Override
    public View onCreateContentView(Bundle savedInstanceState) {
        // inflate and set your custom view here
        View view = inflate(R.layout.dialog_input);
        mInput = (AutoCompleteTextView) view.findViewById(R.id.editText);
        mInputLayout = (TextInputLayout) view.findViewById(R.id.inputLayout);

        // Note: setting TYPE_CLASS_TEXT as default is very important!
        mInput.setInputType(getArguments().getInt(INPUT_TYPE, InputType.TYPE_CLASS_TEXT));
        if ((getArguments().getInt(INPUT_TYPE) & InputType.TYPE_MASK_CLASS) == InputType.TYPE_CLASS_PHONE) {
            // format phone number automatically
            mInput.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        }
        //mInput.setHint(getArgString(HINT));
        mInputLayout.setHint(getArgString(HINT));
        if (getArguments().getInt(MAX_LENGTH) > 0) {
            mInputLayout.setCounterMaxLength(getArguments().getInt(MAX_LENGTH));
            mInputLayout.setCounterEnabled(true);
        }


        if (savedInstanceState != null) {
            mInput.setText(savedInstanceState.getString(TEXT));
        } else {
            mInput.setText(getArgString(TEXT));
            mInput.setSelection(0, mInput.length());
        }

        mInput.setImeOptions(EditorInfo.IME_ACTION_DONE);
        mInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE){
                    pressPositiveButton();
                    return true;
                }
                return false;
            }
        });

        mInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                setPositiveButtonEnabled(posEnabled());
            }
        });

        // Auto complete
        String[] suggestionList = getArguments().getStringArray(SUGGESTIONS);
        if (suggestionList != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                    // android.R.layout.simple_dropdown_item_1line
                    android.R.layout.simple_list_item_1, suggestionList);
            mInput.setAdapter(adapter);
            mInput.setThreshold(1);
        }

        return view;
    }

    private boolean posEnabled(){
        return (!isInputEmpty() || getArguments().getBoolean(ALLOW_EMPTY)) && (getText() == null
                || getText().length() <= getArguments().getInt(MAX_LENGTH, getText().length()));
    }



    @Override
    protected void onDialogShown() {
        setPositiveButtonEnabled(posEnabled());
        //mInput.requestFocus();
        openKeyboard();
    }

    @Override
    protected boolean acceptsPositiveButtonPress() {
        String input = getText();
        String error = onValidateInput(input);
        if (error == null) {
            return true;
        } else {
            mInputLayout.setError(error);
            mInputLayout.setErrorEnabled(true);
            return false;
        }
    }


    @Override
    public Bundle onResult(int which) {
        Bundle result = new Bundle();
        result.putString(TEXT, getText());
        return result;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(TEXT, getText());
    }
}
