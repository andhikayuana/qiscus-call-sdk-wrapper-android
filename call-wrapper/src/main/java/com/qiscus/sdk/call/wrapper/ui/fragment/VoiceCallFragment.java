package com.qiscus.sdk.call.wrapper.ui.fragment;

import android.view.View;

import com.qiscus.sdk.call.wrapper.R;

/**
 * Created by rahardyan on 06/06/17.
 */

public class VoiceCallFragment extends CallFragment {
    @Override
    protected int getLayout() {
        return R.layout.fragment_qiscus_voice_call;
    }

    @Override
    protected void onParentViewCreated(View view) {
        //
    }
}

