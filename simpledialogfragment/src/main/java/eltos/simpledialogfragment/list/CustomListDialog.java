package eltos.simpledialogfragment.list;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.DimenRes;
import android.support.annotation.StringRes;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Filterable;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import eltos.simpledialogfragment.CustomViewDialog;
import eltos.simpledialogfragment.R;

/**
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

    public static final String SELECTED_IDS = TAG + "selectedIds";
    public static final String SELECTED_POSITIONS = TAG + "selectedPos";
    public static final String SELECTED_SINGLE_ID = TAG + "selectedSingleId";
    public static final String SELECTED_SINGLE_POSITION = TAG + "selectedSinglePos";

    protected static final String CHOICE_MODE = TAG + "choiceMode";
    protected static final String CHOICE_MIN_COUNT = TAG + "choiceMin";
    protected static final String CHOICE_MAX_COUNT = TAG + "choiceMax";
    protected static final String INITIALLY_CHECKED_POSITIONS = TAG + "initCheck";
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


    private boolean pmFlag = false;

    public This choiceMode(int mode){
        if (!pmFlag && mode == SINGLE_CHOICE_DIRECT){
            pos(null);
        }
        return setArg(CHOICE_MODE, mode);
    }
    public This choiceMin(int count){ return setArg(CHOICE_MIN_COUNT, count); }
    public This choiceMax(int count){ return setArg(CHOICE_MAX_COUNT, count); }
    public This choicePreset(List<Integer> positions){
        int[] p = new int[positions.size()];
        for (int i = 0; i < p.length; i++) {
            p[i] = positions.get(i);
        }
        return choicePreset(p);
    }
    @SuppressWarnings("unchecked cast")
    public This choicePreset(int[] positions){
        getArguments().putIntArray(INITIALLY_CHECKED_POSITIONS, positions);
        return (This) this;
    }
    public This choicePreset(int position){
        return choicePreset(new int[]{position});
    }
    public This grid(){
        return setArg(GRID, true);
    }
    public This gridNumColumn(int numColumns){
        return setArg(GRID_N, numColumns);
    }
    public This gridColumnWidth(@DimenRes int columnWidthDimenResId){
        return setArg(GRID_W, columnWidthDimenResId);
    }
    public This divider(boolean show){ return setArg(SHOW_DIVIDER, show); }

    /**
     * Note that the adapter has to implement {@link Filterable} and has to provide stable ids
     * (if choice mode is one of {@link #SINGLE_CHOICE} or {@link #MULTI_CHOICE}
     * for this to work
     */
    public This filterable(boolean enabled){ return setArg(FILTER, enabled); }
    public This emptyText(String title){ return setArg(EMPTY_TEXT, title); }
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


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        return false;
    }





    private AbsListView mListView;
    private AdvancedAdapter mAdapter;
    private EditText mFilterEditText;


    @Override
    protected View onCreateContentView(Bundle savedInstanceState) {
        // inflate and set your custom view here
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view;
        TextView mEmptyView;
        if (getArguments().containsKey(GRID)){
            view = inflater.inflate(R.layout.dialog_grid, null);
            mListView = (GridView) view.findViewById(R.id.gridView);
            if (getArguments().containsKey(GRID_W)){
                ((GridView) mListView).setColumnWidth(getResources().getDimensionPixelSize(
                        getArguments().getInt(GRID_W)));
            }
            ((GridView) mListView).setNumColumns(getArguments().getInt(GRID_N, GridView.AUTO_FIT));
        } else {
            view = inflater.inflate(R.layout.dialog_list, null);
            mListView = (ListView) view.findViewById(R.id.listView);
        }

        mFilterEditText = (EditText) view.findViewById(R.id.filter);
        mEmptyView = (TextView) view.findViewById(R.id.emptyView);

        mEmptyView.setText(getArgString(EMPTY_TEXT));

        mAdapter = onCreateAdapter();
        mListView.setAdapter(mAdapter);
        mListView.setEmptyView(mEmptyView);

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
                mListView.invalidateViews();
                //mAdapter.notifyDataSetChanged();

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
            int[] set = getArguments().getIntArray(INITIALLY_CHECKED_POSITIONS);
            if (set != null) {
                for (int i : set) {
                    mAdapter.setItemChecked(i, true);
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
