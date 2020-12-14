package com.mobo.funplay.gamebox.bean.push;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class LocalPushMessage implements Parcelable {
    private int id;
    private String title;
    private String desc;
    private String icon_url;
    private String preview_url;
    private String link;
    private Bitmap iconBitmap; //下载之后的图片
    private Bitmap bannerBitmap;

    public LocalPushMessage(Parcel in) {
        id = in.readInt();
        title = in.readString();
        desc = in.readString();
        icon_url = in.readString();
        preview_url = in.readString();
        link = in.readString();
    }

    public static final Creator<LocalPushMessage> CREATOR = new Creator<LocalPushMessage>() {
        @Override
        public LocalPushMessage createFromParcel(Parcel in) {
            return new LocalPushMessage(in);
        }

        @Override
        public LocalPushMessage[] newArray(int size) {
            return new LocalPushMessage[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getIcon_url() {
        return icon_url;
    }

    public void setIcon_url(String icon_url) {
        this.icon_url = icon_url;
    }

    public String getPreview_url() {
        return preview_url;
    }

    public void setPreview_url(String preview_url) {
        this.preview_url = preview_url;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Bitmap getIconBitmap() {
        return iconBitmap;
    }

    public void setIconBitmap(Bitmap iconBitmap) {
        this.iconBitmap = iconBitmap;
    }

    public Bitmap getBannerBitmap() {
        return bannerBitmap;
    }

    public void setBannerBitmap(Bitmap bannerBitmap) {
        this.bannerBitmap = bannerBitmap;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(desc);
        dest.writeString(icon_url);
        dest.writeString(preview_url);
        dest.writeString(link);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LocalPushMessage bean = (LocalPushMessage) o;
        return id == bean.id;
    }
}