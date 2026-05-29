package com.mokardder.AlwaysActive.service.Sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class SyncService extends Service {

    private static final Object LOCK = new Object();
    private static AutomaticAccountSyncAdapter syncAdapter;

    @Override
    public void onCreate() {
        super.onCreate();
        synchronized (LOCK) {
            if (syncAdapter == null) {
                syncAdapter = new AutomaticAccountSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return syncAdapter.getSyncAdapterBinder();
    }
}
