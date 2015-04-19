package com.ljn.aigame.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ljn.aigame.R;
import com.ljn.aigame.domain.Block;

import java.util.List;

/**
 * Created by lijianan on 15-4-15.
 */
public class GameGridAdapter extends BaseAdapter {

    private List<Block> mBlockList;
    private Context mContext;

    public GameGridAdapter(Context mContext, List<Block> mBlockList) {
        this.mContext = mContext;
        this.mBlockList = mBlockList;
    }

    @Override
    public int getCount() {
        return mBlockList.size();
    }

    @Override
    public Block getItem(int i) {
        return mBlockList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View inflateView = View.inflate(mContext, R.layout.item_game_gridview, null);
        TextView mBlockTextView = (TextView) inflateView.findViewById(R.id.tv_item);
        int stats = mBlockList.get(i).getBlockStats();
        switch (stats) {
            case Block.BLOCK_BLANK:
                mBlockTextView.setText(" ");
                break;
            case Block.BLOCK_O:
                mBlockTextView.setText("O");
                break;
            case Block.BLOCK_X:
                mBlockTextView.setText("X");
                break;
        }

        return inflateView;
    }

    public List<Block> getBlockList() {
        return mBlockList;
    }
}
