package com.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class DBOperation {

    public DBOpenHelper dbOpenHelper;
    public SQLiteDatabase db;

    public DBOperation(Context context) {
        dbOpenHelper = new DBOpenHelper(context, "goss.db", null, 2);
        db = dbOpenHelper.getWritableDatabase();
    }


    public Setting querySet() {

        Cursor cursor = db.query("Setting", null, null,
                null, null, null, null);

        Setting set1 = new Setting();
        if (!cursor.moveToFirst()) {
            cursor.close();
            return set1;
        }
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            Integer id = cursor.getInt(cursor.getColumnIndex("id"));
            String UserManage = cursor.getString(cursor.getColumnIndex("UserManage"));
            String Server = cursor.getString(cursor.getColumnIndex("Server"));
            String DataBase = cursor.getString(cursor.getColumnIndex("DataBase"));
            String Power = cursor.getString(cursor.getColumnIndex("Power"));
            String CCTime = cursor.getString(cursor.getColumnIndex("CCTime"));
            String AgainDeadline = cursor.getString(cursor.getColumnIndex("AgainDeadline"));
            String ScanInterval = cursor.getString(cursor.getColumnIndex("ScanInterval"));

            set1.setID(id);
            set1.setUserManage(UserManage);
            set1.setServer(Server);
            set1.setDataBase(DataBase);
            set1.setPower(Power);
            set1.setCCTime(CCTime);
            set1.setAgainDeadline(AgainDeadline);
            set1.setScanInterval(ScanInterval);

        }

        cursor.close();
        return set1;
    }

    public void insertSet(Setting set) {
        ContentValues values = new ContentValues();
        values.put("UserManage", set.getUserManage());
        values.put("Server", set.getServer());
        values.put("DataBase", set.getDataBase());
        values.put("Power", set.getPower());
        values.put("CCTime", set.getCCTime());
        values.put("AgainDeadline", set.getAgainDeadline());
        values.put("ScanInterval", set.getScanInterval());
        db.insert("Setting", null, values);
        values.clear();
    }

    public void deleteSet() {
        db.delete("Setting", null, null);
        dbOpenHelper.onUpgrade(db, 0, 0);
    }


}
