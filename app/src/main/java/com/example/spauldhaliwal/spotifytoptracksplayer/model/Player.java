package com.example.spauldhaliwal.spotifytoptracksplayer.model;

import com.example.spauldhaliwal.spotifytoptracksplayer.listener.PlayerStateListener;
import com.example.spauldhaliwal.spotifytoptracksplayer.listener.PremiumAccountListener;
import com.example.spauldhaliwal.spotifytoptracksplayer.model.impl.TrackModel;

import java.util.List;

public interface Player {

    void playTrack(TrackModel trackModel, List trackList);

    void playPlaylist(String playlistId, List trackList);

    void pauseResumeTrack();

    void skipTrack();

    void skipPrevTrack();

    void broadcastState(List trackList);

    void stateUpdated(TrackModel trackState);

    void trackLoaded();

    void addListener(PlayerStateListener listener);

    void removeListener(PlayerStateListener listener);

    void canPlayPremiumContent(boolean canPlayPremiumContent);

    void addPremiumAccountListener(PremiumAccountListener listener);

    void playerRemoteConnected(String playlistId);
}
