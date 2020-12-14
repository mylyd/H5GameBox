package com.mobo.funplay.gamebox.bean;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

@SuppressLint("ParcelCreator")
public class ItemBean implements Parcelable {
    private int id;
    private int cate_id;
    private List<Tag> tags;
    private String preview;
    private String thumbnail;
    private int is_free;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCate_id() {
        return cate_id;
    }

    public void setCate_id(int cate_id) {
        this.cate_id = cate_id;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public String getPreview() {
        return preview;
    }

    public void setPreview(String preview) {
        this.preview = preview;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public int getIs_free() {
        return is_free;
    }

    public void setIs_free(int is_free) {
        this.is_free = is_free;
    }

    protected ItemBean(Parcel in) {
        id = in.readInt();
        cate_id = in.readInt();
        tags = in.readArrayList(Tag.class.getClassLoader());
        preview = in.readString();
        thumbnail = in.readString();
        is_free = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(cate_id);
        dest.writeList(tags);
        dest.writeString(preview);
        dest.writeString(thumbnail);
        dest.writeInt(is_free);
    }

    public static final Creator<ItemBean> CREATOR = new Creator<ItemBean>() {
        @Override
        public ItemBean createFromParcel(Parcel in) {
            return new ItemBean(in);
        }

        @Override
        public ItemBean[] newArray(int size) {
            return new ItemBean[size];
        }
    };
}
