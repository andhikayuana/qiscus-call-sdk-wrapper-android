package com.qiscus.sdk.call.wrapper.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.qiscus.sdk.call.wrapper.QiscusRtc;

public class MainActivity extends AppCompatActivity {
    private RadioGroup call_as;
    private RadioGroup call_type;
    private EditText target;
    private EditText roomId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        target = findViewById(R.id.target);
        roomId = findViewById(R.id.room_id);
        roomId.setText(generateRoomCall());

        call_as = findViewById(R.id.call_as);
        call_type = findViewById(R.id.call_type);
    }

    private String generateRoomCall() {
        String room = "CallRoom_" + String.valueOf(System.currentTimeMillis());
        return room;
    }

    public void startCall(View view) {
        int selectedId;

        selectedId = call_as.getCheckedRadioButtonId();
        RadioButton callAs = findViewById(selectedId);

        selectedId = call_type.getCheckedRadioButtonId();
        RadioButton callType = findViewById(selectedId);

        if (!target.getText().toString().isEmpty() || !roomId.getText().toString().isEmpty()) {
            if (callAs.getText().toString().equals("Caller")) {
                QiscusRtc.buildCallWith(roomId.getText().toString())
                        .setCallAs(QiscusRtc.CallAs.CALLER)
                        .setCallType(callType.getText().toString().equals("Voice") ? QiscusRtc.CallType.VOICE : QiscusRtc.CallType.VIDEO)
                        .setCallerUsername(QiscusRtc.getAccount().getUsername())
                        .setCalleeUsername(target.getText().toString())
                        .setCalleeDisplayName(target.getText().toString())
                        .setCalleeDisplayAvatar("http://dk6kcyuwrpkrj.cloudfront.net/wp-content/uploads/sites/45/2014/05/avatar-blank.jpg")
                        .show(this);
            } else {
                QiscusRtc.buildCallWith(roomId.getText().toString())
                        .setCallAs(QiscusRtc.CallAs.CALLEE)
                        .setCallType(callType.getText().toString().equals("Voice") ? QiscusRtc.CallType.VOICE : QiscusRtc.CallType.VIDEO)
                        .setCalleeUsername(QiscusRtc.getAccount().getUsername())
                        .setCallerUsername(target.getText().toString())
                        .setCallerDisplayName(target.getText().toString())
                        .setCallerDisplayAvatar("http://dk6kcyuwrpkrj.cloudfront.net/wp-content/uploads/sites/45/2014/05/avatar-blank.jpg")
                        .show(this);
            }
        } else {
            Toast.makeText(this, "Target and room required", Toast.LENGTH_SHORT).show();
        }
    }
}
