package com.example.NCEPU.Student.TimeTable.util;

import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.example.NCEPU.Student.TimeTable.MyApplication;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeUnit;

import okhttp3.CookieJar;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OkHttpUtils {
    private static final byte[] EMPTY_BYTES = new byte[0];
    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");

    /**
     * 自动存储保存cookies
     * <p>
     * private static class MyCookieJar implements CookieJar {
     * private Map<String, List<Cookie>> cookieStore = new HashMap<>();
     *
     * @NotNull
     * @Override public List<Cookie> loadForRequest(@NotNull HttpUrl httpUrl) {
     * List<Cookie> cookies = cookieStore.get(httpUrl.host());
     * return cookies != null ? cookies : new ArrayList<Cookie>();
     * }
     * @Override public void saveFromResponse(@NotNull HttpUrl httpUrl, @NotNull List<Cookie> list) {
     * cookieStore.put(httpUrl.host(), list);
     * }
     * }
     * <p>
     * private static MyCookieJar cookieJar = new MyCookieJar();
     */

    /**
     * 静态内部类单例模式
     * 需要时加载
     * <p>
     * 只有第一次调用getInstance方法时，
     * 虚拟机才加载 Inner 并初始化okHttpClient，
     * 只有一个线程可以获得对象的初始化锁，其他线程无法进行初始化，
     * 保证对象的唯一性。目前此方式是所有单例模式中最推荐的模式。
     */
    private static class Inner {
        private static final CookieJar cookieJar = new PersistentCookieJar(
                new SetCookieCache(),
                new SharedPrefsCookiePersistor(MyApplication.getApplication()));
        private static OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .cookieJar(cookieJar)
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .followRedirects(true)
                .build();
    }

    public static OkHttpClient getOkHttpClient() {
        return Inner.okHttpClient;
    }

    public static void setFollowRedirects(boolean followRedirects) {
        if (Inner.okHttpClient.followRedirects() != followRedirects) {
            Inner.okHttpClient = new OkHttpClient().newBuilder()
                    .cookieJar(Inner.cookieJar)
                    .connectTimeout(5, TimeUnit.SECONDS)
                    .readTimeout(5, TimeUnit.SECONDS)
                    .writeTimeout(5, TimeUnit.SECONDS)
                    .followRedirects(followRedirects)
                    .build();
        }
    }

    public static CookieJar getCookieJar() {
        return Inner.cookieJar;
    }

    /**
     * 下载文件到本地
     *
     * @param url
     * @param path 文件夹地址
     * @param name 文件名
     * @return
     */
    public static boolean downloadToLocal(String url, String path, String name) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        return downloadToLocal(request, path, name);
    }

    /**
     * 下载文件到本地
     *
     * @param path 文件夹地址
     * @param name 文件名
     * @return
     */
    public static boolean downloadToLocal(Request request, String path, String name) {
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            Response response = getOkHttpClient().newCall(request).execute();
            if (response.code() == 200) {
                File file = new File(path);
                if (!file.exists()) {
                    file.mkdirs();
                } else {
                    if (!file.isDirectory())
                        return false;
                }

                bos = new BufferedOutputStream(
                        new FileOutputStream(path + File.separator + name));
                bis = new BufferedInputStream(response.body().byteStream());

                byte[] buffer = new byte[1024];
                int len;
                while ((len = bis.read(buffer, 0, 1024)) != -1) {
                    bos.write(buffer, 0, len);
                }
                bos.flush();

                return true;
            }
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bis != null) {
                    bis.close();
                }
                if (bos != null) {
                    bos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return false;
    }

    /**
     * 下载文本内容
     *
     * @param url
     * @return
     */
    public static String downloadText(String url) {
        return downloadText(url, "UTF-8");
    }

    /**
     * 下载文本内容
     *
     * @param request
     * @return
     */
    public static String downloadText(Request request) {
        return downloadText(request, "UTF-8");
    }

    /**
     * 下载文本内容
     *
     * @param url
     * @return
     */
    public static String downloadText(String url, String encoding) {
        try {
            return new String(downloadRaw(url), encoding);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 下载文本内容
     *
     * @param request
     * @param encoding
     * @return
     */
    public static String downloadText(Request request, String encoding) {
        try {
            return new String(downloadRaw(request), encoding);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 下载字节码
     *
     * @param url
     * @return
     */
    public static byte[] downloadRaw(String url) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        return downloadRaw(request);
    }

    /**
     * 下载字节码
     *
     * @param request
     * @return 返回下载内容
     */
    public static byte[] downloadRaw(Request request) {
        try {
            Response response = getOkHttpClient().newCall(request).execute();
            if (response.code() == 200) {
                InputStream is = response.body().byteStream();
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len;
                while ((len = is.read(buffer, 0, 1024)) != -1) {
                    bos.write(buffer, 0, len);
                }
                bos.flush();
                return bos.toByteArray();
            }
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }
        return EMPTY_BYTES;
    }
}
