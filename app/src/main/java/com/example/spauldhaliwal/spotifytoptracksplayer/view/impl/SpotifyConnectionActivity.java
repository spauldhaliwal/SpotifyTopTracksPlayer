package com.example.spauldhaliwal.spotifytoptracksplayer.view.impl;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.spauldhaliwal.spotifytoptracksplayer.Constants;
import com.example.spauldhaliwal.spotifytoptracksplayer.R;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

public class SpotifyConnectionActivity extends AppCompatActivity {
    private static final String TAG = "SpotifyConnectionActivi";

    private String authToken;
    private ProgressBar progressBar;
    private Button connectButton;
    private TextView connectionMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spotify_connection);

        if (authToken == null) {
            openLoginWindow();
        } else {
            startMainActivity();
        }

        connectionMessage = findViewById(R.id.connectToSpotifyText);
        progressBar = findViewById(R.id.connectToSpotifyProgress);
        connectButton = findViewById(R.id.connectToSpotifyButton);

        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectionMessage.setText(R.string.contacting_spotify);
                progressBar.setVisibility(View.VISIBLE);
                connectButton.setVisibility(View.GONE);
                openLoginWindow();
            }
        });
    }

    private void openLoginWindow() {
        AuthenticationRequest request = new AuthenticationRequest
                .Builder(Constants.CLIENT_ID, AuthenticationResponse.Type.TOKEN, Constants.REDIRECT_URI)
                .setScopes(new String[]{"app-remote-control"})
                .build();
        request.getState();
        AuthenticationClient.openLoginActivity(this, Constants.REQUEST_CODE, request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == Constants.REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    connectionMessage.setText(R.string.loading_player);
                    authToken = response.getAccessToken();
                    startMainActivity();
                    break;

                // Auth flow returned an error
                case ERROR:
                    Log.d(TAG, "onActivityResult: responseError" + response.getError());
                    connectionMessage.setText(R.string.contacting_spotify_error);
                    progressBar.setVisibility(View.GONE);
                    connectButton.setVisibility(View.VISIBLE);
                    break;

                // Most likely auth flow was cancelled
                default:
                    connectionMessage.setText(R.string.contacting_spotify_error);
                    progressBar.setVisibility(View.GONE);
                    connectButton.setVisibility(View.VISIBLE);
                    Log.d(TAG, "onActivityResult " + response.getType());
            }
        }
    }

    protected void startMainActivity() {
        Intent playerActivityIntent = new Intent(this, MainActivity.class);
        playerActivityIntent.putExtra("authToken", authToken);
        playerActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(playerActivityIntent);
        finish();
    }
}