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

public class ImageParser {
    private final TessBaseAPI tess;
    private final Context context;
    private boolean initialized;

    private static final int BORDER_SIZE = 12;
    private static final int BORDER_COLOR = Color.BLACK;
    private static final int CONTRAST = 10; //0...10 default is 1
    private static final int BRIGHTNESS = 0; //-255...255 default is 0

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


    public Bitmap optimizeImage(Bitmap img){
        //add border
        Bitmap optimizedImg =  Bitmap.createBitmap(img.getWidth()+BORDER_SIZE*2, img.getHeight()+BORDER_SIZE*2, img.getConfig());
        int height = optimizedImg.getHeight();
        int width = optimizedImg.getWidth();

        //set brightness and contrast
        ColorMatrix cm = new ColorMatrix(new float[]
                {
                        CONTRAST, 0, 0, 0, BRIGHTNESS,
                        0, CONTRAST, 0, 0, BRIGHTNESS,
                        0, 0, CONTRAST, 0, BRIGHTNESS,
                        0, 0, 0, 1, 0
                });

        Canvas canvas = new Canvas(optimizedImg);
        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawColor(BORDER_COLOR);
        canvas.drawBitmap(img, BORDER_SIZE, BORDER_SIZE, null);

        //convert to grayscale
        int alpha, red, green, blue;
        int pixel, newPixel;

        for(int x=0; x<width; x++) {
            for(int y=0; y<height; y++) {
                pixel = optimizedImg.getPixel(x,y);
                alpha = (pixel >> 24) & 0xff;
                red = (pixel >> 16) & 0xff;
                green = (pixel >> 8) & 0xff;
                blue = (pixel) & 0xff;
                red = (int) (0.21 * red + 0.71 * green + 0.07 * blue);
                newPixel = (alpha & 0xff) << 24 | (red & 0xff) << 16 | (red & 0xff) << 8 | (red & 0xff);
                optimizedImg.setPixel(x, y, newPixel);
            }
        }

        //remove noise
        for(int y=0; y<height; y++){
            for(int x=0; x<width; x++){
                red = (optimizedImg.getPixel(x,y) >> 16) & 0xff;
                int T=0;
                if(y+1<height){
                    if(red!=((optimizedImg.getPixel(x,y+1) >> 16) & 0xff)) T++;
                }
                else T++;
                if(y-1>=0){
                    if(red!=((optimizedImg.getPixel(x,y-1) >> 16) & 0xff)) T++;
                }
                else T++;
                if(x+1<width){
                    if(red!=((optimizedImg.getPixel(x+1,y) >> 16) & 0xff)) T++;
                }
                else T++;
                if(x-1>=0){
                    if(red!=((optimizedImg.getPixel(x-1,y) >> 16) & 0xff)) T++;
                }
                else T++;
                if(T==4){
                    pixel = optimizedImg.getPixel(x,y+1);
                    alpha = (pixel >> 24) & 0xff;;
                    red = (pixel >> 16) & 0xff;
                    newPixel = (alpha & 0xff) << 24 | (red & 0xff) << 16 | (red & 0xff) << 8 | (red & 0xff);
                    optimizedImg.setPixel(x, y, newPixel);
                }
            }
        }

        return optimizedImg;
    }

    public String parseImg(Bitmap img){
        //Bitmap optimizedImage = optimizeImage(img);
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
