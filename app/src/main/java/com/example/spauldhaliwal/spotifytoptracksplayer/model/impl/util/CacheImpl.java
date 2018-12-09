package com.example.spauldhaliwal.spotifytoptracksplayer.model.impl.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.spauldhaliwal.spotifytoptracksplayer.Constants;
import com.example.spauldhaliwal.spotifytoptracksplayer.model.Cache;

public abstract class CacheImpl implements Cache {

    protected SharedPreferences sharedPreferences;

    public CacheImpl(Context context) {
        sharedPreferences = context
                .getSharedPreferences(Constants.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public void storeString(String key, String string) {
        sharedPreferences.edit().putString(key, string).apply();
    }

    @Override
    public void storeInt(String key, int i) {
        sharedPreferences.edit().putInt(key, i).apply();
    }

    @Override
    public void storeLong(String key, long l) {
        sharedPreferences.edit().putLong(key, l).apply();
    }

    @Override
    public void storeFloat(String key, float f) {
        sharedPreferences.edit().putFloat(key, f).apply();
    }

    @Override
    public void storeBoolean(String key, boolean x) {
        sharedPreferences.edit().putBoolean(key, x).apply();
    }
}
