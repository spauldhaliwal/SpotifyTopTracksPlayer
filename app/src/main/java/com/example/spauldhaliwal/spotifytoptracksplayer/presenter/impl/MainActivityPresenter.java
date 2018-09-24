package com.example.spauldhaliwal.spotifytoptracksplayer.presenter.impl;

import android.util.Log;

import com.example.spauldhaliwal.spotifytoptracksplayer.listener.PlayerStateListener;
import com.example.spauldhaliwal.spotifytoptracksplayer.listener.RepositoryListener;
import com.example.spauldhaliwal.spotifytoptracksplayer.model.Player;
import com.example.spauldhaliwal.spotifytoptracksplayer.model.TracksRepository;
import com.example.spauldhaliwal.spotifytoptracksplayer.model.impl.TrackModel;
import com.example.spauldhaliwal.spotifytoptracksplayer.presenter.Presenter;
import com.example.spauldhaliwal.spotifytoptracksplayer.view.MainActivityView;
import com.spotify.protocol.types.PlayerState;

import java.util.List;

public class MainActivityPresenter implements Presenter, RepositoryListener, PlayerStateListener {
    private static final String TAG = "MainActivityPresenter";

    private MainActivityView view;
    private TracksRepository repository;
    private List tracksList;

    private Player player;


    public MainActivityPresenter(MainActivityView view, TracksRepository repository, Player player) {
        this.view = view;
        this.repository = repository;
        this.player = player;
    }

    @Override
    public void loadTracks() {
        Log.d(TAG, "loadTracks: starts");
        repository.addListener(this);
        tracksList = repository.getTracks();

    }

    @Override
    public void listenForPlayerStateChanges() {
        player.addListener(this);
        player.broadcastState();
    }

    @Override
    public void removePlayerStateChangesListeners() {
        Log.d(TAG, "removePlayerStateChangesListeners: starts");
        player.removeListener(this);
    }

    @Override
    public void onTrackSelected(TrackModel trackModel) {
        player.playTrack(trackModel);
        view.updateNowPlayingAlbumArt(trackModel.getAlbumCoverArtUrl());
    }

    @Override
    public void onPauseResumeButtonClicked() {
        player.pauseResume();

    }

    @Override
    public void onTracksLoaded(List tracksList) {
        view.displayTracks(tracksList);

    }

    @Override
    public void onStateUpdated(PlayerState data) {

        int position = (int) data.playbackPosition;
        int duration = (int) data.track.duration;

        String title = data.track.name;
        String album = data.track.album.name;

        boolean isPaused = data.isPaused;

        view.updateProgress(position, duration);
        view.updateNowPlayingBar(title, album);
        view.updateResumePauseState(isPaused);


    }
}
