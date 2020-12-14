package com.mobo.funplay.gamebox.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.mobo.funplay.gamebox.MyApp;
import com.mobo.funplay.gamebox.R;
import com.mobo.funplay.gamebox.utils.Utils;


public class StartActivity extends BaseActivity {
    private static final String TAG = "StartActivity";

    @Override
    protected void onCreate() {
        if (MyApp.getHadLaunched()) {
            MainActivity.newStart(StartActivity.this);
            finish();
            return;
        }
    }

    @Override
    protected void initView() {
        ((TextView) findViewById(R.id.tv_version)).
                setText(getResources().getText(R.string.app_name) + " V " + Utils.getVersionName(this));
    }

    @Override
    protected void initData() {
        getWindow().getDecorView().postDelayed(() -> {
            MainActivity.newStart(StartActivity.this);
            overridePendingTransition(0, R.anim.zoomout);
            finish();
        }, 2000);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_start;
    }

}
