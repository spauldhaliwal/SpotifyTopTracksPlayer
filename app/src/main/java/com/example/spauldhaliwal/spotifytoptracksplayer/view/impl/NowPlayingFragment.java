package com.example.spauldhaliwal.spotifytoptracksplayer.view.impl;

import android.graphics.Outline;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.spauldhaliwal.spotifytoptracksplayer.R;
import com.example.spauldhaliwal.spotifytoptracksplayer.model.impl.TrackModel;


public class NowPlayingFragment extends Fragment {
    private static final String TAG = "NowPlayingFragment";


    public static NowPlayingFragment newInstance(TrackModel trackModel) {
        NowPlayingFragment fragment = new NowPlayingFragment();
        Bundle argument = new Bundle();
        argument.putSerializable(TrackModel.class.getSimpleName(), trackModel);
        fragment.setArguments(argument);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.now_playing_page, container, false);

        Bundle arguments = getArguments();
        TrackModel trackModel = (TrackModel) arguments.getSerializable(TrackModel.class.getSimpleName());
        Log.d(TAG, "NowPlayingPage: " + trackModel.getAlbumCoverArtUrl());
        ImageView albumArt = rootView.findViewById(R.id.nowPlayingAlbumArtPage);

//          Alternate method to round corners

        albumArt.setClipToOutline(true);
        albumArt.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, 0, view.getWidth(), (view.getHeight()+8), 8);
//                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), 8);
            }
        });

        Glide.with(albumArt)
                .load(trackModel.getAlbumCoverArtUrl())
                .apply(new RequestOptions()
                        .fitCenter())
                .into(albumArt);

        rootView.findViewById(R.id.nowPlayingAlbumArtPage).setTransitionName(String.valueOf(trackModel));


        Glide.with(this)
                .load(trackModel.getAlbumCoverArtUrl())
                .apply(new RequestOptions()
                        .fitCenter()
                        .transform(new RoundedCorners(8))
                )
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable>
                            target, boolean isFirstResource) {
                        // The postponeEnterTransition is called on the parent ImagePagerFragment, so the
                        // startPostponedEnterTransition() should also be called on it to get the transition
                        // going in case of a failure.
                        getParentFragment().startPostponedEnterTransition();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable>
                            target, DataSource dataSource, boolean isFirstResource) {
                        // The postponeEnterTransition is called on the parent ImagePagerFragment, so the
                        // startPostponedEnterTransition() should also be called on it to get the transition
                        // going when the image is ready.
                        getParentFragment().startPostponedEnterTransition();
                        return false;
                    }
                })
                .into((ImageView) rootView.findViewById(R.id.nowPlayingAlbumArtPage));

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

}