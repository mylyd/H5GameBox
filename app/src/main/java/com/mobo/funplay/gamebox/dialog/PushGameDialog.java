package com.mobo.funplay.gamebox.dialog;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mobo.funplay.gamebox.R;
import com.mobo.funplay.gamebox.adapter.PushDialogItemAdapter;
import com.mobo.funplay.gamebox.manager.LocalGamePushManager;

/**
 * @author : ydli
 * @time : 19-12-9 上午9:14
 * @description 游戏推荐
 */
public class PushGameDialog extends BaseDialog {

    private RecyclerView recyclerView;
    private OnGamePush onGamePush;
    private Activity activity;

    public PushGameDialog(Activity activity) {
        super(activity, R.style.dialog_soft_input);
        this.activity = activity;
    }

    public interface OnGamePush {
        void setOnClickPush();
    }

    public void setStringPush(OnGamePush push) {
        this.onGamePush = push;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_push_game);

        Window window = this.getWindow();
        window.setGravity(Gravity.CENTER);
        window.setWindowAnimations(R.style.dialog_scale_style);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        findViewById(R.id.dialog_push_close).setOnClickListener(view -> dismiss());
        recyclerView = findViewById(R.id.push_recycler);

        PushDialogItemAdapter adapter = new PushDialogItemAdapter(activity, LocalGamePushManager.getLocalPush());
        recyclerView.setLayoutManager(new GridLayoutManager(activity, 2));
        recyclerView.setAdapter(adapter);
    }
}
