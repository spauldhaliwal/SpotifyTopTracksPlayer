//package com.example.spauldhaliwal.spotifytoptracksplayer.presenter.impl;
//
//import com.example.spauldhaliwal.spotifytoptracksplayer.listener.RepositoryListener;
//import com.example.spauldhaliwal.spotifytoptracksplayer.model.SpotifyLookupRepository;
//import com.example.spauldhaliwal.spotifytoptracksplayer.presenter.SpotifyConnectionPresenter;
//import com.example.spauldhaliwal.spotifytoptracksplayer.view.SpotifyConnectionActivityView;
//
//import java.util.List;
//
//public class SpotifyConnectionPresenterImpl implements SpotifyConnectionPresenter, RepositoryListener{
//
//    private SpotifyConnectionActivityView view;
//    private SpotifyLookupRepository repository;
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
