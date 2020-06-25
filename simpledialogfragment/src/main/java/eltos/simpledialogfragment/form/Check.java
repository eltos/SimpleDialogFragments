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
import android.os.Parcel;
import androidx.annotation.BoolRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import eltos.simpledialogfragment.SimpleDialog.OnDialogResultListener;

/**
 * An checkbox form element to be used with {@link SimpleFormDialog}
 * 
 * This is a CheckBox - what else?
 * 
 * This will add a Boolean to resource bundle containing the checked state.
 * 
 * Created by eltos on 21.02.17.
 */

public class Check extends FormElement<Check, CheckViewHolder> {

    private String text = null;
    private int textResourceId = NO_ID;
    private Boolean preset = null;
    private int presetId = NO_ID;

    private Check(String resultKey) {
        super(resultKey);
    }

    /**
     * Factory method for a check field.
     *
     * @param key the key that can be used to receive the final state from the bundle in
     *            {@link OnDialogResultListener#onResult}
     * @return this instance
     */
    public static Check box(String key){
        return new Check(key);
    }


    /**
     * Sets the initial state of the checkbox
     *
     * @param preset initial state
     * @return this instance
     */
    public Check check(boolean preset){
        this.preset = preset;
        return this;
    }

    /**
     * Sets the initial state of the checkbox
     *
     * @param preset initial state as boolean resource
     * @return this instance
     */
    public Check check(@BoolRes int preset){
        this.presetId = preset;
        return this;
    }

    /**
     * Sets the label
     *
     * @param text label text as string
     */
    public Check label(String text){
        this.text = text;
        return this;
    }

    /**
     * Sets the label
     *
     * @param textResourceId label text as android string resource
     */
    public Check label(@StringRes int textResourceId){
        this.textResourceId = textResourceId;
        return this;
    }













    ///////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public CheckViewHolder buildViewHolder() {
        return new CheckViewHolder(this);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////

    @Nullable
    protected String getText(Context context){
        if (text != null) {
            return text;
        } else if (textResourceId != NO_ID){
            return context.getString(textResourceId);
        }
        return null;
    }

    protected boolean getInitialState(Context context){
        if (preset != null) {
            return preset;
        } else if (presetId != NO_ID){
            return context.getResources().getBoolean(presetId);
        }
        return false;
    }




    private Check(Parcel in) {
        super(in);
        text = in.readString();
        textResourceId = in.readInt();
        byte b = in.readByte();
        preset = b < 0 ? null : b != 0;
        presetId = in.readInt();
    }

    public static final Creator<Check> CREATOR = new Creator<Check>() {
        @Override
        public Check createFromParcel(Parcel in) {
            return new Check(in);
        }

        @Override
        public Check[] newArray(int size) {
            return new Check[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(text);
        dest.writeInt(textResourceId);
        dest.writeByte((byte) (preset == null ? -1 : (preset ? 1 : 0)));
        dest.writeInt(presetId);
    }


}
