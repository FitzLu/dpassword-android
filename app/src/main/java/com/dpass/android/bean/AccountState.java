package com.dpass.android.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AccountState implements Parcelable{

    /**
     * balance : 9489999998980000000000
     * nonce : 51
     * type : 87
     */

    @Expose
    @SerializedName("balance")
    private String balance;
    @Expose
    @SerializedName("nonce")
    private int    nonce;
    @Expose
    @SerializedName("type")
    private int    type;

    public AccountState() {
    }

    public AccountState(String balance, int nonce, int type) {
        this.balance = balance;
        this.nonce = nonce;
        this.type = type;
    }

    protected AccountState(Parcel in) {
        balance = in.readString();
        nonce = in.readInt();
        type = in.readInt();
    }

    public static final Creator<AccountState> CREATOR = new Creator<AccountState>() {
        @Override
        public AccountState createFromParcel(Parcel in) {
            return new AccountState(in);
        }

        @Override
        public AccountState[] newArray(int size) {
            return new AccountState[size];
        }
    };

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public int getNonce() {
        return nonce;
    }

    public void setNonce(int nonce) {
        this.nonce = nonce;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(balance);
        dest.writeInt(nonce);
        dest.writeInt(type);
    }
}
