package com.mobo.funplay.gamebox.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.Toast;

import com.mobo.funplay.gamebox.R;

public final class GradeUtils {

    public static void gotoGooglePlay(Context context) {
        gotoGooglePlay(context, "", true);
    }

    public static void gotoGooglePlay(Context context, String faildMessage, boolean isShort) {
        try {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(context.getString(R.string.rating_url)));
            browserIntent.setClassName("com.android.vending",
                    "com.android.vending.AssetBrowserActivity");
            browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(browserIntent);
        } catch (Exception e) {
            try {
                Intent browserIntent2 = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(context.getString(R.string.rating_url)));
                context.startActivity(browserIntent2);
            } catch (Exception e1) {
                if (!TextUtils.isEmpty(faildMessage)) {
                    Toast.makeText(context, faildMessage, isShort ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
