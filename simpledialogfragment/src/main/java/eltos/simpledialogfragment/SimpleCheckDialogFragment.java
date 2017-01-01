package eltos.simpledialogfragment;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

/**
 * An SimpleDialogFragment with a checkbox
 *
 * Created by eltos on 14.10.2015.
 */
public class SimpleCheckDialogFragment extends CustomViewDialogFragment<SimpleCheckDialogFragment> {

    public static final String CHECKED = "simpleCheckDialogFragment.checked";

    private static final String CHECKBOX_LABEL = "simpleCheckDialogFragment.check_label";
    private static final String CHECKBOX_REQUIRED = "simpleCheckDialogFragment.check_required";

    private CheckBox mCheckBox;

    public static SimpleCheckDialogFragment build(){
        return new SimpleCheckDialogFragment();
    }

    public SimpleCheckDialogFragment check(boolean preset){ return setArg(CHECKED, preset); }
    public SimpleCheckDialogFragment label(String checkBoxLabel){ return setArg(CHECKBOX_LABEL, checkBoxLabel); }
    public SimpleCheckDialogFragment label(int checkBoxLabelResourceId){ return setArg(CHECKBOX_LABEL, checkBoxLabelResourceId); }
    public SimpleCheckDialogFragment checkRequired(boolean required){ return setArg(CHECKBOX_REQUIRED, required); }

    private boolean canGoAhead() {
        return mCheckBox.isChecked() || !getArguments().getBoolean(CHECKBOX_REQUIRED);
    }

    @Override
    public View onCreateContentView(Bundle savedInstanceState, final AlertDialog dialog) {
        // inflate and set your custom view here
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_check_box, null);
        mCheckBox = (CheckBox) view.findViewById(R.id.checkBox);

        mCheckBox.setText(getArgString(CHECKBOX_LABEL));

        if (savedInstanceState != null){
            mCheckBox.setChecked(savedInstanceState.getBoolean(CHECKED, false));
        } else if (getArguments() != null) {
            mCheckBox.setChecked(getArguments().getBoolean(CHECKED, false));
        }

        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setPositiveButtonEnabled(canGoAhead());
            }
        });

        return view;
    }

    @Override
    protected void onDialogShown() {
        setPositiveButtonEnabled(canGoAhead());
    }

    @Override
    public Bundle onResult(int which) {
        Bundle result = new Bundle();
        result.putBoolean(CHECKED, mCheckBox.isChecked());
        return result;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(CHECKED, mCheckBox.isChecked());
    }
}
