package com.ljn.aigame.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ljn.aigame.domain.ReciteLog;

/**
 * Created by lijianan on 15-4-17.
 */
public class LogDBHelper extends SQLiteOpenHelper {


    public LogDBHelper(Context context) {
        super(context, ReciteLog.TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " +
                        ReciteLog.TABLE_NAME + "(" +
                        ReciteLog.RECITE_ID + " integer primary key," +
                        ReciteLog.RECITE_LOG + " varchar," +
                        ReciteLog.WIN_TYPE + " integer" +
                        ")"
        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {

    }
}
