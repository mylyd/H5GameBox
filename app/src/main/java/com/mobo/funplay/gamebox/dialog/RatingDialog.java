package com.mobo.funplay.gamebox.dialog;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatRatingBar;

import com.mobo.funplay.gamebox.R;


/**
 * @author : ydli
 * @time : 19-10-22 下午2:25
 * @description 评分引导弹窗
 */
public class RatingDialog extends BaseDialog {
    private static final String TAG = "RatingDialog";
    private Context context;
    public TextView mTitle;
    private TextView mContent;
    public AppCompatRatingBar compatRatingBar;
    private OnRateListener onRateListener;

    public RatingDialog(Context context) {
        //弹框从底部弹出时会被虚拟按键挡住一部分，dialog_soft_input可以解决此问题
        super(context, R.style.dialog_soft_input);
        this.context = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_rating);
        setCanceledOnTouchOutside(true);
        Window window = this.getWindow();
        window.setGravity(Gravity.CENTER);
        window.setWindowAnimations(R.style.bottom_dialog_anim_style);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //设置dialog横向占比为全屏
        //window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        initView();
        setListener();
    }

    private void initView() {
        mTitle = findViewById(R.id.tv_title);
        mContent = findViewById(R.id.tv_content);
        compatRatingBar = findViewById(R.id.ratingBar);
        findViewById(R.id.dialog_back).setOnClickListener(view -> dismiss());
    }

    /**
     * =控件监听，用于实时通过显示具体对应的UI
     */
    private void setListener() {
        setOnDismissListener(dialog -> compatRatingBar.setRating(0.0f));
        compatRatingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            if (fromUser) {
                if (rating > 3.0f) {
                    if (onRateListener != null) {
                        onRateListener.onHeightLevel();
                    }
                } else {
                    if (onRateListener != null) {
                        onRateListener.onLowLevel();
                    }
                }
                dismiss();
            }
        });
    }

    public interface OnRateListener {
        void onHeightLevel();

        void onLowLevel();
    }

    public void setOnRateListener(OnRateListener listener) {
        this.onRateListener = listener;
    }
}
