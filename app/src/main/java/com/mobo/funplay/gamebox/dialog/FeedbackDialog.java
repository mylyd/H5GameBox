package com.mobo.funplay.gamebox.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.gson.JsonElement;
import com.mobo.funplay.gamebox.R;
import com.mobo.funplay.gamebox.interfaces.CommonCallback;
import com.mobo.funplay.gamebox.utils.Utils;

/**
 * @author : ydli
 * @time : 19-11-20 下午3:30
 * @description feedback 反馈信息dialog
 */
public class FeedbackDialog extends BaseDialog implements View.OnClickListener {

    private Context mContext;
    private EditText mEmail;
    private EditText mSuggestion;
    private TextView mSubmit;

    public FeedbackDialog(@NonNull Context context) {
        // R.style.dialog_soft_input防止键盘弹出时挡住部分弹框
        super(context, R.style.dialog_soft_input);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_feedback);
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
        mSubmit = findViewById(R.id.btn_submit);
        mSubmit.setOnClickListener(this);
        findViewById(R.id.dialog_back).setOnClickListener(this);
        mEmail = findViewById(R.id.et_email);
        mSuggestion = findViewById(R.id.et_suggestion);
    }

    /**
     * 重写关闭对话框方法，保证在每次选择评分较低后下次出现时依旧是 5 星状态
     */
    public void Dismiss() {
        if (mEmail.length() != 0 || mSuggestion.length() != 0) {
            mEmail.setText(null);
            mSuggestion.setText(null);
        }
        dismiss();
    }

    /**
     * 提交反馈信息
     *
     * @param email
     * @param suggestion
     */
    private void requestFeedBack(String email, String suggestion) {
        Utils.requestFeedBack(mContext, email, suggestion, new CommonCallback<JsonElement>() {
            @Override
            public void onResponse(JsonElement response) {
                Toast.makeText(mContext, "We have received your feedback , thanks !", Toast.LENGTH_SHORT).show();
                mSubmit.setEnabled(true);
                mEmail.setText(null);
                mSuggestion.setText(null);
                Dismiss();
            }

            @Override
            public void onFailure(Throwable t, boolean isServerUnavailable) {
                mSubmit.setEnabled(true);
                Toast.makeText(getContext(), "feedback failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Dismiss();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.dialog_back:
                Dismiss();
                break;
            case R.id.btn_submit:
                String email = mEmail.getText().toString();
                String suggestion = mSuggestion.getText().toString();
                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(suggestion)) {
                    mSubmit.setEnabled(false);
                    requestFeedBack(email, suggestion);
                } else {
                    Toast.makeText(mContext, "Email and feedback content are not empty", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
