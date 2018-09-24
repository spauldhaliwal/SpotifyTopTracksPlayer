package com.example.spauldhaliwal.spotifytoptracksplayer.model;

import com.example.spauldhaliwal.spotifytoptracksplayer.listener.PlayerStateListener;
import com.example.spauldhaliwal.spotifytoptracksplayer.model.impl.TrackModel;

public interface Player {

    void playTrack(TrackModel trackModel);

    void pauseResume();

    void broadcastState();

    void stateUpdated(TrackModel trackState);

    void addListener(PlayerStateListener listener);

    void removeListener(PlayerStateListener listener);
}
