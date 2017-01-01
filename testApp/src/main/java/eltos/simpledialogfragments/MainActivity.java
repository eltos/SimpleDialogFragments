package eltos.simpledialogfragments;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import eltos.simpledialogfragment.SimpleCheckDialogFragment;
import eltos.simpledialogfragment.SimpleDialogFragment;
import eltos.simpledialogfragment.SimpleInputDialogFragment;

public class MainActivity extends AppCompatActivity implements
        SimpleDialogFragment.OnDialogFragmentResultListener,
        SimpleInputDialogFragment.InputValidator {

    private static final String CHECK_DIALOG = "checkDialogTag";
    private static final String INPUT_DIALOG = "inputDialogTag";
    private Button mAlertButton;
    private Button mCheckButton;
    private Button mInputButton;
    private Button mPassButton;
    private TextView mTextView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAlertButton = (Button) findViewById(R.id.alert_dialog);
        mCheckButton = (Button) findViewById(R.id.check_dialog);
        mInputButton = (Button) findViewById(R.id.input_dialog);
        mPassButton = (Button) findViewById(R.id.passwd_dialog);
        mTextView = (TextView) findViewById(R.id.textView);

        mAlertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SimpleDialogFragment.build()
                        .title(R.string.hello)
                        .msg(R.string.hello_world)
                        .pos()
                        .show(MainActivity.this);

            }
        });
        mCheckButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SimpleCheckDialogFragment.build()
                        .title(R.string.check)
                        .msg(R.string.please_check)
                        .pos()
                        .label(R.string.accept)
                        .checkRequired(true)
                        .cancelable(false)
                        .show(MainActivity.this, CHECK_DIALOG);

            }
        });
        mInputButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SimpleInputDialogFragment.build()
                        .title(R.string.test)
                        .msg(R.string.enter_name)
                        .hint(R.string.name)
                        .text(mTextView.getText().toString())
                        .pos(R.string.continue_)
                        .neut()
                        .show(MainActivity.this, INPUT_DIALOG);

            }
        });
        mPassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SimpleInputDialogFragment.build()
                        .title(R.string.password)
                        .hint(R.string.password)
                        .pos()
                        .neut()
                        .max(25)
                        .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)
                        .show(MainActivity.this);

            }
        });
    }


    @Override
    public boolean onDialogFragmentResult(@NonNull String dialogTag, int which, @NonNull Bundle extras) {
        if (which == BUTTON_POSITIVE && CHECK_DIALOG.equals(dialogTag)){
            Toast.makeText(getBaseContext(), R.string.accepted, Toast.LENGTH_SHORT).show();
            return true;
        }
        if (which == BUTTON_POSITIVE && INPUT_DIALOG.equals(dialogTag)){
            String name = extras.getString(SimpleInputDialogFragment.TEXT);
            mTextView.setText(name);
        }
        return false;
    }

    @Override
    public String validate(String dialogTag, String input, @Nullable Bundle extras) {
        if (INPUT_DIALOG.equals(dialogTag)) {
            if (input == null || input.isEmpty()) {
                return getString(R.string.empty);
            } else if (input.length() < 3) {
                return getString(R.string.three_chars_min);
            }
        }
        return null;
    }
}
