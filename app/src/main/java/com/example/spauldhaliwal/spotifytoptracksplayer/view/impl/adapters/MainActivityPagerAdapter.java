package com.example.spauldhaliwal.spotifytoptracksplayer.view.impl.adapters;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.spauldhaliwal.spotifytoptracksplayer.view.impl.ArtistSearchFragment;
import com.example.spauldhaliwal.spotifytoptracksplayer.view.impl.TrackListFragment;

public class MainActivityPagerAdapter extends FragmentPagerAdapter {

    private String[] tabTitles = new String[]{"Songs", "Artists"};


    public MainActivityPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: return TrackListFragment.newInstance();
            case 1: return ArtistSearchFragment.newInstance();
            default: return TrackListFragment.newInstance();
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}