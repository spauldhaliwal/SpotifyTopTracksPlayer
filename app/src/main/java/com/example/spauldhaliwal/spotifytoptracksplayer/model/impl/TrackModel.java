package com.example.spauldhaliwal.spotifytoptracksplayer.model.impl;

public class TrackModel {
    private String id;
    private String title;
    private String albumTitle;
    private String albumCoverArtUrl;
    private long durationInMs;
    private long positionInMs;
    private boolean isPaused;
    private int index;

    TrackModel(String id, String title, String albumTitle, String albumCoverArtUrl, long durationinMs, int index) {
        this.id = id;
        this.title = title;
        this.albumTitle = albumTitle;
        this.albumCoverArtUrl = albumCoverArtUrl;
        this.durationInMs = durationinMs;
        this.index = index;
    }

    public TrackModel(String title, String albumTitle, long durationInMs, long positionInMs, boolean isPaused) {
        this.title = title;
        this.albumTitle = albumTitle;
        this.durationInMs = durationInMs;
        this.positionInMs = positionInMs;
        this.isPaused = isPaused;
    }

    @Override
    public String toString() {
        return "TrackModel{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", albumTitle='" + albumTitle + '\'' +
                ", albumCoverArtUrl='" + albumCoverArtUrl + '\'' +
                ", durationInMs=" + durationInMs +
                ", index=" + index +
                '}';
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAlbumTitle() {
        return albumTitle;
    }

    public String getAlbumCoverArtUrl() {
        return albumCoverArtUrl;
    }

    public long getDurationInMs() {
        return durationInMs;
    }

    public long getPositionInMs() {
        return positionInMs;
    }

    public boolean isPaused() {
        return isPaused;
    }
}
