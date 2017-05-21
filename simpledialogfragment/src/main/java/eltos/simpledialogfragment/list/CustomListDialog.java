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
import android.support.annotation.DimenRes;
import android.support.annotation.StringRes;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import eltos.simpledialogfragment.CustomViewDialog;
import eltos.simpledialogfragment.R;

/**
 * A dialog that displays a list of items.
 * MULTI_CHOICE and SINGLE_CHOICE modes are supported.
 * Specify your custom adapter
 *
 * Result:
 *      SELECTED_POSITIONS          Integer ArrayList   selected item positions
 *      SELECTED_IDS                Long[]              selected item ids
 * In SINGLE_CHOICE and SINGLE_CHOICE_DIRECT mode also:
 *      SELECTED_SINGLE_POSITION    int                 selected item position
 *      SELECTED_SINGLE_ID          long                selected item id
 *
 * Created by eltos on 02.01.2017.
 */
public abstract class CustomListDialog<This extends CustomListDialog<This>>
        extends CustomViewDialog<This>
        implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private static final String TAG = "CustomListDialog";

    public static final int NO_CHOICE = 0;
    public static final int SINGLE_CHOICE = 1;
    public static final int SINGLE_CHOICE_DIRECT = 11;
    public static final int MULTI_CHOICE = 2;

    /**
     * Key for a <b>long[]</b> returned by {@link CustomListDialog#onResult}
     */
    public static final String SELECTED_IDS = TAG + "selectedIds";

    /**
     * Key for an <b>ArrayList&lt;Integer&gt;</b> returned by {@link CustomListDialog#onResult}
     */
    public static final String SELECTED_POSITIONS = TAG + "selectedPos";

    /**
     * Key for a <b>long</b> returned by {@link CustomListDialog#onResult} in single choice mode
     */
    public static final String SELECTED_SINGLE_ID = TAG + "selectedSingleId";

    /**
     * Key for an <b>int</b> returned by {@link CustomListDialog#onResult} in single choice mode
     */
    public static final String SELECTED_SINGLE_POSITION = TAG + "selectedSinglePos";

    protected static final String CHOICE_MODE = TAG + "choiceMode";
    protected static final String CHOICE_MIN_COUNT = TAG + "choiceMin";
    protected static final String CHOICE_MAX_COUNT = TAG + "choiceMax";
    protected static final String INITIALLY_CHECKED_POSITIONS = TAG + "initCheckPos";
    protected static final String INITIALLY_CHECKED_IDS = TAG + "initCheckId";
    private static final String GRID = TAG + "grid";
    private static final String GRID_N = TAG + "gridN";
    private static final String GRID_W = TAG + "gridW";
    private static final String SHOW_DIVIDER = TAG + "showDivider";
    private static final String FILTER = TAG + "filter";
    private static final String EMPTY_TEXT = TAG + "emptyText";


    /**
     * Overwrite this method to provide a custom adapter
     *
     * @return the ListAdapter to use
     */
    protected abstract AdvancedAdapter onCreateAdapter();


    // used to hide the default visible positive button if it wasn't explicitly set
    private boolean pmFlag = false;

    /**
     * Sets the list choice mode
     * @param mode one of {@link CustomListDialog#NO_CHOICE}, {@link CustomListDialog#SINGLE_CHOICE},
     * {@link CustomListDialog#SINGLE_CHOICE_DIRECT} or {@link CustomListDialog#MULTI_CHOICE}
     */
    public This choiceMode(int mode){
        if (!pmFlag && mode == SINGLE_CHOICE_DIRECT){
            pos(null);
        }
        return setArg(CHOICE_MODE, mode);
    }

    /**
     * Sets the minimum required choices for the positive button to become enabled
     *
     * @param count the minimum required choices
     */
    public This choiceMin(int count){ return setArg(CHOICE_MIN_COUNT, count); }

    /**
     * Sets the maximum allowed choices for the positive button to become enabled
     *
     * @param count the maximum allowed choices
     */
    public This choiceMax(int count){ return setArg(CHOICE_MAX_COUNT, count); }

    /**
     * Sets the initially checked item positions
     *
     * @param positions the initially checked positions
     */
    public This choicePreset(List<Integer> positions){
        int[] p = new int[positions.size()];
        for (int i = 0; i < p.length; i++) {
            p[i] = positions.get(i);
        }
        return choicePreset(p);
    }

    /**
     * Sets the initially checked item positions
     *
     * @param positions the initially checked positions
     */
    @SuppressWarnings("unchecked cast")
    public This choicePreset(int[] positions){
        getArguments().putIntArray(INITIALLY_CHECKED_POSITIONS, positions);
        return (This) this;
    }

    /**
     * Sets the initially checked item position
     *
     * @param position the initially checked position
     */
    public This choicePreset(int position){
        return choicePreset(new int[]{position});
    }

    /**
     * Sets the initially checked items by their ids
     *
     * @param ids the initially checked item ids
     */
    public This choiceIdPreset(List<Long> ids){
        long[] p = new long[ids.size()];
        for (int i = 0; i < p.length; i++) {
            p[i] = ids.get(i);
        }
        return choiceIdPreset(p);
    }

    /**
     * Sets the initially checked items by their ids
     *
     * @param ids the initially checked item ids
     */
    @SuppressWarnings("unchecked cast")
    public This choiceIdPreset(long[] ids){
        getArguments().putLongArray(INITIALLY_CHECKED_IDS, ids);
        return (This) this;
    }

    /**
     * Sets the initially checked item by its id
     *
     * @param id the initially checked item id
     */
    public This choiceIdPreset(long id){
        return choiceIdPreset(new long[]{id});
    }

    /**
     * Change the list into a grid list view (grid mode)
     */
    public This grid(){
        return setArg(GRID, true);
    }

    /**
     * Specifies the number of columns of this grid view (only if in grid mode)
     *
     * @param numColumns the number of columns
     */
    public This gridNumColumn(int numColumns){
        return setArg(GRID_N, numColumns);
    }

    /**
     * Specifies the column with of this grid view (only if in grid mode)
     *
     * @param columnWidthDimenResId the with as an android dimension resource identifier
     */
    public This gridColumnWidth(@DimenRes int columnWidthDimenResId){
        return setArg(GRID_W, columnWidthDimenResId);
    }

    /**
     * Sets the visibility of the divider
     *
     * @param show weather to display a divider or not
     */
    public This divider(boolean show){ return setArg(SHOW_DIVIDER, show); }

    /**
     * If set to true, show an input field at the to of the list and allow the user
     * to filter the list
     *
     * @param enabled weather to allow filtering or not
     */
    public This filterable(boolean enabled){ return setArg(FILTER, enabled); }

    /**
     * Sets a string to be displayed if no items are currently visible
     *
     * @param title the string to be displayed
     */
    public This emptyText(String title){ return setArg(EMPTY_TEXT, title); }

    /**
     * Sets a string to be displayed if no items are currently visible
     *
     * @param titleResourceId the android string resource to be displayed
     */
    public This emptyText(@StringRes int titleResourceId){ return setArg(EMPTY_TEXT, titleResourceId); }


    @Override
    public This pos(String positiveButton) {
        pmFlag = true;
        return super.pos(positiveButton);
    }

    @Override
    public This pos(int positiveButtonResourceId) {
        pmFlag = true;
        return super.pos(positiveButtonResourceId);
    }


    /**
     * Callback method to be invoked when an item in this AdapterView has
     * been clicked.
     * Implementers can call Adapter#getItem(position) if they need
     * to access the data associated with the selected item.
     *
     * @param parent The AdapterView where the click happened.
     * @param view The view within the AdapterView that was clicked (this
     *            will be a view provided by the adapter)
     * @param position The position of the view in the adapter.
     * @param id The row id of the item that was clicked.
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    /**
     * Callback method to be invoked when an item in this view has been
     * clicked and held.
     *
     * Implementers can call Adapter#getItem(position) if they need to access
     * the data associated with the selected item.
     *
     * @param parent The AbsListView where the click happened
     * @param view The view within the AbsListView that was clicked
     * @param position The position of the view in the list
     * @param id The row id of the item that was clicked
     *
     * @return true if the callback consumed the long click, false otherwise
     */
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        return false;
    }





    private AbsListView mListView;
    private AdvancedAdapter<?> mAdapter;
    private EditText mFilterEditText;


    @Override
    protected View onCreateContentView(Bundle savedInstanceState) {

        View view;
        if (getArguments().containsKey(GRID)){
            view = inflate(R.layout.simpledialogfragment_grid);
            mListView = (GridView) view.findViewById(R.id.gridView);
            if (getArguments().containsKey(GRID_W)){
                ((GridView) mListView).setColumnWidth(getResources().getDimensionPixelSize(
                        getArguments().getInt(GRID_W)));
            }
            ((GridView) mListView).setNumColumns(getArguments().getInt(GRID_N, GridView.AUTO_FIT));
        } else {
            view = inflate(R.layout.simpledialogfragment_list);
            mListView = (ListView) view.findViewById(R.id.listView);
        }

        mFilterEditText = (EditText) view.findViewById(R.id.filter);
        TextView emptyView = (TextView) view.findViewById(R.id.emptyView);

        emptyView.setText(getArgString(EMPTY_TEXT));

        mAdapter = onCreateAdapter();
        mListView.setAdapter(mAdapter);
        mListView.setEmptyView(emptyView);

        switch (getArguments().getInt(CHOICE_MODE, ListView.CHOICE_MODE_NONE)) {
            case SINGLE_CHOICE:
            case SINGLE_CHOICE_DIRECT:
                mAdapter.setChoiceMode(AdvancedAdapter.CHOICE_MODE_SINGLE);
                break;
            case MULTI_CHOICE:
                mAdapter.setChoiceMode(AdvancedAdapter.CHOICE_MODE_MULTIPLE);
                break;
        }

        mFilterEditText.setVisibility(View.GONE);
        if (getArguments().getBoolean(FILTER)){
            mFilterEditText.setVisibility(View.VISIBLE);
            mFilterEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}
                @Override
                public void afterTextChanged(Editable s) {
                    if (mAdapter.getFilter() != null){
                        mAdapter.getFilter().filter(s);
                    }
                }
            });
        }




        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                CustomListDialog.this.onItemClick(parent, view, position, id);

                mAdapter.toggleChecked(position);
                //mListView.invalidateViews();
                mAdapter.notifyDataSetChanged();

                updatePosButton();

                if (mAdapter.getCheckedItemCount() > 0 &&
                        getArguments().getInt(CHOICE_MODE) == SINGLE_CHOICE_DIRECT){
                    pressPositiveButton();
                }
            }
        });
        mListView.setOnItemLongClickListener(this);
        if (mListView instanceof ListView && !getArguments().getBoolean(SHOW_DIVIDER)){
            ((ListView) mListView).setDivider(null);
        }

        if (savedInstanceState == null) {
            // initially checked items
            if (getArguments().containsKey(INITIALLY_CHECKED_IDS)){
                long[] idSet = getArguments().getLongArray(INITIALLY_CHECKED_IDS);
                mAdapter.setItemsCheckedFromIds(idSet);
            }
            if (getArguments().containsKey(INITIALLY_CHECKED_POSITIONS)) {
                int[] set = getArguments().getIntArray(INITIALLY_CHECKED_POSITIONS);
                if (set != null) {
                    for (int i : set) {
                        mAdapter.setItemChecked(i, true);
                    }
                }
            }
        } else {
            // preserved checked states
            ArrayList<Integer> checked = savedInstanceState.getIntegerArrayList(SELECTED_POSITIONS);
            if (checked != null){
                for (Integer i : checked) {
                    mAdapter.setItemChecked(i, true);
                }
            }
        }



        return view;
    }


    protected AbsListView getListView(){
        return mListView;
    }

    protected void notifyDataSetChanged(){
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDialogShown() {
        updatePosButton();
        if (getArguments().getBoolean(FILTER)){
            // show keyboard
            InputMethodManager imm = (InputMethodManager) getActivity()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(mFilterEditText, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private void updatePosButton(){
        if (getArguments().getInt(CHOICE_MODE) == NO_CHOICE) {
            setPositiveButtonEnabled(true);
        } else {
            int min = getArguments().getInt(CHOICE_MIN_COUNT, -1);
            int max = getArguments().getInt(CHOICE_MAX_COUNT, -1);
            setPositiveButtonEnabled(
                    (min < 0 || mAdapter.getCheckedItemCount() >= min) &&
                    (max < 0 || mAdapter.getCheckedItemCount() <= max));
        }
    }

    @Override
    protected Bundle onResult(int which) {
        Bundle result = new Bundle();
        ArrayList<Integer> checked = mAdapter.getCheckedItemOriginalPositions();
        ArrayList<Long> checkedIds = mAdapter.getCheckedItemIds();

        if (getArguments().getInt(CHOICE_MODE) != NO_CHOICE) {
            result.putIntegerArrayList(SELECTED_POSITIONS, checked);
            long[] checkedIdsArray = new long[checkedIds.size()];
            for (int i = 0; i < checkedIdsArray.length; i++) {
                checkedIdsArray[i] = checkedIds.get(i);
            }
            result.putLongArray(SELECTED_IDS, checkedIdsArray);

        }
        if (getArguments().getInt(CHOICE_MODE) == SINGLE_CHOICE
                || getArguments().getInt(CHOICE_MODE) == SINGLE_CHOICE_DIRECT) {
            if (checked.size() >= 1){
                result.putInt(SELECTED_SINGLE_POSITION, checked.get(0));
            }
            if (checkedIds.size() >= 1){
                result.putLong(SELECTED_SINGLE_ID, checkedIds.get(0));
            }
        }
        return result;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putIntegerArrayList(SELECTED_POSITIONS, mAdapter.getCheckedItemOriginalPositions());
        super.onSaveInstanceState(outState);
    }
}
