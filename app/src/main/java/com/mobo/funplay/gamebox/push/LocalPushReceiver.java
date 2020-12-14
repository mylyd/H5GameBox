package com.mobo.funplay.gamebox.push;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.android.gms.common.util.CollectionUtils;
import com.mobo.funplay.gamebox.bean.push.LocalPushMessage;
import com.mobo.funplay.gamebox.interfaces.GrayStatus;
import com.mobo.funplay.gamebox.manager.SPManager;
import com.mobo.funplay.gamebox.tracker.FirebaseTracker;
import com.mobo.funplay.gamebox.tracker.MyTracker;
import com.mobo.funplay.gamebox.utils.SystemUtils;

import java.util.List;

/**
 * @Author: jzhou
 * @Description: 推送消息广播接收器
 * @CreateDate: 20-6-17 下午5:30
 */
public class LocalPushReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (PushConstants.LOCAL_PUSH_BROADCAST_ACTION.equals(intent.getAction())) {
            onPushReceive(context, intent);
        } else if (PushConstants.NOTIFICATION_BROADCAST_ACTION.equals(intent.getAction())) {
            onNotificationReceive(context, intent);
        }
    }

    /**
     * 执行推送任务发送的广播
     *
     * @param context
     * @param intent
     */
    private void onPushReceive(Context context, Intent intent) {
        if (!GrayStatus.push_H5) return;

        List<LocalPushMessage> messages = SPManager.getInstance().getLocalPushMessageList();
        if (CollectionUtils.isEmpty(messages)) return;

        boolean isRepeatPush = intent.getBooleanExtra(PushConstants.IS_REPEAT_PUSH, false);
        int index = intent.getIntExtra(PushConstants.LOCAL_PUSH_MSG_INDEX, 0);
        if (index >= messages.size()) {
            index = index % messages.size();
        }

        PushNotificationManager.showNotification(context, isRepeatPush, index, messages.get(index));
        if (index == 0 && !isRepeatPush) {
            FirebaseTracker.getInstance().track(MyTracker.PUSH_FRIST_SHOW);
        } else if (index == 1 && !isRepeatPush) {
            FirebaseTracker.getInstance().track(MyTracker.PUSH_SECOND_SHOW);
        } else {
            FirebaseTracker.getInstance().track(MyTracker.PUSH_THIRD_SHOW);
        }

        if (isRepeatPush) {
            List<Integer> intervals = SPManager.getInstance().getLocalPushIntervalList();
            if (CollectionUtils.isEmpty(intervals)) return;
            LocalPushManager.startRepeatPush(context, intervals.get(intervals.size() - 1), ++index);
        }
    }

    /**
     * 点击通知消息发送的广播
     *
     * @param context
     * @param intent
     */
    private void onNotificationReceive(Context context, Intent intent) {
        String link = intent.getStringExtra(PushConstants.LOCAL_PUSH_LINK);
        int index = intent.getIntExtra(PushConstants.LOCAL_PUSH_MSG_INDEX, 0);
        boolean isRepeatPush = intent.getBooleanExtra(PushConstants.IS_REPEAT_PUSH, false);

        SystemUtils.startWebView(context, link);

        if (index == 0 && !isRepeatPush) {
            FirebaseTracker.getInstance().track(MyTracker.PUSH_FRIST_CLICK);
        } else if (index == 1 && !isRepeatPush) {
            FirebaseTracker.getInstance().track(MyTracker.PUSH_SECOND_CLICK);
        } else {
            FirebaseTracker.getInstance().track(MyTracker.PUSH_THIRD_CLICK);
        }
    }
}
