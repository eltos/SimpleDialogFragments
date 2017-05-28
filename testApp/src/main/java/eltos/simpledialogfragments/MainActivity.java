package eltos.simpledialogfragments;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.ArrayRes;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumMap;

import eltos.simpledialogfragment.SimpleDateDialog;
import eltos.simpledialogfragment.SimpleDialog;
import eltos.simpledialogfragment.SimpleImageDialog;
import eltos.simpledialogfragment.SimpleTimeDialog;
import eltos.simpledialogfragment.color.SimpleColorDialog;
import eltos.simpledialogfragment.color.SimpleColorWheelDialog;
import eltos.simpledialogfragment.form.Check;
import eltos.simpledialogfragment.form.Input;
import eltos.simpledialogfragment.form.SimpleFormDialog;
import eltos.simpledialogfragment.form.Spinner;
import eltos.simpledialogfragment.input.SimpleInputDialog;
import eltos.simpledialogfragment.list.SimpleListDialog;

public class MainActivity extends AppCompatActivity implements
        SimpleDialog.OnDialogResultListener,
        SimpleInputDialog.InputValidator {

    private static final int REQUEST_ACCOUNTS_PERMISSION = 123;

    private static final String TERMS_DIALOG = "dialogTagTerms";
    private static final String CHOICE_DIALOG = "dialogTagChoice";
    private static final String PRODUCT_DIALOG = "dialogTagProduct";
    private static final String NUMBER_DIALOG = "dialogTagNumber";
    private static final String LOGIN_DIALOG = "dialogTagLogin";
    private static final String EMAIL_DIALOG = "dialogTagEmail";
    private static final String REGISTRATION_DIALOG = "dialogTagRegistration";
    private static final String CHECK_DIALOG = "dialogTagCheck";
    private static final String INPUT_DIALOG = "dialogTagInput";
    private static final String COLOR_DIALOG = "dialogTagColor";
    private static final String COLOR_WHEEL_DIALOG = "dialogTagColorWheel";
    private static final String DATE_DIALOG = "dialogTagDate";
    private static final String TIME_DIALOG = "dialogTagTime";
    private static final String DATETIME_DIALOG_DATE = "dialogTagDateTimeDate";
    private static final String DATETIME_DIALOG_TIME = "dialogTagDateTimeTime";
    private static final String YES_NO_DIALOG = "dialogTagYesNo";

    private static final String PRODUCT_ID = "productId";
    private static final String PHONE_NUMBER = "phoneNumber";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String KEEP_STARRED = "keepStarred";
    private static final String COUNTRY = "country";
    private static final String NEWSLETTER = "newsletter";
    private static final String FIRST_NAME = "firstName";
    private static final String SURNAME = "surname";
    private static final String EMAIL = "email";
    private static final String GENDER = "gender";


    private @ColorInt int color = 0xff9c27b0;
    private int counter = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.flat_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(), FlatFragmentActivity.class));
            }
        });

        newColor(color);

    }


    // ==   A l e r t s   ==

    public void showInfo(View view){

        SimpleDialog.build()
                .title(R.string.message)
                .msg(R.string.hello_world)
                .show(this);

    }


    public void showHtml(View view){

        SimpleDialog.build()
                .title(R.string.terms_title)
                .msgHtml(R.string.terms_and_conditions_html_styled)
                .cancelable(false)
                .pos(R.string.accept)
                .neg(R.string.decline)
                .show(this, TERMS_DIALOG);

        /** Results: {@link MainActivity#onResult} **/

    }


    public void showImage(View view){

        SimpleImageDialog.build()
                .image(new int[]{
                        R.drawable.wide_image_sample,
                        R.drawable.tall_image_sample,
                        R.drawable.image_sample}[counter++ % 3])
                .show(this);

    }


    public void showQr(View view){

        Bitmap qr = null;

        // Generate
        try {

            String content = "https://github.com/eltos/SimpleDialogFragments";

            int size = 1024;
            EnumMap<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.Q);
            hints.put(EncodeHintType.CHARACTER_SET, "UTF8");
            BitMatrix bitMatrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, size, size, hints);
            int[] pixels = new int[size * size];
            for (int y = 0; y < size; y++) {
                int offset = y * size;
                for (int x = 0; x < size; x++) {
                    pixels[offset + x] = bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE;
                }
            }
            qr = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565);
            qr.setPixels(pixels, 0, size, 0, 0, size, size);

        } catch (WriterException e) {
            Toast.makeText(getBaseContext(), R.string.error, Toast.LENGTH_SHORT).show();
        }

        // show
        SimpleImageDialog.build()
                .image(qr)
                .show(MainActivity.this);

    }


    public void showYesNo(View view){

        SimpleDialog.build()
                .title(R.string.ask_exit)
                .msg(R.string.ask_changes_discard)
                .pos(R.string.save)
                .neg(R.string.discard)
                .neut()
                .show(this, YES_NO_DIALOG);

        /** Results: {@link MainActivity#onResult} **/

    }


    public void showThemed(View view){

        SimpleFormDialog.build()
                .title(R.string.custom_dialog_theme)
                .msg(R.string.custom_theme_info)
                .fields(
                        Input.email(null),
                        Check.box(null).label(R.string.receive_newsletter).required()
                )
                .theme(R.style.MyFancyDialogTheme)
                .show(this);

    }



    // ==   L i s t s   a n d   C h o i c e s   ==

    public void showDirectChoice(View view){

        SimpleListDialog.build()
                .title(R.string.select_one)
                .choiceMode(SimpleListDialog.SINGLE_CHOICE_DIRECT)
                .items(getBaseContext(), R.array.choices)
                .show(this, CHOICE_DIALOG);

        /** Results: {@link MainActivity#onResult} **/

    }


    public void showSingleChoice(View view){

        Bundle extras = new Bundle();
        extras.putString(PRODUCT_ID, "X23E5HZL6X2");

        SimpleListDialog.build()
                .title(R.string.select_one)
                .choiceMode(SimpleListDialog.SINGLE_CHOICE)
                .choiceMin(1)
                .items(
                        new String[]{"Flavour A", "Flavour B", "Flavour C"},
                        new   long[]{    1348223,     7845325,     6875212})    // ids
                .extra(extras)
                .show(this, PRODUCT_DIALOG);

        /** Results: {@link MainActivity#onResult} **/

    }


    public void showMultiChoice(View view){

        SimpleListDialog.build()
                .title(R.string.select_up_to_5)
                .choiceMode(SimpleListDialog.MULTI_CHOICE)
                .choiceMax(5)
                .items(getBaseContext(), R.array.activites)
                .filterable(true)
                .show(this, CHOICE_DIALOG);

        /** Results: {@link MainActivity#onResult} **/

    }



    // ==   C o l o r s   ==

    public void showColorPicker(View view){

        @ArrayRes int pallet = new int[]{
                SimpleColorDialog.MATERIAL_COLOR_PALLET, // default if no pallet explicitly set
                SimpleColorDialog.MATERIAL_COLOR_PALLET_DARK,
                SimpleColorDialog.MATERIAL_COLOR_PALLET_LIGHT,
                SimpleColorDialog.BEIGE_COLOR_PALLET,
                SimpleColorDialog.COLORFUL_COLOR_PALLET
        }[counter++ % 5];

        SimpleColorDialog.build()
                .title(R.string.pick_a_color)
                .colors(this, pallet)
                .colorPreset(color)
                .allowCustom(true)
                .show(this, COLOR_DIALOG);

        /** Results: {@link MainActivity#onResult} **/

    }


    public void showHsvWheel(View view){

        SimpleColorWheelDialog.build()
                .color(color)
                .alpha(true)
                .show(this, COLOR_WHEEL_DIALOG);

        /** Results: {@link MainActivity#onResult} **/

    }



    // ==   I n p u t s   a n d   F o r m s   ==

    public void showCheckBox(View view){

        SimpleFormDialog.build()
                .title(getResources().getQuantityString(R.plurals.delete_messages, 17, 17))
                .fields(Check.box(KEEP_STARRED)
                        .label(R.string.keep_starred)
                        .check(true))
                .show(this, CHECK_DIALOG);

        /** Results: {@link MainActivity#onResult} **/

    }


    public void showEmailInput(View view){

        // email suggestion from registered accounts
        ArrayList<String> emails = new ArrayList<>(0);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && view != null) {
                requestPermissions(new String[]{Manifest.permission.GET_ACCOUNTS}, REQUEST_ACCOUNTS_PERMISSION);
                return;
            }
        } else {
            Account[] accounts = AccountManager.get(this).getAccounts();
            for (Account account : accounts) {
                if (Patterns.EMAIL_ADDRESS.matcher(account.name).matches()) {
                    emails.add(account.name);
                }
            }
        }

        SimpleFormDialog.build()
                .fields(Input.email(EMAIL)
                        .required()
                        .suggest(emails)
                        .text(emails.size() > 0 ? emails.get(0) : null)
                )
                .show(this, EMAIL_DIALOG);

        /** Results: {@link MainActivity#onResult} **/

    }


    public void showLogin(View view){

        SimpleFormDialog.buildLogin(USERNAME, PASSWORD)
                .show(this, LOGIN_DIALOG);

        /** Results: {@link MainActivity#onResult} **/

    }


    public void showNumberInput(View view){

        SimpleFormDialog.buildNumberInput(PHONE_NUMBER)
                .show(this, NUMBER_DIALOG);

        /** Results: {@link MainActivity#onResult} **/

    }


    public void showForm(View view){

        SimpleFormDialog.build()
                .title(R.string.register)
                .msg(R.string.please_fill_in_form)
                .fields(
                        Input.name(FIRST_NAME).hint(R.string.first_name),
                        Input.name(SURNAME).hint(R.string.surname).required(),
                        Spinner.plain(GENDER)
                                .label(R.string.gender).items(R.array.genders)
                                .placeholder(R.string.select___).required(),
                        Input.plain(COUNTRY).hint(R.string.country)
                                .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES)
                                .suggest(R.array.countries_locale).forceSuggestion(),
                        Input.email(EMAIL).required(),
                        Check.box(NEWSLETTER).label(R.string.receive_newsletter).check(true),
                        Input.password(PASSWORD).max(20).required().validatePatternStrongPassword(),
                        Check.box(null).label(R.string.terms_accept).required()
                )
                .show(this, REGISTRATION_DIALOG);

        /** Results: {@link MainActivity#onResult} **/

    }


    // ==   D a t e   a n d   T i m e   ==

    public void showDate(View view){

        SimpleDateDialog.build()
                .minDate(new Date())    // only future days
                .show(this, DATE_DIALOG);

        /** Results: {@link MainActivity#onResult} **/

    }


    public void showTime(View view){

        SimpleTimeDialog.build()
                .neut()
                .hour(12).minute(0)
        .show(this, TIME_DIALOG);

        /** Results: {@link MainActivity#onResult} **/

    }


    public void showDatetime(View view){

        SimpleDateDialog.build()
                .show(MainActivity.this, DATETIME_DIALOG_DATE);

        /** Results: {@link MainActivity#onResult} **/

    }






    private void newColor(int color){
        this.color = color;

        // Sets action bar colors
        if (getSupportActionBar() == null) return;

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xFF000000 | color));

        boolean dark = Color.red(color) * 0.299 + Color.green(color) * 0.587 + Color.blue(color) * 0.114 < 180;
        SpannableString s = new SpannableString(getSupportActionBar().getTitle());
        s.setSpan(new ForegroundColorSpan(dark ? Color.WHITE : Color.BLACK), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getSupportActionBar().setTitle(s);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            float[] hsv = new float[3];
            Color.colorToHSV(color, hsv);
            hsv[2] *= 0.75;
            getWindow().setStatusBarColor(Color.HSVToColor(hsv));
        }
    }

    // ==   R E S U L T S   ==


    /**
     * Let the hosting fragment or activity implement this interface
     * to receive results from the dialog
     *
     * @param dialogTag the tag passed to {@link SimpleDialog#show}
     * @param which result type, one of {@link #BUTTON_POSITIVE}, {@link #BUTTON_NEGATIVE},
     *              {@link #BUTTON_NEUTRAL} or {@link #CANCELED}
     * @param extras the extras passed to {@link SimpleDialog#extra(Bundle)}
     * @return true if the result was handled, false otherwise
     */
    @Override
    public boolean onResult(@NonNull String dialogTag, int which, @NonNull Bundle extras) {

        if (TERMS_DIALOG.equals(dialogTag)){ /** {@link MainActivity#showHtml} **/
            if (which == BUTTON_POSITIVE){ // terms accepted
                Toast.makeText(this, R.string.accepted, Toast.LENGTH_SHORT).show();

            } else { // terms declined, exit
                Toast.makeText(this, R.string.terms_declined_exited, Toast.LENGTH_SHORT).show();
                System.exit(0);

            }
            return true;
        }

        if (YES_NO_DIALOG.equals(dialogTag)) { /** {@link MainActivity#showYesNo} **/
            switch (which) {
                case BUTTON_POSITIVE:
                    Toast.makeText(this, R.string.saved, Toast.LENGTH_SHORT).show();
                    return true;
                case BUTTON_NEGATIVE:
                    Toast.makeText(this, R.string.discarded, Toast.LENGTH_SHORT).show();
                    return true;
                case BUTTON_NEUTRAL:
                case CANCELED:
                    Toast.makeText(this, R.string.canceled, Toast.LENGTH_SHORT).show();
                    return true;
            }
        }

        if (which == BUTTON_POSITIVE) {
            switch (dialogTag) {

                case CHOICE_DIALOG: /** {@link MainActivity#showDirectChoice}, {@link MainActivity#showMultiChoice} **/
                    ArrayList<String> labels = extras.getStringArrayList(SimpleListDialog.SELECTED_LABELS);
                    Toast.makeText(this, android.text.TextUtils.join(", ", labels), Toast.LENGTH_SHORT).show();
                    return true;

                case PRODUCT_DIALOG: /** {@link MainActivity#showSingleChoice} **/
                    String savedProductId = extras.getString(PRODUCT_ID);
                    long flavourId = extras.getLong(SimpleListDialog.SELECTED_SINGLE_ID);
                    Toast.makeText(this, getString(R.string.product_flavour_chosen, flavourId + "", savedProductId), Toast.LENGTH_SHORT).show();
                    return true;

                case COLOR_DIALOG: /** {@link MainActivity#showColorPicker(View)} **/
                    newColor(extras.getInt(SimpleColorDialog.COLOR));
                    return true;

                case COLOR_WHEEL_DIALOG: /** {@link MainActivity#showHsvWheel(View)} **/
                    newColor(extras.getInt(SimpleColorWheelDialog.COLOR));
                    return true;

                case CHECK_DIALOG: /** {@link MainActivity#showCheckBox(View)} **/
                    boolean keep = extras.getBoolean(KEEP_STARRED);
                    Toast.makeText(this, keep ? R.string.deleted_but_starred_kept : R.string.deleted, Toast.LENGTH_SHORT).show();
                    return true;

                case EMAIL_DIALOG: /** {@link MainActivity#showEmailInput(View)} **/
                    String mail = extras.getString(EMAIL);
                    Toast.makeText(this, mail, Toast.LENGTH_SHORT).show();
                    return true;

                case LOGIN_DIALOG: /** {@link MainActivity#showLogin(View)} **/
                    String username = extras.getString(USERNAME),
                            pass = extras.getString(PASSWORD);
                    Toast.makeText(this, username + ", " + pass, Toast.LENGTH_SHORT).show();
                    return true;

                case NUMBER_DIALOG: /** {@link MainActivity#showNumberInput(View)} **/
                    String number = extras.getString(PHONE_NUMBER);
                    Toast.makeText(this, number, Toast.LENGTH_SHORT).show();
                    return true;

                case REGISTRATION_DIALOG: /** {@link MainActivity#showForm(View)} **/
                    int gender = extras.getInt(GENDER);
                    boolean newsletter = extras.getBoolean(NEWSLETTER);
                    String firstName = extras.getString(FIRST_NAME),
                            name = extras.getString(SURNAME),
                            genderString = gender < 0 ? null : getResources().getStringArray(R.array.genders)[gender],
                            country = extras.getString(COUNTRY),
                            email = extras.getString(EMAIL),
                            password = extras.getString(PASSWORD);

                    Toast.makeText(this, firstName+" "+name+" ("+genderString+"), "+country+"\n"+
                            email+"(Newsletter: "+newsletter+"), "+password, Toast.LENGTH_SHORT).show();
                    // ...
                    return true;

                case DATE_DIALOG: /** {@link MainActivity#showDate(View)} **/
                    Date date = new Date(extras.getLong(SimpleDateDialog.DATE));
                    Toast.makeText(getBaseContext(), SimpleDateFormat.getDateInstance().format(date), Toast.LENGTH_SHORT).show();
                    return true;

                case TIME_DIALOG: /** {@link MainActivity#showTime(View)} **/
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.HOUR_OF_DAY, extras.getInt(SimpleTimeDialog.HOUR));
                    calendar.set(Calendar.MINUTE, extras.getInt(SimpleTimeDialog.MINUTE));
                    Toast.makeText(getBaseContext(), SimpleDateFormat.getTimeInstance().format(calendar.getTime()), Toast.LENGTH_SHORT).show();
                    return true;

                case DATETIME_DIALOG_DATE: /** {@link MainActivity#showDatetime(View)} **/
                    SimpleTimeDialog.build()
                            .extra(extras) // store the result again, so that is is available later
                            .show(this, DATETIME_DIALOG_TIME);
                    return true;

                case DATETIME_DIALOG_TIME:
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(extras.getLong(SimpleDateDialog.DATE));
                    cal.set(Calendar.HOUR_OF_DAY, extras.getInt(SimpleTimeDialog.HOUR));
                    cal.set(Calendar.MINUTE, extras.getInt(SimpleTimeDialog.MINUTE));
                    Toast.makeText(getBaseContext(), SimpleDateFormat.getDateTimeInstance().format(cal.getTime()), Toast.LENGTH_SHORT).show();
                    return true;



            }
        }
        return false;
    }




