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
import androidx.annotation.ArrayRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import eltos.simpledialogfragment.SimpleDialog.OnDialogResultListener;


/**
 * A spinner form element to be used with {@link SimpleFormDialog}
 * 
 * This is an Spinner that can be used to give a choice between different values.
 * 
 * This will add an int to resource bundle containing the index of the chosen value
 * within the supplied item list or -1 if none was chosen.
 * 
 * Created by philipp on 23.02.17.
 */

@SuppressWarnings({"unused", "WeakerAccess"})
public class Spinner extends FormElement<Spinner, SpinnerViewHolder> {

    private int itemArrayRes = NO_ID;
    private int[] itemStringResArray = null;
    private String[] items = null;
    private String placeholder = null;
    private int placeholderResourceId = NO_ID;
    int position = -1;

    private Spinner(String resultKey) {
        super(resultKey);
    }


    /**
     * Factory method for a plain spinner.
     *
     * @param key the key that can be used to receive the chosen item index from the bundle in
     *            {@link OnDialogResultListener#onResult}
     */
    public static Spinner plain(String key){
        return new Spinner(key);
    }




    /**
     * Sets the placeholder text displayed if nothing is selected
     *
     * @param text placeholder text as string
     */
    public Spinner placeholder(String text){
        this.placeholder = text;
        return this;
    }

    /**
     * Sets the placeholder text displayed if nothing is selected
     *
     * @param textResourceId placeholder text as android string resource
     */
    public Spinner placeholder(@StringRes int textResourceId){
        this.placeholderResourceId = textResourceId;
        return this;
    }

    /**
     * Provide an array resource with items to be shown by this spinner.
     *
     * @param itemArrayRes the string array resource to suggest
     */
    public Spinner items(@ArrayRes int itemArrayRes){
        this.itemArrayRes = itemArrayRes;
        return this;
    }

    /**
     * Provide an array of items to be shown by this spinner.
     *
     * @param itemsStringResArray array of string resources to suggest
     */
    public Spinner items(@StringRes int... itemsStringResArray){
        if (itemsStringResArray != null && itemsStringResArray.length > 0) {
            this.itemStringResArray = itemsStringResArray;
        }
        return this;
    }

    /**
     * Provide an array of items to be shown by this spinner.
     *
     * @param items array of strings to suggest
     */
    public Spinner items(String... items){
        if (items != null && items.length > 0) {
            this.items = items;
        }
        return this;
    }

    /**
     * Set the initially selected item
     *
     * @param itemIndex The index of the initially selected item
     */
    public Spinner preset(int itemIndex){
        this.position = itemIndex;
        return this;
    }





    ///////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public SpinnerViewHolder buildViewHolder() {
        return new SpinnerViewHolder(this);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////


    @Nullable
    protected String getPlaceholderText(Context context){
        if (placeholder != null) {
            return placeholder;
        } else if (placeholderResourceId != NO_ID){
            return context.getString(placeholderResourceId);
        }
        return null;
    }

    @Nullable
    protected String[] getItems(Context context){
        if (items != null){
            return items;
        } else if (itemStringResArray != null){
            String[] s = new String[itemStringResArray.length];
            for (int i = 0; i < itemStringResArray.length; i++) {
                s[i] = context.getString(itemStringResArray[i]);
            }
            return s;
        } else if (itemArrayRes != NO_ID){
            return context.getResources().getStringArray(itemArrayRes);
        }
        return null;
    }


    // Parcel implementation

    protected Spinner(Parcel in) {
        super(in);
        itemArrayRes = in.readInt();
        itemStringResArray = in.createIntArray();
        items = in.createStringArray();
        placeholder = in.readString();
        placeholderResourceId = in.readInt();
        position = in.readInt();
    }

    public static final Creator<Spinner> CREATOR = new Creator<Spinner>() {
        @Override
        public Spinner createFromParcel(Parcel in) {
            return new Spinner(in);
        }

        @Override
        public Spinner[] newArray(int size) {
            return new Spinner[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(itemArrayRes);
        dest.writeIntArray(itemStringResArray);
        dest.writeStringArray(items);
        dest.writeString(placeholder);
        dest.writeInt(placeholderResourceId);
        dest.writeInt(position);
    }
}
