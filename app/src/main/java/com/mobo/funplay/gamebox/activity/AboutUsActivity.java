package com.mobo.funplay.gamebox.activity;

import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.graphics.Paint;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobo.funplay.gamebox.R;
import com.mobo.funplay.gamebox.tracker.MyTracker;
import com.mobo.funplay.gamebox.utils.SystemUtils;

/**
 * @author : ydli
 * @time : 20-6-22 下午6:55
 * @description 我的信息界面
 */
public class AboutUsActivity extends BaseActivity {

    private TextView version;
    private TextView privacy;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_about_us;
    }

    @Override
    protected void onCreate() {
        track(MyTracker.click_aboutus);
    }

    @Override
    protected void initView() {
        version = findViewById(R.id.tv_version);
        privacy = findViewById(R.id.tv_privacy);
        findViewById(R.id.iv_back).setOnClickListener(view -> onBackPressed());
        ((TextView) findViewById(R.id.tv_title)).setText(R.string.about_us);
    }

    @Override
    protected void initData() {
        version.setText(getString(R.string.v).concat(SystemUtils.getVersionName(this)));

        //为显示隐私政策tv添加下划线
        privacy.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        privacy.setOnClickListener(View -> SystemUtils.startWebView(this, getString(R.string.privacy_url)));
    }

    public static void newStart(Fragment fragment) {
        Intent intent = new Intent(fragment.getContext(), AboutUsActivity.class);
        fragment.startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
