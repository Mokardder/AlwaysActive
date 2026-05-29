package com.mokardder.AlwaysActive.service.Sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import com.mokardder.AlwaysActive.service.Sync.utils.NotificationUtil;

public class AutomaticAccountSyncAdapter extends AbstractThreadedSyncAdapter {

    private static final String TAG = "AccountSync";

    public AutomaticAccountSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(
            Account account,
            Bundle extras,
            String authority,
            ContentProviderClient provider,
            SyncResult syncResult) {

        int depthRemaining = extras.getInt(AccountHelper.EXTRA_CHAIN_DEPTH, AccountHelper.MAX_CHAIN_DEPTH);
        String chainSource = extras.getString(AccountHelper.EXTRA_CHAIN_SOURCE, "system");

        Log.d(
                TAG,
                "Sync started for " + account.name
                        + " from " + chainSource
                        + " with depth " + depthRemaining);

        NotificationUtil.showSyncNotification(
                getContext(),
                account.name,
                depthRemaining,
                chainSource);

        if (depthRemaining <= 0) {
            Log.d(TAG, "Sync chain finished for " + account.name);
            return;
        }

        Account nextAccount = AccountHelper.getNextAccount(account);
        int nextDepth = depthRemaining - 1;

        Log.d(
                TAG,
                "Chaining sync to " + nextAccount.name
                        + " with depth " + nextDepth);

        AccountHelper.requestSync(nextAccount, nextDepth, account.name);
    }
}
