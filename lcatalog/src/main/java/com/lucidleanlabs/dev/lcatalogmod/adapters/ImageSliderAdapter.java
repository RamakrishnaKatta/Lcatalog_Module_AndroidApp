package com.lucidleanlabs.dev.lcatalogmod.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.lucidleanlabs.dev.lcatalogmod.R;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class ImageSliderAdapter extends PagerAdapter {

   public  static final  String TAG = "ImageSliderAdapter";

    private ArrayList<String> Images;
    private LayoutInflater inflater;
    private Context context;

    public ImageSliderAdapter(Context context, ArrayList<String> Images) {
        this.context = context;
        this.Images = Images;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return Images.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.fragment_product_images, container, false);
        ImageView images = v.findViewById(R.id.article_image_view);
        Bitmap b = download_images(Images.get(position));
        images.setImageBitmap(b);
        container.addView(v);
        return v;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.invalidate();
    }

    private Bitmap download_images(String urls) {
        String urldisplay = "http://35.154.150.204:4000/upload/images/" + urls;
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
}
