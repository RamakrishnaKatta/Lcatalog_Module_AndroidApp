package com.lucidleanlabs.dev.lcatalogmod;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lucidleanlabs.dev.lcatalogmod.AR.ARNativeActivity;
import com.lucidleanlabs.dev.lcatalogmod.Utils.DownloadManager;
import com.lucidleanlabs.dev.lcatalogmod.Utils.UnzipUtil;
import com.lucidleanlabs.dev.lcatalogmod.adapters.ImageSliderAdapter;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;


public class Fragment_ProductImages extends Fragment {

    private static final String TAG = "Fragment_ProductImages";

    private static String FILE_URL_3DS = "http://35.154.150.204:4000/upload/3dviewfiles/";
    private static String EXTENDED_URL_3DS;


    LinearLayout note;
    ImageButton  article_download, article_3d_view, article_augment;

    String article_images;
    // article_images is split in to five parts and assigned to each string
    String image1, image2, image3, image4, image5;

    String article_name, article_3ds;

    private ViewPager ArticleViewPager;
    private LinearLayout Slider_dots;
    ImageSliderAdapter imagesliderAdapter;
    ArrayList<String> slider_images = new ArrayList<>();
    TextView[] dots;
    int page_position = 0;


    String Article_3DS_ZipFileLocation, Article_3DS_ExtractLocation, Article_3DS_FileLocation;
    private boolean zip_3ds_downloaded = true;
    File article_3ds_zip_file, article_3ds_file;

