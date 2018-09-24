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
import com.example.spauldhaliwal.spotifytoptracksplayer.presenter.Presenter;
import com.example.spauldhaliwal.spotifytoptracksplayer.presenter.impl.MainActivityPresenter;
import com.example.spauldhaliwal.spotifytoptracksplayer.view.MainActivityView;

import java.util.ArrayList;
import java.util.List;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

public class MainActivity extends AppCompatActivity implements MainActivityView {
    private static final String TAG = "MainActivity";

    Intent starterIntent;

//    private SpotifyAppRemote spotifyAppRemote;

    private RecyclerView recyclerView;
    private BottomSheetBehavior bottomSheetBehavior;
    private MaterialProgressBar playProgressBar;

    Toolbar toolbar;
    private TextView nowPlayingTitle;
    private TextView nowPlayingAlbum;
    private FloatingActionButton pauseResumeButton;
    private ImageView nowPlayingAlbumLarge;


    private Player player;
    private Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        starterIntent = getIntent();
        String authToken = starterIntent.getStringExtra("authToken");

        recyclerView = findViewById(R.id.trackListRecyclerView);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        View nowPlayingBottomSheet = findViewById(R.id.nowPlayingBottomSheet);
        bottomSheetBehavior = BottomSheetBehavior.from(nowPlayingBottomSheet);
        nowPlayingTitle = findViewById(R.id.nowPlayingTitle);
        nowPlayingAlbum = findViewById(R.id.nowPlayingAlbum);
        nowPlayingAlbumLarge = findViewById(R.id.nowPlayingAlbumArtLarge);
        FrameLayout nowPlayingBar = findViewById(R.id.nowPlayingTitleFrame);

        View bg = findViewById(R.id.bg);

        pauseResumeButton = findViewById(R.id.playPauseFab);
        playProgressBar = findViewById(R.id.playProgressBar);


        player = new SpotifyRemotePlayer(this);

        presenter = new MainActivityPresenter(this,
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
                if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });

        nowPlayingBottomSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }

            }
        });

        bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

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
    public void onBackPressed() {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            super.onBackPressed();
        }
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
}
