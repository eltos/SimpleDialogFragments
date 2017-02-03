package eltos.simpledialogfragments;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import eltos.simpledialogfragment.CustomViewDialog;

/**
 *
 * Created by eltos on 02.02.2017.
 */
public class RecursiveDialog extends CustomViewDialog<RecursiveDialog> {


    private static final String I = "recursiveDialog.i";

    public static RecursiveDialog build(){
        return new RecursiveDialog();
    }

    private RecursiveDialog i(int i){ return setArg(I, i); }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        title("# " + getArguments().getInt(I, 1));
    }

    @Override
    protected View onCreateContentView(Bundle savedInstanceState) {
        Button b = new Button(getContext());
        b.setText(R.string.popup);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RecursiveDialog.build()
                        .i(getArguments().getInt(I, 1)+1)
                        .show(RecursiveDialog.this);


            }
        });

        return b;
    }
}
