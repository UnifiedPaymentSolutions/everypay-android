package com.everypay.sdk.util;


import android.util.Pair;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Reflect {

    public static void setString(Object obj, String methodName, String value) {
        try {
            Method method = obj.getClass().getMethod(methodName, String.class);
            method.invoke(obj, value);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Reflection failed.", e);
        } catch (InvocationTargetException e) {
            throw new IllegalArgumentException("Reflection failed.", e);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Reflection failed.", e);
        }
    }

    public static String getString(Object obj, String methodName) {
        try {
            Method method = obj.getClass().getMethod(methodName);
            return (String)method.invoke(obj);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Reflection failed.", e);
        } catch (InvocationTargetException e) {
            throw new IllegalArgumentException("Reflection failed.", e);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Reflection failed.", e);
        }
    }

    public static void call(Object obj, String methodName, Pair<Class, Object>... typedArgs) throws Throwable {
        try {
            Class[] types = new Class[typedArgs.length];
            Object[] args = new Object[typedArgs.length];
            for (int i = 0; i < typedArgs.length; ++i) {
                types[i] = typedArgs[i].first;
                args[i] = typedArgs[i].second;
            }

            Method method = obj.getClass().getMethod(methodName, types);
            method.invoke(obj, args);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Reflection failed.", e);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("Reflection failed.", e);
        }
    }
}
