package com.example.appbanhang.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.appbanhang.R;

import org.json.JSONException;
import org.json.JSONObject;

public class JoinActivity extends AppCompatActivity {
    private String Token ="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhcGlrZXkiOiI1NjJlNWI3YS01Zjg5LTRkMjItYjFmZS0xODVkNTRkNWM2ODYiLCJwZXJtaXNzaW9ucyI6WyJhbGxvd19qb2luIl0sImlhdCI6MTcxMjE1NTg0NCwiZXhwIjoxNzE5OTMxODQ0fQ.eiULUS3vtsCJXNCv9488O7fcomYvEU3GQCTuxiUPJY4";
    private static final int PERMISSION_REQ_ID = 22;

    private static final String[] REQUESTED_PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_join);

        checkSelfPermission(REQUESTED_PERMISSIONS[0], PERMISSION_REQ_ID);
        checkSelfPermission(REQUESTED_PERMISSIONS[1], PERMISSION_REQ_ID);

        final Button btnCreate = findViewById(R.id.btnCreatMeeting);
        final Button btnJoinHost = findViewById(R.id.btnJoinHostMeeting);
        final Button btnJoinViewer = findViewById(R.id.btnJoinViewerMeeting);
        final EditText etMeetingId = findViewById(R.id.etMeetingId);

        // create meeting and join as Host
        btnCreate.setOnClickListener(v -> createMeeting(Token));

        // Join as Host
        btnJoinHost.setOnClickListener(v -> {
            Intent intent = new Intent(JoinActivity.this, MeetingActivity.class);
            intent.putExtra("token", Token);
            intent.putExtra("meetingId", etMeetingId.getText().toString().trim());
            intent.putExtra("mode", "CONFERENCE");
            startActivity(intent);
        });
    }

    private void createMeeting(String token) {
        AndroidNetworking.post("https://api.videosdk.live/v2/rooms")
                .addHeaders("Authorization", token) //we will pass the token in the Headers
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // response will contain `roomId`
                            final String meetingId = response.getString("roomId");

                            // starting the MeetingActivity with received roomId and our sampleToken
                            Intent intent = new Intent(JoinActivity.this, MeetingActivity.class);
                            intent.putExtra("token", Token);
                            intent.putExtra("meetingId", meetingId);
                            intent.putExtra("mode", "CONFERENCE");
                            startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        anError.printStackTrace();
                        Toast.makeText(JoinActivity.this, anError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void checkSelfPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) !=
                PackageManager.PERMISSION_GRANTED) {
            Activity.requestPermissions(this, REQUESTED_PERMISSIONS, requestCode);
        }
    }
    }
