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

package eltos.simpledialogfragment.form;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.view.View;

import eltos.simpledialogfragment.SimpleDialog.OnDialogResultListener;

/**
 * The Base class for all ViewHolders
 * </p>
 * This class is used to create the View that represents the corresponding {@link FormElement}
 * and to maintain it's functionality
 * </p>
 * Created by eltos on 23.02.17.
 */

@SuppressWarnings("WeakerAccess")
public abstract class FormElementViewHolder<E extends FormElement> {

    /**
     * The FormElement that this ViewHolder represents.
     */
    protected E field;


    protected FormElementViewHolder(E field){
        this.field = field;
    }

    /**
     * Implement this method to return a custom layout resource id for this view
     *
     * @return layout string resource
     */
    protected abstract @LayoutRes int getContentViewLayout();

    /**
     * Implement this method to setup your view for the first time or after a
     * {@link FormElementViewHolder#saveState}
     *
     * @param view The view that was inflated using the layout from
     *             {@link FormElementViewHolder#getContentViewLayout()}
     * @param context The context of this view
     * @param savedInstanceState A bundle containing everything that was saved in
     *                           {@link FormElementViewHolder#saveState(Bundle)}
     * @param actions A callback for convenient methods. See {@link SimpleFormDialog.DialogActions}
     * @param isLastElement Weather this is the last form element
     * @param isOnlyElement Weather this is the only form element
     */
    protected abstract void setUpView(View view, Context context, Bundle savedInstanceState,
                            SimpleFormDialog.DialogActions actions,
                            boolean isLastElement, boolean isOnlyElement);

    /**
     * Method to save this elements state
     * Bundles are maintained on a per-view basis, so that keys can be arbitrary
     *
     * @param outState The bundle to save the state to
     */
    protected abstract void saveState(Bundle outState);

    /**
     * Method to publish results from this view in
     * {@link OnDialogResultListener#onResult}
     *
     * @param results The bundle to save the results to
     * @param key The key that has to be used when storing results in the bundle
     */
    protected abstract void putResults(Bundle results, String key);

    /**
     * Method to focus this element
     *
     * @param actions An object providing useful callbacks, see {@link SimpleFormDialog.FocusActions}
     *
     * @return Whether this view or one of its descendants actually took focus.
     */
    protected abstract boolean focus(SimpleFormDialog.FocusActions actions);

    /**
     * Method to check for empty input, (un-)checked state etc.
     * Only simple (and fast) checks here, no error displaying!
     * This is used only for single element forms.
     *
     * @param context A context
     *
     * @return true if positive button can be enabled
     */
    protected abstract boolean posButtonEnabled(Context context);

    /**
     * Method to validate input, state etc. and display an error message or indicator
     *
     * @param context A context
     *
     * @return true if the input, state etc. is valid, false otherwise
     */
    protected abstract boolean validate(Context context);

}