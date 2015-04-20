package com.ljn.aigame.engine;

import com.ljn.aigame.domain.Block;

import java.util.ArrayList;
import java.util.List;

/**
 * 生成数据的引擎
 * Created by lijianan on 15-4-15.
 */
public class BlockMatrxEngine {
    public static final int NO_WINNER = 0;
    public static final int O_WIN = 1;
    public static final int X_WIN = 2;
    public static int ROW_COUNT = 14;//14 x 14 的棋盘
    public static int COUNT_OF_WIN_PIECE = 5;//赢 需要连成一条线的棋子个数

    public static int RECITE_COUNT = 0;
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
        for (int i = 0; i < ROW_COUNT; i++) {
            cosList.add(blockList.get(cosNum + ROW_COUNT * i));
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
        for (int i = 0; i < ROW_COUNT; i++) {
            rowList.add(blockList.get(i + rowNum * ROW_COUNT));
        }
        return rowList;
    }

    public int getLTtoRBXBlockCount(int i) {
        return getRow_Or_Cos_and_X_or_OBlockCount(IS_LTRB, i, Block.BLOCK_X);
    }

    public int getLTtoRBOBlockCount(int i) {
        return getRow_Or_Cos_and_X_or_OBlockCount(IS_LTRB, i, Block.BLOCK_O);
    }

    public int getRTtoLBXBlockCount(int i) {
        return getRow_Or_Cos_and_X_or_OBlockCount(IS_RTLB, i, Block.BLOCK_X);
    }

    public int getRTtoLBOBlockCount(int i) {
        return getRow_Or_Cos_and_X_or_OBlockCount(IS_RTLB, i, Block.BLOCK_O);
    }


    /**
     * 获取左上角到右下角对角线到BlockList
     *
     * @param rowIndex 以左下角为第0列 到右上角的列的编号
     * @return
     */
    public List<Block> getLTtoRBList(int rowIndex) {
        int x = rowIndex > ROW_COUNT - 1 ? rowIndex - (ROW_COUNT - 1) : 0;
        int y = rowIndex > ROW_COUNT - 1 ? 0 : (ROW_COUNT - 1) - rowIndex;
        List<Block> ltTorbList = new ArrayList<Block>();
        while (x < ROW_COUNT && y < ROW_COUNT) {
            ltTorbList.add(blockList.get(x + y * ROW_COUNT));
            x++;
            y++;
        }
        return ltTorbList;
    }

    /**
     * 获取右上角到左下角对角线到BlockList
     *
     * @param rowIndex 以左上角为第0列 到右下角的列的编号
     * @return
     */
    public List<Block> getRTtoLBList(int rowIndex) {
        int x = rowIndex > ROW_COUNT - 1 ? ROW_COUNT - 1 : rowIndex;
        int y = rowIndex > ROW_COUNT - 1 ? rowIndex - (ROW_COUNT - 1) : 0;
        List<Block> rtTolbList = new ArrayList<Block>();
        while (x >= 0 && y < ROW_COUNT) {
            rtTolbList.add(blockList.get(x + y * ROW_COUNT));
            x--;
            y++;
        }
        return rtTolbList;
    }

    /**
     * 获取某行X的个数
     *
     * @param rowNum
     * @return
     */
    public int getRowXBlockCount(int rowNum) {
        return getRow_Or_Cos_and_X_or_OBlockCount(IS_ROW, rowNum, Block.BLOCK_X);
    }

    /**
     * 获取某行O的个数
     *
     * @param rowNum
     * @return
     */
    public int getRowOBlockCount(int rowNum) {
        return getRow_Or_Cos_and_X_or_OBlockCount(IS_ROW, rowNum, Block.BLOCK_O);
    }

    /**
     * 获取某列X的个数
     *
     * @param rowNum
     * @return
     */
    public int getCosXBlockCount(int cosNum) {
        return getRow_Or_Cos_and_X_or_OBlockCount(IS_COS, cosNum, Block.BLOCK_X);
    }

