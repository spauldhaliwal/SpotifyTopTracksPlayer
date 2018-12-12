package com.example.spauldhaliwal.spotifytoptracksplayer.view.impl.adapters;

import android.graphics.Outline;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.spauldhaliwal.spotifytoptracksplayer.R;
import com.example.spauldhaliwal.spotifytoptracksplayer.model.impl.ArtistModel;
import com.example.spauldhaliwal.spotifytoptracksplayer.view.impl.uihelper.TitleCaseHelper;

import java.util.ArrayList;
import java.util.List;

public class ArtistsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "ArtistsAdapter";

    private ArrayList<ArtistModel> artistList;
    private final ArtistAdapterHolder adapterHolder;

    public ArtistsAdapter(ArrayList<ArtistModel> artistList, ArtistAdapterHolder adapterHolder) {
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
        artistViewHolder.setGenre(artistModel.getGenre());
        artistViewHolder.setFollowers(Integer.toString(artistModel.getFollowers()) + " Followers");
        artistViewHolder.setArtistProfileArt(artistModel.getArtistImageUrl());

        String genre = artistModel.getGenre();
        artistViewHolder.setGenre(TitleCaseHelper.convertToTitleCase(genre));

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
        private final TextView genre;
        private final TextView followers;
        private final ImageView artistProfileArt;

        ArtistViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.artistName);
            genre = itemView.findViewById(R.id.artistGenre);
            followers = itemView.findViewById(R.id.artistFollowers);
            artistProfileArt = itemView.findViewById(R.id.artistProfileArt);
        }

        public void setName(String name) {
            this.name.setText(name);
        }

        public void setGenre(String genre) {
            this.genre.setText(genre);
        }

        public void setFollowers(String followers) {
            this.followers.setText(followers);
        }

        public void setArtistProfileArt(String url) {
            artistProfileArt.setClipToOutline(true);
            artistProfileArt.setOutlineProvider(new ViewOutlineProvider() {
                @Override
                public void getOutline(View view, Outline outline) {
                    outline.setRoundRect(0, 0, view.getWidth(), (view.getHeight() + 8), 8);
                }
            });

            Glide.with(artistProfileArt)
                    .load(url)
                    .apply(new RequestOptions()
                            .fitCenter())
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