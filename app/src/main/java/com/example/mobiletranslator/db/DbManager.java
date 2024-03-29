package com.example.mobiletranslator.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


import com.example.mobiletranslator.db.Contract.*;

import java.util.ArrayList;

public class DbManager {
    public static final String API_KEY_PARAM_NAME = "ApiKey";

    private final DbHelper helper;

    public DbManager(Context context){
        helper = new DbHelper(context);
    }

    public String getApiKey(){
        SQLiteDatabase db = helper.getReadableDatabase();
        String selection = Param.COLUMN_NAME_DESCR+" = ?";
        String[] args = {API_KEY_PARAM_NAME};
        Cursor cursor = db.query(Param.TABLE_NAME,null, selection, args, null, null, null);
        cursor.moveToNext();
        int idx = cursor.getColumnIndexOrThrow(Param.COLUMN_NAME_VALUE);
        String value = cursor.getString(idx);
        cursor.close();
        return value;
    }

    public void setApiKey(String apiKey){
        SQLiteDatabase db = helper.getWritableDatabase();
        String selection = Param.COLUMN_NAME_DESCR+" = ?";
        String[] args = {API_KEY_PARAM_NAME};
        ContentValues cv = new ContentValues();
        cv.put(Param.COLUMN_NAME_VALUE,apiKey);
        db.update(Param.TABLE_NAME,cv,selection,args);
    }

    public ArrayList<LanguageItem> getLanguagesIn(){
        return getLanguages(new String[]{LanguageOptionVisibility.VISIBILITY_IN});
    }

    public ArrayList<LanguageItem> getLanguagesOut(){
        return getLanguages(new String[]{LanguageOptionVisibility.VISIBILITY_OUT});
    }

    public ArrayList<LanguageItem> getLanguages(String[] args){
        SQLiteDatabase db = helper.getReadableDatabase();

        String selection = Language.COLUMN_NAME_VISIBILITY+" IN ('"+LanguageOptionVisibility.VISIBILITY_BOTH+"', ?)";

        Cursor cursor = db.query(Language.TABLE_NAME, null, selection, args, null, null, Language.COLUMN_NAME_NAME);

        ArrayList<LanguageItem> langList = new ArrayList<>();
        int colName = cursor.getColumnIndexOrThrow(Language.COLUMN_NAME_NAME);
        int colIso = cursor.getColumnIndexOrThrow(Language.COLUMN_NAME_ISO_CODE);
        int colIso3 = cursor.getColumnIndexOrThrow(Language.COLUMN_NAME_ISO_CODE3);
        int colVisibility = cursor.getColumnIndexOrThrow(Language.COLUMN_NAME_VISIBILITY);
        int colFormal = cursor.getColumnIndexOrThrow(Language.COLUMN_NAME_SUPPORT_FORMAL);
        int colFilename = cursor.getColumnIndexOrThrow(Language.COLUMN_OCR_FILENAME);

        if (cursor.moveToFirst()) {
            do{
                langList.add(new LanguageItem(
                    cursor.getString(colName),
                    cursor.getString(colIso),
                    cursor.getString(colIso3),
                    cursor.getString(colVisibility),
                    cursor.getString(colFilename),
                    cursor.getInt(colFormal) > 0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return langList;
    }
}
