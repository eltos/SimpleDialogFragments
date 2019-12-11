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

package eltos.simpledialogfragment.form;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;

import android.view.View;
import android.widget.TextView;

import eltos.simpledialogfragment.R;

/**
 * The ViewHolder class for {@link Hint}
 * 
 * Created by eltos on 06.07.2018.
 */

@SuppressWarnings("WeakerAccess")
class HintViewHolder extends FormElementViewHolder<Hint> {

    HintViewHolder(Hint field) {
        super(field);
    }

    @Override
    protected int getContentViewLayout() {
        return R.layout.simpledialogfragment_form_item_hint;
    }

    @Override
    protected void setUpView(View view, Context context, Bundle savedInstanceState,
                             final SimpleFormDialog.DialogActions actions) {

        TextView label = (TextView) view.findViewById(R.id.label);
        label.setHint(field.getText(context));

    }




    @Override
    protected void saveState(Bundle outState) {
    }

    @Override
    protected void putResults(Bundle results, String key) {
    }

    @Override
    protected boolean focus(SimpleFormDialog.FocusActions actions) {
        return false;
    }

    @Override
    protected boolean posButtonEnabled(Context context) {
        return true;
    }

    @Override
    protected boolean validate(Context context) {
        return true;
    }

}
