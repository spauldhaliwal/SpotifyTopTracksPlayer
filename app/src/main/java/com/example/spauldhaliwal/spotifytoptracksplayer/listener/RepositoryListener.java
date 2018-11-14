package com.example.spauldhaliwal.spotifytoptracksplayer.listener;

import java.util.List;

public interface RepositoryListener {

    void onResultsLoaded(List resultsAsList);
    void onQueueBuildComplete(String queuePlaylistId, List trackList);
}
