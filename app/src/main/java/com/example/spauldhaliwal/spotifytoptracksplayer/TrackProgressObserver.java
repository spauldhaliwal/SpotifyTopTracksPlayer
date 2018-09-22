package com.example.spauldhaliwal.spotifytoptracksplayer;

import android.widget.ProgressBar;

import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.PlayerState;

import java.util.concurrent.atomic.AtomicBoolean;

public class TrackProgressObserver implements Runnable {
    private AtomicBoolean stop = new AtomicBoolean(false);
    private ProgressBar progressBar;
    private SpotifyAppRemote spotifyAppRemote;

    TrackProgressObserver(ProgressBar progressBar, SpotifyAppRemote spotifyAppRemote) {
        this.progressBar = progressBar;
        this.spotifyAppRemote = spotifyAppRemote;
    }

    public void stop() {
        stop.set(true);
    }

    @Override
    public void run() {
        while (!stop.get()) {
            final Subscription.EventCallback<PlayerState> playerStateEventCallback = new Subscription.EventCallback<PlayerState>() {
                @Override
                public void onEvent(PlayerState data) {
                    progressBar.setMax((int) data.track.duration);
                    progressBar.setProgress((int) data.playbackPosition);
                }
            };

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
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        }
    }
