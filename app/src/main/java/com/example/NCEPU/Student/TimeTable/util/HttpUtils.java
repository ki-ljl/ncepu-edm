package com.example.NCEPU.Student.TimeTable.util;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class HttpUtils {
    /**
     * ping 判断网络连通
     *
     * @return
     */
    public static boolean isNetworkConnected() {
        try {
            //代表ping 3 次 超时时间为10秒
            Process p = Runtime.getRuntime().exec("ping -c 3 -w 10 www.baidu.com");//ping3次
            int status = p.waitFor();
            if (status == 0) {
                //代表成功
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public static String sendGet(String strUrl) {
        return sendGet(strUrl, "UTF-8", null);
    }

    public static String sendGet(String strUrl, String cookie) {
        return sendGet(strUrl, "UTF-8", cookie);
    }

    /**
     * @param strUrl      String 网址
     * @param charsetName String 设置网页编码
     * @param cookie      String 设置cookie
     * @return 返回html源码
     */
    public static String sendGet(String strUrl, String charsetName, String cookie) {
        BufferedReader bis = null;
        try {
            URL url = new URL(strUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");
            // 设置通用的请求属性

            if (cookie != null && !cookie.isEmpty())
                connection.setRequestProperty("Cookie", cookie);
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");

            //设置连接超时为5秒
            connection.setConnectTimeout(5000);

            if (connection.getResponseCode() == 200) {
                bis = new BufferedReader(
                        new InputStreamReader(connection.getInputStream(), charsetName));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bis.readLine()) != null) {
                    stringBuilder.append(line);
                }
                return stringBuilder.toString();
            } else
                return "";

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bis != null)
                    bis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return "";
    }

    /**
     * 获取cookie
     *
     * @param strUrl 网址
     * @return 返回cookie
     */
    public static String getCookie(String strUrl) {
        try {
            URL url = new URL(strUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");

            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);

            List<String> list = connection.getHeaderFields().get("Set-Cookie");
            if (list != null) {
                StringBuilder stringBuilder = new StringBuilder();
                for (String str : list) {
                    stringBuilder.append(str);
                }
                return stringBuilder.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String sendPost(String strUrl, String data) {
        return sendPost(strUrl, "", data);
    }

    /**
     * @param strUrl 网址
     * @param cookie cookie
     * @param data   post数据
     * @return 返回响应
     */
    public static String sendPost(String strUrl, String cookie, String data) {
        BufferedReader br = null;
        try {
            URL url = new URL(strUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");

            if (cookie != null && !cookie.isEmpty()) {
                connection.setRequestProperty("Cookie", cookie);
            }
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");


            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setConnectTimeout(5000);
            if (data != null && !data.isEmpty()) {
                OutputStream os = connection.getOutputStream();
                os.write(data.getBytes());
            }
            if (connection.getResponseCode() == 200) {
                br = new BufferedReader(new InputStreamReader(connection.getInputStream()));


                StringBuilder stringBuilder = new StringBuilder();
                int len;
                String line;
                while ((line = br.readLine()) != null) {
                    stringBuilder.append(line);
                }

                return stringBuilder.toString();

            } else
                return "";


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null)
                    br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public static boolean download(String strUrl, String path) {
        return download(strUrl, "", path);
    }

    /**
     * @param strUrl String 网址
     * @param cookie String cookie
     * @param path   String 文件路径
     * @return boolean 是否下载成功
     */
    public static boolean download(String strUrl, String cookie, String path) {
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            URL url = new URL(strUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // 设置通用的请求属性
            if (cookie != null && !cookie.isEmpty()) {
                connection.setRequestProperty("Cookie", cookie);
            }
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");

            connection.setRequestMethod("GET");

            connection.setConnectTimeout(5000);

            File file = new File(path);
            if (!file.getParentFile().exists()) {
                if (file.getParentFile().mkdirs())
                    return false;
            }
            if (connection.getResponseCode() == 200) {
                bis = new BufferedInputStream(connection.getInputStream());
                bos = new BufferedOutputStream(new FileOutputStream(file));
                int len;
                byte[] buffer = new byte[1024];
                while ((len = bis.read(buffer, 0, 1024)) != -1) {
                    bos.write(buffer, 0, len);
                }
                bos.flush();
                return true;

            } else
                return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (bis != null)
                    bis.close();
                if (bos != null)
                    bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
