package com.example.NCEPU.Student.Query;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;

import com.alibaba.fastjson.JSONObject;
import com.example.NCEPU.MainActivity;
import com.example.NCEPU.R;
import com.example.NCEPU.Student.User.CSDNActivity;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class TeachingEvaluationActivity extends AppCompatActivity {

    private WebView webView;
    private ImageButton back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teaching_evaluation);
        webView = findViewById(R.id.eval);
        back = findViewById(R.id.ib_back_tong);
        back.setOnClickListener(v -> onBackPressed());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setBlockNetworkImage(false);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        //以下两条设置可以使页面适应手机屏幕的分辨率，完整的显示在屏幕上
        //设置是否使用WebView推荐使用的窗口
        webView.getSettings().setUseWideViewPort(true);
        //设置WebView加载页面的模式
        webView.getSettings().setLoadWithOverviewMode(true);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        webView.setWebViewClient(new MyWebViewClient());
        webView.setWebChromeClient(new MyWebChromeClient());

        SharedPreferences sharedPreferences = getSharedPreferences("user_info", Context.MODE_PRIVATE);
        String id = sharedPreferences.getString("id", "");
        String url = "https://202-204-74-178.webvpn.ncepu.edu.cn/jwglxt/xspjgl/xspj_cxXspjIndex.html?doType=details&gnmkdm=N401605&layout=default&su=" + id;

        Map<String, String> cookies_in = MainActivity.connectJWGL.cookies_innet;
        Map<String, String> cookies = MainActivity.connectJWGL.cookies;

        String in = "_astraeus_session=" + cookies_in.get("_astraeus_session");
        in += ";_webvpn_key=" + cookies_in.get("_webvpn_key");
        in += ";webvpn_username=" + cookies_in.get("webvpn_username");
        String out = "JSESSIONID=" + cookies.get("JSESSIONID");
        System.out.println("huge");
        System.out.println(in);
        System.out.println(out);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.setAcceptThirdPartyCookies(webView, true);
            cookieManager.removeSessionCookies(null);
            cookieManager.flush();
        } else {
            cookieManager.removeSessionCookie();
            CookieSyncManager.getInstance().sync();
        }
        cookieManager.setAcceptCookie(true);
        cookieManager.setCookie(url, in);
        cookieManager.setCookie(url, out);
        cookieManager.setCookie(url, "_astraeus_session=" + cookies_in.get("_astraeus_session"));
        cookieManager.setCookie(url, "_webvpn_key=" + cookies_in.get("_webvpn_key"));
        cookieManager.setCookie(url, "webvpn_username=" + cookies_in.get("webvpn_username"));
//        cookieManager.setCookie(url, "JSESSIONID=" + cookies.get("JSESSIONID"));
//        String CookieStr = cookieManager.getCookie(url);
//        System.out.println("huge");
//        System.out.println(CookieStr);
        webView.loadUrl(url);
    }

    class MyWebViewClient extends WebViewClient{
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            view.loadUrl(request.getUrl().toString());
            return true;
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed();
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            Log.d("WebView","onPageStarted...");
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            //imgReset();
            Log.d("WebView","onPageFinished...");
            //mWvMain.loadUrl("javascript:alert('hello')");
            //mWvMain.evaluateJavascript("javascript:alert('hello')",null);
        }

        public void imgReset() {
            webView.loadUrl("javascript:(function(){" +
                    "var obj = document.getElementsByTagName('img'); " +
                    "for(var i=0;i<obj.length;i++) " +
                    "{"
                    + "var img = obj[i]; " +
                    " img.style.maxWidth = '100%'; img.style.height = 'auto'; " +
                    "}" +
                    "})()");
        }
    }

    class MyWebChromeClient extends WebChromeClient{
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);

        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            setTitle(title);
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
