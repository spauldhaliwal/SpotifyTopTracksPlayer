package com.example.spauldhaliwal.spotifytoptracksplayer.model;

import com.example.spauldhaliwal.spotifytoptracksplayer.listener.RepositoryListener;

import java.util.List;

public interface SpotifyLookupRepository {

    void getResult();

    void addListener(RepositoryListener listener);

    void resultLoaded(List resultAsList);
}
