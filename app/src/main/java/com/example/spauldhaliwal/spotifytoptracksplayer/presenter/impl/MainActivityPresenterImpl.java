package com.example.spauldhaliwal.spotifytoptracksplayer.presenter.impl;

import com.example.spauldhaliwal.spotifytoptracksplayer.listener.ArtistRepositoryListener;
import com.example.spauldhaliwal.spotifytoptracksplayer.listener.PlayerStateListener;
import com.example.spauldhaliwal.spotifytoptracksplayer.listener.PremiumAccountListener;
import com.example.spauldhaliwal.spotifytoptracksplayer.listener.TrackRepositoryListener;
import com.example.spauldhaliwal.spotifytoptracksplayer.model.Player;
import com.example.spauldhaliwal.spotifytoptracksplayer.model.SpotifyArtistRepository;
import com.example.spauldhaliwal.spotifytoptracksplayer.model.SpotifyTrackRepository;
import com.example.spauldhaliwal.spotifytoptracksplayer.model.impl.ArtistModel;
import com.example.spauldhaliwal.spotifytoptracksplayer.model.impl.TrackModel;
import com.example.spauldhaliwal.spotifytoptracksplayer.presenter.MainActivityPresenter;
import com.example.spauldhaliwal.spotifytoptracksplayer.view.MainActivityView;

import java.util.List;

public class MainActivityPresenterImpl implements MainActivityPresenter,
        TrackRepositoryListener,
        ArtistRepositoryListener,
        PlayerStateListener,
        PremiumAccountListener {

    private MainActivityView view;
    private SpotifyArtistRepository artistRepository;
    private SpotifyTrackRepository trackRepository;
    private Player player;

    public MainActivityPresenterImpl(MainActivityView view,
                                     SpotifyTrackRepository trackRepository,
                                     SpotifyArtistRepository artistRepository,
                                     Player player) {
        this.view = view;
        this.trackRepository = trackRepository;
        this.artistRepository = artistRepository;
        this.player = player;
    }

    @Override
    public void loadTracks(ArtistModel artistModel) {
        trackRepository.addListener(this);
        trackRepository.getResult(artistModel);
    }

    @Override
    public void onSearchArtist(String searchParamater) {
        artistRepository.addListener(this);
        artistRepository.getResult(searchParamater);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onArtistResultsLoaded(List resultsAsList) {

        view.displayArtists(resultsAsList);
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
        trackRepository.buildQueue(trackModel);
        view.onTrackLoading();
    }

    @Override
    public void trackLoadedFromRepository(Boolean trackIsFinishedLoading) {
        view.onTrackLoaded(trackIsFinishedLoading);
    }

    @Override
    public void onQueueBuildComplete(String queuePlaylistId, List trackList, TrackModel trackModel) {
        player.playPlaylist(queuePlaylistId, trackList, trackModel);
    }

    @Override
    public void onPlayerRemoteConnected(String playlistId, TrackModel trackModel) {
        trackRepository.playOverWebApi(playlistId, trackModel);
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
        boolean isPaused = trackState.isPaused();
        String id = trackState.getId();

        view.updateProgress(position, duration, id);
        view.updateNowPlayingBar(trackState);
        view.updateResumePauseState(isPaused);
    }

    @Override
    public void onHasPremiumAccount(Boolean hasPremiumAccount) {
        view.onHasPremiumAccount(hasPremiumAccount);
    }
}
