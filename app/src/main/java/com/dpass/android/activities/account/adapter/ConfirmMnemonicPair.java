package com.dpass.android.activities.account.adapter;

public class ConfirmMnemonicPair {

    private String code;
    private int    status;

    public ConfirmMnemonicPair() {
    }

    public ConfirmMnemonicPair(String code, int status) {
        this.code = code;
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
