package eltos.simpledialogfragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import eltos.simpledialogfragment.SimpleCheckDialog;
import eltos.simpledialogfragment.SimpleDateDialog;
import eltos.simpledialogfragment.SimpleDialog;
import eltos.simpledialogfragment.SimpleTimeDialog;
import eltos.simpledialogfragment.color.SimpleColorDialog;
import eltos.simpledialogfragment.color.SimpleColorWheelDialog;
import eltos.simpledialogfragment.input.SimpleEMailDialog;
import eltos.simpledialogfragment.input.SimpleInputDialog;
import eltos.simpledialogfragment.list.SimpleListDialog;

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
    private static final String COLOR_WHEEL_DIALOG = "colorWheelDialogTag";

    private int mColor = SimpleColorDialog.NONE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void onCheckClick(View view) {


        SimpleCheckDialog.build()
                .title(R.string.check)
                .msg(R.string.please_check)
                .label(R.string.accept)
                .checkRequired(true)
                .cancelable(false)
                .show(MainActivity.this, CHECK_DIALOG);

    }

    public void onSuggestionClick(View view){

        String[] countries = getResources().getStringArray(R.array.countries_locale);

        SimpleInputDialog.build()
                .title(R.string.test)
                .msg(R.string.enter_country)
                .hint(R.string.country)
                .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES)
                .neut()
                .pos(R.string.continue_)
                .suggest(countries)
                .show(MainActivity.this, INPUT_DIALOG);

    }

    public void onPasswordClick(View view){

        SimpleInputDialog.build()
                .title(R.string.password)
                .hint(R.string.password)
                .neut()
                .max(25)
                .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)
                .show(MainActivity.this);

    }

    public void onEmailClick(View view){

        Bundle extra = new Bundle();
        extra.putInt("ID", 123);

        SimpleEMailDialog.build()
                .msg(R.string.enter_email)
                .extra(extra)
                .show(MainActivity.this, MAIL_DIALOG);

    }

    public void onNumerClick(View view){

        SimpleInputDialog.build()
                .msg(R.string.enter_number)
                .inputType(InputType.TYPE_CLASS_PHONE)
                .show(MainActivity.this);

    }

    public void onSimpleClick(View view){


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

    public void onSingleClick(View view){


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

    public void onDirectClick(View view){


        int[] data = new int[]{R.string.choice_A, R.string.choice_B, R.string.choice_C};

        SimpleListDialog.build()
                .title(R.string.select_one)
                .choiceMode(SimpleListDialog.SINGLE_CHOICE_DIRECT)
                .items(getBaseContext(), data)
                .show(MainActivity.this, LIST_DIALOG);

    }

    public void onMultiClick(View view){

        String[] data = new String[100];
        for (int i = 0; i < data.length; i++) {
            data[i] = getString(R.string.choice_i, i+"");
        }

        SimpleListDialog.build()
                .title(R.string.select_any)
                .choiceMode(SimpleListDialog.MULTI_CHOICE)
                .choicePreset(new int[]{0,2})
                .items(data)
                .filterable(true)
                .show(MainActivity.this, LIST_DIALOG);

    }

    public void onColorClick(View view){

        SimpleColorDialog.build()
                .title(R.string.pick_a_color)
                .colorPreset(mColor)
//                .choiceMode(SimpleColorDialog.SINGLE_CHOICE_DIRECT)
                .allowCustom(true)
                .show(MainActivity.this, COLOR_DIALOG);

    }

    public void onRecourseClick(View view){

        RecursiveDialog.build()
                .show(MainActivity.this);

    }

    public void onDateClick(View view){

        SimpleDateDialog.build()
                .minDate(new Date())    // only future days
                .show(MainActivity.this, DATE);

    }

    public void onTimeClick(View view){

        SimpleTimeDialog.build()
                .neut()
                .hour(12).minute(0)
                .show(MainActivity.this, TIME);

    }

    public void onDateTimeClick(View view){

        SimpleDateDialog.build()
                .show(MainActivity.this, DATETIME1);

    }

    public void onColorWheelClick(View view){

        SimpleColorWheelDialog.build()
                .color(mColor)
                .alpha(true)
                .show(MainActivity.this, COLOR_WHEEL_DIALOG);

    }

    public void onAlertClick(View view){

        SimpleDialog.build()
                .title(R.string.hello)
                .msg(R.string.hello_world)
                .show(MainActivity.this);

    }

    public void onOverflowClick(View view){

        SimpleDialog.build()
                .title(R.string.long_title)
                .msgHtml(R.string.long_message)
                .pos(R.string.long_ok)
                .neg(R.string.long_no)
                .neut(R.string.long_cancel)
                .show(MainActivity.this);

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

                case COLOR_WHEEL_DIALOG:
                    mColor = extras.getInt(SimpleColorWheelDialog.COLOR);
                    return true;

                case DATE:
                    Date date = new Date(extras.getLong(SimpleDateDialog.DATE));
                    Toast.makeText(getBaseContext(), SimpleDateFormat.getDateInstance().format(date), Toast.LENGTH_SHORT).show();
                    return true;

                case TIME:
                    Calendar cal = Calendar.getInstance();
                    cal.set(Calendar.HOUR_OF_DAY, extras.getInt(SimpleTimeDialog.HOUR));
                    cal.set(Calendar.MINUTE, extras.getInt(SimpleTimeDialog.MINUTE));
                    Toast.makeText(getBaseContext(), SimpleDateFormat.getTimeInstance().format(cal.getTime()), Toast.LENGTH_SHORT).show();
                    return true;

                case DATETIME1:
                    SimpleTimeDialog.build()
                            .extra(extras) // store the result again, so that is is available later
                            .show(MainActivity.this, DATETIME2);
                    return true;

                case DATETIME2:
                    Calendar cal2 = Calendar.getInstance();
                    cal2.setTimeInMillis(extras.getLong(SimpleDateDialog.DATE));
                    cal2.set(Calendar.HOUR_OF_DAY, extras.getInt(SimpleTimeDialog.HOUR));
                    cal2.set(Calendar.MINUTE, extras.getInt(SimpleTimeDialog.MINUTE));
                    Toast.makeText(getBaseContext(), SimpleDateFormat.getDateTimeInstance().format(cal2.getTime()), Toast.LENGTH_SHORT).show();
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
