/*
 *  Copyright 2019 Philipp Niedermayer (github.com/eltos)
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

import java.util.Date;

import eltos.simpledialogfragment.SimpleDialog.OnDialogResultListener;

/**
 * A color element to be used with {@link SimpleFormDialog}
 * 
 * One can pick a date, time or both here
 * 
 * This will add a long to resource bundle containing the timestamp.
 * 
 * Created by eltos on 13.02.19
 */

public class DateTime extends FormElement<DateTime, DateTimeViewHolder> {

    protected enum Type {DATE, TIME, DATETIME};

    protected Type type = Type.DATE;
    protected Long min, max, date;
    protected Integer hour, minute;

    private DateTime(String resultKey) {
        super(resultKey);
    }




    /**
     * Factory method for a date/time/datetime field.
     *
     * @param key the key that can be used to receive the final state from the bundle in
     *            {@link OnDialogResultListener#onResult}
     */
    public static DateTime picker(String key){
        return new DateTime(key);
    }

    /**
     * Factory method for a date field.
     *
     * @param key the key that can be used to receive the final state from the bundle in
     *            {@link OnDialogResultListener#onResult}
     */
    public static DateTime date(String key){
        return picker(key).type(Type.DATE);
    }

    /**
     * Factory method for a time field.
     *
     * @param key the key that can be used to receive the final state from the bundle in
     *            {@link OnDialogResultListener#onResult}
     */
    public static DateTime time(String key){
        return picker(key).type(Type.TIME);
    }

    /**
     * Factory method for a datetime field.
     *
     * @param key the key that can be used to receive the final state from the bundle in
     *            {@link OnDialogResultListener#onResult}
     */
    public static DateTime datetime(String key){
        return picker(key).type(Type.DATETIME);
    }


    /**
     * Sets this fields type
     *
     * @param type type of field: {@link Type#DATE}, {@link Type#TIME} or {@link Type#DATETIME}
     */
    public DateTime type(Type type){
        this.type = type;
        return this;
    }


    /**
     * Sets the first date selectable
     *
     * @param date minimal date
     */
    public DateTime min(Date date){
        this.min = date.getTime();
        return this;
    }

    /**
     * Sets the last date selectable
     *
     * @param date maximal date
     */
    public DateTime max(Date date){
        this.max = date.getTime();
        return this;
    }

    /**
     * Sets the initial date and time
     *
     * @param date initial date
     */
    public DateTime date(Date date){
        this.date = date.getTime();
        this.hour = date.getHours();
        this.minute = date.getMinutes();
        return this;
    }

    /**
     * Sets the initial time
     *
     * @param hour initial hour
     * @param minute initial minute
     */
    public DateTime time(int hour, int minute){
        this.hour = hour;
        this.minute = minute;
        return this;
    }








    ///////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public DateTimeViewHolder buildViewHolder() {
        return new DateTimeViewHolder(this);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////


//    protected @ColorInt int getInitialColor(Context context){
//        if (presetId != NO_ID){
//            return context.getResources().getColor(presetId);
//        }
//        return preset;
//    }


    protected DateTime(Parcel in) {
        super(in);
        type = Type.valueOf(in.readString());
        date = in.readLong();  date = date == 0 ? null : date;
        min = in.readLong();   min = min == 0 ? null : min;
        max = in.readLong();   max = max == 0 ? null : max;
        hour = in.readInt();   hour = hour == -1 ? null : hour;
        minute = in.readInt(); minute = minute == -1 ? null : minute;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(type.name());
        dest.writeLong(date == null ? 0 : date);
        dest.writeLong(min == null ? 0 : min);
        dest.writeLong(max == null ? 0 : max);
        dest.writeInt(hour == null ? -1 : hour);
        dest.writeInt(minute == null ? -1 : minute);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DateTime> CREATOR = new Creator<DateTime>() {
        @Override
        public DateTime createFromParcel(Parcel in) {
            return new DateTime(in);
        }

        @Override
        public DateTime[] newArray(int size) {
            return new DateTime[size];
        }
    };

}
