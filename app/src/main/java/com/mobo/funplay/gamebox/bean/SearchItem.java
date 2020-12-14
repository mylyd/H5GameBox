package com.mobo.funplay.gamebox.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * @author : ydli
 * @time : 20-6-30 上午10:57
 * @description
 */
public class SearchItem implements Parcelable {
    private String msg;
    private int is_match;
    private List<GameItemBean> wallpapers;

    protected SearchItem(Parcel in) {
        msg = in.readString();
        is_match = in.readInt();
        wallpapers = in.createTypedArrayList(GameItemBean.CREATOR);
    }

    public static final Creator<SearchItem> CREATOR = new Creator<SearchItem>() {
        @Override
        public SearchItem createFromParcel(Parcel in) {
            return new SearchItem(in);
        }

        @Override
        public SearchItem[] newArray(int size) {
            return new SearchItem[size];
        }
    };

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getIs_match() {
        return is_match;
    }

    public void setIs_match(int is_match) {
        this.is_match = is_match;
    }

    public List<GameItemBean> getWallpapers() {
        return wallpapers;
    }

    public void setWallpapers(List<GameItemBean> wallpapers) {
        this.wallpapers = wallpapers;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(msg);
        parcel.writeInt(is_match);
        parcel.writeTypedList(wallpapers);
    }
}
