package com.ljn.aigame;

import android.app.Application;
import android.content.SharedPreferences;

import com.ljn.aigame.engine.BlockMatrxEngine;

/**
 * Created by lijianan on 15-4-21.
 */
public class MainApplication extends Application {
    private SharedPreferences mSp;

    @Override
    public void onCreate() {
        super.onCreate();
        mSp = getSharedPreferences("setting", MODE_PRIVATE);
        BlockMatrxEngine.ROW_COUNT = mSp.getInt("ROW_COUNT", 14);
        BlockMatrxEngine.COUNT_OF_WIN_PIECE = mSp.getInt("COUNT_OF_WIN", 5);
    }
}
