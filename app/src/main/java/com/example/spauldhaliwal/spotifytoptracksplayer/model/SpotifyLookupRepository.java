package com.example.spauldhaliwal.spotifytoptracksplayer.model;

import com.example.spauldhaliwal.spotifytoptracksplayer.listener.RepositoryListener;
import com.example.spauldhaliwal.spotifytoptracksplayer.model.impl.TrackModel;

import java.util.List;

public interface SpotifyLookupRepository {

    void getResult(TrackModel trackModel);

    void addListener(RepositoryListener listener);

    void resultLoaded(List resultAsList);

    void buildQueue(TrackModel trackModel);

    void queueBuildComplete(String playlistId);

    void playOverWebApi(String playlist);
}
