/**
 * Date   : 2021/2/3 21:00
 * Author : KI
 * File   : ConnectSZHD
 * Desc   : 数字华电
 * Motto  : Hungry And Humble
 */
package com.example.NCEPU.JWUtils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.StrictMode;

import com.example.NCEPU.MainActivity;
import com.example.NCEPU.Utils.ZC;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConnectSZHD {
    Map<String, String> cookies_innet;
    private Map<String,String> cookies = new HashMap<>();
    public static String id;
    String password;
    public boolean flag = false;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    public ConnectSZHD(Context context, String id, String password) throws Exception {
        sharedPreferences = context.getSharedPreferences("user_info", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        this.password = password;
        this.id = id;
        cookies_innet = MainActivity.connectJWGL.cookies_innet;
    }

    public boolean login() throws IOException {

        StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //先获取lt参数和execution
        String url = "https://ids.webvpn.ncepu.edu.cn/authserver/login?service=https%3A%2F%2Fmy-443.webvpn.ncepu.edu.cn%2Findex.portal";
        Connection connection = Jsoup.connect(url);
        connection.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.61 Safari/537.36");
        connection.header("Content-Type","application/x-www-form-urlencoded;charset=utf-8");
        Connection.Response response = connection.execute();
        cookies = response.cookies();
        Document document = Jsoup.parse(response.body());
        //System.out.println(document);
        String lt = document.getElementsByAttributeValue("name", "lt").attr("value");
        String execution = document.getElementsByAttributeValue("name", "execution").attr("value");
        String _eventId = document.getElementsByAttributeValue("name", "_eventId").attr("value");
        String rmShown = document.getElementsByAttributeValue("name", "rmShown").attr("value");
        System.out.println(lt);
        System.out.println(execution);
        System.out.println(_eventId);
        System.out.println(rmShown);
        //开始登录
        connection.data("username", id);
        connection.data("password", password);
        connection.data("lt", lt);
        connection.data("execution", execution);
        connection.data("_eventId", _eventId);
        connection.data("rmShown", rmShown);

        response = connection.cookies(cookies_innet).cookies(cookies).method(Connection.Method.POST).ignoreContentType(true).execute();
        cookies = response.cookies();
        if(response.body().contains("我要上网")) {
            System.out.println("内网密码正确");
            flag = true;
            return true;
        }else {
            System.out.println("内网密码错误");
            return false;
        }
    }

    public List<ZC> getZC() throws IOException {

        StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        List<ZC> data = new ArrayList<>();
        String url = "https://my-443.webvpn.ncepu.edu.cn/index.portal?.pn=p469_p882_p1801";
        Connection connection = Jsoup.connect(url);
        connection.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.61 Safari/537.36");
        connection.header("Content-Type","application/x-www-form-urlencoded;charset=utf-8");
        Connection.Response response = connection.cookies(cookies_innet).cookies(cookies).ignoreContentType(true).execute();
        //System.out.println(response.body());
        Document doc = Jsoup.parse(response.body());
        //System.out.println(doc);

        String zc_p = doc.getElementsByAttributeValue("id", "one3").attr("onclick");
        zc_p = zc_p.substring(19, zc_p.length() - 1);  //.p
        System.out.println(zc_p);
        url = "https://my-443.webvpn.ncepu.edu.cn/index.portal?.p=" + zc_p;
        connection = Jsoup.connect(url);
        connection.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.61 Safari/537.36");
        connection.header("Content-Type","application/x-www-form-urlencoded;charset=utf-8");
        response = connection.cookies(cookies_innet).cookies(cookies).ignoreContentType(true).execute();
        doc = Jsoup.parse(response.body());
        List<Element> elementList =  doc.getElementsByAttributeValue("id", "jj");
        ArrayList<String> final_list_p = new ArrayList<>();
        for(Element e : elementList) {
            final_list_p.add(e.attr("onclick").substring(19, e.attr("onclick").length() - 1));
        }
        ArrayList<String> temp = new ArrayList<>();
        for(String x : final_list_p) {
            url = "https://my-443.webvpn.ncepu.edu.cn/index.portal?.p=" + x;
            connection = Jsoup.connect(url);
            connection.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.61 Safari/537.36");
            connection.header("Content-Type","application/x-www-form-urlencoded;charset=utf-8");
            response = connection.cookies(cookies_innet).cookies(cookies).ignoreContentType(true).execute();
            doc = Jsoup.parse(response.body());
            List<Element> list = doc.getElementsByAttributeValue("class", "portlet-form-table");
            Element e = list.get(0);   //成绩数据
            Elements elements = e.select("tr");
            Elements xx = elements.get(2).select("td");;
            temp.add(xx.get(0).text());
            for(int k = 3; k <= 6; k++) {
                Element ex = elements.get(k);
                Elements td = ex.select("td");
                for(int i = 0; i < td.size(); i++) {  //6-12
                    temp.add(td.get(i).text());
                }
            }
        }
        int size = temp.size() / 8;
        for(int i = 0; i < size; i++) {
            ZC zc = new ZC();
            zc.setYear(temp.get(i * 8 + 0));
            zc.setSx(temp.get(i * 8 + 1));
            zc.setGrade(temp.get(i * 8 + 2));
            zc.setPe(temp.get(i * 8 + 3));
            zc.setFj(temp.get(i * 8 + 4));
            zc.setTotal(temp.get(i * 8 + 5));
            zc.setClass_sort(temp.get(i * 8 + 6));
            zc.setMajor_sort(temp.get(i * 8 + 7));
            data.add(zc);
        }
        return data;
    }
}

