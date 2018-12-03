package com.example.spauldhaliwal.spotifytoptracksplayer.view.impl.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.spauldhaliwal.spotifytoptracksplayer.model.impl.TrackModel;
import com.example.spauldhaliwal.spotifytoptracksplayer.view.FragmentAdapterHolder;
import com.example.spauldhaliwal.spotifytoptracksplayer.view.impl.NowPlayingFragment;

import java.util.List;

public class NowPlayingPagerAdapter extends FragmentStatePagerAdapter {
    private static final String TAG = "NowPlayingPagerAdapter";

    private List<TrackModel> tracksList;

    public NowPlayingPagerAdapter(List<TrackModel> tracksList,
                                  FragmentAdapterHolder fragmentAdapterHolder) {
        super(fragmentAdapterHolder.getChildFragmentManager());
        this.tracksList = tracksList;
    }

    @Override
    public Fragment getItem(int position) {
        return NowPlayingFragment.newInstance(tracksList.get(position));
    }

    @Override
    public int getCount() {
        return tracksList.size();
    }
}
