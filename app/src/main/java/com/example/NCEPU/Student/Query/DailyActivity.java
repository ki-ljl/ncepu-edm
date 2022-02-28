package com.example.NCEPU.Student.Query;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.example.NCEPU.MainActivity;
import com.example.NCEPU.R;

import java.util.Map;

import static com.example.NCEPU.Student.Query.QueryFragment.dip2px;

public class DailyActivity extends AppCompatActivity {

    private ImageButton imageButton;
    private LinearLayout ly_back;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily);

        imageButton = findViewById(R.id.ib_back_day);
        ly_back = findViewById(R.id.ly_back_day);
        imageButton.setOnClickListener(v -> onBackPressed());

        setHeight();

        webView = findViewById(R.id.daily_reporting);
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
        webView.setWebViewClient(new DailyActivity.MyWebViewClient());
        webView.setWebChromeClient(new DailyActivity.MyWebChromeClient());

        SharedPreferences sharedPreferences = getSharedPreferences("user_info", Context.MODE_PRIVATE);
        String id = sharedPreferences.getString("id", "");
        String url = "https://app.ncepu.edu.cn/ncov/wap/default/index";

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
        cookieManager.setAcceptCookie(true);
//        cookieManager.setCookie(url, in);
//        cookieManager.setCookie(url, out);
//        cookieManager.setCookie(url, "_astraeus_session=" + cookies_in.get("_astraeus_session"));
//        cookieManager.setCookie(url, "_webvpn_key=" + cookies_in.get("_webvpn_key"));
//        cookieManager.setCookie(url, "webvpn_username=" + cookies_in.get("webvpn_username"));
//        cookieManager.setCookie(url, "JSESSIONID=" + cookies.get("JSESSIONID"));
//        String CookieStr = cookieManager.getCookie(url);
//        System.out.println("huge");
//        System.out.println(CookieStr);
        webView.loadUrl(url);
    }

    private void setHeight() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_info", Context.MODE_PRIVATE);
        float pagerHeight = sharedPreferences.getInt("pager", 0);

        LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams)ly_back.getLayoutParams();
        linearParams.height = dip2px(this, (float) (pagerHeight * 0.062));
        ly_back.setLayoutParams(linearParams);

        linearParams = (LinearLayout.LayoutParams)imageButton.getLayoutParams();
        linearParams.height = dip2px(this, (float) (pagerHeight * 0.0325));
        linearParams.width = dip2px(this, (float) (pagerHeight * 0.0279));
        imageButton.setLayoutParams(linearParams);

    }

    class MyWebViewClient extends WebViewClient {
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
            imgReset();
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

    class MyWebChromeClient extends WebChromeClient {
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