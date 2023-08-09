package com.example.docty.fragments;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.docty.R;
import com.example.docty.activity.MainActivity;

import io.agora.rtc2.ChannelMediaOptions;
import io.agora.rtc2.Constants;
import io.agora.rtc2.IRtcEngineEventHandler;
import io.agora.rtc2.RtcEngine;
import io.agora.rtc2.RtcEngineConfig;
import io.agora.rtc2.video.VideoCanvas;

public class VideoCallFragment extends Fragment {

    Button joinButton;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public VideoCallFragment() {

    }

    FrameLayout remote;
    FrameLayout local;

    public static VideoCallFragment newInstance(String param1, String param2) {
        VideoCallFragment fragment = new VideoCallFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private static final int PERMISSION_REQ_ID = 22;
    private static final String[] REQUESTED_PERMISSIONS =
            {
                    android.Manifest.permission.RECORD_AUDIO,
                    android.Manifest.permission.CAMERA
            };

    private boolean checkSelfPermission()
    {
        if (ContextCompat.checkSelfPermission(getContext(), REQUESTED_PERMISSIONS[0]) !=  PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(getContext(), REQUESTED_PERMISSIONS[1]) !=  PackageManager.PERMISSION_GRANTED)
        {
            return false;
        }
        return true;
    }

    private int uid = 2;

    private boolean isJoined = false;

    private RtcEngine agoraEngin;
    private SurfaceView localSurfaceView;
    private SurfaceView remoteSurfaceView;

    private void setupVideoSDKEngine(){
        try {
            RtcEngineConfig config = new RtcEngineConfig();
            config.mContext = getActivity().getBaseContext();
            config.mAppId = getResources().getString(R.string.appId);
            config.mEventHandler = mRtcHandler;
            agoraEngin = RtcEngine.create(config);
            agoraEngin.enableVideo();
            joinChannel();
        }catch (Exception e){
            showMessage(e.toString());
        }
    }

    void showMessage(String message){
        Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private final IRtcEngineEventHandler mRtcHandler = new IRtcEngineEventHandler() {
        @Override
        public void onUserJoined(int uid, int elapsed) {
            super.onUserJoined(uid, elapsed);
            showMessage("Remote user joined "+uid);
            getActivity().runOnUiThread(()->setupRemoteVideo(uid));
        }

        @Override
        public void onError(int err) {
            super.onError(err);
            System.out.println(err);
            Toast.makeText(getContext(), err+"  Error", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
            super.onJoinChannelSuccess(channel, uid, elapsed);
            isJoined = true;
            showMessage("Joined Channel "+channel);
        }

        @Override
        public void onUserOffline(int uid, int reason) {
            super.onUserOffline(uid, reason);
            showMessage("Remote user offline "+uid +" "+reason);
            getActivity().runOnUiThread(()->remoteSurfaceView.setVisibility(View.GONE));
        }
    };

    private void setupRemoteVideo(int uid) {
        remoteSurfaceView = new SurfaceView(getActivity().getBaseContext());
        remoteSurfaceView.setZOrderMediaOverlay(true);
        remote.addView(remoteSurfaceView);
        agoraEngin.setupRemoteVideo(new VideoCanvas(remoteSurfaceView, VideoCanvas.RENDER_MODE_FIT, uid));
        remoteSurfaceView.setVisibility(View.VISIBLE);
    }

    private void setupLocalVideo(){
        localSurfaceView = new SurfaceView(getActivity().getBaseContext());
        local.addView(localSurfaceView);
        agoraEngin.setupLocalVideo(new VideoCanvas(localSurfaceView, VideoCanvas.RENDER_MODE_HIDDEN, uid));
    }

    public void joinChannel(){
        if (isJoined){
            if(checkSelfPermission()){
                ChannelMediaOptions options = new ChannelMediaOptions();
                options.channelProfile = Constants.CHANNEL_PROFILE_COMMUNICATION;
                options.clientRoleType = Constants.CLIENT_ROLE_BROADCASTER;
                setupLocalVideo();
                localSurfaceView.setVisibility(View.VISIBLE);
                agoraEngin.startPreview();
                agoraEngin.joinChannel(getResources().getString(R.string.token),getResources().getString(R.string.channel), uid, options);
            }else {
                showMessage("Permission was not granted");
            }
        }
        else{
            showMessage("JOin a channel");
        }
    }

    public void leaveChannel(){
        if(!isJoined){
            showMessage("Join a channel first");
        }else{
            agoraEngin.leaveChannel();
            showMessage("You left channel");
            if(remoteSurfaceView != null) remoteSurfaceView.setVisibility(View.GONE);
            if(localSurfaceView != null) localSurfaceView.setVisibility(View.GONE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video_call, container, false);
        remote = view.findViewById(R.id.remoteVideo);
        local = view.findViewById(R.id.localVideo);

        joinButton = view.findViewById(R.id.joinButton);
        joinButton.setOnClickListener(v->{
            setupVideoSDKEngine();
        });

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        agoraEngin.stopPreview();
        agoraEngin.leaveChannel();

        new Thread(()->{
            RtcEngine.destroy();
            agoraEngin = null;
        }).start();
    }
}