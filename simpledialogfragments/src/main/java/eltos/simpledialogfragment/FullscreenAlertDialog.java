package eltos.simpledialogfragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Message;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.Toolbar;

import java.util.HashMap;
import java.util.Map;

import kotlin.NotImplementedError;

/**
 * Fullscreen dialog class mimicing the AlertDialog interface for compatibility reasons
 */
public class FullscreenAlertDialog extends AlertDialog {

    Toolbar mToolbar;
    ViewGroup mContainer;
    View mMessageContainer;
    TextView mMessage;
    private boolean cancelable = true;
    private CharSequence title = null;
    private CharSequence message;
    private View view;
    private Map<Integer, Pair<CharSequence, OnClickListener>> buttons = new HashMap<>(3);

    protected FullscreenAlertDialog(@NonNull Context context, int themeResId) {
        super(new ContextThemeWrapper(context, themeResId), R.style.FullscreenDialog);
    }

    @Override
    @SuppressLint("MissingSuperCall")
    protected void onCreate(Bundle savedInstanceState) {
        // Skip the default dialog creation, we create our own views!
        //super.onCreate(savedInstanceState);

        View root = getLayoutInflater().inflate(R.layout.dialog_fullscreen, null, false);
        ViewGroup frame = root.findViewById(R.id.frame);
        View intermediate = getLayoutInflater().inflate(R.layout.simpledialogfragment_custom_view, null, false);
        frame.addView(intermediate);
        setContentView(root);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mToolbar = root.findViewById(R.id.toolbar);
        mMessage = intermediate.findViewById(R.id.customMessage);
        mMessageContainer = intermediate.findViewById(R.id.customMessageContainer);
        mContainer = (ViewGroup) intermediate.findViewById(R.id.customView);

        setTitle(title);
        setMessage(message);
        setView(view);

        mToolbar.inflateMenu(R.menu.dialog_buttons);
        setCancelable(cancelable);
        for (Map.Entry<Integer, Pair<CharSequence, OnClickListener>> button : buttons.entrySet()) {
            setButton(button.getKey(), button.getValue().first, button.getValue().second);
        }

    }

    public void setCancelable(boolean cancelable) {
        this.cancelable = cancelable;
        if (mToolbar != null) {
            if (cancelable) {
                mToolbar.setNavigationContentDescription(android.R.string.cancel);
                mToolbar.setNavigationIcon(R.drawable.ic_clear_search);
                mToolbar.setNavigationOnClickListener(v -> dismiss());
            } else {
                mToolbar.setNavigationIcon(null);
                mToolbar.setNavigationOnClickListener(null);
                // TODO: prevent back button press?
            }
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        this.title = title;
        if (mToolbar != null) {
            mToolbar.setTitle(title);
        }
    }

    public void setMessage(CharSequence message) {
        this.message = message;
        if (mMessage != null) {
            mMessage.setText(message);
            mMessage.setVisibility(message == null ? View.GONE : View.VISIBLE);
            mMessageContainer.setVisibility(mMessage.getVisibility());
        }
    }

    @Override
    public void setView(View view) {
        this.view = view;
        if (mContainer != null) {
            mContainer.removeAllViews();
            if (view != null) {
                mContainer.addView(view);
            }
        }
    }

    public void setButton(int whichButton, CharSequence text, Message msg) {
        throw new NotImplementedError("Set the button passing an OnClickListener instead!");
    }

    public void setButton(int whichButton, CharSequence text, OnClickListener listener) {
        buttons.put(whichButton, new Pair<>(text, listener));

        if (mToolbar != null) {
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
    }

    public Button getButton(int whichButton) {
        // TODO: how to handle this???? Important to change enabled state etc.

        return super.getButton(whichButton); // TODO mAlert.getButton(whichButton);
    }

    public void setIcon(int resId) {
        // TODO mAlert.setIcon(resId);
    }


}
