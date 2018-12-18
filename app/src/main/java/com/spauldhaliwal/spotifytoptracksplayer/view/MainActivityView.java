package com.spauldhaliwal.spotifytoptracksplayer.view;

import com.spauldhaliwal.spotifytoptracksplayer.model.impl.ArtistModel;
import com.spauldhaliwal.spotifytoptracksplayer.model.impl.TrackModel;

import java.util.List;

public interface MainActivityView {

    void displayTracks(List<TrackModel> tracksList);

    void displayArtists(List<ArtistModel> artistList);

    void updateProgress(int position, int duration, String id);

    void updateNowPlayingBar(TrackModel trackModel);

    void updateResumePauseState(boolean isPaused);

    void expandNowPlayingBar();

    void toggleBottomSheet();

    void dismissBottomSheet();

    void onHasPremiumAccount(boolean canPlayPremiumContent);

    void onTrackLoading();

    void onTrackLoaded(Boolean isFinished);

}
