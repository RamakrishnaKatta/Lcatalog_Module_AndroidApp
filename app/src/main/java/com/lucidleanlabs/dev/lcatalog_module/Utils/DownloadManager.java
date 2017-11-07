package com.lucidleanlabs.dev.lcatalog_module.Utils;

import android.os.Environment;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;


public class DownloadManager {

    private String DOWNLOAD_URL;
    private String Article_Name, Article_ID;

    public DownloadManager(String url, String article_name, String article_id) {

        DOWNLOAD_URL = url;
        Article_Name = article_name;
        Article_ID = article_id;
        try {
            Download();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void Download() throws FileNotFoundException, IOException {
        URL u = new URL(DOWNLOAD_URL);
        URLConnection conn = u.openConnection();
        int contentLength = conn.getContentLength();

        DataInputStream stream = new DataInputStream(u.openStream());

        byte[] buffer = new byte[contentLength];
        stream.readFully(buffer);
        stream.close();

        DataOutputStream file_out = new DataOutputStream(new FileOutputStream(Environment.getExternalStorageDirectory() + "/L_CATALOG_MOD/Models/" + Article_Name + "/" + Article_ID + ".zip"));
        file_out.write(buffer);
        file_out.flush();
        file_out.close();
    }
}
