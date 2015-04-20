package com.ljn.aigame;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ljn.aigame.activity.SettingActivity;
import com.ljn.aigame.adapter.GameGridAdapter;
import com.ljn.aigame.db.LogDao;
import com.ljn.aigame.domain.Block;
import com.ljn.aigame.domain.ReciteLog;
import com.ljn.aigame.engine.BlockMatrxEngine;
import com.ljn.aigame.utils.GameUtils;

import java.util.ArrayList;
import java.util.List;


public class MyActivity extends ActionBarActivity implements View.OnClickListener {

    private static final int STUDY_MODE = 0;
    private static final int PLAY_MODE = 1;
    private GridView mGameGridView;
    private List<Block> mBlockList;
    private GameGridAdapter mGameAdapter;
    private TextView mXStepCountTextView;
    private TextView mOStepCountTextView;
    private BlockMatrxEngine mGameEngine;
    private Button mRestartButton;
    private Button mGoSettingButton;
    private boolean mIsGameFinished = false;
    private TextView mReciteLogTextView;
    private LogDao mLogDao;
    private RadioGroup mModeSwitchRadioGroup;
    private int mCurrentMode = STUDY_MODE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        mGameGridView = (GridView) findViewById(R.id.gv_game);
        mXStepCountTextView = (TextView) findViewById(R.id.tv_x_stepcount);
        mOStepCountTextView = (TextView) findViewById(R.id.tv_o_stepcount);
        mReciteLogTextView = (TextView) findViewById(R.id.tv_recitelog);
        mRestartButton = (Button) findViewById(R.id.bt_restart);
        mGoSettingButton = (Button) findViewById(R.id.bt_gosetting);
        mModeSwitchRadioGroup = (RadioGroup) findViewById(R.id.rg_mode_switch);
        mLogDao = new LogDao(MyActivity.this);
        reLoadData();
        setListener();
    }

    private boolean mIsUserPlay = true;
    private int mOStepCount = 0;
    private int mXStepCount = 0;

    private void setListener() {
        mGoSettingButton.setOnClickListener(this);
        mRestartButton.setOnClickListener(this);
        mModeSwitchRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int checkedRadioButtonId = radioGroup.getCheckedRadioButtonId();
                switch (checkedRadioButtonId) {
                    case R.id.rb_study:
                        mCurrentMode = STUDY_MODE;
                        break;
                    case R.id.rb_play:
                        mCurrentMode = PLAY_MODE;
                        break;
                }
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
        if (mCurrentMode == PLAY_MODE && !mIsGameFinished) {
            aiTurn();
        }
    }

    private void oneTurn(int pos) {
        if (mGameAdapter.getItem(pos).getBlockStats() != Block.BLOCK_BLANK || mIsGameFinished) {
            return;
        }

        if (mIsUserPlay) {
            mGameAdapter.getItem(pos).setBlockStats(Block.BLOCK_O);
            mOStepCount++;
            judgeWhoIsWin(pos, Block.BLOCK_O);
        } else {
            mGameAdapter.getItem(pos).setBlockStats(Block.BLOCK_X);
            mXStepCount++;
            judgeWhoIsWin(pos, Block.BLOCK_X);
        }

        mXStepCountTextView.setText("x step:" + mXStepCount);
        mOStepCountTextView.setText("o step:" + mOStepCount);
        mIsUserPlay = !mIsUserPlay;


        mGameAdapter.notifyDataSetChanged();
        return;
    }

    private void aiTurn() {
        int aiNextPos = GameUtils.generateNextAIPos(MyActivity.this, mBlockList);
        oneTurn(aiNextPos);
    }


    private void judgeWhoIsWin(int pos, int blockType) {
        int winner = mGameEngine.getWinner(pos, blockType);
        if (winner == BlockMatrxEngine.O_WIN) {
            Toast.makeText(getApplicationContext(), "O WIN", Toast.LENGTH_SHORT).show();
            recordGameLog(winner);
            mIsGameFinished = true;
        } else if (winner == BlockMatrxEngine.X_WIN) {
            Toast.makeText(getApplicationContext(), "X WIN", Toast.LENGTH_SHORT).show();
            recordGameLog(winner);
            mIsGameFinished = true;
        } else if (GameUtils.getBlankPosCount(mBlockList) == BlockMatrxEngine.NO_WINNER) {
            Toast.makeText(getApplicationContext(), "NO WINNER", Toast.LENGTH_SHORT).show();
            recordGameLog(winner);
            mIsGameFinished = true;
        }
    }

    private void recordGameLog(int winType) {
        String log = getCurrentLogString();
        mLogDao.addLog(new ReciteLog(BlockMatrxEngine.RECITE_COUNT, log, winType));
        BlockMatrxEngine.RECITE_COUNT++;
        mReciteLogTextView.setText(GameUtils.getReciteLogs(MyActivity.this));
    }

    private String getCurrentLogString() {
        return GameUtils.getLogStringByBlockList(mBlockList);
    }


    private void reLoadData() {
        mIsGameFinished = false;
        mIsUserPlay = true;
        mBlockList = new ArrayList<Block>();
        for (int i = 0; i < BlockMatrxEngine.ROW_COUNT * BlockMatrxEngine.ROW_COUNT; i++) {
            mBlockList.add(new Block());
        }
        mGameAdapter = new GameGridAdapter(MyActivity.this, mBlockList);
        mGameGridView.setAdapter(mGameAdapter);
        mXStepCount = mOStepCount = 0;
        mXStepCountTextView.setText("x step:" + mXStepCount);
        mOStepCountTextView.setText("o step:" + mOStepCount);
        mGameGridView.setNumColumns(BlockMatrxEngine.ROW_COUNT);
        mGameEngine = new BlockMatrxEngine(mBlockList);
        mReciteLogTextView.setText(GameUtils.getReciteLogs(MyActivity.this));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_gosetting:
                startActivityForResult(new Intent(MyActivity.this, SettingActivity.class), SettingActivity.SETTING_ACTIVITY_REQC);
                break;
            case R.id.bt_restart:
                reLoadData();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case SettingActivity.SETTING_ACTIVITY_REQC:
                if (data.getBooleanExtra("IS_SETTING_CONFIRM", false)) {
                    reLoadData();
                }
                break;
        }
    }
}
