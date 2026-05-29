package com.mokardder.AlwaysActive.service.QSTile;


import android.service.quicksettings.TileService;
import android.widget.Toast;

public class MyTileService extends TileService {

    // Called when user adds the tile
    @Override
    public void onTileAdded() {
        super.onTileAdded();

        // Show success message
        Toast.makeText(
                getApplicationContext(),
                "Tile Added Successfully",
                Toast.LENGTH_LONG
        ).show();
    }

    // Called when Quick Settings panel becomes visible
    @Override
    public void onStartListening() {
        super.onStartListening();

        // Show tile ready message
        Toast.makeText(
                this,
                "Quick Tile Ready",
                Toast.LENGTH_SHORT
        ).show();
    }
}