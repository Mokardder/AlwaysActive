package com.mokardder.AlwaysActive.service.Sync.utils;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.mokardder.AlwaysActive.R;
import com.mokardder.AlwaysActive.ui.MainActivity;

import java.util.concurrent.atomic.AtomicInteger;

public final class NotificationUtil {

    private static final String CHANNEL_ID = "account_sync_channel";
    private static final String CHANNEL_NAME = "Account Sync";
    private static final String CHANNEL_DESCRIPTION = "Shows every account sync adapter run.";
    private static final int NOTIFICATION_ID_START = 10_000;

    private static final AtomicInteger NEXT_NOTIFICATION_ID = new AtomicInteger(NOTIFICATION_ID_START);

    private NotificationUtil() {
    }

    public static void showSyncNotification(
            Context context,
            String accountName,
            int depthRemaining,
            String chainSource) {
        createNotificationChannel(context);

        if (!canPostNotifications(context)) {
            return;
        }

        PendingIntent contentIntent = PendingIntent.getActivity(
                context,
                0,
                new Intent(context, MainActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Account sync running")
                .setContentText(accountName + " hit onPerformSync")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Account: " + accountName
                                + "\nDepth remaining: " + depthRemaining
                                + "\nSource: " + chainSource))
                .setContentIntent(contentIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(nextNotificationId(), builder.build());
        }
    }

    private static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }

        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription(CHANNEL_DESCRIPTION);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(channel);
        }
    }

    private static boolean canPostNotifications(Context context) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU
                || context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED;
    }

    private static int nextNotificationId() {
        int notificationId = NEXT_NOTIFICATION_ID.getAndIncrement();
        if (notificationId == Integer.MAX_VALUE) {
            NEXT_NOTIFICATION_ID.set(NOTIFICATION_ID_START);
        }
        return notificationId;
    }
}
