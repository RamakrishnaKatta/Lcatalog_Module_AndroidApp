package com.lucidleanlabs.dev.lcatalogmod.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lucidleanlabs.dev.lcatalogmod.ProductPageActivity;
import com.lucidleanlabs.dev.lcatalogmod.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class GridViewAdapter extends RecyclerView.Adapter<GridViewAdapter.ViewHolder> {

    private static final String TAG = "GridViewAdapter";

    private Activity activity;

    private ArrayList<String> item_ids;
    private ArrayList<String> item_names;
    private ArrayList<String> item_descriptions;
    private ArrayList<String> item_prices;
    private ArrayList<String> item_discounts;
    private ArrayList<String> item_vendors;
    private ArrayList<String> item_images;
    private ArrayList<String> item_dimensions;
    private ArrayList<String> item_3ds;

    public GridViewAdapter(Activity activity,
                           ArrayList<String> item_ids,
                           ArrayList<String> item_names,
                           ArrayList<String> item_descriptions,
                           ArrayList<String> item_prices,
                           ArrayList<String> item_discounts,
                           ArrayList<String> item_vendors,
                           ArrayList<String> item_images,
                           ArrayList<String> item_dimensions,
                           ArrayList<String> item_3ds) {
        this.item_ids = item_ids;
        this.item_names = item_names;
        this.item_descriptions = item_descriptions;
        this.item_prices = item_prices;
        this.item_discounts = item_discounts;
        this.item_vendors = item_vendors;
        this.item_images = item_images;
        this.item_dimensions = item_dimensions;
        this.item_3ds = item_3ds;

        Log.e(TAG, "ids----" + item_ids);
        Log.e(TAG, "names----" + item_names);
        Log.e(TAG, "descriptions----" + item_descriptions);
        Log.e(TAG, "prices----" + item_prices);
        Log.e(TAG, "discounts----" + item_discounts);
        Log.e(TAG, "vendors----" + item_vendors);
        Log.e(TAG, "Images----" + item_images);
        Log.e(TAG, "Dimensions----" + item_dimensions);
        Log.e(TAG, "GridViewAdapter: 3ds---" + item_3ds);

        this.activity = activity;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.item_grid, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GridViewAdapter.ViewHolder viewHolder, final int position) {
        final Context[] context = new Context[1];

        viewHolder.item_image.setImageResource(R.drawable.dummy_icon);

        String im1 = null;
        String get_image = item_images.get(position);
        try {
            JSONArray images_json = new JSONArray(get_image);
            for (int i = 0; i < images_json.length(); i++) {
                im1 = images_json.getString(0);
                Log.e(TAG, "image1 >>>>" + im1);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Glide.with(activity).load("http://35.154.150.204:4000/upload/images/" + im1)
                .placeholder(R.drawable.dummy_icon)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(viewHolder.item_image);

        Integer x = Integer.parseInt(item_prices.get(position));
        Integer y = Integer.parseInt(item_discounts.get(position));
        Integer z = (x * (100 - y)) / 100;
        String itemNewPrice = Integer.toString(z);

        viewHolder.item_name.setText(item_names.get(position));
        viewHolder.item_description.setText(item_descriptions.get(position));
        viewHolder.item_price.setText((Html.fromHtml("<strike>" + item_prices.get(position) + "</strike>")));
        viewHolder.item_price.setPaintFlags(viewHolder.item_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        viewHolder.item_discount.setText(item_discounts.get(position));
        viewHolder.item_price_new.setText(itemNewPrice);

        viewHolder.grid_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                context[0] = v.getContext();

                Intent intent = new Intent(context[0], ProductPageActivity.class);
                Bundle b = new Bundle();

                b.putString("article_id", item_ids.get(position));
                b.putString("article_title", item_names.get(position));
                b.putString("article_description", item_descriptions.get(position));
                b.putString("article_price", item_prices.get(position));
                b.putString("article_discount", item_discounts.get(position));
                b.putString("article_vendor", item_vendors.get(position));
                b.putString("article_dimensions", item_dimensions.get(position));
                b.putString("article_images", item_images.get(position));
                b.putString("article_3ds", item_3ds.get(position));
                b.putString("article_position", String.valueOf(position));

                intent.putExtras(b);

                context[0].startActivity(intent);

//                Toast.makeText(activity, "You clicked on Article: " + position, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return item_names.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView item_name, item_description, item_price, item_discount, item_price_new;
        private ImageView item_image;
        private RelativeLayout grid_container;

        ViewHolder(View view) {
            super(view);
            grid_container = view.findViewById(R.id.grid_container);
            item_image = view.findViewById(R.id.grid_item_image);
            item_name = view.findViewById(R.id.grid_item_name);
            item_description = view.findViewById(R.id.grid_item_description);
            item_price = view.findViewById(R.id.grid_item_price);
            item_discount = view.findViewById(R.id.grid_item_discount_value);
            item_price_new = view.findViewById(R.id.grid_item_price_new);

        }
    }
}
