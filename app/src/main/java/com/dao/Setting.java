package com.dao;

import java.io.Serializable;

public class Setting implements Serializable {

    private static final long serialVersionUID = -8835186325316098956L;
    private Integer ID;
    private String Company;
    private String Node;
    private String WorkName;
    private String Url;
    private String Telephone;
    private String Lat_Lon;
    private String NewData;

    private String Power;
    private String LogDeadline;
    private String CCTime;
    private String ScanStrategy;
    private String AgainDeadline;
    private String Vesion;
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

    public String getCompany() {
        return Company;
    }

    public void setCompany(String company) {
        Company = company;
    }

    public String getNode() {
        return Node;
    }

    public void setNode(String node) {
        Node = node;
    }

    public String getWorkName() {
        return WorkName;
    }

    public void setWorkName(String workName) {
        WorkName = workName;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }

    public String getTelephone() {
        return Telephone;
    }

    public void setTelephone(String telephone) {
        Telephone = telephone;
    }

    public String getLat_Lon() {
        return Lat_Lon;
    }

    public void setLat_Lon(String lat_Lon) {
        Lat_Lon = lat_Lon;
    }

    public String getNewData() {
        return NewData;
    }

    public void setNewData(String newData) {
        NewData = newData;
    }

    public String getPower() {
        return Power;
    }

    public void setPower(String power) {
        Power = power;
    }

    public String getLogDeadline() {
        return LogDeadline;
    }

    public void setLogDeadline(String logDeadline) {
        LogDeadline = logDeadline;
    }

    public String getCCTime() {
        return CCTime;
    }

    public void setCCTime(String CCTime) {
        this.CCTime = CCTime;
    }

    public String getScanStrategy() {
        return ScanStrategy;
    }

    public void setScanStrategy(String scanStrategy) {
        ScanStrategy = scanStrategy;
    }

    public String getAgainDeadline() {
        return AgainDeadline;
    }

    public void setAgainDeadline(String againDeadline) {
        AgainDeadline = againDeadline;
    }

    public String getVesion() {
        return Vesion;
    }

    public void setVesion(String vesion) {
        this.Vesion = vesion;
    }

    public String getScanInterval() {
        return ScanInterval;
    }

    public void setScanInterval(String scanInterval) {
        ScanInterval = scanInterval;
    }
}
