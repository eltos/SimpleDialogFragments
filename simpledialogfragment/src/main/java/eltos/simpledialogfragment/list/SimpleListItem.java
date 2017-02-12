/**
 * Copyright 2017 Philipp Niedermayer (github.com/eltos)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */
package eltos.simpledialogfragment.list;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ListView;

/**
 *
 * Created by eltos on 02.01.2017.
 */
public class SimpleListItem implements Parcelable {

    private String string;
    private long id;

    @Override
    public String toString() {
        return string;
    }

    protected SimpleListItem(String string){
        this(string, ListView.INVALID_ROW_ID);
    }
    protected SimpleListItem(String string, long id){
        this.string = string;
        this.id = id;
    }


    protected SimpleListItem(Parcel in) {
        string = in.readString();
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(string);
    }
    public static final Creator<SimpleListItem> CREATOR = new Creator<SimpleListItem>() {
        @Override
        public SimpleListItem createFromParcel(Parcel in) {
            return new SimpleListItem(in);
        }

        @Override
        public SimpleListItem[] newArray(int size) {
            return new SimpleListItem[size];
        }
    };
    @Override
    public int describeContents() {
        return 0;
    }


    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }
}
