package com.everypay.sdk.util;


import android.text.TextUtils;

public class Str {

    public static String spaced(String str, int afterEvery) {
        if (afterEvery <= 0)
            throw new IllegalArgumentException("Cannot add spaces after every " + afterEvery + " characters.");

        if (TextUtils.isEmpty(str))
            return "";

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); ++i) {
            sb.append(str.charAt(i));
            if ((i + 1) % afterEvery == 0)
                sb.append(' ');
        }
        return sb.toString().trim();
    }
}
