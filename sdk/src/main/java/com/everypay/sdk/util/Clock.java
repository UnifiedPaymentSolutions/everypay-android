package com.everypay.sdk.util;

import java.util.Calendar;

/**
 * From https://github.com/stripe/stripe-android/blob/master/stripe/src/main/java/com/stripe/android/time/Clock.java
 */
public class Clock {
    private static Clock instance;
    protected Calendar calendarInstance;

    protected static Clock getInstance() {
        if (instance == null) {
            instance = new Clock();
        }
        return instance;
    }

    private Calendar _calendarInstance() {
        return calendarInstance != null ? (Calendar) calendarInstance.clone() : Calendar.getInstance();
    }

    public static Calendar getCalendarInstance() {
        return getInstance()._calendarInstance();
    }
}
