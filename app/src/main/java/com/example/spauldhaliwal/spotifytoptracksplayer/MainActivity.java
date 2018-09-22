package com.example.spauldhaliwal.spotifytoptracksplayer;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.client.CallResult;
import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.Capabilities;
import com.spotify.protocol.types.PlayerState;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

public class MainActivity extends AppCompatActivity implements Player {
    private static final String TAG = "MainActivity";

    private static final String CLIENT_ID = "7ae68c3c242644e49a86813d88579c9e";
    private static final String REDIRECT_URI = "https://github.com/spauldhaliwal/callback";
    private static final int REQUEST_CODE = 1333;

    private SpotifyAppRemote mpotifyAppRemote;
    private RequestQueue requestQueue;
    private String authToken;
    private AuthenticationRequest request;

    String artistId;
    private ArrayList<TrackModel> tracks;
    private TracksAdapter tracksAdapter;
    private RecyclerView recyclerView;
    private BottomSheetBehavior bottomSheetBehavior;
    private PlayerState playerState;
    private Subscription<PlayerState> playerStateSubscription;
    private MaterialProgressBar playProgressBar;

    private TrackProgressObserver observer = null;

    // TODO Safeguard against memory leaks for progress bar

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.trackListRecyclerView);
        requestQueue = Volley.newRequestQueue(this);

        View nowPlayingBottomSheet = findViewById(R.id.nowPlayingBottomSheet);
        bottomSheetBehavior = BottomSheetBehavior.from(nowPlayingBottomSheet);

        View bg = findViewById(R.id.bg);

        final FloatingActionButton playPauseButton = findViewById(R.id.playPauseFab);
        playProgressBar = findViewById(R.id.playProgressBar);
        playProgressBar.setMax(50);
        playProgressBar.setProgress(20);


        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mpotifyAppRemote != null) {

                    mpotifyAppRemote.getPlayerApi().getPlayerState().setResultCallback(new CallResult.ResultCallback<PlayerState>() {
                        @Override
                        public void onResult(PlayerState result) {
                            if (result.isPaused) {
                                mpotifyAppRemote.getPlayerApi().resume();
                            } else {
                                mpotifyAppRemote.getPlayerApi().pause();

                            }
                        }
                    });
                }
            }
        });

        nowPlayingBottomSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }
        });

        bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

            }
        });

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED)
                    findViewById(R.id.bg).setVisibility(View.GONE);
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                Log.d(TAG, "onSlide: slideOffset" + slideOffset + "");
                findViewById(R.id.bg).setVisibility(View.VISIBLE);
                findViewById(R.id.bg).setAlpha(slideOffset);
            }
        });


        if (authToken == null) {
            openLoginWindow();
        } else {
            Log.d(TAG, "onStart: authToken: " + authToken + "let's play some musaccx");
            playMusic("6b2oQwSGFkzsMtQruIWm2p");
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    private void onConnected(String trackId) {

        // Check to see if user can play tracks on demand.
        mpotifyAppRemote.getUserApi().getCapabilities().setResultCallback(new CallResult.ResultCallback<Capabilities>() {
            @Override
            public void onResult(Capabilities capabilities) {
                // Returns true if user can play tracks on demand
                Log.d(TAG, "onConnect getCapabilities result: " + capabilities.canPlayOnDemand);
            }
        });
        mpotifyAppRemote.getPlayerApi().play("spotify:track:" + trackId);

        observer = new TrackProgressObserver(playProgressBar, playerStateSubscription, mpotifyAppRemote);
        new Thread(observer).start();


    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void openLoginWindow() {
        request = new AuthenticationRequest
                .Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI)
                .setScopes(new String[]{"app-remote-control"})
                .build();
        request.getState();
        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    Log.d(TAG, "onActivityResult: authentication state: " + request.getState());
                    Log.d(TAG, "onActivityResult: auth token: " + response.getAccessToken());
                    authToken = response.getAccessToken();
                    getJson("4Z8W4fKeB5YxbusRsdQVPb");
                    playMusic("6b2oQwSGFkzsMtQruIWm2p");
                    break;

                // Auth flow returned an error
                case ERROR:
                    Log.d(TAG, "onActivityResult: responseError" + response.getError());
                    break;

                // Most likely auth flow was cancelled
                default:
                    Log.d(TAG, "onActivityResult " + response.getType());
            }
        }
    }

    public void playMusic(final String trackId) {
        ConnectionParams connectionParams =
                new ConnectionParams.Builder(CLIENT_ID)
                        .setRedirectUri(REDIRECT_URI)
                        .showAuthView(true)
                        .build();

        SpotifyAppRemote.CONNECTOR.connect(this, connectionParams,
                new Connector.ConnectionListener() {

                    @Override
                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {

                        mpotifyAppRemote = spotifyAppRemote;
                        Log.d("MainActivity", "Connected! Yay!");

                        // Now you can start interacting with App Remote
                        MainActivity.this.onConnected(trackId);
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.e("onFailure: ", throwable.getMessage(), throwable);

                        // Something went wrong when attempting to connect! Handle errors here
                    }

                });
    }

    public void getJson(String artistId) {
        Log.d(TAG, "getJson: starts");
        String url = "https://api.spotify.com/v1/artists/" + artistId + "/top-tracks?country=CA";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                tracks = new ArrayList<>();
                if (!response.equals(null)) {
                    try {
                        JSONArray jsonArray = response.getJSONArray("tracks");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject track = jsonArray.getJSONObject(i);
                            JSONObject album = track.getJSONObject("album");
                            JSONArray albumImageSet = album.getJSONArray("images");
                            JSONObject albumCoverArt = albumImageSet.getJSONObject(1);

                            String id = track.getString("id");
                            String title = track.getString("name");
                            String albumTitle = album.getString("name");
                            String albumCoverArtUrl = albumCoverArt.getString("url");
                            long durationInMs = track.getLong("duration_ms");

                            TrackModel trackModel = new TrackModel(id, title, albumTitle, albumCoverArtUrl, durationInMs);
                            tracks.add(trackModel);

                            tracksAdapter = new TracksAdapter(tracks, MainActivity.this);
                            recyclerView.setAdapter(tracksAdapter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                            recyclerView.setHasFixedSize(true);
                            tracksAdapter.notifyDataSetChanged();
                        }
//                    tracksAdapter.notifyDataSetChanged();
                        for (int i = 0; i < tracks.size(); i++) {
                            Log.d(TAG, "onResponse: Track " + i + ": " + tracks.get(i).toString());
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e("Your Array Response", "Data Null");
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onResponse success: " + error.toString());
            }
        }) {

            //This is for Headers If You Needed
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Log.d(TAG, "onResponse: authToken: " + authToken);
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + authToken);
                return headers;
            }

        };

        requestQueue.add(request);
    }

    @Override
    public void onBackPressed() {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }

}
