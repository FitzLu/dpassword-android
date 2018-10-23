package com.dpass.android.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GasPrice implements Parcelable{

    /**
     * gas_price : 1000000
     */

    @Expose
    @SerializedName("gas_price")
    private String gasPrice;

    public GasPrice() {
    }

    public GasPrice(String gasPrice) {
        this.gasPrice = gasPrice;
    }

    protected GasPrice(Parcel in) {
        gasPrice = in.readString();
    }

    public static final Creator<GasPrice> CREATOR = new Creator<GasPrice>() {
        @Override
        public GasPrice createFromParcel(Parcel in) {
            return new GasPrice(in);
        }

        @Override
        public GasPrice[] newArray(int size) {
            return new GasPrice[size];
        }
    };

    public String getGasPrice() {
        return gasPrice;
    }

    public void setGasPrice(String gasPrice) {
        this.gasPrice = gasPrice;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(gasPrice);
    }
}
