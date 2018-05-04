package com.qiscus.sdk.call.agora.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.qiscus.sdk.call.agora.QiscusRtc;

/**
 * Created on : May 04, 2018
 * Author     : zetbaitsu
 * Name       : Zetra
 * GitHub     : https://github.com/zetbaitsu
 */
public class Call implements Parcelable {
    private QiscusRtc.CallAs callAs = QiscusRtc.CallAs.CALLER;
    private QiscusRtc.CallType callType = QiscusRtc.CallType.VIDEO;
    private String roomId;
    private String callerUsername;
    private String callerDisplayName;
    private String callerAvatar;
    private String calleeUsername;
    private String calleeDisplayName;
    private String calleeAvatar;

    public Call() {
        //
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setCallerUsername(String callerUsername) {
        this.callerUsername = callerUsername;
    }

    public String getCallerUsername() {
        return callerUsername;
    }

    public void setCallerDisplayName(String callerDisplayName) {
        this.callerDisplayName = callerDisplayName;
    }

    public String getCallerDisplayName() {
        return callerDisplayName;
    }

    public void setCallerAvatar(String callerAvatar) {
        this.callerAvatar = callerAvatar;
    }

    public String getCallerAvatar() {
        return callerAvatar;
    }

    public void setCalleeUsername(String calleeUsername) {
        this.calleeUsername = calleeUsername;
    }

    public String getCalleeUsername() {
        return calleeUsername;
    }

    public void setCalleeDisplayName(String calleeDisplayName) {
        this.calleeDisplayName = calleeDisplayName;
    }

    public String getCalleeDisplayName() {
        return calleeDisplayName;
    }

    public void setCalleeAvatar(String calleeAvatar) {
        this.calleeAvatar = calleeAvatar;
    }

    public String getCalleeAvatar() {
        return calleeAvatar;
    }

    public void setCallAs(QiscusRtc.CallAs callAs) {
        this.callAs = callAs;
    }

    public QiscusRtc.CallAs getCallAs() {
        return callAs;
    }

    public void setCallType(QiscusRtc.CallType callType) {
        this.callType = callType;
    }

    public QiscusRtc.CallType getCallType() {
        return callType;
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.callAs == null ? -1 : this.callAs.ordinal());
        dest.writeInt(this.callType == null ? -1 : this.callType.ordinal());
        dest.writeString(this.roomId);
        dest.writeString(this.callerUsername);
        dest.writeString(this.callerDisplayName);
        dest.writeString(this.callerAvatar);
        dest.writeString(this.calleeUsername);
        dest.writeString(this.calleeDisplayName);
        dest.writeString(this.calleeAvatar);
    }

    protected Call(Parcel in) {
        int tmpCallAs = in.readInt();
        this.callAs = tmpCallAs == -1 ? null : QiscusRtc.CallAs.values()[tmpCallAs];
        int tmpCallType = in.readInt();
        this.callType = tmpCallType == -1 ? null : QiscusRtc.CallType.values()[tmpCallType];
        this.roomId = in.readString();
        this.callerUsername = in.readString();
        this.callerDisplayName = in.readString();
        this.callerAvatar = in.readString();
        this.calleeUsername = in.readString();
        this.calleeDisplayName = in.readString();
        this.calleeAvatar = in.readString();
    }

    public static final Parcelable.Creator<Call> CREATOR = new Parcelable.Creator<Call>() {
        @Override
        public Call createFromParcel(Parcel source) {
            return new Call(source);
        }

        @Override
        public Call[] newArray(int size) {
            return new Call[size];
        }
    };

    @Override
    public String toString() {
        return "CallData{" +
                "callAs=" + callAs +
                ", callType=" + callType +
                ", roomId='" + roomId + '\'' +
                ", callerDisplayName='" + callerDisplayName + '\'' +
                ", callerAvatar='" + callerAvatar + '\'' +
                ", calleeUsername='" + calleeUsername + '\'' +
                ", callerUsername='" + callerUsername + '\'' +
                ", calleeDisplayName='" + calleeDisplayName + '\'' +
                ", calleeAvatar='" + calleeAvatar + '\'' +
                '}';
    }
}
