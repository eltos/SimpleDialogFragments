package eltos.simpledialogfragment.input;

import android.support.annotation.Nullable;
import android.text.InputType;

import java.util.regex.Pattern;

import eltos.simpledialogfragment.R;

/**
 * An extension for the input dialog that will ensure the input is a valid email address
 *
 * Results:
 *      EMAIL    String      The entered email-address
 *
 * Created by expos on 02.01.2017.
 */
public class SimpleEMailDialog extends SimpleInputDialog {

    public static final String EMAIL = TEXT;

    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    Pattern pattern = Pattern.compile(EMAIL_PATTERN);

    public static SimpleEMailDialog build(){
        SimpleEMailDialog dialog = new SimpleEMailDialog();
        dialog.inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
                .hint(R.string.email_address);

        return dialog;
    }

    @Override
    protected String onValidateInput(@Nullable String input) {
        if (input != null && pattern.matcher(input).matches()){
            return super.onValidateInput(input);
        } else {
            return getString(R.string.invalid_email_address);
        }
    }
}
