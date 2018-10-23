package com.dpass.android.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TransactionReceipt implements Parcelable{

    /**
     * hash : cda54445ffccf4ea17f043e86e54be11b002053f9edbe30ae1fbc0437c2b6a73
     * chainId : 100
     * from : n1Z6SbjLuAEXfhX1UJvXT6BB5osWYxVg3F3
     * to : n1PxKRaJ5jZHXwTfgM9WqkZJJVXBxRcggEE
     * value : 10000000000000000000
     * nonce : 53
     * timestamp : 1521964742
     * type : binary
     * data : null
     * gas_price : 1000000
     * gas_limit : 20000
     * contract_address :
     * status : 1
     * gas_used : 20000
     */

    @Expose
    @SerializedName("hash")
    private String hash;
    @Expose
    @SerializedName("chainId")
    private int    chainId;
    @Expose
    @SerializedName("from")
    private String from;
    @Expose
    @SerializedName("to")
    private String to;
    @Expose
    @SerializedName("value")
    private String value;
    @Expose
    @SerializedName("nonce")
    private String nonce;
    @Expose
    @SerializedName("timestamp")
    private String timestamp;
    @Expose
    @SerializedName("type")
    private String type;
    @Expose
    @SerializedName("data")
    private Object data;
    @Expose
    @SerializedName("gas_price")
    private String gasPrice;
    @Expose
    @SerializedName("gas_limit")
    private String gasLimit;
    @Expose
    @SerializedName("contract_address")
    private String contractAddress;
    @Expose
    @SerializedName("status")
    private int    status;
    @Expose
    @SerializedName("gas_used")
    private String gasUsed;

    public TransactionReceipt() {
    }

    public TransactionReceipt(String hash, int chainId, String from, String to, String value, String nonce, String timestamp, String type, Object data, String gasPrice, String gasLimit, String contractAddress, int status, String gasUsed) {
        this.hash = hash;
        this.chainId = chainId;
        this.from = from;
        this.to = to;
        this.value = value;
        this.nonce = nonce;
        this.timestamp = timestamp;
        this.type = type;
        this.data = data;
        this.gasPrice = gasPrice;
        this.gasLimit = gasLimit;
        this.contractAddress = contractAddress;
        this.status = status;
        this.gasUsed = gasUsed;
    }

    protected TransactionReceipt(Parcel in) {
        hash = in.readString();
        chainId = in.readInt();
        from = in.readString();
        to = in.readString();
        value = in.readString();
        nonce = in.readString();
        timestamp = in.readString();
        type = in.readString();
        gasPrice = in.readString();
        gasLimit = in.readString();
        contractAddress = in.readString();
        status = in.readInt();
        gasUsed = in.readString();
    }

    public static final Creator<TransactionReceipt> CREATOR = new Creator<TransactionReceipt>() {
        @Override
        public TransactionReceipt createFromParcel(Parcel in) {
            return new TransactionReceipt(in);
        }

        @Override
        public TransactionReceipt[] newArray(int size) {
            return new TransactionReceipt[size];
        }
    };

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public int getChainId() {
        return chainId;
    }

    public void setChainId(int chainId) {
        this.chainId = chainId;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getGasPrice() {
        return gasPrice;
    }

    public void setGasPrice(String gasPrice) {
        this.gasPrice = gasPrice;
    }

    public String getGasLimit() {
        return gasLimit;
    }

    public void setGasLimit(String gasLimit) {
        this.gasLimit = gasLimit;
    }

    public String getContractAddress() {
        return contractAddress;
    }

    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getGasUsed() {
        return gasUsed;
    }

    public void setGasUsed(String gasUsed) {
        this.gasUsed = gasUsed;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(hash);
        dest.writeInt(chainId);
        dest.writeString(from);
        dest.writeString(to);
        dest.writeString(value);
        dest.writeString(nonce);
        dest.writeString(timestamp);
        dest.writeString(type);
        dest.writeString(gasPrice);
        dest.writeString(gasLimit);
        dest.writeString(contractAddress);
        dest.writeInt(status);
        dest.writeString(gasUsed);
    }
}
