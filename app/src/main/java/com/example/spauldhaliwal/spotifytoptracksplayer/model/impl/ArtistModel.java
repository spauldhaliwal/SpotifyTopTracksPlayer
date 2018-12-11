package com.example.spauldhaliwal.spotifytoptracksplayer.model.impl;

import com.example.spauldhaliwal.spotifytoptracksplayer.view.impl.ArtistSearchFragment;
import com.google.gson.Gson;

public class ArtistModel {
    private String id;
    private String name;
    private String artistImageUrl;
    private String genre;
    private int followers;

    public ArtistModel(String id, String name, String artistImageUrl, String genre, int followers) {
        this.id = id;
        this.name = name;
        this.artistImageUrl = artistImageUrl;
        this.genre = genre;
        this.followers = followers;
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

    public String getGenre() {
        return genre;
    }

    public int getFollowers() {
        return followers;
    }

    public String serialize() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    static public ArtistModel deserialize(String data) {
        Gson gson = new Gson();
        return gson.fromJson(data, ArtistModel.class);
    }

    @Override
    public String toString() {
        return "ArtistModel{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", artistImageUrl='" + artistImageUrl + '\'' +
                ", genre='" + genre + '\'' +
                ", followers=" + followers +
                '}';
    }
}
