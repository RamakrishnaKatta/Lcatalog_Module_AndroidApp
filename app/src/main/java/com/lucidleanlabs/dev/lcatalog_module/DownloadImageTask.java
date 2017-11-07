package com.lucidleanlabs.dev.lcatalog_module;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.URL;


public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    @SuppressLint("StaticFieldLeak")
    private ImageView bmImage;

    public DownloadImageTask(ImageView bmImage) {
        this.bmImage = bmImage;
    }

    protected Bitmap doInBackground(String... urls) {
        String urldisplay = "http://lcatalog.immersionslabs.com:8080" + urls[0];
        Bitmap mIcon = null;
        try {

            InputStream in = new URL(urldisplay).openStream();

            mIcon = BitmapFactory.decodeStream(in);

        } catch (Exception e) {

            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return mIcon;
    }

    protected void onPostExecute(Bitmap result) {
        if (result != null) {
            bmImage.setImageBitmap(result);
        }
    }

}