package com.example.spauldhaliwal.spotifytoptracksplayer.view.impl;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.transition.Transition;
import android.support.transition.TransitionInflater;
import android.support.v4.app.Fragment;
import android.support.v4.app.SharedElementCallback;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.spauldhaliwal.spotifytoptracksplayer.R;
import com.example.spauldhaliwal.spotifytoptracksplayer.model.impl.TrackModel;
import com.example.spauldhaliwal.spotifytoptracksplayer.view.FragmentAdapterHolder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

import static com.example.spauldhaliwal.spotifytoptracksplayer.Constants.PAGER_DRAG_THRESHOLD;

public class NowPlayingPagerFragment extends Fragment implements FragmentAdapterHolder, TrackListFragment.OnTrackSelectedListener {
    private static final String TAG = "NowPlayingPagerFragment";

    private ViewPager viewPager;
    private OnNowPlayingDraggedListener mCallback;

    private int currentPage;
    private int selectedPage = 0;
    private int selectedDirection = 0;
    private int dragDirection = 0;
    private int oldDragDirection = 0;
    private int positionInPixels = 0;
    private int dragState;
    private boolean beingDragged = false;
    private boolean readyToSwitch = false;
    private boolean trackIsBeingSeleted;

    int positionAtSwitch = 0;

    public static NowPlayingPagerFragment newInstance(List<TrackModel> trackList) {
        NowPlayingPagerFragment fragment = new NowPlayingPagerFragment();
        Bundle argument = new Bundle();
        argument.putSerializable("TrackList", (Serializable) trackList);
        fragment.setArguments(argument);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewPager = (ViewPager) inflater.inflate(
                R.layout.now_playing_pager_fragment, container, false);
        OverScrollDecoratorHelper.setUpOverScroll(viewPager);
        prepareSharedElementTransition();

        // Avoid a postponeEnterTransition on orientation change, and postpone only of first creation.
        if (savedInstanceState == null) {
            postponeEnterTransition();
        }
        loadQueue(new ArrayList<TrackModel>());

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            private float lastPositionAndOffsetSum = 0f;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                currentPage = position;
                if (position + positionOffset == lastPositionAndOffsetSum) {
                    //IT'S NOT MOVING
                    dragDirection = 0;

                    if (dragState == ViewPager.SCROLL_STATE_DRAGGING) {
                        // page was dragged, but not realeased, and is not moving
                    } else if (dragState == ViewPager.SCROLL_STATE_SETTLING) {
                        // page was dragged, and released but not flinged
                    } else if (dragState == ViewPager.SCROLL_STATE_IDLE) {
                        beingDragged = false;
                    }

                } else if (position + positionOffset > lastPositionAndOffsetSum) {
                    //RIGHT TO LEFT
                    dragDirection = 1;
                    if (!beingDragged && dragState == ViewPager.SCROLL_STATE_DRAGGING
                            && oldDragDirection == 0) {
                        Log.d(TAG, "onPageScrolled: page first began moving left-------------------->>");
                        beingDragged = true;
                        oldDragDirection = dragDirection;
                        mCallback.pagedDragged(dragState, dragDirection);
                        positionAtSwitch = 0;
                        readyToSwitch = false;

                    } else if (dragState == ViewPager.SCROLL_STATE_DRAGGING
                            && dragDirection == oldDragDirection * -1) {
                        // page was dragged left, but not realeased, and is moving left moving
                        Log.d(TAG, "onPageScrolled: page SWITCHED DIRECTION going left with direction: " + dragDirection);
                        oldDragDirection = dragDirection;
                        positionInPixels = positionOffsetPixels;
                        readyToSwitch = true;

                    } else if (dragState == ViewPager.SCROLL_STATE_DRAGGING
                            && readyToSwitch
                            && positionOffsetPixels - positionInPixels >= PAGER_DRAG_THRESHOLD) {
                        Log.d(TAG, "onPageScrolled: page SWITCHED DIRECTION ULTRA TRIGGER going left");
                        Log.d(TAG, "onPageScrolled: page oldDragDirection: " + oldDragDirection);
                        Log.d(TAG, "onPageScrolled: page dragDirection: " + dragDirection);
                        positionAtSwitch = position;
                        readyToSwitch = false;
                        mCallback.pageDragDirectionSwitched(dragDirection);

                    } else if (dragState == ViewPager.SCROLL_STATE_DRAGGING
                            && dragDirection == oldDragDirection) {
                        // Page began moving left and is still moving left
                        // do nothing
                    } else if (dragState == ViewPager.SCROLL_STATE_SETTLING) {
                        // page was dragged left, and flinged to release
                        // reset values
                        dragDirection = 0;
                        oldDragDirection = 0;
                        positionInPixels = 0;
                    }

                } else {
                    //LEFT TO RIGHT
                    dragDirection = -1;
                    if (!beingDragged && dragState == ViewPager.SCROLL_STATE_DRAGGING
                            && oldDragDirection == 0) {
                        Log.d(TAG, "onPageScrolled: page first began moving right-------------------->>");
                        beingDragged = true;
                        oldDragDirection = dragDirection;
                        mCallback.pagedDragged(dragState, dragDirection);

                    } else if (dragState == ViewPager.SCROLL_STATE_DRAGGING
                            && dragDirection == oldDragDirection * -1) {
                        // page was dragged left, but not realeased, and is moving left moving
                        Log.d(TAG, "onPageScrolled: page SWITCHED DIRECTION going right at position: " + positionOffsetPixels);
                        oldDragDirection = dragDirection;
                        positionInPixels = positionOffsetPixels;
                        readyToSwitch = true;

                    } else if (dragState == ViewPager.SCROLL_STATE_DRAGGING
                            && readyToSwitch
                            && positionInPixels - positionOffsetPixels >= PAGER_DRAG_THRESHOLD) {
                        Log.d(TAG, "onPageScrolled: page SWITCHED DIRECTION ULTRA TRIGGER going right");
                        Log.d(TAG, "onPageScrolled: page oldDragDirection: " + oldDragDirection);
                        Log.d(TAG, "onPageScrolled: page dragDirection: " + dragDirection);
                        positionAtSwitch = position;
                        readyToSwitch = false;
                        mCallback.pageDragDirectionSwitched(dragDirection);

                    } else if (dragState == ViewPager.SCROLL_STATE_DRAGGING
                            && dragDirection == oldDragDirection) {
                        // Page began moving left and is still moving left
                        // do nothing
                    } else if (dragState == ViewPager.SCROLL_STATE_SETTLING) {
                        // page was dragged left, and flinged to release
                        // reset values
                        Log.d(TAG, "onPageScrolled: dragDirection: " + dragDirection);
                        dragDirection = 0;
                        oldDragDirection = 0;
                        positionInPixels = 0;
                    }
                }
                lastPositionAndOffsetSum = position + positionOffset;
            }

