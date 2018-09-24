package com.example.spauldhaliwal.spotifytoptracksplayer.model.impl;

public class TrackModel {
    private String id;
    private String title;
    private String albumTitle;
    private String albumCoverArtUrl;
    private long durationinMs;
    private int index;

    TrackModel(String id, String title, String albumTitle, String albumCoverArtUrl, long durationinMs, int index) {
        this.id = id;
        this.title = title;
        this.albumTitle = albumTitle;
        this.albumCoverArtUrl = albumCoverArtUrl;
        this.durationinMs = durationinMs;
        this.index = index;
    }

    @Override
    public String toString() {
        return "TrackModel{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", albumTitle='" + albumTitle + '\'' +
                ", albumCoverArtUrl='" + albumCoverArtUrl + '\'' +
                ", durationinMs=" + durationinMs +
                ", index=" + index +
                '}';
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getIndex() {
        return index;
    }

    public String getAlbumTitle() {
        return albumTitle;
    }

    public String getAlbumCoverArtUrl() {
        return albumCoverArtUrl;
    }

}
