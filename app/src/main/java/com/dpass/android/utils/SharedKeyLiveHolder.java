package com.dpass.android.utils;

public class SharedKeyLiveHolder {

    private boolean alive;

    private byte[]  key;

    public SharedKeyLiveHolder() {
    }

    public SharedKeyLiveHolder(boolean alive, byte[] key) {
        this.alive = alive;
        this.key = key;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public byte[] getKey() {
        return key;
    }

    public void setKey(byte[] key) {
        this.key = key;
    }
}
