package com.mobo.funplay.gamebox.interfaces;

import com.mobo.funplay.gamebox.utils.MoboLogger;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by wjwang on 2016/5/3.
 */
public abstract class CommonCallback<T> implements Callback<T> {
    private static final String TAG = "CommonCallback";

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if (!response.isSuccessful()) {
            MoboLogger.debug(TAG, response.toString());
            this.onFailure(new IOException(response.toString()), false);
            return;
        }
        if (response.body() == null) {
            MoboLogger.debug(TAG, response.toString());
            this.onFailure(new IOException(response.toString()), false);
            return;
        }
        onResponse(response.body());
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        MoboLogger.debug(TAG, "Throwable " + t);
        this.onFailure(t, true);
    }

    public abstract void onResponse(T response);

    /**
     * @param t
     * @param isServerUnavailable 服务不可用/连不上/超时/本地错误
     */
    public abstract void onFailure(Throwable t, boolean isServerUnavailable);
}
