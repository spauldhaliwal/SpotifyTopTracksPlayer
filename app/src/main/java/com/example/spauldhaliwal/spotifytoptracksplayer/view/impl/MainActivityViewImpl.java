package com.example.spauldhaliwal.spotifytoptracksplayer.view.impl;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.drawable.Animatable2;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
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
import com.example.spauldhaliwal.spotifytoptracksplayer.model.impl.ArtistsRecentlySearched;
import com.example.spauldhaliwal.spotifytoptracksplayer.model.impl.RecentArtistsCache;
import com.example.spauldhaliwal.spotifytoptracksplayer.model.impl.SpotifyArtistRepositoryImpl;
import com.example.spauldhaliwal.spotifytoptracksplayer.model.impl.SpotifyRemotePlayer;
import com.example.spauldhaliwal.spotifytoptracksplayer.model.impl.SpotifyTrackRepositoryImpl;
import com.example.spauldhaliwal.spotifytoptracksplayer.model.impl.TrackModel;
import com.example.spauldhaliwal.spotifytoptracksplayer.presenter.MainActivityPresenter;
import com.example.spauldhaliwal.spotifytoptracksplayer.presenter.impl.MainActivityPresenterImpl;
import com.example.spauldhaliwal.spotifytoptracksplayer.view.MainActivityView;
import com.example.spauldhaliwal.spotifytoptracksplayer.view.impl.adapters.MainActivityPagerAdapter;
import com.example.spauldhaliwal.spotifytoptracksplayer.view.impl.uihelper.ShapeIndicatorView;

import java.util.List;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;
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
    AppBarLayout appBarLayout;
    private ViewPager viewPager;
    private TextView nowPlayingTitle;
    private TextView nowPlayingAlbum;
    private FloatingActionButton pauseResumeButton;

    private MainActivityPresenter presenter;
    private String currentlyLoadedArtistId = "";
    private String selectedArtistIdFromTrack = "";
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
    ArtistsRecentlySearched recentArtists = new ArtistsRecentlySearched();
    RecentArtistsCache recentArtistsCache;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        appBarLayout = findViewById(R.id.appBarLayout);
        setSupportActionBar(toolbar);

        starterIntent = getIntent();
        String authToken = starterIntent.getStringExtra("authToken");

        if (savedInstanceState != null) {
            currentPosition = savedInstanceState.getInt(KEY_CURRENT_POSITION, 0);
        }

        recentArtistsCache = new RecentArtistsCache(this);
        viewPager = findViewById(R.id.mainActivityPager);
        viewPager.setAdapter(new MainActivityPagerAdapter(getSupportFragmentManager()));
        OverScrollDecoratorHelper.setUpOverScroll(viewPager);

        TabLayout tabLayout = findViewById(R.id.tabs);
