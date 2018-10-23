package io.nebulas.nebulas;

import android.support.annotation.RestrictTo;

import io.nebulas.account.AccountHelper;
import io.nebulas.core.Address;

@RestrictTo(RestrictTo.Scope.LIBRARY)
class NebulasAccountManager {

    private static NebulasAccountManager mInstance;

    private AccountHelper accountHelper;

    public static NebulasAccountManager get(){
        return mInstance;
    }

    private NebulasAccountManager() throws Exception {
        accountHelper = new AccountHelper();
    }

    public static void initialize() throws Exception{
        mInstance = new NebulasAccountManager();
    }

    public Address createNewAccount(String passphrase, byte[] data) throws Exception{
        return accountHelper.newAccount(passphrase, data);
    }

}
