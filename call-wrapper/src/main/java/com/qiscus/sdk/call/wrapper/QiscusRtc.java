package com.qiscus.sdk.call.wrapper;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.qiscus.nirmana.Nirmana;
import com.qiscus.sdk.call.wrapper.data.config.CallConfig;
import com.qiscus.sdk.call.wrapper.data.local.Session;
import com.qiscus.sdk.call.wrapper.data.model.Account;
import com.qiscus.sdk.call.wrapper.data.model.Call;
import com.qiscus.sdk.call.wrapper.ui.QiscusCallActivity;

/**
 * Created on : May 03, 2018
 * Author     : zetbaitsu
 * Name       : Zetra
 * GitHub     : https://github.com/zetbaitsu
 */
public final class QiscusRtc {
    private static Application application;
    private static CallConfig callConfig;
    private static String key;
    private static Session session;

    private QiscusRtc() {

    }

    public static void init(Application application, String key) {
        init(application, key, new CallConfig());
    }

    public static void init(Application application, String key, CallConfig callConfig) {
        QiscusRtc.application = application;
        QiscusRtc.key = key;
        QiscusRtc.callConfig = callConfig;
        session = new Session(application);

        Nirmana.init(application);
    }

    public static Application getApplication() {
        return application;
    }

    public static String getKey() {
        return key;
    }

    public static CallConfig getCallConfig() {
        return callConfig;
    }

    public static void register(String username, String displayName, String avatarUrl) {
        Account account = new Account(username, displayName, avatarUrl);
        session.register(account);
    }

    public static boolean isRegistered() {
        return session.isRegistered();
    }

    public static Account getAccount() {
        if (!isRegistered()) {
            return null;
        }
        return new Account(session.getUsername(), session.getDisplayName(), session.getAvatarUrl());
    }

    public static void logout() {
        session.logout();
    }

    /**
     * CallType Enum
     */
    public enum CallType {
        VOICE,
        VIDEO
    }

    /**
     * CallAs Enum
     */
    public enum CallAs {
        CALLER,
        CALLEE
    }

    /**
     * CallEvent Enum
     */
    public enum CallEvent {
        CALLING,
        REJECT,
        CANCEL,
        END,
        PN_RECEIVED,
        INCOMING
    }

    /**
     * Use this method to start an Activity for call with other user.
     *
     * @param roomCallId generated manually of roomCallId from main app.
     * @return Call Activity builder
     */
    public static RequiredRoomId buildCallWith(String roomCallId) {
        return new CallActivityBuilder(roomCallId);
    }

    public interface RequiredRoomId {
        RequiredCallAs setCallAs(QiscusRtc.CallAs callAs);
    }

    public interface RequiredCallAs {
        OptionalMethod setCallType(QiscusRtc.CallType callType);
    }

    public interface OptionalMethod {
        OptionalMethod setCallerUsername(String callerUsername);

        OptionalMethod setCallerDisplayName(String callerDisplayName);

        OptionalMethod setCallerDisplayAvatar(String callerDisplayAvatar);

        OptionalMethod setCalleeUsername(String calleeUsername);

        OptionalMethod setCalleeDisplayName(String calleeDisplayName);

        OptionalMethod setCalleeDisplayAvatar(String calleeDisplayAvatar);

        void show(Context context);
    }

    public static class CallActivityBuilder implements RequiredRoomId, RequiredCallAs, OptionalMethod {
        private String roomCallId;
        private QiscusRtc.CallAs callAs;
        private QiscusRtc.CallType callType;
        private String callerUsername;
        private String callerDisplayName;
        private String callerDisplayAvatar;
        private String calleeUsername;
        private String calleeDisplayName;
        private String calleeDisplayAvatar;

        private CallActivityBuilder(String roomCallId) {
            this.roomCallId = roomCallId;
            this.callerUsername = "";
            this.callerDisplayName = "";
            this.callerDisplayAvatar = "";
            this.calleeUsername = "";
            this.calleeDisplayName = "";
            this.calleeDisplayAvatar = "";
        }

        /**
         * Set the setCallAs of call activity.
         *
         * @param callAs person as caller or callee.
         * @return builder
         */
        @Override
        public RequiredCallAs setCallAs(CallAs callAs) {
            this.callAs = callAs;
            return this;
        }

        /**
         * Set the setCallType of call activity.
         *
         * @param callType type of call video or voice.
         * @return builder
         */
        @Override
        public OptionalMethod setCallType(CallType callType) {
            this.callType = callType;
            return this;
        }

        /**
         * Set the setCallerUsername of call activity.
         *
         * @param callerUsername is email or number phone from caller.
         * @return builder
         */
        @Override
        public OptionalMethod setCallerUsername(String callerUsername) {
            this.callerUsername = callerUsername;
            return this;
        }

        /**
         * Set the setCallerDisplayName of call activity.
         *
         * @param callerDisplayName display avatar callee in call screen
         * @return builder
         */
        @Override
        public OptionalMethod setCallerDisplayName(String callerDisplayName) {
            this.callerDisplayName = callerDisplayName;
            return this;
        }

        /**
         * Set the setCallerAvatar of call activity.
         *
         * @param callerDisplayAvatar display avatar callee in call screen
         * @return builder
         */
        @Override
        public OptionalMethod setCallerDisplayAvatar(String callerDisplayAvatar) {
            this.callerDisplayAvatar = callerDisplayAvatar;
            return this;
        }

        /**
         * Set the setCalleeUsername of call activity.
         *
         * @param calleeUsername is email or number phone from callee.
         * @return builder
         */
        @Override
        public OptionalMethod setCalleeUsername(String calleeUsername) {
            this.calleeUsername = calleeUsername;
            return this;
        }

        /**
         * Set the setCalleeDisplayName of call activity.
         *
         * @param calleeDisplayName display avatar callee in call screen
         * @return builder
         */
        @Override
        public OptionalMethod setCalleeDisplayName(String calleeDisplayName) {
            this.calleeDisplayName = calleeDisplayName;
            return this;
        }

        /**
         * Set the setCalleeAvatar of call activity.
         *
         * @param calleeDisplayAvatar display avatar callee in call screen
         * @return builder
         */
        @Override
        public OptionalMethod setCalleeDisplayAvatar(String calleeDisplayAvatar) {
            this.calleeDisplayAvatar = calleeDisplayAvatar;
            return this;
        }

        /**
         * show the Call activity intent
         *
         * @param context Context for start the Activity
         */
        @Override
        public void show(Context context) {
            Call callData = new Call();
            callData.setRoomId(roomCallId);
            callData.setCallAs(callAs);
            callData.setCallType(callType);
            callData.setCallerUsername(callerUsername);
            callData.setCallerDisplayName(callerDisplayName);
            callData.setCallerAvatar(callerDisplayAvatar);
            callData.setCalleeUsername(calleeUsername);
            callData.setCalleeDisplayName(calleeDisplayName);
            callData.setCalleeAvatar(calleeDisplayAvatar);

            //if (!LocalDataManager.getInstance().isContainCallSession(roomCallId)) {
            //  LocalDataManager.getInstance().addCallSession(roomCallId);
            Intent intent = new Intent(QiscusCallActivity.generateIntent(context, callData));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            //}
        }
    }
}
