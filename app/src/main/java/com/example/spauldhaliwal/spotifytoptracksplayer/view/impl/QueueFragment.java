package com.example.spauldhaliwal.spotifytoptracksplayer.view.impl;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.transition.Transition;
import android.support.transition.TransitionInflater;
import android.support.v4.app.Fragment;
import android.support.v4.app.SharedElementCallback;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.spauldhaliwal.spotifytoptracksplayer.R;
import com.example.spauldhaliwal.spotifytoptracksplayer.model.impl.TrackModel;
import com.example.spauldhaliwal.spotifytoptracksplayer.view.TrackListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class QueueFragment extends Fragment implements TrackAdapterHolder, TrackListView {
    private static final String TAG = "QueueFragment";
    private RecyclerView recyclerView;
    private OnTrackSelectedListener mCallback;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: QueueFragment called");
        return inflater.inflate(R.layout.track_list_fragment, container, false);
    }

    private void scrollToPosition() {
        recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v,
                                       int left,
                                       int top,
                                       int right,
                                       int bottom,
                                       int oldLeft,
                                       int oldTop,
                                       int oldRight,
                                       int oldBottom) {
                recyclerView.removeOnLayoutChangeListener(this);
                final RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                View viewAtPosition = layoutManager.findViewByPosition(MainActivityViewImpl.currentPosition);
                // Scroll to position if the view for the current position is null (not currently part of
                // layout manager children), or it's not completely visible.
                if (viewAtPosition == null || layoutManager
                        .isViewPartiallyVisible(viewAtPosition, false, true)) {
                    recyclerView.post(() -> layoutManager.scrollToPosition(MainActivityViewImpl.currentPosition));
                }
            }
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        scrollToPosition();
        recyclerView = getView().findViewById(R.id.trackListRecyclerView);
    }

    @Override
    public void displayTracks(List<TrackModel> tracksList) {
        QueueAdapter queueAdapter = new QueueAdapter((ArrayList<TrackModel>) tracksList, this, this);
        recyclerView.setAdapter(queueAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        queueAdapter.notifyDataSetChanged();

        prepareTransitions();
        postponeEnterTransition();
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

    /**
     * Prepares the shared element transition to the pager fragment, as well as the other transitions
     * that affect the flow.
     */
    private void prepareTransitions() {
        Transition transition = TransitionInflater.from(getContext())
                .inflateTransition(R.transition.grid_exit_transition);
        transition.setDuration(250);
        setExitTransition(transition);

        // A similar mapping is set at the ImagePagerFragment with a setEnterSharedElementCallback.
        setExitSharedElementCallback(
                new SharedElementCallback() {
                    @Override
                    public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                        // Locate the ViewHolder for the clicked position.f
                        RecyclerView.ViewHolder selectedViewHolder = recyclerView
                                .findViewHolderForAdapterPosition(MainActivityViewImpl.currentPosition);
                        if (selectedViewHolder == null || selectedViewHolder.itemView == null) {
                            return;
                        }

                        // Map the first shared element name to the child ImageView.
                        sharedElements
                                .put(names.get(0), selectedViewHolder.itemView.findViewById(R.id.albumArt));
                    }
                });
    }

}