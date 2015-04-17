package com.ljn.aigame;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.ljn.aigame.adapter.GameGridAdapter;
import com.ljn.aigame.db.LogDao;
import com.ljn.aigame.domain.Block;
import com.ljn.aigame.domain.ReciteLog;
import com.ljn.aigame.engine.BlockMatrxEngine;

import java.util.ArrayList;
import java.util.List;


public class MyActivity extends ActionBarActivity {

    private GridView mGameGridView;
    private List<Block> mBlockList;
    private GameGridAdapter mGameAdapter;
    private TextView mXStepCountTextView;
    private TextView mOStepCountTextView;
    private BlockMatrxEngine mGameEngine;
    private Button mRestartButton;
    private Button mClearlogButton;
    private int mReciteID = 0;
    private boolean mIsGameFinished = false;
    private TextView mReciteLogTextView;
    private SharedPreferences mReciteLogSp;
    private LogDao mLogDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        mGameGridView = (GridView) findViewById(R.id.gv_game);
        mXStepCountTextView = (TextView) findViewById(R.id.tv_x_stepcount);
        mOStepCountTextView = (TextView) findViewById(R.id.tv_o_stepcount);
        mReciteLogTextView = (TextView) findViewById(R.id.tv_recitelog);
        mRestartButton = (Button) findViewById(R.id.bt_restart);
        mClearlogButton = (Button) findViewById(R.id.bt_clear);
        mReciteLogSp = getSharedPreferences("reciteLog", MODE_PRIVATE | MODE_APPEND);
        clearReciteLog();
        initData();
        setListener();
    }

    private boolean mIsUserPlay = true;
    private int mOStepCount = 0;
    private int mXStepCount = 0;

    private void setListener() {
        mClearlogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearReciteLog();
            }
        });
        mRestartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initData();
            }
        });
        mGameGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                userTurn(i);
            }
        });
    }

    private void userTurn(int i) {
        oneTurn(i);
        aiTurn();
    }

    private void oneTurn(int i) {
        if (mGameAdapter.getItem(i).getBlockStats() != Block.BLOCK_BLANK || mIsGameFinished) {
            return;
        }

        if (mIsUserPlay) {
            mGameAdapter.getItem(i).setBlockStats(Block.BLOCK_O);
            mOStepCount++;
        } else {
            mGameAdapter.getItem(i).setBlockStats(Block.BLOCK_X);
            mXStepCount++;
        }

        mXStepCountTextView.setText("x step:" + mXStepCount);
        mOStepCountTextView.setText("o step:" + mOStepCount);
        mIsUserPlay = !mIsUserPlay;

        judgeWhoIsWin();

        mGameAdapter.notifyDataSetChanged();
        return;
    }

    private void clearReciteLog() {
//        SharedPreferences.Editor edit = mReciteLogSp.edit();
//        edit.clear();
//        edit.commit();
        mLogDao = mLogDao == null ? new LogDao(MyActivity.this) : mLogDao;
        mLogDao.deleteAllLog();
        mReciteID = 0;
        showReciteLog();
    }

    private void aiTurn() {
//        int size = mReciteLogSp.getAll().size() / 2;
//        for (int i = 0; i < size; i++) {
//            String key = mReciteLogSp.getInt()
//            getRowXorOCount(Block.BLOCK_O, i, mReciteLogSp.getString())
//        }
    }

    private void judgeWhoIsWin() {
        int winner = mGameEngine.getWinner();
        if (winner == BlockMatrxEngine.O_WIN) {
            Toast.makeText(getApplicationContext(), "O WIN", Toast.LENGTH_SHORT).show();
            recordGameLog(BlockMatrxEngine.O_WIN);
            mIsGameFinished = true;
        } else if (winner == BlockMatrxEngine.X_WIN) {
            Toast.makeText(getApplicationContext(), "X WIN", Toast.LENGTH_SHORT).show();
            recordGameLog(BlockMatrxEngine.X_WIN);
            mIsGameFinished = true;
        }
    }

    private void recordGameLog(int winType) {

//        SharedPreferences.Editor edit = mReciteLogSp.edit();

        String log = "";
        for (Block block : mBlockList) {
            log += block.getBlockStats();
        }

        mLogDao.addLog(new ReciteLog(mReciteID, log, winType));

//        edit.putString(mReciteID + "_log", log);
//        edit.putInt(mReciteID + "", winType);
//        edit.commit();

        mReciteID++;

        showReciteLog();
    }

    private void showReciteLog() {
//        Map<String, ?> allMap = mReciteLogSp.getAll();

//        for (String key : allMap.keySet()) {
//            showLog += "key= " + key + " value= " + allMap.get(key) + "\n";
//        }
        List<ReciteLog> allLogs = mLogDao.findAllLogs();
        String showLog = "";
        for (ReciteLog log : allLogs) {
            showLog += "--------------------\n";
            showLog += "id=" + log.getReciteId() + " log=" + log.getReciteLog() + " winner=" + log.getWinType() + "\n";
        }

        mReciteLogTextView.setText(showLog);
    }

    /**
     * 获取某一列log中 X或者O的总数
     *
     * @param type
     * @param rowIndex
     * @param reciteLogString
     * @return
     */
    private int getRowXorOCount(int type, int rowIndex, List<String> reciteLogString) {
        int count = 0;
        for (String str : reciteLogString) {
            if (str.charAt(rowIndex) == type) {
                count++;
            }
        }
        return count;
    }

    private void initData() {
        mIsGameFinished = false;
        mBlockList = new ArrayList<Block>();
        for (int i = 0; i < 9; i++) {
            mBlockList.add(new Block());
        }
        mGameAdapter = new GameGridAdapter(MyActivity.this, mBlockList);
        mGameGridView.setAdapter(mGameAdapter);
        mXStepCount = mOStepCount = 0;
        mXStepCountTextView.setText("x step:" + mXStepCount);
        mOStepCountTextView.setText("o step:" + mOStepCount);
        mGameEngine = new BlockMatrxEngine(mBlockList);
        mLogDao = new LogDao(MyActivity.this);
    }

}
