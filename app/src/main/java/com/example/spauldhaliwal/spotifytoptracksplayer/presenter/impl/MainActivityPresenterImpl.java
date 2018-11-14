package com.example.spauldhaliwal.spotifytoptracksplayer.presenter.impl;

import android.util.Log;

import com.example.spauldhaliwal.spotifytoptracksplayer.listener.PlayerStateListener;
import com.example.spauldhaliwal.spotifytoptracksplayer.listener.PremiumAccountListener;
import com.example.spauldhaliwal.spotifytoptracksplayer.listener.RepositoryListener;
import com.example.spauldhaliwal.spotifytoptracksplayer.model.Player;
import com.example.spauldhaliwal.spotifytoptracksplayer.model.SpotifyLookupRepository;
import com.example.spauldhaliwal.spotifytoptracksplayer.model.impl.TrackModel;
import com.example.spauldhaliwal.spotifytoptracksplayer.presenter.MainActivityPresenter;
import com.example.spauldhaliwal.spotifytoptracksplayer.view.MainActivityView;

import java.util.List;

public class MainActivityPresenterImpl implements MainActivityPresenter, RepositoryListener, PlayerStateListener, PremiumAccountListener {
    private static final String TAG = "MainActivityPresenterIm";
    private MainActivityView view;
    private SpotifyLookupRepository repository;
    private Player player;

    public MainActivityPresenterImpl(MainActivityView view, SpotifyLookupRepository repository, Player player) {
        this.view = view;
        this.repository = repository;
        this.player = player;
    }

    @Override
    public void loadTracks() {
        repository.addListener(this);
        repository.getResult(null);
    }

    @Override
    public void listenForPlayerStateChanges(List trackList) {
        player.addListener(this);
        player.broadcastState(trackList);
    }

    @Override
    public void removePlayerStateChangesListeners() {
        player.removeListener(this);
    }

    @Override
    public void listenForPremiumAccount() {
        player.addPremiumAccountListener(this);
    }

    @Override
    public void onTrackSelected(TrackModel trackModel, List trackList) {
        listenForPremiumAccount();
        listenForPlayerStateChanges(trackList);
        player.playTrack(trackModel, trackList);
        repository.buildQueue(trackModel);
        view.updateNowPlayingAlbumArt(trackModel.getAlbumCoverArtUrl());
        view.onLoadingTrack();
    }

    @Override
    public void onTrackLoaded() {
        view.onTrackLoaded();
    }

    @Override
    public void onQueueBuildComplete(String queuePlaylistId, List trackList) {
        player.playPlaylist(queuePlaylistId, trackList);
    }

    @Override
    public void onPlayerRemoteConnected(String playlistId) {
        repository.playOverWebApi(playlistId);
    }

    @Override
    public void onPauseResumeButtonClicked() {
        player.pauseResumeTrack();

    }

    @Override
    public void onSkipTrackSelected() {
        player.skipTrack();
    }

    @Override
    public void onSkipPrevTrackSelected() {
        player.skipPrevTrack();
    }

    @Override
    public void onNowPlayingBarClicked() {
        view.expandNowPlayingBar();
    }

    @Override
    public void onNowPlayingBottomSheetClicked() {
        view.toggleBottomSheet();
    }

    @Override
    public void onBgClicked() {
        view.dismissBottomSheet();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onResultsLoaded(List resultsAsList) {
        view.displayTracks(resultsAsList);

    }

    @Override
    public void onStateUpdated(TrackModel trackState) {

        int position = (int) trackState.getPositionInMs();
        int duration = (int) trackState.getDurationInMs();
        String id = trackState.getId();

        String title = trackState.getTitle();
        String albumTitle = trackState.getAlbumTitle();

        String albumArtUrl = trackState.getAlbumCoverArtUrl();

        Log.d(TAG, "onStateUpdated: " + trackState.getAlbumCoverArtUrl());

        boolean isPaused = trackState.isPaused();

        view.updateProgress(position, duration, id);
        view.updateNowPlayingBar(title, albumTitle);
        view.updateResumePauseState(isPaused);
        view.updateNowPlayingAlbumArt(albumArtUrl);
    }

    @Override
    public void onHasPremiumAccount(Boolean hasPremiumAccount) {
        view.onHasPremiumAccount(hasPremiumAccount);
    }

    @Override
    public String toString() {
        return "MainActivityPresenterImpl{" +
                "view=" + view +
                ", repository=" + repository +
                ", player=" + player +
                '}';
    }
}
