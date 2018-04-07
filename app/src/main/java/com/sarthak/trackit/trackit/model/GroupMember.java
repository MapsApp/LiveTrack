package com.sarthak.trackit.trackit.model;

import android.os.Parcel;
import android.os.Parcelable;

public class GroupMember implements Parcelable {

    public String location;

    public GroupMember() {
    }

    public GroupMember(String location) {
        this.location = location;
    }

    protected GroupMember(Parcel in) {
        location = in.readString();
    }

    public static final Creator<GroupMember> CREATOR = new Creator<GroupMember>() {
        @Override
        public GroupMember createFromParcel(Parcel in) {
            return new GroupMember(in);
        }

        @Override
        public GroupMember[] newArray(int size) {
            return new GroupMember[size];
        }
    };

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(location);
    }
}
