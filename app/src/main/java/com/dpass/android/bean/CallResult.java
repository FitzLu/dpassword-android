package com.dpass.android.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CallResult implements Parcelable {

    @Expose
    @SerializedName("result")
    private Call result;

    public CallResult() {
    }

    public CallResult(Call result) {
        this.result = result;
    }

    protected CallResult(Parcel in) {
        result = in.readParcelable(Call.class.getClassLoader());
    }

    public static final Creator<CallResult> CREATOR = new Creator<CallResult>() {
        @Override
        public CallResult createFromParcel(Parcel in) {
            return new CallResult(in);
        }

        @Override
        public CallResult[] newArray(int size) {
            return new CallResult[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(result, flags);
    }

    public Call getResult() {
        return result;
    }

    public void setResult(Call result) {
        this.result = result;
    }

    public static Creator<CallResult> getCREATOR() {
        return CREATOR;
    }
}
