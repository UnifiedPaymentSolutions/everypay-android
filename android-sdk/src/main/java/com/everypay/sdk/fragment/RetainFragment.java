/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.everypay.sdk.fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import java.util.HashMap;

/**
 * A simple non-UI Fragment that stores a single Object and is retained over configuration changes.
 */
public class RetainFragment extends BaseFragment {
    private static final String TAG = "RetainFragment";
    public static final String DEFAULT_KEY = "com.speakeasy.android.DEFAULT_KEY";
    private HashMap<String, Object> mObjects = new HashMap<String, Object>();

    /**
     * Empty constructor as per the Fragment documentation
     */
    public RetainFragment() {
    }

    /**
     * Locate an existing instance of this Fragment or if not found, create and
     * add it using FragmentManager.
     *
     * @param fm The FragmentManager manager to use.
     * @return The existing instance of the Fragment or the new instance if just
     * created.
     */
    public static RetainFragment findOrCreateRetainFragment(FragmentManager fm) {
        // Check to see if we have retained the worker fragment.
        RetainFragment mRetainFragment = (RetainFragment) fm.findFragmentByTag(TAG);

        // If not retained (or first time running), we need to create and add it.
        if (mRetainFragment == null) {
            mRetainFragment = new RetainFragment();
            fm.beginTransaction().add(mRetainFragment, TAG).commit();
        }

        return mRetainFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make sure this Fragment is retained over a configuration change
        setRetainInstance(true);
    }

    public void setObject(final Object object) {
        mObjects.put(DEFAULT_KEY, object);
    }

    public void setObject(final String key, final Object object) {
        mObjects.put(key, object);
    }

    public Object getObject() {
        return getObject(DEFAULT_KEY);
    }

    public Object getObject(final String key) {
        if (!mObjects.containsKey(key)) {
            return null;
        }
        return mObjects.get(key);

    }

    public java.util.Set<String> getObjectKeys() {
        return mObjects.keySet();
    }

}