    /**
     * 获取某列O的个数
     *
     * @param rowNum
     * @return
     */
    public int getCosOBlockCount(int cosNum) {
        return getRow_Or_Cos_and_X_or_OBlockCount(IS_COS, cosNum, Block.BLOCK_O);
    }


    /**
     * 获得谁赢谁输
     *
     * @return
     */
    public int getWinner(int pos, int blockType) {
        // 这里不要遍历每一列 只要便利当前列就可以
        // 取的pos对应的行号 列号 斜对角线的行列号
        int rowIndex = getRowIndexByPos(pos);
        int cosIndex = getCosIndexByPos(pos);
        int ltrbIndex = getLTtoRBListIndexByPos(pos);
        int rtlbIndex = getRTtoLBListIndexByPos(pos);
        if (getRowXBlockCount(rowIndex) == COUNT_OF_WIN_PIECE || getCosXBlockCount(cosIndex) == COUNT_OF_WIN_PIECE
                || getLTtoRBXBlockCount(ltrbIndex) == COUNT_OF_WIN_PIECE || getRTtoLBXBlockCount(rtlbIndex) == COUNT_OF_WIN_PIECE) {
            return X_WIN;
        } else if ((getRowOBlockCount(rowIndex) == COUNT_OF_WIN_PIECE || getCosOBlockCount(cosIndex) == COUNT_OF_WIN_PIECE
                || getLTtoRBXBlockCount(ltrbIndex) == COUNT_OF_WIN_PIECE || getRTtoLBOBlockCount(rtlbIndex) == COUNT_OF_WIN_PIECE)) {
            return O_WIN;
        }
        return NO_WINNER;
    }

    /**
     * 根据位置 获取按照右上角到左下角排布的行号
     *
     * @param pos
     * @return
     */
    private int getRTtoLBListIndexByPos(int pos) {
        int rowIndexByPos = getRowIndexByPos(pos);
        int cosIndexByPos = getCosIndexByPos(pos);
        int index = rowIndexByPos + cosIndexByPos;
        return index;
    }

    /**
     * 根据位置 获取按照左上角到右下角排布的行号
     *
     * @param pos
     * @return
     */
    private int getLTtoRBListIndexByPos(int pos) {
        int rowIndexByPos = getRowIndexByPos(pos);
        int cosIndexByPos = getCosIndexByPos(pos);
        int index = 0;
        if (rowIndexByPos < ROW_COUNT - 1) {
            index = rowIndexByPos - (ROW_COUNT - 1 - cosIndexByPos);
        } else {
            index = rowIndexByPos + (ROW_COUNT - 1 - cosIndexByPos);
        }
        return index;
    }

    /**
     * 根据位置 获取所在的行号
     *
     * @param pos
     * @return
     */
    private int getRowIndexByPos(int pos) {
        return pos / ROW_COUNT;
    }

    /**
     * 根据位置 获取所在的列号
     *
     * @param pos
     * @return
     */
    private int getCosIndexByPos(int pos) {
        return pos % ROW_COUNT;
    }

    public static final int IS_ROW = 0;
    public static final int IS_COS = 1;
    public static final int IS_LTRB = 2;
    public static final int IS_RTLB = 3;

    /**
     * 获取 某一行或者某一列或者左上到右下或者右上到坐下的 X或者O的总数
     *
     * @param type
     * @param num
     * @param status
     * @return
     */
    private int getRow_Or_Cos_and_X_or_OBlockCount(int type, int num, int status) {
        List<Block> list;
        switch (type) {
            case IS_ROW:
                list = getRowList(num);
                break;
            case IS_COS:
                list = getCosList(num);
                break;
            case IS_LTRB:
                list = getLTtoRBList(num);
                break;
            case IS_RTLB:
                list = getRTtoLBList(num);
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
