package com.mokardder.AlwaysActive.service.Sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;

public final class AccountHelper {

    public static final String ACCOUNT_TYPE = "com.mokardder.AlwaysActive.account";
    public static final String AUTHORITY = "com.mokardder.AlwaysActive.sync.provider";

    public static final String ACCOUNT_1 = "AlwaysActive Primary Sync";
    public static final String ACCOUNT_2 = "AlwaysActive Secondary Sync";

    public static final String EXTRA_CHAIN_DEPTH = "com.mokardder.AlwaysActive.EXTRA_CHAIN_DEPTH";
    public static final String EXTRA_CHAIN_SOURCE = "com.mokardder.AlwaysActive.EXTRA_CHAIN_SOURCE";

    public static final int MAX_CHAIN_DEPTH = 10;
    public static final long FIFTEEN_MINUTES_SECONDS = 15 * 60L;

    private AccountHelper() {
    }

    public static void createAccounts(Context context) {
        AccountManager accountManager = AccountManager.get(context);

        Account primaryAccount = getPrimaryAccount();
        Account secondaryAccount = getSecondaryAccount();

        addAccountIfMissing(accountManager, primaryAccount);
        addAccountIfMissing(accountManager, secondaryAccount);

        configureAutomaticSync(primaryAccount);
        configureAutomaticSync(secondaryAccount);
    }

    public static void startChainedSync(Context context) {
        createAccounts(context);
        requestSync(getPrimaryAccount(), MAX_CHAIN_DEPTH, "manual_start");
    }

    public static Account getPrimaryAccount() {
        return new Account(ACCOUNT_1, ACCOUNT_TYPE);
    }

    public static Account getSecondaryAccount() {
        return new Account(ACCOUNT_2, ACCOUNT_TYPE);
    }

    static Account getNextAccount(Account currentAccount) {
        if (ACCOUNT_1.equals(currentAccount.name)) {
            return getSecondaryAccount();
        }
        return getPrimaryAccount();
    }

    static void requestSync(Account account, int chainDepth, String chainSource) {
        Bundle extras = new Bundle();
        extras.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        extras.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        extras.putInt(EXTRA_CHAIN_DEPTH, chainDepth);
        extras.putString(EXTRA_CHAIN_SOURCE, chainSource);
        ContentResolver.requestSync(account, AUTHORITY, extras);
    }

    private static void addAccountIfMissing(AccountManager accountManager, Account account) {
        Account[] existingAccounts = accountManager.getAccountsByType(account.type);
        for (Account existingAccount : existingAccounts) {
            if (existingAccount.name.equals(account.name)) {
                return;
            }
        }
        accountManager.addAccountExplicitly(account, null, null);
    }

    private static void configureAutomaticSync(Account account) {
        ContentResolver.setIsSyncable(account, AUTHORITY, 1);
        ContentResolver.setSyncAutomatically(account, AUTHORITY, true);

        Bundle periodicExtras = new Bundle();
        periodicExtras.putInt(EXTRA_CHAIN_DEPTH, MAX_CHAIN_DEPTH);
        periodicExtras.putString(EXTRA_CHAIN_SOURCE, "periodic_15_minute_sync");
        ContentResolver.addPeriodicSync(account, AUTHORITY, periodicExtras, FIFTEEN_MINUTES_SECONDS);
    }
}
