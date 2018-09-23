package com.example.spauldhaliwal.spotifytoptracksplayer.Repositories;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.spauldhaliwal.spotifytoptracksplayer.TrackModel;

import java.util.List;

public interface TracksRepository {

    JsonObjectRequest getTracks(String authToken);
}
