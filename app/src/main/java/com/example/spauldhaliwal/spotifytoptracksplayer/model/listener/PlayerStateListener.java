package com.example.spauldhaliwal.spotifytoptracksplayer.model.listener;

import com.example.spauldhaliwal.spotifytoptracksplayer.model.impl.TrackModel;

import java.util.List;

public interface PlayerStateListener {

    void onStateUpdated(TrackModel trackState);

    void onTrackLoadFailed(TrackModel trackModel, List trackList);

    void onPlayerRemoteConnected(String playlistId, TrackModel trackModel);
}
