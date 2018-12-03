package com.example.spauldhaliwal.spotifytoptracksplayer.view;

import com.example.spauldhaliwal.spotifytoptracksplayer.model.impl.TrackModel;

import java.util.List;

public interface TrackListView {
    void displayTracks(List<TrackModel> tracksList);
}
