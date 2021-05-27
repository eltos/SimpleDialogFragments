package eltos.simpledialogfragment;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;

public class SimpleProgressDialog extends CustomViewDialog<SimpleProgressDialog> {


    public static final String TAG = "SimpleProgressDialog.";

    private static final String
            INDETERMINATE = TAG + "indeterminate";


    public static SimpleProgressDialog build(){
        return new SimpleProgressDialog();
    }

    public SimpleProgressDialog(){
        // defaults
        cancelable(false); // not cancelable by back-button press
        neut(android.R.string.cancel); // cancelable by cancel button
        pos(null);
    }





    public ProgressBar mProgressBar;




    @Override
    protected View onCreateContentView(Bundle savedInstanceState) {
        // inflate and set your custom view here

        View view = inflate(R.layout.simpledialogfragment_progress);
        mProgressBar = view.findViewById(R.id.progress);


        if (savedInstanceState == null){
            savedInstanceState = getArguments();
        }
        if (savedInstanceState != null) {
            mProgressBar.setIndeterminate(savedInstanceState.getBoolean(INDETERMINATE, true));
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(INDETERMINATE, mProgressBar.isIndeterminate());
    }

}
