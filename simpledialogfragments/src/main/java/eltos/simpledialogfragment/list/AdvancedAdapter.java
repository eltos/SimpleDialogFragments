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
import android.content.res.TypedArray;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Checkable;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by eltos on 04.12.2016.
 *
 * This adapter keeps track of checked items even if they are currently not visible
 * due to filtering.
 *
 * When extending this class, note the following:
 * - Set the underlying data set via {@link AdvancedAdapter#setData} or {@link AdvancedAdapter#setDataAndIds}
 * - Overwrite {@link AdvancedAdapter#getView}. You can either return with the super-call, that will
 *   automatically care for the checked state if the View is an instance of Checkable
 *   or set checked state yourself by using {@link AdvancedAdapter#isItemChecked}
 *
 * In your activity refer to the {@link AdvancedAdapter#isItemChecked} and other functions to get
 * checked items rather than using the functions of the ListView!
 *
 */
public abstract class AdvancedAdapter<T> extends BaseAdapter implements Filterable {

    public static final int CHOICE_MODE_NONE = 0;
    public static final int CHOICE_MODE_SINGLE = 1;
    public static final int CHOICE_MODE_MULTIPLE = 2;
    private int mChoiceMode = CHOICE_MODE_MULTIPLE;
    private boolean mNoAnimations = false;
    private @ColorInt Integer mDefaultHighlightColor = null;

    class Item {
        T object;
        Long id = null;
        boolean checked = false;
        Item(T object){
            this.object = object;
        }
        Item(T object, boolean checked){
            this(object);
            this.checked = checked;
        }
        Item(T object, Long id){
            this(object);
            this.id = id;
        }

        public Long getId() {
            return id != null ? id : hashCode();
        }
    }

    private ArrayList<Item> mFilteredItems = new ArrayList<>();
    private ArrayList<Item> mItems = new ArrayList<>();
    //    private OnFilteredCallback mOnFilteredCallback = null;
    private CharSequence mFilterConstraint = null;


    public interface ItemIdentifier<Item> {
        @Nullable
        Long getIdForItem(Item item);
    }


    /**
     * Set this adapters data
     *
     * @param list a list of objects to be maintained by this adapter
     */
    public void setData(T[] list){
        setData(new ArrayList<>(Arrays.asList(list)));
    }

    /**
     * Set this adapters data
     *
     * @param list an array-list of objects to be maintained by this adapter
     */
    public void setData(ArrayList<? extends T> list){
        mItems.clear();
        for (T t : list) {
            mItems.add(new Item(t));
        }
        mFilteredItems = new ArrayList<>(mItems);
        filterItems();
    }

    /**
     * Set this adapters data and ids
     *
     * @param list a list of objects to be maintained by this adapter
     * @param identifier an Identifier returning a unique id for every item
     */
    public void setData(T[] list, ItemIdentifier<T> identifier){
        setData(new ArrayList<>(Arrays.asList(list)), identifier);
    }

    /**
     * Set this adapters data and ids
     *
     * @param list an array-list of objects to be maintained by this adapter
     * @param identifier an Identifier returning a unique id for every item
     */
    public void setData(ArrayList<? extends T> list, ItemIdentifier<T> identifier){
        mItems.clear();
        for (T t : list) {
            mItems.add(new Item(t, identifier.getIdForItem(t)));
        }
        mFilteredItems = new ArrayList<>(mItems);
        filterItems();
    }

    public void setDataAndIds(T[] list, long[] ids){
        mItems.clear();
        ArrayList<Pair<T, Long>> pairs = new ArrayList<>(list.length);
        for (int i = 0; i < list.length && i < ids.length; i++) {
            mItems.add(new Item(list[i], ids[i]));
        }
        mFilteredItems = new ArrayList<>(mItems);
        filterItems();
    }

    public void setDataAndIds(ArrayList<Pair<T, Long>> list){
        mItems.clear();
        for (Pair<T, Long> pair : list) {
            mItems.add(new Item(pair.first, pair.second));
        }
        mFilteredItems = new ArrayList<>(mItems);
        filterItems();
    }

    /**
     * Get the data maintained by this adapter
     *
     * @return an array-list of the data
     */
    public ArrayList<T> getData(){
        ArrayList<T> list = new ArrayList<>(mItems.size());
        for (Item item : mItems) {
            list.add(item.object);
        }
        return list;
    }


    /**
     * Overwrite this method to return your AdvancedFilter here
     *
     * @return an instance of AdvancedFilter for filtering data
     */
    @Nullable
    @Override
    public AdvancedFilter getFilter(){
        return null;
    }

    /**
     * Defines the choice behavior for the list. By default, lists do not have any choice behavior
     * ({@link AdvancedAdapter#CHOICE_MODE_NONE}). By setting the choiceMode to
     * {@link AdvancedAdapter#CHOICE_MODE_SINGLE}, the list allows up to one item to be checked
     * Using {@link AdvancedAdapter#CHOICE_MODE_MULTIPLE}, any number of items may be checked.
     *
     * @param choiceMode One of {@link AdvancedAdapter#CHOICE_MODE_NONE},
     * {@link AdvancedAdapter#CHOICE_MODE_SINGLE} or {@link AdvancedAdapter#CHOICE_MODE_MULTIPLE}
     */
    public void setChoiceMode(int choiceMode){
        mChoiceMode = choiceMode;
        if (mChoiceMode == CHOICE_MODE_NONE){
            // un-check all items
            setAllItemsChecked(false);

        } else if (mChoiceMode == CHOICE_MODE_SINGLE && getCheckedItemCount() > 1){
            // un-check all but the first checked item
            boolean single = true;
            for (Item item : mItems) {
                if (single && item.checked) { single = false; continue; }
                item.checked = false;
            }
            filterItems();
        }
    }


    @Override
    public int getCount() {
        return mFilteredItems.size();
    }

    @Override
    public T getItem(int filteredPosition) {
        return mFilteredItems.get(filteredPosition).object;
    }

    @Override
    public long getItemId(int filteredPosition) {
        return mFilteredItems.get(filteredPosition).getId();
    }


    public void setItemChecked(int filteredPosition, boolean checked) {
        if (mChoiceMode != CHOICE_MODE_NONE) {
            if (checked && mChoiceMode == CHOICE_MODE_SINGLE){
                setAllItemsChecked(false);
            }
            mFilteredItems.get(filteredPosition).checked = checked;
        }
    }

    public void toggleChecked(int filteredPosition){
        if (mChoiceMode == CHOICE_MODE_MULTIPLE){
            setItemChecked(filteredPosition, !isItemChecked(filteredPosition));
        } else if (mChoiceMode == CHOICE_MODE_SINGLE){
            setItemChecked(filteredPosition, true);
        }
    }

    public void setAllItemsChecked(boolean checked){
        if (!checked || mChoiceMode == CHOICE_MODE_MULTIPLE) {
            for (Item i : mItems) {
                i.checked = checked;
            }
        }
    }

    public void setItemChecked(long id, boolean checked) {
        if (mChoiceMode != CHOICE_MODE_NONE) {
            if (checked && mChoiceMode == CHOICE_MODE_SINGLE){
                setAllItemsChecked(false);
            }
            for (Item item : mItems) {
                if (item.getId() == id){
                    item.checked = checked;
                    break;
                }
            }
        }
    }

    public void setItemsCheckedFromIds(ArrayList<Long> checkedItemIds){
        for (Item item : mItems) {
            item.checked = checkedItemIds.contains(item.getId());
        }
    }

    public void setItemsCheckedFromIds(long[] checkedItemIds){
        setAllItemsChecked(false);
        for (long id : checkedItemIds) {
            setItemChecked(id, true);
        }
    }
    

    public boolean isItemChecked(int filteredPosition) {
        return mFilteredItems.get(filteredPosition).checked;
    }

    public int getCheckedItemCount(){
        int count = 0;
        for (Item i : mItems){
            if (i.checked) count ++;
        }
        return count;
    }

    public ArrayList<T> getCheckedItems(){
        ArrayList<T> list = new ArrayList<>(getCheckedItemCount());
        for (Item i : mItems){
            if (i.checked) list.add(i.object);
        }
        return list;
    }

    public ArrayList<Integer> getCheckedItemOriginalPositions(){
        ArrayList<Integer> result = new ArrayList<>(getCheckedItemCount());
        for (int i = 0; i < mItems.size(); i++) {
            if (mItems.get(i).checked) result.add(i);
        }
        return result;
    }

    public ArrayList<Long> getCheckedItemIds(){
        ArrayList<Long> result = new ArrayList<>(getCheckedItemCount());
        for (Item item : mItems) {
            if (item.checked) result.add(item.getId());
        }
        return result;
    }


    protected void filterItems(){
        if (getFilter() != null){
            getFilter().filter(mFilterConstraint);
        }
    }


//    public interface OnFilteredCallback{
//        void onFiltered(int count);
//    }

    /**
     * Highlights everything that matched the current filter (if any) in text
     *
     * @param text the text to highlight
     * @param context a context to get the default highlight color from
     * @return a spannable string
     */
    protected Spannable highlight(String text, Context context) {
        if (mDefaultHighlightColor == null){
            TypedArray array = context.obtainStyledAttributes(new int[]{
                    android.R.attr.textColorHighlight});
            mDefaultHighlightColor = array.getColor(0, 0x6633B5E5);
            array.recycle();
        }
        return highlight(text, mDefaultHighlightColor);
    }

    /**
     * Highlights everything that matched the current filter (if any) in text
     *
     * @param text the text to highlight
     * @param color the highlight color
     * @return a spannable string
     */
    protected Spannable highlight(String text, int color) {
        if (text == null) return null;

        Spannable highlighted = new SpannableStringBuilder(text);
        AdvancedFilter filter = getFilter();

        if (filter == null || filter.mPattern == null){
            return highlighted;
        }

        Matcher matcher = filter.mPattern.matcher(text);

        while (matcher.find()){
            highlighted.setSpan(new BackgroundColorSpan(color), matcher.start(),
                    matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            );
        }

        return highlighted;
    }





    /**
     * An advanced filter where only the {@link AdvancedFilter#matches} method needs
     * to be overwritten
     *
     */
    public abstract class AdvancedFilter extends Filter {
        private @Nullable CharSequence mConstraint;
        private @Nullable Pattern mPattern;
        private boolean mIgnoreCase = true;
        private boolean mMatchWordBeginning = true;

        public AdvancedFilter(){
            this(true, true);
        }

        /**
         * The flags specified here are used in the default {@link AdvancedFilter#matches} and
         * {@link AdvancedAdapter#highlight} methods.
         *
         * @param ignoreCase whether default matching is not case-sensitive
         * @param matchWordBeginning whether default matching is performed only at the beginning of words
         */
        public AdvancedFilter(boolean ignoreCase, boolean matchWordBeginning){
            mIgnoreCase = ignoreCase;
            mMatchWordBeginning = matchWordBeginning;
        }

        protected boolean isIgnoreCase() {
            return mIgnoreCase;
        }

        protected boolean isMatchWordBeginning() {
            return mMatchWordBeginning;
        }

        protected abstract boolean matches(T object, @NonNull CharSequence constraint);

        /**
         * Use {@link AdvancedFilter#matches(String)} instead
         *
         * @param string string to search in
         * @param constraint string to search for
         * @return true if string contains the constraint, false otherwise
         */
        @Deprecated
        protected boolean matchesWord(String string, @NonNull CharSequence constraint) {
            return string != null && Pattern.compile((mMatchWordBeginning ? "\\b" : "") +
                    "(" + constraint + ")", mIgnoreCase ? Pattern.CASE_INSENSITIVE : 0)
                    .matcher(string).find();
        }

        /**
         * Simple string matcher that uses the current constraint and flags as specified
         * upon creation.
         *
         * @param string the string to search in
         * @return true if at least one match is found
         */
        protected boolean matches(String string) {
            return string != null && mPattern != null && mPattern.matcher(string).find();
        }


        @Override @Nullable
        protected FilterResults performFiltering(@Nullable CharSequence constraint) {
            mConstraint = constraint;
            if (constraint == null || constraint.length() == 0) {
                mPattern = null;
                return null;
            }
            mPattern = Pattern.compile((mMatchWordBeginning ? "\\b" : "") +
                    "(" + mConstraint + ")", mIgnoreCase ? Pattern.CASE_INSENSITIVE : 0);

            ArrayList<Item> filteredResults = new ArrayList<>();
            for (Item item : mItems) {
                if (matches(item.object, constraint)) {
                    filteredResults.add(item);
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredResults;
            results.count = filteredResults.size();
            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, @Nullable FilterResults results) {
            mFilterConstraint = constraint;
            mFilteredItems.clear();
            if (results != null && results.values != null) {
                mFilteredItems.addAll((Collection<? extends Item>) results.values);
            } else {
                mFilteredItems.addAll(mItems);
            }
            notifyDataSetChanged();
            mNoAnimations = true;
//            if (mOnFilteredCallback != null) {mOnFilteredCallback.onFiltered(getCount()); }
        }
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        mNoAnimations = false;
    }


    //    public void setOnFilteredCallback(@Nullable OnFilteredCallback callback){
//        mOnFilteredCallback = callback;
//    }




    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // apply checked state
        if (convertView instanceof Checkable){
            ((Checkable) convertView).setChecked(isItemChecked(position));
            if (mNoAnimations){ // suppresses the check animation when filtering
                convertView.jumpDrawablesToCurrentState();
            }
        }

        return convertView;
    }
}
