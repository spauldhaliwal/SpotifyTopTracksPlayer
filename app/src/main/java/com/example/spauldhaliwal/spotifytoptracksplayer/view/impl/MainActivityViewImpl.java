package com.example.spauldhaliwal.spotifytoptracksplayer.view.impl;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.drawable.Animatable2;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.spauldhaliwal.spotifytoptracksplayer.Constants;
import com.example.spauldhaliwal.spotifytoptracksplayer.R;
import com.example.spauldhaliwal.spotifytoptracksplayer.model.Player;
import com.example.spauldhaliwal.spotifytoptracksplayer.model.impl.ArtistModel;
import com.example.spauldhaliwal.spotifytoptracksplayer.model.impl.SpotifyArtistRepositoryImpl;
import com.example.spauldhaliwal.spotifytoptracksplayer.model.impl.SpotifyRemotePlayer;
import com.example.spauldhaliwal.spotifytoptracksplayer.model.impl.SpotifyTrackRepositoryImpl;
import com.example.spauldhaliwal.spotifytoptracksplayer.model.impl.TrackModel;
import com.example.spauldhaliwal.spotifytoptracksplayer.presenter.MainActivityPresenter;
import com.example.spauldhaliwal.spotifytoptracksplayer.presenter.impl.MainActivityPresenterImpl;
import com.example.spauldhaliwal.spotifytoptracksplayer.view.MainActivityView;

import java.util.List;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

