package com.lucidleanlabs.dev.lcatalog_module.AR;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.lucidleanlabs.dev.lcatalog_module.R;

import org.artoolkit.ar.base.ARActivity;
import org.artoolkit.ar.base.assets.AssetHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Timestamp;

import static com.lucidleanlabs.dev.lcatalog_module.AR.ARNativeApplication.getInstance;

public class ARNativeActivity extends ARActivity {

    private ARNativeRenderer arNativeRenderer = new ARNativeRenderer();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arnative);
        initializeInstance();
    }

    public void onStop() {
        ARNativeRenderer.demoShutdown();
        super.onStop();
    }

    @Override
    protected ARNativeRenderer supplyRenderer() {
        return arNativeRenderer;
    }

    @Override
    protected FrameLayout supplyFrameLayout() {
        return (FrameLayout) this.findViewById(R.id.arFrameLayout);

    }

    // Here we do one-off initialisation which should apply to all activities
    // in the application.
    protected void initializeInstance() {

        // Unpack assets to cache directory so native library can read them.
        // N.B.: If contents of assets folder changes, be sure to increment the
        // versionCode integer in the AndroidManifest.xml file.
        AssetHelper assetHelper = new AssetHelper(getAssets());
        assetHelper.cacheAssetFolder(getInstance(), "Data");
    }

}
