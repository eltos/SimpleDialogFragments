package eltos.simpledialogfragment;

import android.content.Context;
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
        String[] labels = new String[labelsResourceIds.length];
        for (int i = 0; i < labels.length; i++) {
            labels[i] = context.getString(labelsResourceIds[i]);
        }
        return items(labels);
    }
    public SimpleListDialog items(String[] labels){
        ArrayList<SimpleListItem> list = new ArrayList<>(labels.length);
        for (String label : labels) {
            list.add(new SimpleListItem(label));
        }
        getArguments().putParcelableArrayList(DATA_SET, list);
        return this;
    }


    @Override
    protected ListAdapter onCreateAdapter() {

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

        List<Map<String, String>> map = new ArrayList<>(data.size());
        for (SimpleListItem item : data) {
            Map<String, String> e = new HashMap<>(1);
            e.put("label", item.toString());
            map.add(e);
        }

        return new SimpleAdapter(getContext(), map, layout,
                new String[]{"label"}, new int[]{android.R.id.text1});

    }

}
