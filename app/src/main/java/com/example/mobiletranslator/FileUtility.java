package com.example.mobiletranslator;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Xml;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.example.mobiletranslator.db.LanguageItem;

public class FileUtility {
    public static final int FILE_RETURN_CODE = 100;
    public static final String DEFAULT_OCR_FILE = "eng.traineddata";
    public static final String SQL_LANGUAGE_DATA_FILE = "languages.xml";

    private FileUtility(){}

    public static Intent createIntentGetImage(){
        return createIntent("image/*","Select gallery app",true);
    }

    public static Intent createIntentGetText(){
        return createIntent("text/plain","Select file manager",false);
    }

    public static String readFile(Uri uri, ContentResolver cr){
        try {
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
        catch(FileNotFoundException e){
            return null;
        }
        catch (IOException e){
            return null;
        }
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

    public static Bitmap createBmp(Uri uri, Context context){
        try {
            InputStream is = context.getContentResolver().openInputStream(uri);
            return BitmapFactory.decodeStream(is);
        }
        catch(IOException e){
            return null;
        }
    }

    public static Uri createTempImageUri(Context context) {
        try {
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String name = "IMG_" + timestamp;
            File tempFile = File.createTempFile(name, ".jpg", context.getFilesDir());
            String authority = context.getApplicationContext().getPackageName() + ".provider";
            return FileProvider.getUriForFile(context, authority, tempFile);
        }
        catch(IOException e){
            return null;
        }
    }

    public static void copyAssetFile(@NonNull AssetManager am, @NonNull String assetName, @NonNull File outFile) {
        try {
            InputStream in = am.open(assetName);
            OutputStream out = new FileOutputStream(outFile);
            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
        }
        catch(IOException e){
        }
    }

    public static InputStream copyAssetFile(@NonNull AssetManager am, @NonNull String assetName){
        try {
            return am.open(assetName);
        } catch (IOException e) {
            return null;
        }
    }

    public static ArrayList<LanguageItem> parseLanguageFile(@NonNull AssetManager am) {
        try {
            InputStream in = copyAssetFile(am, SQL_LANGUAGE_DATA_FILE);
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
                    String formal = parser.getAttributeValue(null,"polite");
                    String filename = parser.getAttributeValue(null, "filename");
                    list.add(new LanguageItem(name, isoCode, filename, formal,"false"));
                }
            }
            return list;
        }
        catch (XmlPullParserException e) {
            return null;
        }
        catch (IOException e){
            return null;
        }
    }
}
