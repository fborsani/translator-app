package com.example.mobiletranslator;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;

import com.example.mobiletranslator.db.DbManager;

import java.io.File;

public class LocalDataManager {
    private final File filesDir;
    private final DownloadManager downloadManager;
    private final Context context;
    private final DbManager dbm;

    public static final String OCR_FOLDER = "tessdata";

    public LocalDataManager(Context context){
        this.context = context;
        dbm = new DbManager(context);
        filesDir = context.getExternalFilesDir(null);
        downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
    }

    public String getFileDirPath(){ return filesDir.getAbsolutePath(); }

    public File getFile(String subfolder, String filename){
        return new File(filesDir+"/"+subfolder+"/"+filename);
    }

    public Uri getDownloadedFile(long downloadId){
        return downloadManager.getUriForDownloadedFile(downloadId);
    }

    public long downloadFile(String filename, String subfolder, Uri uri) throws AppException{
        File destDir = new File(filesDir, subfolder);
        boolean mkdirResult = true;

        if (!destDir.exists()) {
            mkdirResult = destDir.mkdir();
        }

        if(mkdirResult) {
            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setDescription("Downloading file");
            request.setTitle("Downloading File");
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalFilesDir(context, subfolder, filename);
            return downloadManager.enqueue(request);
        }
        else{
            throw new AppException("Write Error");
        }
    }

    public long downloadOcrFile(String langIso3) throws AppException{
        String filename = dbm.getOcrFile(langIso3);
        String siteUrl = dbm.getOcrDownloadUrl();
        return downloadFile(filename, OCR_FOLDER, Uri.parse(siteUrl));
    }
}
