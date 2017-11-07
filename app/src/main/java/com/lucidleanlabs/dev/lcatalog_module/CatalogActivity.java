package com.lucidleanlabs.dev.lcatalog_module;

import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
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
import com.lucidleanlabs.dev.lcatalog_module.adapters.GridViewAdapter;
import com.lucidleanlabs.dev.lcatalog_module.adapters.ListViewHorizontalAdapter;
import com.lucidleanlabs.dev.lcatalog_module.adapters.ListViewVerticalAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class CatalogActivity extends AppCompatActivity {
    private static final String REGISTER_URL = "http://lcatalog.immersionslabs.com:8080/lll/web/article/all?from=0&count=50";
    private static final String TAG = "CatalogActivity";
//    from=0&count=10

    private ArrayList<String> item_names;
    private ArrayList<String> item_descriptions;
    private ArrayList<String> item_prices;
    private ArrayList<String> item_discounts;
    private ArrayList<String> item_vendors;
    private ArrayList<String> item_images;
    private ArrayList<String> item_dimensions;
    private ArrayList<String> item_ids;

    GridViewAdapter gridAdapter;
    ListViewVerticalAdapter VerticalAdapter;
    ListViewHorizontalAdapter horizontalAdapter;

    RecyclerView recycler;
    ProgressBar progressBar;
    GridLayoutManager GridManager;
    LinearLayoutManager HorizontalManager, VerticalManager;
    //    Boolean loading = false, refreshStatus = false;
    SwipeRefreshLayout refreshLayout;
    FloatingActionButton fab_grid, fab_vertical, fab_horizontal;
    Boolean Loadmore = false;
//    int to = 0;
//    int from = 0;
//    int pagesize = 10;
//    int pagenumber = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        fab_grid = findViewById(R.id.fab_grid_list);
        fab_vertical = findViewById(R.id.fab_vertical_list);
        fab_horizontal = findViewById(R.id.fab_horizontal_list);

        recycler = findViewById(R.id.recycler);
        recycler.setHasFixedSize(true);
        recycler.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        HorizontalManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        VerticalManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        GridManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);

        refreshLayout = findViewById(R.id.swipe_refresh);
        progressBar = findViewById(R.id.progress_grid);

        Toolbar toolbar = findViewById(R.id.toolbar_catalog);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        item_names = new ArrayList<>();
        item_descriptions = new ArrayList<>();
        item_prices = new ArrayList<>();
        item_discounts = new ArrayList<>();
        item_vendors = new ArrayList<>();
        item_images = new ArrayList<>();
        item_dimensions = new ArrayList<>();
        item_ids = new ArrayList<>();

        fab_vertical.setSize(1);
        fab_horizontal.setSize(1);
        fab_grid.setSize(0);

        commonGetdata();

        /*Floating Button for Gridview*/
        fab_grid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fab_vertical.setSize(1);
                fab_horizontal.setSize(1);
                fab_grid.setSize(0);
                recycler.setLayoutManager(GridManager);
                recycler.setAdapter(gridAdapter);
            }
        });

        /*Floating Button for Vertical Listview*/
        fab_vertical.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fab_vertical.setSize(0);
                fab_horizontal.setSize(1);
                fab_grid.setSize(1);
                recycler.setLayoutManager(VerticalManager);
                recycler.setAdapter(VerticalAdapter);
            }
        });

        /*Floating Button for Horizontal Listview*/
        fab_horizontal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fab_vertical.setSize(1);
                fab_horizontal.setSize(0);
                fab_grid.setSize(1);
                recycler.setLayoutManager(HorizontalManager);
                recycler.setAdapter(horizontalAdapter);
            }
        });

        checkInternetConnection();
        if (NetworkConnectivity.checkInternetConnection(CatalogActivity.this)) {
        } else {
            InternetMessage();
        }
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (NetworkConnectivity.checkInternetConnection(CatalogActivity.this)) {
                    refreshLayout.setRefreshing(true);

                    item_ids.clear();
                    item_names.clear();
                    item_descriptions.clear();
                    item_prices.clear();
                    item_discounts.clear();
                    item_vendors.clear();
                    item_images.clear();
                    item_dimensions.clear();

                    commonGetdata();
                }
            }
        });
    }

    private void commonGetdata() {
        Log.e(TAG, "commonGetdata: " + REGISTER_URL);
        final JSONObject baseclass = new JSONObject();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, REGISTER_URL, baseclass, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.e(TAG, "response--" + response);

                if (refreshLayout.isRefreshing()) {
                    refreshLayout.setRefreshing(false);
                }
                try {
                    JSONArray resp = response.getJSONArray("resp");
                    if (!Loadmore) {
                        CatalogView(resp);
                    } else {

                        for (int i = 0; i < resp.length(); i++) {
                            JSONObject obj = null;
                            try {
                                obj = resp.getJSONObject(i);

                                item_ids.add(obj.getString("id"));
                                item_names.add(obj.getString("name"));
                                item_descriptions.add(obj.getString("description"));
                                item_prices.add(obj.getString("price"));
                                item_discounts.add(obj.getString("discount"));
                                item_vendors.add(obj.getString("vendorId"));
                                item_images.add(obj.getString("images"));
                                item_dimensions.add(obj.getString("dimension"));

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        if (fab_vertical.getSize() == 1 && fab_horizontal.getSize() == 1 && fab_grid.getSize() == 0) {
                            gridAdapter.notifyDataSetChanged();
                        } else if (fab_vertical.getSize() == 0 && fab_horizontal.getSize() == 1 && fab_grid.getSize() == 1) {
                            VerticalAdapter.notifyDataSetChanged();
                        } else if (fab_vertical.getSize() == 1 && fab_horizontal.getSize() == 0 && fab_grid.getSize() == 1) {
                            horizontalAdapter.notifyDataSetChanged();
                        }


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(CatalogActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
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
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }

    /*Internet message for Network connectivity*/
    private boolean checkInternetConnection() {
        // get Connectivity Manager object to check connection
        ConnectivityManager connec =
                (ConnectivityManager) getSystemService(getBaseContext().CONNECTIVITY_SERVICE);

        // Check for network connections
        if (connec.getActiveNetworkInfo().getState() == android.net.NetworkInfo.State.CONNECTED ||
                connec.getActiveNetworkInfo().getState() == android.net.NetworkInfo.State.CONNECTING) {

            // if connected with internet
            return true;

        } else if (
                connec.getActiveNetworkInfo().getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                        connec.getActiveNetworkInfo().getState() == android.net.NetworkInfo.State.DISCONNECTED) {

            Toast.makeText(this, " Internet Not Available  ", Toast.LENGTH_LONG).show();
            return false;
        }
        return false;
    }

    private void InternetMessage() {
        final View view = this.getWindow().getDecorView().findViewById(android.R.id.content);
        final Snackbar snackbar = Snackbar.make(view, "Check Your Internet connection", Snackbar.LENGTH_INDEFINITE);
        snackbar.setActionTextColor(getResources().getColor(R.color.red));
        snackbar.setAction("RETRY", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
                if (NetworkConnectivity.checkInternetConnection(CatalogActivity.this)) {
                } else {
                    InternetMessage();
                }
            }
        });
        snackbar.show();
    }

    /*Adapter class for GridView/ListViewHorizontal/ListViewVertical Adapter*/
    public void CatalogView(JSONArray g_jsonArray) {

        for (int i = 0; i < g_jsonArray.length(); i++) {
            JSONObject obj = null;
            try {
                obj = g_jsonArray.getJSONObject(i);

                item_ids.add(obj.getString("id"));
                item_names.add(obj.getString("name"));
                item_descriptions.add(obj.getString("description"));
                item_prices.add(obj.getString("price"));
                item_discounts.add(obj.getString("discount"));
                item_vendors.add(obj.getString("vendorId"));
                item_images.add(obj.getString("images"));
                item_dimensions.add(obj.getString("dimension"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Log.e(TAG, "ids******" + item_ids);
        Log.e(TAG, "names******" + item_names);
        Log.e(TAG, "descriptions******" + item_descriptions);
        Log.e(TAG, "prices******" + item_prices);
        Log.e(TAG, "discounts******" + item_discounts);
        Log.e(TAG, "vendors******" + item_vendors);
        Log.e(TAG, "images******" + item_images);
        Log.e(TAG, "dimensions******" + item_dimensions);

        gridAdapter = new GridViewAdapter(this, item_ids, item_names, item_descriptions, item_prices, item_discounts, item_vendors, item_images, item_dimensions);
        horizontalAdapter = new ListViewHorizontalAdapter(this, item_ids, item_names, item_descriptions, item_prices, item_discounts, item_vendors, item_images, item_dimensions);
        VerticalAdapter = new ListViewVerticalAdapter(this, item_ids, item_names, item_descriptions, item_prices, item_discounts, item_vendors, item_images, item_dimensions);

        if (fab_vertical.getSize() == 1 && fab_horizontal.getSize() == 1 && fab_grid.getSize() == 0) {
            recycler.removeAllViews();
            recycler.setLayoutManager(GridManager);
            recycler.setAdapter(gridAdapter);

        } else if (fab_vertical.getSize() == 0 && fab_horizontal.getSize() == 1 && fab_grid.getSize() == 1) {
            recycler.removeAllViews();
            recycler.setLayoutManager(VerticalManager);
            recycler.setAdapter(VerticalAdapter);

        } else if (fab_vertical.getSize() == 1 && fab_horizontal.getSize() == 0 && fab_grid.getSize() == 1) {
            recycler.removeAllViews();
            recycler.setLayoutManager(HorizontalManager);
            recycler.setAdapter(horizontalAdapter);
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
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
        finish();

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