//        tabLayout.setupWithViewPager(viewPager, true);

        ShapeIndicatorView shapeIndicatorView = findViewById(R.id.custom_indicator);
        shapeIndicatorView.setupWithTabLayout(tabLayout);
        shapeIndicatorView.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_tracks_selected_24px);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_artist_24px);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Animatable2 tracksIconAnimation;
                Animatable2 artistsIconAnimation;

                switch (position) {
                    case 0:
                        tabLayout.getTabAt(0).setIcon(R.drawable.tracks_selected_anim);
                        tabLayout.getTabAt(1).setIcon(R.drawable.artist_unselected_anim);
                        tracksIconAnimation = (Animatable2) tabLayout.getTabAt(0).getIcon();
                        artistsIconAnimation = (Animatable2) tabLayout.getTabAt(1).getIcon();

                        tracksIconAnimation.start();
                        artistsIconAnimation.start();
                        break;
                    case 1:
                        tabLayout.getTabAt(0).setIcon(R.drawable.tracks_unselected_anim);
                        tabLayout.getTabAt(1).setIcon(R.drawable.artist_selected_anim);
                        tracksIconAnimation = (Animatable2) tabLayout.getTabAt(0).getIcon();
                        artistsIconAnimation = (Animatable2) tabLayout.getTabAt(1).getIcon();

                        tracksIconAnimation.start();
                        artistsIconAnimation.start();
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        ft = getSupportFragmentManager().beginTransaction();
//        ft.replace(R.id.trackListFragmentFrame, new TrackListFragment());
//        ArtistSearchFragment searchFragment = new ArtistSearchFragment();
//        ft.replace(R.id.trackListFragmentFrame, searchFragment);
//        ft.replace(R.id.nowPlayingQueueFrame, new QueueFragment());
        NowPlayingPagerFragment nowPlayingPagerFragment = new NowPlayingPagerFragment();
        nowPlayingPagerFragment.setOnNowPlayingDraggedListener(this);
        ft.replace(R.id.nowPlayingQueueFrame, nowPlayingPagerFragment);
        ft.replace(R.id.trackListFragmentFrame, new TrackListFragment());
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
        recentArtists = recentArtistsCache.retrieveRecents();
        try {
            presenter.loadTracks(recentArtists.getArtist(0));

        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void displayTracks(List<TrackModel> tracksList) {
        TrackListFragment trackListFragment = (TrackListFragment)
                getSupportFragmentManager().findFragmentByTag("android:switcher:"
                        + R.id.mainActivityPager
                        + ":" + 0);
        getSupportFragmentManager().executePendingTransactions();
        trackListFragment.displayTracks(tracksList);
        this.loadedTrackList = tracksList;

        viewPager.setCurrentItem(0);

//        QueueFragment nowPlayingQueue = (QueueFragment)
//                getSupportFragmentManager().findFragmentById(R.id.nowPlayingQueueFrame);
//        nowPlayingQueue.displayTracks(tracksList);

        // Reload recent searches every time an artist is selected
        recentArtists = recentArtistsCache.retrieveRecents();
        Log.d(TAG, "onCreate: " + recentArtists.getRecentArtistsAsList());
        displayArtists(recentArtists.getRecentArtistsAsList());
    }

    @Override
    public void displayArtists(List<ArtistModel> artistList) {
        Log.d(TAG, "displayArtists: artistList: " + artistList);
        searchFragment = (ArtistSearchFragment)
                getSupportFragmentManager().findFragmentByTag("android:switcher:"
                        + R.id.mainActivityPager
                        + ":" + 1);
        searchFragment.displayArtists(artistList);
    }

    @Override
    public void onArtistSelected(ArtistModel artistModel) {
        appBarLayout.setExpanded(true);
        presenter.loadTracks(artistModel);
        currentlyLoadedArtistId = artistModel.getId();
        recentArtistsCache.storeArtist(artistModel, recentArtists);
    }

    @Override
    public void queryArtist(String artistQuery) {
        presenter.onSearchArtist(artistQuery);
    }

    @Override
    public void onTrackSelected(TrackModel trackModel, List trackList) {
        presenter.onTrackSelected(trackModel, trackList);
        findViewById(R.id.nothingIsPlayingTextView).setVisibility(View.GONE);
        if (nowPlayingPagerFragment == null) {
            Log.d(TAG, "onTrackSelected: trackQueue reloaded");
            nowPlayingPagerFragment = (NowPlayingPagerFragment)
                    getSupportFragmentManager().findFragmentById(R.id.nowPlayingQueueFrame);
            nowPlayingPagerFragment.loadQueue(trackList);
        } else if (!selectedArtistIdFromTrack.equals(currentlyLoadedArtistId)) {
            Log.d(TAG, "onTrackSelected: trackQueue reloaded");
            nowPlayingPagerFragment = (NowPlayingPagerFragment)
                    getSupportFragmentManager().findFragmentById(R.id.nowPlayingQueueFrame);
            nowPlayingPagerFragment.loadQueue(trackList);
            selectedArtistIdFromTrack = trackModel.getArtistId();
        }

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
            Drawable drawable = playProgressBar.getProgressDrawable();
            drawable.setColorFilter(0xFFFFFFFF, android.graphics.PorterDuff.Mode.MULTIPLY);


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