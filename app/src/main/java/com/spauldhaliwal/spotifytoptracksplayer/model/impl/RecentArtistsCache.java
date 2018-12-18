package com.spauldhaliwal.spotifytoptracksplayer.model.impl;

import android.content.Context;

import com.spauldhaliwal.spotifytoptracksplayer.Constants;
import com.spauldhaliwal.spotifytoptracksplayer.model.impl.util.CacheImpl;

import java.util.ArrayList;

public class RecentArtistsCache extends CacheImpl {
    private static final String TAG = "RecentArtistsCache";
    public RecentArtistsCache(Context context) {
        super(context);
    }

    public void storeArtist(ArtistModel artistModel,
                            ArtistsRecentlySearched artistsRecentlySearched) {

        ArrayList<ArtistModel> artistList = artistsRecentlySearched.getRecentArtistsAsList();
        for (int i=0; i<artistList.size(); i++) {
            ArtistModel storedArtist = artistList.get(i);
            if (artistModel.getId().equals(storedArtist.getId())) {
                artistList.remove(i);
            }
        }
        artistsRecentlySearched.addArtist(artistModel);
        String artists = artistsRecentlySearched.serialize();
        super.storeString(Constants.SHARED_PREFS_RECENT_ARTISTS, artists);
    }

    public ArtistsRecentlySearched retrieveRecents() {
        ArtistsRecentlySearched artistsRecentlySearched = new ArtistsRecentlySearched();
        if (sharedPreferences.contains(Constants.SHARED_PREFS_RECENT_ARTISTS)) {
            String recents = sharedPreferences.getString(Constants.SHARED_PREFS_RECENT_ARTISTS, "null");
            artistsRecentlySearched = ArtistsRecentlySearched.deserialize(recents);
        }
        return artistsRecentlySearched;
    }

}
