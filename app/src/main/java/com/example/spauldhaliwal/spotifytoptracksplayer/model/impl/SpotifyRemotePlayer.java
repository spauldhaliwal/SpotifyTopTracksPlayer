package com.example.spauldhaliwal.spotifytoptracksplayer.model.impl;

import android.content.Context;
import android.media.session.MediaController;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.spauldhaliwal.spotifytoptracksplayer.Constants;
import com.example.spauldhaliwal.spotifytoptracksplayer.listener.PlayerStateListener;
import com.example.spauldhaliwal.spotifytoptracksplayer.listener.PremiumAccountListener;
import com.example.spauldhaliwal.spotifytoptracksplayer.model.Player;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.client.CallResult;
import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.Capabilities;
import com.spotify.protocol.types.ListItem;
import com.spotify.protocol.types.PlayerState;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SpotifyRemotePlayer implements Player {
    private static final String TAG = "SpotifyRemotePlayer";

    private Context context;

    private SpotifyAppRemote spotifyAppRemote;

    private List<PlayerStateListener> playerStateListeners = new ArrayList<>();
    private List<PremiumAccountListener> premiumAccountListeners = new ArrayList<>();
    private Runnable stateObserverRunnableCode;
    private Handler stateObserver;


    public SpotifyRemotePlayer(Context context) {
        this.context = context;
    }

    @Override
    public void playTrack(TrackModel trackModel) {
        connectAppRemote(trackModel);
    }

    @Override
    public void playPlaylist(String playlistId) {
        connectAppRemote(playlistId);
    }

    @Override
    public void pauseResumeTrack() {
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

    @Override
    public void skipTrack() {
        if (spotifyAppRemote != null) {
            spotifyAppRemote.getPlayerApi().skipNext();
        }
    }

    @Override
    public void skipPrevTrack() {
        if (spotifyAppRemote != null) {
            spotifyAppRemote.getPlayerApi().skipPrevious();
        }
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

    private void connectAppRemote(final String playlistId) {
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
                        SpotifyRemotePlayer.this.onAppRemoteConnected(playlistId);
                        broadcastState();
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.d(TAG, "onFailure: " + throwable.getMessage());
                        Toast.makeText(context, "Connection Error. Retrying...", Toast.LENGTH_SHORT).show();
                        connectAppRemote(playlistId);
                        // Something went wrong when attempting to connect! Handle errors here
                    }
                });
    }

    private void onAppRemoteConnected(final TrackModel trackModel) {
        // Check to see if user can play tracks on demand.
        spotifyAppRemote.getUserApi().getCapabilities().setResultCallback(new CallResult.ResultCallback<Capabilities>() {
            @Override
            public void onResult(Capabilities capabilities) {
                // Returns true if user can play tracks on demand
                if (capabilities.canPlayOnDemand) {
                    Log.d(TAG, "onAppRemoteConnected: User has a premium account and can play tracks on demand");
                    canPlayPremiumContent(true);
                    spotifyAppRemote.getPlayerApi().play("spotify:track:" + trackModel.getId());
                    stateObserver.post(stateObserverRunnableCode);
                } else {
                    Log.d(TAG, "onAppRemoteConnected: User can not play tracks on demand");
                    // TODO Play 30 second previews only
                    canPlayPremiumContent(false);
                }
            }
        });
    }

    private void onAppRemoteConnected(final String playlistId) {
        // Check to see if user can play tracks on demand.
        spotifyAppRemote.getUserApi().getCapabilities().setResultCallback(new CallResult.ResultCallback<Capabilities>() {
            @Override
            public void onResult(Capabilities capabilities) {
                // Returns true if user can play tracks on demand
                if (capabilities.canPlayOnDemand) {
                    Log.d(TAG, "onAppRemoteConnected: User has a premium account and can play tracks on demand");
                    canPlayPremiumContent(true);

                    // Playback delayed because playlist on spotify server not always up to date.
                    // (Or the spotify player's cache is out of date)
                    Handler handler = new Handler();
                    Runnable r = new Runnable() {
                        public void run() {
                            spotifyAppRemote.getPlayerApi().play("spotify:user:spotify:playlist:" + playlistId);
//                            spotifyAppRemote.getPlayerApi().queue("spotify:track:7wVwKqDtZ5EZHghJ82XGw9");
                            trackLoaded();
                        }
                    };
                    stateObserver.post(stateObserverRunnableCode);
                    handler.postDelayed(r, 1500);
                } else {
                    Log.d(TAG, "onAppRemoteConnected: User can not play tracks on demand");
                    // TODO Play 30 second previews only
                    canPlayPremiumContent(false);
                }
            }
        });
    }

    @Override
    public void addListener(PlayerStateListener listener) {
        Log.d(TAG, "addListener: starts");
        playerStateListeners.clear();
        playerStateListeners.add(listener);
    }

    @Override
    public void addPremiumAccountListener(PremiumAccountListener listener) {
        premiumAccountListeners.clear();
        premiumAccountListeners.add(listener);
    }

    @Override
    public void removeListener(PlayerStateListener playerStateListener) {
        if (spotifyAppRemote != null) {
            stateObserver.removeCallbacks(stateObserverRunnableCode);
        }
        playerStateListeners.clear();
    }

    @Override
    public void broadcastState() {
        stateObserver = new Handler();
        stateObserverRunnableCode = new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "BroadcastState: starts");
                spotifyAppRemote.getPlayerApi().getPlayerState().setResultCallback(new CallResult.ResultCallback<PlayerState>() {
                    @Override
                    public void onResult(PlayerState data) {
                        Log.d(TAG, "onResult: starts");
                        TrackModel trackState = new TrackModel(data.track.name,
                                data.track.album.name,
                                data.track.duration,
                                data.playbackPosition,
                                data.isPaused);
                        stateUpdated(trackState);
                    }
                });
                stateObserver.postDelayed(this, 2000);
            }
        };
    }

    @Override
    public void stateUpdated(TrackModel trackState) {
        for (PlayerStateListener playerStateListener : playerStateListeners)
            playerStateListener.onStateUpdated(trackState);
    }

    @Override
    public void trackLoaded() {
        for (PlayerStateListener playerStateListener : playerStateListeners)
            playerStateListener.onTrackLoaded();
    }

    @Override
    public void canPlayPremiumContent(boolean canPlayPremiumContent) {
        for (PremiumAccountListener premiumAccountListener : premiumAccountListeners)
            premiumAccountListener.onHasPremiumAccount(canPlayPremiumContent);
    }
}

