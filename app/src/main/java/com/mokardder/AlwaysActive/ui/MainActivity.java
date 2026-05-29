package com.mokardder.AlwaysActive.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.app.Activity;
import android.content.Intent;
import android.net.VpnService;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import androidx.appcompat.app.AppCompatActivity;

import com.mokardder.AlwaysActive.databinding.ActivityMainBinding;
import com.mokardder.AlwaysActive.service.Sync.AccountHelper;
import com.mokardder.AlwaysActive.service.VPN.MyVpnService;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private final ActivityResultLauncher<String> notificationPermissionLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(),
                    isGranted -> {
                        if (!isGranted) {
                            Toast.makeText(
                                            this,
                                            "Notifications disabled for sync updates",
                                            Toast.LENGTH_SHORT)
                                    .show();
                        }
                    });

    private final ActivityResultLauncher<Intent> vpnPermissionLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            startEmptyVpnService();
                        } else {
                            Toast.makeText(this, "VPN permission denied", Toast.LENGTH_SHORT)
                                    .show();
                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        requestNotificationPermissionIfNeeded();

        binding.btnVPN.setOnClickListener(
                v -> {
                    showDialog(
                            "VPN Permission",
                            "This app needs VPN access for Always-on mode. After Enabling Vpn Goto Connection-> VPN -> ✓ Tick Always-on",
                            "Grant",
                            this::checkAndRequestVpnPermission);
                });
                
        binding.btnTile.setOnClickListener(
                v -> {
                    showDialog(
                            "QSTile Service",
                            "Enable Tile Icon for this app from Notification Edit Tile, and it will start listen whenever user slide down notification shade. Enable Notification-> Edit Tile -> Add 'QSAlways' -> ✓ Add",
                            "Grant",
                            null);
                });

        binding.btnSync.setOnClickListener(
                v -> {
                    AccountHelper.startChainedSync(this);
                    Toast.makeText(
                                    this,
                                    "Account sync chain started with depth 10",
                                    Toast.LENGTH_SHORT)
                            .show();
                });
    }

    private void requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
        }
    }

    private void showDialog(
            String title, String message, String positiveBtnText, Runnable onPositiveClick) {

        new MaterialAlertDialogBuilder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(
                        positiveBtnText,
                        (dialog, which) -> {
                            if (onPositiveClick != null) {
                                onPositiveClick.run();
                            }
                        })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .setCancelable(false)
                .show();
    }

    private void checkAndRequestVpnPermission() {

        Intent intent = VpnService.prepare(this);

        if (intent != null) {

            // Permission not granted yet
            vpnPermissionLauncher.launch(intent);

        } else {

            // Permission already granted
            startEmptyVpnService();
        }
    }

    private void startEmptyVpnService() {

        Intent vpnServiceIntent = new Intent(this, MyVpnService.class);

        startService(vpnServiceIntent);

        Toast.makeText(this, "VPN started. Enable Always-on", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(Settings.ACTION_VPN_SETTINGS);
        startActivity(intent);
    }
}
