/*
 *  Copyright 2017 Philipp Niedermayer (github.com/eltos)
 *  Copyright 2016 Alex Yanchenko
 *
 *  Modified from
 *  https://github.com/droidparts/droidparts/blob/develop/droidparts/src/org/droidparts/widget/ClearableEditText.java
 *
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


package eltos.simpledialogfragment.list;




import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;

import eltos.simpledialogfragment.R;

/**
 * An edit text with a clear button
 * 
 */
public class ClearableEditText extends MaterialAutoCompleteTextView implements OnTouchListener, OnFocusChangeListener {



    public enum Location {
        LEFT(0), RIGHT(2);

        final int idx;

        Location(int idx) {
            this.idx = idx;
        }
    }

    public interface Listener {
        void didClearText();
    }

    public ClearableEditText(Context context) {
        super(context);
        init(context, null);
    }

    public ClearableEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ClearableEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }


    public void setClearPosition(Location loc) {
        this.loc = loc;
    }

    @SuppressWarnings("deprecation")
    public void setClearDrawable(@DrawableRes int resId){
        xD = getResources().getDrawable(resId);
    }

    @Override
    public void setOnTouchListener(OnTouchListener l) {
        this.l = l;
    }

    @Override
    public void setOnFocusChangeListener(OnFocusChangeListener f) {
        this.f = f;
    }

    private Location loc = Location.RIGHT;

    private Drawable xD;
    private Listener listener;

    private OnTouchListener l;
    private OnFocusChangeListener f;

    private final TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            if (isFocused()) {
                setClearIconVisible(s != null && s.length() > 0);
            }
        }
    };

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (isClearIconVisible()) {
            int x = (int) event.getX();
            int y = (int) event.getY();
            int left = (loc == Location.LEFT) ? 0 : getWidth() - getPaddingRight() - xD.getIntrinsicWidth();
            int right = (loc == Location.LEFT) ? getPaddingLeft() + xD.getIntrinsicWidth() : getWidth();
            boolean tappedX = x >= left && x <= right && y >= 0 && y <= (getBottom() - getTop());
            if (tappedX) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    setText("");
                    if (listener != null) {
                        listener.didClearText();
                    }
                }
                return true;
            }
        }
        return l != null && l.onTouch(v, event);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            setClearIconVisible(getText() != null && getText().length() > 0);
        } else {
            setClearIconVisible(false);
        }
        if (f != null) {
            f.onFocusChange(v, hasFocus);
        }
    }

    private void init(Context context, @Nullable AttributeSet attrs) {

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.ClearableEditText, 0, 0);
        try {
            Integer i = a.getInteger(R.styleable.ClearableEditText_clearPosition, -1);
            if (i == 0){
                loc = Location.LEFT;
            } else if (i == 1){
                loc = Location.RIGHT;
            }
            xD = a.getDrawable(R.styleable.ClearableEditText_clearDrawable);

        } finally {
            a.recycle();
        }

        if (xD == null){
            xD = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_clear_search, context.getTheme());
        }
        if (xD != null) {
            xD.setBounds(0, 0, xD.getIntrinsicWidth(), xD.getIntrinsicHeight());
            int min = getPaddingTop() + xD.getIntrinsicHeight() + getPaddingBottom();
            if (getSuggestedMinimumHeight() < min) {
                setMinimumHeight(min);
            }
        }

        super.setOnTouchListener(this);
        super.setOnFocusChangeListener(this);
        addTextChangedListener(mTextWatcher);
        setClearIconVisible(false);
    }


    protected boolean isClearIconVisible() {
        if (loc == Location.LEFT){
            return getCompoundDrawables()[0] != null;
        } else if (loc == Location.RIGHT){
            return getCompoundDrawables()[2] != null;
        }
        return false;
    }

    protected void setClearIconVisible(boolean visible) {
        if (visible != isClearIconVisible()) {
            super.setCompoundDrawables(
                    (visible & loc == Location.LEFT) ? xD : null, null,
                    (visible & loc == Location.RIGHT) ? xD : null, null);
        }
    }
}

