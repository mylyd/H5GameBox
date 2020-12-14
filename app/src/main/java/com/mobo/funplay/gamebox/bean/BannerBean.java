package com.mobo.funplay.gamebox.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class BannerBean extends BaseBean<BannerItem> implements Parcelable {
    public BannerBean() {
    }

    protected BannerBean(Parcel in) {
        ret = in.readInt();
        data = in.readArrayList(ItemBean.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(ret);
        dest.writeList(data);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BannerBean> CREATOR = new Creator<BannerBean>() {
        @Override
        public BannerBean createFromParcel(Parcel in) {
            return new BannerBean(in);
        }

        @Override
        public BannerBean[] newArray(int size) {
            return new BannerBean[size];
        }
    };
}
