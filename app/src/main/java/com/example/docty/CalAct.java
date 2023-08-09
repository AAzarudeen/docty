package com.example.docty;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import io.agora.agorauikit_android.AgoraConnectionData;
import io.agora.agorauikit_android.AgoraSettings;
import io.agora.agorauikit_android.AgoraVideoViewer;
import io.agora.agorauikit_android.DevicePermissionsKt;
import io.agora.rtc2.Constants;

public class CalAct extends AppCompatActivity {

    AgoraVideoViewer apViewer;

    String appId = "8fc993637fca4d6187b5479ab929d54c";

    String token = "007eJxTYCjRSnD22xj10JRl4YuTG7QLA7WzJJ0NZW20LBtenWEq3qfAYJGWbGlpbGZsnpacaJJiZmhhnmRqYm6ZmGRpZJliapJsznMqpSGQkWGVQQszIwMEgvisDCn5ySWVDAwAdPwcfQ==";

    String channelName = "docty";

    private AgoraVideoViewer agView = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cal);
        initializeAndJoinChannel();
    }

        private void initializeAndJoinChannel() {
            // Create AgoraVideoViewer instance
            try {
                agView = new AgoraVideoViewer(this, new AgoraConnectionData(appId, token), AgoraVideoViewer.Style.FLOATING, new AgoraSettings(), null);
                Log.i("Steps","Step One");
            } catch (Exception e) {
                Log.e("AgoraVideoViewer",
                        "Could not initialize AgoraVideoViewer. Check that your app Id is valid.");
                Log.e("Exception", e.toString());
                return;
            }

            // Add the AgoraVideoViewer to the Activity layout
            this.addContentView(agView, new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT)
            );
            Log.i("Steps","Step 2");


            // Check permission and join a channel
            if (DevicePermissionsKt.requestPermissions(AgoraVideoViewer.Companion, this)) {
                Log.i("Steps","Step 3");
                joinChannel();
                Log.i("Steps","Step 3 finish");

            } else {
                Button joinButton = new Button(this);
                joinButton.setText("Allow camera and microphone access, then click here");
                joinButton.setOnClickListener(new View.OnClickListener() {
                    // When the button is clicked, check permissions again and join channel
                    @Override
                    public void onClick(View view) {
                        if (DevicePermissionsKt.requestPermissions(AgoraVideoViewer.Companion, getApplicationContext())) {
                            ((ViewGroup) joinButton.getParent()).removeView(joinButton);
                            joinChannel();
                        }
                    }
                });
                this.addContentView(joinButton, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 200));
            }
        }

        void joinChannel(){
            agView.join(channelName, token, Constants.CLIENT_ROLE_BROADCASTER, 0);
            Log.i("Steps","Step 4");
        }

}