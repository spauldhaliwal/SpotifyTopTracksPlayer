package com.example.spauldhaliwal.spotifytoptracksplayer.model;

import com.example.spauldhaliwal.spotifytoptracksplayer.listener.RepositoryListener;
import com.example.spauldhaliwal.spotifytoptracksplayer.model.impl.TrackModel;

import java.util.List;

public interface TracksRepository {

    List<TrackModel> getTracks();
    void addListener(RepositoryListener listener);
    void tracksLoaded(List tracksList);
}
