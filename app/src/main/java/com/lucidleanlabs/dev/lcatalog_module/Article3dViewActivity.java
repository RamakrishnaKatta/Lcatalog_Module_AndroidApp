package com.lucidleanlabs.dev.lcatalog_module;


import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import com.lucidleanlabs.dev.lcatalog_module.loader3ds.MyGLRenderer;
import com.lucidleanlabs.dev.lcatalog_module.loader3ds.MyGLSurfaceView;


public class Article3dViewActivity extends AppCompatActivity {

    private static final String TAG = "Article3dViewActivity";

    private MyGLSurfaceView mGLView;
    private MyGLRenderer mRenderer;
    private SeekBar scaleBar;
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article3d_view);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_3dview);
        setSupportActionBar(toolbar);

        Bundle b3 = getIntent().getExtras();
        name = (String) b3.getCharSequence("article_name");
        Log.e(TAG, "Name ---- " + name);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_3dView);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();

                if (scaleBar.getVisibility() == View.INVISIBLE) {
                    scaleBar.setVisibility(View.VISIBLE);
                } else if (scaleBar.getVisibility() == View.VISIBLE) {
                    scaleBar.setVisibility(View.INVISIBLE);
                }
            }
        });

        mGLView = (MyGLSurfaceView) findViewById(R.id.glView);

        // Check if the system supports OpenGL ES 2.0.
        final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;

        if (supportsEs2) {
            // Request an OpenGL ES 2.0 compatible context.
            mGLView.setEGLContextClientVersion(2);

            final DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

            // Set the renderer for the GLSurfaceView
            mRenderer = new MyGLRenderer(this, name);
            mGLView.setRenderer(mRenderer, displayMetrics.density);

        } else {
            // Show error message, if the device is not OpenGL ES 2.0 compatible
            Toast.makeText(this, "OpenGL ES 2.0 is not supported on this device", Toast.LENGTH_LONG).show();

            return;
        }

        //Create a seek bar for scaling
        scaleBar = (SeekBar) findViewById(R.id.seekbar1);

        if (scaleBar != null) {
            //Set the bar's event listener, which will update the scale factor everytime, the value is changed
            scaleBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                int progressChanged = 0;

                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    progressChanged = progress;
                    //Turn the integer value to a percentage value
                    mRenderer.changeScale(progressChanged / 100.0f);
                }

                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
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
