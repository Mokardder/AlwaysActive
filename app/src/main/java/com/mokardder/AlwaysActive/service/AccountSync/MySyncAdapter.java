package com.mokardder.AlwaysActive.service.AccountSync;

// STEP 2
// SyncAdapter with chained syncing

package com.mokardder.AlwaysActive;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

public class MySyncAdapter
        extends AbstractThreadedSyncAdapter {

    public MySyncAdapter(
            Context context,
            boolean autoInitialize
    ) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(
            Account account,
            Bundle extras,
            String authority,
            ContentProviderClient provider,
            SyncResult syncResult
    ) {

        // Detect which account triggered sync
        Log.d(
                "SYNC",
                "Current Account: " + account.name
        );

        try {

            // Fake sync work
            Thread.sleep(3000);

            // If first account syncs
            if (account.name.equals(
                    AccountHelper.ACCOUNT_1
            )) {

                Log.d(
                        "SYNC",
                        "Chained sync -> SecondaryAccount"
                );

                // Trigger second account sync
                triggerSecondSync();
            }

            // If second account syncs
            if (account.name.equals(
                    AccountHelper.ACCOUNT_2
            )) {

                Log.d(
                        "SYNC",
                        "Secondary account synced"
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Trigger second account manually
    private void triggerSecondSync() {

        Bundle bundle = new Bundle();

        // Immediate sync
        bundle.putBoolean(
                android.content.ContentResolver
                        .SYNC_EXTRAS_MANUAL,
                true
        );

        // High priority sync
        bundle.putBoolean(
                android.content.ContentResolver
                        .SYNC_EXTRAS_EXPEDITED,
                true
        );

        // Request sync for second account
        android.content.ContentResolver
                .requestSync(
                        AccountHelper.account2,
                        AccountHelper.AUTHORITY,
                        bundle
                );
    }
}