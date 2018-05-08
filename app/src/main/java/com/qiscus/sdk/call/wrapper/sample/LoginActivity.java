package com.qiscus.sdk.call.wrapper.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.qiscus.sdk.call.wrapper.QiscusRtc;

/**
 * Created on : May 04, 2018
 * Author     : zetbaitsu
 * Name       : Zetra
 * GitHub     : https://github.com/zetbaitsu
 */
public class LoginActivity extends AppCompatActivity {
    private EditText username;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username = findViewById(R.id.username);

        if (QiscusRtc.isRegistered()) {
            openMainPage();
        }
    }

    private void openMainPage() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public void register(View v) {
        if (!username.getText().toString().isEmpty()) {
            QiscusRtc.register(username.getText().toString(), username.getText().toString(),
                    "http://dk6kcyuwrpkrj.cloudfront.net/wp-content/uploads/sites/45/2014/05/avatar-blank.jpg");
            openMainPage();
        } else {
            Toast.makeText(LoginActivity.this, "Username required", Toast.LENGTH_SHORT).show();
        }
    }
}
