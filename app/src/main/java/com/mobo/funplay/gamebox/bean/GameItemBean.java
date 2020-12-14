package com.mobo.funplay.gamebox.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author : ydli
 * @time : 19-12-6 下午3:13
 * @description H5游戏数据Bean
 */
public class GameItemBean implements Parcelable {
    private String link;
    private String thumbnail;
    private String name;
    private int id;
    private String desc;
    private String preview;
    private int cnt_like;
    private boolean collect;

    protected GameItemBean(Parcel in) {
        link = in.readString();
        thumbnail = in.readString();
        name = in.readString();
        id = in.readInt();
        desc = in.readString();
        preview = in.readString();
        cnt_like = in.readInt();
        collect = in.readByte() == 1;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(link);
        dest.writeString(thumbnail);
        dest.writeString(name);
        dest.writeInt(id);
        dest.writeString(desc);
        dest.writeString(preview);
        dest.writeInt(cnt_like);
        dest.writeInt((byte) (collect ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<GameItemBean> CREATOR = new Creator<GameItemBean>() {
        @Override
        public GameItemBean createFromParcel(Parcel in) {
            return new GameItemBean(in);
        }

        @Override
        public GameItemBean[] newArray(int size) {
            return new GameItemBean[size];
        }
    };

    public GameItemBean(String link, String thumbnail, String name, int id) {
        this.link = link;
        this.thumbnail = thumbnail;
        this.name = name;
        this.id = id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getPreview() {
        return preview;
    }

    public void setPreview(String preview) {
        this.preview = preview;
    }

    public int getCnt_like() {
        return cnt_like;
    }

    public void setCnt_like(int cnt_like) {
        this.cnt_like = cnt_like;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isCollect() {
        return collect; //false 收藏 ，true 没收藏
    }

    public void setCollect(boolean collect) {
        this.collect = collect;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        GameItemBean bean = (GameItemBean) obj;
        return this.id == bean.id;
    }
}
