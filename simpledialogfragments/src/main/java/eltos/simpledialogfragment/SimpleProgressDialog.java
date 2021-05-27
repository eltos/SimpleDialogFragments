package eltos.simpledialogfragment;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;

public class SimpleProgressDialog extends CustomViewDialog<SimpleProgressDialog> {


    public static final String TAG = "SimpleProgressDialog.";

    private static final String
            INDETERMINATE = TAG + "indeterminate",
            TYPE = TAG + "type";

    public enum Type {BAR, CIRCLE};


    public static SimpleProgressDialog build(){
        return new SimpleProgressDialog();
    }
    public static SimpleProgressDialog bar(){
        return SimpleProgressDialog.build().type(Type.BAR);
    }
    public static SimpleProgressDialog circle(){
        return SimpleProgressDialog.build().type(Type.CIRCLE);
    }

    public SimpleProgressDialog(){
        // defaults
        cancelable(false); // not cancelable by back-button press
        neut(android.R.string.cancel); // cancelable by cancel button
        pos(null);
    }


    public SimpleProgressDialog type(Type type){
        return setArg(TYPE, type.ordinal());
    }



    public ProgressBar mProgressBar;




    @Override
    protected View onCreateContentView(Bundle savedInstanceState) {
        // inflate and set your custom view here

        View view = inflate(R.layout.simpledialogfragment_progress);
        if (getArgs().getInt(TYPE) == Type.CIRCLE.ordinal()) {
            mProgressBar = view.findViewById(R.id.progressCircle);
        } else { // default: Type.BAR
            mProgressBar = view.findViewById(R.id.progress);
        }


        if (savedInstanceState == null){
            savedInstanceState = getArgs();
        }

        mProgressBar.setVisibility(View.VISIBLE);
        mProgressBar.setIndeterminate(savedInstanceState.getBoolean(INDETERMINATE, true));

        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(INDETERMINATE, mProgressBar.isIndeterminate());
    }

}
