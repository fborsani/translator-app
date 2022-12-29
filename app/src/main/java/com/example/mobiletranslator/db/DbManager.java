package com.example.mobiletranslator.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.Nullable;

import com.example.mobiletranslator.db.Contract.*;

import java.util.ArrayList;
import java.util.HashMap;

public class DbManager {
    public static final String API_KEY_PARAM_NAME = "ApiKey";

    public static final String LANG_PARAM_ISO = "iso";
    public static final String LANG_PARAM_ISO3 = "iso3";
    public static final String LANG_PARAM_FORMAL = "formal";

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
        String apiKey = cursor.getString(idx);
        cursor.close();
        return apiKey;
    }

    public void setApiKey(String apiKey){
        SQLiteDatabase db = helper.getWritableDatabase();
        String selection = Param.COLUMN_NAME_DESCR+" = ?";
        String[] args = {API_KEY_PARAM_NAME};
        ContentValues cv = new ContentValues();
        cv.put(Param.COLUMN_NAME_VALUE,apiKey);
        db.update(Param.TABLE_NAME,cv,selection,args);
    }

    public ArrayList<String> getLanguagesIn(@Nullable ArrayList<HashMap<String,Object>> arrayDataList){
        return getLanguages(arrayDataList,new String[]{LanguageOptionVisibility.VISIBILITY_IN});
    }

    public ArrayList<String> getLanguagesOut(@Nullable ArrayList<HashMap<String,Object>> arrayDataList){
        return getLanguages(arrayDataList,new String[]{LanguageOptionVisibility.VISIBILITY_OUT});
    }

    public ArrayList<String> getLanguages(@Nullable ArrayList<HashMap<String,Object>> arrayDataList, String[] args){
        SQLiteDatabase db = helper.getReadableDatabase();
        String[] columns = {Language._ID,
                            Language.COLUMN_NAME_NAME,
                            Language.COLUMN_NAME_ISO_CODE,
                            Language.COLUMN_NAME_ISO_CODE3,
                            Language.COLUMN_NAME_SUPPORT_FORMAL};

        String selection = Language.COLUMN_NAME_VISIBILITY+" IN (\'"+LanguageOptionVisibility.VISIBILITY_BOTH+"\', ?)";

        Cursor cursor = db.query(Language.TABLE_NAME, columns, selection, args, null, null, Language.COLUMN_NAME_NAME);

        ArrayList<String> langList = new ArrayList<>();
        int colName = cursor.getColumnIndexOrThrow(Language.COLUMN_NAME_NAME);
        int colIso = cursor.getColumnIndexOrThrow(Language.COLUMN_NAME_ISO_CODE);
        int colIso3 = cursor.getColumnIndexOrThrow(Language.COLUMN_NAME_ISO_CODE3);
        int colFormal = cursor.getColumnIndexOrThrow(Language.COLUMN_NAME_SUPPORT_FORMAL);

        if (cursor.moveToFirst()) {
            do{
                langList.add(cursor.getString(colName));
                if(arrayDataList != null){
                    HashMap<String,Object> dataMapItem = new HashMap<>();
                    dataMapItem.put(LANG_PARAM_ISO,cursor.getString(colIso));
                    dataMapItem.put(LANG_PARAM_ISO3,cursor.getString(colIso3));
                    dataMapItem.put(LANG_PARAM_FORMAL,cursor.getInt(colFormal));
                    arrayDataList.add(dataMapItem);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return langList;
    }
}
