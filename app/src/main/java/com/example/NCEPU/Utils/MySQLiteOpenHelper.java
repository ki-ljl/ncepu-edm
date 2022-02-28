package com.example.NCEPU.Utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MySQLiteOpenHelper extends SQLiteOpenHelper {

    private static final String DB_NAME="user.db";
    private static final int DB_VERSION=1;
    public static final String TABLE_NAME="users_info";

    public MySQLiteOpenHelper(Context context,String db_name) {
        super(context,db_name,null,DB_VERSION);
    }



    @Override
    //创建数据库，只在创建的时候用一次
    public void onCreate(SQLiteDatabase db) {
        //String drop_sql="DROP TABLE IF EXISTS "+TABLE_NAME+";";
       // db.execSQL(drop_sql);
        /*String create_sql="CREATE TABLE IF NOT EXISTS "+TABLE_NAME+" (" +
                "_id TEXT PRIMARY KEY  NOT NULL  UNIQUE," +
                "password TEXT NOT NULL" +
                ");";
        //Log.d("dscd",create_sql);
        db.execSQL(create_sql);*/
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
