package com.example.spauldhaliwal.spotifytoptracksplayer.model.impl;

import java.io.Serializable;

public class TrackModel implements Serializable {
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

    public TrackModel(String id, String title, String albumTitle, String albumCoverArtUrl, long durationInMs, long positionInMs, boolean isPaused, int index) {
        this.id = id;
        this.title = title;
        this.albumCoverArtUrl = albumCoverArtUrl;
        this.albumTitle = albumTitle;
        this.durationInMs = durationInMs;
        this.positionInMs = positionInMs;
        this.isPaused = isPaused;
        this.index = index;
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

    public int getIndex() {
        return index;
    }

    public boolean isPaused() {
        return isPaused;
    }
}
