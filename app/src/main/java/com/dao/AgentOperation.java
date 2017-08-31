package com.dao;

import android.content.Context;
import android.net.ConnectivityManager;


import com.zjfd.chenxiao.DHL.UI.HomeActivity;

import java.util.List;


public class AgentOperation {


    public static void setConfig(List<ShowInfo> showInfoList, Setting newset) {
    }


    public static UploadInfo convertUploadInfo(ShowInfo showinfo, Setting set) {
        UploadInfo uploadinfo = new UploadInfo();

        uploadinfo.setProductName("滚轴");
        uploadinfo.setNode(set.getNode());
        uploadinfo.setWorkName(set.getWorkName());
        uploadinfo.setLat("130.24");
        uploadinfo.setLon("31");
        uploadinfo.setFieldName("足迹");
        uploadinfo.setDBIP("http://180.167.66.99");
        uploadinfo.setPort("36751");
        uploadinfo.setExternalCode("86.1020.301");
        uploadinfo.setCompany(set.getCompany());
        uploadinfo.setFullCode("86.1020.301" + "/" + showinfo.getEpc());
        uploadinfo.setTime(showinfo.getTime());
//        uploadinfo.setData("" + "{\"节点\":\"" + HomeActivity.homeActivity.spinner_node.getSelectedItem().toString() + "\",\"经办人\":\"" + uploadinfo.getWorkName() + "\",\"时间\":\"" + uploadinfo.getTime() + "\",\"Lon\":\"" + uploadinfo.getLon() + "\",\"Lat\":\"" + uploadinfo.getLat() + "\",\"公司\":\"" + uploadinfo.getCompany() +
//                "\",\"备注\":\"" + set.getNewData() + "\"}");

        return uploadinfo;
    }


    public static boolean NetworkIsConnected(Context context) {

        ConnectivityManager con = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean wifi = con.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
        boolean internet = con.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
        if (wifi | internet) {
            return true;
        } else {
            return false;
        }

    }


}
