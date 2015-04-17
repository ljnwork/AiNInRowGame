package com.ljn.aigame.domain;

/**
 * Created by lijianan on 15-4-15.
 */
public class Block {
    public static final int BLOCK_BLANK = 0;
    public static final int BLOCK_O = 1;
    public static final int BLOCK_X = 2;

    private int mBlockStats = BLOCK_BLANK;

    public int getBlockStats() {
        return mBlockStats;
    }

    public void setBlockStats(int mBlockStats) {
        this.mBlockStats = mBlockStats;
    }

    public void initBlockStats(){
        mBlockStats = BLOCK_BLANK;
    }

}