            @Override
            public void onPageSelected(int position) {
                if (selectedPage >= position) {
                    selectedPage = position;
                    selectedDirection = -1;
                    mCallback.pageChanged(selectedDirection);
                } else if (selectedPage <= position) {
                    selectedPage = position;
                    selectedDirection = 1;
                    mCallback.pageChanged(selectedDirection);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                dragState = state;
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    beingDragged = false;
                    mCallback.pagedDragged(ViewPager.SCROLL_STATE_IDLE, selectedDirection);
                }

            }
        });
        return viewPager;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void loadQueue(List<TrackModel> trackList) {
//        Bundle arguments = getArguments();
//        trackList = (ArrayList<TrackModel>) arguments.getSerializable("TrackList");
        viewPager.setAdapter(new NowPlayingPagerAdapter(trackList, this));

        viewPager.setCurrentItem(MainActivityViewImpl.currentPosition);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                MainActivityViewImpl.currentPosition = position;
            }
        });

        viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
    }

    private void prepareSharedElementTransition() {
        Transition transition =
                TransitionInflater.from(getContext())
                        .inflateTransition(R.transition.image_shared_element_transition);
        transition.setDuration(300);
        setSharedElementEnterTransition(transition);

        // A similar mapping is set at the GridFragment with a setExitSharedElementCallback.
        setEnterSharedElementCallback(
                new SharedElementCallback() {
                    @Override
                    public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                        // Locate the image view at the primary fragment (the ImageFragment that is currently
                        // visible). To locate the fragment, call instantiateItem with the selection position.
                        // At this stage, the method will simply return the fragment at the position and will
                        // not create a new one.
                        Fragment currentFragment = (Fragment) viewPager.getAdapter()
                                .instantiateItem(viewPager, MainActivityViewImpl.currentPosition);
                        View view = currentFragment.getView();
                        if (view == null) {
                            return;
                        }

                        // Map the first shared element name to the child ImageView.
                        sharedElements.put(names.get(0), view.findViewById(R.id.nowPlayingAlbumArtPage));
                    }
                });
    }

    public void setOnNowPlayingDraggedListener(Activity activity) {
        mCallback = (OnNowPlayingDraggedListener) activity;
    }

    @Override
    public void onTrackSelected(TrackModel trackModel, List trackList) {
//        trackIsBeingSeleted = true;
        mCallback.stateIsBeingRefreshed(true);
        viewPager.setCurrentItem(trackModel.getIndex(), true);
//        trackIsBeingSeleted = false;
    }

    public void onTrackChannged(TrackModel trackModel) {
        Log.d(TAG, "onTrackChannged: trackIndex: " + trackModel.getIndex());
        mCallback.stateIsBeingRefreshed(true);
        viewPager.setCurrentItem(trackModel.getIndex(), true);
    }

    public interface OnNowPlayingDraggedListener {
        void pagedDragged(int state, int direction);

        void pageDragDirectionSwitched(int direction);

        void pageChanged(int direction);

        void stateIsBeingRefreshed(boolean isBeingRefreshed);
    }

}
