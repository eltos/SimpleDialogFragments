package eltos.simpledialogfragment.color;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;

import eltos.simpledialogfragment.CustomViewDialog;
import eltos.simpledialogfragment.R;

/**
 *
 * Created by eltos on 04.02.2017.
 */
public class SimpleColorWheelDialog extends CustomViewDialog<SimpleColorWheelDialog> {

    public static final String COLOR = "SimpleColorWheelDialog.color";

    private static final String ALPHA = "SimpleColorWheelDialog.alpha";

    private ColorWheelView mColorWheelView;
    private EditText mHexInput;
    private ImageView mNew;
    private ImageView mOld;
    private SeekBar mAlphaSlider;
    private View mTransparency;

    public static SimpleColorWheelDialog build(){
        return new SimpleColorWheelDialog();
    }

    /**
     * Specifies the initial color of the color wheel
     * @param color the initial color (argb)
     */
    public SimpleColorWheelDialog color(int color){ return setArg(COLOR, color); }

    /**
     * Specifies weather a seek bar for transparency control is displayed
     * @param enabled weather or not to allow transparency (alpha) adjustment
     */
    public SimpleColorWheelDialog alpha(boolean enabled){ return setArg(ALPHA, enabled); }


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
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_color_wheel, null);
        mColorWheelView = (ColorWheelView) view.findViewById(R.id.colorWheel);
        mTransparency = view.findViewById(R.id.transparencyBox);
        mAlphaSlider = (SeekBar) view.findViewById(R.id.alpha);
        mHexInput = (EditText) view.findViewById(R.id.hexEditText);
        mNew = (ImageView) view.findViewById(R.id.colorNew);
        mOld = (ImageView) view.findViewById(R.id.colorOld);

        mOld.setVisibility(getArguments().containsKey(COLOR) ? View.VISIBLE : View.GONE);
        final int oldColor = getArguments().getInt(COLOR);
        mOld.setImageDrawable(new ColorDrawable(oldColor));
        mOld.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mColorWheelView.setColor(oldColor);
                mAlphaSlider.setProgress(255 - Color.alpha(oldColor));
            }
        });

        int color = getArguments().getInt(COLOR, ColorWheelView.DEFAULT_COLOR);
        mColorWheelView.setColor(color);
        mAlphaSlider.setMax(255);
        mAlphaSlider.setProgress(255 - Color.alpha(color));
        mHexInput.setText(String.format("%06X", color & 0xFFFFFF));

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
