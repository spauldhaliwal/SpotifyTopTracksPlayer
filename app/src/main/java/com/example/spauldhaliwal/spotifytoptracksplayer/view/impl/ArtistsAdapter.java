package com.example.spauldhaliwal.spotifytoptracksplayer.view.impl;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.spauldhaliwal.spotifytoptracksplayer.R;
import com.example.spauldhaliwal.spotifytoptracksplayer.model.impl.ArtistModel;

import java.util.ArrayList;
import java.util.List;

public class ArtistsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "ArtistsAdapter";

    private ArrayList<ArtistModel> artistList;
    private final ArtistAdapterHolder adapterHolder;

    ArtistsAdapter(ArrayList<ArtistModel> artistList, ArtistAdapterHolder adapterHolder) {
        this.artistList = artistList;
        this.adapterHolder = adapterHolder;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        final View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.artist_item, viewGroup, false);
        return new ArtistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i) {
        final ArtistModel artistModel = getItem(i);

        ArtistViewHolder artistViewHolder = (ArtistViewHolder) holder;
        artistViewHolder.setName(artistModel.getName());
        artistViewHolder.setArtistProfileArt(artistModel.getArtistImageUrl());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapterHolder.onArtistSelected(artistModel, artistList);
            }
        });

    }

    public void updateResults(ArrayList<ArtistModel> artistList) {
        this.artistList = artistList;
    }

    @Override
    public int getItemCount() {
        return artistList.size();
    }

    static class ArtistViewHolder extends RecyclerView.ViewHolder {

        private final TextView name;
        private final ImageView artistProfileArt;

        ArtistViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.artistName);
            artistProfileArt = itemView.findViewById(R.id.artistProfileArt);
        }

        public void setName(String name) {
            this.name.setText(name);
        }

        public void setArtistProfileArt(String url) {
            Glide.with(artistProfileArt)
                    .load(url)
                    .apply(RequestOptions.circleCropTransform())
                    .into(artistProfileArt);
        }

        public TextView getName() {
            return name;
        }
    }

    private ArtistModel getItem(int position) {
        return artistList.get(position);
    }

    public interface ArtistAdapterHolder {
        void onArtistSelected(ArtistModel trackModel, List artistList);

    }

}