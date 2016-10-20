package com.everypay.sdk.fragment;

import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.widget.TextView;

import com.everypay.sdk.activity.BaseActivity;
import com.everypay.sdk.util.Log;


/**
 * Base fragment that all other fragments should extend for convenience
 * Created by Harri Kirik (harri35@gmail.com).
 */
public class BaseFragment extends Fragment {
    protected Log log = Log.getInstance(this);
    private TextView textEmpty;

    /**
     * Set the retain object throw the BaseActivity. PS: Use with caution, destroys any existing objects!
     *
     * @param object
     */
    protected void setRetainObject(final Object object) {
        setRetainObject(RetainFragment.DEFAULT_KEY, object);
    }

    protected void setRetainObject(final String key, final Object object) {
        if (getActivity() == null || !(getActivity() instanceof BaseActivity)) {
            log.w("setRetainObject - no base class to support this!");
        }
        ((BaseActivity) getActivity()).setRetainObject(key, object);
    }

    /**
     * Get the retain object throw the BaseActivity
     */
    protected Object getRetainObject() {
        return getRetainObject(RetainFragment.DEFAULT_KEY);
    }

    protected Object getRetainObject(final String key) {
        if (getActivity() == null || !(getActivity() instanceof BaseActivity)) {
            log.w("getRetainObject - no base class to support this!");
            return null;
        }

        return ((BaseActivity) getActivity()).getRetainObject(key);
    }

    protected void removeRetainObject(final Object o) {
        if (o == null) {
            return;
        }
        removeRetainObject(o.getClass());
    }

    protected void removeRetainObject(final Class clazz) {
        removeRetainObject(RetainFragment.DEFAULT_KEY, clazz);
    }

    protected void removeRetainObject(final String id, final Class clazz) {
        if (getRetainObject() != null && TextUtils.equals(clazz.getCanonicalName(), getRetainObject().getClass().getCanonicalName())) {
            setRetainObject(null);
        }
    }






}
