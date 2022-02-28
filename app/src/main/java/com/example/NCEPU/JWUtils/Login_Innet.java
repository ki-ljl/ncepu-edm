package com.example.NCEPU.JWUtils;

import android.os.StrictMode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;


public class Login_Innet {

    private static String id;
    private static String password;

    public Login_Innet(String id, String password) {
        this.id = id;
        this.password = password;
    }

    public Map<String,String> in_net() throws Exception {
        StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Connection connection = Jsoup.connect("https://webvpn.ncepu.edu.cn/users/sign_in").ignoreContentType(true).timeout(6000);
        connection.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.61 Safari/537.36");   // 配置模拟浏览器
        Response res = connection.followRedirects(true).execute();                // 获取响应
        Document d = Jsoup.parse(res.body());       // 通过Jsoup将返回信息转换为Dom树
        List<Element> elements = d.select("form");  // 获取提交form表单，可以通过查看页面源码代码得知

        Map<String, String> datas = new HashMap<>();
        for (Element e : elements.get(0).getAllElements()) {
            // 设置用户名
            if (e.attr("name").equals("user[login]")) {
                e.attr("value", id);
            }
            // 设置用户密码
            if (e.attr("name").equals("user[password]")) {
                e.attr("value", password);
            }
            //设置其它
            if (e.attr("name").length() > 0) {
                datas.put(e.attr("name"), e.attr("value"));
            }
        }

        Connection connection2 = Jsoup.connect("https://webvpn.ncepu.edu.cn/users/sign_in");
        connection2.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.61 Safari/537.36");

        // 设置cookie和post上面的map数据
        Response response = connection2.ignoreContentType(true).followRedirects(true).method(Method.POST)
                .data(datas).cookies(res.cookies()).execute();
        String body = response.body();
        if(body.contains("密码错误")) {
            System.out.println("密码错误！");
            Map<String, String> maps = new HashMap<>();
            maps.put("pass", "no");
            return maps;
        }
        Map<String, String> map = response.cookies();
        return map;
    }
}

