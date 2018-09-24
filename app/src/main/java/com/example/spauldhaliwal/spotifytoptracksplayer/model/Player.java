package com.example.spauldhaliwal.spotifytoptracksplayer.model;

import com.example.spauldhaliwal.spotifytoptracksplayer.model.impl.TrackModel;

public interface Player {
    void playTrack(TrackModel trackModel);
    void pauseResume();
}
