package com.example.mobiletranslator.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.mobiletranslator.db.Contract.*;

public class DbManager {
    public static final String API_KEY_PARAM_NAME = "ApiKey";

    private final Context context;
    private final DbHelper helper;

    DbManager(Context context){
        helper = new DbHelper(context);
        this.context = context;
    }

    public String getApiKey(){
        SQLiteDatabase db = helper.getReadableDatabase();
        String selection = "WHERE "+Param.COLUMN_NAME_DESCR+" = ?";
        String[] args = {API_KEY_PARAM_NAME};
        Cursor cursor = db.query(Param.TABLE_NAME,null, selection, args, null, null, null);
        cursor.moveToNext();
        int idx = cursor.getColumnIndexOrThrow(Param.COLUMN_NAME_VALUE);
        return cursor.getString(idx);
    }
}