    public Fragment_ProductImages() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_product_images, container, false);

        article_download = view.findViewById(R.id.article_download_icon);
        article_3d_view = view.findViewById(R.id.article_3dview_icon);
        article_augment = view.findViewById(R.id.article_augment_icon);

        article_images = getArguments().getString("article_images");
        article_name = getArguments().getString("article_name");
        article_3ds= getArguments().getString("article_3ds");

        Log.d(TAG, "onCreateView:3ds" + article_3ds);
        Log.d(TAG, "onCreateView:name" + article_name);

        try {

            JSONArray image_json = new JSONArray(article_images);
            for (int i = 0; i < image_json.length(); i++) {
                image1 = image_json.getString(0);
                image2 = image_json.getString(1);
                image3 = image_json.getString(2);
                image4 = image_json.getString(3);
                image5 = image_json.getString(4);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e(TAG, "Article Image 1----" + image1);
        Log.e(TAG, "Article Image 2----" + image2);
        Log.e(TAG, "Article Image 3----" + image3);
        Log.e(TAG, "Article Image 4----" + image4);
        Log.e(TAG, "Article Image 5----" + image5);

        final String[] Images = {image1, image2, image3, image4, image5};

        Collections.addAll(slider_images, Images);

        ArticleViewPager = view.findViewById(R.id.article_view_pager);
        imagesliderAdapter = new ImageSliderAdapter(getContext(), slider_images);
        ArticleViewPager.setAdapter(imagesliderAdapter);

        Slider_dots = view.findViewById(R.id.article_slider_dots);

        ArticleViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                addBottomDots(position);
            }

            private void addBottomDots(int currentPage) {

                dots = new TextView[slider_images.size()];

                Slider_dots.removeAllViews();

                for (int i = 0; i < dots.length; i++) {
                    dots[i] = new TextView(view.getContext());
                    dots[i].setText(Html.fromHtml("&#8226;"));
                    dots[i].setTextSize(35);
                    dots[i].setTextColor(Color.WHITE);
                    Slider_dots.addView(dots[i]);
                }

                if (dots.length > 0)
                    dots[currentPage].setTextColor(Color.parseColor("#004D40"));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        Article_3DS_ZipFileLocation = Environment.getExternalStorageDirectory() + "/L_CATALOG_MOD/Models/" + article_name + "/" + article_3ds;
        Log.e(TAG, "ZipFileLocation--" + Article_3DS_ZipFileLocation);
        Article_3DS_ExtractLocation = Environment.getExternalStorageDirectory() + "/L_CATALOG_MOD/Models/" + article_name + "/";
        Log.e(TAG, "ExtractLocation--" + Article_3DS_ExtractLocation);
        Article_3DS_FileLocation = Environment.getExternalStorageDirectory() + "/L_CATALOG_MOD/Models/" + article_name + "/article_view.3ds";
        Log.e(TAG, "Object3DFileLocation--" + Article_3DS_FileLocation);

        note = view.findViewById(R.id.download_note);

        article_3ds_zip_file = new File(Article_3DS_ZipFileLocation);
        article_3ds_file = new File(Article_3DS_FileLocation);

        zip_3ds_downloaded = false;

        article_3d_view.setEnabled(false);
        if (article_3ds_file.exists()) {
            article_3d_view.setEnabled(true);
            article_download.setVisibility(View.GONE);
            note.setVisibility(View.GONE);
            zip_3ds_downloaded = true;
        }

        article_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.AppTheme_Dark_Dialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Downloading Article, Just for once....");
                progressDialog.setTitle("Article Downloading");
                progressDialog.show();

                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                try {
                                    addModelFolder();
                                    EXTENDED_URL_3DS = FILE_URL_3DS + article_3ds;
                                    Log.e(TAG, "URL ---------- " + EXTENDED_URL_3DS);
                                    new DownloadManager(EXTENDED_URL_3DS, article_name, article_3ds);

                                    if (article_3ds_zip_file.exists()) {
                                        new UnzipUtil(Article_3DS_ZipFileLocation, Article_3DS_ExtractLocation);
                                    } else {
                                        Toast.makeText(getContext(), "Cannot locate Zipper, Try to download again", Toast.LENGTH_SHORT).show();
                                    }

                                    zip_3ds_downloaded = true;

                                    Log.e(TAG, "Zip Downloaded ---------- " + true);
                                    progressDialog.dismiss();
                                    article_download.setVisibility(View.GONE);
                                    article_3d_view.setEnabled(true);
                                    note.setVisibility(View.GONE);

                                } catch (IOException e) {
                                    article_download.setVisibility(View.VISIBLE);
                                    article_3d_view.setEnabled(false);
                                    zip_3ds_downloaded = false;
                                    Log.e(TAG, "Zip Not Downloaded ---------- " + false);
                                    e.printStackTrace();
                                    note.setVisibility(View.VISIBLE);
                                }
                            }
                        }, 6000);
            }
        });

        article_3d_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (zip_3ds_downloaded) {

                    Bundle b3 = new Bundle();
                    b3.putString("article_name", article_name);
                    Intent _3d_intent = new Intent(getContext(), Article3dViewActivity.class).putExtras(b3);
                    startActivity(_3d_intent);
                }
            }
        });


        article_augment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ARNativeActivity.class);
                startActivity(intent);
            }
        });


        final Handler handler = new Handler();
        final Runnable update = new Runnable() {
            @Override
            public void run() {
                if (page_position == slider_images.size()) {
                    page_position = 0;
                } else {
                    page_position = page_position + 1;
                }
                ArticleViewPager.setCurrentItem(page_position, true);
            }
        };

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(update);
            }
        }, 2000, 5000);


        return view;
    }




    /*creation of directory in external storage */
    private void addModelFolder() throws IOException {
        String state = Environment.getExternalStorageState();

        File folder = null;
        if (state.contains(Environment.MEDIA_MOUNTED)) {
            Log.e(TAG, "Article Name--" + article_name);
            folder = new File(Environment.getExternalStorageDirectory() + "/L_CATALOG_MOD/Models/" + article_name);
        }
        assert folder != null;
        if (!folder.exists()) {
            boolean wasSuccessful = folder.mkdirs();
            Log.e(TAG, "Model Directory is Created --- '" + wasSuccessful + "' Thank You !!");
        }
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
