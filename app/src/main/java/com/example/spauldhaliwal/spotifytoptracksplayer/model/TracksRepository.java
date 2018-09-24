package com.example.spauldhaliwal.spotifytoptracksplayer.model;

import com.example.spauldhaliwal.spotifytoptracksplayer.listener.RepositoryListener;

import java.util.List;

public interface TracksRepository {

    void getTracks();
    void addListener(RepositoryListener listener);
    void tracksLoaded(List tracksList);
}
