package com.dao;

public class ShowInfo {
    String Epc;
    String time;
    String Epc2;
    String time2;
    String power;
    String rssi;
    String flag;
    boolean UploadFlag;

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public boolean getUploadFlag() {
        return UploadFlag;
    }

    public void setUploadFlag(boolean uploadFlag) {
        UploadFlag = uploadFlag;
    }

    public String getEpc() {
        return Epc;
    }

    public void setEpc(String Epc) {
        this.Epc = Epc;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPower() {
        return power;
    }

    public void setPower(String power) {
        this.power = power;
    }

    public String getRssi() {
        return rssi;
    }

    public void setRssi(String rssi) {
        this.rssi = rssi;
    }

    public String getEpc2() {
        return Epc2;
    }

    public void setEpc2(String epc2) {
        Epc2 = epc2;
    }

    public String getTime2() {
        return time2;
    }

    public void setTime2(String time2) {
        this.time2 = time2;
    }
}