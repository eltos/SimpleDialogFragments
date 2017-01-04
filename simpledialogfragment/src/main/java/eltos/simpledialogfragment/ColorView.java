package eltos.simpledialogfragment;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Checkable;
import android.widget.FrameLayout;
import android.widget.ImageView;


/**
 * Modified from
 * https://github.com/the-blue-alliance/spectrum
 *
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 The Blue Alliance
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute, sub-
 * license, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

class ColorView extends FrameLayout implements Checkable {

    private @ColorInt int mColor;
    private boolean mChecked = false;
    private int mOutlineWidth = 0;

    private ImageView mCheckView;
    private FrameLayout mColorView;

    private final Animation showAnim;
    private final Animation hideAnim;

    public ColorView(Context context) {
        this(context, null);
    }

    public ColorView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        showAnim = AnimationUtils.loadAnimation(getContext(), R.anim.zoom_show);
        hideAnim = AnimationUtils.loadAnimation(getContext(), R.anim.zoom_out);

        LayoutInflater.from(getContext()).inflate(R.layout.color_item, this, true);
        mCheckView = (ImageView) findViewById(R.id.checkmark);
        mColorView = (FrameLayout) findViewById(R.id.color);

        update();
    }


    public @ColorInt int getColor() {
        return mColor;
    }

    public void setColor(@ColorInt int color) {
        if ((color & 0xFF000000) == 0 && color != 0){ // if alpha value omitted, set now
            color = color | 0xFF000000;
        }
        mColor = color;
        update();
    }

    @Override
    public boolean isChecked() {
        return mChecked;
    }

    @Override
    public void toggle() {
        setChecked(!isChecked());
    }

    @Override
    public void setChecked(boolean checked) {
        if (!mChecked && checked){ // checked
            mCheckView.startAnimation(showAnim);
            mCheckView.setVisibility(VISIBLE);
        } else if (mChecked && !checked){ // unchecked
            mCheckView.startAnimation(hideAnim);
            mCheckView.setVisibility(INVISIBLE);
        }
        mChecked = checked;
    }

    /**
     * Change the size of the outlining
     *
     * @param width in px
     */
    public void setOutlineWidth(int width) {
        mOutlineWidth = width;
        update();
    }


    private void update() {
        setForeground(null);
        mColorView.setForeground(createForegroundDrawable());
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            mColorView.setBackgroundDrawable(createBackgroundDrawable());
        } else {
            mColorView.setBackground(createBackgroundDrawable());
        }
        mCheckView.setColorFilter(isColorDark(mColor) ? Color.WHITE : Color.BLACK);
    }


    private Drawable createBackgroundDrawable() {
        GradientDrawable mask = new GradientDrawable();
        mask.setShape(GradientDrawable.OVAL);
        if (mOutlineWidth != 0) {
            mask.setStroke(mOutlineWidth, isColorDark(mColor) ? Color.WHITE : Color.BLACK);
        }
        mask.setColor(mColor);
        return mask;
    }

    private Drawable createForegroundDrawable() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Use a ripple drawable
            GradientDrawable mask = new GradientDrawable();
            mask.setShape(GradientDrawable.OVAL);
            mask.setColor(Color.BLACK);
            return new RippleDrawable(ColorStateList.valueOf(getRippleColor(mColor)), null, mask);

        } else {
            // Use a translucent foreground
            StateListDrawable foreground = new StateListDrawable();
            foreground.setAlpha(80);
            foreground.setEnterFadeDuration(250);
            foreground.setExitFadeDuration(250);

            GradientDrawable mask = new GradientDrawable();
            mask.setShape(GradientDrawable.OVAL);
            mask.setColor(getRippleColor(mColor));
            foreground.addState(new int[]{android.R.attr.state_pressed}, mask);

            foreground.addState(new int[]{}, new ColorDrawable(Color.TRANSPARENT));

            return foreground;
        }
    }



    @Override
    @SuppressWarnings("SuspiciousNameCombination")
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // make view square
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }




    public static boolean isColorDark(@ColorInt int color) {
        double brightness = Color.red(color) * 0.299 +
                Color.green(color) * 0.587 +
                Color.blue(color) * 0.114;
        return brightness < 180;
    }

    public static @ColorInt int getRippleColor(@ColorInt int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] = hsv[2] * 0.5f;
        return Color.HSVToColor(hsv);
    }


}

