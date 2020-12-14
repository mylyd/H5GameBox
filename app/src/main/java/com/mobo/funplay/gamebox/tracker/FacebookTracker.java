package com.mobo.funplay.gamebox.tracker;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsConstants;
import com.facebook.appevents.AppEventsLogger;

/**
 * @author : ydli
 * @time : 20-06-22 11：33
 * @description
 */
public class FacebookTracker {

    private static final String TAG = "FacebookTracker";

    //event name

    private static FacebookTracker mFacebookTracker;
    private static AppEventsLogger mAppEventsLogger;

    public static FacebookTracker getInstance() {
        if (mFacebookTracker == null) {
            synchronized (FacebookTracker.class) {
                if (mFacebookTracker == null) {
                    mFacebookTracker = new FacebookTracker();
                }
            }
        }
        return mFacebookTracker;
    }

    public void init(Context context) {
        FacebookSdk.sdkInitialize(context);
        mAppEventsLogger = AppEventsLogger.newLogger(context);
    }

    public void track(String eventName) {
        if (mAppEventsLogger == null) {
            //throw new IllegalStateException("FacebookTracker should be initialzed first.");
            return;
        }
        try {
            mAppEventsLogger.logEvent(eventName);
        } catch (Exception e) {
            //e.printStackTrace();
        }
        Log.d(TAG, "track " + eventName);
    }

    /**
     * This function assumes logger is an instance of AppEventsLogger and has been
     * created using AppEventsLogger.newLogger() call.
     */
    public void unlockAchievementEvent(String description) {
        if (mAppEventsLogger == null) {
            //throw new IllegalStateException("FacebookTracker should be initialzed first.");
            return;
        }

        try {
            Bundle params = new Bundle();
            params.putString(AppEventsConstants.EVENT_PARAM_DESCRIPTION, description);
            mAppEventsLogger.logEvent(AppEventsConstants.EVENT_NAME_UNLOCKED_ACHIEVEMENT, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This function assumes logger is an instance of AppEventsLogger and has been
     * created using AppEventsLogger.newLogger() call.
     */
    public void viewContentEvent(String description) {
        if (mAppEventsLogger == null) {
            //throw new IllegalStateException("FacebookTracker should be initialzed first.");
            return;
        }
        try {
            Bundle params = new Bundle();
            params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, "product");
            params.putString(AppEventsConstants.EVENT_PARAM_CONTENT, description);
            params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, "a");
            params.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, "USD");
            mAppEventsLogger.logEvent(AppEventsConstants.EVENT_NAME_VIEWED_CONTENT, 1, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
