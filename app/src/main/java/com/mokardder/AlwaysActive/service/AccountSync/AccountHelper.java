package com.mokardder.AlwaysActive.service.AccountSync;

// STEP 1
// Create 2 chained accounts

package com.example.syncapp;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;

public class AccountHelper {

    public static final String ACCOUNT_TYPE =
            "com.mokardder.AlwaysActive.account";

    public static final String AUTHORITY =
            "com.mokardder.AlwaysActive.provider";

    // First account
    public static final String ACCOUNT_1 =
            "PrimaryAccount";

    // Second account
    public static final String ACCOUNT_2 =
            "SecondaryAccount";

    // Store created accounts
    public static Account account1;
    public static Account account2;

    public static void createAccounts(Context context) {

        // Get Android AccountManager
        AccountManager manager =
                (AccountManager)
                        context.getSystemService(
                                Context.ACCOUNT_SERVICE
                        );

        // Create first account
        account1 = new Account(
                ACCOUNT_1,
                ACCOUNT_TYPE
        );

        // Create second account
        account2 = new Account(
                ACCOUNT_2,
                ACCOUNT_TYPE
        );

        // Add first account
        manager.addAccountExplicitly(
                account1,
                null,
                null
        );

        // Add second account
        manager.addAccountExplicitly(
                account2,
                null,
                null
        );

        // Enable auto sync for first account
        android.content.ContentResolver
                .setSyncAutomatically(
                        account1,
                        AUTHORITY,
                        true
                );

        // Enable auto sync for second account
        android.content.ContentResolver
                .setSyncAutomatically(
                        account2,
                        AUTHORITY,
                        true
                );

        // Sync first account every 15 minutes
        android.content.ContentResolver
                .addPeriodicSync(
                        account1,
                        AUTHORITY,
                        new android.os.Bundle(),
                        15 * 60
                );

        // Sync second account every 15 minutes
        android.content.ContentResolver
                .addPeriodicSync(
                        account2,
                        AUTHORITY,
                        new android.os.Bundle(),
                        15 * 60
                );
    }
}