package com.spauldhaliwal.spotifytoptracksplayer.model.listener;

import com.spauldhaliwal.spotifytoptracksplayer.model.impl.TrackModel;

import java.util.List;

public interface TrackRepositoryListener {

    void onResultsLoaded(List resultsAsList);
    void trackLoadedFromRepository(Boolean trackIsFinishedLoading);
    void onQueueBuildComplete(String queuePlaylistId, List trackList, TrackModel trackModel);
}
