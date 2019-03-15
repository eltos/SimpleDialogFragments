package eltos.simpledialogfragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import eltos.simpledialogfragment.CustomViewDialog;
import eltos.simpledialogfragment.SimpleDialog;

/**
 *
 * Created by eltos on 02.02.2017.
 */
public class RecursiveDialog extends CustomViewDialog<RecursiveDialog>
        implements SimpleDialog.OnDialogResultListener {


    private static final String I = "recursiveDialog.i";
    private static final String RECURSIVE_DIALOG = "recursiveDialog";

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
                        .neg()
                        .show(RecursiveDialog.this, RECURSIVE_DIALOG);


            }
        });

        return b;
    }


    @Override
    public boolean onResult(@NonNull String dialogTag, int which, @NonNull Bundle extras) {
        if (RECURSIVE_DIALOG.equals(dialogTag)){
            Toast.makeText(getContext(), "# "+getArguments().getInt(I, 1)+": "+
                    (which == BUTTON_POSITIVE ? "+":"-"), Toast.LENGTH_SHORT).show();
            // do not return true here, so that the result is passed through to the activity
        }
        return false;
    }
}
