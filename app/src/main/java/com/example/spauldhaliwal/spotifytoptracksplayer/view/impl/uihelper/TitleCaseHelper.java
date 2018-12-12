package com.example.spauldhaliwal.spotifytoptracksplayer.view.impl.uihelper;

public abstract class TitleCaseHelper {

    public static String convertToTitleCase(String string) {
        String[] strArray = string.split(" ");
        StringBuilder builder = new StringBuilder();
        for (String s : strArray) {
            String cap = s.substring(0, 1).toUpperCase() + s.substring(1);
            builder.append(cap + " ");
        }
        return builder.toString();
    }
}