package com.mobo.funplay.gamebox.utils;

import android.content.Context;
import android.content.Intent;

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
}
