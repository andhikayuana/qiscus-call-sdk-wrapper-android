package com.qiscus.sdk.call.agora.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.qiscus.sdk.call.agora.util.HashCode;
import com.qiscus.sdk.call.agora.util.ObjectUtil;

/**
 * Created on : May 04, 2018
 * Author     : zetbaitsu
 * Name       : Zetra
 * GitHub     : https://github.com/zetbaitsu
 */
public class Account implements Parcelable {
    private String username;
    private String displayName;
    private String avatarUrl;

    public Account() {

    }

    public Account(String username, String displayName, String avatarUrl) {
        this.username = username;
        this.displayName = displayName;
        this.avatarUrl = avatarUrl;
    }

    protected Account(Parcel in) {
        username = in.readString();
        displayName = in.readString();
        avatarUrl = in.readString();
    }

    public static final Creator<Account> CREATOR = new Creator<Account>() {
        @Override
        public Account createFromParcel(Parcel in) {
            return new Account(in);
        }

        @Override
        public Account[] newArray(int size) {
            return new Account[size];
        }
    };

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeString(displayName);
        dest.writeString(avatarUrl);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Account)) return false;
        Account account = (Account) o;
        return ObjectUtil.equals(username, account.username) &&
                ObjectUtil.equals(displayName, account.displayName) &&
                ObjectUtil.equals(avatarUrl, account.avatarUrl);
    }

    @Override
    public int hashCode() {
        return HashCode.hash(username, displayName, avatarUrl);
    }

    @Override
    public String toString() {
        return "Account{" +
                "username='" + username + '\'' +
                ", displayName='" + displayName + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                '}';
    }
}
