package com.spauldhaliwal.spotifytoptracksplayer.view;

import com.spauldhaliwal.spotifytoptracksplayer.model.impl.TrackModel;

import java.util.List;

public interface TrackListView {
    void displayTracks(List<TrackModel> tracksList);
}
