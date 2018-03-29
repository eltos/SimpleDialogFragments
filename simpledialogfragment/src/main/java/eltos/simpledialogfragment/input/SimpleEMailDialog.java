/*
 *  Copyright 2018 Philipp Niedermayer (github.com/eltos)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

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

    public static final String TAG = "SimpleEMailDialog.";

    public static final String
            EMAIL = TEXT;


    public static SimpleEMailDialog build() {
        return new SimpleEMailDialog();
    }


    protected static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    Pattern pattern = Pattern.compile(EMAIL_PATTERN);

    public SimpleEMailDialog(){
        inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        hint(R.string.email_address);
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
