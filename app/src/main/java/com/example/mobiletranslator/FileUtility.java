package com.example.mobiletranslator;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import androidx.annotation.NonNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class FileUtility {
    public static final int FILE_RETURN_CODE = 100;

    public FileUtility(ContentResolver cr){}

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
            String line = null;

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

    public static Bitmap createBmp(Uri uri, Context context) {
        try {
            InputStream is = context.getContentResolver().openInputStream(uri);
            return BitmapFactory.decodeStream(is);
        }
        catch(IOException e){
            return null;
        }
    }

    //TODO: REMOVE ME
    public static void copyFile(@NonNull AssetManager am, @NonNull String assetName,
                                 @NonNull File outFile) {
        try (
                InputStream in = am.open(assetName);
                OutputStream out = new FileOutputStream(outFile)
        ) {
            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
