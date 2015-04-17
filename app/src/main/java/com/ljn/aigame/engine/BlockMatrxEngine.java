package com.ljn.aigame.engine;

import android.util.Log;

import com.ljn.aigame.domain.Block;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lijianan on 15-4-15.
 */
public class BlockMatrxEngine {

    private List<Block> blockList;

    public BlockMatrxEngine(List<Block> blockList) {
        this.blockList = blockList;
    }

    /**
     * 获取某列的BlockList
     *
     * @param cosNum
     * @return
     */
    public List<Block> getCosList(int cosNum) {
        List<Block> cosList = new ArrayList<Block>();
        for (int i = 0; i < 3; i++) {
            cosList.add(blockList.get(cosNum + 3 * i));
        }
        return cosList;
    }

    /**
     * 获取某行的BlockList
     *
     * @param rowNum
     * @return
     */
    public List<Block> getRowList(int rowNum) {
        List<Block> rowList = new ArrayList<Block>();
        for (int i = 0; i < 3; i++) {
            rowList.add(blockList.get(i + rowNum * 3));
        }
        return rowList;
    }

    public int getLTtoRBXBlockCount() {
        return getRow_Or_Cos_and_X_or_YBlockCount(IS_LTRB, 0, Block.BLOCK_X);
    }

    public int getLTtoRBOBlockCount() {
        return getRow_Or_Cos_and_X_or_YBlockCount(IS_LTRB, 0, Block.BLOCK_O);
    }

    public int getRTtoLBXBlockCount() {
        return getRow_Or_Cos_and_X_or_YBlockCount(IS_RTLB, 0, Block.BLOCK_X);
    }

    public int getRTtoLBOBlockCount() {
        return getRow_Or_Cos_and_X_or_YBlockCount(IS_RTLB, 0, Block.BLOCK_O);
    }


    /**
     * 获取左上角到右下角对角线到BlockList
     *
     * @return
     */
    public List<Block> getLTtoRBList() {
        List<Block> ltTorbList = new ArrayList<Block>();
        ltTorbList.add(blockList.get(0));
        ltTorbList.add(blockList.get(4));
        ltTorbList.add(blockList.get(8));
        return ltTorbList;
    }

    /**
     * 获取右上角到左下角对角线到BlockList
     *
     * @return
     */
    public List<Block> getRTtoLBList() {
        List<Block> rtTolbList = new ArrayList<Block>();
        rtTolbList.add(blockList.get(2));
        rtTolbList.add(blockList.get(4));
        rtTolbList.add(blockList.get(6));
        return rtTolbList;
    }

    /**
     * 获取某行X的个数
     *
     * @param rowNum
     * @return
     */
    public int getRowXBlockCount(int rowNum) {
        return getRow_Or_Cos_and_X_or_YBlockCount(IS_ROW, rowNum, Block.BLOCK_X);
    }

    /**
     * 获取某行O的个数
     *
     * @param rowNum
     * @return
     */
    public int getRowOBlockCount(int rowNum) {
        return getRow_Or_Cos_and_X_or_YBlockCount(IS_ROW, rowNum, Block.BLOCK_O);
    }

    /**
     * 获取某列X的个数
     *
     * @param rowNum
     * @return
     */
    public int getCosXBlockCount(int cosNum) {
        return getRow_Or_Cos_and_X_or_YBlockCount(IS_COS, cosNum, Block.BLOCK_X);
    }

    /**
     * 获取某列O的个数
     *
     * @param rowNum
     * @return
     */
    public int getCosOBlockCount(int cosNum) {
        return getRow_Or_Cos_and_X_or_YBlockCount(IS_COS, cosNum, Block.BLOCK_O);
    }

    public static final int NO_WINNER = 0;
    public static final int O_WIN = 1;
    public static final int X_WIN = 2;


    /**
     * 获得谁赢谁输
     *
     * @return
     */
    public int getWinner() {

        for (int i = 0; i < 3; i++) {
            if (getRowXBlockCount(i) == 3 || getCosXBlockCount(i) == 3 || getLTtoRBXBlockCount() == 3 || getRTtoLBXBlockCount() == 3) {
                return X_WIN;
            } else if (getRowOBlockCount(i) == 3 || getCosOBlockCount(i) == 3 || getLTtoRBOBlockCount() == 3 || getRTtoLBOBlockCount() == 3) {
                return O_WIN;
            }
        }

        return NO_WINNER;
    }

    public static final int IS_ROW = 0;
    public static final int IS_COS = 1;
    public static final int IS_LTRB = 2;
    public static final int IS_RTLB = 3;

    private int getRow_Or_Cos_and_X_or_YBlockCount(int type, int num, int status) {
        List<Block> list;
        switch (type) {
            case IS_ROW:
                list = getRowList(num);
                break;
            case IS_COS:
                list = getCosList(num);
                break;
            case IS_LTRB:
                list = getLTtoRBList();
                break;
            case IS_RTLB:
                list = getRTtoLBList();
                break;
            default:
                list = new ArrayList<Block>();
                break;
        }
        int blockCount = 0;
        for (Block block : list) {
            if (block.getBlockStats() == status) {
                blockCount++;
            }
        }
        return blockCount;
    }
}
