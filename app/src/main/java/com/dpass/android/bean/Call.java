package com.dpass.android.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Call implements Parcelable{

    /**
     * result : 0
     * execute_err : insufficient balance
     * estimate_gas : 22208
     */

    @Expose
    @SerializedName("data")
    private String data;
    @Expose
    @SerializedName("result")
    private String result;
    @Expose
    @SerializedName("execute_err")
    private String executeErr;
    @Expose
    @SerializedName("estimate_gas")
    private String estimateGas;

    public Call() {
    }

    public Call(String data, String result, String executeErr, String estimateGas) {
        this.data = data;
        this.result = result;
        this.executeErr = executeErr;
        this.estimateGas = estimateGas;
    }

    protected Call(Parcel in) {
        data = in.readString();
        result = in.readString();
        executeErr = in.readString();
        estimateGas = in.readString();
    }

    public static final Creator<Call> CREATOR = new Creator<Call>() {
        @Override
        public Call createFromParcel(Parcel in) {
            return new Call(in);
        }

        @Override
        public Call[] newArray(int size) {
            return new Call[size];
        }
    };

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getExecuteErr() {
        return executeErr;
    }

    public void setExecuteErr(String executeErr) {
        this.executeErr = executeErr;
    }

    public String getEstimateGas() {
        return estimateGas;
    }

    public void setEstimateGas(String estimateGas) {
        this.estimateGas = estimateGas;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(data);
        dest.writeString(result);
        dest.writeString(executeErr);
        dest.writeString(estimateGas);
    }
}
