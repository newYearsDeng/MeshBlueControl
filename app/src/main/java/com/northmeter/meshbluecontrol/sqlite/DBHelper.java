package com.northmeter.meshbluecontrol.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by dyd on 2019/4/8.
 */

public class DBHelper extends SQLiteOpenHelper{


    public DBHelper(Context context) {
        // 创建数据库
        super(context, DBStrings.DBName, null, DBStrings.DBVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        System.out.println("创建表===============");
        // 设备基本信息 id
        db.execSQL("create table blueTooths(_id integer primary key autoincrement,type text,name text,tableNum text,Mac text,fatherNum text,fatherMac text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
