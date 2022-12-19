package com.example.mobiletranslator;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

import android.net.Uri;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.util.ArrayList;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;


public class ImageParser {
    private final TessBaseAPI tess;
    private final Context context;
    private boolean initialized;

    private static final int IMG_WIDTH = 2048;
    private static final int IMG_HEIGHT = 1024;
    private static final int BORDER_SIZE = 8;
    private static final int BORDER_COLOR = Color.BLACK;
    private static final int CONTRAST = 10; //0...10 default is 1
    private static final int BRIGHTNESS = 0; //-255...255 default is 0
    private static final int LIGHTNESS_THRESHOLD = 64; //0...255 default is 64


    public ImageParser(Context context, String language){
        this.tess = new TessBaseAPI();
        this.context = context;
        this.tess.setVariable("user_defined_dpi", "300");
        String dataFolder = context.getFilesDir().getAbsolutePath();
        File tessDir = new File(dataFolder,"tessdata");
        if(!tessDir.exists()){
            tessDir.mkdir();
        }

        //TODO: edit file checks
        File engFile = new File(tessDir, "eng.traineddata");
        if (!engFile.exists()) {
            AssetManager am = context.getAssets();
            FileUtility.copyAssetFile(am, "eng.traineddata", engFile);
        }

        initialized = tess.init(dataFolder,language);
        if(!initialized){
            tess.recycle();
        }

        OpenCVLoader.initDebug();
    }

    public boolean isInitialized(){
        return initialized;
    }

    public Bitmap optimizeImage(Bitmap img){

        //add border
        Bitmap optimizedBmp =  Bitmap.createBitmap(img.getWidth()+BORDER_SIZE*2, img.getHeight()+BORDER_SIZE*2, img.getConfig());

        //set brightness and contrast
        ColorMatrix cm = new ColorMatrix(new float[]
                {
                        CONTRAST, 0, 0, 0, BRIGHTNESS,
                        0, CONTRAST, 0, 0, BRIGHTNESS,
                        0, 0, CONTRAST, 0, BRIGHTNESS,
                        0, 0, 0, 1, 0
                });

        Canvas canvas = new Canvas(optimizedBmp);
        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawColor(BORDER_COLOR);
        canvas.drawBitmap(img, BORDER_SIZE, BORDER_SIZE, null);

        Mat mat = new Mat();
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size((2*2) + 1, (2*2)+1));;
        Utils.bitmapToMat(optimizedBmp,mat);

        //resize
        Size scaleSize;
        if(optimizedBmp.getWidth() > optimizedBmp.getHeight()){
            scaleSize = new Size(IMG_WIDTH, IMG_HEIGHT);
        }
        else if(optimizedBmp.getWidth() < optimizedBmp.getHeight()){
            scaleSize = new Size(IMG_HEIGHT, IMG_WIDTH);
        }
        else{
            scaleSize = new Size(IMG_HEIGHT, IMG_HEIGHT);
        }
        Imgproc.resize(mat,mat,scaleSize,0,0,Imgproc.INTER_CUBIC);

        //invert image in case of dark background
        Mat bg = new Mat();
        Mat bgHls = new Mat();
        ArrayList<Mat> channels = new ArrayList<>(3);
        Imgproc.medianBlur(mat,bg,21);
        Imgproc.cvtColor(bg, bgHls, Imgproc.COLOR_BGR2HLS);
        Core.split(bgHls,channels);
        Scalar lvalue0 = Core.mean(channels.get(0));
        Scalar lvalue = Core.mean(channels.get(1));
        Scalar lvalue2 = Core.mean(channels.get(2));
        if(lvalue.val[0] < LIGHTNESS_THRESHOLD){
            Core.bitwise_not(mat,mat);
        }

        //set to grayscale, remove noise and add blur
        Imgproc.cvtColor(mat,mat,Imgproc.COLOR_BGR2GRAY);
        Imgproc.dilate(mat,mat,kernel);
        Imgproc.erode(mat,mat,kernel);
        Imgproc.medianBlur(mat,mat,5);

        //binarize image
        Imgproc.adaptiveThreshold(mat,mat,255,Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,Imgproc.THRESH_BINARY,31,2);

        Bitmap bitmapOut = Bitmap.createBitmap(mat.width(),mat.height(),optimizedBmp.getConfig());
        Utils.matToBitmap(mat,bitmapOut);
        return bitmapOut;
    }

    public String parseImg(Bitmap img){
        Bitmap optimizedImage = optimizeImage(img);
        tess.setImage(optimizedImage);
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
