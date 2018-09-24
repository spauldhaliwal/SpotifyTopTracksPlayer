package com.example.spauldhaliwal.spotifytoptracksplayer.view.impl;

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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.spauldhaliwal.spotifytoptracksplayer.Constants;
import com.example.spauldhaliwal.spotifytoptracksplayer.R;
import com.example.spauldhaliwal.spotifytoptracksplayer.model.Player;
import com.example.spauldhaliwal.spotifytoptracksplayer.model.impl.SpotifyRemotePlayer;
import com.example.spauldhaliwal.spotifytoptracksplayer.model.impl.Top10TracksRepository;
import com.example.spauldhaliwal.spotifytoptracksplayer.model.impl.TrackModel;
import com.example.spauldhaliwal.spotifytoptracksplayer.presenter.MainActivityPresenter;
import com.example.spauldhaliwal.spotifytoptracksplayer.view.MainActivityView;

import java.util.ArrayList;
import java.util.List;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

public class MainActivity extends AppCompatActivity implements MainActivityView {
    private static final String TAG = "MainActivity";

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

        View bg = findViewById(R.id.bg);
        View nowPlayingBottomSheet = findViewById(R.id.nowPlayingBottomSheet);
        bottomSheetBehavior = BottomSheetBehavior.from(nowPlayingBottomSheet);

        Player player = new SpotifyRemotePlayer(this);

        presenter = new com.example.spauldhaliwal.spotifytoptracksplayer.presenter.impl.MainActivityPresenter(this,
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
                Log.d(TAG, "onSlide: slideOffset" + slideOffset + "");
                findViewById(R.id.bg).setVisibility(View.VISIBLE);
                findViewById(R.id.bg).setAlpha(slideOffset);
            }
        });

    }

    @Override
    public void displayTracks(List<TrackModel> tracksList) {
        TracksAdapter tracksAdapter = new TracksAdapter((ArrayList<TrackModel>) tracksList, presenter);
        recyclerView.setAdapter(tracksAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setHasFixedSize(true);
        tracksAdapter.notifyDataSetChanged();

    }

    @Override
    public void updateProgress(int position, int duration) {
        playProgressBar.setMax(duration);
        playProgressBar.setProgress(position);
    }

    @Override
    public void updateNowPlayingBar(String title, String album) {
        nowPlayingTitle.setText(title);
        nowPlayingAlbum.setText(album);
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
    protected void onPause() {
        super.onPause();
        presenter.removePlayerStateChangesListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.listenForPlayerStateChanges();
    }

    @Override
    public void onBackPressed() {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }
}