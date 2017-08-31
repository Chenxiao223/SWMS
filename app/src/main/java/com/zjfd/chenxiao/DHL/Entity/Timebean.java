package com.zjfd.chenxiao.DHL.Entity;

/**
 * Created by Administrator on 2017/5/10.
 */
public class Timebean {
    private  String CJtime;
    private  String SCtime;

    public Timebean() {
    }

    public Timebean(String SCtime, String CJtime) {
        this.SCtime = SCtime;
        this.CJtime = CJtime;
    }

    public String getCJtime() {
        return CJtime;
    }

    public void setCJtime(String CJtime) {
        this.CJtime = CJtime;
    }

    public String getSCtime() {
        return SCtime;
    }

    public void setSCtime(String SCtime) {
        this.SCtime = SCtime;
    }

    //    public Timebean(String time) {
//        this.time = time;
//    }
//
//    public Timebean() {
//    }
//
//    public String getTime() {
//        return time;
//    }
//
//    public void setTime(String time) {
//        this.time = time;
//    }
}
