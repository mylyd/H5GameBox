package com.mobo.funplay.gamebox.bean;

import java.util.List;

/**
 * 数据列表形式基础类
 * @param <T> 单个数据
 */
public abstract class BaseBean<T> {
    protected int ret;
    protected List<T> data;

    public int getRet() {
        return ret;
    }

    public void setRet(int ret) {
        this.ret = ret;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}
