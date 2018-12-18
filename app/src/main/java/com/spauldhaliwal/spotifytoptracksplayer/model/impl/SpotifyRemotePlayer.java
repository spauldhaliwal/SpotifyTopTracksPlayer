package com.spauldhaliwal.spotifytoptracksplayer.model.impl;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.spauldhaliwal.spotifytoptracksplayer.Constants;
import com.spauldhaliwal.spotifytoptracksplayer.model.Player;
import com.spauldhaliwal.spotifytoptracksplayer.model.listener.PlayerStateListener;
import com.spauldhaliwal.spotifytoptracksplayer.model.listener.PremiumAccountListener;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.client.CallResult;
import com.spotify.protocol.types.Capabilities;
import com.spotify.protocol.types.PlayerState;

import java.util.ArrayList;
import java.util.List;

public class SpotifyRemotePlayer implements Player {
    private static final String TAG = "SpotifyRemotePlayer";

    private Context context;

    private SpotifyAppRemote spotifyAppRemote;

    private List<PlayerStateListener> playerStateListeners = new ArrayList<>();
    private List<PremiumAccountListener> premiumAccountListeners = new ArrayList<>();
    private Runnable stateObserverRunnableCode;
    private Handler stateObserver;

    private int silentTrackCounter = 0;
    private boolean isSilentTrackPlaying = true;
    private Handler trackLoadFailedHandler;
    private Runnable trackLoadFailedRunnable;

    public SpotifyRemotePlayer(Context context) {
        this.context = context;
    }

    @Override
    public void playTrack(TrackModel trackModel, List trackList) {
        connectAppRemote(trackModel, trackList);
    }

