/*
 *  Copyright 2017 Philipp Niedermayer (github.com/eltos)
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

package eltos.simpledialogfragments;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;

import eltos.simpledialogfragment.SimpleDialog;
import eltos.simpledialogfragment.color.SimpleColorDialog;

public class FlatFragmentActivity extends AppCompatActivity implements SimpleDialog.OnDialogResultListener {

    private static final String COLOR_FRAGMENT = "color_fragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flat_fragment);


        /**
         * This example shows how one could re-use the views from a dialog
         * in an activity, using a wrapper class like {@link FlatColorFragment}
         *
         */

        FragmentManager fragmentManager = getSupportFragmentManager();
        final FlatColorFragment fragment;

        if (savedInstanceState == null){

            // instantiate and configure properties
            fragment = new FlatColorFragment();
            fragment.allowCustom(true);
            fragment.colorPreset(0xFFCF4747);

            fragmentManager.beginTransaction().add(R.id.frame, fragment, COLOR_FRAGMENT).commit();

        } else {
            fragment = (FlatColorFragment) fragmentManager.findFragmentByTag(COLOR_FRAGMENT);
        }

        // call result listener
        findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment.callResultListener();
            }
        });

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

        // handle results as usual
        if (COLOR_FRAGMENT.equals(dialogTag) && which == BUTTON_POSITIVE) {
            @ColorInt int color = extras.getInt(SimpleColorDialog.COLOR);

            // Sets action bar colors
            if (getSupportActionBar() != null) {
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0xFF000000 | color));

                boolean dark = Color.red(color) * 0.299 + Color.green(color) * 0.587 + Color.blue(color) * 0.114 < 180;
                SpannableString s = new SpannableString(getSupportActionBar().getTitle());
                s.setSpan(new ForegroundColorSpan(dark ? Color.WHITE : Color.BLACK), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                getSupportActionBar().setTitle(s);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                float[] hsv = new float[3];
                Color.colorToHSV(color, hsv);
                hsv[2] *= 0.75;
                getWindow().setStatusBarColor(Color.HSVToColor(hsv));
            }

            return true;
        }
        return false;
    }

}
