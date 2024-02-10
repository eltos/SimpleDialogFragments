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

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.ArrayRes;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import eltos.simpledialogfragment.R;
import eltos.simpledialogfragment.list.AdvancedAdapter;
import eltos.simpledialogfragment.list.CustomListDialog;


/**
 * A dialog that let's the user select a color
 * <p>
 * Result:
 *      COLOR   int     Selected color (rgb)
 * <p>
 * Created by eltos on 17.04.2016.
 */
public class SimpleColorDialog extends CustomListDialog<SimpleColorDialog> implements SimpleColorWheelDialog.OnDialogResultListener {

    public static final String TAG = "SimpleColorDialog.";

    public static final String
            COLOR = TAG + "color",
            COLORS = TAG + "colors";


    public static SimpleColorDialog build(){
        return new SimpleColorDialog();
    }


    public static final @ArrayRes int
            MATERIAL_COLOR_PALLET = R.array.material_pallet,
            MATERIAL_COLOR_PALLET_LIGHT = R.array.material_pallet_light,
            MATERIAL_COLOR_PALLET_DARK = R.array.material_pallet_dark,
            BEIGE_COLOR_PALLET = R.array.beige_pallet,
            COLORFUL_COLOR_PALLET = R.array.colorful_pallet;

    public static final int
            NONE = ColorView.NONE,
            AUTO = ColorView.AUTO;


    /**
     * Sets the colors to choose from
     * Default is the {@link SimpleColorDialog#DEFAULT_COLORS} set
     *
     * @param colors array of rgb-colors
     * @return this instance
     */
    public SimpleColorDialog colors(@ColorInt int[] colors){
        getArgs().putIntArray(COLORS, colors);
        return this;
    }

    /**
     * Sets the color pallet to choose from
     * May be one of {@link SimpleColorDialog#MATERIAL_COLOR_PALLET},
     * {@link SimpleColorDialog#MATERIAL_COLOR_PALLET_DARK},
     * {@link SimpleColorDialog#MATERIAL_COLOR_PALLET_LIGHT},
     * {@link SimpleColorDialog#BEIGE_COLOR_PALLET} or a custom pallet
     *
     * @param context a context to resolve the resource
     * @param colorArrayRes color array resource id
     * @return this instance
     */
    public SimpleColorDialog colors(Context context, @ArrayRes int colorArrayRes){
        return colors(context.getResources().getIntArray(colorArrayRes));
    }

    /**
     * Sets the initially selected color
     *
     * @param color the selected color
     * @return this instance
     */
    public SimpleColorDialog colorPreset(@ColorInt int color){
        getArgs().putInt(COLOR, color);
        return this;
    }

    /**
     * Set this to true to show a field with a color picker option
     *
     * @param allow allow custom picked color if true
     * @return this instance
     */
    public SimpleColorDialog allowCustom(boolean allow){
        return setArg(CUSTOM, allow);
    }

    /**
     * Configures {@link SimpleColorWheelDialog} for custom color picking
     * See {@link SimpleColorWheelDialog#alpha(boolean)}
     *
     * @param alpha whether or not to allow transparency (alpha) adjustment
     * @return this instance
     */
    public SimpleColorDialog setupColorWheelAlpha(boolean alpha){
        return setArg(CUSTOM_ALPHA, alpha);
    }

    /**
     * Configures {@link SimpleColorWheelDialog} for custom color picking
     * See {@link SimpleColorWheelDialog#hideHexInput(boolean)}
     *
     * @param hideHex whether or not to hide the input field
     * @return this instance
     */
    public SimpleColorDialog setupColorWheelHideHex(boolean hideHex){
        return setArg(CUSTOM_HIDE_HEX, hideHex);
    }

    /**
     * Configures {@link SimpleColorWheelDialog} for custom color picking
     * See {@link SimpleColorWheelDialog#title(CharSequence)}
     *
     * @param text the title for the color wheel dialog
     * @return this instance
     */
    public SimpleColorDialog setupColorWheelTitle(CharSequence text){
        return setArg(CUSTOM_TITLE, text);
    }

