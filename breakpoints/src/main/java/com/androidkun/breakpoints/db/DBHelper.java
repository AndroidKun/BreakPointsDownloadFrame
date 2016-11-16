package com.androidkun.breakpoints.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by kun on 2016/11/10.
 */
public class DBHelper extends SQLiteOpenHelper{

    private static final String DB_NAME = "download.db";
    private static  DBHelper dbHelper = null;

    public static DBHelper getInstance(Context context){
        if(dbHelper==null) dbHelper = new DBHelper(context);
        return dbHelper;
    }

    private DBHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table thread_info (_id integer primary key autoincrement," +
                "thread_id integer,url text,start integer,end integer,finished integer)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
