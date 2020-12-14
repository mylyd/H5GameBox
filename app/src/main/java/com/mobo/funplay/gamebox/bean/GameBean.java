package com.mobo.funplay.gamebox.bean;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import java.util.List;

/**
 * @author : ydli
 * @time : 19-12-6 下午3:15
 * @description H5游戏分类界面Bean
 */
public class GameBean implements Parcelable {
    private String cate_name;
    private String w_type;
    private int cate_id;
    private String cate_desc;
    private List<GameItemBean> items;

    public GameBean(String cate_name, String w_type, int cate_id, String cate_desc, List<GameItemBean> items) {
        this.cate_name = cate_name;
        this.w_type = w_type;
        this.cate_id = cate_id;
        this.cate_desc = cate_desc;
        this.items = items;
    }

    protected GameBean(Parcel in) {
        cate_name = in.readString();
        w_type = in.readString();
        cate_id = in.readInt();
        cate_desc = in.readString();
        items = in.createTypedArrayList(GameItemBean.CREATOR);
    }

    public static final Creator<GameBean> CREATOR = new Creator<GameBean>() {
        @Override
        public GameBean createFromParcel(Parcel in) {
            return new GameBean(in);
        }

        @Override
        public GameBean[] newArray(int size) {
            return new GameBean[size];
        }
    };

    public String getCate_name() {
        return cate_name;
    }

    public void setCate_name(String cate_name) {
        this.cate_name = cate_name;
    }

    public String getW_type() {
        return w_type;
    }

    public void setW_type(String w_type) {
        this.w_type = w_type;
    }

    public int getCate_id() {
        return cate_id;
    }

    public void setCate_id(int cate_id) {
        this.cate_id = cate_id;
    }

    public String getCate_desc() {
        return cate_desc;
    }

    public void setCate_desc(String cate_desc) {
        this.cate_desc = cate_desc;
    }

    public List<GameItemBean> getItems() {
        return items;
    }

    public void setItems(List<GameItemBean> items) {
        this.items = items;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(cate_name);
        parcel.writeString(w_type);
        parcel.writeInt(cate_id);
        parcel.writeString(cate_desc);
        parcel.writeTypedList(items);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        GameBean bean = (GameBean) obj;
        return this.cate_id == bean.cate_id;
    }
}
