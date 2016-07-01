package com.everypay.sdk;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.PermissionRequest;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.everypay.sdk.util.Log;
import com.everypay.sdk.util.Util;
import com.everypay.sdk.util.WebViewStorage;
import com.rey.material.widget.ProgressView;

import java.util.Locale;

import static com.everypay.sdk.EveryPaySession.WEBVIEW_RESULT_FAILURE;


public class PaymentBrowserActivity extends AppCompatActivity {

    private static final String STATE_PROGRESS_VISIBILITY = "com.everypay.sdk.STATE_PROGRESS_VISIBILITY";
    private static final String EXTRA_GATEWAY_URL = "com.everypay.sdk.EXTRA_GATEWAY_URL";
    private static final String EXTRA_ID = "com.everypay.sdk.EXTRA_GATEWAY_ID";
    private static final String BROWSER_FLOW_END_URL_PATH = "authentication3ds/";
    private static final String PAYMENT_STATE_AUTHORISED = "authorised";
    private static final String PAYMENT_STATE = "payment_state";

    private ViewGroup layoutRoot;
    private ProgressView progressBar;
    private String id;
    private static String browserFlowEndUrlPrefix;
    private ViewGroup layoutContainer;
    private Log log = Log.getInstance(this);
    private String gatewayURL;

    public static void start(EveryPay ep, final Context context, final String url, final String id) {
        setBrowserFlowEndUrl(ep);
        final Intent intent = new Intent(context, PaymentBrowserActivity.class);
        intent.putExtra(EXTRA_GATEWAY_URL, url);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(EXTRA_ID, id);

        context.startActivity(intent);
    }

    private static void setBrowserFlowEndUrl(EveryPay ep) {
        browserFlowEndUrlPrefix = ep.getEverypayUrl() + BROWSER_FLOW_END_URL_PATH;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log.d("onCreate");
        setContentView(R.layout.payment_browser_activity);
        layoutRoot = (ViewGroup) findViewById(R.id.layout_generic_root);
        layoutContainer = (ViewGroup) findViewById(R.id.layout_payment_browser_container);
        progressBar = (ProgressView) layoutRoot.findViewById(R.id.progress);
        if (savedInstanceState != null) {
            //noinspection WrongConstant
            progressBar.setVisibility(savedInstanceState.getInt(STATE_PROGRESS_VISIBILITY, View.VISIBLE));
        } else if (getIntent() != null && getIntent().getExtras() != null) {
            gatewayURL = getIntent().getExtras().getString(EXTRA_GATEWAY_URL);
            id = getIntent().getExtras().getString(EXTRA_ID);
            if (TextUtils.isEmpty(gatewayURL)) {
                log.e("No gateway URL provided : Finishing activity");
                finish();
            }
        }
    }

