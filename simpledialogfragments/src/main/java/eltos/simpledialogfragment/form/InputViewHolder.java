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
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.android.material.textfield.TextInputLayout;

import java.util.Arrays;

import eltos.simpledialogfragment.R;
import eltos.simpledialogfragment.input.TextInputAutoCompleteTextView;

/**
 * The ViewHolder class for {@link Input}
 * 
 * This class is used to create an EditText and to maintain it's functionality
 * 
 * Created by philipp on 23.02.17.
 */

@SuppressWarnings("WeakerAccess")
class InputViewHolder extends FormElementViewHolder<Input> {

    protected static final String SAVED_TEXT = "savedText";
    private TextInputAutoCompleteTextView input;
    private TextInputLayout inputLayout;

    InputViewHolder(Input field) {
        super(field);
    }

    @Override
    protected int getContentViewLayout() {
        return R.layout.simpledialogfragment_form_item_input;
    }

    @Override
    protected void setUpView(View view, Context context, final Bundle savedInstanceState,
                             final SimpleFormDialog.DialogActions actions) {

        input = view.findViewById(R.id.editText);
        inputLayout = view.findViewById(R.id.inputLayout);

        if (savedInstanceState == null) {
            // Text preset
            input.setText(field.getText(context));
            // Select all on first focus
            input.setSelectAllOnFocus(true);
            input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        input.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                input.setSelectAllOnFocus(false);
                                input.setOnFocusChangeListener(null);
                            }
                        }, 10);
                    }
                }
            });
        } else {
            input.setText(savedInstanceState.getString(SAVED_TEXT));
        }

        // Hint
        inputLayout.setHint(field.getHint(context));

        // InputType
        if ((field.inputType & InputType.TYPE_MASK_CLASS) == 0) {
            // Setting TYPE_CLASS_TEXT as default is very important since the field
            // is not editable, if no class is specified.
            field.inputType |= InputType.TYPE_CLASS_TEXT;
        }
        input.setInputType(field.inputType);
        if ((field.inputType & InputType.TYPE_MASK_CLASS) == InputType.TYPE_CLASS_PHONE) {
            // format phone number automatically
            input.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        }

        // PW hide/visible toggle button
        if (field.passwordToggleVisible){
            inputLayout.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);
        }

        // Counter (maxLength)
        if (field.maxLength > 0) {
            inputLayout.setCounterMaxLength(field.maxLength);
            inputLayout.setCounterEnabled(true);
        }


        // IME action
        input.setImeOptions(actions.isLastFocusableElement() ? EditorInfo.IME_ACTION_DONE : EditorInfo.IME_ACTION_NEXT);
        input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_NEXT:
                        // auto complete if suggestions forced
                        if (field.forceSuggestion && input.isPopupShowing()
                                && input.getAdapter().getCount() > 0) {
                            input.setText(input.getAdapter().getItem(0).toString());
                        }
                    // fall through...
                    case EditorInfo.IME_ACTION_DONE:
                        // input.performCompletion();
                        actions.continueWithNextElement(true);
                        return true;
                }
                return false;
            }
        });
        input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){
                    validate(v.getContext());
                }
            }
        });

        // Positive button state for single element forms
        if (actions.isOnlyFocusableElement()) {
            input.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }
                @Override
                public void afterTextChanged(Editable s) {
                    actions.updatePosButtonState();
                }
            });
        }

        // Auto complete suggestions
        String[] suggestions = field.getSuggestions(context);
        if (suggestions != null) {
            if (field.isSpinner && !field.required){
                suggestions = Arrays.copyOf(suggestions, suggestions.length + 1);
                suggestions[suggestions.length - 1] = "";
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                    // android.R.layout.simple_dropdown_item_1line
                    android.R.layout.simple_list_item_1, suggestions);
            input.setAdapter(adapter);
            input.setThreshold(1);
            input.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (field.isSpinner && !field.required && position == input.getAdapter().getCount()-1){
                        input.setText(null);
                    }
                    validate(view.getContext());
                    actions.updatePosButtonState();
                    actions.continueWithNextElement(true);
                }
            });
            if (field.isSpinner || (field.forceSuggestion && !field.passwordToggleVisible)) {
                inputLayout.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_OUTLINE); // Background none not supported in combination with dropdown
                inputLayout.setEndIconMode(TextInputLayout.END_ICON_DROPDOWN_MENU);
            }
            if (field.isSpinner) {
                input.setInputType(InputType.TYPE_NULL);
                input.setKeyListener(null);
                input.doNotFilter = true;
                input.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        actions.hideKeyboard();
                    }
                });
            }
        }
    }

    @Nullable
    protected String getText(){
        return input.getText() != null ? input.getText().toString().trim() : null;
    }

    private boolean isInputEmpty(){
        return getText() == null || getText().isEmpty();
    }

    private boolean isLengthExceeded() {
        return field.maxLength > 0 && getText() != null && getText().length() > field.maxLength;
    }

    protected void setError(boolean enabled, @Nullable String error){
        inputLayout.setError(error);
        inputLayout.setErrorEnabled(enabled);
    }


    @Override
    protected void saveState(Bundle outState) {
        outState.putString(SAVED_TEXT, getText());
    }


    @Override
    protected void putResults(Bundle results, String key) {
        results.putString(key, getText());
    }


    @Override
    protected boolean focus(final SimpleFormDialog.FocusActions actions) {
        if (field.isSpinner){
            actions.hideKeyboard();
            input.showDropDown();
        } else {
            // Damn, there is an issue that hides the error hint and counter behind the keyboard
            // because only the input EditText gets focused here, not the entire layout
            // See: https://code.google.com/p/android/issues/detail?id=178153
            // Workaround is resizing the dialog
            input.postDelayed(new Runnable() {
                @Override
                public void run() {
                    InputMethodManager imm = (InputMethodManager) input.getContext()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);
                    }
                }
            }, 100);
        }
        if (field.forceSuggestion){
            input.showDropDown();
        }
//        actions.openKeyboard(input);
        return input.requestFocus();
    }


    @Override
    protected boolean posButtonEnabled(Context context) {
        return !(field.required && isInputEmpty() || isLengthExceeded());
    }


    @Override
    protected boolean validate(Context context) {
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
        if (field.forceSuggestion && !isInputEmpty()){
            String[] suggestions = field.getSuggestions(context);
            String text = getText();
            boolean match = false;
            if (suggestions != null && text != null && suggestions.length > 0){
                for (String s : suggestions) {
                    if (s == null) continue;
                    if (text.equalsIgnoreCase(s)){
                        match = true;
                        input.setTextKeepState(s); // correct case
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
        setError(error != null, error);
        return error == null;
    }
}
