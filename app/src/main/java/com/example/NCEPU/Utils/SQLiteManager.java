package com.example.NCEPU.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SQLiteManager {
    MySQLiteOpenHelper mySQLiteOpenHelper=null;
    private SQLiteDatabase sqLiteDatabase=null;
    Context context;
    public SQLiteManager(Context context,String db_name) {
        this.context=context;
        this.mySQLiteOpenHelper=new MySQLiteOpenHelper(context,db_name);  //创建了数据库

    }

    //打开数据库的读连接
    public SQLiteDatabase openReadLink() {
        if(sqLiteDatabase==null||!sqLiteDatabase.isOpen()) {
            sqLiteDatabase=mySQLiteOpenHelper.getReadableDatabase();
        }
        return sqLiteDatabase;
    }

    //打开数据库的写连接
    public SQLiteDatabase openWriteLink() {
        if(sqLiteDatabase==null||!sqLiteDatabase.isOpen()) {
            sqLiteDatabase=mySQLiteOpenHelper.getWritableDatabase();
        }
        return sqLiteDatabase;
    }

    public void closeLink() {
        if(sqLiteDatabase!=null&&sqLiteDatabase.isOpen()) {
            sqLiteDatabase.close();
            sqLiteDatabase=null;
        }
    }

    public void insert() {
        openWriteLink();
        for(int i=1;i<=28;i++) {
            ContentValues cv=new ContentValues();
            String suffix="";
            if(i<10) {
                suffix+="0";
                suffix+=String.valueOf(i);
            }else {
                suffix=String.valueOf(i);
            }
            String id="1201810802"+suffix;
            cv.put("_id",id);
            String password=id.substring(6);
            cv.put("password",password);
            sqLiteDatabase.insert(mySQLiteOpenHelper.TABLE_NAME,"",cv);
        }

        closeLink();
    }
    //根据id查询密码
    public String getPassword(String ID) {
        openReadLink();  //打开读操作
        String query_sql="SELECT password FROM users_info WHERE _id='"+ID+"'";
        Cursor cursor=sqLiteDatabase.rawQuery(query_sql,new String[]{});
        cursor.moveToNext();
        int index=cursor.getColumnIndex("password");
        String res=cursor.getString(index);
        closeLink();
        return res;
    }

    public boolean searchSingle(String text) {
        openReadLink();
        if(text==null) {
            return false;
        }
        String course=text.replaceAll(" ","");  //去掉空格
        try {
            //String sql="select count(*)  from sqlite_master where type='table' and name = '"+course+"';";
            String sql = "select count(*) from Sqlite_master  where type ='table' and name ='" + course + "' ";
            Cursor  cursor= sqLiteDatabase.rawQuery(sql, null);
            if(cursor.moveToNext()) {
                int count=cursor.getInt(0);
                if(count>0) {
                    cursor.close();
                    return true;
                }else {
                    cursor.close();
                }
            }
        }catch (Exception e) {

        }
        closeLink();
        return false;
    }

    public String findScore(String course,String id) {
        openReadLink();
        String not_found="不存在该成绩！！";
        //Cursor cursor = sqLiteDatabase.query(course, null, null, null, null, null, null);
        String query_sql="SELECT 成绩 FROM '"+course+"' WHERE 学号='"+id+"'";
        Cursor cursor=sqLiteDatabase.rawQuery(query_sql,new String[]{});
        if(cursor.getCount()==0) {
            return not_found;
        }
        cursor.moveToNext();
        int index=cursor.getColumnIndex("成绩");
        String res=cursor.getString(index);
        closeLink();
        return res;
    }


    public String converTo(String score) {
        if(score.equals("优秀")) {
            score="95";
        }else if(score.equals("良好")) {
            score="85";
        }else if(score.equals("中等")) {
            score="75";
        }else if(score.equals("及格")) {
            score="65";
        }else if(score.equals("不及格")){
            score="45";
        }
        return score;
    }


    public int findRank(String course,String score) {
        openReadLink();
        List<String> scores = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.query(course, null, null, null, null, null, null);
        if(cursor.getCount()==0) {
            return 0;
        }
        while (cursor.moveToNext()) {
            int index = cursor.getColumnIndex("成绩");
            String elem = cursor.getString(index);
            elem = converTo(elem);
            scores.add(elem);
        }
        cursor.close();
       Collections.sort(scores, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                if (o1.compareTo(o2) > 0) {
                    return -1;
                }
                if (o1.compareTo(o2) < 0) {
                    return 1;
                }
                return 0;
            }
        });
        int[] b = new int[scores.size()];
        for (int i = 0; i < scores.size(); i++) {
            b[i] = i + 1;
        }
        /*for (int i = 0; i < scores.size(); i++) {
            while (scores.get(i).equals(scores.get(i + 1))) {
                b[i + 1] = b[i];
                i++;
            }
        }*/

        closeLink();
        return b[scores.indexOf(score)];
        //return 0;

    }

}
