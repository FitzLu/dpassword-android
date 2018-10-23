package io.nebulas.nebulas;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.text.TextUtils;
import android.util.Base64;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import io.nebulas.dev.NASLog;
import io.nebulas.exception.KeyDecryptException;
import io.nebulas.exception.KeyEncryptException;
import io.nebulas.exception.KeyNotFoundException;
import io.nebulas.exception.PassphraseException;
import io.nebulas.storage.NebulasSharedPreferences;

@RestrictTo(RestrictTo.Scope.LIBRARY)
class NebulasSharedPrefManager {

    private static final String mTag = "NebulasSharedPrefManager";

    private static NebulasSharedPrefManager mInstance;

    private NebulasSharedPreferences mNebulasSharedPreferences;

    private static final String addressKey = "address";
    private static final String privateKey = "privateKey";
    private static final String publicKey  = "publicKey";

    private NebulasSharedPrefManager(Context context){
        mNebulasSharedPreferences = new NebulasSharedPreferences(context);
    }

    public static NebulasSharedPrefManager get(){
        return mInstance;
    }

    public static void initialize(Context context){
        mInstance = new NebulasSharedPrefManager(context);
    }

    public void setAddress(String address){
        mNebulasSharedPreferences.putString(addressKey, address);
    }

    public String getAddress(){
        return mNebulasSharedPreferences.getString(addressKey, "");
    }

    public void setPrivateKey(byte[] key, String passPhrase) throws KeyEncryptException, PassphraseException {
        SecretKey sk = generateKey(passPhrase);
        if (sk == null){
            throw new PassphraseException();
        }
        byte[] encryptByteArray = encrypt(key, sk);
        if (encryptByteArray == null || encryptByteArray.length == 0){
            throw new KeyEncryptException();
        }
        String encrpytKey = Base64.encodeToString(encryptByteArray, Base64.NO_WRAP);
        mNebulasSharedPreferences.putString(privateKey, encrpytKey);
    }

    public byte[] getPrivateKey(String passPhrase) throws KeyDecryptException, KeyNotFoundException, PassphraseException {
        SecretKey sk = generateKey(passPhrase);
        if (sk == null){
            throw new PassphraseException();
        }
        String readKey = mNebulasSharedPreferences.getString(privateKey, "");
        if (TextUtils.isEmpty(readKey)){
            throw new KeyNotFoundException();
        }
        byte[] privateKey = decrypt(Base64.decode(readKey, Base64.NO_WRAP), sk);
        if (privateKey == null || privateKey.length == 0){
            throw new KeyDecryptException();
        }
        return privateKey;
    }

    public void setPublicKey(byte[] key) throws Exception{
        String base64Encode = Base64.encodeToString(key, Base64.NO_WRAP);
        if (!TextUtils.isEmpty(base64Encode)) {
            mNebulasSharedPreferences.putString(publicKey, base64Encode);
        }else{
            throw new Exception("public key base64 encode failed");
        }
    }

    public byte[] getPublicKey() throws Exception{
        String base64Encode = mNebulasSharedPreferences.getString(publicKey, "");
        if (!TextUtils.isEmpty(base64Encode)){
            return Base64.decode(base64Encode, Base64.NO_WRAP);
        }else{
            throw new Exception("public key base64 decode failed");
        }
    }

    @Nullable
    private static byte[] encrypt(byte[] plainText, SecretKey secret) {
        /* Encrypt the message. */
        byte[] cipherText = null;
        try {
            Cipher cipher;
            cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secret);
            cipherText = cipher.doFinal(plainText);
        }catch (Exception e){
            NASLog.e(mTag, e.toString());
        }
        return cipherText;
    }

    @Nullable
    private static byte[] decrypt(byte[] cipherText, SecretKey secret) {
        /* Decrypt the message, given derived encContentValues and initialization vector. */
        byte[] decryptText= null;
        try {
            Cipher cipher;
            cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secret);
            decryptText = cipher.doFinal(cipherText);
        }catch (Exception e){
            NASLog.e(mTag, e.toString());
        }
        return decryptText;
    }

    @Nullable
    private static SecretKey generateKey(String passphrase) {
        SecretKey key;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");// 将此换成SHA-1、SHA-512、SHA-384等参数
            md.update(passphrase.getBytes());
            key = new SecretKeySpec(md.digest(), "AES");
        }catch (NoSuchAlgorithmException e) {
            return null;
        }
        return key;
    }

}
