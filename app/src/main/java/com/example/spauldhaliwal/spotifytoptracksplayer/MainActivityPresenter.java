package com.example.spauldhaliwal.spotifytoptracksplayer;

import android.util.Log;

import com.example.spauldhaliwal.spotifytoptracksplayer.Repositories.TracksRepository;
import com.example.spauldhaliwal.spotifytoptracksplayer.Repositories.impl.Top10TracksRepository;

import java.util.List;

public class MainActivityPresenter implements Presenter, RepositoryListener{
    private static final String TAG = "MainActivityPresenter";
    MainActivityView view;
    TracksRepository repository;
    List tracksList;

    public MainActivityPresenter(MainActivityView view, TracksRepository repository) {
        this.view = view;
        this.repository = repository;
    }

    public void loadTracks() {
        Log.d(TAG, "loadTracks: starts");
        repository.addListener(this);
        tracksList = repository.getTracks();

    }

    @Override
    public void onTracksLoaded(List tracksList) {
        view.displayTracks(tracksList);

    }


}
