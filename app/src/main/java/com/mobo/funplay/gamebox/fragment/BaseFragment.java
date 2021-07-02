package com.mobo.funplay.gamebox.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.mobo.funplay.gamebox.R;
import com.mobo.funplay.gamebox.activity.GamePreviewActivity;
import com.mobo.funplay.gamebox.activity.SearchActivity;
import com.mobo.funplay.gamebox.bean.GameItemBean;
import com.mobo.funplay.gamebox.constants.Constants;
import com.mobo.funplay.gamebox.interfaces.GrayStatus;

/**
 * Fragment基类
 */
abstract class BaseFragment extends Fragment {

    private TextView title;
    private ImageView search;
    private ImageView gameIcon;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(getLayoutId(), container, false);
    }

    /**
     * 获取布局id
     *
     * @return
     */
    protected abstract int getLayoutId();

    protected <T extends View> T findViewById(@IdRes int id) {
        try {
            return getView().findViewById(id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    protected void replaceFragment(Fragment fragment, @IdRes int res) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(res, fragment);
        transaction.commit();
    }


    /**
     * 游戏game按钮
     *
     * @param gameIcon
     */
    protected void forGameHomeUrlListener(@NonNull Fragment fragment, @NonNull ImageView gameIcon) {
        gameIcon.setOnClickListener(v -> {
            GameItemBean gameItemBean = new GameItemBean(
                    Constants.game_url,
                    null, " ", 1);
            GamePreviewActivity.newStart(fragment, gameItemBean, Constants.GAME_ICON);
        });

        //灰度控制时候展示这个按钮
        gameIcon.setVisibility(GrayStatus.game_icon_show ? View.VISIBLE : View.GONE);
    }

    /**
     * 搜索按钮控制
     *
     * @param fragment
     * @param search
     */
    protected void forSearchIntent(@NonNull Fragment fragment, @NonNull ImageView search) {
        search.setVisibility(GrayStatus.game_ads_value ? View.VISIBLE : View.GONE);

        search.setOnClickListener(view -> SearchActivity.newStart(fragment));
    }

    /**
     * 同一初始化action bar
     * TODO 因主页Game 滑动处理，将Action bar 单独在每个页面添加
     */
    protected void initActionBar() {
        title = findViewById(R.id.action_title);
        search = findViewById(R.id.action_search);
        gameIcon = findViewById(R.id.action_game_icon);
    }

    protected TextView getTitle() {
        if (title == null) {
            initActionBar();
            return title;
        }
        return title;
    }

    protected ImageView getSearch() {
        if (search == null) {
            initActionBar();
            return search;
        }
        return search;
    }

    protected ImageView getGameIcon() {
        if (gameIcon == null) {
            initActionBar();
            return gameIcon;
        }
        return gameIcon;
    }
}