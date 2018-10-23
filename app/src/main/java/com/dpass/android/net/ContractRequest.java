package com.dpass.android.net;


public class ContractRequest {

    private String Function;

    private String Args;

    public ContractRequest() { }

    public ContractRequest(String function, String args) {
        Function = function;
        Args = args;
    }

    public String getFunction() {
        return Function;
    }

    public void setFunction(String function) {
        Function = function;
    }

    public String getArgs() {
        return Args;
    }

    public void setArgs(String args) {
        Args = args;
    }
}
