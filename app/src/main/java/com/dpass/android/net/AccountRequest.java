package com.dpass.android.net;

public class AccountRequest {

    private String address;

    public AccountRequest() {
    }

    public AccountRequest(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
