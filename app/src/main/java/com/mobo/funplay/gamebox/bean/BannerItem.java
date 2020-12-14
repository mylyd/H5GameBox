package com.mobo.funplay.gamebox.bean;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.IdRes;

import com.mobo.funplay.gamebox.constants.Constants;

public class BannerItem implements Parcelable {
    public int id;
    public String link;
    public String banner;
    public String name;

    protected BannerItem(Parcel in) {
        id = in.readInt();
        link = in.readString();
        banner = in.readString();
        name = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(link);
        dest.writeString(banner);
        dest.writeString(name);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BannerItem> CREATOR = new Creator<BannerItem>() {
        @Override
        public BannerItem createFromParcel(Parcel in) {
            return new BannerItem(in);
        }

        @Override
        public BannerItem[] newArray(int size) {
            return new BannerItem[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return link;
    }

    public void setUrl(String name) {
        this.link = name;
    }

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BannerItem() {
        this.id = 1111;
        this.link = Constants.game_url;
    }
}
