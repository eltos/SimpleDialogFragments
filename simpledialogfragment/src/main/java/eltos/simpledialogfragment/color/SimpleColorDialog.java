package eltos.simpledialogfragment.color;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import eltos.simpledialogfragment.R;
import eltos.simpledialogfragment.list.AdvancedAdapter;
import eltos.simpledialogfragment.list.CustomListDialog;


/**
 *
 * Created by eltos on 17.04.2016.
 */
public class SimpleColorDialog extends CustomListDialog<SimpleColorDialog> {


    public static final String COLOR = "simpleColorDialog.color";
    public static final int NONE = -1;

    protected static final @ColorInt int[] DEFAULT_COLORS = new int[]{
            0xfff44336, 0xffe91e63, 0xff9c27b0, 0xff673ab7,
            0xff3f51b5, 0xff2196f3, 0xff03a9f4, 0xff00bcd4,
            0xff009688, 0xff4caf50, 0xff8bc34a, 0xffcddc39,
            0xffffeb3b, 0xffffc107, 0xffff9800, 0xffff5722,
            0xff795548, 0xff9e9e9e, 0xff607d8b
    };


    private static final String COLORS = "simpleColorDialog.colors";


    public static SimpleColorDialog build(){
        return new SimpleColorDialog()
                .grid().gridColumnWidth(R.dimen.dialog_color_item_size)
                .choiceMode(SINGLE_CHOICE)
                .choiceMin(1)
                .colors(DEFAULT_COLORS);
    }

    public SimpleColorDialog colors(@ColorInt int[] colors){
        getArguments().putIntArray(COLORS, colors);
        return this;
    }

    public SimpleColorDialog colorPreset(@ColorInt int color){
        getArguments().putInt(COLOR, color);
        return this;
    }



    @Override
    protected AdvancedAdapter onCreateAdapter() {

        int[] colors = getArguments().getIntArray(COLORS);
        if (colors == null) colors = new int[0];

        if (getArguments().containsKey(COLOR)){
            int preset = getArguments().getInt(COLOR, NONE);
            for (int i = 0; i < colors.length; i++) {
                if (colors[i] == preset){
                    choicePreset(i);
                    break;
                }
            }
        }

        /** Selector provided by {@link ColorView} **/
        getListView().setSelector(new ColorDrawable(Color.TRANSPARENT));

        return new ColorAdapter(colors);
    }


    @Override
    protected Bundle onResult(int which) {
        Bundle b = super.onResult(which);
        b.putInt(COLOR, (int) b.getLong(SELECTED_SINGLE_ID));
        return b;
    }


    private class ColorAdapter extends AdvancedAdapter<Integer>{

        ColorAdapter(int[] colors){
            if (colors != null) {
                ArrayList<Pair<Integer, Long>> cs = new ArrayList<>(colors.length);
                for (int color : colors) {
                    cs.add(new Pair<>(color, (long) color));
                }
                setDataAndIds(cs);
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ColorView item;

            if (convertView instanceof ColorView){
                item = (ColorView) convertView;
            } else {
                item = new ColorView(getContext());
            }

            item.setColor(getItem(position));

            return super.getView(position, item, parent);
        }
    }

//    private class ColorAdapter extends BaseAdapter{
//
//        @NonNull
//        private int[] mColors;
//
//        ColorAdapter(int[] colors){
//            mColors = colors == null ? new int[0] : colors;
//        }
//
//        @Override
//        public int getCount() {
//            return mColors.length;
//        }
//
//        @Override
//        public Integer getItem(int position) {
//            if (position >= 0 && position < mColors.length){
//                return mColors[position];
//            }
//            return NONE;
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return getItem(position);
//        }
//
//        @Override
//        public boolean hasStableIds() {
//            return true;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            ColorView item;
//
//            if (convertView instanceof ColorView){
//                item = (ColorView) convertView;
//            } else {
//                item = new ColorView(getContext());
//            }
//
//            item.setColor(getItem(position));
//
//            return item;
//        }
//    }

}
