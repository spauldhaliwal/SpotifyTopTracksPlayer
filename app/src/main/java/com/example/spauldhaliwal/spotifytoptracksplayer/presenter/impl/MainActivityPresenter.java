package com.example.spauldhaliwal.spotifytoptracksplayer.presenter.impl;

import android.util.Log;

import com.example.spauldhaliwal.spotifytoptracksplayer.listener.RepositoryListener;
import com.example.spauldhaliwal.spotifytoptracksplayer.model.Player;
import com.example.spauldhaliwal.spotifytoptracksplayer.model.impl.TrackModel;
import com.example.spauldhaliwal.spotifytoptracksplayer.model.TracksRepository;
import com.example.spauldhaliwal.spotifytoptracksplayer.presenter.Presenter;
import com.example.spauldhaliwal.spotifytoptracksplayer.view.MainActivityView;

import java.util.List;

public class MainActivityPresenter implements Presenter, RepositoryListener {
    private static final String TAG = "MainActivityPresenter";

    MainActivityView view;
    private TracksRepository repository;
    List tracksList;

    private Player player;


    public MainActivityPresenter(MainActivityView view, TracksRepository repository, Player player) {
        this.view = view;
        this.repository = repository;
        this.player = player;
    }

    public void loadTracks() {
        Log.d(TAG, "loadTracks: starts");
        repository.addListener(this);
        tracksList = repository.getTracks();

    }

    @Override
    public void onTrackSelected(TrackModel trackModel) {
        player.playTrack(trackModel);
    }

    @Override
    public void onPauseResumeButtonClicked() {
        player.pauseResume();

    }

    @Override
    public void onTracksLoaded(List tracksList) {
        view.displayTracks(tracksList);

    }


}
