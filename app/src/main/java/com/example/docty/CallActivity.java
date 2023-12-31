package com.example.docty;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Switch;
import android.widget.Toast;

import io.agora.rtc2.ChannelMediaOptions;
import io.agora.rtc2.Constants;
import io.agora.rtc2.IRtcEngineEventHandler;
import io.agora.rtc2.RtcEngine;
import io.agora.rtc2.RtcEngineConfig;
import io.agora.rtc2.video.VideoCanvas;

public class CallActivity extends AppCompatActivity {

    // Fill the App ID of your project generated on Agora Console.
    private final String appId = "37ee21a6eed841d58a6a6aff122d157d";
    // Fill the channel name.
    private String channelName = "docty";
    // Fill the temp token generated on Agora Console.
    private String token = "007eJxTYDgqyGfK8nFjxXHZBe6zJ2qGSz0RrL2i8e3aDb458xYtSvFVYDA2T001Mkw0S01NsTAxTDG1SDQDwrQ0QyOjFENT85T03etSGgIZGVandbAyMkAgiM/KkJKfXFLJwAAATzwgGQ==";
    // An integer that identifies the local user.
    private int uid = 0;
    private boolean isJoined = false;

    private RtcEngine agoraEngine;
    //SurfaceView to render local video in a Container.
    private SurfaceView localSurfaceView;
    //SurfaceView to render Remote video in a Container.
    private SurfaceView remoteSurfaceView;
    // A toggle switch to change the User role.
    private Switch audienceRole;


    private void setupLocalVideo() {
        FrameLayout container = findViewById(R.id.local_video_view_container);
        // Create a SurfaceView object and add it as a child to the FrameLayout.
        localSurfaceView = new SurfaceView(getBaseContext());
        container.addView(localSurfaceView);
        // Call setupLocalVideo with a VideoCanvas having uid set to 0.
        agoraEngine.setupLocalVideo(new VideoCanvas(localSurfaceView, VideoCanvas.RENDER_MODE_HIDDEN, 0));
    }

    private void setupRemoteVideo(int uid) {
        FrameLayout container = findViewById(R.id.remote_video_view_container);
        remoteSurfaceView = new SurfaceView(getBaseContext());
        remoteSurfaceView.setZOrderMediaOverlay(true);
        container.addView(remoteSurfaceView);
        agoraEngine.setupRemoteVideo(new VideoCanvas(remoteSurfaceView, VideoCanvas.RENDER_MODE_FIT, uid));
        // Display RemoteSurfaceView.
        remoteSurfaceView.setVisibility(View
                .VISIBLE);
    }

    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {
        @Override
        // Listen for the remote host joining the channel to get the uid of the host.
        public void onUserJoined(int uid, int elapsed) {
//            showMessage("Remote user joined " + uid);
            if (!audienceRole.isChecked()) return;
            // Set the remote video view
            runOnUiThread(() -> setupRemoteVideo(uid));
        }

        @Override
        public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
            isJoined = true;
//            showMessage("Joined Channel " + channel);
        }

        @Override
        public void onUserOffline(int uid, int reason) {
//            showMessage("Remote user offline " + uid + " " + reason);
            runOnUiThread(() -> remoteSurfaceView.setVisibility(View.GONE));
        }
    };

    private void setupVideoSDKEngine() {
        try {
            RtcEngineConfig config = new RtcEngineConfig();
            config.mContext = getBaseContext();
            config.mAppId = appId;
            config.mEventHandler = mRtcEventHandler;
            agoraEngine = RtcEngine.create(config);
            // By default, the video module is disabled, call enableVideo to enable it.
            agoraEngine.enableVideo();
        } catch (Exception e) {
            System.out.println(e.toString());
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public void joinChannel(View view) {
            ChannelMediaOptions options = new ChannelMediaOptions();
            // For Live Streaming, set the channel profile as LIVE_BROADCASTING.
            options.channelProfile = Constants.CHANNEL_PROFILE_LIVE_BROADCASTING;
            // Set the client role as BROADCASTER or AUDIENCE according to the scenario.
            if (audienceRole.isChecked()) { //Audience
                options.clientRoleType = Constants.CLIENT_ROLE_AUDIENCE;
            } else { //Host
                options.clientRoleType = Constants.CLIENT_ROLE_BROADCASTER;
                // Display LocalSurfaceView.
                setupLocalVideo();
                localSurfaceView.setVisibility(View.VISIBLE);
                // Start local preview.
                agoraEngine.startPreview();
            }
            audienceRole.setEnabled(false); // Disable the switch
            // Join the channel with a temp token.
            // You need to specify the user ID yourself, and ensure that it is unique in the channel.
            agoraEngine.joinChannel(token, channelName, uid, options);
    }

    public void leaveChannel(View view) {
        if (!isJoined) {
//            showMessage("Join a channel first");
        } else {
            agoraEngine.leaveChannel();
//            showMessage("You left the channel");
            // Stop remote video rendering.
            if (remoteSurfaceView != null) remoteSurfaceView.setVisibility(View.GONE);
            // Stop local video rendering.
            if (localSurfaceView != null) localSurfaceView.setVisibility(View.GONE);
            isJoined = false;
        }
        audienceRole.setEnabled(true); // Enable the switch
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);
        audienceRole = (Switch) findViewById(R.id.switch1);
        // If all the permissions are granted, initialize the RtcEngine object and join a channel.
//        if (!checkSelfPermission()) {
//            ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, PERMISSION_REQ_ID);
//        }
        setupVideoSDKEngine();
    }
    protected void onDestroy() {
        super.onDestroy();
        agoraEngine.stopPreview();
        agoraEngine.leaveChannel();

        // Destroy the engine in a sub-thread to avoid congestion
        new Thread(() -> {
            RtcEngine.destroy();
            agoraEngine = null;
        }).start();
    }
}