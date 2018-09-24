package com.example.spauldhaliwal.spotifytoptracksplayer.model.impl;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.example.spauldhaliwal.spotifytoptracksplayer.Constants;
import com.example.spauldhaliwal.spotifytoptracksplayer.listener.PlayerStateListener;
import com.example.spauldhaliwal.spotifytoptracksplayer.model.Player;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.client.CallResult;
import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.Capabilities;
import com.spotify.protocol.types.PlayerState;

import java.util.ArrayList;
import java.util.List;

public class SpotifyRemotePlayer implements Player {
    private static final String TAG = "SpotifyRemotePlayer";

    private Context context;

    private SpotifyAppRemote spotifyAppRemote;

    private List<PlayerStateListener> listeners = new ArrayList<>();
    private Runnable stateObserverRunnableCode;
    private Handler stateObserver;

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
                    } else {
                        spotifyAppRemote.getPlayerApi().pause();
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
                        // Now you can start interacting with App Remote
                        SpotifyRemotePlayer.this.spotifyAppRemote = spotifyAppRemote;
                        SpotifyRemotePlayer.this.onAppRemoteConnected(trackModel);
                        broadcastState();
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.d(TAG, "onFailure: " + throwable.getMessage());
                        Toast.makeText(context, "Connection Error. Retrying...", Toast.LENGTH_SHORT).show();
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
        stateObserver.post(stateObserverRunnableCode);
    }

    @Override
    public void addListener(PlayerStateListener listener) {
        listeners.clear();
        listeners.add(listener);
        for (int i =0; i<listeners.size(); i++) {
            Log.d(TAG, "addListener: size = " + i+1);
        }
    }

    @Override
    public void removeListener(PlayerStateListener playerStateListener) {
        if (spotifyAppRemote != null) {
            stateObserver.removeCallbacks(stateObserverRunnableCode);
        }
        listeners.clear();
    }

    @Override
    public void broadcastState() {
        stateObserver = new Handler();
        stateObserverRunnableCode = new Runnable() {
            @Override
            public void run() {
                final Subscription.EventCallback<PlayerState> playerStateEventCallback = new Subscription.EventCallback<PlayerState>() {
                    @Override
                    public void onEvent(PlayerState data) {
                        TrackModel trackState = new TrackModel(data.track.name,
                                data.track.album.name,
                                data.track.duration,
                                data.playbackPosition,
                                data.isPaused);
                        stateUpdated(trackState);
                    }
                };
                if (spotifyAppRemote != null) {
                    spotifyAppRemote.getPlayerApi().subscribeToPlayerState()
                            .setEventCallback(playerStateEventCallback)
                            .setLifecycleCallback(new Subscription.LifecycleCallback() {
                                @Override
                                public void onStart() {
                                }
                                @Override
                                public void onStop() {
                                }
                            });
                }
                stateObserver.postDelayed(this, 2000);
            }
        };
    }

    @Override
    public void stateUpdated(TrackModel trackState) {
        for (PlayerStateListener playerStateListener : listeners)
            playerStateListener.onStateUpdated(trackState);
    }
}
