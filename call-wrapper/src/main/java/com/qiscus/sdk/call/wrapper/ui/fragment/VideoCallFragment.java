package com.qiscus.sdk.call.wrapper.ui.fragment;

import android.view.View;
import android.widget.ImageView;

import com.qiscus.sdk.call.wrapper.QiscusRtc;
import com.qiscus.sdk.call.wrapper.R;

/**
 * Created by rahardyan on 06/06/17.
 */

public class VideoCallFragment extends CallFragment {
    private ImageView btnVideo;
    private ImageView btnSwitchCamera;

    @Override
    protected int getLayout() {
        return R.layout.fragment_qiscus_video_call;
    }

    @Override
    protected void onParentViewCreated(View view) {
        initView(view);
    }

    private void initView(View view) {
        btnSwitchCamera = view.findViewById(R.id.button_switch_camera);
        btnVideo = view.findViewById(R.id.button_video);
        btnVideo.setVisibility(View.GONE);

        final int cameraFrontIcon = QiscusRtc.getCallConfig().getFrontCameraIcon();
        final int cameraRearIcon = QiscusRtc.getCallConfig().getRearCameraIcon();
        btnSwitchCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frontCamera = !frontCamera;
                btnSwitchCamera.setImageResource(frontCamera ? cameraRearIcon : cameraFrontIcon);
                onCallListener.onCameraSwitch(frontCamera);
            }
        });

        final int videoActiveIcon = QiscusRtc.getCallConfig().getVideoActiveIcon();
        final int videoInactiveIcon = QiscusRtc.getCallConfig().getVideoInactiveIcon();
        btnVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isVideoOn = !isVideoOn;
                btnVideo.setImageResource(isVideoOn ? videoActiveIcon : videoInactiveIcon);
                btnSwitchCamera.setVisibility(isVideoOn ? View.VISIBLE : View.GONE);
                onCallListener.onVideoToggle(isVideoOn);
            }
        });
    }
}

