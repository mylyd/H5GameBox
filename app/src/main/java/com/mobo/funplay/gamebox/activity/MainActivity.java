package com.mobo.funplay.gamebox.activity;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.mobo.funplay.gamebox.MyApp;
import com.mobo.funplay.gamebox.R;
import com.mobo.funplay.gamebox.adapter.ViewPagerFragmentAdapter;
import com.mobo.funplay.gamebox.bean.GameItemBean;
import com.mobo.funplay.gamebox.constants.Constants;
import com.mobo.funplay.gamebox.dialog.ConfirmDialog;
import com.mobo.funplay.gamebox.dialog.PushGameDialog;
import com.mobo.funplay.gamebox.fragment.CategoryFragment;
import com.mobo.funplay.gamebox.fragment.GameFragment;
import com.mobo.funplay.gamebox.fragment.HotFragment;
import com.mobo.funplay.gamebox.fragment.MineFragment;
import com.mobo.funplay.gamebox.interfaces.GrayStatus;
import com.mobo.funplay.gamebox.manager.SPManager;
import com.mobo.funplay.gamebox.tracker.MyTracker;
import com.mobo.funplay.gamebox.views.UnTouchScrollViewPager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity";

    private TabLayout tablayout;
    private UnTouchScrollViewPager viewPager;
    private ConfirmDialog confirmDialog;
    private PushGameDialog pushGameDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate() {
        MyApp.setHadLaunched(true);
    }

    @Override
    protected void initView() {
        tablayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        confirmDialog = new ConfirmDialog(this);
        confirmDialog.setOnConfirmListener(this::finish);

    }

    @Override
    protected void initData() {
        List<Integer> iconIds = buildTabIcons();
        List<Integer> titleIds = buildTabTitles(iconIds);
        List<Fragment> fragments = buildTabFragments(iconIds);
        viewPager.setAdapter(new ViewPagerFragmentAdapter(getSupportFragmentManager(), fragments));
        viewPager.setOffscreenPageLimit(fragments.size());
        tablayout.setupWithViewPager(viewPager);

        //自定义第一个tabItem,在文字上边添加一个icon
        for (int i = 0; i < titleIds.size(); i++) {
            TabLayout.Tab tab = tablayout.getTabAt(i);
            if (tab != null) {
                tab.setCustomView(getTabView(i, titleIds, iconIds));
            }

            //处理点击item
            if (tab.getCustomView() != null) {
                View tabView = (View) tab.getCustomView().getParent();
                tabView.setTag(i);
                tabView.setOnClickListener(this::onTabLayoutClick);
            }
        }

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        viewPager.setOnEvent(GrayStatus.main_tab_slide);

        pushGameDialog = new PushGameDialog(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {

        }
        return super.onTouchEvent(event);
    }

    private void pushGame() {
        long pastTime = SPManager.getInstance().getLong(Constants.PUSH_TIME_KEY, -1);
        if (pastTime < 0) {
            showPushDialog();
            return;
        }
        if ((System.currentTimeMillis() - pastTime) >= Constants.ONE_DAY_TIME) {
            showPushDialog();
        }
    }

    public void setViewPagerItem(int index) {
        if (viewPager != null) {
            Constants.INTENT_COLLECTION = false;
            viewPager.setCurrentItem(index);
        }
    }

    /**
     * 图标
     *
     * @return
     */
    private List<Integer> buildTabIcons() {
        TypedArray array = getResources().obtainTypedArray(
                GrayStatus.tab_hot ? R.array.tab_main_hot_icons : R.array.tab_main_icons);
        List<Integer> iconIds = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            int iconId = array.getResourceId(i, 0);
            iconIds.add(iconId);
        }
        array.recycle();
        return iconIds;
    }

    /**
     * 标题
     *
     * @param iconIds
     * @return
     */
    private List<Integer> buildTabTitles(List<Integer> iconIds) {
        List<Integer> titles = new ArrayList<>();
        for (int iconId : iconIds) {
            titles.add(getTabTitle(iconId));
        }
        return titles;
    }

    /**
     * fragment
     *
     * @param iconIds
     * @return
     */
    private List<Fragment> buildTabFragments(List<Integer> iconIds) {
        List<Fragment> fragments = new ArrayList<>();
        for (int iconId : iconIds) {
            fragments.add(getFragment(iconId));
        }
        return fragments;
    }

    private int getTabTitle(int iconId) {
        switch (iconId) {
            case R.drawable.tab_game_selector:
                return R.string.game;
            case R.drawable.tab_category_selector:
                return R.string.category;
            case R.drawable.tab_hot_selector:
                return R.string.hot;
            case R.drawable.tab_mine_selector:
                return R.string.mine;
            default:
                return R.string.nulls;
        }
    }

    private Fragment getFragment(int iconId) {
        switch (iconId) {
            case R.drawable.tab_game_selector:
                return GameFragment.newInstance();
            case R.drawable.tab_category_selector:
                return CategoryFragment.newInstance();
            case R.drawable.tab_hot_selector:
                return HotFragment.newInstance();
            case R.drawable.tab_mine_selector:
                return MineFragment.newInstance();
            default:
                return null;
        }
    }

    public View getTabView(int position, List<Integer> titles, List<Integer> icons) {
        View view = LayoutInflater.from(this).inflate(R.layout.item_main_tab, null);
        TextView tvTitle = view.findViewById(R.id.tv_title);
        tvTitle.setText(titles.get(position));
        ImageView ivIcon = view.findViewById(R.id.iv_icon);
        ivIcon.setImageResource(icons.get(position));
        return view;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (GrayStatus.game_recommend) {
            pushGame();
        }
        if (Constants.INTENT_COLLECTION) {
            setViewPagerItem(0);
        }
    }

    @Override
    public void onBackPressed() {
        if (confirmDialog != null && !confirmDialog.isShowing()) {
            confirmDialog.show();
            confirmDialog.setTitleText(getString(R.string.quit_app));
            confirmDialog.setContextText(getString(R.string.quit_app_content));
            confirmDialog.setConfirmText(getString(R.string.exit));
        }
    }

    @Override
    protected void onDestroy() {
        MyApp.setHadLaunched(false);
        super.onDestroy();
    }

    public static void newStart(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    public void onTabLayoutClick(View view) {
        int position = (int) view.getTag();
        switch (position) {
            case 0://game hot

                break;
            case 1://game category

                break;
            case 2://game mine

                break;
        }
    }

    private void showPushDialog() {
        if (pushGameDialog != null && !pushGameDialog.isShowing()) {
            pushGameDialog.show();
            SPManager.getInstance().setLong(Constants.PUSH_TIME_KEY, System.currentTimeMillis());
            track(MyTracker.show_suggest_img);
        }
    }
}
