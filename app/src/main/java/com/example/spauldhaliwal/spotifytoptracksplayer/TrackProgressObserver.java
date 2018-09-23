package com.example.spauldhaliwal.spotifytoptracksplayer;

import android.media.MediaPlayer;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.spotify.android.appremote.api.ImagesApi;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.PlayerState;

import java.util.concurrent.atomic.AtomicBoolean;

public class TrackProgressObserver implements Runnable {
    private static final String TAG = "TrackProgressObserver";
    private AtomicBoolean stop = new AtomicBoolean(false);
    private ProgressBar progressBar;
    private SpotifyAppRemote spotifyAppRemote;
    TextView nowPlayingTile;
    TextView nowPlayingAlbum;
    ImageView albumArt;
    FloatingActionButton playPauseButton;

    TrackProgressObserver(ProgressBar progressBar, SpotifyAppRemote spotifyAppRemote, TextView nowPlayingTitle, TextView nowPlayingAlbum, FloatingActionButton playPauseButton) {
        this.progressBar = progressBar;
        this.spotifyAppRemote = spotifyAppRemote;
        this.nowPlayingTile = nowPlayingTitle;
        this.nowPlayingAlbum = nowPlayingAlbum;
        this.playPauseButton = playPauseButton;
    }

    public void stop() {
        Log.d(TAG, "TrackProgressObserver stops. stop = " + stop);
        stop.set(true);
    }

    @Override
    public void run() {
        Log.d(TAG, "TrackProgressObserver starts. stop = " + stop);
        while (!stop.get()) {
            final Subscription.EventCallback<PlayerState> playerStateEventCallback = new Subscription.EventCallback<PlayerState>() {
                @Override
                public void onEvent(PlayerState data) {
                    progressBar.setMax((int) data.track.duration);
                    progressBar.setProgress((int) data.playbackPosition);

                    nowPlayingTile.setText(data.track.name);
                    nowPlayingAlbum.setText(data.track.album.name);

                    if (data.isPaused) {
                        playPauseButton.setImageResource(android.R.drawable.ic_media_play);
                    } else {
                        playPauseButton.setImageResource(android.R.drawable.ic_media_pause);

                    }
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
