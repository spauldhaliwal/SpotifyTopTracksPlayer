package com.example.spauldhaliwal.spotifytoptracksplayer.view;

import com.example.spauldhaliwal.spotifytoptracksplayer.model.impl.TrackModel;

import java.util.List;

public interface MainActivityView {

    void displayTracks(List<TrackModel> tracksList);
    void updateProgress(int position, int duration);
    void updateNowPlayingBar(String title, String album);
    void updateResumePauseState(boolean isPaused);
    void updateNowPlayingAlbumArt(String albumCoverArtUrl);

    void expandNowPlayingBar();

    void toggleBottomSheet();

    void dismissBottomSheet();
}
