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
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import eltos.simpledialogfragment.R;

/**
 * The ViewHolder class for {@link Spinner}
 * </p>
 * This class is used to create a SpinnerView and to maintain it's functionality
 * </p>
 * Created by eltos on 23.02.17.
 */

@SuppressWarnings("WeakerAccess")
class SpinnerViewHolder extends FormElementViewHolder<Spinner> {

    public static final int NONE = -1;

    protected static final String SAVED_POSITION = "pos";
    private android.widget.Spinner spinner;
    private TextView label;
    private CustomSpinnerAdapter adapter;

    SpinnerViewHolder(Spinner field) {
        super(field);
    }

    @Override
    protected int getContentViewLayout() {
        return R.layout.simpledialogfragment_form_item_spinner;
    }

    @Override
    protected void setUpView(View view, Context context, Bundle savedInstanceState,
                             final SimpleFormDialog.DialogActions actions,
                             final boolean isLastElement, boolean isOnlyElement) {

        spinner = (android.widget.Spinner) view.findViewById(R.id.spinner);
        label = (TextView) view.findViewById(R.id.label);

        // Label
        String text = field.getText(context);
        label.setText(text);
        label.setVisibility(text == null ? View.GONE : View.VISIBLE);

        // Items
        String[] items = field.getItems(context);
        if (items != null) {
            adapter = new CustomSpinnerAdapter(context, items,
                    !field.required, field.getPlaceholderText(context));
            spinner.setAdapter(adapter);

            int preset = field.position >= 0 && field.position < items.length ? field.position : NONE;
            setSelection(preset);
        }

        if (savedInstanceState != null) {
            setSelection(savedInstanceState.getInt(SAVED_POSITION));
        }

        // Select listener
//        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                if (!isLastElement) {
//                    actions.continueWithNextElement();
//                }
//            }
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {}
//        });



    }




    @Override
    protected void saveState(Bundle outState) {
        outState.putInt(SAVED_POSITION, getSelection());
    }

    @Override
    protected void putResults(Bundle results, String key) {
        results.putInt(key, getSelection());
    }

    @Override
    protected boolean focus(SimpleFormDialog.FocusActions actions) {
        actions.hideKeyboard();
        actions.clearCurrentFocus();
        spinner.performClick();
        return spinner.requestFocus();
    }

    @Override
    protected boolean posButtonEnabled(Context context) {
        return !field.required || getSelection() != NONE;
    }

    @Override
    protected boolean validate(Context context) {
        boolean valid = !field.required || getSelection() != NONE;
        if (valid) {
            TypedValue value = new TypedValue();
            label.getContext().getTheme().resolveAttribute(android.R.attr.textColor, value, true);
//            int[] attr = new int[] {android.R.attr.textColor};
//            TypedArray a = context.obtainStyledAttributes(value.data, attr);
            label.setTextColor(value.data);
//            a.recycle();

        } else {
            //noinspection deprecation
            label.setTextColor(context.getResources().getColor(R.color.simpledialogfragment_error_color));
        }
//        checkBox.setError(valid ? null : context.getString(R.string.required));
        return valid;
    }




    private int getSelection(){
        return adapter.mapFromSelection(spinner.getSelectedItemPosition());
    }

    private void setSelection(int index){
        spinner.setSelection(adapter.mapToSelection(index), false);
    }

    private static class CustomSpinnerAdapter extends ArrayAdapter<String> {

        private int emptyIndex;
        private boolean allowEmpty;

        int mapToSelection(int index){
            return index == NONE ? emptyIndex : index < emptyIndex ? index : index + 1;
        }
        int mapFromSelection(int index){
            return index == emptyIndex ? NONE : index < emptyIndex ? index : index - 1;
        }

        CustomSpinnerAdapter(Context context, String[] items, boolean allowEmpty, String emptyString){
            super(context, android.R.layout.simple_spinner_dropdown_item);
            this.allowEmpty = allowEmpty;
            if (allowEmpty) {
                emptyIndex = 0;
                add(emptyString);
                addAll(items);
            } else {
                addAll(items);
                add(emptyString);
                emptyIndex = items.length;
            }
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            if (position == emptyIndex) {
                TextView label = (TextView) view.findViewById(android.R.id.text1);
                label.setText("");
                label.setHint(getItem(position));
            }
            return view;
        }

        @Override
        public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
            View view =  super.getDropDownView(position, convertView, parent);
            if (position == emptyIndex) {
                TextView label = (TextView) view.findViewById(android.R.id.text1);
                label.setText("");
                label.setHint(getItem(position));
            }
            return view;
        }

        @Override
        public int getCount() {
            return super.getCount() - (allowEmpty ? 0 : 1);
        }

    }


}
