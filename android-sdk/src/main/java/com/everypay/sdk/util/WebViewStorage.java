
package com.everypay.sdk.util;

import android.annotation.SuppressLint;
import android.webkit.WebView;

/**
 * Helper class to keep the WebView for the case when the hosting activity is lost during the browser flow
 * Do not forget to clear the WebView instance or you will leak memory
 * Created by Harri Kirik (harri35@gmail.com)
 */
public class WebViewStorage {
    @SuppressLint("StaticFieldLeak")
    private volatile static WebViewStorage instance;
    private WebView webView;

    private WebViewStorage() {
        super();
    }

    public static WebViewStorage getInstance() {
        if (instance == null) {
            synchronized (WebViewStorage.class) {
                if (instance == null) {
                    instance = new WebViewStorage();
                }
            }
        }
        return instance;
    }

    public WebView getWebView() {
        return webView;
    }

    public void setWebView(final WebView webView) {
        destroy(); // Throw away the last one if we had any
        this.webView = webView;
    }

    public void destroy() {
        if (webView != null) {
            webView.destroy();
        }
        webView = null;
    }

    public static void destroyInstance() {
        if (instance != null) {
            synchronized (WebViewStorage.class) {
                if (instance != null) {
                    instance.destroy();
                    instance = null;
                }
            }
        }
    }
}