public class MainActivityViewImpl extends AppCompatActivity implements MainActivityView,
        TrackListFragment.OnTrackSelectedListener,
        ArtistSearchFragment.onSearchFragmentQueryListener,
        NowPlayingPagerFragment.OnNowPlayingDraggedListener {

    private static final String TAG = "MainActivityViewImpl";
    Intent starterIntent;

    public static int currentPosition;
    private static final String KEY_CURRENT_POSITION =
            "com.example.spauldhaliwal.spotifytoptracksplayer.key.currentPosition";

    private BottomSheetBehavior bottomSheetBehavior;
    private MaterialProgressBar playProgressBar;

    Toolbar toolbar;
    private TextView nowPlayingTitle;
    private TextView nowPlayingAlbum;
    private FloatingActionButton pauseResumeButton;

    private MainActivityPresenter presenter;
    private int lastPosition = 0;
    private List loadedTrackList;
    private String currentlyPlayingTrackId = "";
    private ProgressBar playProgressBarLoading;

    private boolean isPaused = true;
    int pagerDragDirection = 0;

    private boolean stateIsBeingRefreshed = false;
    private boolean playPauseIconIsAnimating = false;
    private boolean nowPlayingisBeingDragged = false;
    private NowPlayingPagerFragment nowPlayingPagerFragment;
    private ArtistSearchFragment searchFragment;
    private FragmentTransaction ft;
    private ObjectAnimator playProgressAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        starterIntent = getIntent();
        String authToken = starterIntent.getStringExtra("authToken");

        if (savedInstanceState != null) {
            currentPosition = savedInstanceState.getInt(KEY_CURRENT_POSITION, 0);
        }

        ft = getSupportFragmentManager().beginTransaction();
//        ft.replace(R.id.trackListFragmentFrame, new TrackListFragment());
        ArtistSearchFragment searchFragment = new ArtistSearchFragment();
        ft.replace(R.id.trackListFragmentFrame, searchFragment);
//        ft.replace(R.id.nowPlayingQueueFrame, new QueueFragment());
        NowPlayingPagerFragment nowPlayingPagerFragment = new NowPlayingPagerFragment();
        nowPlayingPagerFragment.setOnNowPlayingDraggedListener(this);

        ft.replace(R.id.nowPlayingQueueFrame, nowPlayingPagerFragment);
        ft.commit();

        nowPlayingTitle = findViewById(R.id.nowPlayingTitle);
        nowPlayingAlbum = findViewById(R.id.nowPlayingAlbum);

        FrameLayout nowPlayingBar = findViewById(R.id.nowPlayingTitleFrame);
        pauseResumeButton = findViewById(R.id.playPauseFab);
        playProgressBar = findViewById(R.id.playProgressBar);
        playProgressBarLoading = findViewById(R.id.plaProgressBarLoading);

        View bg = findViewById(R.id.bg);
        View nowPlayingBottomSheet = findViewById(R.id.nowPlayingBottomSheet);
        bottomSheetBehavior = BottomSheetBehavior.from(nowPlayingBottomSheet);

        Player player = new SpotifyRemotePlayer(this);

        presenter = new MainActivityPresenterImpl(this,
                new SpotifyTrackRepositoryImpl(Constants.ARTIST_ID,
                        authToken,
                        getApplicationContext()),
                new SpotifyArtistRepositoryImpl(authToken,
                        getApplicationContext()),
                player);

//        presenter.loadTracks();
        pauseResumeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPaused) {
                    pauseResumeButton.setImageResource(R.drawable.play_to_pause_anim);
                    isPaused = false;
                } else {
                    pauseResumeButton.setImageResource(R.drawable.pause_to_play_anim);
                    isPaused = true;
                }
                Animatable2 animatable = (Animatable2) pauseResumeButton.getDrawable();
                animatable.registerAnimationCallback(new Animatable2.AnimationCallback() {
                    @Override
                    public void onAnimationStart(Drawable drawable) {
                        playPauseIconIsAnimating = true;
                        super.onAnimationStart(drawable);
                    }

                    @Override
                    public void onAnimationEnd(Drawable drawable) {
                        playPauseIconIsAnimating = false;
                        super.onAnimationEnd(drawable);
                    }
                });
                animatable.start();

                presenter.onPauseResumeButtonClicked();
            }
        });

        nowPlayingBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onNowPlayingBarClicked();
            }
        });

        nowPlayingBottomSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onNowPlayingBottomSheetClicked();
            }
        });

        bg.setOnClickListener(v -> presenter.onBgClicked());

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED)
                    findViewById(R.id.bg).setVisibility(View.GONE);
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                findViewById(R.id.bg).setVisibility(View.VISIBLE);
                findViewById(R.id.bg).setAlpha(slideOffset);
            }
        });

    }

    @Override
    public void displayTracks(List<TrackModel> tracksList) {

        TrackListFragment trackListFragment = (TrackListFragment) getSupportFragmentManager()
                .findFragmentById(R.id.trackListFragmentFrame);
//        if (fragment instanceof TrackListFragment) {
        Log.d(TAG, "displayTracks: fragment instanceof");
        trackListFragment.displayTracks(tracksList);
        this.loadedTrackList = tracksList;
//        }
//
////        QueueFragment nowPlayingQueue = (QueueFragment)
////                getSupportFragmentManager().findFragmentById(R.id.nowPlayingQueueFrame);
////        nowPlayingQueue.displayTracks(tracksList);

        nowPlayingPagerFragment = (NowPlayingPagerFragment)
                getSupportFragmentManager().findFragmentById(R.id.nowPlayingQueueFrame);
        nowPlayingPagerFragment.loadQueue(tracksList);
//
//        Log.d(TAG, "displayTracks: " + tracksList.toString());
    }

    @Override
    public void displayArtists(List<ArtistModel> artistList) {
        Log.d(TAG, "displayArtists: artistList: " + artistList);

        searchFragment = (ArtistSearchFragment)
                getSupportFragmentManager().findFragmentById(R.id.trackListFragmentFrame);

        searchFragment.displayArtists(artistList);
    }

    @Override
    public void onArtistSelected(ArtistModel artistModel) {
        ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.trackListFragmentFrame, new TrackListFragment());
        ft.commit();
        presenter.loadTracks(artistModel);
    }

    @Override
    public void queryArtist(String artistQuery) {
        Toast.makeText(this, "Searching " + artistQuery, Toast.LENGTH_LONG).show();
        presenter.onSearchArtist(artistQuery);
    }

    @Override
    public void onTrackSelected(TrackModel trackModel, List trackList) {
        Log.d(TAG, "updateNowPlayingBar: currentTrackId: " + trackModel.getId());
        presenter.onTrackSelected(trackModel, trackList);
        nowPlayingPagerFragment.onTrackSelected(trackModel, trackList);
        currentlyPlayingTrackId = trackModel.getId();
    }

    @Override
    public void onTrackLoading() {
        playProgressBar.setVisibility(View.GONE);
        playProgressAnimator = ObjectAnimator.ofInt(playProgressBar,
                "progress",
                0);
        playProgressAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        playProgressAnimator.setDuration(0);
        playProgressAnimator.start();
        playProgressBarLoading.setVisibility(View.VISIBLE);
    }

    @Override
    public void onTrackLoaded(Boolean isFinished) {
        if (isFinished) {
            playProgressBar.setProgress(0);
            playProgressBarLoading.setVisibility(View.GONE);
            playProgressBar.setVisibility(View.VISIBLE);
            stateIsBeingRefreshed = false;
            nowPlayingisBeingDragged = false;
        }
    }

    @Override
    public void updateProgress(int position, int trackDuration, String id) {
        if (!stateIsBeingRefreshed) {
            playProgressAnimator = ObjectAnimator.ofInt(playProgressBar,
                    "progress",
                    position);
            int positionDifferential = position - lastPosition;
            if (lastPosition <= position + 2000
                    && position - lastPosition > 3000
                    && !id.equals(Constants.SILENT_TRACK_ID)) {
                Log.d(TAG, "updateProgress: position differential: " + positionDifferential);
                Log.d(TAG, "updateProgress: progress jumps forward");
                playProgressBar.setMax(trackDuration);
                playProgressAnimator.setDuration(800);
                playProgressAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                playProgressAnimator.start();
            } else if (lastPosition - position >= 3000
                    && position >= 2000
                    && !id.equals(Constants.SILENT_TRACK_ID)) {
                Log.d(TAG, "updateProgress: position differential: " + positionDifferential);
                Log.d(TAG, "updateProgress: progress jumps back");
                playProgressBar.setMax(trackDuration);
                playProgressAnimator.setDuration(800);
                playProgressAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                playProgressAnimator.start();
            } else if (lastPosition <= position + 2000
                    && !id.equals(Constants.SILENT_TRACK_ID)) {
                Log.d(TAG, "updateProgress: position differential: " + positionDifferential);
                Log.d(TAG, "updateProgress: progress steps");
                playProgressBar.setMax(trackDuration);
                playProgressAnimator.setDuration(2000);
                playProgressAnimator.setInterpolator(new LinearInterpolator());
                playProgressAnimator.start();
            } else {
                Log.d(TAG, "updateProgress: position differential: " + positionDifferential);
                Log.d(TAG, "updateProgress: track skips");
                playProgressAnimator = ObjectAnimator.ofInt(playProgressBar,
                        "progress",
                        0);
                playProgressAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                playProgressAnimator.setDuration(800);
                playProgressAnimator.start();
            }
            lastPosition = position;
        }
    }

    @Override
    public void updateNowPlayingBar(TrackModel trackModel) {
        String trackId = trackModel.getId();
        if (!trackId.equals(Constants.SILENT_TRACK_ID)) {
            nowPlayingTitle.setText(trackModel.getTitle());
            nowPlayingAlbum.setText(trackModel.getAlbumTitle());
            Log.d(TAG, "updateNowPlayingBar: statebeingrefreshed: " + stateIsBeingRefreshed);
            if (!trackId.equals(currentlyPlayingTrackId)
                    && !stateIsBeingRefreshed) {
                nowPlayingPagerFragment.onTrackChannged(trackModel);
                currentlyPlayingTrackId = trackId;
                stateIsBeingRefreshed = false;
                Log.d(TAG, "updateNowPlayingBar: currentTrackIndex: update " + trackModel.getIndex());
            }
        }
    }

    @Override
    public void updateResumePauseState(boolean isPaused) {
        Log.d(TAG, "updateResumePauseState: isPause: " + isPaused);
        if (!nowPlayingisBeingDragged
                && !stateIsBeingRefreshed
                && !playPauseIconIsAnimating) {
            if (this.isPaused != isPaused
                    && !isPaused) {
                pauseResumeButton.setImageResource(R.drawable.play_to_pause_anim);
                Animatable2 animatable = (Animatable2) pauseResumeButton.getDrawable();
                animatable.registerAnimationCallback(new Animatable2.AnimationCallback() {
                    @Override
                    public void onAnimationStart(Drawable drawable) {
                        playPauseIconIsAnimating = true;
                        super.onAnimationStart(drawable);
                    }

                    @Override
                    public void onAnimationEnd(Drawable drawable) {
                        playPauseIconIsAnimating = false;
                        super.onAnimationEnd(drawable);
                    }
                });
                animatable.start();
                this.isPaused = isPaused;

            } else if (!isPaused) {
                pauseResumeButton.setImageResource(R.drawable.pause);

            } else if (this.isPaused != isPaused
                    && isPaused) {
                pauseResumeButton.setImageResource(R.drawable.pause_to_play_anim);
                Animatable2 animatable = (Animatable2) pauseResumeButton.getDrawable();
                animatable.registerAnimationCallback(new Animatable2.AnimationCallback() {
                    @Override
                    public void onAnimationStart(Drawable drawable) {
                        playPauseIconIsAnimating = true;
                        super.onAnimationStart(drawable);
                    }

                    @Override
                    public void onAnimationEnd(Drawable drawable) {
                        playPauseIconIsAnimating = false;
                        super.onAnimationEnd(drawable);
                    }
                });
                animatable.start();
                this.isPaused = isPaused;

            } else if (isPaused) {
                pauseResumeButton.setImageResource(R.drawable.play);
            }
        }
    }

    @Override
    public void expandNowPlayingBar() {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    @Override
    public void toggleBottomSheet() {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    @Override
    public void dismissBottomSheet() {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    @Override
    public void onBackPressed() {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            super.onBackPressed();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onHasPremiumAccount(boolean hasPremiumAccount) {
        if (!hasPremiumAccount) {
            Toast.makeText(this, "Premium Spotify account required.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        if (fragment instanceof TrackListFragment) {
            TrackListFragment trackListFragment = (TrackListFragment) fragment;
            trackListFragment.setOnTrackSelectedListener(this);
        }
    }

    @Override
    public void pagedDragged(int state, int direction) {
        pagerDragDirection = direction;
        Animatable2 animatable;
        Log.d(TAG, "pagedDragged: isPaused: " + isPaused);
        Log.d(TAG, "pagedDragged: statebeingreefreshed: " + playPauseIconIsAnimating);
        if (!isPaused
                && !stateIsBeingRefreshed
                && !playPauseIconIsAnimating) {
            nowPlayingisBeingDragged = true;
            switch (state) {
                case ViewPager.SCROLL_STATE_DRAGGING:
                    if (direction == -1) {
                        pauseResumeButton.setImageResource(R.drawable.pause_to_rw);
                        animatable = (Animatable2) pauseResumeButton.getDrawable();
                        animatable.registerAnimationCallback(new Animatable2.AnimationCallback() {
                            @Override
                            public void onAnimationStart(Drawable drawable) {
                                playPauseIconIsAnimating = true;
                                super.onAnimationStart(drawable);
                            }

                            @Override
                            public void onAnimationEnd(Drawable drawable) {
                                playPauseIconIsAnimating = false;
                                super.onAnimationEnd(drawable);
                            }
                        });
                        animatable.start();
                    } else if (direction == 1) {
                        pauseResumeButton.setImageResource(R.drawable.pause_to_ff);
                        animatable = (Animatable2) pauseResumeButton.getDrawable();
                        animatable.registerAnimationCallback(new Animatable2.AnimationCallback() {
                            @Override
                            public void onAnimationStart(Drawable drawable) {
                                playPauseIconIsAnimating = true;
                                super.onAnimationStart(drawable);
                            }

                            @Override
                            public void onAnimationEnd(Drawable drawable) {
                                playPauseIconIsAnimating = false;
                                super.onAnimationEnd(drawable);
                            }
                        });
                        animatable.start();
                    }
                    break;
                case ViewPager.SCROLL_STATE_IDLE:
                    if (direction == -1) {
                        Log.d(TAG, "pagedDragged: direction: " + direction);
                        pauseResumeButton.setImageResource(R.drawable.rw_to_pause);
                        animatable = (Animatable2) pauseResumeButton.getDrawable();
                        animatable.registerAnimationCallback(new Animatable2.AnimationCallback() {
                            @Override
                            public void onAnimationStart(Drawable drawable) {
                                playPauseIconIsAnimating = true;
                                super.onAnimationStart(drawable);
                            }

                            @Override
                            public void onAnimationEnd(Drawable drawable) {
                                playPauseIconIsAnimating = false;
                                super.onAnimationEnd(drawable);
                            }
                        });
                        animatable.start();

                        pulseView(pauseResumeButton);
                        pulseView(playProgressBar);
//                        isPaused = false;
                        nowPlayingisBeingDragged = false;
                    } else if (direction == 1) {
                        Log.d(TAG, "pagedDragged: direction: " + direction);
                        pauseResumeButton.setImageResource(R.drawable.ff_to_pause_anim);
                        animatable = (Animatable2) pauseResumeButton.getDrawable();
                        animatable.registerAnimationCallback(new Animatable2.AnimationCallback() {
                            @Override
                            public void onAnimationStart(Drawable drawable) {
                                playPauseIconIsAnimating = true;
                                super.onAnimationStart(drawable);
                            }

                            @Override
                            public void onAnimationEnd(Drawable drawable) {
                                playPauseIconIsAnimating = false;
                                super.onAnimationEnd(drawable);
                            }
                        });
                        animatable.start();

                        pulseView(pauseResumeButton);
                        pulseView(playProgressBar);
//                        isPaused = false;
                        nowPlayingisBeingDragged = false;

                    }
                    break;
            }
        } else if (isPaused
                && !stateIsBeingRefreshed
                && !playPauseIconIsAnimating) {
            nowPlayingisBeingDragged = true;
            switch (state) {
                case ViewPager.SCROLL_STATE_DRAGGING:
                    if (direction == -1) {
                        pauseResumeButton.setImageResource(R.drawable.play_to_rw);
                        animatable = (Animatable2) pauseResumeButton.getDrawable();
                        animatable.registerAnimationCallback(new Animatable2.AnimationCallback() {
                            @Override
                            public void onAnimationStart(Drawable drawable) {
                                playPauseIconIsAnimating = true;
                                super.onAnimationStart(drawable);
                            }

                            @Override
                            public void onAnimationEnd(Drawable drawable) {
                                playPauseIconIsAnimating = false;
                                super.onAnimationEnd(drawable);
                            }
                        });
                        animatable.start();
                    } else if (direction == 1) {
                        pauseResumeButton.setImageResource(R.drawable.play_to_ff_anim);
                        animatable = (Animatable2) pauseResumeButton.getDrawable();
                        animatable.registerAnimationCallback(new Animatable2.AnimationCallback() {
                            @Override
                            public void onAnimationStart(Drawable drawable) {
                                playPauseIconIsAnimating = true;
                                super.onAnimationStart(drawable);
                            }

                            @Override
                            public void onAnimationEnd(Drawable drawable) {
                                playPauseIconIsAnimating = false;
                                super.onAnimationEnd(drawable);
                            }
                        });
                        animatable.start();
                    }
                    break;
                case ViewPager.SCROLL_STATE_IDLE:
                    if (direction == -1) {
                        pauseResumeButton.setImageResource(R.drawable.rw_to_pause);
                        animatable = (Animatable2) pauseResumeButton.getDrawable();
                        animatable.registerAnimationCallback(new Animatable2.AnimationCallback() {
                            @Override
                            public void onAnimationStart(Drawable drawable) {
                                playPauseIconIsAnimating = true;
                                super.onAnimationStart(drawable);
                            }

                            @Override
                            public void onAnimationEnd(Drawable drawable) {
                                playPauseIconIsAnimating = false;
                                super.onAnimationEnd(drawable);
                            }
                        });
                        animatable.start();

                        pulseView(pauseResumeButton);
                        pulseView(playProgressBar);
                        isPaused = false;
                        nowPlayingisBeingDragged = false;
                    } else if (direction == 1) {
                        pauseResumeButton.setImageResource(R.drawable.ff_to_pause_anim);
                        animatable = (Animatable2) pauseResumeButton.getDrawable();
                        animatable.registerAnimationCallback(new Animatable2.AnimationCallback() {
                            @Override
                            public void onAnimationStart(Drawable drawable) {
                                playPauseIconIsAnimating = true;
                                super.onAnimationStart(drawable);
                            }

                            @Override
                            public void onAnimationEnd(Drawable drawable) {
                                playPauseIconIsAnimating = false;
                                super.onAnimationEnd(drawable);
                            }
                        });
                        animatable.start();

                        pulseView(pauseResumeButton);
                        pulseView(playProgressBar);
                        isPaused = false;
                        nowPlayingisBeingDragged = false;
                    }
                    break;
            }
        }
    }

    @Override
    public void pageDragDirectionSwitched(int direction) {
        Log.d(TAG, "pageDragDirectionSwitched: starts");
        Animatable2 animatable;
        if (direction == 1
//                && pagerDragDirection == direction * -1
                ) {
            pauseResumeButton.setImageResource(R.drawable.rw_to_ff);
            animatable = (Animatable2) pauseResumeButton.getDrawable();
            animatable.registerAnimationCallback(new Animatable2.AnimationCallback() {
                @Override
                public void onAnimationStart(Drawable drawable) {
                    playPauseIconIsAnimating = true;
                    super.onAnimationStart(drawable);
                }

                @Override
                public void onAnimationEnd(Drawable drawable) {
                    playPauseIconIsAnimating = false;
                    super.onAnimationEnd(drawable);
                }
            });
            animatable.start();

        } else if (direction == -1
//                && pagerDragDirection == direction * -1
                ) {
            pauseResumeButton.setImageResource(R.drawable.ff_to_rw_anim);
            animatable = (Animatable2) pauseResumeButton.getDrawable();
            animatable.registerAnimationCallback(new Animatable2.AnimationCallback() {
                @Override
                public void onAnimationStart(Drawable drawable) {
                    playPauseIconIsAnimating = true;
                    super.onAnimationStart(drawable);
                }

                @Override
                public void onAnimationEnd(Drawable drawable) {
                    playPauseIconIsAnimating = false;
                    super.onAnimationEnd(drawable);
                }
            });
            animatable.start();
        }
    }

    @Override
    public void pageChanged(int direction) {
        Log.d(TAG, "pageChanged: direction: " + direction);
        if (!stateIsBeingRefreshed) {
            if (direction == -1) {
                presenter.onSkipPrevTrackSelected();
            } else if (direction == 1) {
                presenter.onSkipTrackSelected();
            }
        }
    }

    @Override
    public void stateIsBeingRefreshed(boolean isBeingRefreshed) {
        stateIsBeingRefreshed = isBeingRefreshed;
    }

    @Override
    protected void onPause() {
        super.onPause();
        presenter.removePlayerStateChangesListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.listenForPlayerStateChanges(loadedTrackList);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_CURRENT_POSITION, currentPosition);
    }

    public void pulseView(View view) {
        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(view, "scaleX", 0.925f);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", 0.925f);
        scaleDownY.setDuration(150);
        scaleDownX.setDuration(150);

        ObjectAnimator scaleUpX = ObjectAnimator.ofFloat(view, "scaleX", 1.0f);
        ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(view, "scaleY", 1.0f);
        scaleUpY.setDuration(200);
        scaleUpX.setDuration(200);

        AnimatorSet scaleDown = new AnimatorSet();
        scaleDown.playTogether(scaleDownY, scaleDownX);
        AnimatorSet scaleUp = new AnimatorSet();
        scaleUp.playTogether(scaleUpX, scaleUpY);

        AnimatorSet pulse = new AnimatorSet();
        pulse.playSequentially(scaleDown, scaleUp);
        pulse.start();
    }
}