package com.example.spauldhaliwal.spotifytoptracksplayer.view.impl;

import com.example.spauldhaliwal.spotifytoptracksplayer.model.impl.TrackModel;

import java.util.List;

public interface AdapterHolder {
    void onTrackSelected(TrackModel trackModel, List trackList);
}
