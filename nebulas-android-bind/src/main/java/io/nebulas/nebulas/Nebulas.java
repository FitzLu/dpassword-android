package io.nebulas.nebulas;

import android.content.Context;
import android.support.annotation.Nullable;

import io.nebulas.core.Address;
import io.nebulas.dev.NASLog;
import io.nebulas.exception.KeyDecryptException;
import io.nebulas.exception.KeyEncryptException;
import io.nebulas.exception.KeyNotFoundException;
import io.nebulas.exception.PassphraseException;

public class Nebulas {

    private static final String mTag = "Nebulas";
    private static volatile boolean sIsInitialized = false;

    /**
     * Initialize nebulas android sdk framework
     * @param context Recommend use application context
     * */
    public static void initialize(Context context){
        if (sIsInitialized){
            NASLog.e(mTag, "Nebulas has already been initialized! `Nebulas.initialize(...)` should only be called " +
                    "1 single time to avoid memory leaks!");
        }else{
            sIsInitialized = true;
        }
        NebulasSharedPrefManager.initialize(context);
        try{
            NebulasAccountManager.initialize();
        }catch (Exception e){
            NASLog.e(mTag, "NebulasAccountManager initialize failed " + e.toString());
        }
    }

    /**
     * Create a new account
     * @param passphrase user pass phrase
     * @return address may be null if occur exception
     * */
    @Nullable
    public static Address createNewAccount(String passphrase, byte[] data){
        try {
            return NebulasAccountManager.get().createNewAccount(passphrase, data);
        }catch (Exception e){
            NASLog.e(mTag, "Failed to create account " + e.toString());
            return null;
        }
    }

    /**
     * Store wallet address
     * @param address current wallet address
     * */
    public static void setMyWalletAddress(String address){
        NebulasSharedPrefManager.get().setAddress(address);
    }

    /**
     * Get current wallet address
     * @return address
     * */
    public static String getMyWalletAddress(){
        return NebulasSharedPrefManager.get().getAddress();
    }

    /**
     * Store public key
     * @param publicKey current public key
     * */
    public static void setMyPublicKey(byte[] publicKey) throws Exception {
        NebulasSharedPrefManager.get().setPublicKey(publicKey);
    }

    /**
     * Get current public key
     * @return public key
     * */
    public static byte[] getMyPublicKey() throws Exception {
        return NebulasSharedPrefManager.get().getPublicKey();
    }

    /**
     * Store private key
     * @param privateKey current private key
     * @param passPhrase user pass phrase */
    public static void setMyPrivateKey(byte[] privateKey, String passPhrase) throws KeyEncryptException, PassphraseException {
        NebulasSharedPrefManager.get().setPrivateKey(privateKey, passPhrase);
    }

    /**
     * Get current private key
     * @param passPhrase user pass phrase
     * @return private key
     * */
    public static byte[] getMyPrivateKey(String passPhrase) throws KeyDecryptException, KeyNotFoundException, PassphraseException {
        return NebulasSharedPrefManager.get().getPrivateKey(passPhrase);
    }

}
