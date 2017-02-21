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

package eltos.simpledialogfragment.list;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.ArrayRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * A dialog that displays a list.
 * Choice modes such as SINGLE_CHOICE or MULTI_CHOICE can be activated.
 *
 * Created by eltos on 02.01.2017.
 */
public class SimpleListDialog extends CustomListDialog<SimpleListDialog> {

    private static final String TAG = "simpleListDialog";

    private final static String DATA_SET = TAG + "data_set";

    /**
     * Key for an <b>ArrayList&lt;String&gt;</b> returned by {@link #onResult}
     */
    public static final String SELECTED_LABELS = TAG + "selectedLabels";

    /**
     * Key for a <b>String</b> returned by {@link #onResult} in single choice mode
     */
    public static final String SELECTED_SINGLE_LABEL = TAG + "selectedSingleLabel";


    public static SimpleListDialog build(){
        return new SimpleListDialog();
    }

    /**
     * Populate the list with the labels provided
     *
     * @param context a context for resolving the string ids
     * @param labelsResourceIds a list of android string resource identifiers
     */
    public SimpleListDialog items(Context context, @StringRes int[] labelsResourceIds){
        ArrayList<SimpleListItem> list = new ArrayList<>(labelsResourceIds.length);
        for (int id : labelsResourceIds) {
            list.add(new SimpleListItem(context.getString(id), id));
        }
        return items(list);
    }

    /**
     * Populate the list using a string array resource id
     *
     * @param context a context for resolving the resource id
     * @param labelArrayResourceIds an android string array resource identifier
     */
    public SimpleListDialog items(Context context, @ArrayRes int labelArrayResourceIds){
        return items(context.getResources().getStringArray(labelArrayResourceIds));
    }

    /**
     * Populate the list with the labels provided
     *
     * @param labels a list of string to be displayed
     */
    public SimpleListDialog items(String[] labels){
        ArrayList<SimpleListItem> list = new ArrayList<>(labels.length);
        for (String label : labels) {
            list.add(new SimpleListItem(label, label.hashCode()));
        }
        return items(list);
    }

    /**
     * Populate the list with the labels provided
     * The corresponding ids can be used to identify which labels were selected
     *
     * @param labels a list of string to be displayed
     * @param ids a list of ids corresponding to the strings
     *
     * @throws IllegalArgumentException if the arrays length don't match
     */
    public SimpleListDialog items(String[] labels, long[] ids){
        if (labels.length != ids.length){
            throw new IllegalArgumentException("Length of ID-array must match label array length!");
        }
        ArrayList<SimpleListItem> list = new ArrayList<>(labels.length);
        for (int i = 0; i < labels.length && i < ids.length; i++) {
            list.add(new SimpleListItem(labels[i], ids[i]));
        }
        return items(list);
    }

    /**
     * Populate the list with the Items provided.
     * See {@link SimpleListItem} for further details
     *
     * @param items a list of {@link SimpleListItem}
     */
    public SimpleListDialog items(ArrayList<SimpleListItem> items){
        getArguments().putParcelableArrayList(DATA_SET, items);
        return this;
    }


    ArrayList<SimpleListItem> mData;

    @Override
    protected SimpleListAdapter onCreateAdapter() {

        int layout;
        switch (getArguments().getInt(CHOICE_MODE)) {
            case SINGLE_CHOICE:
                layout = android.R.layout.simple_list_item_single_choice;
                break;
            case MULTI_CHOICE:
                layout = android.R.layout.simple_list_item_multiple_choice;
                break;
            case NO_CHOICE:
            case SINGLE_CHOICE_DIRECT:
            default:
                layout = android.R.layout.simple_list_item_1;
                break;
        }


        mData = getArguments().getParcelableArrayList(DATA_SET);
        if (mData == null) mData = new ArrayList<>(0);

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        return new SimpleListAdapter(inflater, layout, mData);

    }


    @Override
    protected Bundle onResult(int which) {
        Bundle result = super.onResult(which);
        if (result != null) {

            ArrayList<Integer> positions = result.getIntegerArrayList(SELECTED_POSITIONS);
            if (positions != null) {
                ArrayList<String> labels = new ArrayList<>(positions.size());
                for (Integer pos : positions) {
                    labels.add(mData.get(pos).getString());
                }
                result.putStringArrayList(SELECTED_LABELS, labels);
            }

            if (result.containsKey(SELECTED_SINGLE_POSITION)) {
                result.putString(SELECTED_SINGLE_LABEL, mData.get(
                        result.getInt(SELECTED_SINGLE_POSITION)).getString());
            }

        }
        return result;
    }



    class SimpleListAdapter extends AdvancedAdapter<String> {

        private LayoutInflater mInflater;
        private int mLayout;

        SimpleListAdapter(LayoutInflater layoutInflater, @LayoutRes int layout,
                          ArrayList<SimpleListItem> data){
            mInflater = layoutInflater;
            mLayout = layout;
            ArrayList<Pair<String, Long>> dataAndIds = new ArrayList<>(data.size());
            for (SimpleListItem simpleListItem : data) {
                dataAndIds.add(new Pair<>(simpleListItem.getString(), simpleListItem.getId()));
            }
            setDataAndIds(dataAndIds);
        }

        AdvancedFilter mFilter = new AdvancedFilter(){

            @Override
            protected boolean matches(String object, @NonNull CharSequence constraint) {
                return matchesWord(object, constraint);
            }
        };

        @Override
        public AdvancedFilter getFilter() {
            return mFilter;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            TextView textView;

            if (convertView == null){
                convertView = mInflater.inflate(mLayout, parent, false);
                textView = (TextView) convertView.findViewById(android.R.id.text1);
                convertView.setTag(textView);
            } else {
                textView = (TextView) convertView.getTag();
            }

            textView.setText(getItem(position));



            return super.getView(position, convertView, parent);
        }

    }

}
