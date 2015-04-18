package com.ljn.aigame;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
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
import java.util.Random;


public class MyActivity extends ActionBarActivity {

    private static final int STUDY_MODE = 0;
    private static final int PLAY_MODE = 1;
    private GridView mGameGridView;
    private List<Block> mBlockList;
    private GameGridAdapter mGameAdapter;
    private TextView mXStepCountTextView;
    private TextView mOStepCountTextView;
    private BlockMatrxEngine mGameEngine;
    private Button mRestartButton;
    private Button mClearlogButton;
    private Button mStudyOrPlayButton;
    private int mReciteID = 0;
    private boolean mIsGameFinished = false;
    private TextView mReciteLogTextView;
    private LogDao mLogDao;
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
        mClearlogButton = (Button) findViewById(R.id.bt_clear);
        mStudyOrPlayButton = (Button) findViewById(R.id.bt_study_or_play);
        clearReciteLog();
        initData();
        setListener();
    }

    private boolean mIsUserPlay = true;
    private int mOStepCount = 0;
    private int mXStepCount = 0;

    private void setListener() {
        mStudyOrPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchStudyOrPlay();
            }
        });
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

    private void switchStudyOrPlay() {
        mCurrentMode = mCurrentMode == STUDY_MODE ? PLAY_MODE : STUDY_MODE;
        switch (mCurrentMode) {
            case PLAY_MODE:
                mStudyOrPlayButton.setText("play");
                break;
            case STUDY_MODE:
                mStudyOrPlayButton.setText("study");
                break;
        }
    }

    private void userTurn(int i) {
        oneTurn(i);
        if (mCurrentMode == PLAY_MODE && !mIsGameFinished) {
            aiTurn();
        }
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
        mLogDao = mLogDao == null ? new LogDao(MyActivity.this) : mLogDao;
        mLogDao.deleteAllLog();
        mReciteID = 0;
        showReciteLog();
    }

    private void aiTurn() {
        //根据当前的棋盘形式  去匹配数据库中可能性最大的结果
        List<ReciteLog> allXWinLogs = mLogDao.findAllXWinLogs();
        List<Integer> currentXIndexList = getXorOIndexList(getCurrentLogString(), Block.BLOCK_X);
        List<Integer> currentOIndexList = getXorOIndexList(getCurrentLogString(), Block.BLOCK_O);
        String currentLogString = getCurrentLogString();

        //策略1:当棋盘棋子布局跟数据库中有匹配时，取匹配度最高的
        List<List<Integer>> logsMatch_x_1 = new ArrayList<List<Integer>>();
        //策略2:当棋盘棋子布局对手棋子与数据库有匹配时，取匹配度最高的
        List<List<Integer>> logsMatch_x_2 = new ArrayList<List<Integer>>();
        //策略3:当棋盘棋子布局与AI棋子数据库有匹配时，取匹配度最高的
        List<List<Integer>> logsMatch_x_3 = new ArrayList<List<Integer>>();
        //策略4:当没有匹配时，随机走一步


        for (ReciteLog winLog : allXWinLogs) {
            List<Integer> xPosInXWinLogList = getXorOIndexList(winLog.getReciteLog(), Block.BLOCK_X);
            List<Integer> oPosInXWinLogList = getXorOIndexList(winLog.getReciteLog(), Block.BLOCK_O);

            if (xPosInXWinLogList.containsAll(currentXIndexList) && oPosInXWinLogList.containsAll(currentOIndexList)) {
                logsMatch_x_1.add(xPosInXWinLogList);
            }

            if (xPosInXWinLogList.containsAll(currentOIndexList)) {
                logsMatch_x_2.add(xPosInXWinLogList);
            }

            if (oPosInXWinLogList.containsAll(currentXIndexList)) {
                logsMatch_x_3.add(xPosInXWinLogList);
            }

        }

        //如果找到了匹配的数组，遍历它，找出x出现次数最多的位置（这里的算法有待优化）
        int[] recordMtchTimes_x_1 = new int[mBlockList.size()];
        int[] recordMtchTimes_x_2 = new int[mBlockList.size()];
        int[] recordMtchTimes_x_3 = new int[mBlockList.size()];

        int aiNextPos = -1;
        if (!logsMatch_x_1.isEmpty()) {
            for (List<Integer> logMatchX : logsMatch_x_1) {
                for (Integer index : logMatchX) {
                    recordMtchTimes_x_1[index]++;
                }
            }
            //取道这一点坐标，如果这一点已经有棋子  则取下一个
            //取出下一步可走的按照概率从大到小排列大数组
            int[] aiNextPosArray = getIndexArrayOfMaxRankInArray(recordMtchTimes_x_1);
            for (int i = 0; i < aiNextPosArray.length; i++) {
                //如果下一步的位置为空白区域，则可走
                if (isBlankPos(aiNextPosArray[i])) {
                    aiNextPos = aiNextPosArray[i];
                    break;
                }
            }

        } else if (!logsMatch_x_2.isEmpty()) {
            for (List<Integer> logMatchX : logsMatch_x_2) {
                for (Integer index : logMatchX) {
                    recordMtchTimes_x_2[index]++;
                }
            }
            int[] aiNextPosArray = getIndexArrayOfMaxRankInArray(recordMtchTimes_x_2);
            for (int i = 0; i < aiNextPosArray.length; i++) {
                //如果下一步的位置为空白区域，则可走
                if (isBlankPos(aiNextPosArray[i])) {
                    aiNextPos = aiNextPosArray[i];
                    break;
                }
            }
        } else if (!logsMatch_x_3.isEmpty()) {
            for (List<Integer> logMatchX : logsMatch_x_3) {
                for (Integer index : logMatchX) {
                    recordMtchTimes_x_3[index]++;
                }
            }
            int[] aiNextPosArray = getIndexArrayOfMaxRankInArray(recordMtchTimes_x_3);
            for (int i = 0; i < aiNextPosArray.length; i++) {
                //如果下一步的位置为空白区域，则可走
                if (isBlankPos(aiNextPosArray[i])) {
                    aiNextPos = aiNextPosArray[i];
                    break;
                }
            }
        }

        if (aiNextPos == -1 && getBlankPosCount() > 0) {
            aiNextPos = generateAvalibleRandomNextPos();
        }

        oneTurn(aiNextPos);

        Log.i("test", currentXIndexList.size() + "");
    }

    /**
     * 获取当前为空的点的个数
     *
     * @return
     */
    private int getBlankPosCount() {
        int count = 0;
        String currentLogString = getCurrentLogString();
        for (int i = 0; i < currentLogString.length(); i++) {
            if (isBlankPos(i)) {
                count++;
            }
        }
        return count;
    }

    /**
     * 生成一个可以走的 下一个随机点
     *
     * @return
     */
    private int generateAvalibleRandomNextPos() {
        Random random = new Random();
        int randomPos = random.nextInt(mBlockList.size());
        if (isBlankPos(randomPos)) {
            return randomPos;
        } else {
            return generateAvalibleRandomNextPos();
        }
    }

    /**
     * 判断当前点是否为空
     *
     * @param pos
     * @return
     */
    private boolean isBlankPos(int pos) {

        int blockType = Integer.parseInt(getCurrentLogString().substring(pos, pos + 1));
        if (blockType == Block.BLOCK_BLANK) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 取出数组中数值安降序排列的下标位置 的数组
     *
     * @param recordMtchTimes 每个位置匹配次数的记录数组
     * @return
     */
    private int[] getIndexArrayOfMaxRankInArray(int[] recordMtchTimes) {
        int[] tempCloneArray = recordMtchTimes.clone();//创建一个记录数组的克隆
        int[] tempRankPosArray = new int[tempCloneArray.length];//用来记录按照出现次数降序排列的 匹配记录所在位置 的数组

        for (int i = 0; i < tempRankPosArray.length; i++) {
            //取出克隆数组中最大值所在下标
            int maxIndexInArray = getMaxIndexInArray(tempCloneArray);
            //将克隆数组此位置 数据置为0 避免下次匹配
            tempCloneArray[maxIndexInArray] = 0;

            tempRankPosArray[i] = maxIndexInArray;
        }
        return tempRankPosArray;
    }

    /**
     * 根据给定的数组 取出最大值所在的下标
     *
     * @param array
     * @return
     */
    private int getMaxIndexInArray(int[] array) {
        int maxPos = 0;
        int temp = 0;
        for (int i = 0; i < array.length; i++) {
            if (temp < array[i]) {
                temp = array[i];
                maxPos = i;
            }
        }
        return maxPos;
    }

    private List<Integer> getXorOIndexList(String logString, int winnerType) {
        List<Integer> indexList = new ArrayList<Integer>();
        int index = logString.indexOf(String.valueOf(winnerType));
        while (logString.indexOf(String.valueOf(winnerType), index) >= 0 && index >= 0) {
            indexList.add(index);
            index = logString.indexOf(String.valueOf(winnerType), index + 1);
        }
        return indexList;
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
        } else if (getBlankPosCount() == 0) {
            Toast.makeText(getApplicationContext(), "NO WINNER", Toast.LENGTH_SHORT).show();
            recordGameLog(BlockMatrxEngine.NO_WINNER);
            mIsGameFinished = true;
        }
    }

    private void recordGameLog(int winType) {
        String log = getCurrentLogString();
        mLogDao.addLog(new ReciteLog(mReciteID, log, winType));
        mReciteID++;
        showReciteLog();
    }

    private String getCurrentLogString() {
        String log = "";
        for (Block block : mBlockList) {
            log += block.getBlockStats();
        }
        return log;
    }

    private void showReciteLog() {
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
        mIsUserPlay = true;
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
