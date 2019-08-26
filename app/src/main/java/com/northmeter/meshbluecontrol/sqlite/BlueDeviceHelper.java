package com.northmeter.meshbluecontrol.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.northmeter.meshbluecontrol.bean.DBBlueToothBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dyd on 2019/4/8.
 */

public class BlueDeviceHelper {

    private DBHelper dbHelper;

    public BlueDeviceHelper(Context context) {
        super();
        this.dbHelper = new DBHelper(context);
    }

    public void insert(List<DBBlueToothBean> blueList){
        SQLiteDatabase db = null;
        try{
            db = dbHelper.getWritableDatabase();
            for(DBBlueToothBean blueItem:blueList){
                //实例化常量值
                ContentValues cValue = new ContentValues();
                cValue.put("type",blueItem.getType());
                cValue.put("name",blueItem.getName());
                cValue.put("tableNum",blueItem.getTableNum());
                cValue.put("Mac",blueItem.getMac());
                cValue.put("fatherNum",blueItem.getFatherNum());
                cValue.put("fatherMac",blueItem.getFatherMac());
                //调用insert()方法插入数据
                db.insert("blueTooths",null,cValue);
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            db.close();
        }
    }

    public boolean delete(String deleteName,String Mac) {
        boolean result = false;
        SQLiteDatabase db = null;
        try{
            db = dbHelper.getWritableDatabase();
            //删除条件
            String whereClause = deleteName+"=?";
            //删除条件参数
            String[] whereArgs = {Mac};
            //执行删除
            db.delete("blueTooths",whereClause,whereArgs);
            result = true;
        }catch (Exception e){
            result = false;
            e.printStackTrace();
        }finally {
            db.close();
        }
        return result;
    }

    private void update() {
        SQLiteDatabase db = null;
        try{
            db = dbHelper.getWritableDatabase();
            //实例化内容值
            ContentValues values = new ContentValues();
            //在values中添加内容
            values.put("sage", "23");
            //where 子句 "?"是占位符号，对应后面的"1",
            String whereClause="id=?";
            String [] whereArgs = {String.valueOf(1)};
            //参数1 是要更新的表名
            //参数2 是一个ContentValeus对象
            //参数3 是where子句
            db.update("blueTooths", values, whereClause, whereArgs);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            db.close();
        }
    }

    public List<DBBlueToothBean> query() {
        Cursor cursor = null;
        List<DBBlueToothBean> blueList = new ArrayList<>();
        SQLiteDatabase db = null;
        try{
            db = dbHelper.getWritableDatabase();
            //查询获得游标
            cursor = db.query ("blueTooths",null,null,null,null,null,null);
            //判断游标是否为空
            if(cursor.moveToFirst()){
                //遍历游标
                for(int i=0;i<cursor.getCount();i++){
                    cursor.move(i);
                    //获得ID
                    int id = cursor.getInt(0);
                    //获得type
                    String type=cursor.getString(1);
                    //获得name
                    String name=cursor.getString(2);
                    //获得tableNum
                    String tableNum =cursor.getString(3);
                    //获得Mac
                    String Mac=cursor.getString(4);
                    //fatherNum
                    String fatherNum=cursor.getString(5);
                    //获得fatherMac
                    String fatherMac=cursor.getString(6);
                    blueList.add(new DBBlueToothBean(type,name,tableNum,Mac,fatherNum,fatherMac,false,false));
                    //输出用户信息
                    System.out.println(id+":"+type+":"+name+":"+tableNum+":"+Mac+":"+fatherNum+":"+fatherMac);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }finally {
            cursor.close();
            db.close();
        }
        return blueList;
    }

    /**根据type类型*/
    public List<DBBlueToothBean> queryByCondit(String selectClause,String selectionArgs,String selectOut) {
        Cursor cursor = null;
        List<DBBlueToothBean> blueList = new ArrayList<>();
        SQLiteDatabase db = null;
        String whereClause = selectClause+"=?" + selectOut;
        String [] whereArgs = {selectionArgs};
        try{
            db = dbHelper.getWritableDatabase();
            //查询获得游标
            cursor = db.query ("blueTooths",null,whereClause,whereArgs,null,null,null);
            //判断游标是否为空
            if(cursor.getCount()>0){
                while (cursor.moveToNext()) {
                    int id = cursor.getInt(0);
                    //获得type
                    String type=cursor.getString(1);
                    //获得name
                    String name=cursor.getString(2);
                    //获得tableNum
                    String tableNum =cursor.getString(3);
                    //获得Mac
                    String Mac=cursor.getString(4);
                    //fatherNum
                    String fatherNum=cursor.getString(5);
                    //获得fatherMac
                    String fatherMac=cursor.getString(6);
                    blueList.add(new DBBlueToothBean(type,name,tableNum,Mac,fatherNum,fatherMac,false,false));
                    //输出用户信息
                    System.out.println(id+":"+type+":"+name+":"+tableNum+":"+Mac+":"+fatherNum+":"+fatherMac);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
            return blueList;
        }finally {
            cursor.close();
            db.close();
        }
        return blueList;
    }


    /**多条件类型查询*/
    public List<DBBlueToothBean> queryByConditMore(String selectClause,String[] selectionArgs) {
        Cursor cursor = null;
        List<DBBlueToothBean> blueList = new ArrayList<>();
        SQLiteDatabase db = null;
        String whereClause = selectClause;
        try{
            db = dbHelper.getWritableDatabase();
            //查询获得游标
            cursor = db.query ("blueTooths",null,whereClause,selectionArgs,null,null,null);
            //判断游标是否为空
            if(cursor.getCount()>0){
                while (cursor.moveToNext()) {
                    int id = cursor.getInt(0);
                    //获得type
                    String type=cursor.getString(1);
                    //获得name
                    String name=cursor.getString(2);
                    //获得tableNum
                    String tableNum =cursor.getString(3);
                    //获得Mac
                    String Mac=cursor.getString(4);
                    //fatherNum
                    String fatherNum=cursor.getString(5);
                    //获得fatherMac
                    String fatherMac=cursor.getString(6);
                    blueList.add(new DBBlueToothBean(type,name,tableNum,Mac,fatherNum,fatherMac,false,false));
                    //输出用户信息
                    System.out.println(id+":"+type+":"+name+":"+tableNum+":"+Mac+":"+fatherNum+":"+fatherMac);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
            return blueList;
        }finally {
            cursor.close();
            db.close();
        }
        return blueList;
    }
}
