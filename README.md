# Qiscus Call SDK Agora

Qiscus voice and video call SDK using agora engine.

## Quick Start

### Dependency

Add to your project build.gradle

```groovy
allprojects {
  repositories {
    maven { url  "https://dl.bintray.com/qiscustech/maven" }
    maven {
        url "https://artifactory.qiscus.com/artifactory/qiscus-library-open-source"
    }
  }
}
```

```groovy
dependencies {
  implementation 'com.qiscus.sdk:call-agora:1.0.0'
}
```

## Authentication

### Init Qiscus

Init Qiscus at your application

Parameters:
* application: Application
* key: String, agora key

```java
public class SampleApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        QiscusRtc.init(this, "myAgoraKey");
    }
}
```

## Method

### Register User

Before user can start call each other, they must register the user to our server

Parameters:
* username: String
* displayName: String
* avatarUrl: String

```java
QiscusRtc.register(username, displayName, avatarUrl);
```

Start call object:

* roomId: String
* callAs: Enum QiscusRtc.CallAs.CALLER / QiscusRtc.CallAs.CALLEE
* callType: Enum QiscusRtc.CallType.VOICE / QiscusRtc.CallType.VIDEO
* callerUsername: String
* calleeUsername: String
* callerDisplayName: String
* calleeAvatarUrl: String

### Start Call

#### Start voice call

```java
QiscusRtc.buildCallWith(roomId)
        .setCallAs(QiscusRtc.CallAs.CALLER)
        .setCallType(QiscusRtc.CallType.VOICE)
        .setCallerUsername(QiscusRtc.getUser())
        .setCalleeUsername(calleeUsername)
        .setCalleeDisplayName(calleeDisplayName)
        .setCalleeDisplayAvatar(calleeAvatarUrl)
        .show(context);
```
#### Start video call

```java
QiscusRtc.buildCallWith(roomId)
        .setCallAs(QiscusRtc.CallAs.CALLER)
        .setCallType(QiscusRtc.CallType.VIDEO)
        .setCallerUsername(QiscusRtc.getUser())
        .setCalleeUsername(calleeUsername)
        .setCalleeDisplayName(calleeDisplayName)
        .setCalleeDisplayAvatar(calleeAvatarUrl)
        .show(context);
```

### Custom your call

You can custom your call notification, icon and callback button action with ```QiscusRtc.getCallConfig()```

```java
QiscusRtc.getCallConfig()
        .setBackgroundDrawable(R.drawable.bg_call)
        .setOngoingNotificationEnable(true)
        .setLargeOngoingNotifIcon(R.drawable.ic_call_white_24dp);
```

That's it! You just need 3 steps to build voice call in your apps.