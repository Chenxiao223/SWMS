package com.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper {
    private Context mContext;

    public static final String SetSql = "create table Setting (id integer PRIMARY KEY autoincrement not null,Company varchar(50) not null,Node varchar(50) not null,WorkName varchar(50) not null," +
            "Url varchar(500) not null,Telephone varchar(500) not null," +
            "Lat_Lon varchar(255) not null,NewData varchar(50) not null,Power varchar(50) not null," +
            "LogDeadline varchar(50) not null,CCTime varchar(500),ScanStrategy varchar(500),AgainDeadline varchar(500),Vesion varchar(500),ScanInterval varchar(500))";

    public static final String delete_setsql = "drop table if exists Setting";

    public static final String DATABASENAME = "goss.db";

    public static final int DATABASEVERSION = 1;

    public DBOpenHelper(Context context,String name,SQLiteDatabase.CursorFactory factory,int version) {
        super(context, name, factory, version);
        // TODO Auto-generated constructor stub
        mContext=context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(SetSql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
        // TODO Auto-generated method stub
        db.execSQL(delete_setsql);
        onCreate(db);
    }


}
