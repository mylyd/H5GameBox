package com.mobo.funplay.gamebox.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class SearchBean implements Parcelable {
    private int ret;
    private SearchItem data;

    protected SearchBean(Parcel in) {
        ret = in.readInt();
        data = in.readParcelable(SearchItem.class.getClassLoader());
    }

    public static final Creator<SearchBean> CREATOR = new Creator<SearchBean>() {
        @Override
        public SearchBean createFromParcel(Parcel in) {
            return new SearchBean(in);
        }

        @Override
        public SearchBean[] newArray(int size) {
            return new SearchBean[size];
        }
    };

    public int getRet() {
        return ret;
    }

    public void setRet(int ret) {
        this.ret = ret;
    }

    public SearchItem getData() {
        return data;
    }

    public void setData(SearchItem data) {
        this.data = data;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(ret);
        parcel.writeParcelable(data, i);
    }
}
