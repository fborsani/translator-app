package com.example.mobiletranslator;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import androidx.exifinterface.media.ExifInterface;
import android.net.Uri;
import android.util.Xml;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.example.mobiletranslator.db.LanguageItem;

public final class FileUtility {

    private FileUtility(){}

    public static Intent createIntentGetImage(String title){
        return createIntent("image/*", title,true);
    }

    public static Intent createIntentGetText(String title){
        return createIntent("text/plain", title,false);
    }

    public static String readFile(Uri uri, ContentResolver cr) throws IOException{
        InputStream is = cr.openInputStream(uri);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuilder content = new StringBuilder();
        String line;

        while ((line = br.readLine()) != null) {
            content.append(line);
            content.append(System.lineSeparator());
        }

        br.close();
        return content.toString();
    }

    private static Intent createIntent(String type, String appSelectionTitle, Boolean isImage){
        Intent intentGetFile = new Intent(Intent.ACTION_GET_CONTENT);
        intentGetFile.addCategory(Intent.CATEGORY_OPENABLE);
        intentGetFile.setType(type);

        Uri mediaStoreUri = null;
        if(isImage){
            mediaStoreUri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }

        Intent pickApp = new Intent(Intent.ACTION_PICK, mediaStoreUri);
        Intent chooserIntent = Intent.createChooser(intentGetFile, appSelectionTitle);
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickApp});
        return chooserIntent;
    }

    public static Bitmap createBmp(Uri uri, Context context) throws IOException {
        InputStream is = context.getContentResolver().openInputStream(uri);
        ExifInterface ei = new ExifInterface(is);
        is.close();
        is = context.getContentResolver().openInputStream(uri);
        Bitmap img = BitmapFactory.decodeStream(is);

        //rotate image if taken in portrait mode
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
        int angle;

        switch(orientation){
            case ExifInterface.ORIENTATION_ROTATE_90:
                angle = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                angle = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                angle = 270;
                break;
            case ExifInterface.ORIENTATION_NORMAL:
            default:
                return img;
        }

        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
    }

    @SuppressLint("SimpleDateFormat")
    public static Uri createTempImageUri(Context context) throws IOException {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String name = "IMG_" + timestamp;
        File tempFile = File.createTempFile(name, ".jpg", context.getFilesDir());
        String authority = context.getApplicationContext().getPackageName() + ".provider";
        return FileProvider.getUriForFile(context, authority, tempFile);
    }

    public static InputStream copyAssetFile(@NonNull AssetManager am, @NonNull String assetName) throws IOException{
        return am.open(assetName);
    }

    public static ArrayList<LanguageItem> parseLanguageFile(@NonNull AssetManager am) throws IOException, XmlPullParserException{
        InputStream in = copyAssetFile(am, LocalDataManager.SQL_LANGUAGE_DATA_FILE);
        ArrayList<LanguageItem> list = new ArrayList<>();
        XmlPullParser parser = Xml.newPullParser();
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        parser.setInput(in, null);
        int eventType = parser.next();
        while(eventType != XmlPullParser.END_DOCUMENT){
            eventType= parser.next();
            if(eventType == XmlPullParser.START_TAG && parser.getName().equals("entry")){
                String name = parser.getAttributeValue(null,"name");
                String isoCode = parser.getAttributeValue(null,"iso");
                String isoCode3 = parser.getAttributeValue(null,"iso3");
                String visibility = parser.getAttributeValue(null, "visibility");
                String formal = parser.getAttributeValue(null,"polite");
                String filename = parser.getAttributeValue(null, "filename");
                list.add(new LanguageItem(name, isoCode, isoCode3, visibility, filename, formal));
            }
        }
        return list;
    }
}
