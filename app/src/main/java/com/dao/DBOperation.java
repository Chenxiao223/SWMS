package com.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class DBOperation {

    public DBOpenHelper dbOpenHelper;
    public SQLiteDatabase db;

    public DBOperation(Context context) {
        dbOpenHelper = new DBOpenHelper(context,"goss.db",null,2);
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

            String Company = cursor.getString(cursor.getColumnIndex("Company"));

            String Node = cursor.getString(cursor.getColumnIndex("Node"));

            String WorkName = cursor.getString(cursor.getColumnIndex("WorkName"));

            String Url = cursor.getString(cursor.getColumnIndex("Url"));

            String Telephone = cursor.getString(cursor.getColumnIndex("Telephone"));

            String Lat_Lon = cursor.getString(cursor.getColumnIndex("Lat_Lon"));

            String NewData = cursor.getString(cursor.getColumnIndex("NewData"));

            String Power = cursor.getString(cursor.getColumnIndex("Power"));

            String LogDeadline = cursor.getString(cursor.getColumnIndex("LogDeadline"));

            String CCTime = cursor.getString(cursor.getColumnIndex("CCTime"));

            String ScanStrategy = cursor.getString(cursor.getColumnIndex("ScanStrategy"));

            String AgainDeadline = cursor.getString(cursor.getColumnIndex("AgainDeadline"));

            String Vesion = cursor.getString(cursor.getColumnIndex("Vesion"));

            String ScanInterval=cursor.getString(cursor.getColumnIndex("ScanInterval"));

            set1.setID(id);
            set1.setCompany(Company);
            set1.setNode(Node);
            set1.setWorkName(WorkName);
            set1.setUrl(Url);
            set1.setTelephone(Telephone);
            set1.setLat_Lon(Lat_Lon);
            set1.setNewData(NewData);

            set1.setPower(Power);
            set1.setLogDeadline(LogDeadline);
            set1.setCCTime(CCTime);
            set1.setScanStrategy(ScanStrategy);
            set1.setAgainDeadline(AgainDeadline);
            set1.setVesion(Vesion);
            set1.setScanInterval(ScanInterval);

        }

        cursor.close();
        return set1;
    }

    public void insertSet(Setting set) {
        ContentValues values = new ContentValues();
        values.put("Company", set.getCompany());
        values.put("Node", set.getNode());
        values.put("WorkName", set.getWorkName());
        values.put("Url", set.getUrl());
        values.put("Telephone", set.getTelephone());
        values.put("Lat_Lon", set.getLat_Lon());
        values.put("NewData", set.getNewData());

        values.put("Power", set.getPower());
        values.put("LogDeadline", set.getLogDeadline());
        values.put("CCTime", set.getCCTime());
        values.put("ScanStrategy", set.getScanStrategy());
        values.put("AgainDeadline", set.getAgainDeadline());
        values.put("Vesion", set.getVesion());
        values.put("ScanInterval", set.getScanInterval());
        db.insert("Setting", null, values);
        values.clear();
    }

    public void deleteSet() {
        db.delete("Setting", null, null);
        dbOpenHelper.onUpgrade(db,0,0);
    }


}
