package com.example.spauldhaliwal.spotifytoptracksplayer.listener;

import com.example.spauldhaliwal.spotifytoptracksplayer.model.impl.TrackModel;

public interface PlayerStateListener {

    void onStateUpdated(TrackModel trackState);
}