    @Override
    public void playPlaylist(String playlistId, List trackList, TrackModel trackModel) {
        connectAppRemote(playlistId, trackList, trackModel);
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
            spotifyAppRemote.getPlayerApi().seekTo(0).setResultCallback(empty -> spotifyAppRemote.getPlayerApi().skipPrevious());
        }
    }

    private void connectAppRemote(final TrackModel trackModel, final List trackList) {
        ConnectionParams connectionParams =
                new ConnectionParams.Builder(Constants.CLIENT_ID)
                        .setRedirectUri(Constants.REDIRECT_URI)
                        .showAuthView(true)
                        .build();

        SpotifyAppRemote.connect(context, connectionParams,
                new Connector.ConnectionListener() {
                    @Override
                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        // Now you can start interacting with App Remote
                        SpotifyRemotePlayer.this.spotifyAppRemote = spotifyAppRemote;
                        SpotifyRemotePlayer.this.onAppRemoteConnected(trackModel, trackList);
//                        broadcastState(trackList);
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.d(TAG, "onFailure: " + throwable.getMessage());
//                        Toast.makeText(context, "Connection Error. Retrying...", Toast.LENGTH_SHORT).show();
//                        connectAppRemote(trackModel, trackList);
                        // Something went wrong when attempting to connect! Handle errors here
                    }
                });
    }

    private void connectAppRemote(final String playlistId, final List trackList, TrackModel trackModel) {
        ConnectionParams connectionParams =
                new ConnectionParams.Builder(Constants.CLIENT_ID)
                        .setRedirectUri(Constants.REDIRECT_URI)
                        .showAuthView(true)
                        .build();

        SpotifyAppRemote.connect(context, connectionParams,
                new Connector.ConnectionListener() {
                    @Override
                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        // Now you can start interacting with App Remote
                        SpotifyRemotePlayer.this.spotifyAppRemote = spotifyAppRemote;
                        SpotifyRemotePlayer.this.onAppRemoteConnected(playlistId, trackModel);
                        if (stateObserver == null) {
                            broadcastState(trackList);
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.d(TAG, "onFailure: " + throwable.getMessage());
//                        Toast.makeText(context, "Connection Error. Retrying...", Toast.LENGTH_SHORT).show();
//                        connectAppRemote(playlistId, trackList, trackModel);
                        // Something went wrong when attempting to connect! Handle errors here
                    }
                });
    }

    private void onAppRemoteConnected(final TrackModel trackModel, final List trackList) {
        // Check to see if user can play tracks on demand.
        spotifyAppRemote.getUserApi().getCapabilities().setResultCallback(new CallResult.ResultCallback<Capabilities>() {
            @Override
            public void onResult(Capabilities capabilities) {
                // Returns true if user can play tracks on demand
                if (capabilities.canPlayOnDemand) {
                    Log.d(TAG, "onAppRemoteConnected: User has a premium account and can play tracks on demand");
                    canPlayPremiumContent(true);

                    // We play a silent track to prime the web api player, which fails unless something is already playing
                    spotifyAppRemote.getPlayerApi().play("spotify:track:" + Constants.SILENT_TRACK_ID);
                    spotifyAppRemote.getPlayerApi().setRepeat(2);
                    trackLoadFailedHandler = new Handler();
                    trackLoadFailedRunnable = new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "run: silentTrack");
                            spotifyAppRemote.getPlayerApi().getPlayerState().setResultCallback(new CallResult.ResultCallback<PlayerState>() {
                                @Override
                                public void onResult(PlayerState data) {
                                    Log.d(TAG, "onResult: starts");
                                    String id = data.track.uri.substring(14);
                                    if (id.equals(Constants.SILENT_TRACK_ID)) {
                                        Log.d(TAG, "onResult: silent track is playing");
                                        silentTrackCounter++;
                                        if (silentTrackCounter >=1 && silentTrackCounter<=5) {
                                            // Loading track failed (silent track is still playing)
                                            Log.d(TAG, "onResult: silentTrackCounter = " + silentTrackCounter);
                                            trackLoadFailed(trackModel, trackList);
                                        }
                                        isSilentTrackPlaying = true;
                                    } else {
                                        Log.d(TAG, "onResult: silent track is not playing");
                                        isSilentTrackPlaying = false;
                                        silentTrackCounter = 0;
                                    }
                                }
                            });
                            if (isSilentTrackPlaying) {
                                trackLoadFailedHandler.postDelayed(this, 3000); // reschedule the handler
                            } else {
                                trackLoadFailedHandler.removeCallbacks(this);
                            }
                        }
                    };
                    trackLoadFailedHandler.postDelayed(trackLoadFailedRunnable, 3000);

                } else {
                    Log.d(TAG, "onAppRemoteConnected: User can not play tracks on demand");
                    // TODO Play 30 second previews only
                    canPlayPremiumContent(false);
                }
            }
        });
    }

    /////////////////
    private void onAppRemoteConnected(final String playlistId, TrackModel trackModel) {
//        stateObserver = null;
        // Check to see if user can play tracks on demand.
        spotifyAppRemote.getUserApi().getCapabilities().setResultCallback(new CallResult.ResultCallback<Capabilities>() {
            @Override
            public void onResult(Capabilities capabilities) {
                // Returns true if user can play tracks on demand
                Log.d(TAG, "onResult: playing playlist through remote player");
                if (capabilities.canPlayOnDemand) {
                    Log.d(TAG, "onAppRemoteConnected playlist: User has a premium account and can play tracks on demand");
                    canPlayPremiumContent(true);
//                    spotifyAppRemote.getPlayerApi().play("spotify:playlist:" + playlistId);
                    spotifyAppRemote.getPlayerApi().setRepeat(2);
                    playerRemoteConnected(playlistId, trackModel);
                    stateObserver.removeCallbacks(stateObserverRunnableCode);
                    stateObserver.post(stateObserverRunnableCode);
//                    stateObserver.postDelayed(stateObserverRunnableCode, 1500);
                } else {
                    Log.d(TAG, "onAppRemoteConnected: User can not play tracks on demand");
                    // TODO Play 30 second previews only
                    canPlayPremiumContent(false);
                }
            }
        });
    }

    @Override
    public void addPremiumAccountListener(PremiumAccountListener listener) {
        premiumAccountListeners.clear();
        premiumAccountListeners.add(listener);
    }

    @Override
    public void addListener(PlayerStateListener listener) {
        playerStateListeners.clear();
        playerStateListeners.add(listener);
    }

    @Override
    public void removeListener(PlayerStateListener playerStateListener) {
        if (spotifyAppRemote != null) {
            stateObserver.removeCallbacks(stateObserverRunnableCode);
        }
    }

    @Override
    public void broadcastState(List trackList) {
        if (stateObserver != null) {
            stateObserver.removeCallbacks(stateObserverRunnableCode);
        }
        Log.d(TAG, "broadcastState: stateObserver: " + stateObserver);
        Log.d(TAG, "broadcastState: spotifyAppRemote: " + spotifyAppRemote);
        Log.d(TAG, "broadcastState: stateObserverRunnableCode" + stateObserverRunnableCode);
        stateObserver = new Handler();
        stateObserverRunnableCode = new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "broadcastState: starts");
                spotifyAppRemote.getPlayerApi().getPlayerState().setResultCallback(new CallResult.ResultCallback<PlayerState>() {
                    @Override
                    public void onResult(PlayerState data) {
                        Log.d(TAG, "onResult: starts");
                        String id = data.track.uri.substring(14);
                        String albumArtUrl = "";
                        int index = 0;

                        for (int i = 0; i < trackList.size(); i++) {
                            TrackModel trackModel = (TrackModel) trackList.get(i);
                            String trackId = trackModel.getId();
                            if (trackId.equals(id)) {
                                index = trackModel.getIndex();
                            }
                        }

                        TrackModel trackState = new TrackModel(id,
                                data.track.name,
                                data.track.album.name,
                                albumArtUrl,
                                data.track.duration,
                                data.playbackPosition,
                                data.isPaused,
                                index);

                        if (trackState.getId().equals(Constants.SILENT_TRACK_ID)) {
                            // attempt to play again
                        }
                        stateUpdated(trackState);
                    }
                });
                stateObserver.postDelayed(this, 2000);
            }

        };
        if (spotifyAppRemote != null) {
            stateObserver.postDelayed(stateObserverRunnableCode, 2000);
        }
    }

    @Override
    public void stateUpdated(TrackModel trackState) {
        for (PlayerStateListener playerStateListener : playerStateListeners)
            playerStateListener.onStateUpdated(trackState);
    }

    @Override
    public void trackLoadFailed(TrackModel trackModel, List trackList) {
        Log.d(TAG, "trackLoadFailed: called");
        trackLoadFailedHandler.removeCallbacks(trackLoadFailedRunnable);
        for (PlayerStateListener playerStateListener : playerStateListeners)
            playerStateListener.onTrackLoadFailed(trackModel, trackList);
    }

    @Override
    public void playerRemoteConnected(String playlistId, TrackModel trackModel) {
        for (PlayerStateListener playerStateListener : playerStateListeners)
            playerStateListener.onPlayerRemoteConnected(playlistId, trackModel);
    }

    @Override
    public void canPlayPremiumContent(boolean canPlayPremiumContent) {
        for (PremiumAccountListener premiumAccountListener : premiumAccountListeners)
            premiumAccountListener.onHasPremiumAccount(canPlayPremiumContent);
    }
}

