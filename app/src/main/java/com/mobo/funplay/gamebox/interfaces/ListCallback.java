package com.mobo.funplay.gamebox.interfaces;

import android.util.Log;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author : jzhou
 * @time : 19-11-14 上午11:10
 * @description 通用网络请求回调接口
 */
public abstract class ListCallback<T> implements Callback<ListResponse<T>> {
    private static final String TAG = ListCallback.class.getSimpleName();

    @Override
    public void onResponse(Call<ListResponse<T>> call, Response<ListResponse<T>> response) {
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
    public void onFailure(Call<ListResponse<T>> call, Throwable t) {
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
