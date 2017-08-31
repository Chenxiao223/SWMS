package com.dao;

public class ShowInfo {
    String Epc;
    String time;
    String power;
    String rssi;
    boolean UploadFlag;

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

}