package com.sarthak.trackit.trackit.model;

/**
 * Created by hp on 3/10/2018.
 */

public class UserSearchResult {

    private String userName , displayName ;

    public UserSearchResult(String userName, String displayName) {
        this.userName = userName;
        this.displayName = displayName;
    }

    public UserSearchResult() {

    }

    public String getUserName() {
        return userName;
    }

    public String getDisplayName() {
        return displayName;
    }


    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
