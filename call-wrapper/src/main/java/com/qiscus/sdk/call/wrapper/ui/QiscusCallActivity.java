package com.qiscus.sdk.call.wrapper.ui;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.qiscus.sdk.call.wrapper.QiscusRtc;
import com.qiscus.sdk.call.wrapper.R;
import com.qiscus.sdk.call.wrapper.data.config.CallConfig;
import com.qiscus.sdk.call.wrapper.data.model.Call;
import com.qiscus.sdk.call.wrapper.ui.fragment.CallFragment;
import com.qiscus.sdk.call.wrapper.ui.fragment.CallingFragment;
import com.qiscus.sdk.call.wrapper.util.DimUtil;
import com.qiscus.sdk.call.wrapper.util.RingManager;

import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;

import static com.qiscus.sdk.call.wrapper.data.config.Constants.CALL_DATA;
import static com.qiscus.sdk.call.wrapper.data.config.Constants.ON_GOING_NOTIF_ID;

/**
 * Created by fitra on 2/10/17.
 */

public class QiscusCallActivity extends BaseActivity implements CallingFragment.OnCallingListener, CallFragment.OnCallListener {
    private static final String LOG_TAG = QiscusCallActivity.class.getSimpleName();
    private static final String TAG = QiscusCallActivity.class.getSimpleName();

