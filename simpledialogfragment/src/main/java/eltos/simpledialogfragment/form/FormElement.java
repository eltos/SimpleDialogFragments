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

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Base-class for form elements to be used with {@link SimpleFormDialog}
 * </p>
 * Each form element holds a {@link FormElement#resultKey} that is used to receive element
 * specific results in {@link SimpleFormDialog#onResult}
 *
 * Created by eltos on 20.02.17.
 */

@SuppressWarnings("WeakerAccess")
public abstract class FormElement<T extends FormElement, V extends FormElementViewHolder> implements Parcelable {

    protected String resultKey;
    public boolean required = false;


    protected FormElement(String resultKey){
        this.resultKey = resultKey;
    }


    /**
     * Return your custom implementation of {@link FormElementViewHolder} here
     *
     * @return The view holder that can represent this form element
     */
    public abstract V getViewHolder();


    /**
     * Mark this Field as required.
     * See {@link FormElement#required(boolean)}
     */
    public T required(){
        return required(true);
    }

    /**
     * Set the required flag for this field.
     * This has different meanings depending on the element type
     * </p>
     * Input fields will display an error message if their input is empty.
     * Check fields will be required to be checked
     *
     * @param required weather this field is required
     */
    @SuppressWarnings("unchecked cast")
    public T required(boolean required){
        this.required = required;
        return (T) this;
    }


    ///////////////////////////////////////////////////////////////////////////////////////////


    // Parcel implementation

    protected FormElement(Parcel in) {
        resultKey = in.readString();
        required = in.readByte() != 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(resultKey);
        dest.writeByte((byte) (required ? 1 : 0));
    }
}
