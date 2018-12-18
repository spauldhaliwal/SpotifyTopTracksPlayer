package com.spauldhaliwal.spotifytoptracksplayer.model.impl;

import com.google.gson.Gson;

import java.util.ArrayList;

public class ArtistsRecentlySearched {

    private ArrayList<ArtistModel> recentArtists;

    public ArtistsRecentlySearched() {
        recentArtists = new ArrayList<ArtistModel>();
    }

    void addArtist(ArtistModel artistModel) {
        if (recentArtists.size() > 9) {
            recentArtists.remove(9);
            recentArtists.add(0,artistModel);
        } else {
            recentArtists.add(0, artistModel);
        }
    }

    public void refreshList(ArrayList<ArtistModel> storedList) {
        recentArtists = storedList;
    }

    public ArtistModel getArtist(int i) throws IndexOutOfBoundsException {
        return recentArtists.get(i);
    }

    public ArrayList<ArtistModel> getRecentArtistsAsList() {
        return recentArtists;
    }

    String serialize() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    static ArtistsRecentlySearched deserialize(String data) {
        Gson gson = new Gson();
        return gson.fromJson(data, ArtistsRecentlySearched.class);
    }

    @Override
    public String toString() {
        return recentArtists.toString();
    }
}
