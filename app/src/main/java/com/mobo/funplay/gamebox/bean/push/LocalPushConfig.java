package com.mobo.funplay.gamebox.bean.push;

import java.util.List;

/**
 * @author : jzhou
 * @description 本地推送 全局配置数据
 * @time : 13:48
 */
public class LocalPushConfig {
    private int ret;
    private DataBean data;

    public int getRet() {
        return ret;
    }

    public void setRet(int ret) {
        this.ret = ret;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        private List<Integer> interval;
        private List<List<LocalPushMessage>> messages;

        public List<Integer> getInterval() {
            return interval;
        }

        public void setInterval(List<Integer> interval) {
            this.interval = interval;
        }

        public List<List<LocalPushMessage>> getMessages() {
            return messages;
        }

        public void setMessages(List<List<LocalPushMessage>> messages) {
            this.messages = messages;
        }
    }
}