    @Override
    protected void onStart() {
        initWebView();
        super.onStart();
        log.d("onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        clearWebViewInstanceFromActivityReferences();
        log.d("onStop");
    }

    private void clearWebViewInstanceFromActivityReferences() {
        log.d("clearWebViewInstanceFromActivityReferences");
        if (layoutContainer != null) {
            layoutContainer.removeAllViews();
        }
        if (WebViewStorage.getInstance().getWebView() != null) {
            WebViewStorage.getInstance().getWebView().setWebChromeClient(null);
            WebViewStorage.getInstance().getWebView().setWebViewClient(null);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearWebViewInstanceFromActivityReferences();

        EveryPay.getDefault().setWebViewResult(id, "no");
        if (isFinishing()) {
            WebViewStorage.destroyInstance();
        }
        log.d("onDestroy");
    }

    @SuppressLint({"SetJavaScriptEnabled"})
    private void initWebView() {
        WebView webView = WebViewStorage.getInstance().getWebView();
        boolean isInit = webView == null;
        log.d("initWebView - isInit: " + isInit);
        if (isInit) {
            webView = new WebView(this);
            webView.setTag("webview_payment_browser");

            final WebSettings settings = webView.getSettings();
            settings.setJavaScriptEnabled(true);
            settings.setDomStorageEnabled(true);
            settings.setSupportMultipleWindows(true);
            settings.setLoadsImagesAutomatically(true);
            settings.setJavaScriptCanOpenWindowsAutomatically(true);
            settings.setCacheMode(WebSettings.LOAD_NO_CACHE);

            webView.setWebViewClient(new WebClientImpl());
            webView.setWebChromeClient(new WebClientChromeImpl());

            // Store
            WebViewStorage.getInstance().setWebView(webView);
        }
        // Always Update the clients, otherwise the callbacks will go to the wrong place
        webView.setWebViewClient(new WebClientImpl());
        webView.setWebChromeClient(new WebClientChromeImpl());


        if (webView.getParent() != null && webView.getParent() instanceof ViewGroup) {
            ((ViewGroup) webView.getParent()).removeAllViews();
        }
        layoutContainer.addView(webView);
        if (isInit && !TextUtils.isEmpty(gatewayURL)) {
            layoutContainer.post(new Runnable() {
                @Override
                public void run() {
                    WebViewStorage.getInstance().getWebView().loadUrl(gatewayURL);
                }
            });

        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (progressBar != null) {
            outState.putInt(STATE_PROGRESS_VISIBILITY, progressBar.getVisibility());
        }
        super.onSaveInstanceState(outState);
    }

    private void updateProgress(final boolean show) {
        // We currently do not care about actual progress
        if (!show) {
            progressBar.setVisibility(View.GONE);
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
    }

    private boolean isBrowserFlowEndUrl(final String url) {
        if (Util.contains(url, PAYMENT_STATE)) {
            log.d("isBrowserFlowEndUrl - true, url: " + url);
            return true;
        }

        return false;
    }

    private void onBrowserFlowEnded(final String url) {
        if (isBrowserFlowSuccessful(url)) {
            log.d("onBrowserFlowEnded - result: success, url: " + url);
            String paymentReference = getSuccessResultFromURL(url);
            EveryPay.getDefault().setWebViewResult(id, paymentReference);
            finish();
        } else {
            log.d("onBrowserFlowEnded - result: fail, url: " + url);
            EveryPay.getDefault().setWebViewResult(id, WEBVIEW_RESULT_FAILURE);
            finish();
        }
    }

    private String getSuccessResultFromURL(String url) {
        // Simple string cutting. Cannot use Util.getUrlParamValue since payment reference is part of the url and not a parameter
        String urlWithoutPrefix = url.replace(browserFlowEndUrlPrefix, "");
        String[] parts = urlWithoutPrefix.split("\\?");
        return parts[0];
    }

    private boolean  isBrowserFlowSuccessful(final String url) {
        String paymentState = Util.getUrlParamValue(url, PAYMENT_STATE, null);
        return !TextUtils.isEmpty(url) && url.toLowerCase(Locale.ENGLISH).startsWith(browserFlowEndUrlPrefix) && !TextUtils.isEmpty(paymentState) && TextUtils.equals(paymentState, PAYMENT_STATE_AUTHORISED);
    }

    private class WebClientImpl extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            log.d("shouldOverrideUrlLoading: " + url);
            boolean isBrowserFlowEndUrl = isBrowserFlowEndUrl(url);
            if (isBrowserFlowEndUrl) {
                log.d("shouldOverrideUrlLoading (yes, payment end): " + url);
                onBrowserFlowEnded(url);
                return true;
            }

            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            log.d("onPageStarted: " + url);
            super.onPageStarted(view, url, favicon);
            updateProgress(true);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            log.d("onPageFinished: " + url);
            super.onPageFinished(view, url);
            updateProgress(false);
        }


        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            log.e("onReceivedSslError: " + error);
            super.onReceivedSslError(view, handler, error);
        }

        @Override
        public void onLoadResource(WebView view, String url) {
            log.d("onLoadResource: " + url);
            super.onLoadResource(view, url);
        }

    }

    private class WebClientChromeImpl extends WebChromeClient {
        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            log.d("onJsAlert: " + url + ", " + message);
            return super.onJsAlert(view, url, message, result);
        }

        @Override
        public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
            log.d("onJsConfirm: " + url + ", " + message);
            return super.onJsConfirm(view, url, message, result);
        }

        @Override
        public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
            log.d("onJsPrompt: " + url + ", " + message);
            return super.onJsPrompt(view, url, message, defaultValue, result);
        }


        @Override
        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
            log.d("onConsoleMessage: " + consoleMessage);
            return super.onConsoleMessage(consoleMessage);
        }

        @Override
        public void onPermissionRequest(PermissionRequest request) {
            log.d("onPermissionRequest: " + request);
            super.onPermissionRequest(request);
        }

        @Override
        public void onPermissionRequestCanceled(PermissionRequest request) {
            log.d("onPermissionRequestCanceled: " + request);
            super.onPermissionRequestCanceled(request);
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
        }

        @Override
        public boolean onJsBeforeUnload(WebView view, String url, String message, JsResult result) {
            log.d("onJsBeforeUnload: " + url + ", " + message);
            return super.onJsBeforeUnload(view, url, message, result);
        }
    }
}