//
//    public void onRecourseClick(View view) {
//
//        RecursiveDialog.build()
//                .show(MainActivity.this);
//
//
//        SimpleDialog.build()
//                .title("TITLE")
//                .msg("MSG")
//                .show(this);
//        SimpleFormDialog.build()
//                .title("TITLE")
//                .msg("MSG")
//                .show(this);
//
//        SimpleDialog.build()
//                .title("TITLE")
//                .show(this);
//        SimpleFormDialog.build()
//                .title("TITLE")
//                .show(this);
//
//        SimpleDialog.build()
//                .msg("MSG")
//                .show(this);
//        SimpleFormDialog.build()
//                .msg("MSG")
//                .show(this);
//
//        SimpleDialog.build()
//                .title("TITLE")
//                .msg("MSG")
//                .pos(null)
//                .show(this);
//        SimpleFormDialog.build()
//                .title("TITLE")
//                .msg("MSG")
//                .pos(null)
//                .show(this);
//
//        SimpleDialog.build()
//                .title("TITLE")
//                .pos(null)
//                .show(this);
//        SimpleFormDialog.build()
//                .title("TITLE")
//                .pos(null)
//                .show(this);
//
//        SimpleDialog.build()
//                .msg("MSG")
//                .pos(null)
//                .show(this);
//        SimpleFormDialog.build()
//                .msg("MSG")
//                .pos(null)
//                .show(this);
//
//    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_ACCOUNTS_PERMISSION){
            // Another android bug requires this delay
            // See https://code.google.com/p/android/issues/detail?id=190966
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showEmailInput(null);
                }
            }, 10);

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
