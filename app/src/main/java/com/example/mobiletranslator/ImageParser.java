package com.example.mobiletranslator;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.net.Uri;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;

public class ImageParser {
    private final TessBaseAPI tess;
    private final Context context;
    private boolean initialized;

    public ImageParser(Context context, String language){
        this.tess = new TessBaseAPI();
        this.context = context;
        String dataFolder = context.getFilesDir().getAbsolutePath();
        File tessDir = new File(dataFolder,"tessdata");
        if(!tessDir.exists()){
            tessDir.mkdir();
        }

        //TODO: edit file checks
        File engFile = new File(tessDir, "eng.traineddata");
        if (!engFile.exists()) {
            AssetManager am = context.getAssets();
            FileUtility.copyFile(am, "eng.traineddata", engFile);
        }

        initialized = tess.init(dataFolder,language);
        if(!initialized){
            tess.recycle();
        }
    }

    public boolean isInitialized(){
        return initialized;
    }

    public String parseImg(Bitmap img){
        tess.setImage(img);
        return tess.getUTF8Text();
    }

    public String parseUri(Uri uri){
        Bitmap bmp = FileUtility.createBmp(uri,context);
        return parseImg(bmp);
    }

    public void recycle() {
        tess.recycle();
        initialized = false;
    }
}
