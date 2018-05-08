package com.qiscus.sdk.call.wrapper.sample;

import android.app.Application;

import com.qiscus.sdk.call.wrapper.QiscusRtc;

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
        QiscusRtc.init(this, BuildConfig.AGORA_KEY);
    }
}
