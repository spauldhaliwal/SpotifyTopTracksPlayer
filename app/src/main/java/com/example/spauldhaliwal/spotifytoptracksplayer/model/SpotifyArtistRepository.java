package com.example.spauldhaliwal.spotifytoptracksplayer.model;

import com.example.spauldhaliwal.spotifytoptracksplayer.listener.ArtistRepositoryListener;

import java.util.List;

public interface SpotifyArtistRepository {

    void getResult(String searchParameter);

    void addListener(ArtistRepositoryListener listener);

    void resultLoaded(List resultAsList);
}
