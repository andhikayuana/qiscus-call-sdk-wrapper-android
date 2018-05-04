package com.qiscus.sdk.call.agora.ui.fragment;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qiscus.nirmana.Nirmana;
import com.qiscus.sdk.call.agora.QiscusRtc;
import com.qiscus.sdk.call.agora.R;
import com.qiscus.sdk.call.agora.data.model.Call;
import com.qiscus.sdk.ui.view.QiscusCircularImageView;

import static com.qiscus.sdk.call.agora.data.config.Constants.CALL_DATA;

/**
 * Created by rahardyan on 06/06/17.
 */

public abstract class CallFragment extends Fragment {
    protected boolean frontCamera = true;
    protected boolean isVideoOn = true;
    protected OnCallListener onCallListener;
    private Call callData;

    @Nullable
    private RelativeLayout headerContainer;
    private LinearLayout panelBtnContainer;
    private TextView tvCallerName;
    private ImageView btnEndCall, btnMic, btnSpeaker, backgroundImage;
    private View background;
    private QiscusCircularImageView calleeAvatar;
    private Chronometer callDuration;
    private boolean speakerOn = true;
    private boolean micOn = true;
    private boolean isPanelHidden;
    private long callDurationMillis;

    public static CallFragment newInstance(Call callData) {
        Bundle args = new Bundle();
        args.putParcelable(CALL_DATA, callData);
        CallFragment fragment = new VideoCallFragment();

        if (callData.getCallType() == QiscusRtc.CallType.VOICE) {
            fragment = new VoiceCallFragment();
        }

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parseArgumentData();
        onCallListener = (OnCallListener) getActivity();
    }

    private void parseArgumentData() {
        callData = getArguments().getParcelable(CALL_DATA);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayout(), container, false);
        initView(view);
        onParentViewCreated(view);

        return view;
    }

    protected abstract void onParentViewCreated(View view);

    private void initView(View view) {
        btnEndCall = view.findViewById(R.id.button_end_call);
        btnSpeaker = view.findViewById(R.id.button_speaker);
        btnMic = view.findViewById(R.id.button_mic);
        callDuration = view.findViewById(R.id.call_duration);
        tvCallerName = view.findViewById(R.id.caller_name);
        background = view.findViewById(R.id.background);
        backgroundImage = view.findViewById(R.id.image_background);
        panelBtnContainer = view.findViewById(R.id.panel_btn_container);
        calleeAvatar = view.findViewById(R.id.caller_avatar);

        if (callData.getCallType() == QiscusRtc.CallType.VIDEO) {
            headerContainer = view.findViewById(R.id.header_container);
        }

        setConfigToView();
        startTime();

        if (callData.getCallType() == QiscusRtc.CallType.VOICE) {
            speakerOn = false;
        }

        onCallListener.onSpeakerToggle(speakerOn);
        tvCallerName.setText(callData.getCalleeDisplayName());

        if (callData.getCallAs() == QiscusRtc.CallAs.CALLER) {
            String displayCalleeAvatar = (callData.getCalleeAvatar() == null ||
                    callData.getCalleeAvatar().isEmpty() ||
                    callData.getCalleeAvatar().equals("null")) ? "Anonymous" : callData.getCalleeAvatar();
            tvCallerName.setText(callData.getCalleeDisplayName());
            Nirmana.getInstance().get()
                    .load(displayCalleeAvatar)
                    .placeholder(R.drawable.ic_qiscus_profile_account)
                    .error(R.drawable.ic_qiscus_profile_account)
                    .dontAnimate()
                    .into(calleeAvatar);
        } else {
            String displayCallerAvatar = (callData.getCalleeAvatar() == null || callData.getCallerAvatar().isEmpty()) ? "Anonymous" : callData.getCallerAvatar();
            tvCallerName.setText(callData.getCallerDisplayName());
            Nirmana.getInstance().get()
                    .load(displayCallerAvatar)
                    .placeholder(R.drawable.ic_qiscus_profile_account)
                    .error(R.drawable.ic_qiscus_profile_account)
                    .dontAnimate()
                    .into(calleeAvatar);
        }

        final int speakerActiveIcon = QiscusRtc.getCallConfig().getSpeakerActiveIcon();
        final int speakerInactiveIcon = QiscusRtc.getCallConfig().getSpeakerInactiveIcon();
        btnSpeaker.setImageResource(callData.getCallType() == QiscusRtc.CallType.VIDEO ? speakerActiveIcon : speakerInactiveIcon);
        btnSpeaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speakerOn = !speakerOn;
                btnSpeaker.setImageResource(speakerOn ? speakerActiveIcon : speakerInactiveIcon);
                onCallListener.onSpeakerToggle(speakerOn);
            }
        });

        final int micActiveIcon = QiscusRtc.getCallConfig().getMicActiveIcon();
        final int micInactiveIcon = QiscusRtc.getCallConfig().getMicInactiveIcon();
        btnMic.setImageResource(micActiveIcon);
        btnMic.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                micOn = !micOn;
                btnMic.setImageResource(micOn ? micActiveIcon : micInactiveIcon);
                onCallListener.onMicToggle(micOn);
            }
        });

        final int btnEndCallDrawable = QiscusRtc.getCallConfig().getEndCallButton();
        btnEndCall.setImageResource(btnEndCallDrawable);
        btnEndCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopTimer();
                onCallListener.onEndCall(callDurationMillis);
            }
        });
    }

    public void hidePanelButton() {
        if (isPanelHidden) {
            slideUpView(panelBtnContainer);
        } else {
            slideDownView(panelBtnContainer);
        }

        isPanelHidden = !isPanelHidden;
    }

    public void startTime() {
        callDuration.setBase(SystemClock.elapsedRealtime());
        callDuration.start();
    }

    public long getCallDurationMillis() {
        return callDurationMillis;
    }

    private void slideDownView(final ViewGroup viewGroup) {
        Animation slideDownAnim = AnimationUtils.loadAnimation(getContext(), R.anim.qiscus_fly_out_down);
        slideDownAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                onCallListener.hidePanelSlide();
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                viewGroup.setVisibility(View.GONE);
                onCallListener.onPanelSlide(true);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        viewGroup.startAnimation(slideDownAnim);
    }

    private void slideUpView(final ViewGroup viewGroup) {
        Animation slideUpAnim = AnimationUtils.loadAnimation(getContext(), R.anim.qiscus_fly_in_up);
        slideUpAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                viewGroup.setVisibility(View.VISIBLE);
                onCallListener.hidePanelSlide();
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                onCallListener.onPanelSlide(false);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        viewGroup.startAnimation(slideUpAnim);
    }

    private void stopTimer() {
        callDurationMillis = SystemClock.elapsedRealtime() - callDuration.getBase();
        callDuration.stop();
    }

    private void setConfigToView() {
        if (QiscusRtc.getCallConfig().getBackgroundColor() == 0) {
            background.setBackground(getResources().getDrawable(QiscusRtc.getCallConfig().getBackgroundDrawable()));
        } else {
            background.setBackgroundColor(getResources().getColor(QiscusRtc.getCallConfig().getBackgroundColor()));
        }
    }

    public boolean isFrontCamera() {
        return frontCamera;
    }

    protected abstract int getLayout();

    public interface OnCallListener {
        void onSpeakerToggle(boolean speakerOn);

        void onMicToggle(boolean micOn);

        void onVideoToggle(boolean videoOn);

        void onCameraSwitch(boolean frontCamera);

        void onPanelSlide(boolean hidden);

        void onEndCall(long callDurationMillis);

        void hidePanelSlide();
    }
}
