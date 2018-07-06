/*
 * Copyright 2017 Philipp Niedermayer (github.com/eltos)
 *
 * Modified from https://github.com/the-blue-alliance/spectrum
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

package eltos.simpledialogfragment.color;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Checkable;
import android.widget.FrameLayout;
import android.widget.ImageView;

import eltos.simpledialogfragment.R;



public class ColorView extends FrameLayout implements Checkable {

    public static final int NONE = 0x00000000;
    public static final int AUTO = 0x00FFFFFF;

    private @ColorInt int mColor;
    private boolean mChecked = false;
    private int mOutlineWidth = 0;
    private @ColorInt int mOutlineColor = AUTO;

    private ImageView mCheckView;
    private FrameLayout mColorView;
    private FrameLayout mRippleView;

    private final Animation showAnim;
    private final Animation hideAnim;
    private Style mStyle = Style.CHECK;
    private Drawable mDarkRippleDrawable;
    private Drawable mLightRippleDrawable;


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

        LayoutInflater.from(getContext()).inflate(R.layout.simpledialogfragment_color_item, this, true);
        mCheckView = (ImageView) findViewById(R.id.checkmark);
        mColorView = (FrameLayout) findViewById(R.id.color);
        mRippleView = (FrameLayout) findViewById(R.id.ripple);

        update();
    }

    public enum Style {CHECK, PALETTE}

    public void setStyle(Style style){
        if (mStyle != style){
            mStyle = style;
            update();
        }

    }


    public @ColorInt int getColor() {
        return mColor;
    }

    public void setColor(@ColorInt int color) {
        if ((color & 0xFF000000) == 0 && color != 0){ // if alpha value omitted, set now
            color = color | 0xFF000000;
        }
        if (mColor != color) {
            mColor = color;
            update();
        }
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
        setChecked(checked, true);
    }

    public void setChecked(boolean checked, boolean animate) {
        switch (mStyle) {
            case CHECK:
                showHide(mCheckView, mChecked, checked, animate);
                break;

            case PALETTE:
                showHide(mColorView, mChecked, checked, animate);
                if (mColor != NONE) {
                    mCheckView.setColorFilter(checked ?
                            (isColorDark(mColor) ? Color.WHITE : Color.BLACK) : mColor);
                } else {
                    mCheckView.setColorFilter(null);
                }
                mRippleView.setForeground(checked ? mDarkRippleDrawable : mLightRippleDrawable);
                break;

        }
        mChecked = checked;
    }

    private void showHide(View view, boolean oldState, boolean newState, boolean withAnimation){
        if (withAnimation) {
            if (!oldState && newState) {
                view.startAnimation(showAnim);
            } else if (oldState && !newState) {
                view.startAnimation(hideAnim);
            }
        }
        view.setVisibility(newState ? VISIBLE : INVISIBLE);
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

    /**
     * Change the outline color. Specifying {@link ColorView#AUTO} will choose black or white
     * depending on the brightness
     *
     * @param color as integer or {@link ColorView#AUTO}
     */
    public void setOutlineColor(@ColorInt int color){
        mOutlineColor = color;
        update();
    }


    private void update() {
        setForeground(null);
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            //noinspection deprecation
            mColorView.setBackgroundDrawable(createBackgroundColorDrawable());
        } else {
            mColorView.setBackground(createBackgroundColorDrawable());
        }

        switch (mStyle) {
            case CHECK:
                mCheckView.setImageResource(R.drawable.ic_check_white);
                mCheckView.setColorFilter(isColorDark(mColor) ? Color.WHITE : Color.BLACK);
                mColorView.setVisibility(VISIBLE);
                mRippleView.setForeground(createRippleDrawable(getDarkRippleColor(mColor)));
                break;

            case PALETTE:
                mCheckView.setImageResource(mColor != NONE ? R.drawable.ic_palette_white :
                        R.drawable.ic_palette_color);
                mCheckView.setVisibility(VISIBLE);
                mDarkRippleDrawable = createRippleDrawable(getDarkRippleColor(mColor));
                mLightRippleDrawable = createRippleDrawable(getLightRippleColor(mColor));
                break;
        }
        setChecked(mChecked, false);
    }


    private Drawable createBackgroundColorDrawable() {
        GradientDrawable mask = new GradientDrawable();
        mask.setShape(GradientDrawable.OVAL);
        if (mOutlineWidth != 0) {
            int color = mOutlineColor;
            if (color == AUTO){
                color = isColorDark(mColor) ? Color.WHITE : Color.BLACK;
            }
            mask.setStroke(mOutlineWidth, color);
        }
        mask.setColor(mColor);
        return mask;
    }

    private Drawable createRippleDrawable(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Use a ripple drawable
            GradientDrawable mask = new GradientDrawable();
            mask.setShape(GradientDrawable.OVAL);
            mask.setColor(Color.BLACK);
            return new RippleDrawable(ColorStateList.valueOf(color), null, mask);

        } else {
            // Use a translucent foreground
            StateListDrawable foreground = new StateListDrawable();
            foreground.setAlpha(80);
            foreground.setEnterFadeDuration(250);
            foreground.setExitFadeDuration(250);

            GradientDrawable mask = new GradientDrawable();
            mask.setShape(GradientDrawable.OVAL);
            mask.setColor(color);
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

    public static @ColorInt int getDarkRippleColor(@ColorInt int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] = hsv[2] * 0.5f;
        return Color.HSVToColor(hsv);
    }

    public static @ColorInt int getLightRippleColor(@ColorInt int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[1] = hsv[1] * 0.5f;
        hsv[2] = 1 - (1-hsv[2]) * 0.5f;
        return Color.HSVToColor(hsv);
    }


}

