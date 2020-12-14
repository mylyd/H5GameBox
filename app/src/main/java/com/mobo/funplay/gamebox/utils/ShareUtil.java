package com.mobo.funplay.gamebox.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.fragment.app.Fragment;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

/**
 * @author : ydli
 * @time : 20-7-28 下午3:53
 * @description
 */
public class ShareUtil {

    public static void share(Context context, String string) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, string);
        context.startActivity(Intent.createChooser(intent, "App Share"));
    }

    public static void shareFacebook(Fragment fragment, CallbackManager callbackManager) {
        ShareDialog shareDialog = new ShareDialog(fragment);
        if (ShareDialog.canShow(ShareLinkContent.class)) {
            ShareLinkContent shareLinkContent = new ShareLinkContent.Builder()
                    .setContentUrl(Uri.parse("https://play.google.com/store/apps/details?id=com.mobo.funplay.gamebox"))
                    .build();
            shareDialog.show(shareLinkContent, ShareDialog.Mode.AUTOMATIC);
        }

        //对话框
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                Log.d("shareFacebook", "onSuccess: ");
            }

            @Override
            public void onCancel() {
                Log.d("shareFacebook", "onCancel: ");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("shareFacebook", "onError: " + error.toString());
            }
        });
    }
}
