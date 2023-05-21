package eltos.simpledialogfragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.Toolbar;
import kotlin.NotImplementedError;

/**
 * Fullscreen dialog class mimicing the AlertDialog interface for compatibility reasons
 */
public class FullscreenAlertDialog extends AlertDialog {

    Toolbar mToolbar;
    ViewGroup mContainer;
    TextView mMessage;

    protected FullscreenAlertDialog(@NonNull Context context, int themeResId) {
        super(new ContextThemeWrapper(context, themeResId), R.style.FullscreenDialog);

        // TODO: should do all this in onCreate

        View root = getLayoutInflater().inflate(R.layout.dialog_fullscreen, null, false);
        setContentView(root);
        ViewGroup frame = root.findViewById(R.id.frame);
        mToolbar = root.findViewById(R.id.toolbar);
        mToolbar.setNavigationOnClickListener(v -> dismiss());
        // TODO: is cancelable?
        mToolbar.inflateMenu(R.menu.dialog_buttons);

        View intermediate = getLayoutInflater().inflate(R.layout.simpledialogfragment_custom_view, null, false);
        frame.addView(intermediate);
        mMessage = intermediate.findViewById(R.id.customMessage);
        mContainer = (ViewGroup) intermediate.findViewById(R.id.customView);

        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Skip the default dialog creation, we create our own views
        //super.onCreate(savedInstanceState);
    }

    public void setCancelable(boolean cancelable) {
        if (cancelable) {
            mToolbar.setNavigationIcon(R.drawable.ic_baseline_close);
        } else {
            mToolbar.setNavigationIcon(null);
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        mToolbar.setTitle(title);
    }

    public void setMessage(CharSequence message) {
        mMessage.setText(message);
        mMessage.setVisibility(message == null ? View.GONE : View.VISIBLE);
    }

    @Override
    public void setView(View view) {
        mContainer.addView(view);
        // TODO: align content gravity top (not bottom)!
    }

    public void setButton(int whichButton, CharSequence text, Message msg) {
        throw new NotImplementedError();
    }

    public void setButton(int whichButton, CharSequence text, OnClickListener listener) {
        int id = whichButton == DialogInterface.BUTTON_POSITIVE ? R.id.button_pos :
                    whichButton == DialogInterface.BUTTON_NEGATIVE ? R.id.button_neg :
                        whichButton == DialogInterface.BUTTON_NEUTRAL ? R.id.button_neu : -1;
        MenuItem item = mToolbar.getMenu().findItem(id);
        item.setTitle(text);
        item.setVisible(text != null);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // TODO: handle enabled state
                listener.onClick(FullscreenAlertDialog.this, whichButton);
                dismiss();
                return true;
            }
        });
        // TODO item.setEnabled();
    }

    public Button getButton(int whichButton) {
        // TODO: how to handle this???? Important to change enabled state etc.

        return super.getButton(whichButton); // TODO mAlert.getButton(whichButton);
    }

    public void setIcon(int resId) {
        // TODO mAlert.setIcon(resId);
    }


}
