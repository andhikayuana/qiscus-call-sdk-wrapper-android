package com.qiscus.sdk.call.wrapper.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.qiscus.nirmana.Nirmana;
import com.qiscus.sdk.call.wrapper.QiscusRtc;
import com.qiscus.sdk.call.wrapper.R;
import com.qiscus.sdk.call.wrapper.data.model.Call;
import com.qiscus.sdk.call.wrapper.util.RingManager;

import static com.qiscus.sdk.call.wrapper.data.config.Constants.CALL_DATA;

/**
 * Created by rahardyan on 06/06/17.
 */

public abstract class CallingFragment extends Fragment {
    private ImageView btnEndCall, btnAcceptCall, backgroundImage;
    private View background;
    private OnCallingListener onCallingListener;
    private RingManager ringManager;
    private Call callData;
    private ImageView calleeAvatar;
    private TextView tvCallerName, tvCallState;

    public static CallingFragment newInstance(Call callData) {
        Bundle args = new Bundle();
        args.putParcelable(CALL_DATA, callData);
        CallingFragment fragment = new VideoCallingFragment();

        if (callData.getCallType() == QiscusRtc.CallType.VOICE) {
            fragment = new VoiceCallingFragment();
        }

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parseBundleData();
        onCallingListener = (OnCallingListener) getActivity();
        ringManager = RingManager.getInstance(getContext());
        ringManager.play(callData.getCallType(), callData.getCallAs());
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayout(), container, false);
        initView(view);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ringManager.stop();
    }

    private void parseBundleData() {
        callData = getArguments().getParcelable(CALL_DATA);
    }

    private void initView(View view) {
        btnAcceptCall = view.findViewById(R.id.button_accept_call);
        btnEndCall = view.findViewById(R.id.button_end_call);
        tvCallerName = view.findViewById(R.id.caller_name);
        tvCallState = view.findViewById(R.id.call_state);
        background = view.findViewById(R.id.background);
        backgroundImage = view.findViewById(R.id.image_background);
        calleeAvatar = view.findViewById(R.id.caller_avatar);

        setConfigToView();

        if (callData.getCallAs() == QiscusRtc.CallAs.CALLER) {
            String displayCalleeAvatar = (callData.getCalleeAvatar() == null || callData.getCalleeAvatar().isEmpty() || callData.getCalleeAvatar().equals("null")) ? "Anonymous" : callData.getCalleeAvatar();
            tvCallerName.setText(callData.getCalleeDisplayName());
            Nirmana.getInstance().get()
                    .load(displayCalleeAvatar)
                    .placeholder(R.drawable.ic_qiscus_profile_account)
                    .error(R.drawable.ic_qiscus_profile_account)
                    .dontAnimate()
                    .into(calleeAvatar);
            btnAcceptCall.setVisibility(View.GONE);
            tvCallState.setText(R.string.qiscus_call_calling_state);
        } else {
            String displayCallerAvatar = (callData.getCalleeAvatar() == null || callData.getCallerAvatar().isEmpty()) ? "Anonymous" : callData.getCallerAvatar();
            tvCallerName.setText(callData.getCallerDisplayName());
            Nirmana.getInstance().get()
                    .load(displayCallerAvatar)
                    .placeholder(R.drawable.ic_qiscus_profile_account)
                    .error(R.drawable.ic_qiscus_profile_account)
                    .dontAnimate()
                    .into(calleeAvatar);
            btnAcceptCall.setVisibility(View.VISIBLE);
            tvCallState.setText(R.string.qiscus_call_incoming_state);
        }

        btnAcceptCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (QiscusRtc.getCallConfig().getOnAcceptCallClickListener() != null) {
                    QiscusRtc.getCallConfig().getOnAcceptCallClickListener().onClick(callData);
                }
                onCallingListener.onAcceptPressed();
                ringManager.stop();
                btnAcceptCall.setVisibility(View.GONE);
            }
        });

        btnEndCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callData.getCallAs() == QiscusRtc.CallAs.CALLER) {
                    if (QiscusRtc.getCallConfig().getOnCancelCallClickListener() != null) {
                        QiscusRtc.getCallConfig().getOnCancelCallClickListener().onClick(callData);
                    }
                    onCallingListener.onCancelPressed();
                } else {
                    if (QiscusRtc.getCallConfig().getOnRejectCallClickListener() != null) {
                        QiscusRtc.getCallConfig().getOnRejectCallClickListener().onClick(callData);
                    }
                    onCallingListener.onRejectPressed();
                }
                ringManager.stop();
            }
        });
    }

    public void setCalleeAvatarAndDisplayName(final String displayName, final String avatarUrl) {
        tvCallerName.setText(displayName);
        Nirmana.getInstance().get()
                .load(avatarUrl)
                .placeholder(R.drawable.ic_qiscus_profile_account)
                .error(R.drawable.ic_qiscus_profile_account)
                .dontAnimate()
                .into(calleeAvatar);
    }

    public void setTvCallState(final String callState) {
        tvCallState.setText(callState);
        ringManager.stop();
        ringManager.play(QiscusRtc.CallType.VOICE, QiscusRtc.CallAs.CALLEE);
    }

    private void setConfigToView() {
        //configure accept call button
        int btnAcceptDrawable = QiscusRtc.getCallConfig().getAcceptVoiceCallButton();
        if (callData.getCallType() == QiscusRtc.CallType.VIDEO) {
            btnAcceptDrawable = QiscusRtc.getCallConfig().getAcceptVideoCallButton();
        }

        //configure end call button
        btnAcceptCall.setImageResource(btnAcceptDrawable);
        int btnEndCallDrawable = QiscusRtc.getCallConfig().getEndCallButton();
        btnEndCall.setImageResource(btnEndCallDrawable);

        //configure background cal;
        if (QiscusRtc.getCallConfig().getBackgroundColor() == 0) {
            background.setBackground(getResources().getDrawable(QiscusRtc.getCallConfig().getBackgroundDrawable()));
        } else {
            background.setBackgroundColor(getResources().getColor(QiscusRtc.getCallConfig().getBackgroundColor()));
        }
    }

    public interface OnCallingListener {
        void onAcceptPressed();

        void onRejectPressed();

        void onCancelPressed();
    }

    protected abstract int getLayout();
}

