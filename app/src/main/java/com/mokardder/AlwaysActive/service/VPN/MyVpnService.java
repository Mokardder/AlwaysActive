package com.mokardder.AlwaysActive.service.VPN;


import android.content.Intent;
import android.net.VpnService;

public class MyVpnService extends VpnService {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Return START_STICKY to tell the system to restart the service if it gets killed
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}

