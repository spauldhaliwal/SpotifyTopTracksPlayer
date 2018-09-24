package com.example.spauldhaliwal.spotifytoptracksplayer.presenter;

import com.example.spauldhaliwal.spotifytoptracksplayer.model.impl.TrackModel;

public interface Presenter {

    void loadTracks();
    void onPauseResumeButtonClicked();
    void onTrackSelected(TrackModel trackModel);
    void listenForPlayerStateChanges();
    void removePlayerStateChangesListeners();
}
