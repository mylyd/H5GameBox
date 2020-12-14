package com.mobo.funplay.gamebox.push;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.mobo.funplay.gamebox.R;
import com.mobo.funplay.gamebox.bean.push.LocalPushMessage;

/**
 * @Author: jzhou
 * @Description: 推送通知数据处理
 * @CreateDate: 20-6-17 下午5:30
 */
public class PushNotificationManager {
    private static final String TAG = PushNotificationManager.class.getSimpleName();
    /**
     * 通知小图标
     *
     * android5.0 之后通知栏图标都修改了，小图标不能含有RGB图层，
     * 也就是说图片不能带颜色，只能用白色的图片,还只能是.png格式，否则显示的就成白色（灰色）方格了
     */
    private static final int mNotificationIcon = R.drawable.ic_notification;

    public static void showNotification(Context context, boolean isRepeatPush, int index, LocalPushMessage message) {
        if (message == null) return;
        if (!TextUtils.isEmpty(message.getIcon_url())) {
            loadIconBitmap(context, isRepeatPush, index, message);
        } else if (!TextUtils.isEmpty(message.getPreview_url())) {
            loadBannerBitmap(context, isRepeatPush, index, message);
        } else {
            showNotification(context, isRepeatPush, index, message, null);
        }
    }

    private static void loadIconBitmap(Context context, boolean isRepeatPush, int index,
                                       LocalPushMessage message) {
        Glide.with(context).asBitmap().load(message.getIcon_url()).into(new CustomTarget<Bitmap>() {

            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                Log.d(TAG, "icon download finish");
                message.setIconBitmap(resource);
                if (TextUtils.isEmpty(message.getPreview_url())) {
                    showSystemNotification(context, isRepeatPush, index, message);
                } else {
                    loadBannerBitmap(context, isRepeatPush, index, message);
                }
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {
            }

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                Log.d(TAG, "icon download failed");
                if (TextUtils.isEmpty(message.getPreview_url())) {
                    showSystemNotification(context, isRepeatPush, index, message);
                } else {
                    loadBannerBitmap(context, isRepeatPush, index, message);
                }
            }
        });
    }

    private static void loadBannerBitmap(Context context, boolean isRepeatPush, int index,
                                         LocalPushMessage message) {
        Glide.with(context).asBitmap().load(message.getPreview_url()).into(new CustomTarget<Bitmap>() {

            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                Log.d(TAG, "banner download finish");
                message.setBannerBitmap(resource);
                showSystemNotification(context, isRepeatPush, index, message);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {
            }

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                Log.d(TAG, "banner download failed");
                showSystemNotification(context, isRepeatPush, index, message);
            }
        });
    }

    private static void showSystemNotification(Context context, boolean isRepeatPush, int index,
                                               LocalPushMessage message) {
        if (message.getIconBitmap() == null) {
            Bitmap iconBitmap = BitmapFactory.decodeResource(context.getResources(), mNotificationIcon);
            message.setIconBitmap(iconBitmap);
        }

        String appName = context.getString(R.string.app_name);
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, appName);
        builder.setContentTitle(message.getTitle())//设置通知栏标题
                .setContentText(message.getDesc()) //设置通知栏显示内容
                .setContentIntent(buildPendingIntent(context, isRepeatPush, index, message.getLink())) //设置通知栏点击意图
                //.setNumber(number) //设置通知集合的数量
                .setTicker(message.getTitle()) //通知首次出现在通知栏，带上升动画效果的
                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                .setPriority(Notification.PRIORITY_DEFAULT) //设置该通知优先级
                .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
                .setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                .setDefaults(Notification.DEFAULT_ALL)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
                .setSmallIcon(mNotificationIcon)//设置通知小ICON
                .setLargeIcon(message.getIconBitmap());

        if (message.getBannerBitmap() != null) {
            builder.setStyle(new NotificationCompat.BigPictureStyle().
                    bigPicture(message.getBannerBitmap()));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {// 8.0及以上
            createNotificationChannel(manager, appName, appName);
        }
        manager.notify(1, builder.build());
    }

    private static void showCustomNotification(Context context, boolean isRepeatPush, int index,
                                               LocalPushMessage message) {
        if (message.getIconBitmap() == null) {
            Bitmap iconBitmap = BitmapFactory.decodeResource(context.getResources(), mNotificationIcon);
            message.setIconBitmap(iconBitmap);
        }

        if (message.getBannerBitmap() == null) {
            Bitmap iconBitmap = BitmapFactory.decodeResource(context.getResources(), mNotificationIcon);
            message.setBannerBitmap(iconBitmap);
        }

        RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.layout_push_notification);
        remoteView.setTextViewText(R.id.tv_title, message.getTitle());
        remoteView.setTextViewText(R.id.tv_content, message.getDesc());
        remoteView.setImageViewBitmap(R.id.iv_icon, message.getIconBitmap());
        remoteView.setImageViewBitmap(R.id.iv_banner, message.getBannerBitmap());
        showNotification(context, isRepeatPush, index, message, remoteView);
    }

    private static void showNotification(Context context, boolean isRepeatPush, int index,
                                         LocalPushMessage message, RemoteViews remoteView) {
        String appName = context.getString(R.string.app_name);
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, appName);
        builder.setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setCustomContentView(remoteView);

        builder.setContentIntent(buildPendingIntent(context, isRepeatPush, index, message.getLink())) //设置通知栏点击意图
                //.setNumber(number) //设置通知集合的数量
                .setTicker(message.getTitle()) //通知首次出现在通知栏，带上升动画效果的
                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                .setPriority(Notification.PRIORITY_DEFAULT) //设置该通知优先级
                .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
                .setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                .setDefaults(Notification.DEFAULT_ALL)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
                .setSmallIcon(mNotificationIcon)//设置通知小ICON
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setCustomContentView(remoteView);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {// 8.0及以上
            createNotificationChannel(manager, appName, appName);
        }
        manager.notify(1, builder.build());
    }

    @TargetApi(Build.VERSION_CODES.O)
    private static void createNotificationChannel(NotificationManager notificationManager, String channelId, String channelName) {
        NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
        notificationManager.createNotificationChannel(channel);
    }

    private static PendingIntent buildPendingIntent(Context context, boolean isRepeatPush,
                                                    int index, String link) {
        Intent intent = new Intent(context.getApplicationContext(), LocalPushReceiver.class);
        intent.setAction(PushConstants.NOTIFICATION_BROADCAST_ACTION);
        intent.putExtra(PushConstants.LOCAL_PUSH_MSG_INDEX, index);
        intent.putExtra(PushConstants.IS_REPEAT_PUSH, isRepeatPush);
        intent.putExtra(PushConstants.LOCAL_PUSH_LINK, link);
        return PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}