package com.mobo.funplay.gamebox.push;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.mobo.funplay.gamebox.bean.push.LocalPushConfig;
import com.mobo.funplay.gamebox.bean.push.LocalPushMessage;
import com.mobo.funplay.gamebox.interfaces.CommonCallback;
import com.mobo.funplay.gamebox.interfaces.GrayStatus;
import com.mobo.funplay.gamebox.manager.RetrofitManager;
import com.mobo.funplay.gamebox.manager.SPManager;
import com.mobo.funplay.gamebox.utils.AssetsUtil;
import com.mobo.funplay.gamebox.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @Author: jzhou
 * @Description: 推送消息管理
 * @CreateDate: 20-6-17 下午5:30
 */
public class LocalPushManager {
    private static final long INTERVAL_MINUTES = 60 * 1000L;
    private static final int REPEAT_TASK_REQUEST_CODE = 1010;
    private static final LocalPushManager ourInstance = new LocalPushManager();

    public static LocalPushManager getInstance() {
        return ourInstance;
    }

    private LocalPushManager() {
    }

    public static void startPush(Context context) {
        if (!GrayStatus.push_H5) return;

        List<Integer> intervals = SPManager.getInstance().getLocalPushIntervalList();
        if (intervals == null || intervals.isEmpty()) return;

        // 第一次启动时注册多个一次性推送任务
        long currentTime = System.currentTimeMillis() / INTERVAL_MINUTES;
        long firstLaunchTime = SPManager.getInstance().getLong(PushConstants.FIRST_LAUNCH_TIME, -1);
        if (firstLaunchTime <= 0) {
            SPManager.getInstance().setLong(PushConstants.FIRST_LAUNCH_TIME, currentTime);
            for (int i = 0; i < intervals.size() - 1; i++) {
                startSinglePush(context, intervals.get(i), i);
            }
        }

        //if (hasNotStartRepeatPush(context)) {
        // 还没有注册过定时推送任务, 立即注册一个
        long intervalTime = intervals.get(intervals.size() - 1);
        if (firstLaunchTime > 0) {
            intervalTime = intervalTime - (currentTime - firstLaunchTime) % intervalTime;
        }
        int index = SPManager.getInstance().getInt(PushConstants.REPEAT_PUSH_MSG_INDEX, intervals.size() - 1);
        startRepeatPush(context, intervalTime, index);
        //}
    }

    public static void startSinglePush(Context context, long delayTime, int index) {
        startPush(context, false, delayTime, index, index);
    }

    public static void startRepeatPush(Context context, long intervalTime, int index) {
        startPush(context, true, intervalTime, index, REPEAT_TASK_REQUEST_CODE);
        SPManager.getInstance().setInt(PushConstants.REPEAT_PUSH_MSG_INDEX, index);
    }

    /**
     * @param context      上下文
     * @param delayTime    延时时间, 单位：分钟
     * @param index        推送消息的位置
     * @param requestCode  请求码，如果相同会覆盖上一个推送请求
     * @param isRepeatPush 是否是定时推送
     */
    private static void startPush(Context context, boolean isRepeatPush, long delayTime,
                                  int index, int requestCode) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (am == null) {
            return;
        }

        delayTime = delayTime * INTERVAL_MINUTES;
        PendingIntent pendingIntent = buildPendingIntent(context, isRepeatPush, index, requestCode);

        //版本适配
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {// 19及以上
            am.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + delayTime, pendingIntent);
        } else {
            am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + delayTime, pendingIntent);
        }
    }

    private static PendingIntent buildPendingIntent(Context context, boolean isRepeatPush,
                                                    int index, int requestCode) {
        Intent intent = new Intent(context.getApplicationContext(), LocalPushReceiver.class);
        intent.setAction(PushConstants.LOCAL_PUSH_BROADCAST_ACTION);
        intent.putExtra(PushConstants.LOCAL_PUSH_MSG_INDEX, index);
        intent.putExtra(PushConstants.IS_REPEAT_PUSH, isRepeatPush);
        return PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * 还没有启动定时推送任务
     *
     * @param context
     * @return
     */
    private static boolean hasNotStartRepeatPush(Context context) {
        Intent intent = new Intent(context.getApplicationContext(), LocalPushReceiver.class);
        intent.setAction(PushConstants.LOCAL_PUSH_BROADCAST_ACTION);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, REPEAT_TASK_REQUEST_CODE,
                intent, PendingIntent.FLAG_NO_CREATE);
        return pendingIntent == null;
    }

    public void requestConfig(Context context) {
        initLocalPushConfig(context);
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("packageName", context.getPackageName());
        queryParams.put("versionCode", Utils.getVersionCode(context));
        queryParams.put("message_type", PushConstants.H5_GAME);
        RetrofitManager.INSTANCE.getRequest().getLocalPushConfigRequest(queryParams)
                .enqueue(new CommonCallback<LocalPushConfig>() {
                    @Override
                    public void onResponse(LocalPushConfig response) {
                        if (response.getRet() == 0) {
                            saveLocalPushConfig(response.getData());
                        }
                        startPush(context);
                    }

                    @Override
                    public void onFailure(Throwable t, boolean isServerUnavailable) {
                        Log.d("onFailure", "onFailure: ");
                        startPush(context);
                    }
                });
    }

    private void initLocalPushConfig(Context context) {
        List<Integer> intervals = SPManager.getInstance().getLocalPushIntervalList();
        if (intervals == null || intervals.isEmpty()) {
            LocalPushConfig.DataBean config = AssetsUtil.getLocalPushList(context);
            saveLocalPushConfig(config);
        }
    }

    private void saveLocalPushConfig(LocalPushConfig.DataBean config) {
        if (config == null) return;

        List<Integer> intervals = config.getInterval();
        if (intervals != null && !intervals.isEmpty()) {
            SPManager.getInstance().putLocalPushIntervalList(intervals);
        }

        List<List<LocalPushMessage>> messages = config.getMessages();
        if (messages != null && !messages.isEmpty()) {
            List<LocalPushMessage> messageList = new ArrayList<>();
            for (int i = 0; i < messages.size(); i++) {
                messageList.addAll(messages.get(i));
            }
            SPManager.getInstance().putLocalPushMessageList(messageList);
        }
    }
}