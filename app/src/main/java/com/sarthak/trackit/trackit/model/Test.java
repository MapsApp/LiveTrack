package com.sarthak.trackit.trackit.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by karan on 3/18/2018.
 */

public class Test implements Parcelable {

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public static Creator<Test> getCREATOR() {
        return CREATOR;
    }

    String id;

    public Test(String id) {
        this.id = id;
    }

    protected Test(Parcel in) {
        id = in.readString();
    }

    public static final Creator<Test> CREATOR = new Creator<Test>() {
        @Override
        public Test createFromParcel(Parcel in) {
            return new Test(in);
        }

        @Override
        public Test[] newArray(int size) {
            return new Test[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
    }
}
