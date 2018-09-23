package com.example.spauldhaliwal.spotifytoptracksplayer;

public class TrackModel {
    private String id;
    private String title;
    private String albumTitle;
    private String albumCoverArtUrl;
    private long durationinMs;

    TrackModel(String id, String title, String albumTitle, String albumCoverArtUrl, long durationinMs) {
        this.id = id;
        this.title = title;
        this.albumTitle = albumTitle;
        this.albumCoverArtUrl = albumCoverArtUrl;
        this.durationinMs = durationinMs;
    }

    @Override
    public String toString() {
        return "TrackModel{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", albumTitle='" + albumTitle + '\'' +
                ", albumCoverArtUrl='" + albumCoverArtUrl + '\'' +
                ", durationinMs=" + durationinMs +
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

}
