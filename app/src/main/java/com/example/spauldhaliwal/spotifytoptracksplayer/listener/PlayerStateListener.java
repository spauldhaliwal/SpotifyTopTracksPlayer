package com.example.spauldhaliwal.spotifytoptracksplayer.listener;

import com.spotify.protocol.types.PlayerState;

public interface PlayerStateListener {

    void onStateUpdated(PlayerState data);
}
