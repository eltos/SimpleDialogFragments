package eltos.simpledialogfragments;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import java.util.ArrayList;

import eltos.simpledialogfragment.SimpleCheckDialog;
import eltos.simpledialogfragment.SimpleDialog;
import eltos.simpledialogfragment.SimpleListDialog;
import eltos.simpledialogfragment.input.SimpleEMailDialog;
import eltos.simpledialogfragment.input.SimpleInputDialog;

public class MainActivity extends AppCompatActivity implements
        SimpleDialog.OnDialogResultListener,
        SimpleInputDialog.InputValidator {

    private static final String CHECK_DIALOG = "checkDialogTag";
    private static final String INPUT_DIALOG = "inputDialogTag";
    private static final String MAIL_DIALOG = "mailDialogTag";
    private static final String LIST_DIALOG = "listDialogTag";
    private Button mAlertButton;
    private Button mCheckButton;
    private Button mInputButton;
    private Button mPassButton;
    private Button mMailButton;
    private Button mPhoneButton;
    private Button mListSinglebutton;
    private Button mListMultiplebutton;
    private Button mListDirectbutton;
    private Button mListbutton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAlertButton = (Button) findViewById(R.id.alert);
        mCheckButton = (Button) findViewById(R.id.check);
        mInputButton = (Button) findViewById(R.id.input);
        mPassButton = (Button) findViewById(R.id.passwd);
        mMailButton = (Button) findViewById(R.id.mail);
        mPhoneButton = (Button) findViewById(R.id.phone);
        mListbutton = (Button) findViewById(R.id.list0);
        mListSinglebutton = (Button) findViewById(R.id.list1);
        mListMultiplebutton = (Button) findViewById(R.id.list2);
        mListDirectbutton = (Button) findViewById(R.id.list3);

        mAlertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SimpleDialog.build()
                        .title(R.string.hello)
                        .msg(R.string.hello_world)
                        .show(MainActivity.this);

            }
        });
        mCheckButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SimpleCheckDialog.build()
                        .title(R.string.check)
                        .msg(R.string.please_check)
                        .label(R.string.accept)
                        .checkRequired(true)
                        .cancelable(false)
                        .show(MainActivity.this, CHECK_DIALOG);

            }
        });
        mInputButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SimpleInputDialog.build()
                        .title(R.string.test)
                        .msg(R.string.enter_name)
                        .hint(R.string.name)
                        .neut()
                        .pos(R.string.continue_)
                        .show(MainActivity.this, INPUT_DIALOG);

            }
        });
        mPassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SimpleInputDialog.build()
                        .title(R.string.password)
                        .hint(R.string.password)
                        .neut()
                        .max(25)
                        .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)
                        .show(MainActivity.this);

            }
        });
        mMailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle extra = new Bundle();
                extra.putInt("ID", 123);

                SimpleEMailDialog.build()
                        .msg(R.string.enter_email)
                        .extra(extra)
                        .show(MainActivity.this, MAIL_DIALOG);

            }
        });
        mPhoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SimpleInputDialog.build()
                        .msg(R.string.enter_number)
                        .inputType(InputType.TYPE_CLASS_PHONE)
                        .show(MainActivity.this);

            }
        });
        mListbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String[] data = new String[]{"1", "2", "3", "4", "5", "9", "7", "8", "9", "10"};

                SimpleListDialog.build()
                        .title(R.string.numers)
                        .items(data)
                        .show(MainActivity.this, LIST_DIALOG);

            }
        });
        mListSinglebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                int[] data = new int[]{R.string.choice_A, R.string.choice_B, R.string.choice_C};

                Bundle extras = new Bundle();
                extras.putIntArray("labels", data);

                SimpleListDialog.build()
                        .title(R.string.select_one)
                        .choiceMode(SimpleListDialog.SINGLE_CHOICE) // _DIRECT
                        .choiceMin(1)
                        .items(getBaseContext(), data)
                        .extra(extras)
                        .show(MainActivity.this, LIST_DIALOG);

            }
        });
        mListDirectbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                int[] data = new int[]{R.string.choice_A, R.string.choice_B, R.string.choice_C};

                SimpleListDialog.build()
                        .title(R.string.select_one)
                        .choiceMode(SimpleListDialog.SINGLE_CHOICE_DIRECT)
                        .items(getBaseContext(), data)
                        .show(MainActivity.this, LIST_DIALOG);

            }
        });
        mListMultiplebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                int[] data = new int[]{R.string.choice_A, R.string.choice_B, R.string.choice_C};

                SimpleListDialog.build()
                        .title(R.string.select_any)
                        .choiceMode(SimpleListDialog.MULTI_CHOICE)
                        .choicePreset(new int[]{0,2})
                        .items(getBaseContext(), data)
                        .show(MainActivity.this, LIST_DIALOG);

            }
        });


    }


    @Override
    public boolean onResult(@NonNull String dialogTag, int which, @NonNull Bundle extras) {
        if (which == BUTTON_POSITIVE && CHECK_DIALOG.equals(dialogTag)){
            Toast.makeText(getBaseContext(), R.string.accepted, Toast.LENGTH_SHORT).show();
            return true;
        }
        if (which == BUTTON_POSITIVE && INPUT_DIALOG.equals(dialogTag)){
            String name = extras.getString(SimpleInputDialog.TEXT);
            // ...
        }
        if (which == BUTTON_POSITIVE && MAIL_DIALOG.equals(dialogTag)){
            int id = extras.getInt("ID");
            String mail = extras.getString(SimpleEMailDialog.EMAIL);
            // ...
        }
        if (which == BUTTON_POSITIVE && LIST_DIALOG.equals(dialogTag)) {
//            long[] ids = extras.getLongArray(SimpleListDialog.SELECTED_IDS);
//            ArrayList<Integer> pos = extras.getIntegerArrayList(SimpleListDialog.SELECTED_POSITIONS);
//            if (ids != null) {
//                String a = "";
//                for (long id : ids) {
//                    if (!a.isEmpty()) {
//                        a += ", ";
//                    }
//                    a += getString((int) id);
//                }
//                Toast.makeText(getBaseContext(), getResources().getQuantityString(
//                        R.plurals.selected, ids.length, ids.length) + "\n" + a, Toast.LENGTH_SHORT).show();
//
//            } else if (pos != null) {
//                Toast.makeText(getBaseContext(), getResources().getQuantityString(
//                        R.plurals.selected, pos.size(), pos.size()), Toast.LENGTH_SHORT).show();
//            }

            ArrayList<Integer> pos = extras.getIntegerArrayList(SimpleListDialog.SELECTED_POSITIONS);
            int[] label = extras.getIntArray("labels");
            if (pos != null) {
                if (label != null) {
                    String a = "";
                    for (int i : pos) {
                        if (!a.isEmpty()) {
                            a += ", ";
                        }
                        a += getString(label[i]);
                    }
                    Toast.makeText(getBaseContext(), getResources().getQuantityString(
                            R.plurals.selected, pos.size(), pos.size()) + "\n" + a, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getBaseContext(), getResources().getQuantityString(
                            R.plurals.selected, pos.size(), pos.size()), Toast.LENGTH_SHORT).show();
                }
            }
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
