package com.example.spauldhaliwal.spotifytoptracksplayer.view.impl.adapters;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.transition.TransitionSet;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.spauldhaliwal.spotifytoptracksplayer.R;
import com.example.spauldhaliwal.spotifytoptracksplayer.model.impl.TrackModel;
import com.example.spauldhaliwal.spotifytoptracksplayer.view.impl.MainActivityViewImpl;
import com.example.spauldhaliwal.spotifytoptracksplayer.view.impl.NowPlayingPagerFragment;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class QueueAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "TracksAdapter";

    private interface ViewHolderListener {

        void onLoadCompleted(ImageView view, int adapterPosition);

        void onItemClicked(View view, int adapterPosition);
    }

    private final RequestManager requestManager;
    private final ViewHolderListener viewHolderListener;
    private ArrayList<TrackModel> tracksList;
    private final TracksAdapter.TrackAdapterHolder adapterHolder;


    public QueueAdapter(ArrayList<TrackModel> tracksList, TracksAdapter.TrackAdapterHolder adapterHolder, Fragment fragment) {
        this.tracksList = tracksList;
        this.adapterHolder = adapterHolder;
        this.requestManager = Glide.with(fragment);
        this.viewHolderListener = new ViewHolderListenerImpl(fragment, tracksList);
    }

    /**
     * Default {@link ViewHolderListener} implementation.
     */
    private static class ViewHolderListenerImpl implements ViewHolderListener {

        private Fragment fragment;
        private AtomicBoolean enterTransitionStarted;
        ArrayList<TrackModel> tracksList;

        ViewHolderListenerImpl(Fragment fragment, ArrayList<TrackModel> tracksList) {
            this.fragment = fragment;
            this.enterTransitionStarted = new AtomicBoolean();
            this.tracksList = tracksList;
        }

        @Override
        public void onLoadCompleted(ImageView view, int position) {
            // Call startPostponedEnterTransition only when the 'selected' image loading is completed.
            fragment.startPostponedEnterTransition();
        }

        /**
         * Handles a view click by setting the current position to the given {@code position} and
         * starting a {@link  NowPlayingPagerFragment} which displays the image at the position.
         *
         * @param view     the clicked {@link ImageView} (the shared element view will be re-mapped at the
         *                 GridFragment's SharedElementCallback)
         * @param position the selected view position
         */
        @Override
        public void onItemClicked(View view, int position) {
            // Update the position.
            MainActivityViewImpl.currentPosition = position;

            // Exclude the clicked card from the exit transition (e.g. the card will disappear immediately
            // instead of fading out with the rest to prevent an overlapping animation of fade and move).
            ((TransitionSet) fragment.getExitTransition()).excludeTarget(view, true);

            ImageView transitioningView = view.findViewById(R.id.albumArt);

//            fragment.getFragmentManager()
//                    .beginTransaction()
//                    .setReorderingAllowed(true) // Optimize for shared element transition
//                    .addSharedElement(transitioningView, transitioningView.getTransitionName())
//                    .replace(R.id.nowPlayingQueueFrame, NowPlayingPagerFragment.newInstance(tracksList), NowPlayingPagerFragment.class
//                            .getSimpleName())
//                    .addToBackStack(null)
//                    .commit();

            NowPlayingPagerFragment newFragment = NowPlayingPagerFragment.newInstance(tracksList);

            FragmentManager fm = fragment.getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.nowPlayingQueueFrame, newFragment);

            ft.setReorderingAllowed(true) // Optimize for shared element transition
                    .addSharedElement(transitioningView, transitioningView.getTransitionName())
                    .hide(fragment)
                    .show(newFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    @NonNull
    @Override
    public TrackViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.track_item, viewGroup, false);
        return new TrackViewHolder(view, requestManager, viewHolderListener, tracksList);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i) {
        final TrackModel trackModel = getItem(i);

        TrackViewHolder trackViewHolder = (TrackViewHolder) holder;
        trackViewHolder.setTitle(trackModel.getTitle());
        trackViewHolder.setAlbum(trackModel.getAlbumTitle());
        ((TrackViewHolder) holder).onBind();
//        trackViewHolder.setArtistProfileArt(trackModel.getAlbumCoverArtUrl());

//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                adapterHolder.onTrackSelected(trackModel, tracksList);
//            }
//        });

    }


    @Override
    public int getItemCount() {
        return tracksList.size();
    }

    static class TrackViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener {

        private final TextView title;
        private final TextView album;
        private final ImageView albumArt;
        ArrayList<TrackModel> tracksList;
        private final RequestManager requestManager;
        private final ViewHolderListener viewHolderListener;

        TrackViewHolder(@NonNull View itemView, RequestManager requestManager,
                        ViewHolderListener viewHolderListener, ArrayList<TrackModel> tracksList) {
            super(itemView);
            this.requestManager = requestManager;
            this.viewHolderListener = viewHolderListener;
            this.tracksList = tracksList;
            title = itemView.findViewById(R.id.trackTitle);
            album = itemView.findViewById(R.id.trackAlbum);
            albumArt = itemView.findViewById(R.id.albumArt);
            itemView.setOnClickListener(this);
        }

        void onBind() {
            int adapterPosition = getAdapterPosition();
            setImage(adapterPosition);
            albumArt.setTransitionName(String.valueOf(tracksList.get(adapterPosition)));
        }

        void setImage(final int adapterPosition) {
            // Load the image with Glide to prevent OOM error when the image drawables are very large.
            requestManager
                    .load(tracksList.get(adapterPosition).getAlbumCoverArtUrl())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                    Target<Drawable> target, boolean isFirstResource) {
                            viewHolderListener.onLoadCompleted(albumArt, adapterPosition);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable>
                                target, DataSource dataSource, boolean isFirstResource) {
                            viewHolderListener.onLoadCompleted(albumArt, adapterPosition);
                            return false;
                        }
                    })
                    .into(albumArt);
        }

        @Override
        public void onClick(View v) {
            viewHolderListener.onItemClicked(v, getAdapterPosition());
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
