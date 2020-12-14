package com.mobo.funplay.gamebox.activity;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mobo.funplay.gamebox.R;
import com.mobo.funplay.gamebox.adapter.ViewPagerFragmentAdapter;
import com.mobo.funplay.gamebox.constants.Constants;
import com.mobo.funplay.gamebox.fragment.SearchLabelFragment;
import com.mobo.funplay.gamebox.fragment.SearchResultsFragment;
import com.mobo.funplay.gamebox.manager.SPManager;
import com.mobo.funplay.gamebox.tracker.MyTracker;
import com.mobo.funplay.gamebox.views.UnTouchScrollViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : ydli
 * @time : 20-6-22 下午6:55
 * @description 搜索界面
 */
public class SearchActivity extends BaseActivity implements View.OnClickListener {
    private EditText editText;
    private UnTouchScrollViewPager viewPager;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_search;
    }

    @Override
    protected void onCreate() {
        track(MyTracker.click_search);
    }

    @Override
    protected void initView() {
        findViewById(R.id.iv_back).setOnClickListener(this);
        editText = findViewById(R.id.tv_edit);
        findViewById(R.id.iv_close).setOnClickListener(this);
        findViewById(R.id.tv_enter).setOnClickListener(this);
        viewPager = findViewById(R.id.viewPager);
        setEditActionListener();
    }

    @Override
    protected void initData() {
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(SearchLabelFragment.newInstance());
        fragments.add(SearchResultsFragment.newInstance(true));
        viewPager.setAdapter(new ViewPagerFragmentAdapter(getSupportFragmentManager(), fragments));
        viewPager.setOffscreenPageLimit(fragments.size());
        viewPager.setCurrentItem(0);
    }

    /**
     * 监听软件盘，控制软键盘回车按钮完成搜索功能
     */
    private void setEditActionListener() {
        editText.setOnEditorActionListener((TextView v, int actionId, KeyEvent event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                // 在这里写搜索的操作,一般都是网络请求数据
                investigation();
                return true;
            }
            return false;
        });
    }

    /**
     * 隐藏软键盘
     *
     * @param view :一般为EditText
     */
    public void hideKeyboard(View view) {
        InputMethodManager manager = (InputMethodManager) view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void investigation() {
        //点击搜索的时候隐藏软键盘
        hideKeyboard(editText);

        if (editText == null) {
            return;
        }
        String edStr = editText.getText().toString();
        if (TextUtils.isEmpty(edStr)) {
            Toast.makeText(this, "Search content is empty, please enter search content", Toast.LENGTH_SHORT).show();
            return;
        }
        //请求搜索接口

        SPManager.addLabel(edStr);
        registerIntent(edStr);
        if (viewPager.getCurrentItem() == 0) {
            viewPager.setCurrentItem(1);
        }
    }

    public void setEditText(String string) {
        if (editText != null) {
            editText.setText(string);
        }
    }

    public void setCurrentItem(int currentItem) {
        if (viewPager != null) {
            viewPager.setCurrentItem(currentItem);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.iv_close:
                if (editText == null) {
                    return;
                }
                editText.setText(null);
                if (viewPager.getCurrentItem() == 1) {
                    viewPager.setCurrentItem(0);
                }
                break;
            case R.id.tv_enter:
                //搜索
                investigation();
                break;
        }
    }

    public static void newStart(Context context) {
        Intent intent = new Intent(context, SearchActivity.class);
        context.startActivity(intent);
    }

    public static void newStart(Fragment fragment) {
        newStart(fragment.getContext());
    }

    private void registerIntent(String str) {
        Intent intent = new Intent(Constants.SEARCH_RESULTS_BROADCAST_ACTION);
        intent.putExtra(Constants.SEARCH_RESULTS_BROADCAST_VALUE, str);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public void onBackPressed() {
        if (viewPager == null) {
            super.onBackPressed();
        }
        if (viewPager.getCurrentItem() == 1) {
            if (editText != null && editText.getText().length() != 0) {
                editText.setText(null);
            }
            viewPager.setCurrentItem(0);
        } else {
            super.onBackPressed();
        }
    }
}
