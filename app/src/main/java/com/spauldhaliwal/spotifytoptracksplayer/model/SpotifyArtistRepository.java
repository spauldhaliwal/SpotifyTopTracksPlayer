package com.spauldhaliwal.spotifytoptracksplayer.model;

import com.spauldhaliwal.spotifytoptracksplayer.model.listener.ArtistRepositoryListener;

import java.util.List;

public interface SpotifyArtistRepository {

    void getResult(String searchParameter);

    void addListener(ArtistRepositoryListener listener);

    void resultLoaded(List resultAsList);
}
