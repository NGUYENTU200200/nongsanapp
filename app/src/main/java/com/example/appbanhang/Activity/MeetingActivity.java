package com.example.appbanhang.Activity;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.appbanhang.R;
import com.example.appbanhang.Retrofit.ApiBanHang;
import com.example.appbanhang.Retrofit.RetrofitClient;
import com.example.appbanhang.Utils.Utils;
import com.example.appbanhang.fragment.SpeakerFragment;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import live.videosdk.rtc.android.Meeting;
import live.videosdk.rtc.android.VideoSDK;
import live.videosdk.rtc.android.listeners.MeetingEventListener;

public class MeetingActivity extends AppCompatActivity {
    private Meeting meeting;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ApiBanHang apiBanHang;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_meeting);
        apiBanHang = RetrofitClient.getInstance(Utils.BASE_URL).create(ApiBanHang.class);

        final String meetingId = getIntent().getStringExtra("meetingId");
        String token = getIntent().getStringExtra("token");
        String mode = getIntent().getStringExtra("mode");
        String localParticipantName = "App bán hàng";
        boolean streamEnable = mode.equals("CONFERENCE");
        postDataToMeeting(meetingId,token);

        VideoSDK.initialize(getApplicationContext());

        // Configuration VideoSDK with Token
        VideoSDK.config(token);

        // Initialize VideoSDK Meeting
        meeting = VideoSDK.initMeeting(
                MeetingActivity.this, meetingId, localParticipantName,
                streamEnable, streamEnable, null, mode, false, null);

        // join Meeting
        meeting.join();
        // if mode is CONFERENCE than replace mainLayout with SpeakerFragment otherwise with ViewerFragment
        meeting.addEventListener(new MeetingEventListener() {
            @Override
            public void onMeetingJoined() {
                if (meeting != null) {
                    if (mode.equals("CONFERENCE")) {
                        //pin the local partcipant
                        meeting.getLocalParticipant().pin("SHARE_AND_CAM");
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.mainLayout, new SpeakerFragment(), "MainFragment")
                                .commit();
                    }
                }
            }
        });

    }

    private void postDataToMeeting(String meetingId, String token) {
    compositeDisposable.add(apiBanHang.postMeeting(meetingId, token)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                    messagerModel -> {
                        Log.d("logg","post ok");
                    },
                    throwable -> {
                        Log.d("logg",throwable.getMessage());
                    }
            ));


    }

    public Meeting getMeeting() {
        return meeting;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}