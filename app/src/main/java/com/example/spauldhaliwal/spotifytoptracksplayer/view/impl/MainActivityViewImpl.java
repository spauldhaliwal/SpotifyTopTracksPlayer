package com.example.spauldhaliwal.spotifytoptracksplayer.view.impl;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import com.example.spauldhaliwal.spotifytoptracksplayer.Constants;
import com.example.spauldhaliwal.spotifytoptracksplayer.R;
import com.example.spauldhaliwal.spotifytoptracksplayer.model.Player;
import com.example.spauldhaliwal.spotifytoptracksplayer.model.impl.SpotifyRemotePlayer;
import com.example.spauldhaliwal.spotifytoptracksplayer.model.impl.Top10TracksRepository;
import com.example.spauldhaliwal.spotifytoptracksplayer.model.impl.TrackModel;
import com.example.spauldhaliwal.spotifytoptracksplayer.presenter.MainActivityPresenter;
import com.example.spauldhaliwal.spotifytoptracksplayer.presenter.impl.MainActivityPresenterImpl;
import com.example.spauldhaliwal.spotifytoptracksplayer.view.MainActivityView;

import java.util.ArrayList;
import java.util.List;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

public class MainActivityViewImpl extends AppCompatActivity implements MainActivityView, AdapterHolder {
    private static final String TAG = "MainActivityViewImpl";
    Intent starterIntent;

    private RecyclerView recyclerView;
    private BottomSheetBehavior bottomSheetBehavior;
    private MaterialProgressBar playProgressBar;

    Toolbar toolbar;
    private TextView nowPlayingTitle;
    private TextView nowPlayingAlbum;
    private FloatingActionButton pauseResumeButton;
    private ImageView nowPlayingAlbumLarge;

    private MainActivityPresenter presenter;
    private ObjectAnimator playProgressAnimator;
    private int lastPosition = 0;
    private ProgressBar playProgressBarLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        starterIntent = getIntent();
        String authToken = starterIntent.getStringExtra("authToken");

        recyclerView = findViewById(R.id.trackListRecyclerView);

        nowPlayingTitle = findViewById(R.id.nowPlayingTitle);
        nowPlayingAlbum = findViewById(R.id.nowPlayingAlbum);
        nowPlayingAlbumLarge = findViewById(R.id.nowPlayingAlbumArtLarge);

        FrameLayout nowPlayingBar = findViewById(R.id.nowPlayingTitleFrame);
        pauseResumeButton = findViewById(R.id.playPauseFab);
        playProgressBar = findViewById(R.id.playProgressBar);
        playProgressBarLoading = findViewById(R.id.plaProgressBarLoading);

        View skipTrackHotSpot = findViewById(R.id.skipTrackHotSpot);
        View skipPrevTrackHotSpot = findViewById(R.id.skipPrevTrackHotSpot);

        View bg = findViewById(R.id.bg);
        View nowPlayingBottomSheet = findViewById(R.id.nowPlayingBottomSheet);
        bottomSheetBehavior = BottomSheetBehavior.from(nowPlayingBottomSheet);

        Player player = new SpotifyRemotePlayer(this);

        presenter = new MainActivityPresenterImpl(this,
                new Top10TracksRepository(Constants.ARTIST_ID,
                        authToken,
                        getApplicationContext()),
                player);

        presenter.loadTracks();
        pauseResumeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.onPauseResumeButtonClicked();
            }
        });

        skipTrackHotSpot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onSkipTrackSelected();
            }
        });

        skipPrevTrackHotSpot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onSkipPrevTrackSelected();
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

        bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onBgClicked();
            }
        });

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
        TracksAdapter tracksAdapter = new TracksAdapter((ArrayList<TrackModel>) tracksList, this);
        recyclerView.setAdapter(tracksAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setHasFixedSize(true);
        tracksAdapter.notifyDataSetChanged();
    }

    @Override
    public void onTrackSelected(TrackModel trackModel) {
        presenter.onTrackSelected(trackModel);
    }

    @Override
    public void onLoadingTrack() {
        playProgressBar.setVisibility(View.GONE);
        playProgressBarLoading.setVisibility(View.VISIBLE);
    }

    @Override
    public void onTrackLoaded() {
        playProgressBarLoading.setVisibility(View.GONE);
        playProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void updateProgress(int position, int duration) {
        if (lastPosition <= position) {
            playProgressBar.setMax(duration);
//            playProgressBar.setProgress(position);
            playProgressAnimator = ObjectAnimator.ofInt(playProgressBar,
                    "progress",
                    position);
            playProgressAnimator.setDuration(2000);
            playProgressAnimator.setInterpolator(new LinearInterpolator());
            playProgressAnimator.start();
        } else {
            playProgressAnimator = null;
            playProgressBar.setProgress(0);
        }
        lastPosition = position;
    }

    @Override
    public void updateNowPlayingBar(String title, String albumTitle) {
        nowPlayingTitle.setText(title);
        nowPlayingAlbum.setText(albumTitle);
    }

    @Override
    public void updateNowPlayingAlbumArt(String albumCoverArtUrl) {
        Glide.with(nowPlayingAlbumLarge)
                .load(albumCoverArtUrl)
                .into(nowPlayingAlbumLarge);
    }

    @Override
    public void updateResumePauseState(boolean isPaused) {
        if (isPaused) {
            pauseResumeButton.setImageResource(android.R.drawable.ic_media_play);
        } else {
            pauseResumeButton.setImageResource(android.R.drawable.ic_media_pause);
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
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onHasPremiumAccount(boolean hasPremiumAccount) {
        if (!hasPremiumAccount) {
            Toast.makeText(this, "Premium account required.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
//        presenter.removePlayerStateChangesListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        presenter.listenForPlayerStateChanges();
    }
}