package com.mobo.funplay.gamebox.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.mobo.funplay.gamebox.R;
import com.mobo.funplay.gamebox.activity.AboutUsActivity;
import com.mobo.funplay.gamebox.activity.GamePreviewActivity;
import com.mobo.funplay.gamebox.activity.MyCollectionActivity;
import com.mobo.funplay.gamebox.bean.GameItemBean;
import com.mobo.funplay.gamebox.constants.Constants;
import com.mobo.funplay.gamebox.dialog.FeedbackDialog;
import com.mobo.funplay.gamebox.dialog.RatingDialog;
import com.mobo.funplay.gamebox.utils.GradeUtils;
import com.mobo.funplay.gamebox.utils.ShareUtil;

/**
 * @author : ydli
 * @time : 20-6-22 下午6:55
 * @description 我的界面
 */
public class MineFragment extends BaseFragment {
    private RatingDialog mRatingDialog;
    private FeedbackDialog mFeedbackDialog;

    public static MineFragment newInstance() {
        return new MineFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_mine;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initData();
        initListener();
    }

    private void initView() {
        initActionBar();
        getTitle().setText(R.string.mine);

        findViewById(R.id.ll_more_game).setOnClickListener(this::onClicked);
        findViewById(R.id.ll_collection).setOnClickListener(this::onClicked);
        findViewById(R.id.ll_five_stars).setOnClickListener(this::onClicked);
        findViewById(R.id.ll_share_it).setOnClickListener(this::onClicked);
        findViewById(R.id.ll_about_us).setOnClickListener(this::onClicked);
        findViewById(R.id.ll_share_facebook).setOnClickListener(this::onClicked);

        mRatingDialog = new RatingDialog(getContext());
        mFeedbackDialog = new FeedbackDialog(getContext());
    }

    private void initData() {

    }

    private void initListener() {
        mRatingDialog.setOnRateListener(new RatingDialog.OnRateListener() {
            @Override
            public void onHeightLevel() {
                GradeUtils.gotoGooglePlay(getActivity());
            }

            @Override
            public void onLowLevel() {
                if (mFeedbackDialog != null && !mFeedbackDialog.isShowing()) {
                    mFeedbackDialog.show();
                }
            }
        });
    }

    @SuppressLint("NonConstantResourceId")
    public void onClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_more_game:
                //More Game
                GameItemBean gameItemBean = new GameItemBean(
                        Constants.game_url,
                        null, " ", 1);
                GamePreviewActivity.newStart(this, gameItemBean, Constants.GAME_ICON);
                break;
            case R.id.ll_collection:
                //我的收藏
                MyCollectionActivity.newStart(this);
                break;
            case R.id.ll_five_stars:
                if (mRatingDialog != null && !mRatingDialog.isShowing()) {
                    mRatingDialog.show();
                }
                break;
            case R.id.ll_share_it:
                ShareUtil.share(getContext(), getString(R.string.share_content));
                break;
            case R.id.ll_about_us:
                AboutUsActivity.newStart(this);
                break;
            case R.id.ll_share_facebook:
                break;
        }
    }

}
