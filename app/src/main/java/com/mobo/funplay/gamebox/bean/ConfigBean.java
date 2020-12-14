package com.mobo.funplay.gamebox.bean;

import com.mobo.funplay.gamebox.constants.Constants;

/**
 * @author : ydli
 * @description game 全局配置bean
 * @time : 13:48
 */
public class ConfigBean {
    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        private int code;
        private ConfigItem config;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public ConfigItem getConfig() {
            return config;
        }

        public void setConfig(ConfigItem config) {
            this.config = config;
        }

        public static class ConfigItem {
            private String home_game_link = Constants.game_url;

            public String getHome_game_link() {
                return home_game_link;
            }

            public void setHome_game_link(String home_game_link) {
                this.home_game_link = home_game_link;
            }
        }
    }
}
