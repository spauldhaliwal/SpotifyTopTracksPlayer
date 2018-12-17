package com.example.spauldhaliwal.spotifytoptracksplayer.model;

import com.example.spauldhaliwal.spotifytoptracksplayer.model.impl.ArtistModel;
import com.example.spauldhaliwal.spotifytoptracksplayer.model.impl.TrackModel;
import com.example.spauldhaliwal.spotifytoptracksplayer.model.listener.TrackRepositoryListener;

import java.util.List;

public interface SpotifyTrackRepository {

    void getResult(ArtistModel artistModel);

    void addListener(TrackRepositoryListener listener);

    void resultLoaded(List resultAsList);

    void trackFinishedLoading(boolean isFinished);

    void buildQueue(TrackModel trackModel);

    void queueBuildComplete(String playlistId, List trackList, TrackModel trackModel);

    void playOverWebApi(String playlist, TrackModel trackModel);
}
