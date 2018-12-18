package com.spauldhaliwal.spotifytoptracksplayer.model;

public interface Cache {

    void storeString(String key, String string);
    void storeInt(String key, int i);
    void storeLong(String key, long l);
    void storeFloat(String key, float f);
    void storeBoolean(String key, boolean x);
}
