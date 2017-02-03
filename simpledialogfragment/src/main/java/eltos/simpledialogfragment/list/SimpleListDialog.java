package eltos.simpledialogfragment.list;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eltos.simpledialogfragment.R;

/**
 *
 * Created by eltos on 02.01.2017.
 */
public class SimpleListDialog extends CustomListDialog<SimpleListDialog> {

    private final static String DATA_SET = "simpleListDialog.data_set";

    public static SimpleListDialog build(){
        return new SimpleListDialog();
    }

    public SimpleListDialog items(Context context, int[] labelsResourceIds){
        ArrayList<SimpleListItem> list = new ArrayList<>(labelsResourceIds.length);
        for (int id : labelsResourceIds) {
            list.add(new SimpleListItem(context.getString(id), id));
        }
        return items(list);
    }
    public SimpleListDialog items(String[] labels){
        ArrayList<SimpleListItem> list = new ArrayList<>(labels.length);
        for (String label : labels) {
            list.add(new SimpleListItem(label, label.hashCode()));
        }
        return items(list);
    }
    public SimpleListDialog items(String[] labels, long[] ids){
        ArrayList<SimpleListItem> list = new ArrayList<>(labels.length);
        for (int i = 0; i < labels.length && i < ids.length; i++) {
            list.add(new SimpleListItem(labels[i], ids[i]));
        }
        return items(list);
    }
    public SimpleListDialog items(ArrayList<SimpleListItem> items){
        getArguments().putParcelableArrayList(DATA_SET, items);
        return this;
    }



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

        ArrayList<SimpleListItem> data = getArguments().getParcelableArrayList(DATA_SET);
        if (data == null) data = new ArrayList<>(0);

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        return new SimpleListAdapter(inflater, layout, data);

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

        AdvancedFilter mFilter = new AdvancedFilter() {
            @Override
            protected boolean matches(String string, @NonNull CharSequence constraint) {
                if (string != null){
                    for (String word : string.split(" ")) {
                        if (word.startsWith(constraint.toString())){
                            return true;
                        }
                    }
                }
                return false;
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
