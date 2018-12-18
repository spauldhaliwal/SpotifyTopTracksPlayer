package com.spauldhaliwal.spotifytoptracksplayer.model;

import com.spauldhaliwal.spotifytoptracksplayer.model.impl.TrackModel;
import com.spauldhaliwal.spotifytoptracksplayer.model.listener.PlayerStateListener;
import com.spauldhaliwal.spotifytoptracksplayer.model.listener.PremiumAccountListener;

import java.util.List;

public interface Player {

    void playTrack(TrackModel trackModel, List trackList);

    void playPlaylist(String playlistId, List trackList, TrackModel trackModel);

    void pauseResumeTrack();

    void skipTrack();

    void skipPrevTrack();

    void broadcastState(List trackList);

    void stateUpdated(TrackModel trackState);

    void trackLoadFailed(TrackModel trackModel, List trackList);

    void addListener(PlayerStateListener listener);

    void removeListener(PlayerStateListener listener);

    void canPlayPremiumContent(boolean canPlayPremiumContent);

    void addPremiumAccountListener(PremiumAccountListener listener);

    void playerRemoteConnected(String playlistId, TrackModel trackModel);
}
