package com.lucidleanlabs.dev.lcatalogmod;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.lucidleanlabs.dev.lcatalogmod.AR.ARNativeActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";


    private static final int MY_PERMISSIONS_REQUEST = 10;
    Button click, Augment;
    boolean success = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RequestPermissions();

        click = findViewById(R.id.catalog);
        click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CatalogActivity.class);
                startActivity(intent);
            }
        });

        Augment = findViewById(R.id.augment);
        Augment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent  intent = new Intent(MainActivity.this, ARNativeActivity.class);
                startActivity(intent);
            }
        });

        CreateFolderStructure();

    }

    private void CreateFolderStructure() {
        String root_Path = Environment.getExternalStorageDirectory().toString() + "//L_CATALOG_MOD";
        String models_Path = Environment.getExternalStorageDirectory().toString() + "//L_CATALOG_MOD/Models";
        String screenshots_Path = Environment.getExternalStorageDirectory().toString() + "//L_CATALOG_MOD/Screenshots";
        String cache_Path = Environment.getExternalStorageDirectory().toString() + "//L_CATALOG_MOD/cache";

        File Root_Folder, Models_Folder, Screenshots_Folder, Cache_Folder;

        if (Environment.getExternalStorageState().contains(Environment.MEDIA_MOUNTED)) {
            Root_Folder = new File(root_Path);
            Models_Folder = new File(models_Path);
            Screenshots_Folder = new File(screenshots_Path);
            Cache_Folder = new File(cache_Path);
        } else {
            Root_Folder = new File(root_Path);
            Models_Folder = new File(models_Path);
            Screenshots_Folder = new File(screenshots_Path);
            Cache_Folder = new File(cache_Path);
        }

        if (Root_Folder.exists()) {
        } else {

            if (!Root_Folder.exists()) {
                success = Root_Folder.mkdirs();
            }
            if (!Models_Folder.exists()) {
                success = Models_Folder.mkdirs();
            }
            if (!Screenshots_Folder.exists()) {
                success = Screenshots_Folder.mkdirs();
            }
            if (!Cache_Folder.exists()) {
                success = Cache_Folder.mkdirs();
            }
            if (success) {

            }
        }
    }


    private void RequestPermissions() {

        int PermissionCamera = ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.CAMERA);
        int PermissionReadStorage = ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
        int PermissionWriteStorage = ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        List<String> listPermissionsNeeded = new ArrayList<>();

        if (PermissionCamera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.CAMERA);
        }
        if (PermissionReadStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (PermissionWriteStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MY_PERMISSIONS_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        Log.e(TAG, "Permission callback called-------");
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST: {
                Map<String, Integer> perms = new HashMap<>();
                // Initialize the map with all three permissions
                perms.put(android.Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                perms.put(android.Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                // Fill with actual results from user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check all three permissions
                    if (perms.get(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                            && perms.get(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Log.e(TAG, "Camera and Storage(Read and Write) permission granted");
                        // Process the normal flow
                        // Else any one or both the permissions are not granted
                    } else {
                        Log.e(TAG, "Some permissions are not granted ask again ");

                        // Permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
                        // ShouldShowRequestPermissionRationale will return true
                        // Show the dialog or SnackBar saying its necessary and try again otherwise proceed with setup.

                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.CAMERA) ||
                                ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) ||
                                ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                            showDialogOK(
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    RequestPermissions();
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    // proceed with logic by disabling the related features or quit the app.
                                                    android.os.Process.killProcess(android.os.Process.myPid());
                                                    System.exit(0);
                                                    break;
                                            }
                                        }
                                    });
                        }
                        // permission is denied (and never ask again is checked)
                        // shouldShowRequestPermissionRationale will return false
                        else {
                            Toast.makeText(this, "Go to settings and enable permissions", Toast.LENGTH_LONG).show();
                            // Proceed with logic by disabling the related features or quit the app.
                        }
                    } // other 'case' lines to check for other permissions this app might request
                }
            }
        }
    }

    private void showDialogOK(DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage("Storage and Camera Services are Mandatory for this Application")
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }


}

