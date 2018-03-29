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

package eltos.simpledialogfragment.color;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;

import eltos.simpledialogfragment.CustomViewDialog;
import eltos.simpledialogfragment.R;

/**
 * A dialog with a color wheel to pick a custom color. Supports transparency
 *
 * Result:
 *      COLOR   int     Selected color (argb)
 *
 * Created by eltos on 04.02.2017.
 */
public class SimpleColorWheelDialog extends CustomViewDialog<SimpleColorWheelDialog> {

    public static final String TAG = "SimpleColorWheelDialog.";

    public static final String
            COLOR = TAG + "color";


    public static SimpleColorWheelDialog build(){
        return new SimpleColorWheelDialog();
    }


    /**
     * Specifies the initial color of the color wheel
     *
     * @param color the initial color (argb)
     */
    public SimpleColorWheelDialog color(int color){
        return setArg(COLOR, color);
    }

    /**
     * Specifies weather a seek bar for transparency control is displayed
     *
     * @param enabled weather or not to allow transparency (alpha) adjustment
     */
    public SimpleColorWheelDialog alpha(boolean enabled){
        return setArg(ALPHA, enabled);
    }

    /**
     * Method to hide the input field for color hex code
     *
     * @param enabled weather or not to hide the input field
     */
    public SimpleColorWheelDialog hideHexInput(boolean enabled){
        return setArg(HIDE_HEX, enabled);
    }



    protected static final String ALPHA = TAG + "alpha";
    private static final String HIDE_HEX = TAG + "noHex";

    private ColorWheelView mColorWheelView;
    private EditText mHexInput;
    private ImageView mNew;
    private ImageView mOld;
    private SeekBar mAlphaSlider;
    private View mTransparency;



    private final TextWatcher hexEditWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}
        @Override
        public void afterTextChanged(Editable s) {
            try {
                int color = ((255 - mAlphaSlider.getProgress()) << 24)
                        + (int) Long.parseLong(s.toString(), 16);
                mColorWheelView.setColor(color, false);
                mNew.setImageDrawable(new ColorDrawable(color));
            } catch (NumberFormatException ignored){}
        }
    };


    @Override
    protected View onCreateContentView(Bundle savedInstanceState) {

        View view = inflate(R.layout.simpledialogfragment_color_wheel);
        mColorWheelView = (ColorWheelView) view.findViewById(R.id.colorWheel);
        mTransparency = view.findViewById(R.id.transparencyBox);
        mAlphaSlider = (SeekBar) view.findViewById(R.id.alpha);
        mHexInput = (EditText) view.findViewById(R.id.hexEditText);
        mNew = (ImageView) view.findViewById(R.id.colorNew);
        mOld = (ImageView) view.findViewById(R.id.colorOld);
        View hexLayout = view.findViewById(R.id.hexLayout);


        int color = getArguments().getInt(COLOR, ColorWheelView.DEFAULT_COLOR);
        int oldColor = getArguments().getInt(COLOR);
        if (!getArguments().getBoolean(ALPHA)){
            color = color | 0xFF000000;
            oldColor = oldColor | 0xFF000000;
        }

        mColorWheelView.setColor(color);
        mNew.setImageDrawable(new ColorDrawable(color));
        mAlphaSlider.setMax(255);
        mAlphaSlider.setProgress(255 - Color.alpha(color));
        mHexInput.setText(String.format("%06X", color & 0xFFFFFF));
        hexLayout.setVisibility(getArguments().getBoolean(HIDE_HEX) ? View.GONE : View.VISIBLE);
        mOld.setVisibility(getArguments().containsKey(COLOR) ? View.VISIBLE : View.GONE);
        mOld.setImageDrawable(new ColorDrawable(oldColor));
        final int finalOldColor = oldColor;
        mOld.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mColorWheelView.setColor(finalOldColor);
                mAlphaSlider.setProgress(255 - Color.alpha(finalOldColor));
            }
        });



        mHexInput.addTextChangedListener(hexEditWatcher);
        mColorWheelView.setOnColorChangeListener(new ColorWheelView.OnColorChangeListener() {
            @Override
            public void onColorChange(int color) {
                mHexInput.removeTextChangedListener(hexEditWatcher);
                mHexInput.setText(String.format("%06X", color & 0xFFFFFF));
                mHexInput.addTextChangedListener(hexEditWatcher);
                mNew.setImageDrawable(new ColorDrawable(color));
            }
        });




        mTransparency.setVisibility(getArguments().getBoolean(ALPHA) ? View.VISIBLE : View.GONE);

        mAlphaSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser){
                    mColorWheelView.updateAlpha(255 - progress);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });


        return view;
    }


    @Override
    protected Bundle onResult(int which) {
        Bundle results = new Bundle();
        results.putInt(COLOR, mColorWheelView.getColor());
        return results;
    }
}
