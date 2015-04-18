package com.ljn.aigame.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ljn.aigame.domain.ReciteLog;
import com.ljn.aigame.engine.BlockMatrxEngine;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lijianan on 15-4-17.
 */
public class LogDao {

    private SQLiteOpenHelper helper;

    public LogDao(Context context) {
        helper = new LogDBHelper(context);
    }

    public List<ReciteLog> findAllLogs() {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor query = db.query(ReciteLog.TABLE_NAME, new String[]{ReciteLog.RECITE_ID, ReciteLog.RECITE_LOG, ReciteLog.WIN_TYPE}, null, null, null, null, null);
        List<ReciteLog> logs = new ArrayList<ReciteLog>();
        while (query.moveToNext()) {
            int reciteId = query.getInt(0);
            String reciteLog = query.getString(1);
            int winType = query.getInt(2);
            logs.add(new ReciteLog(reciteId, reciteLog, winType));
        }
        query.close();
        db.close();
        return logs;
    }

    public List<ReciteLog> findAllXWinLogs() {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor query = db.query(ReciteLog.TABLE_NAME, new String[]{ReciteLog.RECITE_ID, ReciteLog.RECITE_LOG, ReciteLog.WIN_TYPE}, "win_type = ?", new String[]{String.valueOf(BlockMatrxEngine.X_WIN)}, null, null, null);
        List<ReciteLog> logs = new ArrayList<ReciteLog>();
        while (query.moveToNext()) {
            int reciteId = query.getInt(0);
            String reciteLog = query.getString(1);
            int winType = query.getInt(2);
            logs.add(new ReciteLog(reciteId, reciteLog, winType));
        }
        query.close();
        db.close();
        return logs;
    }


    public long addLog(ReciteLog log) {
        SQLiteDatabase db = helper.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(ReciteLog.RECITE_ID, log.getReciteId());
        values.put(ReciteLog.RECITE_LOG, log.getReciteLog());
        values.put(ReciteLog.WIN_TYPE, log.getWinType());
        long insert = db.insert(ReciteLog.TABLE_NAME, null, values);
        db.close();
        return insert;
    }

    public int deleteAllLog() {
        SQLiteDatabase db = helper.getReadableDatabase();
        int delete = db.delete(ReciteLog.TABLE_NAME, ReciteLog.RECITE_ID + ">= 0", null);
        db.close();
        return delete;
    }
}
