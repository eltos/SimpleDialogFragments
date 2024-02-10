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

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import eltos.simpledialogfragment.R;
import eltos.simpledialogfragment.SimpleDialog;
import eltos.simpledialogfragment.color.ColorView;
import eltos.simpledialogfragment.color.SimpleColorDialog;

/**
 * The ViewHolder class for {@link ColorField}
 * 
 * This class is used to create a Color Box and to maintain it's functionality
 * <p>
 * Created by eltos on 06.07.2018.
 */

class ColorViewHolder extends FormElementViewHolder<ColorField> implements SimpleDialog.OnDialogResultListener {

    protected static final String SAVED_COLOR = "color";
    private static final String COLOR_DIALOG_TAG = "colorPickerDialogTag";
    private TextView label;
    private ColorView colorView;

    private ImageView clearButton;

    public ColorViewHolder(ColorField field) {
        super(field);
    }

    @Override
    protected int getContentViewLayout() {
        return R.layout.simpledialogfragment_form_item_color;
    }

    @Override
    protected void setUpView(View view, final Context context, Bundle savedInstanceState,
                             final SimpleFormDialog.DialogActions actions) {

        label = view.findViewById(R.id.label);
        colorView = view.findViewById(R.id.color);
        clearButton = view.findViewById(R.id.clear_color);

        // Label
        String text = field.getText(context);
        label.setText(text);
        label.setVisibility(text == null ? View.GONE : View.VISIBLE);
        view.setOnClickListener(v -> colorView.performClick());

        // Color preset
        if (savedInstanceState != null) {
            setColor(savedInstanceState.getInt(SAVED_COLOR));
        } else {
            setColor(field.getInitialColor(context));
        }
        colorView.setOutlineWidth((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                2 /*dp*/, context.getResources().getDisplayMetrics()));
        colorView.setOutlineColor(field.outline);
        colorView.setStyle(ColorView.Style.PALETTE);
        colorView.setChecked(true);


        colorView.setOnClickListener(v -> actions.showDialog(SimpleColorDialog.build()
                .title(field.getText(context))
                .colors(field.colors)
                .allowCustom(field.allowCustom)
                .colorPreset(colorView.getColor())
                .neut(),
                COLOR_DIALOG_TAG+field.resultKey));

        clearButton.setOnClickListener(v -> setColor(ColorView.NONE));

    }

    private void setColor(@ColorInt int color){
        colorView.setColor(color);
        clearButton.setVisibility(field.required || colorView.getColor() == ColorView.NONE ? View.GONE : View.VISIBLE);
    }


    @Override
    protected void saveState(Bundle outState) {
        outState.putInt(SAVED_COLOR, colorView.getColor());
    }


    @Override
    protected void putResults(Bundle results, String key) {
        results.putInt(key, colorView.getColor());
    }


    @Override
    protected boolean focus(final SimpleFormDialog.FocusActions actions) {
        actions.hideKeyboard();
        //colorView.performClick();
        return colorView.requestFocus();

    }


    @Override
    protected boolean posButtonEnabled(Context context) {
        return !field.required || colorView.getColor() != ColorField.NONE;
    }


    @Override
    protected boolean validate(Context context) {
        boolean valid = posButtonEnabled(context);
        if (valid) {
            TypedValue value = new TypedValue();
            if (label.getContext().getTheme().resolveAttribute(android.R.attr.textColor, value, true)) {
                label.setTextColor(value.data);
            } else {
                label.setTextColor(0x8a000000);
            }
        } else {
            //noinspection deprecation
            label.setTextColor(context.getResources().getColor(R.color.simpledialogfragment_error_color));
        }
        return valid;
    }

    @Override
    public boolean onResult(@NonNull String dialogTag, int which, @NonNull Bundle extras) {
        if ((COLOR_DIALOG_TAG+field.resultKey).equals(dialogTag)){
            if (which == BUTTON_POSITIVE && colorView != null){
                setColor(extras.getInt(SimpleColorDialog.COLOR, colorView.getColor()));
            }
            return true;
        }
        return false;
    }
}
