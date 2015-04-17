package com.ljn.aigame.domain;

/**
 * Created by lijianan on 15-4-17.
 */
public class ReciteLog {
    public static final String RECITE_ID = "recite_id";
    public static final String RECITE_LOG = "recite_log";
    public static final String WIN_TYPE = "win_type";
    public static final String TABLE_NAME = "RECITE_LOG";

    private int reciteId;
    private String reciteLog;
    private int winType;

    public int getWinType() {
        return winType;
    }

    public void setWinType(int winType) {
        this.winType = winType;
    }

    public ReciteLog(int reciteId, String reciteLog, int winType) {
        this.reciteId = reciteId;
        this.reciteLog = reciteLog;
        this.winType = winType;
    }

    public int getReciteId() {
        return reciteId;
    }

    public void setReciteId(int reciteId) {
        this.reciteId = reciteId;
    }

    public String getReciteLog() {
        return reciteLog;
    }

    public void setReciteLog(String reciteLog) {
        this.reciteLog = reciteLog;
    }
}
