package com.example.NCEPU.Student.TimeTable.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ShareUtils {
    public static final String SHARE_URL="http://62.234.73.209/share";
    public static boolean judgeURL(String url){
        Matcher matcher=Pattern.compile("^"+SHARE_URL.replaceAll("\\.","\\\\.")+"/[a-z0-9]{24}/?$").matcher(url);
        return matcher.matches();
    }
}
