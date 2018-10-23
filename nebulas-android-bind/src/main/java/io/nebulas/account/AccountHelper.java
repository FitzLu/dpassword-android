package io.nebulas.account;

import io.nebulas.core.Address;
import io.nebulas.crypto.Crypto;
import io.nebulas.crypto.keystore.Algorithm;
import io.nebulas.crypto.keystore.PrivateKey;
import io.nebulas.nebulas.Nebulas;

public class AccountHelper {

    private Algorithm encryptAlg;
    private Algorithm signatureAlg;

    public AccountHelper(){
        this.encryptAlg   = Algorithm.SCRYPT;
        this.signatureAlg = Algorithm.SECP256K1;
    }

    public Address newAccount(String passphrase, byte[] data) throws Exception {
        PrivateKey privateKey = Crypto.NewPrivateKey(this.signatureAlg, data);
        return updateAccount(privateKey, passphrase);
    }

    private Address updateAccount(PrivateKey privateKey, String passphrase) throws Exception {
        byte[] pub = privateKey.publickey().encode();
        Address address = Address.NewAddressFromPubKey(pub);
        Nebulas.setMyWalletAddress(address.string());
        Nebulas.setMyPublicKey(pub);
        Nebulas.setMyPrivateKey(privateKey.encode(), passphrase);
        privateKey.clear();

        return address;
    }

}