    /**
     * Configures {@link SimpleColorWheelDialog} for custom color picking
     * See {@link SimpleColorWheelDialog#pos(CharSequence)}
     *
     * @param text the positive button text for the color wheel dialog
     * @return this instance
     */
    public SimpleColorDialog setupColorWheelPosButton(CharSequence text){
        return setArg(CUSTOM_POS, text);
    }

    /**
     * Configures {@link SimpleColorWheelDialog} for custom color picking
     * See {@link SimpleColorWheelDialog#neut(CharSequence)}
     *
     * @param text the neutral button text for the color wheel dialog
     * @return this instance
     */
    public SimpleColorDialog setupColorWheelNeutButton(CharSequence text){
        return setArg(CUSTOM_NEUT, text);
    }

    /**
     * Add a colored outline to the color fields.
     * {@link SimpleColorDialog#NONE} disables the outline (default)
     * {@link SimpleColorDialog#AUTO} uses a black or white outline depending on the brightness
     *
     * @param color color int or {@link SimpleColorDialog#NONE} or {@link SimpleColorDialog#AUTO}
     * @return this instance
     */
    public SimpleColorDialog showOutline(@ColorInt int color){
        return setArg(OUTLINE, color);
    }




    public static final @ColorInt int[] DEFAULT_COLORS = new int[]{
            0xfff44336, 0xffe91e63, 0xff9c27b0, 0xff673ab7,
            0xff3f51b5, 0xff2196f3, 0xff03a9f4, 0xff00bcd4,
            0xff009688, 0xff4caf50, 0xff8bc34a, 0xffcddc39,
            0xffffeb3b, 0xffffc107, 0xffff9800, 0xffff5722,
            0xff795548, 0xff9e9e9e, 0xff607d8b
    };

    protected static final int PICKER = 0x00BADA55;
    protected static final String PICKER_DIALOG_TAG = TAG + "picker",
            CUSTOM = TAG + "custom",
            CUSTOM_ALPHA = TAG + "custom_alpha",
            CUSTOM_HIDE_HEX = TAG + "custom_hide_hex",
            CUSTOM_TITLE = TAG + "custom_title",
            CUSTOM_POS = TAG + "custom_pos",
            CUSTOM_NEUT = TAG + "custom_neut";

    private static final String SELECTED = TAG + "selected";
    private static final String OUTLINE = TAG + "outline";

    private @ColorInt int mCustomColor = NONE;
    private @ColorInt int mSelectedColor = NONE;

