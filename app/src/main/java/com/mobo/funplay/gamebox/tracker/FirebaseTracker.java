package com.mobo.funplay.gamebox.tracker;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;
/**
 * @author : ydli
 * @time : 20-06-22 11：33
 * @description
 */
public class FirebaseTracker {
    private static Context mContext;
    private static FirebaseTracker mFirebaseTracker;

    public static FirebaseTracker getInstance() {
        if (mFirebaseTracker == null) {
            synchronized (FirebaseTracker.class) {
                if (mFirebaseTracker == null) {
                    mFirebaseTracker = new FirebaseTracker();
                }
            }
        }
        return mFirebaseTracker;
    }

    public void init(Context context) {
        mContext = context;
    }

    public void track(String eventName) {
        if (mContext == null) {
            throw new IllegalStateException("FirebaseTracker should be initialzed first.");
        }
        //在这里截取40长度，因为firebase的eventName超过40会报错
        if (eventName.length() > 40)
            eventName = eventName.substring(0, 39);
        FirebaseAnalytics.getInstance(mContext).logEvent(eventName, null);
    }

    public void track(String eventName, Bundle bundle) {
        if (mContext == null) {
            throw new IllegalStateException("FirebaseTracker should be initialzed first.");
        }
        //在这里截取40长度，因为firebase的eventName超过40会报错
        if (eventName.length() > 40)
            eventName = eventName.substring(0, 39);
        FirebaseAnalytics.getInstance(mContext).logEvent(eventName, bundle);
    }
}
