package com.dao;

import java.io.Serializable;

public class Setting implements Serializable {

    private static final long serialVersionUID = -8835186325316098956L;
    private Integer ID;
    private String UserManage;
    private String Server;
    private String DataBase;
    private String Power;
    private String CCTime;
    private String AgainDeadline;
    private String ScanInterval;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public String getUserManage() {
        return UserManage;
    }

    public void setUserManage(String userManage) {
        UserManage = userManage;
    }

    public String getServer() {
        return Server;
    }

    public void setServer(String server) {
        Server = server;
    }

    public String getDataBase() {
        return DataBase;
    }

    public void setDataBase(String dataBase) {
        DataBase = dataBase;
    }

    public String getPower() {
        return Power;
    }

    public void setPower(String power) {
        Power = power;
    }

    public String getCCTime() {
        return CCTime;
    }

    public void setCCTime(String CCTime) {
        this.CCTime = CCTime;
    }

    public String getAgainDeadline() {
        return AgainDeadline;
    }

    public void setAgainDeadline(String againDeadline) {
        AgainDeadline = againDeadline;
    }

    public String getScanInterval() {
        return ScanInterval;
    }

    public void setScanInterval(String scanInterval) {
        ScanInterval = scanInterval;
    }
}
