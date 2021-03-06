package com.spauldhaliwal.spotifytoptracksplayer.model.impl;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.spauldhaliwal.spotifytoptracksplayer.Constants;
import com.spauldhaliwal.spotifytoptracksplayer.model.SpotifyArtistRepository;
import com.spauldhaliwal.spotifytoptracksplayer.model.listener.ArtistRepositoryListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpotifyArtistRepositoryImpl implements SpotifyArtistRepository {
    private static final String TAG = "SpotifyArtistRepository";
    private List<ArtistModel> artistsList;
    private String authToken;
    private Context context;

    private List<ArtistRepositoryListener> listeners = new ArrayList<>();

    public SpotifyArtistRepositoryImpl(String authToken, Context context) {
        this.authToken = authToken;
        this.context = context;
    }


    @Override
    public void getResult(String searchParameter) {
        Log.d(TAG, "getResult: called");
        String url = "https://api.spotify.com/v1/search?q=" + searchParameter + "&type=artist&market=" + Constants.COUNTRY_CODE + "&limit=10";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        artistsList = new ArrayList<>();
                        try {
                            JSONArray artistQueryResult = response.getJSONObject("artists").getJSONArray("items");
                            for (int i = 0; i < artistQueryResult.length(); i++) {
                                JSONObject artist = artistQueryResult.getJSONObject(i);
                                JSONArray artistImageSet = artist.getJSONArray("images");
                                JSONObject artistImage = artistImageSet.getJSONObject(0);
                                JSONObject artistFollowers = artist.getJSONObject("followers");
                                JSONArray artistGenres = artist.getJSONArray("genres");

                                String id = artist.getString("id");
                                String name = artist.getString("name");
                                String artistImageUrl = artistImage.getString("url");
                                String genre = null;
                                try {
                                    genre = artistGenres.getString(0);
                                } catch (JSONException e) {
                                    genre = "No Genre";
                                    e.printStackTrace();
                                }
                                int followers = artistFollowers.getInt("total");

                                ArtistModel artistModel = new ArtistModel(id,
                                        name,
                                        artistImageUrl,
                                        genre,
                                        followers);

                                artistsList.add(artistModel);

                                for (int j = 0; j < artistsList.size(); j++) {
                                    Log.d(TAG, "onResponse artists: " + artistsList.get(j).toString());
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        resultLoaded(artistsList);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onResponse error: " + error.toString());
            }
        }) {
            // Add required headers here
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + authToken);
                return headers;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(request);
    }

    @Override
    public void addListener(ArtistRepositoryListener listener) {
        listeners.clear();
        listeners.add(listener);
    }

    @Override
    public void resultLoaded(List resultAsList) {
        Log.d(TAG, "resultLoaded: called");
        for (ArtistRepositoryListener artistRepositoryListener : listeners) {
            artistRepositoryListener.onArtistResultsLoaded(resultAsList);
        }
    }
}