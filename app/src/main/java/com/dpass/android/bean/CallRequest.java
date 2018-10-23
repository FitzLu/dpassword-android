package com.dpass.android.bean;

public class CallRequest {


    /**
     * from : n1Z6SbjLuAEXfhX1UJvXT6BB5osWYxVg3F3
     * to : n1mL2WCZyRi1oELEugfCZoNAW3dt8QpHtJw
     * value : 0
     * nonce : 3
     * gasPrice : 1000000
     * gasLimit : 2000000
     * contract : {"function":"transferValue","args":"[500]"}
     */

    private String from;
    private String to;
    private String value;
    private Long   nonce;
    private String gasPrice;
    private String gasLimit;
    private ContractBean contract;

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

    public Long getNonce() {
        return nonce;
    }

    public void setNonce(Long nonce) {
        this.nonce = nonce;
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

    public ContractBean getContract() {
        return contract;
    }

    public void setContract(ContractBean contract) {
        this.contract = contract;
    }

    public static class ContractBean {
        /**
         * function : transferValue
         * args : [500]
         */

        private String function;
        private String args;

        public String getFunction() {
            return function;
        }

        public void setFunction(String function) {
            this.function = function;
        }

        public String getArgs() {
            return args;
        }

        public void setArgs(String args) {
            this.args = args;
        }
    }
}
