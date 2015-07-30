package com.everypay.sdk.util;


/**
 * From https://github.com/stripe/stripe-android/blob/master/stripe/src/main/java/com/stripe/android/util/TextUtils.java
 */
public class TxtUtils {
    public static boolean hasAnyPrefix(String number, String... prefixes) {
        if (number == null) {
            return false;
        }
        for (String prefix : prefixes) {
            if (number.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isWholePositiveNumber(String value) {
        if (value == null) {
            return false;
        }
        for (char c : value.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }

    public static String nullIfBlank(String value) {
        if (isBlank(value)) {
            return null;
        }
        return value;
    }

    public static boolean isBlank(String value) {
        return value == null || value.trim().length() == 0;
    }
}
