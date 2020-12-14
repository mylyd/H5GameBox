package com.mobo.funplay.gamebox.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatRatingBar;

import com.mobo.funplay.gamebox.R;


/**
 * @author : ydli
 * @time : 19-10-22 下午2:25
 * @description 确认弹窗 &&
 */
public class ConfirmDialog extends BaseDialog {
    private Context context;
    public TextView mTitle;
    private TextView mContent;
    private OnConfirmListener onListener;
    private TextView mConfirm;

    public ConfirmDialog(Context context) {
        //弹框从底部弹出时会被虚拟按键挡住一部分，dialog_soft_input可以解决此问题
        super(context, R.style.dialog_soft_input);
        this.context = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_confirm);
        setCanceledOnTouchOutside(true);
        Window window = this.getWindow();
        window.setGravity(Gravity.CENTER);
        window.setWindowAnimations(R.style.bottom_dialog_anim_style);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //设置dialog横向占比为全屏
        //window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        initView();
    }

    private void initView() {
        mTitle = findViewById(R.id.tv_title);
        mContent = findViewById(R.id.tv_content);
        findViewById(R.id.dialog_back).setOnClickListener(view -> dismiss());
        mConfirm = findViewById(R.id.tv_confirm);
        mConfirm.setOnClickListener(view -> {
            onListener.onConfirm();
            dismiss();
        });
    }

    public void setTitleText(String title) {
        if (mTitle != null) {
            mTitle.setText(title);
        }
    }

    public void setContextText(String string) {
        if (mContent != null) {
            mContent.setText(string);
        }
    }

    public void setConfirmText(String string) {
        if (mConfirm != null) {
            mConfirm.setText(string);
        }
    }

    public interface OnConfirmListener {

        void onConfirm();
    }

    public void setOnConfirmListener(OnConfirmListener listener) {
        this.onListener = listener;
    }
}
