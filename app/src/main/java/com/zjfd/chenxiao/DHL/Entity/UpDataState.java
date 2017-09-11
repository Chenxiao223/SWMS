package com.zjfd.chenxiao.DHL.Entity;

/**
 * Created by Administrator on 2017/9/8 0008.
 */
public class UpDataState {
    public String fieldCount;
    public String affectedRows;
    public String insertId;
    public String serverStatus;
    public String warningCount;
    public String message;
    public String protocol41;
    public String changedRows;

    @Override
    public String toString() {
        return "UpDataState{" +
                "fieldCount='" + fieldCount + '\'' +
                ", affectedRows='" + affectedRows + '\'' +
                ", insertId='" + insertId + '\'' +
                ", serverStatus='" + serverStatus + '\'' +
                ", warningCount='" + warningCount + '\'' +
                ", message='" + message + '\'' +
                ", protocol41='" + protocol41 + '\'' +
                ", changedRows='" + changedRows + '\'' +
                '}';
    }

    public String getFieldCount() {
        return fieldCount;
    }

    public void setFieldCount(String fieldCount) {
        this.fieldCount = fieldCount;
    }

    public String getAffectedRows() {
        return affectedRows;
    }

    public void setAffectedRows(String affectedRows) {
        this.affectedRows = affectedRows;
    }

    public String getInsertId() {
        return insertId;
    }

    public void setInsertId(String insertId) {
        this.insertId = insertId;
    }

    public String getServerStatus() {
        return serverStatus;
    }

    public void setServerStatus(String serverStatus) {
        this.serverStatus = serverStatus;
    }

    public String getWarningCount() {
        return warningCount;
    }

    public void setWarningCount(String warningCount) {
        this.warningCount = warningCount;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getProtocol41() {
        return protocol41;
    }

    public void setProtocol41(String protocol41) {
        this.protocol41 = protocol41;
    }

    public String getChangedRows() {
        return changedRows;
    }

    public void setChangedRows(String changedRows) {
        this.changedRows = changedRows;
    }
}
