package com.qiscus.sdk.call.wrapper.data.local;

import android.content.Context;
import android.content.SharedPreferences;

import com.qiscus.sdk.call.wrapper.data.model.Account;

/**
 * Created on : May 04, 2018
 * Author     : zetbaitsu
 * Name       : Zetra
 * GitHub     : https://github.com/zetbaitsu
 */
public class Session {
    private final SharedPreferences sharedPreferences;

    public Session(Context context) {
        sharedPreferences = context.getSharedPreferences("qiscus_rtc_session", Context.MODE_PRIVATE);
    }

    public void register(Account account) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("is_registered", true);
        editor.putString("username", account.getUsername());
        editor.putString("display_name", account.getDisplayName());
        editor.putString("avatar_url", account.getAvatarUrl());
        editor.apply();
    }

    public boolean isRegistered() {
        return sharedPreferences.getBoolean("is_registered", false);
    }

    public String getUsername() {
        return sharedPreferences.getString("username", "");
    }

    public String getDisplayName() {
        return sharedPreferences.getString("display_name", "");
    }

    public String getAvatarUrl() {
        return sharedPreferences.getString("avatar_url", "");
    }

    public void logout() {
        sharedPreferences.edit().clear().apply();
    }

    public void saveLastSuccessUid(int uid) {
        StringBuilder uids = new StringBuilder()
                .append(getLastSuccessUid())
                .append(uid)
                .append(",");
        sharedPreferences.edit().putString("agora_uids", uids.toString()).apply();
    }

    public String getLastSuccessUid() {
        return sharedPreferences.getString("agora_uids", "");
    }
}
