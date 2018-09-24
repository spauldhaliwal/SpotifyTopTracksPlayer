package com.example.spauldhaliwal.spotifytoptracksplayer.Repositories.impl;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.spauldhaliwal.spotifytoptracksplayer.Repositories.TracksRepository;
import com.example.spauldhaliwal.spotifytoptracksplayer.RepositoryListener;
import com.example.spauldhaliwal.spotifytoptracksplayer.TrackModel;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Top10TracksRepository implements TracksRepository{
    private static final String TAG = "Top10TracksRepository";

    List<TrackModel> tracksList;
    String artistId;
    String authToken;
    Context context;
    RequestQueue requestQueue;

    private List<RepositoryListener> listeners = new ArrayList<RepositoryListener>();


    public Top10TracksRepository(String artistId, String authToken, Context context) {
        this.artistId = artistId;
        this.authToken = authToken;
        this.context = context;
    }

    @Override
    public List<TrackModel> getTracks() {
        Log.d(TAG, "getTracks: Repository getTracks starts");
        Log.d(TAG, "getTracks: Repository auth token: " + authToken);
        String url = "https://api.spotify.com/v1/artists/" + artistId + "/top-tracks?country=CA";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                tracksList = new ArrayList<>();
                try {
                    JSONArray jsonArray = response.getJSONArray("tracks");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject track = jsonArray.getJSONObject(i);
                        JSONObject album = track.getJSONObject("album");
                        JSONArray albumImageSet = album.getJSONArray("images");
                        JSONObject albumCoverArt = albumImageSet.getJSONObject(1);

                        String id = track.getString("id");
                        String title = track.getString("name");
                        String albumTitle = album.getString("name");
                        String albumCoverArtUrl = albumCoverArt.getString("url");
                        long durationInMs = track.getLong("duration_ms");
                        int index = i;

                        TrackModel trackModel = new TrackModel(id, title, albumTitle, albumCoverArtUrl, durationInMs, index);
                        tracksList.add(trackModel);
                    }

                    for (int i = 0; i < tracksList.size(); i++) {
                        Log.d(TAG, "Repository onResponse: Track " + i + ": " + tracksList.get(i).toString());
                    }

                    tracksLoaded(tracksList);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onResponse success: " + error.toString());
            }
        }) {

            // Add required headers here
            @Override
            public Map<String, String> getHeaders() {
                Log.d(TAG, "onResponse: authToken: " + authToken);
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + authToken);
                return headers;
            }

        };
        requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(request);
        if (tracksList == null) {
            Log.d(TAG, "getTracks: tracklist in repository is null");
        }
        return null;
    }

    @Override
    public void addListener(RepositoryListener listener) {
        listeners.add(listener);
    }

    public void tracksLoaded(List tracksList) {
        for (RepositoryListener tracksRepositoryListener : listeners)
            tracksRepositoryListener.onTracksLoaded(tracksList);
    }

}
