package com.example.spauldhaliwal.spotifytoptracksplayer.view.impl;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.spauldhaliwal.spotifytoptracksplayer.R;
import com.example.spauldhaliwal.spotifytoptracksplayer.model.impl.TrackModel;
import com.example.spauldhaliwal.spotifytoptracksplayer.presenter.Presenter;

import java.util.ArrayList;

public class TracksAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "TracksAdapter";

    private ArrayList<TrackModel> tracksList;
    private final Presenter presenter;

    TracksAdapter(ArrayList<TrackModel> tracksList, Presenter presenter) {
        this.tracksList = tracksList;
        this.presenter = presenter;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Log.d(TAG, "onCreateViewHolder: starts");
        final View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.track_item, viewGroup, false);
        return new TrackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i) {
        final TrackModel trackModel = getItem(i);

        TrackViewHolder trackViewHolder = (TrackViewHolder) holder;
        trackViewHolder.setTitle(trackModel.getTitle());
        trackViewHolder.setAlbum(trackModel.getAlbumTitle());
        trackViewHolder.setAlbumArt(trackModel.getAlbumCoverArtUrl());
        Log.d(TAG, "onBindViewHolder: " + trackModel.getAlbumCoverArtUrl());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onTrackSelected(trackModel);
                presenter.listenForPlayerStateChanges();

            }
        });

    }

    @Override
    public int getItemCount() {
        return 10;
    }

    static class TrackViewHolder extends RecyclerView.ViewHolder {

        private final TextView title;
        private final TextView album;
        private final ImageView albumArt;

        TrackViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.trackTitle);
            album = itemView.findViewById(R.id.trackAlbum);
            albumArt = itemView.findViewById(R.id.albumArt);


        }

        public void setTitle(String title) {
            this.title.setText(title);
        }

        void setAlbum(String album) {
            this.album.setText(album);
        }

        public void setAlbumArt(String url) {
            Glide.with(albumArt)
                    .load(url)
                    .apply(RequestOptions.circleCropTransform())
                    .into(albumArt);
        }

        public TextView getTitle() {
            return title;
        }
    }

    private TrackModel getItem(int position) {
        return tracksList.get(position);
    }


}
