package com.ljn.aigame.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ljn.aigame.R;
import com.ljn.aigame.engine.BlockMatrxEngine;
import com.ljn.aigame.utils.GameUtils;

/**
 * Created by lijianan on 15-4-20.
 */
public class SettingActivity extends ActionBarActivity implements View.OnClickListener {

    private EditText mRowCountEditText;
    private EditText mWinSizeEditText;
    private Button mClearDataButton;
    private Button mConfirmButton;
    public static final int SETTING_ACTIVITY_REQC = 0;
    private boolean mIsSettingConfirm = false;
    private boolean mIsDataCleared = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        setTitle("设置");
        initView();
        setListener();
    }

    private void setListener() {
        mClearDataButton.setOnClickListener(this);
        mConfirmButton.setOnClickListener(this);
    }

    private void initView() {
        mRowCountEditText = (EditText) findViewById(R.id.et_row_count);
        mWinSizeEditText = (EditText) findViewById(R.id.et_win_count);
        mClearDataButton = (Button) findViewById(R.id.bt_cleardata);
        mConfirmButton = (Button) findViewById(R.id.bt_confirm);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishThisActivity();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_confirm:
                if (TextUtils.isEmpty(mRowCountEditText.getText().toString().trim()) && TextUtils.isEmpty(mWinSizeEditText.getText().toString().trim()) && !mIsDataCleared) {
                    finishThisActivity();
                    return;
                }
                confirmSetting();
                break;
            case R.id.bt_cleardata:
                clearReciteLog();
                mIsDataCleared = true;
                break;
        }
    }

    private void confirmSetting() {
        int rowCount = 0;
        if (!TextUtils.isEmpty(mRowCountEditText.getText().toString().trim())) {
            rowCount = Integer.parseInt(mRowCountEditText.getText().toString().trim());
        }

        int winSize = 0;
        if (!TextUtils.isEmpty(mWinSizeEditText.getText().toString().trim())) {
            winSize = Integer.parseInt(mWinSizeEditText.getText().toString());
        }
        SharedPreferences sp = getSharedPreferences("setting", MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        if (rowCount > 0) {
            edit.putInt("ROW_COUNT", rowCount);
            BlockMatrxEngine.ROW_COUNT = rowCount;
        }
        if (winSize > 0) {
            edit.putInt("COUNT_OF_WIN", winSize);
            BlockMatrxEngine.COUNT_OF_WIN_PIECE = winSize;
        }
        edit.commit();
        clearReciteLog();
        mIsSettingConfirm = true;
        finishThisActivity();
    }

    private void clearReciteLog() {
        GameUtils.deleteReciteLog(SettingActivity.this);
        BlockMatrxEngine.RECITE_COUNT = 0;
    }

    private void finishThisActivity() {
        Intent intent = new Intent();
        intent.putExtra("IS_SETTING_CONFIRM", mIsSettingConfirm);
        setResult(SETTING_ACTIVITY_REQC, intent);
        finish();
    }

}
