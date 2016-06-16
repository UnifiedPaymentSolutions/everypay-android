
package com.everypay.sdk.util;

import android.text.TextUtils;

import timber.log.Timber;

/**
 * Logging wrapper.
 */
public class Log {
    /**
     * Debug log level.
     */
    public static String LOG_LEVEL_DEBUG = "debug";
    /**
     * Release log level.
     */
    public static String LOG_LEVEL_RELEASE = "release";

    private static String logLevel;
    private String tag;

    private Log(final String tag) {
        setLogLevel(logLevel);
        this.tag = tag;
    }

    /**
     * Method to get a new Log instance.
     *
     * @param object object to pass to Log instance.
     * @return Log instance.
     */
    public static Log getInstance(final Object object) {
        return getInstance(object.getClass());
    }

    /**
     * Method to get a new Log instance.
     *
     * @param clazz Class to pass to Log instance.
     * @return Log instance.
     */
    public static Log getInstance(final Class clazz) {
        return getInstance(clazz.getSimpleName());
    }

    /**
     * Method to get a new Log instance.
     *
     * @param tag tag to pass to Log instance.
     * @return Log instance.
     */
    public static Log getInstance(final String tag) {
        return new Log(tag);
    }

    /**
     * Method to set the logging Level.
     *
     * @param newLogLevel Logging level. Either {@link Log#LOG_LEVEL_DEBUG} or {@link Log#LOG_LEVEL_RELEASE}.
     */
    public static void setLogLevel(final String newLogLevel) {
        if (!TextUtils.equals(logLevel, newLogLevel)) {
            Timber.uprootAll();
            if (LOG_LEVEL_DEBUG.equalsIgnoreCase(newLogLevel)) {
                Timber.plant(new Timber.DebugTree());
            } else {
                Timber.plant(new ErrorTree());
            }
        }
        logLevel = newLogLevel;
    }

    /**
     * Method to log out on the info level.
     *
     * @param msg message to log out.
     */
    public void i(final String msg) {
        i(msg, null);
    }

    /**
     * Method to log on the info level with a throwable.
     *
     * @param msg message to log out.
     * @param e   Throwable to log out if exists.
     */
    public void i(final String msg, final Throwable e) {
        Timber.tag(tag);
        if (e == null) {
            Timber.i(msg);
        } else {
            Timber.i(e, msg);
        }
    }


    /**
     * Method to log on the debug level.
     *
     * @param msg message to log out.
     */
    public void d(final String msg) {
        d(msg, null);
    }

    /**
     * Method to log out debug info with throwable.
     *
     * @param msg message to log out.
     * @param e   Throwable to log out if exists.
     */
    public void d(final String msg, final Throwable e) {
        Timber.tag(tag);
        if (e == null) {
            Timber.d(msg);
        } else {
            Timber.d(e, msg);
        }
    }

    /**
     * Method to log on the error level.
     *
     * @param msg message to log out.
     */
    public void e(final String msg) {
        d(msg, null);
    }

    /**
     * Method to log on the error level with a throwable.
     *
     * @param msg message to log out.
     * @param e   Throwable to log out if exists.
     */
    public void e(final String msg, final Throwable e) {
        Timber.tag(tag);
        if (e == null) {
            Timber.e(msg);
        } else {
            Timber.e(e, msg);
        }
    }

    /**
     * Method to log on the warning level.
     *
     * @param msg message to log out.
     */
    public void w(final String msg) {
        w(msg, null);
    }

    /**
     * Method to log on the warning level with a throwable.
     *
     * @param msg message to log out.
     * @param e   Throwable to log out if exists.
     */
    public void w(final String msg, final Throwable e) {
        Timber.tag(tag);
        if (e == null) {
            Timber.w(msg);
        } else {
            Timber.w(e, msg);
        }
    }

    /**
     * Method to log out on the "What a Terrible Failure" level.
     *
     * @param msg message to log out.
     */
    public void wtf(final String msg) {
        wtf(msg, null);
    }

    /**
     * Method to log out on the "What a Terrible Failure" level including a throwable.
     *
     * @param msg message to log out.
     * @param e   Throwable to log out if exists.
     */
    public void wtf(final String msg, final Throwable e) {
        Timber.tag(tag);
        if (e == null) {
            Timber.wtf(msg);
        } else {
            Timber.wtf(e, msg);
        }
    }

}
