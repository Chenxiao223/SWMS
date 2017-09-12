package com.zjfd.chenxiao.DHL.Entity;

/**
 * Created by Administrator on 2017/9/12 0012.
 */
public class RfidInfo {
    private String rfid;
    private String rfidtime;
    private String borcode;
    private String bortime;
    private int flag;

    public String getRfid() {
        return rfid;
    }

    public void setRfid(String rfid) {
        this.rfid = rfid;
    }

    public String getRfidtime() {
        return rfidtime;
    }

    public void setRfidtime(String rfidtime) {
        this.rfidtime = rfidtime;
    }

    public String getBorcode() {
        return borcode;
    }

    public void setBorcode(String borcode) {
        this.borcode = borcode;
    }

    public String getBortime() {
        return bortime;
    }

    public void setBortime(String bortime) {
        this.bortime = bortime;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }
}
