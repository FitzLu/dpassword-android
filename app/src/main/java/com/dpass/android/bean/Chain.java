package com.dpass.android.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Chain implements Parcelable{

    /**
     * chain_id : 100
     * tail : b10c1203d5ae6d4d069d5f520eb060f2f5fb74e942f391e7cadbc2b5148dfbcb
     * lib : da30b4ed14affb62b3719fb5e6952d3733e84e53fe6e955f8e46da503300c985
     * height : 365
     * protocol_version : /neb/1.0.0
     * synchronized : false
     * version : 0.7.0
     */

    @Expose
    @SerializedName("chain_id")
    private Long   chainId;
    @Expose
    @SerializedName("tail")
    private String tail;
    @Expose
    @SerializedName("lib")
    private String lib;
    @Expose
    @SerializedName("height")
    private String height;
    @Expose
    @SerializedName("protocol_version")
    private String protocolVersion;
    @Expose
    @SerializedName("synchronized")
    private boolean synchron;
    @Expose
    @SerializedName("version")
    private String version;

    public Chain() {
    }

    public Chain(Long chainId, String tail, String lib, String height, String protocolVersion, boolean synchron, String version) {
        this.chainId = chainId;
        this.tail = tail;
        this.lib = lib;
        this.height = height;
        this.protocolVersion = protocolVersion;
        this.synchron = synchron;
        this.version = version;
    }

    protected Chain(Parcel in) {
        if (in.readByte() == 0) {
            chainId = null;
        } else {
            chainId = in.readLong();
        }
        tail = in.readString();
        lib = in.readString();
        height = in.readString();
        protocolVersion = in.readString();
        synchron = in.readByte() != 0;
        version = in.readString();
    }

    public static final Creator<Chain> CREATOR = new Creator<Chain>() {
        @Override
        public Chain createFromParcel(Parcel in) {
            return new Chain(in);
        }

        @Override
        public Chain[] newArray(int size) {
            return new Chain[size];
        }
    };

    public Long getChainId() {
        return chainId;
    }

    public void setChainId(Long chainId) {
        this.chainId = chainId;
    }

    public String getTail() {
        return tail;
    }

    public void setTail(String tail) {
        this.tail = tail;
    }

    public String getLib() {
        return lib;
    }

    public void setLib(String lib) {
        this.lib = lib;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    public void setProtocolVersion(String protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public boolean isSynchron() {
        return synchron;
    }

    public void setSynchron(boolean synchron) {
        this.synchron = synchron;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (chainId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(chainId);
        }
        dest.writeString(tail);
        dest.writeString(lib);
        dest.writeString(height);
        dest.writeString(protocolVersion);
        dest.writeByte((byte) (synchron ? 1 : 0));
        dest.writeString(version);
    }
}
