package com.example.spauldhaliwal.spotifytoptracksplayer.model.impl;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.spauldhaliwal.spotifytoptracksplayer.Constants;
import com.example.spauldhaliwal.spotifytoptracksplayer.listener.TrackRepositoryListener;
import com.example.spauldhaliwal.spotifytoptracksplayer.model.SpotifyTrackRepository;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class SpotifyTrackRepositoryImpl implements SpotifyTrackRepository {
    private static final String TAG = "SpotifyTrackRepositoryImpl";

    private SharedPreferences prefs = null;

    private List<TrackModel> tracksList;
    private String artistId;
    private String authToken;
    private Context context;

    private List<TrackRepositoryListener> listeners = new ArrayList<>();
    private String userId;

    public SpotifyTrackRepositoryImpl(String searchParameter, String authToken, Context context) {
        this.artistId = searchParameter;
        this.authToken = authToken;
        this.context = context;
    }

    @Override
    public void getResult(ArtistModel artistModel) {
        this.artistId = artistModel.getId();
        String url = "https://api.spotify.com/v1/artists/" + artistModel.getId() + "/top-tracks?country=" + Constants.COUNTRY_CODE;
        Log.d(TAG, "getResult: oAuthToken: " + authToken);

        prefs = context.getSharedPreferences("com.example.spauldhaliwal.spotifytoptracksplayer", MODE_PRIVATE);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String queueQuery;
                        StringBuilder queueBuilder = new StringBuilder();
                        tracksList = new ArrayList<>();
                        try {
                            JSONArray jsonArray = response.getJSONArray("tracks");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject track = jsonArray.getJSONObject(i);
                                JSONObject album = track.getJSONObject("album");
                                JSONArray albumImageSet = album.getJSONArray("images");
                                JSONObject albumCoverArt = albumImageSet.getJSONObject(0);

                                String id = track.getString("id");
                                String title = track.getString("name");
                                String albumTitle = album.getString("name");
                                String albumCoverArtUrl = albumCoverArt.getString("url");
                                long durationInMs = track.getLong("duration_ms");

                                TrackModel trackModel = new TrackModel(id,
                                        title,
                                        albumTitle,
                                        albumCoverArtUrl,
                                        durationInMs,
                                        i);
                                tracksList.add(trackModel);
                                queueBuilder.append("spotify:track:" + trackModel.getId() + ",");

                                for (int j = 0; j < tracksList.size(); j++) {
                                    Log.d(TAG, "onResponse tracks: " + tracksList.get(j).toString());
                                }
                            }
                            queueQuery = queueBuilder.toString();
                            Log.d(TAG, "onResponse: queueQuery: " + queueQuery.toString());
                            resultLoaded(tracksList);

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
    public void buildQueue(TrackModel trackModel) {
        String url = "https://api.spotify.com/v1/artists/" + artistId + "/top-tracks?country=" + Constants.COUNTRY_CODE;
        Log.d(TAG, "getResult: oAuthToken: " + authToken);

        prefs = context.getSharedPreferences("com.example.spauldhaliwal.spotifytoptracksplayer", MODE_PRIVATE);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String queueQuery;
                        StringBuilder queueBuilder = new StringBuilder();
                        tracksList = new ArrayList<>();
                        try {
                            JSONArray jsonArray = response.getJSONArray("tracks");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject track = jsonArray.getJSONObject(i);
                                JSONObject album = track.getJSONObject("album");
                                JSONArray albumImageSet = album.getJSONArray("images");
                                JSONObject albumCoverArt = albumImageSet.getJSONObject(0);

                                String id = track.getString("id");
                                String title = track.getString("name");
                                String albumTitle = album.getString("name");
                                String albumCoverArtUrl = albumCoverArt.getString("url");
                                long durationInMs = track.getLong("duration_ms");

                                TrackModel trackModel = new TrackModel(id,
                                        title,
                                        albumTitle,
                                        albumCoverArtUrl,
                                        durationInMs,
                                        i);
                                tracksList.add(trackModel);
                                queueBuilder.append("spotify:track:" + trackModel.getId() + ",");

                                for (int j = 0; j < tracksList.size(); j++) {
                                    Log.d(TAG, "onResponse tracks: " + tracksList.get(j).toString());
                                }
                            }
                            queueQuery = queueBuilder.toString();
                            Log.d(TAG, "onResponse: queueQuery: " + queueQuery.toString());

                            if (trackModel == null) {
                                resultLoaded(tracksList);
                            } else if (prefs.contains("nowPlayingQueuePlaylistId")) {
                                String playlistId = prefs.getString("nowPlayingQueuePlaylistId", "null");
                                updateQueuePlaylist(playlistId, tracksList, trackModel);
                            } else {
                                getUserId(tracksList, trackModel);
                            }

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
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + authToken);
                return headers;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(request);
    }

    private void getUserId(final List<TrackModel> queueQuery, final TrackModel selectedTrack) {
        Log.d(TAG, "getUserId: starts");
        String url = "https://api.spotify.com/v1/me";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        tracksList = new ArrayList<>();
                        try {
                            userId = response.getString("id");
                            Log.d(TAG, "getUserId: onResponse id: " + userId);
                            createQueuePlaylist(userId, queueQuery, selectedTrack);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "getUserId: onErrorResponse success: " + error.toString());
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

    private void createQueuePlaylist(final String userId, final List<TrackModel> queueQuery, final TrackModel selectedTrack) {
        Log.d(TAG, "createQueuePlaylist: starts");
        String url = "https://api.spotify.com/v1/users/" + userId + "/playlists";

        JSONObject js = new JSONObject();
        try {
            js.put("name", "Top 10 Tracks Player Queue");
            js.put("public", false);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                url,
                js,
                new Response.Listener<JSONObject>() {
                    private String playListId;

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            playListId = response.getString("id");
                            prefs.edit().putString("nowPlayingQueuePlaylistId", playListId).apply();
                        } catch (JSONException e) {
                            Log.d(TAG, "onResponse: ");
                            e.printStackTrace();
                        }
                        populateQueuePlaylist(playListId, queueQuery, selectedTrack);
                        Log.d(TAG, "createQueuePlaylist Playlist id: " + playListId);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "createQueuePlaylist: onErrorResponse: " + error.toString());
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

    private void populateQueuePlaylist(final String playListId, List<TrackModel> queueAsList, TrackModel selectedTrack) {
        Log.d(TAG, "populateQueuePlaylist: starts");
        String url = "https://api.spotify.com/v1/playlists/" + playListId + "/tracks";
        StringBuilder stringBuilder = new StringBuilder();
        JSONObject js = new JSONObject();
        try {
            JSONArray uris = new JSONArray();
            int trackPosition = selectedTrack.getIndex();

            for (int i = trackPosition; i < queueAsList.size(); i++) {
                String trackId = queueAsList.get(i).getId();
                stringBuilder.append("spotify:track:" + trackId + ",");
                uris.put("spotify:track:" + trackId);
            }
            for (int i = 0; i < trackPosition; i++) {
                String trackId = queueAsList.get(i).getId();
                stringBuilder.append("spotify:track:" + trackId + ",");
                uris.put("spotify:track:" + trackId);
            }
            js.put("uris", uris);
            Log.d(TAG, "populateQueuePlaylist: " + js.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT,
                url,
                js,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "populateQueuePlaylist: POST complete");

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "populateQueuePlaylist: onErrorResponse: " + error.toString());
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

    private void updateQueuePlaylist(final String playListId, final List<TrackModel> queueAsList, TrackModel selectedTrack) {
        Log.d(TAG, "updateQueuePlaylist: starts");
        String url = "https://api.spotify.com/v1/playlists/" + playListId + "/tracks";
        StringBuilder stringBuilder = new StringBuilder();
        JSONObject js = new JSONObject();
        try {
            JSONArray uris = new JSONArray();
            int trackPosition = selectedTrack.getIndex();

            for (int i = 0; i < queueAsList.size(); i++) {
                String trackId = queueAsList.get(i).getId();
                stringBuilder.append("spotify:track:" + trackId + ",");
                uris.put("spotify:track:" + trackId);
            }
//            for (int i = 0; i < trackPosition; i++) {
//                String trackId = queueAsList.get(i).getId();
//                stringBuilder.append("spotify:track:" + trackId + ",");
//                uris.put("spotify:track:" + trackId);
//            }
            js.put("uris", uris);
            Log.d(TAG, "updateQueuePlaylist: " + js.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT,
                url,
                js,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "updateQueuePlaylist: PUT complete");
                        try {
                            String newSnapshotId = response.getString("snapshot_id");
//                            playOverWebApi(playListId);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        queueBuildComplete(playListId, queueAsList, selectedTrack);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "updateQueuePlaylist: onErrorResponse: " + error.toString());
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
    public void addListener(TrackRepositoryListener listener) {
        listeners.add(listener);
    }

    @Override
    public void resultLoaded(List tracksList) {
        for (TrackRepositoryListener tracksRepositoryListener : listeners)
            tracksRepositoryListener.onResultsLoaded(tracksList);
    }

    @Override
    public void trackFinishedLoading(boolean isFinished) {
        for (TrackRepositoryListener tracksRepositoryListener : listeners)
            tracksRepositoryListener.trackLoadedFromRepository(isFinished);
    }

    @Override
    public void queueBuildComplete(String playlistId, List tracksList, TrackModel trackModel) {
        for (TrackRepositoryListener queueBuildCompleteListener : listeners)
            queueBuildCompleteListener.onQueueBuildComplete(playlistId, tracksList, trackModel);
    }

    // Alternative play method. Unreliable, not used.
    @Override
    public void playOverWebApi(String playlistId, TrackModel trackModel) {
        Log.d(TAG, "playOverWebApi: starts");
        String url = "https://api.spotify.com/v1/me/player/play";
        JSONObject js = new JSONObject();
        JSONObject offset = new JSONObject();
        Log.d(TAG, "playOverWebApi: playlistid: " + playlistId);
        try {
            offset.put("position", trackModel.getIndex());
            js.put("context_uri", "spotify:playlist:"+playlistId);
            js.put("offset", offset);
            Log.d(TAG, "playOverWebApi: " + js.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        SpotifyJsonObjectRequest request = new SpotifyJsonObjectRequest(Request.Method.PUT,
                url,
                js,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "playOverWebApi: success");
                        trackFinishedLoading(true);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "playOverWebApi: onErrorResponse: " + error.toString());
            }
        }) {
            // Add required headers here
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
//                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + authToken);
                return headers;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(request);
    }

    public void getPlayerState() {
        Log.d(TAG, "getPlayerState: starts");
        String url = "https://api.spotify.com/v1/me/player";

        SpotifyJsonObjectRequest request = new SpotifyJsonObjectRequest(Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "getPlayerState: success");
                        try {
                            JSONObject device = response.getJSONObject("device");
                            String device_id = device.getString("id");
                            Log.d(TAG, "getPlayerState: device id: " + device_id);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "getPlayerState: onErrorResponse: " + error.toString());
            }
        }) {
            // Add required headers here
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
//                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + authToken);
                return headers;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(request);
    }
}
