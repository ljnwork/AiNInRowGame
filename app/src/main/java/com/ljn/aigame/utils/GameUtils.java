package com.ljn.aigame.utils;

import android.content.Context;

import com.ljn.aigame.db.LogDao;
import com.ljn.aigame.domain.Block;
import com.ljn.aigame.domain.ReciteLog;
import com.ljn.aigame.engine.BlockMatrxEngine;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by lijianan on 15-4-18.
 */
public class GameUtils {

    public static String getReciteLogs(Context context) {
        List<ReciteLog> allLogs = new LogDao(context).findAllLogs();
        String showLog = "";
        for (ReciteLog log : allLogs) {
            showLog += "--------------------\n";
            showLog += "id=" + log.getReciteId() + " log=" + log.getReciteLog() + " winner=" + log.getWinType() + "\n";
        }
        return showLog;
    }

    /**
     * 根据给定的数组 取出最大值所在的下标
     *
     * @param array
     * @return
     */
    public static int getMaxIndexInArray(int[] array) {
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

    public static String getLogStringByBlockList(List<Block> blockList) {

        String log = "";
        for (Block block : blockList) {
            log += block.getBlockStats();
        }
        return log;
    }

    /**
     * 获取某一列log中 X或者O的总数
     *
     * @param type
     * @param rowIndex
     * @param reciteLogString
     * @return
     */
    public static int getRowXorOCount(int type, int rowIndex, List<String> reciteLogString) {
        int count = 0;
        for (String str : reciteLogString) {
            if (str.charAt(rowIndex) == type) {
                count++;
            }
        }
        return count;
    }

    /**
     * 取出数组中数值安降序排列的下标位置 的数组
     *
     * @param recordMtchTimes 每个位置匹配次数的记录数组
     * @return
     */
    public static int[] getIndexArrayOfMaxRankInArray(int[] recordMtchTimes) {
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
     * 生成一个可以走的 下一个随机点
     *
     * @return
     */
    private static int generateAvalibleRandomNextPosByBlockList(List<Block> blockList) {
        Random random = new Random();
        int randomPos = random.nextInt(blockList.size());
        if (isBlankPos(randomPos, blockList)) {
            return randomPos;
        } else {
            return generateAvalibleRandomNextPosByBlockList(blockList);
        }
    }


    /**
     * 判断当前点是否为空
     *
     * @param pos
     * @return
     */
    public static boolean isBlankPos(int pos, List<Block> blockList) {
        int blockType = Integer.parseInt(getLogStringByBlockList(blockList).substring(pos, pos + 1));
        if (blockType == Block.BLOCK_BLANK) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取当前为空的点的个数
     *
     * @return
     */
    public static int getBlankPosCount(List<Block> blockList) {
        int count = 0;
        String currentLogString = getLogStringByBlockList(blockList);
        for (int i = 0; i < currentLogString.length(); i++) {
            if (GameUtils.isBlankPos(i, blockList)) {
                count++;
            }
        }
        return count;
    }

    /**
     * 获取log字符串中 X或者O出现位置组成的list
     *
     * @param logString
     * @param winnerType
     * @return
     */
    private static List<Integer> getXorOIndexList(String logString, int winnerType) {
        List<Integer> indexList = new ArrayList<Integer>();
        int index = logString.indexOf(String.valueOf(winnerType));
        while (logString.indexOf(String.valueOf(winnerType), index) >= 0 && index >= 0) {
            indexList.add(index);
            index = logString.indexOf(String.valueOf(winnerType), index + 1);
        }
        return indexList;
    }

    /**
     * 删除数据库中的log
     */
    public static void deleteReciteLog(Context context) {
        new LogDao(context).deleteAllLog();
    }

    /**
     * 生成下一个AI可走的点
     *
     * @return
     */
    private static int gnerateAvalibleAINextPos(List<List<Integer>> logsMatchXList, List<Block> blockList) {
        int[] array = new int[blockList.size()];
        for (List<Integer> logMatchX : logsMatchXList) {
            for (Integer index : logMatchX) {
                array[index]++;
            }
        }
        //取道这一点坐标，如果这一点已经有棋子  则取下一个
        //取出下一步可走的按照概率从大到小排列大数组
        int aiNextPos = -1;
        int[] aiNextPosArray = GameUtils.getIndexArrayOfMaxRankInArray(array);
        for (int i = 0; i < aiNextPosArray.length; i++) {
            //如果下一步的位置为空白区域，则可走
            if (GameUtils.isBlankPos(aiNextPosArray[i], blockList)) {
                aiNextPos = aiNextPosArray[i];
                break;
            }
        }
        return aiNextPos;
    }

    /**
     * 生成下一个AI要走的点的位置  如果不能生成 返回－1
     *
     * @param context
     * @param blockList
     * @return
     */
    public static int generateNextAIPos(Context context, List<Block> blockList) {
        //根据当前的棋盘形式  去匹配数据库中可能性最大的结果
        List<ReciteLog> allXWinLogs = new LogDao(context).findAllAnalyseXWinLogs();
        List<Integer> currentXIndexList = getXorOIndexList(getLogStringByBlockList(blockList), Block.BLOCK_X);
        List<Integer> currentOIndexList = getXorOIndexList(getLogStringByBlockList(blockList), Block.BLOCK_O);

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
        int aiNextPos = -1;
        if (!logsMatch_x_1.isEmpty()) {
            aiNextPos = gnerateAvalibleAINextPos(logsMatch_x_1, blockList);

        } else if (!logsMatch_x_2.isEmpty()) {
            aiNextPos = gnerateAvalibleAINextPos(logsMatch_x_2, blockList);
        } else if (!logsMatch_x_3.isEmpty()) {
            aiNextPos = gnerateAvalibleAINextPos(logsMatch_x_3, blockList);
        }

        if (aiNextPos == -1 && GameUtils.getBlankPosCount(blockList) > 0) {
            aiNextPos = generateAvalibleRandomNextPosByBlockList(blockList);
        }

        return aiNextPos;
    }

    /**
     * 获取和当前获胜相反的ReciteLog
     *
     * @param log
     * @return
     */
    public static ReciteLog getReverseReciteLog(ReciteLog log) {
        char char_x = String.valueOf(Block.BLOCK_X).charAt(0);
        char char_o = String.valueOf(Block.BLOCK_O).charAt(0);
        if (log.getWinType() == BlockMatrxEngine.O_WIN) {
            log.setWinType(BlockMatrxEngine.X_WIN);
            String reciteLog = log.getReciteLog();
            reciteLog = reciteLog.replace(char_x, char_o);
            log.setReciteLog(reciteLog);
        } else if (log.getWinType() == BlockMatrxEngine.X_WIN) {
            log.setWinType(BlockMatrxEngine.O_WIN);
            String reciteLog = log.getReciteLog();
            reciteLog = reciteLog.replace(char_o, char_x);
            log.setReciteLog(reciteLog);
        }
        return log;
    }
}
