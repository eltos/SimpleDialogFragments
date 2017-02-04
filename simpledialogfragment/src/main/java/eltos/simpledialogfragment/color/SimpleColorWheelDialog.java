package eltos.simpledialogfragment.color;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import eltos.simpledialogfragment.CustomViewDialog;
import eltos.simpledialogfragment.R;

/**
 * Created by Philipp on 04.02.2017.
 */
public class SimpleColorWheelDialog extends CustomViewDialog<SimpleColorWheelDialog> {

    public static final String COLOR = "SimpleColorWheelDialog.color";

    private ColorWheelView mColorWheelView;
    private EditText mHexInput;
    private View mNew;
    private View mOld;

    public static SimpleColorWheelDialog build(){
        return new SimpleColorWheelDialog();
    }

    public SimpleColorWheelDialog color(int color){ return setArg(COLOR, color); }


    private final TextWatcher hexEditWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}
        @Override
        public void afterTextChanged(Editable s) {
            try {
                int color = 0xFF000000 + (int) Long.parseLong(s.toString(), 16);
                mColorWheelView.setColor(color, false);
                mNew.setBackgroundColor(color);
            } catch (NumberFormatException ignored){}
        }
    };


    @Override
    protected View onCreateContentView(Bundle savedInstanceState) {

        // inflate and set your custom view here
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_color_wheel, null);
        mColorWheelView = (ColorWheelView) view.findViewById(R.id.colorWheel);
        mHexInput = (EditText) view.findViewById(R.id.hexEditText);
        mNew = view.findViewById(R.id.colorNew);
        mOld = view.findViewById(R.id.colorOld);

        mOld.setVisibility(getArguments().containsKey(COLOR) ? View.VISIBLE : View.GONE);
        final int oldColor = getArguments().getInt(COLOR);
        mOld.setBackgroundColor(0xFF000000 + oldColor);
        mOld.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mColorWheelView.setColor(oldColor);
            }
        });

        mColorWheelView.setOnColorChangeListener(new ColorWheelView.OnColorChangeListener() {
            @Override
            public void onColorChange(int color) {
                mHexInput.removeTextChangedListener(hexEditWatcher);
                mHexInput.setText(String.format("%06X", color & 0xFFFFFF));
                mHexInput.addTextChangedListener(hexEditWatcher);
                mNew.setBackgroundColor(color);
            }
        });

        mHexInput.addTextChangedListener(hexEditWatcher);

        mColorWheelView.setColor(getArguments().getInt(COLOR, 0xFF0000));

        return view;
    }


    @Override
    protected Bundle onResult(int which) {
        Bundle results = new Bundle();
        results.putInt(COLOR, mColorWheelView.getColor());
        return results;
    }
}
