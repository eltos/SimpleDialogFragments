package eltos.simpledialogfragment;

import android.os.Bundle;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * Created by eltos on 02.01.2017.
 */
public abstract class CustomListDialog<This extends CustomListDialog<This>>
        extends CustomViewDialog<This>
        implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {


    public static final int NO_CHOICE = 0;
    public static final int SINGLE_CHOICE = 1;
    public static final int SINGLE_CHOICE_DIRECT = 11;
    public static final int MULTI_CHOICE = 2;

    /**
     * Note: ID's are only available in the results bundle
     * passed to {@link OnDialogResultListener#onResult} if the
     * adapter's {@link ListAdapter#hasStableIds} returns true
     */
    public static final String SELECTED_IDS = "customListDialog.selected_ids";
    public static final String SELECTED_POSITIONS = "customListDialog.selected_pos";
    public static final String SELECTED_SINGLE_ID = "customListDialog.selected_single_id";
    public static final String SELECTED_SINGLE_POSITION = "customListDialog.selected_single_pos";

    protected static final String CHOICE_MODE = "customListDialog.choice_mode";
    protected static final String CHOICE_MIN_COUNT = "customListDialog.choice_min";
    protected static final String CHOICE_MAX_COUNT = "customListDialog.choice_max";
    protected static final String INITIALLY_CHECKED_POSITIONS = "customListDialog.init_check";
    private static final String GRID = "customListDialog.grid";
    private static final String GRID_N = "customListDialog.grid_N";
    private static final String GRID_W = "customListDialog.grid_W";


    /**
     * Overwrite this method to provide a custom adapter
     *
     * @return the ListAdapter to use
     */
    protected abstract ListAdapter onCreateAdapter();


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
    private ListAdapter mAdapter;


    @Override
    protected View onCreateContentView(Bundle savedInstanceState) {
        // inflate and set your custom view here
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view;
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

        mAdapter = onCreateAdapter();
        mListView.setAdapter(mAdapter);

        switch (getArguments().getInt(CHOICE_MODE, ListView.CHOICE_MODE_NONE)) {
            case SINGLE_CHOICE:
            case SINGLE_CHOICE_DIRECT:
                mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                break;
            case MULTI_CHOICE:
                mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                break;
        }


        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                updatePosButton();

                CustomListDialog.this.onItemClick(parent, view, position, id);

                boolean checked = false;
                if (mListView.getCheckedItemPositions() != null) {
                    checked = mListView.getCheckedItemPositions().get(position);
                }
                if (checked && getArguments().getInt(CHOICE_MODE) == SINGLE_CHOICE_DIRECT){
                    pressPositiveButton();
                }
            }
        });
        mListView.setOnItemLongClickListener(this);

        if (savedInstanceState == null) {
            // initially checked items
            int[] set = getArguments().getIntArray(INITIALLY_CHECKED_POSITIONS);
            if (set != null) {
                for (int i : set) {
                    mListView.setItemChecked(i, true);
                }
            }
        }



        return view;
    }


    public boolean isItemChecked(int position){
        return mListView.isItemChecked(position);
    }

    protected AbsListView getListView(){
        return mListView;
    }

    @Override
    protected void onDialogShown() {
        updatePosButton();
    }

    private void updatePosButton(){
        if (getArguments().getInt(CHOICE_MODE) == NO_CHOICE) {
            setPositiveButtonEnabled(true);
        } else {
            int min = getArguments().getInt(CHOICE_MIN_COUNT, -1);
            int max = getArguments().getInt(CHOICE_MAX_COUNT, -1);
            setPositiveButtonEnabled(
                    (min < 0 || mListView.getCheckedItemCount() >= min) &&
                    (max < 0 || mListView.getCheckedItemCount() <= max));
        }
    }

    @Override
    protected Bundle onResult(int which) {
        Bundle result = new Bundle();
        if (getArguments().getInt(CHOICE_MODE) != NO_CHOICE) {
            ArrayList<Integer> checkedPositions = new ArrayList<>(mListView.getCheckedItemCount());
            SparseBooleanArray sparse = mListView.getCheckedItemPositions();
            if (sparse != null) {
                for (int i = 0; i < mListView.getCount(); i++) {
                    if (sparse.get(i)) {
                        checkedPositions.add(i);
                    }
                }
            }
            result.putIntegerArrayList(SELECTED_POSITIONS, checkedPositions);
            if (mAdapter.hasStableIds()) {
                result.putLongArray(SELECTED_IDS, mListView.getCheckedItemIds());
            }

        }
        if (getArguments().getInt(CHOICE_MODE) == SINGLE_CHOICE
                || getArguments().getInt(CHOICE_MODE) == SINGLE_CHOICE_DIRECT) {
            int pos = mListView.getCheckedItemPosition();
            result.putInt(SELECTED_SINGLE_POSITION, pos);
            if (mAdapter.hasStableIds()) {
                result.putLong(SELECTED_SINGLE_ID, mAdapter.getItemId(pos));
            }
        }
        return result;
    }


}
