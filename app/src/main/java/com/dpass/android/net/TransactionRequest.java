package com.dpass.android.net;


public class TransactionRequest {

    private String data;

    public TransactionRequest() {
    }

    public TransactionRequest(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
