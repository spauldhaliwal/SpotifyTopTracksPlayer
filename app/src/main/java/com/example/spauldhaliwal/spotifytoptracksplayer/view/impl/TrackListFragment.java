package com.example.spauldhaliwal.spotifytoptracksplayer.view.impl;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.spauldhaliwal.spotifytoptracksplayer.R;
import com.example.spauldhaliwal.spotifytoptracksplayer.model.impl.TrackModel;
import com.example.spauldhaliwal.spotifytoptracksplayer.view.TrackListView;

import java.util.ArrayList;
import java.util.List;

public class TrackListFragment extends Fragment implements TrackAdapterHolder, TrackListView {

    private RecyclerView recyclerView;
    private OnTrackSelectedListener mCallback;
    List<TrackModel> tracksList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.track_list_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = getView().findViewById(R.id.trackListRecyclerView);
        if (tracksList != null) {
            TracksAdapter tracksAdapter = new TracksAdapter((ArrayList<TrackModel>) tracksList, this);
            recyclerView.setAdapter(tracksAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setHasFixedSize(true);
            tracksAdapter.notifyDataSetChanged();

        }
    }

    @Override
    public void displayTracks(List<TrackModel> tracksList) {
        TracksAdapter tracksAdapter = new TracksAdapter((ArrayList<TrackModel>) tracksList, this);
        recyclerView.setAdapter(tracksAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        tracksAdapter.notifyDataSetChanged();
    }

    @Override
    public void onTrackSelected(TrackModel trackModel, List trackList) {
        mCallback.onTrackSelected(trackModel, trackList);
    }

    public void setOnTrackSelectedListener(Activity activity) {
        mCallback = (OnTrackSelectedListener) activity;
    }

    public interface OnTrackSelectedListener {
        void onTrackSelected(TrackModel trackModel, List trackList);
    }


}