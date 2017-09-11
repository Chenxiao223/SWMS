package com.zjfd.chenxiao.DHL.Entity;

import java.util.List;

/**
 * Created by Administrator on 2017/9/9 0009.
 */
public class PandianGetData {

    /**
     * id : 1
     * barcode : 548A1F2E0000
     * barcodeTime : 2017-08-31 14:47:29
     * importRfid : BG-01
     * importTime : 2017-09-01 14:47:15
     * dutyRfid : 548A1F2E0000
     * dutyTime : 2017-08-31 14:50:56
     * exportTime : 2017-08-31 14:51:04
     * isExport : 0
     */
    private String id;
    private String barcode;
    private String barcodeTime;
    private String importRfid;
    private String importTime;
    private String dutyRfid;
    private String dutyTime;
    private String exportTime;
    private String isExport;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getBarcodeTime() {
        return barcodeTime;
    }

    public void setBarcodeTime(String barcodeTime) {
        this.barcodeTime = barcodeTime;
    }

    public String getImportRfid() {
        return importRfid;
    }

    public void setImportRfid(String importRfid) {
        this.importRfid = importRfid;
    }

    public String getImportTime() {
        return importTime;
    }

    public void setImportTime(String importTime) {
        this.importTime = importTime;
    }

    public String getDutyRfid() {
        return dutyRfid;
    }

    public void setDutyRfid(String dutyRfid) {
        this.dutyRfid = dutyRfid;
    }

    public String getDutyTime() {
        return dutyTime;
    }

    public void setDutyTime(String dutyTime) {
        this.dutyTime = dutyTime;
    }

    public String getExportTime() {
        return exportTime;
    }

    public void setExportTime(String exportTime) {
        this.exportTime = exportTime;
    }

    public String getIsExport() {
        return isExport;
    }

    public void setIsExport(String isExport) {
        this.isExport = isExport;
    }
}
