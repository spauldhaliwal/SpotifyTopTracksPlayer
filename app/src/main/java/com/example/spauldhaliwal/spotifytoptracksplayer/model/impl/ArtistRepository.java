//package com.example.spauldhaliwal.spotifytoptracksplayer.model.impl;
//
//import android.content.Context;
//import android.util.Log;
//
//import com.android.volley.Request;
//import com.android.volley.RequestQueue;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.JsonObjectRequest;
//import com.android.volley.toolbox.Volley;
//import com.example.spauldhaliwal.spotifytoptracksplayer.Constants;
//import com.example.spauldhaliwal.spotifytoptracksplayer.listener.RepositoryListener;
//import com.example.spauldhaliwal.spotifytoptracksplayer.model.SpotifyLookupRepository;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class ArtistRepository implements SpotifyLookupRepository {
//    private static final String TAG = "Top10TracksRepository";
//
//    private List<ArtistModel> artistsList;
//    private String artistQuery;
//    private String authToken;
//    private Context context;
//
//    private List<RepositoryListener> listeners = new ArrayList<>();
//
//    public ArtistRepository(String searchParameter, String authToken, Context context) {
//        this.artistQuery = searchParameter;
//        this.authToken = authToken;
//        this.context = context;
//    }
//
//    @Override
//    public void getResult() {
//        String url = "https://api.spotify.com/v1/search?q=" + artistQuery + "&type=artist&market=" + Constants.COUNTRY_CODE + "&limit=10";
//        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
//                url,
//                null,
//                new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                artistsList = new ArrayList<>();
//                try {
//                    JSONArray artistQueryResult = response.getJSONObject("artists").getJSONArray("items");
//                    for (int i = 0; i < artistQueryResult.length(); i++) {
//                        JSONObject artist = artistQueryResult.getJSONObject(i);
//                        JSONArray artistImageSet = artist.getJSONArray("images");
//                        JSONObject artistImage = artistImageSet.getJSONObject(0);
//
//                        String id = artist.getString("id");
//                        String name = artist.getString("name");
//                        String artistImageUrl = artistImage.getString("url");
//
//                        ArtistModel artistModel = new ArtistModel(id, name, artistImageUrl);
//                        artistsList.add(artistModel);
//
//                        for (int j=0; j<artistsList.size(); j++) {
//                            Log.d(TAG, "onResponse artists: " + artistsList.get(j).toString());
//                        }
//                    }
//                    resultLoaded(artistsList);
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.d(TAG, "onResponse success: " + error.toString());
//            }
//        }) {
//            // Add required headers here
//            @Override
//            public Map<String, String> getHeaders() {
//                Map<String, String> headers = new HashMap<>();
//                headers.put("Content-Type", "application/json");
//                headers.put("Authorization", "Bearer " + authToken);
//                return headers;
//            }
//        };
//        RequestQueue requestQueue = Volley.newRequestQueue(context);
//        requestQueue.add(request);
//    }
//
//    @Override
//    public void addListener(RepositoryListener listener) {
//        listeners.add(listener);
//    }
//
//    public void resultLoaded(List tracksList) {
//        for (RepositoryListener tracksRepositoryListener : listeners)
//            tracksRepositoryListener.onResultsLoaded(tracksList);
//    }
//
//}
