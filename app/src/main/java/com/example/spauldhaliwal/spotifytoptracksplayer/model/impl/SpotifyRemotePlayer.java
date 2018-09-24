package com.example.spauldhaliwal.spotifytoptracksplayer.model.impl;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.spauldhaliwal.spotifytoptracksplayer.Constants;
import com.example.spauldhaliwal.spotifytoptracksplayer.model.Player;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.client.CallResult;
import com.spotify.protocol.types.Capabilities;
import com.spotify.protocol.types.PlayerState;

public class SpotifyRemotePlayer implements Player {
    private static final String TAG = "SpotifyRemotePlayer";

    private String CLIENT_ID;
    private String REDIRECT_URI;

    private Context context;

    SpotifyAppRemote spotifyAppRemote;

    public SpotifyRemotePlayer(Context context) {
        this.context = context;
    }

    @Override
    public void pauseResume() {

        if (spotifyAppRemote != null) {
            spotifyAppRemote.getPlayerApi().getPlayerState().setResultCallback(new CallResult.ResultCallback<PlayerState>() {
                @Override
                public void onResult(PlayerState result) {

                    if (result.isPaused) {
                        spotifyAppRemote.getPlayerApi().resume();
//                        progressObserver.post(progressRunnableCode);
                    } else {
                        spotifyAppRemote.getPlayerApi().pause();
//                        progressObserver.removeCallbacks(progressRunnableCode);

                    }
                }
            });
        }

    }

    public void playTrack(TrackModel trackModel) {
        connectAppRemote(trackModel);
    }

    private void connectAppRemote(final TrackModel trackModel) {

        ConnectionParams connectionParams =
                new ConnectionParams.Builder(Constants.CLIENT_ID)
                        .setRedirectUri(Constants.REDIRECT_URI)
                        .showAuthView(true)
                        .build();

        SpotifyAppRemote.CONNECTOR.connect(context, connectionParams,
                new Connector.ConnectionListener() {

                    @Override
                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {

                        SpotifyRemotePlayer.this.spotifyAppRemote = spotifyAppRemote;
                        Log.d("MainActivity", "Connected! Yay!");

                        // Now you can start interacting with App Remote
                        SpotifyRemotePlayer.this.onAppRemoteConnected(trackModel);
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.d(TAG, "onFailure: " + throwable.getMessage());
                        Toast.makeText(context, "Error fetching track, retrying...", Toast.LENGTH_SHORT).show();
                        connectAppRemote(trackModel);
                        // Something went wrong when attempting to connect! Handle errors here
                    }

                });

    }

    private void onAppRemoteConnected(TrackModel trackModel) {

        // Check to see if user can play tracks on demand.
        spotifyAppRemote.getUserApi().getCapabilities().setResultCallback(new CallResult.ResultCallback<Capabilities>() {
            @Override
            public void onResult(Capabilities capabilities) {
                // Returns true if user can play tracks on demand
                Log.d(TAG, "onConnect getCapabilities result: " + capabilities.canPlayOnDemand);
            }
        });
        spotifyAppRemote.getPlayerApi().play("spotify:track:" + trackModel.getId());

//        Glide.with(nowPlayingAlbumLarge)
//                .load(trackModel.getAlbumCoverArtUrl())
//                .into(nowPlayingAlbumLarge);
//
//        progressObserver.post(progressRunnableCode);

    }
}