    // Permission
    private String[] permissions = {
            Manifest.permission.CAMERA,
            Manifest.permission.MODIFY_AUDIO_SETTINGS,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    private RtcEngine rtcEngine;
    private Call callData;
    private CallingFragment callingFragment;
    private CallFragment callFragment;
    private PowerManager.WakeLock wakeLock;
    private RingManager ringManager;
    private int field = 0x00000020;
    private boolean callAccepted;
    private boolean callConnected;
    private final IRtcEngineEventHandler rtcEventHandler = new IRtcEngineEventHandler() {

        @Override
        public void onFirstRemoteAudioFrame(int uid, int elapsed) {
            Log.d(TAG, "onFirstRemoteAudioFrame: " + uid);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    callAccepted = true;
                    callConnected = true;
                    initCallFragment();
                }
            });
        }

        @Override
        public void onFirstRemoteVideoDecoded(final int uid, int width, int height, int elapsed) {
            Log.d(TAG, "onFirstRemoteVideoDecoded: " + uid);
            QiscusRtc.getSession().saveLastSuccessUid(uid);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setupRemoteVideo(uid);
                    setupLocalVideo();
                }
            });
        }

        @Override
        public void onUserOffline(final int uid, int reason) {
            Log.d(TAG, "onUserOffline: " + uid);

            //check from screen share
            String uids = QiscusRtc.getSession().getLastSuccessUid();
            String[] split = uids.split(",");

            if (split.length == 1) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        disconnect();
                    }
                });
            } else if (split.length == 2) {
                final int videoUid = Integer.valueOf(split[0]);
                QiscusRtc.getSession().clearLastSessionUid();
                QiscusRtc.getSession().saveLastSuccessUid(videoUid);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setupRemoteVideo(videoUid);
                        setupLocalVideo();
                    }
                });
            }
        }

        @Override
        public void onUserMuteVideo(final int uid, final boolean muted) {
            Log.d(TAG, "onUserMuteVideo: ");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onRemoteUserVideoMuted(uid, muted);
                }
            });
        }

        @Override
        public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
            super.onJoinChannelSuccess(channel, uid, elapsed);
            Log.d(TAG, "onJoinChannelSuccess: " + channel + " uid : " + uid);
        }

        @Override
        public void onLeaveChannel(RtcStats stats) {
            super.onLeaveChannel(stats);
            Log.d(TAG, "onLeaveChannel: ");
        }

        @Override
        public void onRejoinChannelSuccess(String channel, int uid, int elapsed) {
            super.onRejoinChannelSuccess(channel, uid, elapsed);
            Log.d(TAG, "onRejoinChannelSuccess: " + uid);
        }

        @Override
        public void onStreamPublished(String url, int error) {
            super.onStreamPublished(url, error);
            Log.d(TAG, "onStreamPublished: " + url);
        }

        @Override
        public void onStreamUnpublished(String url) {
            super.onStreamUnpublished(url);
            Log.d(TAG, "onStreamUnpublished: " + url);
        }

    };

    public static Intent generateIntent(Context context, Call callData) {
        Intent intent = new Intent(context, QiscusCallActivity.class);
        intent.putExtra(CALL_DATA, callData);
        return intent;
    }

    @Override
    public int getLayout() {
        return R.layout.activity_qiscus_call;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        QiscusRtc.getSession().clearLastSessionUid();
        parseIntentData();
        if (callData != null) {
            initView();
            requestPermission(permissions);
            setAlwaysOn();
            setFullscreen();
            configureProximity();
            autoDisconnect();

            if (QiscusRtc.getCallConfig().isOngoingNotificationEnable()) {
                showOnGoingCallNotification();
            }

        } else {
            finish();
        }

        ringManager = RingManager.getInstance(this);

    }

    private void parseIntentData() {
        callData = getIntent().getParcelableExtra(CALL_DATA);
    }

    @Override
    protected void onPermissionGranted() {
        initializeAgoraEngine();
        if (callData.getCallType() == QiscusRtc.CallType.VIDEO) {
            startVideoCall();
        } else {
            startVoiceCall();
        }
        if (callData.getCallAs() == QiscusRtc.CallAs.CALLER) {
            joinChannel();
        }
    }

    private void autoDisconnect() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!callAccepted) {
                    disconnect();
                }
            }
        }, 30000);
    }

    private void autoDisconnect2() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!callConnected) {
                    disconnect();
                }
            }
        }, 15000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        acquireProximity();
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseProximity();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (!hasFocus) {
            releaseProximity();
        } else {
            acquireProximity();
        }
    }

    private void initializeAgoraEngine() {
        try {
            rtcEngine = RtcEngine.create(getBaseContext(), QiscusRtc.getKey(), rtcEventHandler);
        } catch (Exception e) {
            Log.e(LOG_TAG, Log.getStackTraceString(e));
            throw new RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e));
        }
    }

    private void setupVideoProfile() {
        rtcEngine.enableVideo();
        rtcEngine.setVideoProfile(Constants.VIDEO_PROFILE_360P, false);
    }

    private void setupBeginningLocalVideo() {
        FrameLayout container = findViewById(R.id.remote_video_view);
        if (container.getChildCount() >= 1) {
            return;
        }

        SurfaceView surfaceView = RtcEngine.CreateRendererView(getBaseContext());
        container.addView(surfaceView);
        rtcEngine.setupRemoteVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_ADAPTIVE, 0));

        surfaceView.setTag(0);
    }

    private void setupLocalVideo() {
        FrameLayout container = findViewById(R.id.local_video_view);
        SurfaceView surfaceView = RtcEngine.CreateRendererView(getBaseContext());
        surfaceView.setZOrderMediaOverlay(true);
        container.addView(surfaceView);
        rtcEngine.setupLocalVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_ADAPTIVE, 0));

        toggleLocalVideoOrientation(container, isLandscape());
    }

    private void toggleLocalVideoOrientation(FrameLayout container, boolean isLandscape) {
        container.getLayoutParams().width = Math.round(DimUtil.convertDpToPixel(isLandscape ? 100 : 60, this));
        container.getLayoutParams().height = Math.round(DimUtil.convertDpToPixel(isLandscape ? 60 : 100, this));
        container.requestLayout();
    }

    private boolean isLandscape() {
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    private void joinChannel() {
        Log.d(TAG, "joinChannel: ");
        rtcEngine.joinChannel(null, callData.getRoomId(), "", 0);
    }

    private void setupRemoteVideo(int uid) {
        FrameLayout container = findViewById(R.id.remote_video_view);
        if (container.getChildCount() >= 1) {
            container.removeAllViews();
        }

        SurfaceView surfaceView = RtcEngine.CreateRendererView(QiscusCallActivity.this);
        container.addView(surfaceView);
        rtcEngine.setupRemoteVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_ADAPTIVE, uid));

        surfaceView.setTag(uid);
    }

    private void onRemoteUserLeft() {
        FrameLayout container = findViewById(R.id.remote_video_view);
        container.removeAllViews();
    }

    private void onRemoteUserVideoMuted(int uid, boolean muted) {
        FrameLayout container = findViewById(R.id.remote_video_view);
        SurfaceView surfaceView = (SurfaceView) container.getChildAt(0);
        Object tag = surfaceView.getTag();
        if (tag != null && (Integer) tag == uid) {
            surfaceView.setVisibility(muted ? View.GONE : View.VISIBLE);
        }
    }

    private void acquireProximity() {
        if (callData.getCallType() == QiscusRtc.CallType.VOICE) {
            try {
                wakeLock.acquire();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void releaseProximity() {
        if (callData != null && callData.getCallType() == QiscusRtc.CallType.VOICE) {
            try {
                if (wakeLock.isHeld()) {
                    wakeLock.release();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void configureProximity() {
        try {
            if (wakeLock != null) {
                field = PowerManager.class.getClass().getField("PROXIMITY_SCREEN_OFF_WAKE_LOCK").getInt(null);
            }
        } catch (Throwable ignored) {
            ignored.printStackTrace();
        }

        PowerManager powerManager = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(field, "PROXIMITY");
    }

    private void startVideoCall() {
        initCallingFragment();
        setupVideoProfile();
        setupBeginningLocalVideo();
    }

    private void startVoiceCall() {
        initCallingFragment();
        rtcEngine.setEnableSpeakerphone(false);
    }

    private void initCallingFragment() {
        callingFragment = CallingFragment.newInstance(callData);
        getSupportFragmentManager().beginTransaction().replace(R.id.call_fragment_container, callingFragment).commit();
    }

    private void initCallFragment() {
        callFragment = CallFragment.newInstance(callData);
        getSupportFragmentManager().beginTransaction().replace(R.id.call_fragment_container, callFragment).commit();
    }

    private void initView() {
        if (callData != null) {
            FrameLayout callFragmentContainer = findViewById(R.id.call_fragment_container);
            findViewById(R.id.local_video_view).setVisibility(callData.getCallType() == QiscusRtc.CallType.VOICE ? View.GONE : View.VISIBLE);
            findViewById(R.id.remote_video_view).setVisibility(callData.getCallType() == QiscusRtc.CallType.VOICE ? View.GONE : View.VISIBLE);

            callFragmentContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (callData.getCallType() == QiscusRtc.CallType.VIDEO && callFragment != null) {
                        callFragment.hidePanelButton();
                    }
                }
            });
        } else {
            finish();
        }

    }

    // Calling Fragment Listener
    @Override
    public void onAcceptPressed() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                callAccepted = true;
                joinChannel();
                autoDisconnect2();
                callingFragment.setTvCallState("Connecting");
            }
        });
    }

    @Override
    public void onRejectPressed() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                disconnect();
            }
        });
    }

    @Override
    public void onCancelPressed() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                disconnect();
            }
        });
    }

    // Call Fragment Listener
    @Override
    public void onSpeakerToggle(boolean speakerOn) {
        CallConfig.CallPanelListener.OnSpeakerClickListener listener = QiscusRtc.getCallConfig().getOnSpeakerClickListener();
        if (listener != null) {
            listener.onClick(speakerOn);
        }

        RingManager.getInstance(this).setSpeakerPhoneOn(speakerOn);
        rtcEngine.setEnableSpeakerphone(speakerOn);
    }

    @Override
    public void onMicToggle(boolean micOn) {
        CallConfig.CallPanelListener.OnMicClickListener listener = QiscusRtc.getCallConfig().getOnMicClickListener();
        if (listener != null) {
            listener.onClick(micOn);
        }

        rtcEngine.muteLocalAudioStream(!micOn);
    }

    @Override
    public void onVideoToggle(boolean videoOn) {
        CallConfig.CallPanelListener.OnVideoClickListener listener = QiscusRtc.getCallConfig().getOnVideoClickListener();
        if (listener != null) {
            listener.onClick(videoOn);
        }

        rtcEngine.muteLocalVideoStream(!videoOn);

        FrameLayout container = findViewById(R.id.local_video_view);
        SurfaceView surfaceView = (SurfaceView) container.getChildAt(0);
        surfaceView.setZOrderMediaOverlay(videoOn);
        surfaceView.setVisibility(!videoOn ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onCameraSwitch(boolean frontCamera) {
        CallConfig.CallPanelListener.OnCameraClickListener listener = QiscusRtc.getCallConfig().getOnCameraClickListener();
        if (listener != null) {
            listener.onClick(frontCamera);
        }

        rtcEngine.switchCamera();
    }

    @Override
    public void onPanelSlide(boolean hidden) {
        FrameLayout container = findViewById(R.id.local_video_view);
        container.setVisibility(View.VISIBLE);
        if (hidden) {
            int bottom = Math.round((DimUtil.convertDpToPixel(12, this)));
            setMargins(container, 0, 0, 12, bottom);
        } else {
            int bottom = Math.round((DimUtil.convertDpToPixel(140, this)));
            setMargins(container, 0, 0, 12, bottom);
        }
    }

    @Override
    public void hidePanelSlide() {
        findViewById(R.id.local_video_view).setVisibility(View.GONE);
    }

    @Override
    public void onEndCall(long callDurationMillis) {
        CallConfig.CallButtonListener.OnEndCallClickListener listener = QiscusRtc.getCallConfig().getOnEndCallClickListener();
        if (listener != null) {
            listener.onClick(callData, callDurationMillis);
        }

        disconnect();
    }

    private void disconnect() {
        Log.d(TAG, "disconnect: ");
        onRemoteUserLeft();
        NotificationManagerCompat.from(this).cancel(ON_GOING_NOTIF_ID);
        releaseProximity();

        if (rtcEngine != null) {
            rtcEngine.leaveChannel();
            RingManager.getInstance(this).stop();
            ringManager.playHangup(callData.getCallType());
        }

        RtcEngine.destroy();
        rtcEngine = null;

        if (!isFinishing()) {
            finish();
        }
    }

    private void showOnGoingCallNotification() {
        String notificationChannelId = getApplication().getPackageName() + ".qiscus.rtc.notification.channel";
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel notificationChannel =
                    new NotificationChannel(notificationChannelId, "Call", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                new Intent(this, QiscusCallActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(getApplicationContext(), notificationChannelId)
                .setContentTitle(getString(R.string.qiscus_call_on_going_call_notif))
                .setContentText(getString(R.string.qiscus_call_on_going_call_notif))
                .setSmallIcon(QiscusRtc.getCallConfig().getSmallOngoingNotifIcon())
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), QiscusRtc.getCallConfig().getLargeOngoingNotifIcon()))
                .setOngoing(true)
                .setAutoCancel(false)
                .setContentIntent(pendingIntent)
                .build();

        NotificationManagerCompat
                .from(this)
                .notify(ON_GOING_NOTIF_ID, notification);
    }

    private void setMargins(View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            view.requestLayout();
        }
    }

    @Override
    public void onBackPressed() {
        // do nothing
    }

    @Override
    protected void onDestroy() {
        Thread.setDefaultUncaughtExceptionHandler(null);
        NotificationManagerCompat.from(this).cancel(ON_GOING_NOTIF_ID);
        disconnect();
        QiscusRtc.getSession().clearLastSessionUid();
        super.onDestroy();
    }
}
