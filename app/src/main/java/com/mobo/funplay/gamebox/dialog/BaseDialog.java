package com.mobo.funplay.gamebox.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

/**
 * @Description: Dialog基类  主要处理低端机型上顶部显示一条绿线的bug及其他参数配置
 * @Author: jzhou
 * @CreateDate: 19-10-23 上午10:39
 */
public class BaseDialog extends Dialog {

    public BaseDialog(@NonNull Fragment fragment, int themeResId) {
        super(fragment.getContext(), themeResId);
    }

    public BaseDialog(@NonNull Context context) {
        super(context);
    }

    public BaseDialog(Context context, int themeResId) {
        super(context, themeResId);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int dividerId = getContext().getResources().getIdentifier("android:id/titleDivider", null, null);
        View divider = findViewById(dividerId);
        if (divider != null) {
            divider.setBackgroundColor(Color.TRANSPARENT);
        }

        setCanceledOnTouchOutside(false);
        Window window = this.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }
}
