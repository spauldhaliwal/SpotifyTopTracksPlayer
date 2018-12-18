package com.spauldhaliwal.spotifytoptracksplayer.view.impl.uihelper;

import android.graphics.Outline;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewOutlineProvider;

import com.spauldhaliwal.spotifytoptracksplayer.R;

public class ZoomOutPageTransformer implements ViewPager.PageTransformer {
    private static final float MIN_SCALE = 0.915f;
    private static final float MIN_ALPHA = 1f;

    public void transformPage(View view, float position) {
        int pageWidth = view.getWidth();
        int pageHeight = view.getHeight();

        if (position < -1) { // [-Infinity,-1)
            // This page is way off-screen to the left.
            view.setAlpha(0f);

        } else if (position <= 1) { // [-1,1]
            // Modify the default slide transition to shrink the page as well
            float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
            float vertMargin = pageHeight * (1 - scaleFactor) / 2;
            float horzMargin = pageWidth * (1 - scaleFactor) / 2;
            if (position < 0) {
                view.setTranslationX(horzMargin - vertMargin / 2);
            } else {
                view.setTranslationX(-horzMargin + vertMargin / 2);
            }

            // Scale the page down (between MIN_SCALE and 1)
            view.setScaleX(scaleFactor);
            view.setScaleY(scaleFactor);

            // Fade the page relative to its size.
            view.setAlpha(MIN_ALPHA +
                    (scaleFactor - MIN_SCALE) /
                            (1 - MIN_SCALE) * (1 - MIN_ALPHA));
            view.setElevation(50);

            View albumArt = view.findViewById(R.id.nowPlayingAlbumArtPage);
            albumArt.setOutlineProvider(new ViewOutlineProvider() {
                @Override
                public void getOutline(View view, Outline outline) {
//                    outline.setRoundRect(0, 0, view.getWidth(), (view.getHeight()+8), 8);
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), 8);
                }
            });


        } else { // (1,+Infinity]
            // This page is way off-screen to the right.
            view.setAlpha(0f);
        }
    }

}