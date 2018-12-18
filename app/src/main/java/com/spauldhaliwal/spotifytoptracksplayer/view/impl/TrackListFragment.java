package com.spauldhaliwal.spotifytoptracksplayer.view.impl;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.spauldhaliwal.spotifytoptracksplayer.R;
import com.spauldhaliwal.spotifytoptracksplayer.model.impl.TrackModel;
import com.spauldhaliwal.spotifytoptracksplayer.view.TrackListView;
import com.spauldhaliwal.spotifytoptracksplayer.view.impl.adapters.TracksAdapter;

import java.util.ArrayList;
import java.util.List;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

public class TrackListFragment extends Fragment implements TracksAdapter.TrackAdapterHolder, TrackListView {

    private RecyclerView recyclerView;
    private OnTrackSelectedListener mCallback;
    List<TrackModel> tracksList;

    public TrackListFragment() {
    }

    public static TrackListFragment newInstance() {
        TrackListFragment fragment = new TrackListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

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
            OverScrollDecoratorHelper.setUpOverScroll(recyclerView, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
            tracksAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void displayTracks(List<TrackModel> tracksList) {
        TracksAdapter tracksAdapter = new TracksAdapter((ArrayList<TrackModel>) tracksList, this);
        recyclerView.setAdapter(tracksAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        OverScrollDecoratorHelper.setUpOverScroll(recyclerView, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
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