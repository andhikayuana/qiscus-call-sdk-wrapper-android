package com.qiscus.sdk.call.agora.sample;

import android.app.Application;

import com.qiscus.sdk.call.agora.QiscusRtc;

/**
 * Created on : May 04, 2018
 * Author     : zetbaitsu
 * Name       : Zetra
 * GitHub     : https://github.com/zetbaitsu
 */
public class SampleApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        QiscusRtc.init(this, "7fd46cd9cfab466099a936ec5ef23298");
    }
}
