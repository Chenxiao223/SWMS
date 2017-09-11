package com.zjfd.chenxiao.DHL.Entity;

/**
 * Created by Administrator on 2017/9/8 0008.
 */
public class SearchQuery {
    /**
     * id : 1
     * dutyRfid : 548A1F2E0000
     * duty : A
     * cell : 1
     */

    private String id;
    private String dutyRfid;
    private String duty;
    private String cell;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDutyRfid() {
        return dutyRfid;
    }

    public void setDutyRfid(String dutyRfid) {
        this.dutyRfid = dutyRfid;
    }

    public String getDuty() {
        return duty;
    }

    public void setDuty(String duty) {
        this.duty = duty;
    }

    public String getCell() {
        return cell;
    }

    public void setCell(String cell) {
        this.cell = cell;
    }
}
