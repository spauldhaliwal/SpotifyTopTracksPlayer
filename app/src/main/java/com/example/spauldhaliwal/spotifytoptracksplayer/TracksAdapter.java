package com.example.spauldhaliwal.spotifytoptracksplayer;

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

import java.util.ArrayList;

public class TracksAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "TracksAdapter";

    ArrayList<TrackModel> tracksList;
    final Player player;

    public TracksAdapter(ArrayList<TrackModel> tracksList, Player player) {
        this.tracksList = tracksList;
        this.player = player;

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
                player.playMusic(trackModel);

            }
        });

    }

    @Override
    public int getItemCount() {
        return 10;
    }

    static class TrackViewHolder extends RecyclerView.ViewHolder {

        private View trackView;
        private final TextView title;
        private final TextView album;
        private final ImageView albumArt;

        public TrackViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.trackTitle);
            album = itemView.findViewById(R.id.trackAlbum);
            albumArt = itemView.findViewById(R.id.albumArt);


        }

        public void setTitle(String title) {
            this.title.setText(title);
        }

        public void setAlbum(String album) {
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

    public TrackModel getItem(int position) {
        return tracksList.get(position);
    }


}
