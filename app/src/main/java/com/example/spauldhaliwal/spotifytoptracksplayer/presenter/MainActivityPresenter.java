package com.example.spauldhaliwal.spotifytoptracksplayer.presenter;

import com.example.spauldhaliwal.spotifytoptracksplayer.model.impl.TrackModel;

import java.util.List;

public interface MainActivityPresenter {

    void loadTracks();

    void onPauseResumeButtonClicked();

    void onTrackSelected(TrackModel trackModel, List trackList);

    void onSkipTrackSelected();

    void onSkipPrevTrackSelected();

    void onNowPlayingBarClicked();

    void onNowPlayingBottomSheetClicked();

    void onBgClicked();

    void listenForPlayerStateChanges();

    void removePlayerStateChangesListeners();

    void listenForPremiumAccount();

}
