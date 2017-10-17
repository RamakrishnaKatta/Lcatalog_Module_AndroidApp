package com.lucidleanlabs.dev.lcatalog_module;

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

import com.lucidleanlabs.dev.lcatalog_module.AR.ARNativeActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;


public class Fragment_ProductImages extends Fragment {

    private static final String TAG = "Fragment_ProductImages";

    private static String FILE_URL = "http://lcatalog.immersionslabs.com:8080/models/";
    private static String EXTENDED_URL;

    LinearLayout note;
    ImageButton article_download, article_3d_view, article_augment;

    String article_images;
    // article_images is split in to five parts and assigned to each string
    String image1, image2, image3, image4, image5;

    String article_name, article_id;

    private ViewPager ArticleViewPager;
    private LinearLayout Slider_dots;
    ImageSliderAdapter imagesliderAdapter;
    ArrayList<String> slider_images = new ArrayList<>();
    TextView[] dots;
    int page_position = 0;
    private boolean success = true;


    String Article_ZipFileLocation, Article_ExtractLocation, Article_3DSFileLocation;
    private boolean zip_downloaded = true;
    File zip_file, object_3d_file;

    public Fragment_ProductImages() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_product_images, container, false);

        article_download =  view.findViewById(R.id.article_download_icon);
        article_3d_view =  view.findViewById(R.id.article_3dview_icon);
        article_augment =  view.findViewById(R.id.article_augment_icon);

        article_images = getArguments().getString("article_images");
        article_name = getArguments().getString("article_name");
        article_id = getArguments().getString("article_id");

        try {
            JSONObject image_json = new JSONObject(article_images);

            image1 = image_json.getString("image1");
            image2 = image_json.getString("image2");
            image3 = image_json.getString("image3");
            image4 = image_json.getString("image4");
            image5 = image_json.getString("image5");


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

//                int[] colorsActive = view.getResources().getIntArray(R.array.array_dot_active);
//                int[] colorsInactive = view.getResources().getIntArray(R.array.array_dot_inactive);

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

        Article_ZipFileLocation = Environment.getExternalStorageDirectory() + "/L_CATALOG_MOD/Models/" + article_name + "/" + article_id + ".zip";
        Log.e(TAG, "ZipFileLocation--" + Article_ZipFileLocation);
        Article_ExtractLocation = Environment.getExternalStorageDirectory() + "/L_CATALOG_MOD/Models/" + article_name + "/";
        Log.e(TAG, "ExtractLocation--" + Article_ExtractLocation);
        Article_3DSFileLocation = Environment.getExternalStorageDirectory() + "/L_CATALOG_MOD/Models/" + article_name + "/article_view.3ds";
        Log.e(TAG, "Object3DFileLocation--" + Article_3DSFileLocation);

        note = (LinearLayout) view.findViewById(R.id.download_note);

        zip_file = new File(Article_ZipFileLocation);
        object_3d_file = new File(Article_3DSFileLocation);

        zip_downloaded = false;

        article_3d_view.setEnabled(false);
        if (object_3d_file.exists()) {
            article_3d_view.setEnabled(true);
            article_download.setVisibility(View.GONE);
            note.setVisibility(View.GONE);
            zip_downloaded = true;
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
                                    EXTENDED_URL = FILE_URL + article_id + ".zip";
                                    Log.e(TAG, "URL ---------- " + EXTENDED_URL);
                                    new DownloadManager(EXTENDED_URL, article_name, article_id);

                                    if (zip_file.exists()) {
                                        new UnzipUtil(Article_ZipFileLocation, Article_ExtractLocation);
                                    } else {
                                        Toast.makeText(getContext(), "Failed to download the Files", Toast.LENGTH_SHORT).show();
                                    }

                                    zip_downloaded = true;
                                    Log.e(TAG, "Zip Downloaded ---------- " + zip_downloaded);
                                    progressDialog.dismiss();
                                    article_download.setVisibility(View.GONE);
                                    article_3d_view.setEnabled(true);
                                    note.setVisibility(View.GONE);

                                } catch (IOException e) {
                                    article_download.setVisibility(View.VISIBLE);
                                    article_3d_view.setEnabled(false);
                                    zip_downloaded = false;
                                    Log.e(TAG, "Zip Not Downloaded ---------- " + zip_downloaded);
                                    e.printStackTrace();
                                    note.setVisibility(View.VISIBLE);
                                }
                            }
                        }, 6000);
            }
        });

        CreateFolderStructure();


        article_3d_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (zip_downloaded) {

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

//        addBottomDots(0);

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


    private void CreateFolderStructure() {
        String root_Path = Environment.getExternalStorageDirectory().toString() + "//L_CATALOGUE";
        String models_Path = Environment.getExternalStorageDirectory().toString() + "//L_CATALOGUE/Models";
        String screenshots_Path = Environment.getExternalStorageDirectory().toString() + "//L_CATALOGUE/Screenshots";
        String cache_Path = Environment.getExternalStorageDirectory().toString() + "//L_CATALOGUE/cache";

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

        }

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

        Activity activity;
        if (context instanceof Activity) {
            activity = (Activity) context;
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
