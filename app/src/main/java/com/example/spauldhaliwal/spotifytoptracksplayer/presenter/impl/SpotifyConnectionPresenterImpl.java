//package com.example.spauldhaliwal.spotifytoptracksplayer.presenter.impl;
//
//import com.example.spauldhaliwal.spotifytoptracksplayer.listener.TrackRepositoryListener;
//import com.example.spauldhaliwal.spotifytoptracksplayer.model.SpotifyTrackRepository;
//import com.example.spauldhaliwal.spotifytoptracksplayer.presenter.SpotifyConnectionPresenter;
//import com.example.spauldhaliwal.spotifytoptracksplayer.view.SpotifyConnectionActivityView;
//
//import java.util.List;
//
//public class SpotifyConnectionPresenterImpl implements SpotifyConnectionPresenter, TrackRepositoryListener{
//
//    private SpotifyConnectionActivityView view;
//    private SpotifyTrackRepository repository;
//
//    @Override
//    public void searchArtists() {
//        repository.getResult();
//        repository.addListener(this);
//    }
//
//    @Override
//    public void onArtistSelected() {
//
//    }
//
//    @Override
//    public void onResultsLoaded(List resultsAsList) {
//
//    }
//}
