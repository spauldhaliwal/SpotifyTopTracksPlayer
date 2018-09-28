package com.example.spauldhaliwal.spotifytoptracksplayer.model;

import com.example.spauldhaliwal.spotifytoptracksplayer.listener.PlayerStateListener;
import com.example.spauldhaliwal.spotifytoptracksplayer.listener.PremiumAccountListener;
import com.example.spauldhaliwal.spotifytoptracksplayer.model.impl.TrackModel;

public interface Player {

    void playTrack(TrackModel trackModel);

    void playPlaylist(String playlistId);

    void pauseResumeTrack();

    void skipTrack();

    void skipPrevTrack();

    void broadcastState();

    void stateUpdated(TrackModel trackState);

    void trackLoaded();

    void addListener(PlayerStateListener listener);

    void removeListener(PlayerStateListener listener);

    void canPlayPremiumContent(boolean canPlayPremiumContent);

    void addPremiumAccountListener(PremiumAccountListener listener);
}
