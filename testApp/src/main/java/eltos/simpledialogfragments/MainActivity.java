package eltos.simpledialogfragments;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import eltos.simpledialogfragment.SimpleCheckDialog;
import eltos.simpledialogfragment.list.SimpleColorDialog;
import eltos.simpledialogfragment.SimpleDateDialog;
import eltos.simpledialogfragment.SimpleDialog;
import eltos.simpledialogfragment.list.SimpleListDialog;
import eltos.simpledialogfragment.SimpleTimeDialog;
import eltos.simpledialogfragment.input.SimpleEMailDialog;
import eltos.simpledialogfragment.input.SimpleInputDialog;

public class MainActivity extends AppCompatActivity implements
        SimpleDialog.OnDialogResultListener,
        SimpleInputDialog.InputValidator {

    private static final String CHECK_DIALOG = "checkDialogTag";
    private static final String INPUT_DIALOG = "inputDialogTag";
    private static final String MAIL_DIALOG = "mailDialogTag";
    private static final String LIST_DIALOG = "listDialogTag";
    private static final String COLOR_DIALOG = "colorDialogTag";
    private static final String DATE = "datePickerTag";
    private static final String TIME = "timePickerTag";
    private static final String DATETIME1 = "datetime1";
    private static final String DATETIME2 = "datetime2";
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
    private Button mColorbutton;
    private Button mRecursiveButton;
    private int mColor = SimpleColorDialog.NONE;
    private Button mDatePickerButton;
    private Button mTimePickerButton;
    private Button mDateTimePickerButton;

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
        mColorbutton = (Button) findViewById(R.id.color);
        mRecursiveButton = (Button) findViewById(R.id.recursive);
        mDatePickerButton = (Button) findViewById(R.id.datePicker);
        mTimePickerButton = (Button) findViewById(R.id.timePicker);
        mDateTimePickerButton = (Button) findViewById(R.id.dateTimePicker);

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

                String[] countries = getResources().getStringArray(R.array.countries_locale);

                SimpleInputDialog.build()
                        .title(R.string.test)
                        .msg(R.string.enter_country)
                        .hint(R.string.country)
                        .neut()
                        .pos(R.string.continue_)
                        .suggest(countries)
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


                String[] data = new String[]{"Amelia", "Ava", "Charlie", "Ella", "Emily", "George",
                        "Harry", "Isabella", "Isla", "Jack", "Jacob", "Jessica", "Mia", "Noah",
                        "Oliver", "Olivia", "Oscar", "Poppy", "Thomas", "William"};

                SimpleListDialog.build()
                        .title(R.string.names)
                        .items(data)
                        .divider(true)
                        .filterable(true)
                        .emptyText(R.string.not_found)
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
                        .filterable(true)
                        .show(MainActivity.this, LIST_DIALOG);

            }
        });
        mColorbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SimpleColorDialog.build()
                        .title(R.string.pick_a_color)
                        .colorPreset(mColor)
//                        .choiceMode(SimpleColorDialog.SINGLE_CHOICE_DIRECT)
                        .show(MainActivity.this, COLOR_DIALOG);

            }
        });
        mRecursiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RecursiveDialog.build()
                        .show(MainActivity.this);

            }
        });
        mDatePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SimpleDateDialog.build()
                        .minDate(new Date())    // only future days
                        .show(MainActivity.this, DATE);

            }
        });
        mTimePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SimpleTimeDialog.build()
                        .neut()
                        .hour(12).minute(0)
                        .show(MainActivity.this, TIME);

            }
        });
        mDateTimePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SimpleDateDialog.build()
                        .show(MainActivity.this, DATETIME1);

            }
        });

    }


    @Override
    public boolean onResult(@NonNull String dialogTag, int which, @NonNull Bundle extras) {
        if (which == BUTTON_POSITIVE) {
            switch (dialogTag) {
                case CHECK_DIALOG:
                    Toast.makeText(getBaseContext(), R.string.accepted, Toast.LENGTH_SHORT).show();
                    return true;

                case INPUT_DIALOG:
                    String name = extras.getString(SimpleInputDialog.TEXT);
                    // ...
                    return true;

                case MAIL_DIALOG:
                    int id = extras.getInt("ID");
                    String mail = extras.getString(SimpleEMailDialog.EMAIL);
                    // ...
                    return true;

                case LIST_DIALOG:
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
                    return true;

                case COLOR_DIALOG:
                    mColor = extras.getInt(SimpleColorDialog.COLOR);
                    return true;

                case DATE:
                    Date date = new Date(extras.getLong(SimpleDateDialog.DATE));
                    Toast.makeText(getBaseContext(), SimpleDateFormat.getDateInstance().format(date), Toast.LENGTH_SHORT).show();
                    return true;

                case TIME:
                    int hour = extras.getInt(SimpleTimeDialog.HOUR);
                    int minute = extras.getInt(SimpleTimeDialog.MINUTE);
                    Date time = new Date(0, 0, 0, hour, minute);
                    Toast.makeText(getBaseContext(), SimpleDateFormat.getTimeInstance().format(time), Toast.LENGTH_SHORT).show();
                    return true;

                case DATETIME1:
                    SimpleTimeDialog.build()
                            .extra(extras)
                            .show(MainActivity.this, DATETIME2);
                    return true;

                case DATETIME2:
                    Date datetime = new Date(extras.getLong(SimpleDateDialog.DATE));
                    datetime.setHours(extras.getInt(SimpleTimeDialog.HOUR));
                    datetime.setMinutes(extras.getInt(SimpleTimeDialog.MINUTE));
                    Toast.makeText(getBaseContext(), SimpleDateFormat.getDateTimeInstance().format(datetime), Toast.LENGTH_SHORT).show();
                    return true;
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
