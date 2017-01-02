package eltos.simpledialogfragment.input;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import android.widget.EditText;
import android.widget.TextView;

import eltos.simpledialogfragment.CustomViewDialog;
import eltos.simpledialogfragment.R;
import eltos.simpledialogfragment.SimpleDialog;

/**
 * An SimpleDialogFragment with an input field
 *
 * Created by eltos on 14.10.2015.
 */
public class SimpleInputDialog extends CustomViewDialog<SimpleInputDialog> {

    public static final String TEXT = "simpleInputDialog.text";

    private static final String HINT = "simpleInputDialog.hint";
    private static final String INPUT_TYPE = "simpleInputDialog.input_type";
    private static final String ALLOW_EMPTY = "simpleInputDialog.allow_empty";
    private static final String MAX_LENGTH = "simpleInputDialog.max_length";

    public interface OnDialogResultListener extends SimpleDialog.OnDialogResultListener {
        String TEXT = SimpleInputDialog.TEXT;
    }

    private EditText mInput;
    private TextInputLayout mInputLayout;

    public static SimpleInputDialog build(){
        return new SimpleInputDialog();
    }

    public SimpleInputDialog hint(boolean hint){ return setArg(HINT, hint); }
    public SimpleInputDialog hint(int hintResourceId){ return setArg(HINT, hintResourceId); }
    public SimpleInputDialog text(String text){ return setArg(TEXT, text); }
    public SimpleInputDialog text(int textResourceId){ return setArg(TEXT, textResourceId); }
    public SimpleInputDialog inputType(int inputType){ return setArg(INPUT_TYPE, inputType); }
    public SimpleInputDialog allowEmpty(boolean allow){ return setArg(ALLOW_EMPTY, allow); }
    public SimpleInputDialog max(int maxLength){ return setArg(MAX_LENGTH, maxLength); }

    @Nullable
    public String getText(){
        return mInput.getText() != null ? mInput.getText().toString() : null;
    }

    public boolean isInputEmpty(){
        return getText() == null || getText().trim().isEmpty();
    }

    public void openKeyboard(){
        InputMethodManager imm = (InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mInput, InputMethodManager.SHOW_IMPLICIT);
    }

    @Override
    public View onCreateContentView(Bundle savedInstanceState, final AlertDialog dialog) {
        // inflate and set your custom view here
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_input, null);
        mInput = (EditText) view.findViewById(R.id.editText);
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


    public interface InputValidator {
        /**
         * Let the hosting fragment or activity implement this interface to control
         * when a user can proceed or to display an error message on an invalid input.
         * The method is called every time the user hits the positive button
         *
         * @param dialogTag the tag of this fragment
         * @param input the text entered by the user
         * @param extras the extras passed with {@link #extra(Bundle)}
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
