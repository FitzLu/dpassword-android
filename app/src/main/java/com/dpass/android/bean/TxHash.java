package com.dpass.android.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TxHash implements Parcelable{

    /**
     * txhash : f37acdf93004f7a3d72f1b7f6e56e70a066182d85c186777a2ad3746b01c3b52
     */

    @Expose
    @SerializedName("txhash")
    private String txHash;

    public TxHash() {
    }

    public TxHash(String txHash) {
        this.txHash = txHash;
    }

    protected TxHash(Parcel in) {
        txHash = in.readString();
    }

    public static final Creator<TxHash> CREATOR = new Creator<TxHash>() {
        @Override
        public TxHash createFromParcel(Parcel in) {
            return new TxHash(in);
        }

        @Override
        public TxHash[] newArray(int size) {
            return new TxHash[size];
        }
    };

    public String getTxHash() {
        return txHash;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(txHash);
    }
}
