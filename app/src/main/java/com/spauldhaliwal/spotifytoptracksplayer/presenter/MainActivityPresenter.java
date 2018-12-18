package com.spauldhaliwal.spotifytoptracksplayer.presenter;

import com.spauldhaliwal.spotifytoptracksplayer.model.impl.ArtistModel;
import com.spauldhaliwal.spotifytoptracksplayer.model.impl.TrackModel;

import java.util.List;

public interface MainActivityPresenter {

    void loadTracks(ArtistModel artistModel);

    void onSearchArtist(String searchParamater);

    void onPauseResumeButtonClicked();

    void onTrackSelected(TrackModel trackModel, List trackList);

    void onSkipTrackSelected();

    void onSkipPrevTrackSelected();

    void onNowPlayingBarClicked();

    void onNowPlayingBottomSheetClicked();

    void onBgClicked();

    void listenForPlayerStateChanges(List trackList);

    void removePlayerStateChangesListeners();

    void listenForPremiumAccount();

}
