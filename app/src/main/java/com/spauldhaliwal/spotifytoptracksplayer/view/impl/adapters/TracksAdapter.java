package com.spauldhaliwal.spotifytoptracksplayer.view.impl.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.spauldhaliwal.spotifytoptracksplayer.R;
import com.spauldhaliwal.spotifytoptracksplayer.model.impl.TrackModel;

import java.util.ArrayList;
import java.util.List;

public class TracksAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "TracksAdapter";

    private ArrayList<TrackModel> tracksList;
    private final TrackAdapterHolder adapterHolder;

    public TracksAdapter(ArrayList<TrackModel> tracksList, TrackAdapterHolder adapterHolder) {
        this.tracksList = tracksList;
        this.adapterHolder = adapterHolder;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
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

        if (i == tracksList.size()-1) {
            ((TrackViewHolder) holder).hideDivider();
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapterHolder.onTrackSelected(trackModel, tracksList);
            }
        });

    }


    @Override
    public int getItemCount() {
        return tracksList.size();
    }

    static class TrackViewHolder extends RecyclerView.ViewHolder {

        private final TextView title;
        private final TextView album;
        private final ImageView albumArt;
        private final View divider;

        TrackViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.trackTitle);
            album = itemView.findViewById(R.id.trackAlbum);
            albumArt = itemView.findViewById(R.id.albumArt);
            divider = itemView.findViewById(R.id.divider);
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
                    .into(albumArt)
            ;
        }

        public TextView getTitle() {
            return title;
        }

        void hideDivider() {
            divider.setVisibility(View.INVISIBLE);
        }
    }

    private TrackModel getItem(int position) {
        return tracksList.get(position);
    }

    public interface TrackAdapterHolder {
        void onTrackSelected(TrackModel trackModel, List trackList);
    }

}