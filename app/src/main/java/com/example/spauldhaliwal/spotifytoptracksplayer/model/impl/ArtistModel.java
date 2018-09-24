package com.example.spauldhaliwal.spotifytoptracksplayer.model.impl;

public class ArtistModel {
    private String id;
    private String name;
    private String artistImageUrl;

    public ArtistModel(String id, String name, String artistImageUrl) {
        this.id = id;
        this.name = name;
        this.artistImageUrl = artistImageUrl;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getArtistImageUrl() {
        return artistImageUrl;
    }

    @Override
    public String toString() {
        return "ArtistModel{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", artistImageUrl='" + artistImageUrl + '\'' +
                '}';
    }
}