    public SimpleColorDialog(){
        grid();
        gridColumnWidth(R.dimen.dialog_color_item_size);
        choiceMode(SINGLE_CHOICE);
        choiceMin(1);
        colors(DEFAULT_COLORS);
        setArg(CUSTOM_POS, android.R.string.ok);
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null){
            mCustomColor = savedInstanceState.getInt(CUSTOM, mCustomColor);
            mSelectedColor = savedInstanceState.getInt(SELECTED, mCustomColor);
        }
    }

    @Override
    protected AdvancedAdapter onCreateAdapter() {

        int[] colors = getArgs().getIntArray(COLORS);
        if (colors == null) colors = new int[0];
        boolean custom = getArgs().getBoolean(CUSTOM);

        // preset
        if (mSelectedColor == NONE && getArgs().containsKey(COLOR)){
            @ColorInt int preset = getArgs().getInt(COLOR, NONE);
            int index = indexOf(colors, preset);
            if (index < 0){ // custom preset
                mSelectedColor = mCustomColor = preset;
                if (custom){
                    choicePreset(colors.length);
                }
            } else {
                choicePreset(index);
                mSelectedColor = preset;
            }
        }

        // Selector provided by ColorView
        getListView().setSelector(new ColorDrawable(Color.TRANSPARENT));

        return new ColorAdapter(colors, custom);
    }

    private int indexOf(int[] array, int item){
        for (int i = 0; i < array.length; i++) {
            if (array[i] == item){
                return i;
            }
        }
        return -1;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(CUSTOM, mCustomColor);
        outState.putInt(SELECTED, mSelectedColor);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected boolean acceptsPositiveButtonPress() {
        if (getArgs().getInt(CHOICE_MODE) == SINGLE_CHOICE_DIRECT){
            return mSelectedColor != NONE;
        }
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (id == PICKER){
            ColorAdapter adapter = ((ColorAdapter) parent.getAdapter());
            boolean deselect = getArgs().getInt(CHOICE_MODE) == MULTI_CHOICE && adapter.isItemChecked(position);
            if (!deselect){
                SimpleColorWheelDialog dialog = SimpleColorWheelDialog.build()
                        .theme(getTheme())
                        .title(getArgString(CUSTOM_TITLE))
                        .pos(getArgString(CUSTOM_POS))
                        .neut(getArgString(CUSTOM_NEUT))
                        .alpha(getArgs().getBoolean(CUSTOM_ALPHA))
                        .hideHexInput(getArgs().getBoolean(CUSTOM_HIDE_HEX));
                if (mCustomColor != NONE){
                    dialog.color(mCustomColor);
                } else if (mSelectedColor != NONE){
                    dialog.color(mSelectedColor);
                    mCustomColor = mSelectedColor;
                }
                dialog.show(this, PICKER_DIALOG_TAG);
            }
            mSelectedColor = NONE;
        } else {
            mSelectedColor = (int) id;
        }
    }

    @Override
    public boolean onResult(@NonNull String dialogTag, int which, @NonNull Bundle extras) {
        if (PICKER_DIALOG_TAG.equals(dialogTag) && which == BUTTON_POSITIVE){
            mSelectedColor = mCustomColor = extras.getInt(SimpleColorWheelDialog.COLOR);
            notifyDataSetChanged();
            if (getArgs().getInt(CHOICE_MODE) == SINGLE_CHOICE_DIRECT){
                pressPositiveButton();
            }
            return true;
        }
        return false;
    }

    @Override
    protected Bundle onResult(int which) {
        Bundle b = super.onResult(which);
        int color = (int) b.getLong(SELECTED_SINGLE_ID);
        if (color == PICKER){
            b.putInt(COLOR, mCustomColor);
        } else {
            b.putInt(COLOR, color);
        }

        long[] ids = b.getLongArray(SELECTED_IDS);
        if (ids != null) {
            int[] colors = new int[ids.length];
            for (int i = 0; i < ids.length; i++) {
                if (ids[i] == PICKER) {
                    colors[i] = mCustomColor;
                } else {
                    colors[i] = (int) ids[i];
                }
            }
            b.putIntArray(COLORS, colors);
        }
        return b;
    }


    protected class ColorAdapter extends AdvancedAdapter<Integer>{

        public ColorAdapter(int[] colors, boolean addCustomField){
            if (colors == null) colors = new int[0];

            Integer[] cs = new Integer[colors.length + (addCustomField ? 1 : 0)];
            for (int i = 0; i < colors.length; i++) {
                cs[i] = colors[i];
            }
            if (addCustomField){
                cs[cs.length-1] = PICKER;
            }
            setData(cs, Long::valueOf);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ColorView item;

            if (convertView instanceof ColorView){
                item = (ColorView) convertView;
            } else {
                item = new ColorView(getContext());
            }

            int color = getItem(position);

            if ( color == PICKER){
                item.setColor(mCustomColor);
                item.setStyle(ColorView.Style.PALETTE);
            } else {
                item.setColor(getItem(position));
                item.setStyle(ColorView.Style.CHECK);
            }

            @ColorInt int outline = getArgs().getInt(OUTLINE, ColorView.NONE);
            if (outline != NONE){
                float width = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                        2 /*dp*/, getResources().getDisplayMetrics());
                item.setOutlineWidth((int) width);
                item.setOutlineColor(outline);
            }

            return super.getView(position, item, parent);
        }
    }

}
