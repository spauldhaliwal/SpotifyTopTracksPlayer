package com.example.spauldhaliwal.spotifytoptracksplayer.model.listener;

import com.example.spauldhaliwal.spotifytoptracksplayer.model.impl.TrackModel;

public interface PlayerStateListener {

    void onStateUpdated(TrackModel trackState);

    void onPlayerRemoteConnected(String playlistId, TrackModel trackModel);
}
