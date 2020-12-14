package com.mobo.funplay.gamebox.interfaces;

import android.util.Log;

import com.mobo.funplay.gamebox.bean.BaseBean;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author : ydli
 * @time : 20-06-30 上午11:10
 * @description 通用网络请求回调接口
 */
public abstract class BaseCallback<T> implements Callback<BaseBean<T>> {
    private static final String TAG = BaseCallback.class.getSimpleName();

    @Override
    public void onResponse(Call<BaseBean<T>> call, Response<BaseBean<T>> response) {
        if (!response.isSuccessful()) {
            Log.d(TAG, response.toString());
            this.onFailure(new IOException(response.toString()), false);
            return;
        }
        if (response.body() == null) {
            Log.d(TAG, response.toString());
            this.onFailure(new IOException(response.toString()), false);
            return;
        }
        onResponse(response.body().getData());
    }

    @Override
    public void onFailure(Call<BaseBean<T>> call, Throwable t) {
        Log.d(TAG, "Throwable " + t);
        this.onFailure(t, true);
    }

    public abstract void onResponse(List<T> response);

    /**
     * @param t
     * @param isServerUnavailable 服务不可用/连不上/超时/本地错误
     */
    public abstract void onFailure(Throwable t, boolean isServerUnavailable);
}
