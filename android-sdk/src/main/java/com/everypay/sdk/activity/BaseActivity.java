package com.everypay.sdk.activity;

import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;


import com.everypay.sdk.fragment.RetainFragment;
import com.everypay.sdk.util.DialogUtil;
import com.everypay.sdk.util.Log;

public class BaseActivity extends AppCompatActivity {

    protected Log log = Log.getInstance(this);

    public void setRetainObject(final Object object) {
        setRetainObject(RetainFragment.DEFAULT_KEY, object);
    }

    public void setRetainObject(final String key, final Object object) {
        try {
            final RetainFragment retainFragment = RetainFragment.findOrCreateRetainFragment(getSupportFragmentManager());
            retainFragment.setObject(key, object);
        } catch (IllegalStateException e) {
            log.e("setRetainObject", e);
        }
    }

    public Object getRetainObject() {
        return getRetainObject(RetainFragment.DEFAULT_KEY);
    }

    public Object getRetainObject(final String key) {
        final RetainFragment retainFragment = RetainFragment.findOrCreateRetainFragment(getSupportFragmentManager());
        return retainFragment.getObject(key);
    }

    protected void removeRetainObject(final Class clazz) {
        removeRetainObject(RetainFragment.DEFAULT_KEY, clazz);
    }

    protected void removeRetainObject(final String key, final Class clazz) {
        if (getRetainObject(key) != null && TextUtils.equals(clazz.getCanonicalName(), getRetainObject(key).getClass().getCanonicalName())) {
            setRetainObject(key, null);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DialogUtil.removeAllPendingActionsIfAny();
    }
}
