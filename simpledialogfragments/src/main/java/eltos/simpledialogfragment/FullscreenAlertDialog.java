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
    private Map<Integer, Boolean> buttonStates = new HashMap<>(3);
    private int icon = -1;

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
        setIcon(icon);
        setView(view);

        mToolbar.inflateMenu(R.menu.dialog_buttons);
        setCancelable(cancelable);
        for (Map.Entry<Integer, Pair<CharSequence, OnClickListener>> button : buttons.entrySet()) {
            setButton(button.getKey(), button.getValue().first, button.getValue().second);
        }
        for (Map.Entry<Integer, Boolean> state : buttonStates.entrySet()){
            setButtonEnabled(state.getKey(), state.getValue());
        }

    }

    public void setCancelable(boolean cancelable) {
        this.cancelable = cancelable;
        // show cancel icon (unless user set a custom icon)
        if (mToolbar != null && icon < 0) {
            if (cancelable) {
                mToolbar.setNavigationContentDescription(android.R.string.cancel);
                mToolbar.setNavigationIcon(R.drawable.ic_clear_search);
                mToolbar.setNavigationOnClickListener(v -> dismiss());
            } else {
                mToolbar.setNavigationIcon(null);
                mToolbar.setNavigationOnClickListener(null);
            }
        }
        // prevent back button press if non-cancelable
        super.setCancelable(cancelable);
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

    @Deprecated
    public void setButton(int whichButton, CharSequence text, Message msg) {
        throw new NotImplementedError("Set the button passing an OnClickListener instead!");
    }

    private MenuItem getButtonMenuItem(int whichButton){
        int id = whichButton == DialogInterface.BUTTON_POSITIVE ? R.id.button_pos :
                whichButton == DialogInterface.BUTTON_NEGATIVE ? R.id.button_neg :
                        whichButton == DialogInterface.BUTTON_NEUTRAL ? R.id.button_neu : -1;
        return mToolbar.getMenu().findItem(id);
    }

    public void setButton(int whichButton, CharSequence text, OnClickListener listener) {
        buttons.put(whichButton, new Pair<>(text, listener));

        if (mToolbar != null) {
            MenuItem item = getButtonMenuItem(whichButton);
            item.setTitle(text);
            item.setVisible(text != null);
            item.setOnMenuItemClickListener(item1 -> {
                listener.onClick(FullscreenAlertDialog.this, whichButton);
                return true;
            });
            mToolbar.invalidateMenu();
        }
    }

    public void setButtonEnabled(int whichButton, boolean enabled){
        buttonStates.put(whichButton, enabled);
        if (mToolbar != null) {
            MenuItem item = getButtonMenuItem(whichButton);
            item.setEnabled(enabled);
            mToolbar.invalidateMenu();
        }
    }


    @Deprecated
    public Button getButton(int whichButton) {
        throw new NotImplementedError(
                "Fullscreen Dialog has MenuItems instead of Buttons. " +
                "Use setButton and setButtonEnabled methods!");
    }

    public void setIcon(int resId) {
        this.icon = resId;
        if (mToolbar != null && icon >= 0) {
            mToolbar.setNavigationIcon(resId);
            mToolbar.setNavigationOnClickListener(null);
        }
    }


}
