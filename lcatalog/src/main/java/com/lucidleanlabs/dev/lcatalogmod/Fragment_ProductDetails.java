package com.lucidleanlabs.dev.lcatalogmod;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class Fragment_ProductDetails extends Fragment {

    private static final String TAG = "Fragment_ProductDetails";

    private static final String REGISTER_URL = "http://35.154.150.204:4000/vendorArticles/by?id=";
    private static String VENDOR_URL = null;

    //String Values assigned from the Bundle Arguments
    String a_title, a_description, a_new_price, a_discount, a_old_price, a_width, a_height, a_length, a_dimensions, a_vendor_id;

    //String Values assigned from the JSON Object using Vendor ID
    String vendor_name, vendor_address, vendor_image, vendor_id;

    TextView article_title, article_description, article_old_price, article_discount, article_width, article_height, article_length, article_new_price;
    TextView article_vendor_name, article_vendor_location;
    ImageView article_vendor_logo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_product_details, container, false);

        article_title = view.findViewById(R.id.article_title_text);
        article_description = view.findViewById(R.id.article_description_text);
        article_old_price = view.findViewById(R.id.article_price_value);
        article_discount = view.findViewById(R.id.article_price_discount_value);
        article_width = view.findViewById(R.id.article_width_text);
        article_height = view.findViewById(R.id.article_height_text);
        article_length = view.findViewById(R.id.article_length_text);
        article_new_price = view.findViewById(R.id.article_price_value_new);

        article_vendor_name = view.findViewById(R.id.article_vendor_text);
        article_vendor_location = view.findViewById(R.id.article_vendor_address_text);

        article_vendor_logo = view.findViewById(R.id.article_vendor_logo);

        a_title = getArguments().getString("article_title");
        Log.e(TAG, "--" + a_title);
        article_title.setText(a_title);

        a_description = getArguments().getString("article_description");
        Log.e(TAG, "--" + a_description);
        article_description.setText(a_description);

        a_old_price = getArguments().getString("article_old_price");
        Log.e(TAG, "--" + a_old_price);
        article_old_price.setText(Html.fromHtml("<strike>" + (a_old_price) + "</strike>"));
        article_old_price.setPaintFlags(article_old_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        a_discount = getArguments().getString("article_discount");
        Log.e(TAG, "--" + a_discount);
        article_discount.setText(a_discount);

        a_new_price = getArguments().getString("article_new_price");
        Log.e(TAG, "--" + a_new_price);
        article_new_price.setText(a_new_price);

        a_dimensions = getArguments().getString("article_dimensions");
        a_width = getArguments().getString("article_width");
        a_height = getArguments().getString("article_height");
        a_length = getArguments().getString("article_length");
        Log.e(TAG, "--" + a_width + "--" + a_height + "--" + a_length);
        article_width.setText(a_width);
        article_height.setText(a_height);
        article_length.setText(a_length);

        a_vendor_id = getArguments().getString("article_vendor_id");
        Log.e(TAG, "--" + a_vendor_id);

        VENDOR_URL = REGISTER_URL + a_vendor_id;
        Log.e(TAG, "VENDOR_URL--" + VENDOR_URL);

        try {
            getVendorData();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return view;
    }

    private void getVendorData() throws JSONException {

        final JSONObject baseclass = new JSONObject();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, VENDOR_URL, baseclass, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.e(TAG, "response--" + response);

                try {
                    JSONObject resp = response.getJSONObject("resp");
                    vendor_id = resp.getString("id");
                    vendor_name = resp.getString("name");
                    vendor_address = resp.getString("code");
                    vendor_image = resp.getString("logo");

                    Log.e(TAG, "Article Vendor ID--" + vendor_id);
                    Log.e(TAG, "Article Vendor Name--" + vendor_name);
                    Log.e(TAG, "Article Vendor Address--" + vendor_address);
                    Log.e(TAG, "Article Vendor Image--" + vendor_image);

                    article_vendor_name.setText(vendor_name);
                    article_vendor_location.setText(vendor_address);
                    new DownloadImageTask(article_vendor_logo).execute(vendor_image);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), error.toString(), Toast.LENGTH_SHORT).show();
                NetworkResponse response = error.networkResponse;
                if (error instanceof ServerError && response != null) {
                    try {
                        String res = new String(response.data, HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                        // Now you can use any deserializer to make sense of data
                        JSONObject request = new JSONObject(res);
                        Log.e(TAG, "request--" + request);
                    } catch (UnsupportedEncodingException | JSONException e1) {
                        // Couldn't properly decode data to string
                        e1.printStackTrace();
                    }
                }
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(jsonObjectRequest);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity) {
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
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
