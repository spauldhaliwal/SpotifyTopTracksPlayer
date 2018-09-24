package com.example.spauldhaliwal.spotifytoptracksplayer.presenter;

import com.example.spauldhaliwal.spotifytoptracksplayer.model.impl.TrackModel;

public interface MainActivityPresenter {

    void loadTracks();

    void onPauseResumeButtonClicked();

    void onTrackSelected(TrackModel trackModel);

    void listenForPlayerStateChanges();

    void removePlayerStateChangesListeners();

    void onNowPlayingBarClicked();

    void onNowPlayingBottomSheetClicked();

    void onBgClicked();
}
