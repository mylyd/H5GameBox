package com.mobo.funplay.gamebox.interfaces;

import java.util.List;

/**
 * @author : jzhou
 * @time : 19-11-14 上午11:10
 * @description 网络请求返回数据通用解析类
 */
public class ListResponse<T> {
    private int code;
    private List<T> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}
