package com.example.mobiletranslator;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;

import com.example.mobiletranslator.ui.NotificationUtility;

import java.io.File;

public final class LocalDataManager {
    private final File filesDir;
    private final DownloadManager downloadManager;
    private final Context context;
    private final String ocrDownloadUrl;

    public static final String OCR_FOLDER = "tessdata";
    public static final String SQL_LANGUAGE_DATA_FILE = "languages.xml";

    public LocalDataManager(Context context){
        this.context = context;
        filesDir = context.getExternalFilesDir(null);
        downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        ocrDownloadUrl = context.getResources().getString(R.string.ocr_trained_data_url_best);
    }

    public String getFileDirPath(){ return filesDir.getAbsolutePath(); }

    public Uri getOcrDownloadUri(){ return Uri.parse(ocrDownloadUrl); }

    public boolean checkFile(String subfolder, String filename){
        return new File(filesDir+"/"+subfolder+"/"+filename).exists();
    }

    public void downloadFile(String filename, String subfolder, Uri uri) throws AppException{
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
            downloadManager.enqueue(request);
        }
        else{
            throw new AppException(NotificationUtility.CauseCode.UNABLE_TO_WRITE);
        }
    }

    public void downloadFileConfirmDialog(String filename, String subfolder, Uri uri, Activity activity){
        final Resources resources = activity.getResources();

        NotificationUtility.displayConfirmDialog(
                activity,
                resources.getString(R.string.dialog_confirm_download_generic),
                (dialogInterface, i) -> {
            try {
                downloadFile(filename, subfolder, uri);
            } catch (AppException e) {
                NotificationUtility.displayMessage(activity, e);
            }
        });
    }

    public void downloadFileConfirmDialog(String filename, String subfolder, Uri uri, Activity activity, String message){
        NotificationUtility.displayConfirmDialog(
            activity,
            message,
            (dialogInterface, i) -> {
                try {
                    downloadFile(filename, subfolder, uri);
                } catch (AppException e) {
                    NotificationUtility.displayMessage(activity, e);
                }
            });
    }

    public boolean deleteFile(String subfolder, String filename){
        File file = new File(filesDir+"/"+subfolder, filename);
        return file.delete();
    }

    public void deleteFile(String subfolder, String filename, Activity activity){
        final File file = new File(filesDir+"/"+subfolder, filename);
        final Resources resources = activity.getResources();
        NotificationUtility.displayConfirmDialog(
                activity,
                resources.getString(R.string.dialog_confirm_delete_generic),
                (dialogInterface, i) -> file.delete());
    }
}
