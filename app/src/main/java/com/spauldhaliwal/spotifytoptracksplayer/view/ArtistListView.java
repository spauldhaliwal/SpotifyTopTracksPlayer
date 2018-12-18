package com.spauldhaliwal.spotifytoptracksplayer.view;

import com.spauldhaliwal.spotifytoptracksplayer.model.impl.ArtistModel;

import java.util.List;

public interface ArtistListView {
    void displayArtists(List<ArtistModel> artistList);
}
